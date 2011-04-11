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
import com.herocraftonline.dthielke.herolist.command.commands.PutCommand;
import com.herocraftonline.dthielke.herolist.command.commands.DeleteCommand;
import com.herocraftonline.dthielke.herolist.command.commands.HelpCommand;
import com.herocraftonline.dthielke.herolist.command.commands.CreateCommand;
import com.herocraftonline.dthielke.herolist.command.commands.ViewCommand;
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

		registerCommands();
		setupDatabase();
		loadLists();

		/*
		PrivilegedList list = new PrivilegedList("listname");
		list.put("playername", Privilege.Level.OWNER);
		saveList(list);
		*/
		
		
		for (String name : lists.keySet()) {
			PrivilegedList list = getList(name);
			log(Level.INFO, list.getPlayerSet().toString());
		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		return commandManager.dispatch(sender, command, label, args);
	}

	@Override
	public List<Class<?>> getDatabaseClasses() {
		List<Class<?>> list = new ArrayList<Class<?>>();
		list.add(Privilege.class);
		list.add(PrivilegedList.class);
		return list;
	}

	private void setupDatabase() {
		try {
			getDatabase().find(PrivilegedList.class).findRowCount();
		} catch (PersistenceException ex) {
			System.out.println("Installing database for " + getDescription().getName() + " due to first time usage");
			installDDL();
		}
	}
	
	private void loadLists() {
		List<PrivilegedList> lists = getDatabase().find(PrivilegedList.class).findList();
		for (PrivilegedList list : lists) {
			this.lists.put(list.getName(), list);
		}
	}

	private void registerCommands() {
		commandManager = new CommandManager();
		commandManager.addCommand(new PutCommand(this));
		commandManager.addCommand(new CreateCommand(this));
		commandManager.addCommand(new DeleteCommand(this));
		commandManager.addCommand(new HelpCommand(this));
		commandManager.addCommand(new ViewCommand(this));
		commandManager.addCommand(new RemoveCommand(this));
	}
	
	public PrivilegedList getList(String name) {
		return lists.get(name);
	}
	
	public void saveList(PrivilegedList list) {
		lists.put(list.getName(), list);
		getDatabase().save(list);
	}
	
	public void deleteList(PrivilegedList list) {
		lists.remove(list.getName());
		getDatabase().delete(list);
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
