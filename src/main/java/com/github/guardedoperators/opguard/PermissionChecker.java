package com.github.guardedoperators.opguard;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class PermissionChecker implements Listener
{
    private final OpGuard api;
    private final String permission;
    
    public PermissionChecker(OpGuard api)
    {
        this.api = api;
        
        Random random = new SecureRandom();
        
        this.permission = IntStream.range(0, 50)
            .mapToObj(i -> {
                String alphabet = "AaBbCcDdEeFfGgHhIiJjKkLlMmNnOoPpQqRrSsTtUuVvWwXxYyZz0123456789";
                return String.valueOf(alphabet.charAt(random.nextInt(alphabet.length())));
            })
            .collect(Collectors.joining());
    }
    
    public void check(PlayerEvent event)
    {
        if (!api.config().canCheckPermissions()) { return; }
        
        Player player = event.getPlayer();
        
        if (!player.hasPermission(permission) || api.verifier().isVerified(player.getUniqueId()))
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
