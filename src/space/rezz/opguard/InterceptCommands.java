package space.rezz.opguard;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.server.ServerCommandEvent;

import space.rezz.opguard.util.Messenger;

public class InterceptCommands implements Listener
{
    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event)
    {
        boolean warn = OpGuard.getInstance().getConfig().getBoolean("warn.player-attempt");
        CommandSender sender = event.getPlayer();
        String command = event.getMessage();
        
        if ( cancelOp(sender, command, warn) || cancelManagement(sender, command, event) )
        {
            event.setMessage("/");
        }
    }
    
    @EventHandler(priority=EventPriority.LOWEST)
    public void onConsoleCommand(ServerCommandEvent event)
    {
        boolean warn = OpGuard.getInstance().getConfig().getBoolean("warn.console-attempt");
        CommandSender sender = event.getSender();
        String command = event.getCommand();
        
        if ( cancelOp(sender, command, warn) || cancelManagement(sender, command, event) )
        {
            event.setCommand("");
        }
    }
    
    public boolean cancelOp(CommandSender sender, String command, boolean warn)
    {
        String[] cmd = command.split(" ");
        boolean punishConsole = OpGuard.getInstance().getConfig().getBoolean("warn.console-attempt");
        boolean punishPlayer = OpGuard.getInstance().getConfig().getBoolean("warn.player-attempt");
        boolean player = (sender instanceof Player);
        
        if (cmd.length > 0)
        {
            if (cmd[0].toLowerCase().matches("((?:\\/)?(?:de)?op)"))
            {
                if (cmd.length > 1 && warn)
                {
                    Messenger.broadcast
                    (
                        "&f[&c&lWARNING&f] " + sender.getName() + " attempted to op `&c" + cmd[1] + "&f`", 
                        "opguard.warn"
                    );
                    
                    if ((!player && punishConsole) || (player && punishPlayer))
                    {
                        PunishmentCommand.execute(cmd[1]);
                    }
                }
                return true;
            }
        }
        return false;
    }
    
    public boolean cancelManagement(CommandSender sender, String command, Cancellable event)
    {
        String[] cmd = command.split(" ");
        
        if (cmd.length > 0)
        {
            if (cmd[0].toLowerCase().matches("((?:\\/)?o(?:g)?(?:pguard)?)"))
            {
                if (!sender.hasPermission("opguard.manage"))
                {
                    return true;
                }
                ManagementCommand.run(sender, cmd);
                event.setCancelled(true);
            }
        }
        return false;
    }
}
