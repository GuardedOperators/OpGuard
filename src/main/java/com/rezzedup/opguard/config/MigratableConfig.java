package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.Version;
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
        // Version will be 0.0.0 if config version is null.
        Version loadedVersion = Version.of(config.getString("version"));
        
        // Todo: Update isAtLeast() whenever config requires updates.
        if (!loadedVersion.isAtLeast(3, 2, 5))
        {
            migrateConfig(config, loadedVersion);
        }
    }
    
    private void migrateConfig(FileConfiguration old, Version version)
    {
        ConfigurationTemplate template = new ConfigurationTemplate(this, "config.template.yml");
        List<String> lines = template.apply(old);
        
        File dir = plugin.getDataFolder();
        
        if (file.exists())
        {
            String name = "config.yml.backup-v" + version.toString();
            File rename = new File(dir, name);
            int attempt = 0;
            
            while (rename.exists())
            {
                attempt += 1;
                String updatedName = name + "_" + attempt;
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
    public boolean isLocked()
    {
        return config.getBoolean("lock");
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
    public boolean isManagementPermissionEnabled()
    {
        return config.getBoolean("use-opguard-management-permission-node");
    }
    
    @Override
    public boolean canShutDownOnDisable()
    {
        return config.getBoolean("shutdown-on-disable");
    }
    
    @Override
    public boolean canExemptSelfFromPlugMan()
    {
        return config.getBoolean("exempt-opguard-from-plugman");
    }
    
    // Inspections
    
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
    public boolean isJarCheckEnabled(){
        return config.getBoolean("check-jar-integrity");
    }

    @Override
    public boolean isDataCheckEnabled(){
        return config.getBoolean("check-data-integrity");
    }

    @Override
    public boolean useFastCheck(){
        return config.getBoolean("fast-check");
    }

    @Override
    public boolean canShutDownOnCheckFail(){
        return config.getBoolean("shutdown-on-check-fail");
    }

    @Override
    public long getCheckInspectionInterval(){
        return config.getLong("integrity-check-interval");
    }

    
    // Plugin Exemptions
    
    @Override
    public boolean shouldExemptPlugins()
    {
        return config.getBoolean("enable-exempt-plugins");
    }
    
    @Override
    public List<String> getExemptPlugins()
    {
        return config.getStringList("exempt-plugins");
    }
    
    // Logging
    
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
    public boolean canLogOverwriteAttempts()
    {
        return config.getBoolean("log-overwrite-attempts");
    }
    
    // Messages
    
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
    
    // Punishments
    
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
    
    // Update Checks
    
    @Override
    public boolean canCheckForUpdates()
    {
        return config.getBoolean("check-for-updates");
    }
    
    @Override
    public long getUpdateCheckInterval()
    {
        return config.getLong("update-interval");
    }
    
    // Metrics
    
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
