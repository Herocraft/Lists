package com.herocraftonline.dthielke.lists.command.commands;

import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.PrivilegeLevel;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class ViewCommand extends BaseCommand {

    public ViewCommand(Lists plugin) {
        super(plugin);
        name = "View List";
        description = "Displays the players in the list and their privileges";
        usage = "§e/ls view §9<list>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ls view");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.VIEW) && !plugin.hasPermission(player, Permission.ADMIN_VIEW)) {
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
            if (list.isRestricted()) {
                if (!list.contains(name) || !list.get(name).clears(PrivilegeLevel.VIEWER)) {
                    if (!plugin.hasPermission(player, Permission.ADMIN_VIEW)) {
                        Messaging.send(plugin, sender, "You cannot view $1.", args[0]);
                        return;
                    }
                }
            }
        }

        Set<String> players = list.getUsers().keySet();
        if (!players.isEmpty()) {
            String msg = args[0] + ": ";
            for (String name : players) {
                msg += "§f" + name + "§c[§b" + list.get(name).abbreviation + "§c], ";
            }
            msg = msg.substring(0, msg.length() - 2);
            Messaging.send(plugin, sender, msg);
        } else {
            Messaging.send(plugin, sender, "The list $1 is empty.", args[0]);
        }
    }

}
