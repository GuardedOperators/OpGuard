package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.config.OpGuardConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

public final class MigratableConfig extends BaseConfig implements OpGuardConfig
{
    public MigratableConfig(Plugin plugin)
    {
        super(plugin);
    }
    
    @Override
    protected void load()
    {
        // Check version in the future.
        if (!config.contains("version"))
        {
            migrateConfig(config);
        }
    }
    
    private void migrateConfig(FileConfiguration old)
    {
        ConfigurationTemplate template = new ConfigurationTemplate(this, "config.template.yml");
        List<String> lines = template.apply(old);
        
        File dir = plugin.getDataFolder();
        
        if (file.exists())
        {
            String name = "config.yml.old";
            File rename = new File(dir, name);
            int version = 0;
            
            while (rename.exists())
            {
                version += 1;
                String updatedName = name + version;
                rename = new File(dir, updatedName);
            }
            
            file.renameTo(rename);
        }
        
        try
        {
            file.createNewFile();
            
            Path path = Paths.get(file.toURI());
            Files.write(path, lines, Charset.forName("UTF-8"));
            
            config = YamlConfiguration.loadConfiguration(file);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    }
    
    @Override
    public long getOpListInspectionInterval()
    {
        return config.getLong("inspection-interval");
    }
    
    @Override
    public boolean canCheckPermissions()
    {
        return config.getBoolean("check-permissions");
    }
    
    @Override
    public boolean canOnlyOpIfOnline()
    {
        return config.getBoolean("only-op-if-online");
    }
    
    @Override
    public boolean canOnlyDeopIfOnline()
    {
        return config.getBoolean("only-deop-if-online");
    }
    
    @Override
    public boolean canManagePasswordInGame()
    {
        return config.getBoolean("manage-password-in-game");
    }
    
    @Override
    public boolean canDisableOtherPlugins()
    {
        return config.getBoolean("disable-malicious-plugins-when-caught");
    }
    
    @Override
    public boolean canRenameOtherPlugins()
    {
        return canDisableOtherPlugins() && config.getBoolean("rename-malicious-plugins-when-caught");
    }
    
    @Override
    public boolean canShutDownOnDisable()
    {
        return config.getBoolean("shutdown-on-disable");
    }
    
    @Override
    public boolean isLocked()
    {
        return config.getBoolean("lock");
    }
    
    @Override
    public boolean loggingIsEnabled()
    {
        return config.getBoolean("enable-logging");
    }
    
    @Override
    public boolean canLogPluginAttempts()
    {
        return config.getBoolean("log-plugin-attempts");
    }
    
    @Override
    public boolean canLogConsoleAttempts()
    {
        return config.getBoolean("log-console-attempts");
    }
    
    @Override
    public boolean canLogPlayerAttempts()
    {
        return config.getBoolean("log-player-attempts");
    }
    
    @Override
    public String getWarningPrefix()
    {
        return config.getString("warn-prefix");
    }
    
    @Override
    public String getWarningEmphasisColor()
    {
        return config.getString("warn-emphasis-color");
    }
    
    @Override
    public boolean canSendPluginAttemptWarnings()
    {
        return config.getBoolean("warn-plugin-attempts");
    }
    
    @Override
    public boolean canSendConsoleOpAttemptWarnings()
    {
        return config.getBoolean("warn-console-op-attempts");
    }
    
    @Override
    public boolean canSendConsoleOpGuardAttemptWarnings()
    {
        return config.getBoolean("warn-console-opguard-attempts");
    }
    
    @Override
    public boolean canSendPlayerOpAttemptWarnings()
    {
        return config.getBoolean("warn-player-op-attempts");
    }
    
    @Override
    public boolean canSendPlayerOpGuardAttemptWarnings()
    {
        return config.getBoolean("warn-player-opguard-attempts");
    }
    
    @Override
    public String getSecurityPrefix()
    {
        return config.getString("security-prefix");
    }
    
    @Override
    public boolean canSendSecurityWarnings()
    {
        return config.getBoolean("enable-security-warnings");
    }
    
    @Override
    public String getOkayPrefix()
    {
        return config.getString("okay-prefix");
    }
    
    @Override
    public boolean canPunishPluginAttempts()
    {
        return config.getBoolean("punish-plugin-attempts");
    }
    
    @Override
    public boolean canPunishConsoleOpAttempts()
    {
        return config.getBoolean("punish-console-op-attempts");
    }
    
    @Override
    public boolean canPunishConsoleOpGuardAttempts()
    {
        return config.getBoolean("punish-console-opguard-attempts");
    }
    
    @Override
    public List<String> getPunishmentCommands()
    {
        return config.getStringList("punishment-commands");
    }
    
    @Override
    public boolean shouldExemptCommands()
    {
        return config.getBoolean("enable-exempt-commands");
    }
    
    @Override
    public List<String> getExemptCommands()
    {
        return config.getStringList("exempt-commands");
    }
    
    @Override
    public boolean metricsAreEnabled()
    {
        return config.getBoolean("metrics");
    }
    
    @Override
    public String getVersion()
    {
        return config.getString("version");
    }
}
