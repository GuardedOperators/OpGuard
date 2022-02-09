/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2022 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.config.OpGuardConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import pl.tlinkowski.annotation.basic.NullOr;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

final class OpGuardCommand
{
    private final OpGuard api;
    private final OpGuardConfig config;
    private final OpVerifier verifier;
    
    public OpGuardCommand(OpGuard api)
    {
        this.api = api;
        this.config = api.config();
        this.verifier = api.verifier();
    }
    
    public void execute(CommandSender sender, String[] cmd)
    {
        if (!verifier.hasPassword() && config.canSendSecurityWarnings() && !(cmd.length >= 2 && cmd[1].equalsIgnoreCase("password")))
        {
            api.warn(sender, new Context(api).securityRisk("OpGuard is insecure without a password"));
        }
        
        if (cmd.length < 2)
        {
            usage(sender);
            return;
        }
        
        List<String> args = Arrays.asList(Arrays.copyOfRange(cmd, 1, cmd.length));
        
        if (config.isLocked() && !args.get(0).equalsIgnoreCase("list"))
        {
            Messenger.send(sender, "&cOpGuard (&4&lLock&c):&f OpGuard is currently locked");
            Messenger.send(sender, " &3&oTo unlock OpGuard, set &b&o&nlock&3 to &b&o&nfalse&3&o in the config and restart the server");
            return;
        }
        
        switch (args.get(0).toLowerCase())
        {
            case "op":
                setOp(sender, args, true);
                break;
            case "deop":
                setOp(sender, args, false);
                break;
            
            case "list":
                List<String> names = new ArrayList<>();
                verifier.getVerifiedOperators().forEach(o -> names.add(o.getName()));
                
                Messenger.send(sender, "&6(&e&lVerified Operators&6) &fTotal: &6" + names.size());
                if (names.size() <= 0) { names.add("No verified operators"); }
                Messenger.send(sender, "&6" + String.join(", ", names));
                break;
            
            case "password":
                setPassword(sender, args);
                break;
            case "reset":
                resetPassword(sender, args);
                break;
            
            case "reload":
                reload(sender, args);
                break;
            
            default:
                usage(sender);
        }
    }
    
    private void usage(CommandSender sender)
    {
        String usage = "&f(&6&lOpGuard &6v" + api.plugin().getDescription().getVersion() + " Usage&f)\n";
        usage += "&6/&eopguard &oop &7<&fplayer&7> <&fpassword&7 (if set)>\n";
        usage += "&6/&eopguard &odeop &7<&fplayer&7> <&fpassword&7 (if set)>\n";
        usage += "&6/&eopguard &olist\n";
        usage += "&6/&eopguard &opassword &7<&fnew password&7>\n";
        usage += "&6/&eopguard &oreset &7<&fcurrent password&7>\n";
        usage += "&6/&eopguard &oreload &7<&fpassword&7 (if set)>";
        
        Messenger.send(sender, usage);
    }
    
    private void setOp(CommandSender sender, List<String> args, boolean op)
    {
        String command = args.get(0).toLowerCase();
        Context context = new Context(api).attemptFrom(sender);
        boolean onlineOnly = (op) ? config.canOnlyOpIfOnline() : config.canOnlyDeopIfOnline();
        
        String name = (args.size() <= 1) ? "" : args.get(1);
        String plainTextPassword = (args.size() <= 2) ? "" : args.get(2);
        
        if (verifier.hasPassword())
        {
            if (plainTextPassword.isEmpty())
            {
                Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + command + " <player> <password>");
                return;
            }
        }
        else
        {
            if (name.isEmpty())
            {
                Messenger.send(sender, "&c&oCorrect Usage:&f /opguard " + command + " <player>");
                return;
            }
        }
        
        @NullOr OfflinePlayer player = getPlayer(name, onlineOnly);
        
        if (player == null)
        {
            Messenger.send(sender, "&cError:&f &7" + name + "&f is not online");
            return;
        }
        
        name = player.getName();
        boolean punish = false;
        
        if (op)
        {
            if (verifier.op(player, plainTextPassword))
            {
                context.okay(sender.getName() + " set op for &7" + name);
                Messenger.send(sender, "&aSuccess: &f" + name + " is now a verified operator");
            }
            else
            {
                context.incorrectlyUsedOpGuard().warning(
                    sender.getName() + " attempted to set op for <!>" + name + "&f using an incorrect password"
                );
                Messenger.send(sender, "&cError:&f Incorrect password");
                
                if (sender instanceof ConsoleCommandSender)
                {
                    punish = config.canPunishConsoleOpGuardAttempts();
                }
            }
        }
        else
        {
            if (verifier.deop(player, plainTextPassword))
            {
                context.okay(sender.getName() + " removed op from &7" + player.getName());
                Messenger.send(sender, "&aSuccess: &f" + name + " is no longer a verified operator");
            }
            else
            {
                context.incorrectlyUsedOpGuard().warning
                (
                sender.getName() + " attempted to remove op from <!>" + name + "&f using an incorrect password"
                );
                Messenger.send(sender, "&cError:&f Incorrect password");
            }
        }
        
        api.warn(context).log(context);
        
        if (punish)
        {
            api.punish(context, name);
        }
    }
    
    @SuppressWarnings("deprecation")
    private @NullOr OfflinePlayer getPlayer(String name, boolean online)
    {
        return (online) ? Bukkit.getPlayer(name) : Bukkit.getOfflinePlayer(name);
    }
    
    private void setPassword(CommandSender sender, List<String> args)
    {
        if (preventPasswordManagement(sender)) { return; }
        if (verifier.hasPassword())
        {
            Messenger.send(sender, "&cError:&f Password is already set. Reset the password to modify");
            return;
        }
        if (args.size() != 2)
        {
            Messenger.send(sender, "&c&oCorrect Usage:&f /opguard password <new-password>");
            if (args.size() > 2) { Messenger.send(sender, "&8&o(Don't include spaces)"); }
            return;
        }
        
        verifier.updatePassword(args.get(1));
        Context context = new Context(api).attemptFrom(sender).okay(sender.getName() + " set OpGuard's password");
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
        
        if (verifier.removePassword(args.get(1)))
        {
            context.okay(sender.getName() + " removed Opguard's password");
            Messenger.send(sender, "&aSuccess: &fRemoved OpGuard's password");
        }
        else
        {
            context.incorrectlyUsedOpGuard().warning(sender.getName() + " attempted to remove OpGuard's password");
            Messenger.send(sender, "&c&oError:&f Incorrect password");
        }
        
        api.warn(context).log(context);
    }
    
    private boolean preventPasswordManagement(CommandSender sender)
    {
        if (!config.canManagePasswordInGame() && sender instanceof Player)
        {
            Messenger.send(sender, "&cError: &fOnly console may manage the password");
            return true;
        }
        return false;
    }
    
    private void reload(CommandSender sender, List<String> args)
    {
        Context context = new Context(api).attemptFrom(sender);
        String name = sender.getName();
        
        if (verifier.hasPassword())
        {
            if (args.size() != 2)
            {
                Messenger.send(sender, "&c&oCorrect Usage:&f /opguard reload <current-password>");
                return;
            }
            
            if (!verifier.isPassword(args.get(1)))
            {
                context.incorrectlyUsedOpGuard().warning(name + " attempted to reload OpGuard's config using an incorrect password");
                api.warn(context).log(context);
                return;
            }
        }
        
        api.config().reload();
        context.okay(name + " reloaded OpGuard's config.");
        api.warn(context).log(context);
    }
}
