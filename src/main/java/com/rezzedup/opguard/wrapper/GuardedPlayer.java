package com.rezzedup.opguard.wrapper;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.PluginStackChecker;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
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

public final class GuardedPlayer extends WrappedPlayer
{
    private final OpGuardAPI api;
    
    private GuardedPlayer(Player implementation, OpGuardAPI api)
    {
        super(implementation);
        this.api = api;
    }
    
    @Override
    public void setOp(boolean value)
    {
        PluginStackChecker stack = new PluginStackChecker(api);
    
        if (stack.foundPlugin())
        {
            String name = stack.getPlugin().getName();
            Context context = new Context(api).pluginAttempt().setOp();
            
            if (value)
            {
                context.warning("The plugin <!>" + name + "&f attempted to op <!>" + getName());
            }
            else 
            {
                context.warning("The plugin <!>" + name + "&f attempted to remove op from &7" + getName());
            }
    
            api.warn(context).log(context);
            
            if (value && !api.getVerifier().isVerified(getUniqueId()))
            {
                api.punish(context, getName());
                stack.disablePlugin(api, context);
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
            OpGuardConfig config = api.getConfig();
            
            if (config.shouldExemptCommands())
            {
                String command = event.getMessage().replaceAll("^\\/| .*", "").toLowerCase();
                
                if (!command.matches("^((de)?op|o(g|pguard))$"))
                {
                    for (String exempt : config.getExemptCommands())
                    {
                        if (command.equalsIgnoreCase(exempt)) { return; }
                    }
                }
            }
            
            inject(event);
        }
    
        @EventHandler(priority = EventPriority.LOWEST)
        public void on(PlayerInteractEvent event)
        {
            inject(event);
        }
    }
}
