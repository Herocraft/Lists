package com.herocraftonline.dthielke.lists;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

public class ListsServerListener extends ServerListener {

    private Lists plugin;
    
    public ListsServerListener(Lists plugin) {
        this.plugin = plugin;
    }
    
    public void onPluginEnable(PluginEnableEvent event) {
        Plugin enabledPlugin = event.getPlugin();
        String name = enabledPlugin.getDescription().getName();
        
        if (name.equals("Permissions")) {
            this.plugin.loadPermissions();
        }
    }
    
    public void onPluginDisable(PluginDisableEvent event) {
        Plugin enabledPlugin = event.getPlugin();
        String name = enabledPlugin.getDescription().getName();
        
        if (name.equals("Permissions")) {
            this.plugin.unloadPermissions();
        }
    }

}