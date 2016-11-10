package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

final class CommandInterceptor implements Listener
{
    private final OpGuardAPI api;
    
    public CommandInterceptor(OpGuardAPI api) 
    {
        this.api = api;
        api.registerEvents(this);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event)
    {
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/opguard:intercepted(" + event.getMessage().split("\\/| ")[0] + ")");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(ServerCommandEvent event)
    {
        if (cancel(event.getSender(), event.getCommand(), event))
        {
            event.setCommand("opguard:intercepted(" + event.getCommand().split("\\/| ")[0] + ")");
        }
    }
    
    private boolean cancel(CommandSender sender, String command, Event event)
    {
        String[] cmd = command.split(" ");
        String base = cmd[0].toLowerCase();
        
        if (!base.matches("^[\\/]?((de)?op|o(g|pguard))$"))
        {
            return false;
        }
        
        Context context = new Context(api).attemptFrom(sender);
        PluginStackChecker stack = new PluginStackChecker(api);
        
        if (stack.foundPlugin())
        {
            String name = stack.getPlugin().getName();
            
            context.pluginAttempt().warning
            (
                "The plugin <!>" + name + "&f attempted to make &7" + sender.getName() + 
                "&f execute <!>" + ((!command.startsWith("/")) ? "/" : "") + command
            );
            api.warn(context).log(context);
            
            stack.disablePlugin(api, context);
            return true;
        }
        
        if (base.matches("^[\\/]?op$"))
        {
            context.setOp();
            
            if (cmd.length > 1)
            {
                String name = cmd[1];
                context.warning(sender.getName() + " attempted to " + base + " <!>" + name);
                api.warn(context).log(context).punish(context, name);
            }
        }
        else if (cmd[0].toLowerCase().matches("^[\\/]?o(g|pguard)$"))
        {
            if (sender.hasPermission("opguard.manage"))
            {
                api.run(sender, cmd);
    
                if (event instanceof Cancellable)
                {
                    ((Cancellable) event).setCancelled(true);
                }
            }
            else
            {
                context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to access OpGuard.");
                api.warn(context).log(context);
            }
        }
        return true;
    }
}
