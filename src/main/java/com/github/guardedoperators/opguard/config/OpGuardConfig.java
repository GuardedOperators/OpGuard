/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2021 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
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
package com.github.guardedoperators.opguard.config;

import com.github.zafarkhaja.semver.Version;
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

public final class OpGuardConfig extends BaseConfig
{
    // Update version whenever config requires updates.
    public static final Version UPDATED = Version.forIntegers(3, 2, 5);
    
    public OpGuardConfig(Plugin plugin)
    {
        super(plugin);
    }
    
    @Override
    protected void load()
    {
        Version loadedVersion;
        
        try { loadedVersion = Version.valueOf(config.getString("version")); }
        catch (RuntimeException ignored) { loadedVersion = Version.forIntegers(0); }
        
        if (loadedVersion.lessThan(UPDATED)) { migrateConfig(config, loadedVersion); }
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
    
    public boolean isLocked()
    {
        return config.getBoolean("lock");
    }
    
    public boolean canOnlyOpIfOnline()
    {
        return config.getBoolean("only-op-if-online");
    }
    
    public boolean canOnlyDeopIfOnline()
    {
        return config.getBoolean("only-deop-if-online");
    }
    
    public boolean canManagePasswordInGame()
    {
        return config.getBoolean("manage-password-in-game");
    }
    
    public boolean isManagementPermissionEnabled()
    {
        return config.getBoolean("use-opguard-management-permission-node");
    }
    
    public boolean canShutDownOnDisable()
    {
        return config.getBoolean("shutdown-on-disable");
    }
    
    public boolean canExemptSelfFromPlugMan()
    {
        return config.getBoolean("exempt-opguard-from-plugman");
    }
    
    // Inspections
    
    public long getOpListInspectionInterval()
    {
        return config.getLong("inspection-interval");
    }
    
    public boolean canCheckPermissions()
    {
        return config.getBoolean("check-permissions");
    }
    
    public boolean canDisableOtherPlugins()
    {
        return config.getBoolean("disable-malicious-plugins-when-caught");
    }
    
    public boolean canRenameOtherPlugins()
    {
        return canDisableOtherPlugins() && config.getBoolean("rename-malicious-plugins-when-caught");
    }
    
    // Plugin Exemptions
    
    public boolean shouldExemptPlugins()
    {
        return config.getBoolean("enable-exempt-plugins");
    }
    
    public List<String> getExemptPlugins()
    {
        return config.getStringList("exempt-plugins");
    }
    
    // Logging
    
    public boolean loggingIsEnabled()
    {
        return config.getBoolean("enable-logging");
    }
    
    public boolean canLogPluginAttempts()
    {
        return config.getBoolean("log-plugin-attempts");
    }
    
    public boolean canLogConsoleAttempts()
    {
        return config.getBoolean("log-console-attempts");
    }
    
    public boolean canLogPlayerAttempts()
    {
        return config.getBoolean("log-player-attempts");
    }
    
    // Messages
    
    public String getWarningPrefix()
    {
        return config.getString("warn-prefix");
    }
    
    public String getWarningEmphasisColor()
    {
        return config.getString("warn-emphasis-color");
    }
    
    public boolean canSendPluginAttemptWarnings()
    {
        return config.getBoolean("warn-plugin-attempts");
    }
    
    public boolean canSendConsoleOpAttemptWarnings()
    {
        return config.getBoolean("warn-console-op-attempts");
    }
    
    public boolean canSendConsoleOpGuardAttemptWarnings()
    {
        return config.getBoolean("warn-console-opguard-attempts");
    }
    
    public boolean canSendPlayerOpAttemptWarnings()
    {
        return config.getBoolean("warn-player-op-attempts");
    }
    
    public boolean canSendPlayerOpGuardAttemptWarnings()
    {
        return config.getBoolean("warn-player-opguard-attempts");
    }
    
    public String getSecurityPrefix()
    {
        return config.getString("security-prefix");
    }
    
    public boolean canSendSecurityWarnings()
    {
        return config.getBoolean("enable-security-warnings");
    }
    
    public String getOkayPrefix()
    {
        return config.getString("okay-prefix");
    }
    
    // Punishments
    
    public boolean canPunishPluginAttempts()
    {
        return config.getBoolean("punish-plugin-attempts");
    }
    
    public boolean canPunishConsoleOpAttempts()
    {
        return config.getBoolean("punish-console-op-attempts");
    }
    
    public boolean canPunishConsoleOpGuardAttempts()
    {
        return config.getBoolean("punish-console-opguard-attempts");
    }
    
    public List<String> getPunishmentCommands()
    {
        return config.getStringList("punishment-commands");
    }
    
    // Update Checks
    
    public boolean canCheckForUpdates()
    {
        return config.getBoolean("check-for-updates");
    }
    
    public long getUpdateCheckInterval()
    {
        return config.getLong("update-interval");
    }
    
    // Metrics
    
    public boolean metricsAreEnabled()
    {
        return config.getBoolean("metrics");
    }
    
    public String getVersion()
    {
        return config.getString("version");
    }
}
