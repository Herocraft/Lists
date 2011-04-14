package com.herocraftonline.dthielke.herolist.command.commands;

import java.util.Map;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.PrivilegedList.Level;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class DeleteCommand extends BaseCommand {

	public DeleteCommand(HeroList plugin) {
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
		Map<String, PrivilegedList> lists = plugin.getLists();
		if (!lists.containsKey(args[0])) {
			Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
			return;
		}

		PrivilegedList list = lists.get(args[0]);

		if (sender instanceof Player) {
			String name = ((Player) sender).getName();

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
