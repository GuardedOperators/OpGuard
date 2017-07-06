package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.security.SecureRandom;

public class PermissionChecker implements Listener
{
    private final OpGuardAPI api;
    private final String permission;
    
    public PermissionChecker(OpGuardAPI api)
    {
        api.registerEvents(this);
        this.api = api;
        
        String chars = "Aa.BbCcDdEe-FfGgHhIiJj_KkLlMmNn0123456789OoPpQqRrSs*TtUuVvWwXxYyZz";
        SecureRandom random = new SecureRandom();
        StringBuilder str = new StringBuilder();
        
        while (str.length() < 50)
        {
            str.append(chars.charAt(random.nextInt(chars.length())));
        }
    
        permission = str.toString();
    }
    
    public void check(PlayerEvent event)
    {
        if (!api.getConfig().canCheckPermissions()) { return; }
        
        Player player = event.getPlayer();
        
        if (!player.hasPermission(permission) || api.getVerifier().isVerified(player.getUniqueId()))
        {
            return;
        }
        
        Context context = new Context(api).playerAttempt().hasInvalidPermissions();
        
        PluginStackChecker stack = new PluginStackChecker(api);
    
        // Only exempted plugins found -> exit method.
        if (!stack.hasFoundPlugin() && stack.hasAllowedPlugins())
        {
            Context allowed = context.copy();
            String name = stack.getTopAllowedPlugin().getName();
        
            allowed.okay
            (
                "The plugin &7" + name + "&f was allowed to grant all permissions to &7" + player.getName()
            );
            api.warn(allowed).log(allowed);
            
            return;
        }
        
        // Non-exempt plugin found -> warn and continue to punishment below.
        if (stack.hasFoundPlugin())
        {
            Context foundPlugin = context.copy();
            String name = stack.getPlugin().getName();
            
            foundPlugin.pluginAttempt().warning
            (
                "The plugin <!>" + name + "&f attempted to grant all permissions to &7" + player.getName()
            );
            api.warn(foundPlugin).log(foundPlugin);
            
            stack.disablePlugin(api, foundPlugin);
        }
        
        if (event instanceof Cancellable) 
        { 
            ((Cancellable) event).setCancelled(true); 
        }
        
        context.warning("Player <!>" + player.getName() + "&f has access to all permissions but isn't a verified operator");
        api.warn(context).log(context).punish(context, player.getName());
        
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerCommandPreprocessEvent event)
    {
        check(event);
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void on(PlayerInteractEvent event)
    {
        check(event);
    }
}
