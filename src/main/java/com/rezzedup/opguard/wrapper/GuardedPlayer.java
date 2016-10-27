package com.rezzedup.opguard.wrapper;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.PluginStackChecker;
import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.lang.reflect.Field;

public class GuardedPlayer extends WrappedPlayer
{
    private final OpGuardAPI api;
    
    public GuardedPlayer(Player implementation, OpGuardAPI api)
    {
        super(implementation);
        this.api = api;
    }
    
    @Override
    public void setOp(boolean value)
    {
        FileConfiguration config = api.getConfig();
        PluginStackChecker stack = new PluginStackChecker();
    
        if (value && stack.foundPlugin())
        {
            String name = stack.getPlugin().getName();
            
            Context context = new Context(api)
                .pluginAttempt()
                .setOp()
                .setMessage("The plugin `<!>" + name + "&f` tried giving op to `<!>" + getName() + "&f`")
                .warning();
            
            api.warn(context);
            api.log(context);
            
            if (config.getBoolean("disable-malicious-plugins-when-caught"))
            {
                Bukkit.getPluginManager().disablePlugin(stack.getPlugin());
                
                context = context.copy().okay().setMessage
                (
                    "Disabled the plugin &7" + name + "&f. Please remove it from your server as soon as possible."
                );
                
                api.warn(context);
                api.log(context);
            }
        }
        else
        {
            player.setOp(value);
        }
    }
    
    public static class EventInjector implements Listener
    {
        private final OpGuardAPI api;
        
        public EventInjector(OpGuardAPI api)
        {
            this.api = api;
            api.registerEvents(this);
        }
        
        private void inject(PlayerEvent event)
        {
            if (event.getPlayer() instanceof GuardedPlayer)
            {
                return;
            }
            
            try
            {
                Field playerField = PlayerEvent.class.getDeclaredField("player");
                playerField.setAccessible(true);
        
                Player player = (Player) playerField.get(event);
                GuardedPlayer guarded = new GuardedPlayer(player, api);
                
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
    
        @SuppressWarnings("deprecation")
        @EventHandler(priority = EventPriority.LOWEST)
        public void on(PlayerChatEvent event)
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
}
