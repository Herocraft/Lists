package com.herocraftonline.dthielke.lists;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.herocraftonline.dthielke.lists.command.CommandManager;
import com.herocraftonline.dthielke.lists.command.commands.AddCommand;
import com.herocraftonline.dthielke.lists.command.commands.CreateCommand;
import com.herocraftonline.dthielke.lists.command.commands.DeleteCommand;
import com.herocraftonline.dthielke.lists.command.commands.HelpCommand;
import com.herocraftonline.dthielke.lists.command.commands.ListCommand;
import com.herocraftonline.dthielke.lists.command.commands.PutCommand;
import com.herocraftonline.dthielke.lists.command.commands.RemoveCommand;
import com.herocraftonline.dthielke.lists.command.commands.ViewCommand;
import com.herocraftonline.dthielke.lists.io.ListsSQLHandler;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

/***
 * Bukkit plugin which allows users to create persistent lists of players that can be accessed and modified by other plugins.
 * Lists are typically stored in SQLite or MySQL databases, but all storage is handled internally.
 */
public class Lists extends JavaPlugin {

    /***
     * Internal usage only.
     */
    public enum Permission {
        LIST("user.list"),
        ADMIN_LIST("admin.list"),
        CREATE("user.create"),
        DELETE("user.delete"),
        ADMIN_DELETE("admin.delete"),
        PUT("user.put"),
        ADMIN_PUT("admin.put"),
        VIEW("user.view"),
        ADMIN_VIEW("admin.view"),
        REMOVE("user.remove"),
        ADD("user.add"),
        ADMIN_ADD("admin.add");

        public final String node;

        private Permission(String node) {
            this.node = node;
        }
    }

    private final Logger log = Logger.getLogger("Minecraft");
    private ServerListener serverListener = new ListsServerListener(this);
    private PermissionHandler security;
    private CommandManager commandManager;
    private ListsSQLHandler sql;
    private Map<String, PrivilegedList> lists = new HashMap<String, PrivilegedList>();

    /***
     * Internal usage only.
     */
    @Override
    public void onDisable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " disabled.");
        if (sql != null) {
            sql.disconnect();
        }
    }

    /***
     * Internal usage only.
     */
    @Override
    public void onEnable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " enabled.");
        loadPermissions();
        registerEvents();
        registerCommands();
        if (createSQLHandler()) {
            sql.setupDatabase();
            lists = sql.loadLists();
            log(Level.INFO, lists.size() + " lists loaded.");
        } else {
            log(Level.SEVERE, "Bad configuration file. Disabling.");
            getServer().getPluginManager().disablePlugin(this);
        }
    }

    /***
     * Internal usage only.
     */
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.dispatch(sender, command, label, args);
    }

    /***
     * Internal usage only.
     */
    public void loadPermissions() {
        Plugin plugin = this.getServer().getPluginManager().getPlugin("Permissions");
        if (plugin != null) {
            if (plugin.isEnabled()) {
                Permissions permissions = (Permissions) plugin;
                security = permissions.getHandler();
                log(Level.INFO, "Permissions " + permissions.getDescription().getVersion() + " found.");
            }
        }
    }

    /***
     * Internal usage only.
     */
    public void unloadPermissions() {
        if (security != null) {
            security = null;
            log(Level.INFO, "Permissions lost.");
        }
    }

    private void registerEvents() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Type.PLUGIN_ENABLE, serverListener, Priority.Monitor, this);
        pm.registerEvent(Type.PLUGIN_DISABLE, serverListener, Priority.Monitor, this);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new ViewCommand(this));
        commandManager.addCommand(new PutCommand(this));
        commandManager.addCommand(new AddCommand(this));
        commandManager.addCommand(new RemoveCommand(this));
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new DeleteCommand(this));
        commandManager.addCommand(new HelpCommand(this));
    }

    private boolean createSQLHandler() {
        checkConfig();
        Configuration config = new Configuration(new File(getDataFolder(), "config.yml"));
        config.load();
        
        String driver = config.getString("database.driver");
        String url = config.getString("database.URL");
        String user = config.getString("database.user", "");
        String password = config.getString("database.password", "");

        if (driver == null || url == null) {
            return false;
        } else {
            Pattern pattern = Pattern.compile("\\w+\\z");
            Matcher matcher = pattern.matcher(url);
            if (matcher.find()) {
                sql = new ListsSQLHandler(matcher.group(), driver, url, user, password);
                return true;
            } else {
                return false;
            }
        }
    }
    
    private void checkConfig() {
        File configFile = new File(getDataFolder(), "config.yml");
        if (!configFile.exists()) {
            try {
                configFile.getParentFile().mkdir();
                configFile.createNewFile();
                OutputStream output = new FileOutputStream(configFile, false);
                InputStream input = Lists.class.getResourceAsStream("/defaults/config.yml");
                byte[] buf = new byte[8192];
                while (true) {
                    int length = input.read(buf);
                    if (length < 0) {
                        break;
                    }
                    output.write(buf, 0, length);
                }
                input.close();
                output.close();
                log(Level.WARNING, "Default config created. You may need to configure it.");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /***
     * Internal usage only.
     */
    public boolean hasPermission(Player player, Permission permission) {
        if (security != null) {
            return security.has(player, "lists." + permission.node);
        } else {
            return player.isOp();
        }
    }
    
    /***
     * Returns an array of all existing lists.
     * 
     * @return an array containing all existing <code>PrivilegedList</code>s.
     */
    public final PrivilegedList[] getLists() {
        return lists.values().toArray(new PrivilegedList[0]);
    }

    /***
     * Gets an existing list by its name.
     * 
     * @param name the name of the list (case-sensitive)
     * @return the <code>PrivilegedList</code> associated with the name, or <code>null</code> if no such list exists
     */
    public PrivilegedList getList(String name) {
        return lists.get(name);
    }

    /***
     * Saves a new list or updates an existing list.
     * 
     * @param list the <code>PrivilegedList</code> to save
     */
    public void saveList(PrivilegedList list) {
        lists.put(list.getName(), list);
        sql.saveList(list);
    }

    /***
     * Deletes a list from memory and the database.
     * 
     * @param list the <code>PrivilegedList</code> to delete
     */
    public void deleteList(PrivilegedList list) {
        log(Level.INFO, "Deleting list " + list);
        lists.remove(list.getName());
        sql.deleteList(list.getName());
    }

    /***
     * Internal usage only.
     */
    public void log(Level level, String msg) {
        log.log(level, "[Lists] " + msg);
    }

    /***
     * Internal usage only.
     */
    public CommandManager getCommandManager() {
        return commandManager;
    }

}
