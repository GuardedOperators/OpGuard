package com.rezzedup.opguard.wrapper;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.Messenger;
import com.rezzedup.opguard.PluginStackChecker;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
            String action = ((value) ? "op" : "deop") + " &7" + getName();
            OpGuardConfig config = api.getConfig();
            boolean isAllowed = config.shouldExemptPlugins() && config.getExemptPlugins().contains(name);
            
            if (!isAllowed)
            {
                context.warning("The plugin <!>" + name + "&f attempted to " + action);
            }
            else 
            {
                context.okay("The plugin &7" + name + "&f was allowed to " + action);
            }
    
            api.warn(context).log(context);
            
            if (!isAllowed)
            {
                if (value && !api.getVerifier().isVerified(getUniqueId()))
                {
                    api.punish(context, getName());
                    stack.disablePlugin(api, context);
                }
                return;
            }
        }
        
        player.setOp(value);
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
            new ExemptionTask(this, "Minecraft").runTask(api.getPlugin());
            // Exempting commands from already-loaded plugins
            new ExemptionTask(this).runTask(api.getPlugin());
        }
        
        @EventHandler
        public void on(PluginEnableEvent event)
        {
            new ExemptionTask(this, event.getPlugin().getName()).runTask(api.getPlugin());
        }
    
        private void injectEvent(PlayerEvent event)
        {
            if (api.getConfig().canInjectPlayerEvents())
            {
                inject(event);
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
            injectEvent(event);
        }
    
        @SuppressWarnings("deprecation")
        @EventHandler(priority = EventPriority.LOWEST)
        public void on(PlayerChatEvent event)
        {
            injectEvent(event);
        }
    
        @EventHandler(priority = EventPriority.LOWEST)
        public void on(PlayerInteractEvent event)
        {
            injectEvent(event);
        }
    
        @EventHandler(priority = EventPriority.LOWEST)
        public void on(PlayerCommandPreprocessEvent event)
        {
            OpGuardConfig config = api.getConfig();
            
            if (!config.canInjectPlayerCommands())
            {
                return;
            }
            
            String command = event.getMessage().replaceAll("^\\/| .*", "").toLowerCase();
    
            if (!command.matches("^((minecraft:)?(de)?op|o(g|pguard))$"))
            {
                if (exempt.contains(command)) { return; }
                
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
    }
    
    private static class ExemptionTask extends BukkitRunnable
    {
        private final EventInjector injector;
        private final OpGuardAPI api;
        
        private String specific = null;
        
        private ExemptionTask(EventInjector injector)
        {
            this.injector = injector;
            this.api = injector.api;
        }
        
        private ExemptionTask(EventInjector injector, String specific)
        {
            this(injector);
            this.specific = specific;
        }
        
        @Override
        public void run()
        {
            if (specific == null)
            {
                exemptLoadedPlugins();
            }
            else 
            {
                exemptSpecificPlugin(specific);
            }
        }
        
        private void exemptLoadedPlugins()
        {
            for (Plugin plugin : api.getPlugin().getServer().getPluginManager().getPlugins())
            {
                exemptSpecificPlugin(plugin.getName());
            }
        }
        
        private boolean isExempted(String name)
        {
            if (name.equals("Minecraft")) 
            { 
                return true; 
            }
            
            OpGuardConfig config = api.getConfig();
            
            for (String exempt : config.getExemptPlugins())
            {
                if (exempt.equalsIgnoreCase(name))
                {
                    if (config.shouldExemptPlugins())
                    {
                        return true;
                    }
                    else 
                    {
                        api.warn(new Context(api).warning
                        (
                            "The plugin &7" + name + "&f is defined in OpGuard's exempt-plugins list, " +
                            "but plugin exemptions are currently disabled"
                        ))
                        .warn(new Context(api).warning
                        (
                            "Errors may occur when using commands from &7" + name + "&f until plugin " +
                            "exemptions are enabled in OpGuard's config"
                        ));
                        return false;
                    }
                }
            }
            return false;
        }
        
        private void exemptSpecificPlugin(String name)
        {
            if (!isExempted(name)) 
            { 
                return; 
            }
    
            String commandPrefix = name.toLowerCase() + ":";
            Set<String> commands = getCommandsFrom(name);
    
            if (commands.isEmpty())
            {
                Messenger.send("[OpGuard] &cUnable to exempt commands from " + name);
                return;
            }
            
            for (String command : commands)
            {
                injector.exempt.add(command);
    
                if (!command.startsWith(commandPrefix))
                {
                    injector.exempt.add(commandPrefix + command);
                }
            }
            
            Messenger.send("[OpGuard] Exempted all commands defined for " + name);
        }
        
        private Set<String> getCommandsFrom(String name)
        {
            Set<String> commands = new HashSet<>();
            Server server = api.getPlugin().getServer();
            
            HelpTopic helpTopic = server.getHelpMap().getHelpTopic(name);
    
            if (helpTopic != null)
            {
                String helpText = helpTopic.getFullText(server.getConsoleSender());
    
                for (String text : helpText.split("\n"))
                {
                    text = ChatColor.stripColor(text);
        
                    if (!text.startsWith("/"))
                    {
                        continue;
                    }
        
                    String command = text.replaceAll("(^.*\\/)|(: .*)", "");
        
                    if (!command.isEmpty())
                    {
                        commands.add(command);
                    }
                }
            }
    
            Plugin plugin = server.getPluginManager().getPlugin(name);
            
            if (plugin != null)
            {
                Map<String, Map<String, Object>> definedCommands =  plugin.getDescription().getCommands();
                
                for (String command : definedCommands.keySet())
                {
                    commands.add(command);
                    
                    Map<String, Object> properties = definedCommands.get(command);
    
                    Object aliases = properties.get("aliases");
                    
                    if (aliases != null)
                    {
                        if (aliases instanceof String)
                        {
                            commands.add((String) aliases);
                        }
                        else
                        {
                            ((List<String>) aliases).forEach(commands::add);
                        }
                    }
                }
            }
            
            return commands;
        }
    }
}
