package com.rezzedup.opguard.api.config;

import java.util.List;

public interface OpGuardConfig extends Config
{
    boolean isLocked();
    
    boolean canOnlyOpIfOnline();
    
    boolean canOnlyDeopIfOnline();
    
    boolean canManagePasswordInGame();
    
    boolean canShutDownOnDisable();
    
    boolean canExemptSelfFromPlugMan();
    
    // Inspections
    
    long getOpListInspectionInterval();
    
    boolean canCheckPermissions();
    
    boolean canDisableOtherPlugins();
    
    boolean canRenameOtherPlugins();
    
    // Plugin Exemptions
    
    boolean shouldExemptPlugins();
    
    List<String> getExemptPlugins();
    
    // Logging
    
    boolean loggingIsEnabled();
    
    boolean canLogPluginAttempts();
    
    boolean canLogConsoleAttempts();
    
    boolean canLogPlayerAttempts();
    
    // Messages
    
    String getWarningPrefix();
    
    String getWarningEmphasisColor();
    
    boolean canSendPluginAttemptWarnings();
    
    boolean canSendConsoleOpAttemptWarnings();
    
    boolean canSendConsoleOpGuardAttemptWarnings();
    
    boolean canSendPlayerOpAttemptWarnings();
    
    boolean canSendPlayerOpGuardAttemptWarnings();
    
    String getSecurityPrefix();
    
    boolean canSendSecurityWarnings();
    
    String getOkayPrefix();
    
    // Punishments
    
    boolean canPunishPluginAttempts();
    
    boolean canPunishConsoleOpAttempts();
    
    boolean canPunishConsoleOpGuardAttempts();
    
    List<String> getPunishmentCommands();
    
    // Update Checks
    
    boolean canCheckForUpdates();
    
    long getUpdateCheckInterval();
    
    // Metrics
    
    boolean metricsAreEnabled();
    
    String getVersion();
}
