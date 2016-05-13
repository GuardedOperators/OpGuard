package space.rezz.opguard;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import space.rezz.opguard.util.Messenger;

public class ManagementCommand
{
    public static void run(CommandSender sender, String[] cmd)
    {
        boolean securityWarnings = OpGuard.getInstance().getConfig().getBoolean("warn.security-risks");
        boolean hashExists = OpGuard.getInstance().getConfig().isSet("password.hash");
        boolean passRequired = OpGuard.getInstance().getConfig().getBoolean("password.require");
        
        if ((!hashExists || !passRequired) && securityWarnings)
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
                
            default:
                usage(sender);
        }
        
        
    }
    
    private static void usage(CommandSender sender)
    {
        Messenger.send(sender, "OpGuard v1.0");
    }
    
    private static void op(CommandSender sender, List<String> args, boolean op)
    {
        String arg = args.get(0).toLowerCase();
        String hash = OpGuard.getInstance().getConfig().getString("password.hash");
        String type = "status";
        String message = null;
        
        boolean enabled = (hash != null);
        boolean online = false;
        
        OfflinePlayer player;
        
        if (op)
        {
            online = OpGuard.getInstance().getConfig().getBoolean("only-op-if-online");
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
                Verify.op(player, pass);
                message = "&f[&a&lOKAY&f] " + sender.getName() + "&f set op for `&7" + player.getName() + "&f`";
            }
            else
            {
                Verify.deop(player, pass);
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
                OpGuard.warn(type, message);
                OpGuard.log(type, message);
            }
        }
    }
    
    @SuppressWarnings("deprecation")
    private static OfflinePlayer getPlayer(String name, boolean online) throws Exception
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
    
    private static void setPassword(CommandSender sender, List<String> args)
    {
        String hash = OpGuard.getInstance().getConfig().getString("password.hash");
        boolean enabled = (hash != null);
        
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
        OpGuard.getInstance().getConfig().set("password.hash", pass.getHash());
        Messenger.broadcast("&f[&a&lOKAY&f] " + sender.getName() + " set OpGuard's password.", "opguard.warn");
    }
    
    private static void resetPassword(CommandSender sender, List<String> args)
    {
        String hash = OpGuard.getInstance().getConfig().getString("password.hash");
        boolean enabled = (hash != null);
        
        if (!enabled)
        {
            Messenger.send(sender, "&cThere isn't a password yet!");
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
        OpGuard.getInstance().getConfig().set("password.hash", null);
        Messenger.broadcast("&f[&a&lOKAY&f] " + sender.getName() + " removed OpGuard's password.", "opguard.warn");
    }
}
