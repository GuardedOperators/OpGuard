package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.api.OpGuardAPI;
import com.github.guardedoperators.opguard.api.config.OpGuardConfig;
import com.github.guardedoperators.opguard.api.message.Loggable;
import com.github.guardedoperators.opguard.api.message.Punishable;
import com.github.guardedoperators.opguard.api.message.Warnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public final class Context implements Loggable, Warnable, Punishable
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
    
    private Cause cause = null;
    private Source source = null;
    private Status status = Status.OKAY;
    private String message = "";
    private boolean punishmentActionTaken = false;
    private final OpGuardConfig config;
    
    public Context(OpGuardAPI api)
    {
        this.config = api.getConfig();
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
    
    @Override
    public boolean isLoggable()
    {
        if (punishmentActionTaken) { return true; }
        
        switch (source)
        {
            case PLAYER: return config.canLogPlayerAttempts();
            case CONSOLE: return config.canLogConsoleAttempts();
            case PLUGIN: return config.canLogPluginAttempts();
        }
        
        return true;
    }
    
    @Override
    public boolean isWarnable()
    {
        if (status == Status.SECURITY) { return config.canSendSecurityWarnings(); }
        
        switch (source)
        {
            case PLAYER:
            {
                switch (cause)
                {
                    case OP_COMMAND: return config.canSendPlayerOpAttemptWarnings();
                    case OPGUARD_COMMAND: return config.canSendPlayerOpGuardAttemptWarnings();
                }
                break;
            }
            case CONSOLE:
            {
                switch (cause)
                {
                    case OP_COMMAND: return config.canSendConsoleOpAttemptWarnings();
                    case OPGUARD_COMMAND: return config.canSendConsoleOpGuardAttemptWarnings();
                }
                break;
            }
            case PLUGIN:
            {
                return config.canSendPluginAttemptWarnings();
            }
        }
        
        return true;
    }
    
    @Override
    public boolean isPunishable()
    {
        switch (source)
        {
            case CONSOLE:
            {
                switch (cause)
                {
                    case OP_COMMAND: return config.canPunishConsoleOpAttempts();
                    case OPGUARD_COMMAND: return config.canPunishConsoleOpGuardAttempts();
                }
                break;
            }
            case PLUGIN:
            {
                return config.canPunishPluginAttempts();
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
    
    @Override
    public Context setMessage(String message)
    {
        this.message = prepare(message);
        return this;
    }
    
    @Override
    public String getMessage()
    {
        return message;
    }
    
    @Override
    public boolean hasMessage()
    {
        return message != null && !message.isEmpty();
    }
    
    public Context copy()
    {
        return new Context(this, false);
    }
    
    public Context fullCopy()
    {
        return new Context(this, true);
    }
    
}
