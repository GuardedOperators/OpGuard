package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.config.OpGuardConfig;
import com.rezzedup.opguard.api.message.Loggable;
import com.rezzedup.opguard.api.message.Punishable;
import com.rezzedup.opguard.api.message.Warnable;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;

public final class Context implements Loggable, Warnable, Punishable
{
    // Command
    
    private boolean op = false;
    private boolean opguard = false;
    
    // Source
    
    private boolean isPlugin = false;
    private boolean isConsole = false;
    private boolean isPlayer = false;
    
    // Status
    
    private boolean okay = false;
    private boolean warn = false;
    private boolean security = false;
    
    // Special Action
    
    private boolean punish = false;
    
    // Message that needs context
    
    private String message = "";
    
    // Config
    
    private final OpGuardConfig config;
    
    public Context(OpGuardAPI api)
    {
        this.config = api.getConfig();
    }
    
    public Context(Context context, boolean full)
    {
        config = context.config;
        
        op = context.op;
        opguard = context.opguard;
        
        isPlugin = context.isPlugin;
        isConsole = context.isConsole;
        isPlayer = context.isPlayer;
        
        if (full)
        {
            okay = context.okay;
            warn = context.warn;
            security = context.security;
            
            punish = context.punish;
            
            message = context.message;
        }
    }
    
    private void resetCommand()
    {
        op = opguard = false;
    }
    
    private void resetSource()
    {
        isPlugin = isConsole = isPlayer = false;
    }
    
    private void resetStatus()
    {
        okay = warn = security = false;
    }
    
    public Context setOp()
    {
        resetCommand();
        op = true;
        return this;
    }
    
    public Context incorrectlyUsedOpGuard()
    {
        resetCommand();
        opguard = true;
        return this;
    }
    
    public Context pluginAttempt()
    {
        resetSource();
        isPlugin = true;
        return this;
    }
    
    public Context consoleAttempt()
    {
        resetSource();
        isConsole = true;
        return this;
    }
    
    public Context playerAttempt()
    {
        resetSource();
        isPlayer = true;
        return this;
    }
    
    public Context attemptFrom(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            return consoleAttempt();
        }
        else 
        {
            return playerAttempt();
        }
    }
    
    public Context warning(String message)
    {
        resetStatus();
        warn = true;
        return setMessage(message);
    }
    
    public Context okay(String message)
    {
        resetStatus();
        okay = true;
        return setMessage(message);
    }
    
    public Context securityRisk(String message)
    {
        resetStatus();
        security = true;
        return setMessage(message);
    }
    
    public Context punish()
    {
        punish = true;
        return this;
    }
    
    @Override
    public boolean isLoggable()
    {
        if (punish)
        {
            return true;
        }
        if (isPlayer)
        {
            return config.canLogPlayerAttempts();
        }
        if (isConsole)
        {
            return config.canLogConsoleAttempts();
        }
        if (isPlugin)
        {
            return config.canLogPluginAttempts();
        }
        return false;
    }
    
    @Override
    public boolean isWarnable()
    {
        if (security)
        {
            return config.canSendSecurityWarnings();
        }
        
        if (isPlayer)
        {
            if (op)
            {
                return config.canSendPlayerOpAttemptWarnings();
            }
            if (opguard)
            {
                return config.canSendPlayerOpGuardAttemptWarnings();
            }
        }
        else if (isConsole)
        {
            if (op)
            {
                return config.canSendConsoleOpAttemptWarnings();
            }
            if (opguard)
            {
                return config.canSendConsoleOpGuardAttemptWarnings();
            }
        }
        else if (isPlugin)
        {
            return config.canSendPluginAttemptWarnings();
        }
        
        return true;
    }
    
    @Override
    public boolean isPunishable()
    {
        if (isPlugin)
        {
            return config.canPunishPluginAttempts();
        }
        if (isConsole)
        {
            if (op)
            {
                return config.canPunishConsoleOpAttempts();
            }
            if (opguard)
            {
                return config.canPunishConsoleOpGuardAttempts();
            }
        }
        return false;
    }
    
    public String prepareMessage(String message)
    {
        if (warn)
        {
            message = config.getWarningPrefix() + " &f" + message.replaceAll("<!>", config.getWarningEmphasisColor());
        }
        else if (okay)
        {
            message = config.getOkayPrefix() + " &f" + message;
        }
        else if (security)
        {
            message = config.getSecurityPrefix() + " &f" + message;
        }
        
        return message;
    }
    
    @Override
    public Context setMessage(String message)
    {
        this.message = prepareMessage(message);
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
