package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

public class InterceptCommands implements Listener
{
    private final OpGuardAPI api;
    
    public InterceptCommands(OpGuardAPI API) 
    {
        this.api = API;
        API.registerEvents(this);
    }
    
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        if (cancel(event.getPlayer(), event.getMessage(), event))
        {
            event.setMessage("/");
        }
    }
    
    @EventHandler(priority = EventPriority.LOWEST)
    public void onConsoleCommand(ServerCommandEvent event)
    {        
        String command = event.getCommand();
        
        if (cancel(event.getSender(), command, event))
        {
            event.setCommand("opguard:intercepted(" + command + ")");
        }
    }
    
    public boolean cancel(CommandSender sender, String command, Event event)
    {
        String[] cmd = command.split(" ");
        
        FileConfiguration config = api.getConfig();
        String prefix = config.getString("warn-prefix");
        String emphasis = config.getString("warn-emphasis-color");
        
        boolean isPlayer = (sender instanceof Player);
        
        if (cmd.length > 0)
        {
            String base = cmd[0].toLowerCase();
            
            if (base.matches("^[\\/]?op$"))
            {
                if (cmd.length > 1)
                {
                    boolean log = false;
                    boolean warn = false;
                    boolean punish = false;
                    
                    String name = cmd[1];
                    String message = sender.getName() + " attempted to " + base + " `" + emphasis + name + "&r`";
                    
                    if (isPlayer)
                    {
                        warn = config.getBoolean("warn-player-op-attempts");
                        log = config.getBoolean("log-player-attempts");
                    }
                    else
                    {
                        warn = config.getBoolean("warn-console-op-attempts");
                        log = config.getBoolean("log-console-attempts");
                        punish = config.getBoolean("punish-console-op-attempts");
                    }
                    
                    if (warn)
                    {
                        api.warn(message);
                    }
                    if (log)
                    {
                        api.log(message);
                    }
                    if (punish)
                    {
                        api.punish(name);
                    }
                }
                return true;
            }
            else if (cmd[0].toLowerCase().matches("^[\\/]?o(g|pguard)$"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    return true;
                }
                
                api.run(sender, cmd);

                if (event instanceof Cancellable)
                {
                    ((Cancellable) event).setCancelled(true);
                }
            }
        }
        return false;
    }
}
