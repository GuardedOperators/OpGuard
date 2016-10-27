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
    
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsoleCommand(ServerCommandEvent event)
    {        
        String command = event.getCommand();
        
        if (cancel(event.getSender(), command, event))
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
                    
                    context.setMessage(sender.getName() + " attempted to " + base + " `<!>" + name + "&f`").warning();
                    
                    api.warn(context);
                    api.log(context);
                    api.punish(context, name);
                }
                
                return true;
            }
            else if (cmd[0].toLowerCase().matches("^[\\/]?o(g|pguard)$"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    context.incorrectlyUsedOpGuard(); // TODO: context message
                    
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
