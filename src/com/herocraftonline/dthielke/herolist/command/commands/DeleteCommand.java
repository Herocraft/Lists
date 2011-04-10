package com.herocraftonline.dthielke.herolist.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegeLevel;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class DeleteCommand extends BaseCommand {

    public DeleteCommand(HeroList plugin) {
        super(plugin);
        name = "Delete List";
        description = "Deletes an existing list";
        usage = "§e/ls delete §9<list>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ls delete");
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
            if (!list.getPrivilegeLevel(name).clears(PrivilegeLevel.OWNER)) {
                Messaging.send(plugin, sender, "You do not own $1.", args[0]);
                return;
            }
        }
        
        plugin.removeList(args[0]);
        Messaging.send(plugin, sender, "Deleted list $1.", args[0]);
    }

}
