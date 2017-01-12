package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
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
        String command = event.getMessage();
        
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/opguard:intercepted(" + command.replaceAll("\\/| .*", "") + ")");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(ServerCommandEvent event)
    {
        String command = event.getCommand();
        
        if (cancel(event.getSender(), command, event))
        {
            event.setCommand("opguard:intercepted(" + command.replaceAll("\\/| .*", "") + ")");
        }
    }
    
    private boolean cancel(CommandSender sender, String command, Event event)
    {
        String[] cmd = command.split(" ");
        String base = cmd[0].toLowerCase();
        
        if (!base.matches("^[\\/]?((minecraft:)?(de)?op|o(g|pguard))$"))
        {
            return false;
        }
        
        Context context = new Context(api).attemptFrom(sender);
        OpGuardConfig config = api.getConfig();
        PluginStackChecker stack = new PluginStackChecker(api);
        
        if (stack.hasFoundPlugin())
        {
            String name = stack.getPlugin().getName();
            
            if (config.getExemptPlugins().contains(name))
            {
                Context exemption = context.copy();
                
                exemption.warning
                (
                    "The plugin &7" + name + "&f is defined in the exempt-plugins list, " +
                    "but plugin exemptions are currently disabled"
                );
                api.warn(exemption).log(exemption);
            }
    
            context.pluginAttempt().warning
            (
                "The plugin <!>" + name + "&f attempted to make &7" + sender.getName() +
                "&f execute <!>" + ((!command.startsWith("/")) ? "/" : "") + command
            );
            api.warn(context).log(context);
    
            stack.disablePlugin(api, context);
            return true;
        }
        else if (stack.hasAllowedPlugins())
        {
            Context allowed = context.copy();
            String name = stack.getTopAllowedPlugin().getName();
    
            allowed.okay
            (
                "The plugin &7" + name + "&f was allowed to execute &7" +
                ((!base.startsWith("/")) ? "/" : "") + base +
                "&f on behalf of &7" + sender.getName()
            );
            api.warn(allowed).log(allowed);
        }
        
        if (base.matches("^[\\/]?(minecraft:)?op$"))
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
                context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to access OpGuard");
                api.warn(context).log(context);
            }
        }
        return true;
    }
}
