package com.herocraftonline.dthielke.lists.command.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.Level;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class CreateCommand extends BaseCommand {

	public CreateCommand(Lists plugin) {
		super(plugin);
		name = "Create List";
		description = "Creates a new list";
		usage = "§e/ls create §9<list> §8[-r]";
		minArgs = 1;
		maxArgs = 2;
		identifiers.add("ls create");
		notes.add("Restricted (-r): only VIEW or above can see the list's members");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {	 
	    Player player = null;
	    if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.CREATE)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
	    }
	    
		Map<String, PrivilegedList> lists = plugin.getLists();
		if (lists.containsKey(args[0])) {
			Messaging.send(plugin, sender, "The list name $1 is already being used.", args[0]);
			return;
		}

		PrivilegedList list = new PrivilegedList(args[0]);

		if (player != null) {
			list.put(player.getName(), Level.OWNER);
		}

		if (args.length == 2 && args[1].equals("-r")) {
			list.setRestricted(true);
		}

		list.setName(args[0]);
		plugin.saveList(list);
		Messaging.send(plugin, sender, "Created the list $1.", args[0]);
	}

}
