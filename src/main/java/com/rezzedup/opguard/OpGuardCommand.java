package com.rezzedup.opguard;

import java.util.ArrayList;
import java.util.List;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.util.Messenger;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpGuardCommand
{
    private final OpGuardAPI api;
    
    public OpGuardCommand(OpGuardAPI api)
    {
        this.api = api;
    }
    
    public void run(CommandSender sender, String[] cmd)
    {
        boolean securityWarnings = api.getConfig().getBoolean("warn.security-risk");
        boolean hashExists = api.getConfig().isSet("password.hash");
        
        if (!hashExists && securityWarnings)
        {
            Messenger.send(sender, "&f[&e&lSECURITY&f] OpGuard is insecure without a password.");
        }
        
        if (cmd.length < 2)
        {
            usage(sender);
            return;
        }
        List<String> args = new ArrayList<String>();
        
        for (int i = 1; i < cmd.length; i++)
        {
            args.add(cmd[i]);
        }
        
        switch (args.get(0).toLowerCase())
        {
            case "op":
                op(sender, args, true);
                break;
            case "deop":
                op(sender, args, false);
                break;
                
            case "list":
                List<String> names = new ArrayList<String>();
                
                for (OfflinePlayer player : Bukkit.getOperators())
                {
                    names.add(player.getName());
                }
                Messenger.send(sender, "&f[&e&lVERIFIED OPERATORS&f] Total: &6" + names.size());
                Messenger.send(sender, "&6" + String.join(", ", names));
                break;
                
            case "password":
                setPassword(sender, args);
                break;
                
            case "reset":
                resetPassword(sender, args);
                break;
                
            case "reload":
                String status = "&f[&a&lOKAY&f] " + sender.getName() + " reloaded OpGuard's config.";
                api.warn("status", status);
                api.log("status", status);
                
                //OpGuardAPI.reloadConfig();
                break;
                
            default:
                usage(sender);
        }
        
        
    }
    
    private void usage(CommandSender sender)
    {
        String usage 
               = "&f[&6&lOpGuard &6v" + api.getPlugin().getDescription().getVersion() + " Usage&f]\n";
        usage += "&e/opguard op <player> <password (if set)>\n";
        usage += "&e/opguard deop <player> <password (if set)>\n";
        usage += "&e/opguard list\n";
        usage += "&e/opguard password <new password>\n";
        usage += "&e/opguard reset <current password>\n";
        usage += "&e/opguard reload";
        
        Messenger.send(sender, usage);
    }
    
    private void op(CommandSender sender, List<String> args, boolean op)
    {
        String arg = args.get(0).toLowerCase();
        String hash = api.getConfig().getString("password.hash");
        String type = "status";
        String message = null;
        
        boolean enabled = (hash != null);
        boolean online = false;
        
        OfflinePlayer player;
        
        if (op)
        {
            online = api.getConfig().getBoolean("only-op-if-online");
        }
        
        if (enabled && args.size() != 3)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + arg + " <player> <password>");
            return;
        }
        else if (!enabled && args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + arg + " <player>");
            return;
        }
        
        try
        {
            player = getPlayer(args.get(1), online);
            Password pass = (enabled) ? new Password(args.get(2)) : null;
            
            if (op)
            {
                //Verify.op(player, pass);
                message = "&f[&a&lOKAY&f] " + sender.getName() + "&f set op for `&7" + player.getName() + "&f`";
            }
            else
            {
                //Verify.deop(player, pass);
                message = "&f[&a&lOKAY&f] " + sender.getName() + "&f removed op from `&7" + player.getName() + "&f`";
            }
        }
        catch (Exception e)
        {
            Messenger.send(sender, e.getMessage());
        }
        finally
        {
            if (message != null)
            {
                api.warn(type, message);
                api.log(type, message);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private OfflinePlayer getPlayer(String name, boolean online) throws Exception
    {
        OfflinePlayer player;
        if (online)
        {
            player = Bukkit.getPlayer(name);
            
            if (player == null)
            {
                throw new Exception("&cPlayer `&o" + name + "&c` is not online.");
            }
            return player;
        }
        else
        {
            return Bukkit.getOfflinePlayer(name);
        }
    }
    
    private void setPassword(CommandSender sender, List<String> args)
    {
        boolean inGame = api.getConfig().getBoolean("manage.password-in-game");
        String hash = api.getConfig().getString("password.hash");
        boolean enabled = (hash != null);
        
        if (!inGame && sender instanceof Player)
        {
            Messenger.send(sender, "&cOnly console may manage the password.");
            return;
        }
        if (enabled)
        {
            Messenger.send(sender, "&cPassword is already set! Reset the password to modify.");
            return;
        }
        if (args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard password <new-password>");
            return;
        }
        Password pass = new Password(args.get(1));
        api.getConfig().set("password.hash", pass.getHash());
        //OpGuard.updatedConfig();
        
        String type = "status";
        String message = "&f[&a&lOKAY&f] " + sender.getName() + " set OpGuard's password.";
        api.warn(type, message);
        api.log(type, message);
    }
    
    private void resetPassword(CommandSender sender, List<String> args)
    {
        boolean inGame = api.getConfig().getBoolean("manage.password-in-game");
        String hash = api.getConfig().getString("password.hash");
        boolean enabled = (hash != null);
        
        if (!inGame && sender instanceof Player)
        {
            Messenger.send(sender, "&cOnly console may manage the password.");
            return;
        }
        if (!enabled)
        {
            Messenger.send(sender, "&cThere isn't a password yet!");
            return;
        }
        if (args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard reset <current-password>");
            return;
        }
        Password pass = new Password(args.get(1));
        
        if (!hash.equals(pass.getHash()))
        {
            Messenger.send(sender, "&cIncorrect password.");
            return;
        }
        api.getConfig().set("password.hash", null);
        //OpGuard.updatedConfig();
        
        String type = "status";
        String message = "&f[&a&lOKAY&f] " + sender.getName() + " removed OpGuard's password.";
        api.warn(type, message);
        api.log(type, message);
    }
}
