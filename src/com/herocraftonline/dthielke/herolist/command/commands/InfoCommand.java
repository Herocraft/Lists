package com.herocraftonline.dthielke.herolist.command.commands;

import java.util.Arrays;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegeLevel;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class InfoCommand extends BaseCommand {

    public InfoCommand(HeroList plugin) {
        super(plugin);
        name = "List Info";
        description = "Displays the players in the list and their privileges";
        usage = "§e/ls info §9<list>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ls info");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!plugin.listExists(args[0])) {
            Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
            return;
        }

        PrivilegedList list = plugin.getList(args[0]);

        if (sender instanceof Player) {
            String name = ((Player) sender).getName();
            if (list.isRestricted()) {
                if (!list.containsPlayer(name) || !list.getPrivilegeLevel(name).clears(PrivilegeLevel.VIEWER)) {
                    Messaging.send(plugin, sender, "You cannot view $1.", args[0]);
                    return;
                }
            }
        }

        String[] players = list.getPlayerList();
        Arrays.sort(players);
        if (players.length != 0) {
            String msg = args[0] + ": ";
            for (String name : list.getPlayerList()) {
                msg += "§f" + name + "§c[§b" + list.getPrivilegeLevel(name).abbreviation + "§c], ";
            }
            msg = msg.substring(0, msg.length() - 2);
            Messaging.send(plugin, sender, msg);
        } else {
            Messaging.send(plugin, sender, "The list $1 is empty.", args[0]);
        }
    }

}
