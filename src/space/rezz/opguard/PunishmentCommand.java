package space.rezz.opguard;

import org.bukkit.Bukkit;

public class PunishmentCommand
{
    public static void execute(String name)
    {
        String command = OpGuard.getInstance().getConfig().getString("punish.command");
        command = command.replaceAll("(%player%)", name);
        
        Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), command);
        Messenger.broadcast("&f[&a&lOKAY&f] Punished `&7" + name + "&f` for attempting to gain op.", "opguard.warn");
    }
}
