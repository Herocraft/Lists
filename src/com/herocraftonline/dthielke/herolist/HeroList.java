package com.herocraftonline.dthielke.herolist;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.persistence.PersistenceException;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.dthielke.herolist.command.CommandManager;
import com.herocraftonline.dthielke.herolist.command.commands.AddCommand;
import com.herocraftonline.dthielke.herolist.command.commands.DeleteCommand;
import com.herocraftonline.dthielke.herolist.command.commands.HelpCommand;
import com.herocraftonline.dthielke.herolist.command.commands.CreateCommand;
import com.herocraftonline.dthielke.herolist.command.commands.InfoCommand;
import com.herocraftonline.dthielke.herolist.command.commands.RemoveCommand;

public class HeroList extends JavaPlugin {

    private final Logger log = Logger.getLogger("Minecraft");
    private CommandManager commandManager;
    private Map<String, PrivilegedList> lists = new HashMap<String, PrivilegedList>();

    @Override
    public void onDisable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " disabled.");

        getDatabase().endTransaction();
    }

    @Override
    public void onEnable() {
        log(Level.INFO, "version " + getDescription().getVersion() + " enabled.");

        PrivilegedList.plugin = this;

        setupDatabase();
        loadListsIntoMemory();
        registerCommands();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.dispatch(sender, command, label, args);
    }

    private void setupDatabase() {
        try {
            getDatabase().find(PrivilegedList.class).findRowCount();
        } catch (PersistenceException ex) {
            log(Level.INFO, "Installing database due to first time usage.");
            installDDL();
        }
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.addCommand(new AddCommand(this));
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new DeleteCommand(this));
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new InfoCommand(this));
        commandManager.addCommand(new RemoveCommand(this));
    }

    @Override
    public List<Class<?>> getDatabaseClasses() {
        List<Class<?>> list = new ArrayList<Class<?>>();
        list.add(PrivilegedList.class);
        list.add(PrivilegedPlayer.class);
        return list;
    }

    public void log(Level level, String msg) {
        log.log(level, "[HeroList] " + msg);
    }
    
    /*** START In-Memory List Management ***/
    
    public PrivilegedList getList(String name) {
        name = name.toLowerCase();
        return lists.get(name);
    }
    
    public void putList(String name, PrivilegedList list) {
        name = name.toLowerCase();
        lists.put(name, list);
        saveList(list);
    }
    
    public void removeList(String name) {
        name = name.toLowerCase();
        deleteList(lists.remove(name));
    }
    
    public boolean listExists(String name) {
        name = name.toLowerCase();
        return lists.containsKey(name);
    }
    
    /*** END In-Memory List Management ***/
    
    /*** START Persistent List Management ***/
    
    private void loadListsIntoMemory() {
        List<PrivilegedList> lists = getDatabase().find(PrivilegedList.class).findList();
        this.lists.clear();
        for (PrivilegedList list : lists) {
            this.lists.put(list.getName(), list);
        }
    }
    
    @SuppressWarnings("unused")
    private PrivilegedList loadList(String name) {
        name = name.toLowerCase();
        return getDatabase().find(PrivilegedList.class).where().ieq("name", name).findUnique();
    }
    
    private void saveList(PrivilegedList list) {
        getDatabase().save(list);
    }
    
    private void deleteList(PrivilegedList list) {
        getDatabase().delete(list);
    }
    
    /*** END Persistent List Management ***/

    public CommandManager getCommandManager() {
        return commandManager;
    }

}
