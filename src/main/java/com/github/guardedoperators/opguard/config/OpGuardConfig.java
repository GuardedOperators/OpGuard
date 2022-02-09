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
package com.github.guardedoperators.opguard.config;

import com.github.guardedoperators.opguard.OpGuard;
import com.github.guardedoperators.opguard.util.Versions;
import com.github.zafarkhaja.semver.Version;
import org.bukkit.configuration.InvalidConfigurationException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class OpGuardConfig extends WrappedConfig
{
    public OpGuardConfig(OpGuard opguard)
    {
        super(opguard.plugin(), "config.yml");
        
        reloadsWith(e ->
        {
            Version loadedVersion = Versions.parseOrZero(yaml().getString("version"));
            
            if (loadedVersion.lessThan(opguard.version()))
            {
                try { migrateConfig(loadedVersion); }
                catch (IOException io) { io.printStackTrace(); }
            }
        });
    }
    
    private void migrateConfig(Version version) throws IOException
    {
        ConfigurationTemplate template = new ConfigurationTemplate("config.template.yml");
        List<String> lines = template.apply(yaml());
        
        Path backups = path().getParent().resolve("backups");
        
        if (!Files.isDirectory(backups)) { Files.createDirectories(backups); }
        
        if (Files.isRegularFile(path()))
        {
            String name = "config.yml.backup-v" + version;
            int attempt = 1;
            Path rename;
            
            do { rename = backups.resolve(name + "_" + attempt++); }
            while (Files.isRegularFile(rename));
            
            Files.move(path(), rename);
        }
        
        Files.write(path(), lines, StandardCharsets.UTF_8);
        
        try { yaml().loadFromString(String.join("\n", lines)); }
        catch (InvalidConfigurationException e) { e.printStackTrace(); }
    }
    
    public boolean isLocked()
    {
        return yaml().getBoolean("lock");
    }
    
    public boolean canOnlyOpIfOnline()
    {
        return yaml().getBoolean("only-op-if-online");
    }
    
    public boolean canOnlyDeopIfOnline()
    {
        return yaml().getBoolean("only-deop-if-online");
    }
    
    public boolean canManagePasswordInGame()
    {
        return yaml().getBoolean("manage-password-in-game");
    }
    
    public boolean isManagementPermissionEnabled()
    {
        return yaml().getBoolean("use-opguard-management-permission-node");
    }
    
    public boolean canShutDownOnDisable()
    {
        return yaml().getBoolean("shutdown-on-disable");
    }
    
    public boolean canExemptSelfFromPlugMan()
    {
        return yaml().getBoolean("exempt-opguard-from-plugman");
    }
    
    // Inspections
    
    public boolean canCheckPermissions()
    {
        return yaml().getBoolean("check-permissions");
    }
    
    public boolean canDisableOtherPlugins()
    {
        return yaml().getBoolean("disable-malicious-plugins-when-caught");
    }
    
    public boolean canRenameOtherPlugins()
    {
        return canDisableOtherPlugins() && yaml().getBoolean("rename-malicious-plugins-when-caught");
    }
    
    // Plugin Exemptions
    
    public boolean shouldExemptPlugins()
    {
        return yaml().getBoolean("enable-exempt-plugins");
    }
    
    public List<String> getExemptPlugins()
    {
        return yaml().getStringList("exempt-plugins");
    }
    
    // Logging
    
    public boolean loggingIsEnabled()
    {
        return yaml().getBoolean("enable-logging");
    }
    
    public boolean canLogPluginAttempts()
    {
        return yaml().getBoolean("log-plugin-attempts");
    }
    
    public boolean canLogConsoleAttempts()
    {
        return yaml().getBoolean("log-console-attempts");
    }
    
    public boolean canLogPlayerAttempts()
    {
        return yaml().getBoolean("log-player-attempts");
    }
    
    // Messages
    
    public String getWarningPrefix()
    {
        return yaml().getString("warn-prefix", "");
    }
    
    public String getWarningEmphasisColor()
    {
        return yaml().getString("warn-emphasis-color", "");
    }
    
    public boolean canSendPluginAttemptWarnings()
    {
        return yaml().getBoolean("warn-plugin-attempts");
    }
    
    public boolean canSendConsoleOpAttemptWarnings()
    {
        return yaml().getBoolean("warn-console-op-attempts");
    }
    
    public boolean canSendConsoleOpGuardAttemptWarnings()
    {
        return yaml().getBoolean("warn-console-opguard-attempts");
    }
    
    public boolean canSendPlayerOpAttemptWarnings()
    {
        return yaml().getBoolean("warn-player-op-attempts");
    }
    
    public boolean canSendPlayerOpGuardAttemptWarnings()
    {
        return yaml().getBoolean("warn-player-opguard-attempts");
    }
    
    public String getSecurityPrefix()
    {
        return yaml().getString("security-prefix", "");
    }
    
    public boolean canSendSecurityWarnings()
    {
        return yaml().getBoolean("enable-security-warnings");
    }
    
    public String getOkayPrefix()
    {
        return yaml().getString("okay-prefix", "");
    }
    
    // Punishments
    
    public boolean canPunishPluginAttempts()
    {
        return yaml().getBoolean("punish-plugin-attempts");
    }
    
    public boolean canPunishConsoleOpAttempts()
    {
        return yaml().getBoolean("punish-console-op-attempts");
    }
    
    public boolean canPunishConsoleOpGuardAttempts()
    {
        return yaml().getBoolean("punish-console-opguard-attempts");
    }
    
    public List<String> getPunishmentCommands()
    {
        return yaml().getStringList("punishment-commands");
    }
    
    // Update Checks
    
    public boolean canCheckForUpdates()
    {
        return yaml().getBoolean("check-for-updates");
    }
    
    public long getUpdateCheckInterval()
    {
        return yaml().getLong("update-interval");
    }
    
    // Metrics
    
    public boolean metricsAreEnabled()
    {
        return yaml().getBoolean("metrics");
    }
    
    public String getVersion()
    {
        return yaml().getString("version");
    }
}
