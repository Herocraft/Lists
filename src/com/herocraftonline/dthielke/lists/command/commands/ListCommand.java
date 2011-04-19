package com.herocraftonline.dthielke.lists.command.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.lists.Lists;
import com.herocraftonline.dthielke.lists.PrivilegedList;
import com.herocraftonline.dthielke.lists.Lists.Permission;
import com.herocraftonline.dthielke.lists.PrivilegedList.Level;
import com.herocraftonline.dthielke.lists.command.BaseCommand;
import com.herocraftonline.dthielke.lists.util.Messaging;

public class ListCommand extends BaseCommand {
    
    private static final int LISTS_PER_PAGE = 8;

    public ListCommand(Lists plugin) {
        super(plugin);
        name = "List Lists";
        description = "Displays all lists available to you";
        usage = "§e/ls list §8[page#]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("ls list");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
            if (!plugin.hasPermission(player, Permission.LIST) && !plugin.hasPermission(player, Permission.ADMIN_LIST)) {
                Messaging.send(plugin, sender, "You do not have permission.");
                return;
            }
        }
        
        List<PrivilegedList> lists = new ArrayList<PrivilegedList>();
        for (PrivilegedList list : plugin.getLists()) {
            Level level = null;
            if (player != null) {
                level = list.get(player.getName());
            }
            if (player == null || !list.isRestricted() || level.clears(Level.VIEWER) || plugin.hasPermission(player, Permission.ADMIN_LIST)) {
                lists.add(list);
            }
        }
        
        int page = 0;
        if (args.length != 0) {
            try {
                page = Integer.parseInt(args[0]) - 1;
            } catch (NumberFormatException e) {}
        }
        
        if (lists.isEmpty()) {
            Messaging.send(plugin, sender, "There are no lists available to you.");
        } else {
            int numLists = lists.size();
            int numPages = numLists / LISTS_PER_PAGE;
            if (numLists % LISTS_PER_PAGE != 0) {
                numPages++;
            }
            
            if (page >= numPages || page < 0) {
                page = 0;
            }
            sender.sendMessage("§c-----[ " + "§fHeroList Lists <" + (page + 1) + "/" + numPages + ">§c ]-----");
            int start = page * LISTS_PER_PAGE;
            int end = start + LISTS_PER_PAGE;
            if (end > numLists) {
                end = numLists;
            }
            for (int l = start; l < end; l++) {
                sender.sendMessage("  §a" + (l + 1) + ". " + lists.get(l).getName());
            }
        }
    }

}
