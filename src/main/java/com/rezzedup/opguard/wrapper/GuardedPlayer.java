package com.rezzedup.opguard.wrapper;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.Messenger;
import com.rezzedup.opguard.PluginStackChecker;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Version;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.help.HelpMap;
import org.bukkit.help.HelpTopic;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;

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
            exemptVanillaCommands(30);
            // 30 attempts each taking 2 seconds should ensure the vanilla HelpTopic is generated
        }
        

        private void exemptVanillaCommands(int attempts)
        {
            if (attempts <= 0)
            {
                Messenger.send("[OpGuard] Unable to fix compatibility with vanilla commands.");
                return;
            }
            
            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    HelpMap helpMap = api.getPlugin().getServer().getHelpMap();
                    HelpTopic vanilla = helpMap.getHelpTopic("Minecraft");
    
                    if (vanilla == null)
                    {
                        exemptVanillaCommands(attempts - 1);
                        return;
                    }
    
                    String helpText = vanilla.getFullText(Bukkit.getConsoleSender());
    
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
                            exempt.add(command);
            
                            if (!command.startsWith("minecraft:"))
                            {
                                exempt.add("minecraft:" + command);
                            }
                        }
                    }
                }
            }
            .runTaskLater(api.getPlugin(), 20 * 2);
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
