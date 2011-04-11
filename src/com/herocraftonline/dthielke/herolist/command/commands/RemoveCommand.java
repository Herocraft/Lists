package com.herocraftonline.dthielke.herolist.command.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.Privilege.Level;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class RemoveCommand extends BaseCommand {

	public RemoveCommand(HeroList plugin) {
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
		Map<String, PrivilegedList> lists = plugin.getLists();
		if (!lists.containsKey(args[0])) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		PrivilegedList list = lists.get(args[0]);

		if (sender instanceof Player) {
			String name = ((Player) sender).getName();

			if (!list.get(name).clears(Level.MODIFIER)) {
				Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
				return;
			}
		}

		if (!list.contains(args[1])) {
			Messaging.send(plugin, sender, "There is no player $1 in $2.", args[1], args[0]);
			return;
		}

		list.remove(args[1]);
		Messaging.send(plugin, sender, "Removed player $1 from $2.", args[1], args[0]);
	}

}
