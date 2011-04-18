package com.herocraftonline.dthielke.herolist.command.commands;

import java.util.Map;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.HeroList.Permission;
import com.herocraftonline.dthielke.herolist.PrivilegedList.Level;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class ViewCommand extends BaseCommand {

    public ViewCommand(HeroList plugin) {
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

        Map<String, PrivilegedList> lists = plugin.getLists();
        if (!lists.containsKey(args[0])) {
            Messaging.send(plugin, sender, "There is no list named $1.", args[0]);
            return;
        }

        PrivilegedList list = lists.get(args[0]);

        if (player != null) {
            String name = player.getName();
            if (list.isRestricted()) {
                if (!list.contains(name) || !list.get(name).clears(Level.VIEWER)) {
                    if (!plugin.hasPermission(player, Permission.ADMIN_VIEW)) {
                        Messaging.send(plugin, sender, "You cannot view $1.", args[0]);
                        return;
                    }
                }
            }
        }

        Set<String> players = list.getUserSet();
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
