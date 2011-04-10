package com.herocraftonline.dthielke.herolist.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herolist.HeroList;
import com.herocraftonline.dthielke.herolist.PrivilegeLevel;
import com.herocraftonline.dthielke.herolist.PrivilegedList;
import com.herocraftonline.dthielke.herolist.command.BaseCommand;
import com.herocraftonline.dthielke.herolist.util.Messaging;

public class AddCommand extends BaseCommand {

    public AddCommand(HeroList plugin) {
        super(plugin);
        name = "Add Player";
        description = "Adds or modifies a player to an existing list";
        usage = "§e/ls add §9<list> §9<player> §8-[n|v|m|o]";
        minArgs = 2;
        maxArgs = 3;
        identifiers.add("ls add");
        notes.add("Privileges:");
        notes.add("  -n: NONE (default)");
        notes.add("  -v: VIEW");
        notes.add("  -m: MODIFY");
        notes.add("  -o: OWNER");
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
            if (!list.getPrivilegeLevel(name).clears(PrivilegeLevel.MODIFIER)) {
                Messaging.send(plugin, sender, "You cannot modify $1.", args[0]);
                return;
            }
        }
        
        PrivilegeLevel privilege = PrivilegeLevel.NONE;
        if (args.length == 3) {
            privilege = PrivilegeLevel.parse(args[2].substring(1));
        }
        list.putPlayer(args[1], privilege);
        Messaging.send(plugin, sender, "Added $1 to $2 with $3 privileges.", args[1], args[0], privilege.name());
        
    }

}
