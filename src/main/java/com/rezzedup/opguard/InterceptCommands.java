package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class InterceptCommands implements Listener
{
    private final OpGuardAPI api;
    
    public InterceptCommands(OpGuardAPI API) 
    {
        this.api = API;
        API.registerEvents(this);
    }
    
    
    @EventHandler(priority= EventPriority.LOWEST)
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
    
    public boolean cancel(CommandSender sender, String command, Event event)
    {
        String[] cmd = command.split(" ");
        
        boolean punishConsole = api.getConfig().getBoolean("punish.console-attempt");
        boolean punishPlayer = api.getConfig().getBoolean("punish.player-attempt");
        boolean isPlayer = (sender instanceof Player);
        
        String attempt = (isPlayer) ? "player-attempt" : "console-attempt";
        
        if (cmd.length > 0)
        {
            if (cmd[0].toLowerCase().matches("^[\\/]?(de)?op$"))
            {
                if (cmd.length > 1)
                {
                    String name = cmd[1];
                    String message = "&f[&c&lWARNING&f] " + sender.getName() + " attempted to " + 
                                     cmd[0].toLowerCase() + " `&c" + name + "&f`";
                    
                    api.warn(attempt, message);
                    api.log(attempt, message);
                    
                    if ((!isPlayer && punishConsole) || (isPlayer && punishPlayer))
                    {
                        api.punish(name);
                    }
                }
                return true;
            }
            else if (cmd[0].toLowerCase().matches("^[\\/]?o(g|pguard)$"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    return true;
                }
                
                api.getCommand().run(sender, cmd);

                if (event instanceof Cancellable)
                {
                    ((Cancellable) event).setCancelled(true);
                }
            }
        }
        return false;
    }
}
