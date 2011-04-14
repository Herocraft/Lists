package com.herocraftonline.dthielke.herolist;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

import com.herocraftonline.dthielke.herolist.command.CommandManager;
import com.herocraftonline.dthielke.herolist.command.commands.ListCommand;
import com.herocraftonline.dthielke.herolist.command.commands.PutCommand;
import com.herocraftonline.dthielke.herolist.command.commands.DeleteCommand;
import com.herocraftonline.dthielke.herolist.command.commands.HelpCommand;
import com.herocraftonline.dthielke.herolist.command.commands.CreateCommand;
import com.herocraftonline.dthielke.herolist.command.commands.ViewCommand;
import com.herocraftonline.dthielke.herolist.command.commands.RemoveCommand;
import com.herocraftonline.dthielke.herolist.io.HeroListSQLHandler;

public class HeroList extends JavaPlugin {

    private final Logger log = Logger.getLogger("Minecraft");
    private CommandManager commandManager;
    private HeroListSQLHandler sql;
    private Map<String, PrivilegedList> lists = new HashMap<String, PrivilegedList>();

    @Override
    public void onDisable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " disabled.");
        if (sql != null) {
            sql.disconnect();
        }
    }

    @Override
    public void onEnable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " enabled.");

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

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.dispatch(sender, command, label, args);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.addCommand(new PutCommand(this));
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new DeleteCommand(this));
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new ViewCommand(this));
        commandManager.addCommand(new RemoveCommand(this));
    }

    private boolean createSQLHandler() {
        Configuration config = getConfiguration();
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
                sql = new HeroListSQLHandler(matcher.group(), driver, url, user, password);
                return true;
            } else {
                return false;
            }
        }
    }

    public PrivilegedList getList(String name) {
        return lists.get(name);
    }

    public void saveList(PrivilegedList list) {
        log(Level.INFO, "Saving list " + list);
        lists.put(list.getName(), list);
        sql.saveList(list);
    }

    public void deleteList(PrivilegedList list) {
        log(Level.INFO, "Deleting list " + list);
        lists.remove(list.getName());
        sql.deleteList(list.getName());
    }

    public Map<String, PrivilegedList> getLists() {
        return lists;
    }

    public void log(Level level, String msg) {
        log.log(level, "[HeroList] " + msg);
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
