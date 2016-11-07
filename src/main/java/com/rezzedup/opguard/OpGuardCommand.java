package com.rezzedup.opguard;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.rezzedup.opguard.api.ExecutableCommand;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import com.rezzedup.opguard.api.Verifier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OpGuardCommand implements ExecutableCommand
{
    private final OpGuardAPI api;
    private final OpGuardConfig config;
    private final Verifier verifier;
    
    public OpGuardCommand(OpGuardAPI api)
    {
        this.api = api;
        this.config = api.getConfig();
        this.verifier = api.getVerifier();
    }
    
    public void execute(CommandSender sender, String[] cmd)
    {
        if (!verifier.hasPassword() && config.canSendSecurityWarnings())
        {
            api.warn(sender, new Context(api).securityRisk("OpGuard is insecure without a password."));
        }
        
        if (cmd.length < 2)
        {
            usage(sender);
            return;
        }
        
        List<String> args = Arrays.asList(Arrays.copyOfRange(cmd, 1, cmd.length));
        
        switch (args.get(0).toLowerCase())
        {
            case "op":
                setOp(sender, args, true);
                break;
            case "deop":
                setOp(sender, args, false);
                break;
                
            case "list":
                List<String> names = new ArrayList<String>();
                verifier.getVerifiedOperators().forEach(o -> names.add(o.getName()));
                
                Messenger.send(sender, "&6(&e&lVerified Operators&6) &fTotal: &6" + names.size());
                
                if (names.size() <= 0)
                {
                    names.add("No verified operators.");
                }
                
                Messenger.send(sender, "&6" + String.join(", ", names));
                break;
                
            case "password":
                setPassword(sender, args);
                break;
                
            case "reset":
                resetPassword(sender, args);
                break;
                
            case "reload":
                Context status = new Context(api).okay(sender.getName() + " reloaded OpGuard's config.");
                api.warn(status).log(status);
                
                // TODO: reload config
                break;
                
            default:
                usage(sender);
        }
    }
    
    private void usage(CommandSender sender)
    {
        String usage = "&f(&6&lOpGuard &6v" + api.getPlugin().getDescription().getVersion() + " Usage&f)\n";
        usage += "&e/opguard op <player> <password (if set)>\n";
        usage += "&e/opguard deop <player> <password (if set)>\n";
        usage += "&e/opguard list\n";
        usage += "&e/opguard password <new password>\n";
        usage += "&e/opguard reset <current password>\n";
        usage += "&e/opguard reload";
        
        Messenger.send(sender, usage);
    }
    
    private void setOp(CommandSender sender, List<String> args, boolean op)
    {
        String command = args.get(0).toLowerCase();
        Context context = new Context(api).attemptFrom(sender);
        boolean passwordEnabled = verifier.hasPassword();
        boolean onlineOnly = op && config.canOnlyOpIfOnline();
        
        if (passwordEnabled && args.size() != 3)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + command + " <player> <password>");
            return;
        }
        else if (!passwordEnabled && args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + command + " <player>");
            return;
        }

        String name = args.get(1);
        OfflinePlayer player = getPlayer(name, onlineOnly);
        Password password = (passwordEnabled) ? new Password(args.get(2)) : null;
    
        if (player == null)
        {
            Messenger.send(sender, "&cError:&f `&7" + name + "&f` is not online.");
            return;
        }
        
        name = player.getName();
        
        if (op)
        {
            if (verifier.op(player, password))
            {
                context.okay(sender.getName() + " set op for `&7" + name + "&f`");
                Messenger.send(sender, "&aSuccess: &f" + name + " is now a verified operator.");
            }
            else 
            {
                context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to set op for player `<!>" + name + "&f` using an incorrect password.");
                Messenger.send(sender, "&cError:&f Incorrect password.");
            }
        }
        else
        {
            if (verifier.deop(player, password))
            {
                context.okay(sender.getName() + " removed op from `&7" + player.getName() + "&f`");
                Messenger.send(sender, "&aSuccess: &f" + name + " is no longer a verified operator.");
            }
            else 
            {
                context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to remove op from player `<!>" + name + "&f` using an incorrect password.");
                Messenger.send(sender, "&cError:&f Incorrect password.");
            }
        }
        
        api.warn(context).log(context);
    }
    
    @SuppressWarnings("deprecation")
    private OfflinePlayer getPlayer(String name, boolean online)
    {
        return (online) ? Bukkit.getPlayer(name) : Bukkit.getOfflinePlayer(name);
    }
    
    private void setPassword(CommandSender sender, List<String> args)
    {
        if (preventPasswordManagement(sender))
        {
            return;
        }
        if (verifier.hasPassword())
        {
            Messenger.send(sender, "&cPassword is already set! Reset the password to modify.");
            return;
        }
        if (args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard password <new-password>");
            return;
        }
        verifier.setPassword(new Password(args.get(1)));
        Context context = new Context(api).attemptFrom(sender).okay(sender.getName() + " set OpGuard's password.");
        api.warn(context).log(context);
    }
    
    private void resetPassword(CommandSender sender, List<String> args)
    {
        if (preventPasswordManagement(sender))
        {
            return;
        }
        if (!verifier.hasPassword())
        {
            Messenger.send(sender, "&cThere is no password yet!");
            return;
        }
        if (args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard reset <current-password>");
            return;
        }
        
        Context context = new Context(api).attemptFrom(sender);
        
        if (verifier.removePassword(new Password(args.get(1))))
        {
            context.okay(sender.getName() + " removed Opguard's password.");
            Messenger.send(sender, "&aSuccess: &fRemoved OpGuard's password.");
        }
        else 
        {
            context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to remove OpGuard's password.");
            Messenger.send(sender, "&c&oError:&f Incorrect password.");
        }

        api.warn(context).log(context);
    }
    
    private boolean preventPasswordManagement(CommandSender sender)
    {
        if (!config.canManagePasswordInGame() && sender instanceof Player)
        {
            Messenger.send(sender, "&cOnly console may manage the password.");
            return true;
        }
        return false;
    }
}
