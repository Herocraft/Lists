package com.herocraftonline.dthielke.herolist;

import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.event.server.ServerListener;
import org.bukkit.plugin.Plugin;

public class HLServerListener extends ServerListener {

    private HeroList plugin;
    
    public HLServerListener(HeroList plugin) {
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