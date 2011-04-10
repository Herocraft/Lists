package com.herocraftonline.dthielke.herolist.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegeLevel;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class CreateCommand extends BaseCommand {

    public CreateCommand(HeroList plugin) {
        super(plugin);
        name = "Create List";
        description = "Creates a new list";
        usage = "§e/ls create §9<list> §8[-r]";
        minArgs = 1;
        maxArgs = 2;
        identifiers.add("ls create");
        notes.add("Restricted (-r): only VIEW or above can see the list's members");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (plugin.listExists(args[0])) {
            Messaging.send(plugin, sender, "The list name $1 is already being used.", args[0]);
            return;
        }
        
        PrivilegedList list = new PrivilegedList(args[0]);
        if (sender instanceof Player) {
            String name = ((Player) sender).getName();
            list.putPlayer(name, PrivilegeLevel.OWNER);
        }
        
        if (args.length == 2 && args[1].equals("-r")) {
            list.setRestricted(true);
        }
        
        plugin.putList(args[0], list);
        Messaging.send(plugin, sender, "Created the list $1.", args[0]);
    }

}
