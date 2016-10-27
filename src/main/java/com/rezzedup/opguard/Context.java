package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.configuration.file.FileConfiguration;

public class Context
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
    
    private boolean punish = false;
    
    private String message = "";
    
    private final FileConfiguration config;
    
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
    
    public Context warning()
    {
        resetStatus();
        warn = true;
        return this;
    }
    
    public Context okay()
    {
        resetStatus();
        okay = true;
        return this;
    }
    
    public Context securityRisk()
    {
        resetStatus();
        security = true;
        return this;
    }
    
    public Context punish()
    {
        punish = true;
        return this;
    }
    
    public boolean isLoggable()
    {
        if (punish)
        {
            return true;
        }
        if (isPlayer)
        {
            return config.getBoolean("log-player-attempts");
        }
        if (isConsole)
        {
            return config.getBoolean("log-console-attempts");
        }
        if (isPlugin)
        {
            return config.getBoolean("log-plugin-attempts");
        }
        return false;
    }
    
    public boolean isWarnable()
    {
        if (security)
        {
            return config.getBoolean("enable-security-warnings");
        }
        
        if (isPlayer)
        {
            if (op)
            {
                return config.getBoolean("warn-player-op-attempts");
            }
            if (opguard)
            {
                return config.getBoolean("warn-player-opguard-attempts");
            }
        }
        else if (isConsole)
        {
            if (op)
            {
                return config.getBoolean("warn-console-op-attempts");
            }
            if (opguard)
            {
                return config.getBoolean("warn-console-opguard-attempts");
            }
        }
        else if (isPlugin)
        {
            return config.getBoolean("warn-plugin-attempts");
        }
        
        return true;
    }
    
    public boolean isPunishable()
    {
        if (isPlugin)
        {
            return config.getBoolean("punish-plugin-attempts");
        }
        if (isConsole)
        {
            if (op)
            {
                return config.getBoolean("punish-console-op-attempts");
            }
            if (opguard)
            {
                return config.getBoolean("punish-console-opguard-attempts");
            }
        }
        return false;
    }
    
    public String prepareMessage(String message)
    {
        if (warn)
        {
            message = config.getString("warn-prefix") + " &f" + 
                message.replaceAll("<!>", config.getString("warn-emphasis-color"));
        }
        else if (okay)
        {
            message = config.getString("okay-prefix") + " &f" + message;
        }
        else if (security)
        {
            message = config.getString("security-prefix") + " &f" + message;
        }
        
        return message;
    }
    
    public Context setMessage(String message)
    {
        this.message = prepareMessage(message);
        return this;
    }
    
    public String getMessage()
    {
        return message;
    }
    
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
