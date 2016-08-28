package com.rezzedup.opguard;

import org.bukkit.Bukkit;

public class PunishmentCommand
{
    public static void execute(String name)
    {
        String command = OpGuard.getInstance().getConfig().getString("punish.command");
        command = command.replaceAll("(%player%)", name);
        
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        
        String type = "status";
        String message = "&f[&a&lOKAY&f] Punished `&7" + name + "&f` for attempting to gain op.";
        
        OpGuard.warn(type, message);
        OpGuard.log(type, "Executed punishment command: /" + command);
        OpGuard.log(type, message);
    }
}
