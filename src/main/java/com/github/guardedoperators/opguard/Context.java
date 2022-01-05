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
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import pl.tlinkowski.annotation.basic.NullOr;

public final class Context
{
    public enum Cause
    {
        OP_COMMAND,
        OPGUARD_COMMAND,
        INVALID_PERMISSION
    }
    
    public enum Source
    {
        PLUGIN,
        CONSOLE,
        PLAYER;
    }
    
    public enum Status
    {
        OKAY,
        WARN,
        SECURITY
    }
    
    private @NullOr Cause cause = null;
    private @NullOr Source source = null;
    private Status status = Status.OKAY;
    private String message = "";
    private boolean punishmentActionTaken = false;
    private final OpGuardConfig config;
    
    public Context(OpGuard opguard)
    {
        this.config = opguard.config();
    }
    
    public Context(Context existing, boolean full)
    {
        this.config = existing.config;
        this.cause = existing.cause;
        this.source = existing.source;
        
        if (full)
        {
            this.status = existing.status;
            this.message = existing.message;
            this.punishmentActionTaken = existing.punishmentActionTaken;
        }
    }
    
    public Context setOp()
    {
        this.cause = Cause.OP_COMMAND;
        return this;
    }
    
    public Context incorrectlyUsedOpGuard()
    {
        this.cause = Cause.OPGUARD_COMMAND;
        return this;
    }
    
    public Context hasInvalidPermissions()
    {
        this.cause = Cause.INVALID_PERMISSION;
        return this;
    }
    
    public Context pluginAttempt()
    {
        this.source = Source.PLUGIN;
        return this;
    }
    
    public Context consoleAttempt()
    {
        this.source = Source.CONSOLE;
        return this;
    }
    
    public Context playerAttempt()
    {
        this.source = Source.PLAYER;
        return this;
    }
    
    public Context attemptFrom(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender) { return consoleAttempt(); }
        else { return playerAttempt(); }
    }
    
    public Context warning(String message)
    {
        this.status = Status.WARN;
        return setMessage(message);
    }
    
    public Context okay(String message)
    {
        this.status = Status.OKAY;
        return setMessage(message);
    }
    
    public Context securityRisk(String message)
    {
        this.status = Status.SECURITY;
        return setMessage(message);
    }
    
    public Context punishment()
    {
        punishmentActionTaken = true;
        return this;
    }
    
    public boolean isLoggable()
    {
        if (punishmentActionTaken) { return true; }
        
        if (source != null)
        {
            switch (source)
            {
                case PLAYER: return config.canLogPlayerAttempts();
                case CONSOLE: return config.canLogConsoleAttempts();
                case PLUGIN: return config.canLogPluginAttempts();
            }
        }
        
        return true;
    }
    
    public boolean isWarnable()
    {
        if (status == Status.SECURITY) { return config.canSendSecurityWarnings(); }
        
        if (source != null)
        {
            switch (source)
            {
                case PLAYER:
                {
                    if (cause != null)
                    {
                        switch (cause)
                        {
                            case OP_COMMAND: return config.canSendPlayerOpAttemptWarnings();
                            case OPGUARD_COMMAND: return config.canSendPlayerOpGuardAttemptWarnings();
                        }
                    }
                    break;
                }
                case CONSOLE:
                {
                    if (cause != null)
                    {
                        switch (cause)
                        {
                            case OP_COMMAND: return config.canSendConsoleOpAttemptWarnings();
                            case OPGUARD_COMMAND: return config.canSendConsoleOpGuardAttemptWarnings();
                        }
                    }
                    break;
                }
                case PLUGIN:
                {
                    return config.canSendPluginAttemptWarnings();
                }
            }
        }
        
        return true;
    }
    
    public boolean isPunishable()
    {
        if (source != null)
        {
            switch (source)
            {
                case CONSOLE:
                {
                    if (cause != null)
                    {
                        switch (cause)
                        {
                            case OP_COMMAND: return config.canPunishConsoleOpAttempts();
                            case OPGUARD_COMMAND: return config.canPunishConsoleOpGuardAttempts();
                        }
                    }
                    break;
                }
                case PLUGIN:
                {
                    return config.canPunishPluginAttempts();
                }
            }
        }
        
        if (cause == Cause.INVALID_PERMISSION)
        {
            return config.canCheckPermissions();
        }
        
        return false;
    }
    
    private String prepare(String text)
    {
        switch (status)
        {
            case OKAY: return config.getOkayPrefix() + " &f" + text;
            case WARN: return config.getWarningPrefix() + " &f" + text.replaceAll("<!>", config.getWarningEmphasisColor());
            case SECURITY: return config.getSecurityPrefix() + " &f" + text;
        }
        
        return text;
    }
    
    public Context setMessage(String message)
    {
        this.message = prepare(message);
        return this;
    }
    
    public String getMessage()
    {
        return message;
    }
    
    public boolean hasMessage()
    {
        return !message.isEmpty();
    }
    
    public Context copy()
    {
        return new Context(this, false);
    }
}
