package com.herocraftonline.dthielke.herolist.util;

import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Messaging {

    public static void send(JavaPlugin plugin, CommandSender player, String msg, String ... params) {
        player.sendMessage(parameterizeMessage(plugin, msg, params));
    }
    
    public static void broadcast(JavaPlugin plugin, String msg, String ... params) {
        plugin.getServer().broadcastMessage(parameterizeMessage(plugin, msg, params));
    }
    
    private static String parameterizeMessage(JavaPlugin plugin, String msg, String ... params) {
        msg = "[" + plugin.getDescription().getName() + "] " + "§c" + msg;
        for (int i = 0; i < params.length; i++) {
            msg = msg.replace("$" + (i+1), "§f" + params[i] + "§c");
        }
        return msg;
    }
    
}
