package com.herocraftonline.dthielke.lists.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.Level;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class PutCommand extends BaseCommand {

	public PutCommand(Lists plugin) {
		super(plugin);
		name = "Put Player";
		description = "Adds or modifies a player to an existing list";
		usage = "§e/ls put §9<list> §9<player> §8-[n|v|m|o]";
		minArgs = 2;
		maxArgs = 3;
		identifiers.add("ls put");
		notes.add("Privileges:");
		notes.add("  -n: NONE (default)");
		notes.add("  -v: VIEW");
		notes.add("  -m: MODIFY");
		notes.add("  -o: OWNER");
	}

	@Override
	public void execute(CommandSender sender, String[] args) {
	    Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.PUT) && !plugin.hasPermission(player, Permission.ADMIN_PUT)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }
	    
        PrivilegedList list = plugin.getList(args[0]);
        if (list == null) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		Level privilege = Level.NONE;
		if (args.length == 3) {
			privilege = Level.parse(args[2].substring(1));
			privilege = (privilege == null) ? Level.NONE : privilege;
		}

		if (player != null && !plugin.hasPermission(player, Permission.ADMIN_PUT)) {
			String name = player.getName();

			if (!list.contains(name)) {
				Messaging.send(plugin, sender, "You are not a member of $1.", args[0]);
				return;
			}

			if (name.equalsIgnoreCase(args[1])) {
				Messaging.send(plugin, sender, "You cannot modify your own privilege level.");
				return;
			}

			Level senderPrivilege = list.get(name);

			if (!senderPrivilege.clears(Level.MODIFIER)) {
				Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
				return;
			}

			if (senderPrivilege == Level.MODIFIER && privilege.clears(senderPrivilege)) {
				Messaging.send(plugin, sender, "You cannot set a privilege equal to or higher than your own.");
				return;
			}
		}

		list.put(args[1], privilege);
		plugin.saveList(list);
		Messaging.send(plugin, sender, "Added $1 to $2 with $3 privileges.", args[1], args[0], privilege.name());

	}

}
