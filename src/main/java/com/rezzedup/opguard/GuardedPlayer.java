package com.rezzedup.opguard;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerEvent;

import java.lang.reflect.Field;

public class GuardedPlayer extends WrappedPlayer
{
    public GuardedPlayer(Player implementation)
    {
        super(implementation);
    }
    
    @Override
    public void setOp(boolean value)
    {
        PluginStackChecker stack = new PluginStackChecker();
    
        if (stack.foundPlugin())
        {
            String name = stack.getPlugin().getName();
            Bukkit.broadcastMessage("---\n\nPlugin " + name + " tried to give OP to " + getName() + "\n\n---");
        }
        else
        {
            player.setOp(value);
        }
    }
    
    public static class EventInjector implements Listener
    {
        @AbstractEventRegistrar.AbstractEventHandler(priority = EventPriority.LOWEST)
        public <T extends PlayerEvent> void inject(T event)
        {
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
    }
}
