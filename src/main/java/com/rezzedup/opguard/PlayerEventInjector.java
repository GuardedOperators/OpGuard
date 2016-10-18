package com.rezzedup.opguard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Field;

public class PlayerEventInjector implements Listener
{
    public void inject(PlayerEvent event)
    {
        Bukkit.broadcastMessage("Recieved event.");
        
        try
        {
            Field playerField = PlayerEvent.class.getDeclaredField("player");
            playerField.setAccessible(true);
            
            Player player = (Player) playerField.get(event);
            GuardedPlayer guarded = new GuardedPlayer(player);
            
            playerField.set(event, guarded);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(AsyncPlayerChatEvent event)
    {
        inject(event);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerCommandPreprocessEvent event)
    {
        inject(event);
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void on(PlayerInteractEvent event)
    {
        inject(event);
    }
    
    
}
