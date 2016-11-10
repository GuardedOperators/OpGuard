package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
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
        if (!api.getConfig().canCheckPermissions())
        {
            return;
        }
        
        Player player = event.getPlayer();
        
        if (player.hasPermission(permission) && !api.getVerifier().isVerified(player.getUniqueId()))
        {
            if (event instanceof Cancellable)
            {
                ((Cancellable) event).setCancelled(true);
            }
            
            String name = player.getName();
            
            Context context = new Context(api)
                .playerAttempt()
                .hasInvalidPermissions()
                .warning
                (
                    "Player <!>" + name + "&f has access to all permissions but isn't a verified operator."
                );
            
            api.warn(context).log(context).punish(context, name);
        }
    }
    
    @EventHandler
    public void on(PlayerCommandPreprocessEvent event)
    {
        check(event);
    }
    
    @EventHandler
    public void on(PlayerInteractEvent event)
    {
        check(event);
    }
}
