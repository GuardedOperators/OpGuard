package com.rezzedup.opguard.wrapper;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.PluginStackChecker;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.command.defaults.VanillaCommand;
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
import java.util.HashSet;
import java.util.Map;

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
        private final HashSet<String> exempt = new HashSet<>();
        
        private final OpGuardAPI api;
        
        @SuppressWarnings({"deprecation", "unchecked"})
        public EventInjector(OpGuardAPI api)
        {
            this.api = api;
            api.registerEvents(this);
    
            // Exempting all default commands...
            // They cast the Player to a CraftPlayer
            try
            {
                Server server = api.getPlugin().getServer();
                
                Field commandMapField = server.getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                SimpleCommandMap commandMap = (SimpleCommandMap) commandMapField.get(server);
                
                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                Map<String, Command> commands = (Map<String, Command>) knownCommandsField.get(commandMap);
                
                for (Command command : commands.values())
                {
                    if (command instanceof VanillaCommand)
                    {
                        exempt.add(command.getName());
                        exempt.add("minecraft:" + command.getName());
                    }
                }
            }
            catch (NoSuchFieldException | IllegalAccessException | ClassCastException e)
            {
                e.printStackTrace();;
            }
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
            String command = event.getMessage().replaceAll("^\\/| .*", "").toLowerCase();
    
            if (!command.matches("^((minecraft:)?(de)?op|o(g|pguard))$"))
            {
                if (exempt.contains(command)) { return; }
                
                OpGuardConfig config = api.getConfig();
                
                if (config.shouldExemptCommands())
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
