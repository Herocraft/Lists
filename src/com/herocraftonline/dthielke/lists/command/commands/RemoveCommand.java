package com.herocraftonline.dthielke.lists.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.PrivilegeLevel;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class RemoveCommand extends BaseCommand {

	public RemoveCommand(Lists plugin) {
		super(plugin);
		name = "Remove Player";
		description = "Removes a player from a list";
		usage = "§e/ls rem(ove) §9<list> §9<player>";
		minArgs = 2;
		maxArgs = 2;
		identifiers.add("ls remove");
		identifiers.add("ls rem");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
	    Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.REMOVE)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }
	    
        PrivilegedList list = plugin.getList(args[0]);
        if (list == null) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		if (player != null) {
			String name = player.getName();

			if (!list.get(name).clears(PrivilegeLevel.MODIFIER)) {
				Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
				return;
			}
		}

		if (!list.contains(args[1])) {
			Messaging.send(plugin, sender, "There is no player $1 in $2.", args[1], args[0]);
			return;
		}

		list.remove(args[1]);
		plugin.saveList(list);
		Messaging.send(plugin, sender, "Removed player $1 from $2.", args[1], args[0]);
	}

}
