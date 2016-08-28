package com.rezzedup.opguard;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class InterceptCommands implements Listener
{
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/");
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onConsoleCommand(ServerCommandEvent event)
    {        
        if (cancel(event.getSender(), event.getCommand(), event))
        {
            event.setCommand("");
        }
    }
    
    public boolean cancel(CommandSender sender, String command, Cancellable event)
    {
        String[] cmd = command.split(" ");
        
        boolean punishConsole = OpGuard.getInstance().getConfig().getBoolean("punish.console-attempt");
        boolean punishPlayer = OpGuard.getInstance().getConfig().getBoolean("punish.player-attempt");
        boolean player = (sender instanceof Player);
        
        String attempt = (player) ? "player-attempt" : "console-attempt";
        
        if (cmd.length > 0)
        {
            if (cmd[0].toLowerCase().matches("((?:\\/)?(?:de)?op)"))
            {
                if (cmd.length > 1)
                {
                    String message = "&f[&c&lWARNING&f] " + sender.getName() + " attempted to " + cmd[0].toLowerCase() + " `&c" + cmd[1] + "&f`";
                    
                    OpGuard.warn(attempt, message);
                    OpGuard.log(attempt, message);
                    
                    if ((!player && punishConsole) || (player && punishPlayer))
                    {
                        PunishmentCommand.execute(cmd[1]);
                    }
                }
                return true;
            }
            else if (cmd[0].toLowerCase().matches("((?:\\/)?o(?:g)?(?:pguard)?)"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    return true;
                }
                ManagementCommand.run(sender, cmd);
                event.setCancelled(true);
            }
        }
        return false;
    }
}
