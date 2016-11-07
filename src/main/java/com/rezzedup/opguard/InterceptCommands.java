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
    
    public InterceptCommands(OpGuardAPI api) 
    {
        this.api = api;
        api.registerEvents(this);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event)
    {
        String command = event.getMessage().split("\\/| ")[0];
        
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/opguard:intercepted(" + command + ")");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(ServerCommandEvent event)
    {        
        String command = event.getCommand().split("\\/| ")[0];
        
        if (cancel(event.getSender(), event.getCommand(), event))
        {
            event.setCommand("opguard:intercepted(" + command + ")");
        }
    }
    
    public boolean cancel(CommandSender sender, String command, Event event)
    {
        String[] cmd = command.split(" ");
        Context context = new Context(api);
        
        if (sender instanceof Player)
        {
            context.playerAttempt();
        }
        else 
        {
            context.consoleAttempt();
        }
        
        if (cmd.length > 0)
        {
            String base = cmd[0].toLowerCase();
            
            if (base.matches("^[\\/]?op$"))
            {
                context.setOp();
                
                if (cmd.length > 1)
                {
                    String name = cmd[1];
                    context.warning(sender.getName() + " attempted to " + base + " <!>" + name);
                    api.warn(context).log(context).punish(context, name);
                }
                return true;
            }
            else if (base.matches("^[\\/]?deop$"))
            {
                return true;
            }
            else if (cmd[0].toLowerCase().matches("^[\\/]?o(g|pguard)$"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to access OpGuard.");
                    api.warn(context).log(context);
                    return true;
                }
                
                api.run(sender, cmd);

                if (event instanceof Cancellable)
                {
                    ((Cancellable) event).setCancelled(true);
                }
            }
        }
        return false;
    }
}
