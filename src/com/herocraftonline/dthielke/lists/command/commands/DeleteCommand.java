package com.herocraftonline.dthielke.lists.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.Level;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class DeleteCommand extends BaseCommand {

	public DeleteCommand(Lists plugin) {
		super(plugin);
		name = "Delete List";
		description = "Deletes an existing list";
		usage = "§e/ls del(ete) §9<list>";
		minArgs = 1;
		maxArgs = 1;
		identifiers.add("ls delete");
		identifiers.add("ls del");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
	    Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.DELETE) && !plugin.hasPermission(player, Permission.ADMIN_DELETE)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }
	    
        PrivilegedList list = plugin.getList(args[0]);
        if (list == null) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		if (player != null && !plugin.hasPermission(player, Permission.ADMIN_DELETE)) {
			String name = player.getName();

			if (!list.contains(name)) {
				Messaging.send(plugin, sender, "You are not in $1.", args[0]);
				return;
			}

			if (!list.get(name).clears(Level.OWNER)) {
				Messaging.send(plugin, sender, "You do not own $1.", args[0]);
				return;
			}
		}

		plugin.deleteList(list);
		Messaging.send(plugin, sender, "Deleted list $1.", args[0]);
	}

}
