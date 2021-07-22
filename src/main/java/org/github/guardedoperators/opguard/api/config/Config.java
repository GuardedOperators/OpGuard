package org.github.guardedoperators.opguard.api.config;

import org.bukkit.configuration.file.FileConfiguration;

public interface Config
{
    void reload();
    
    FileConfiguration get();
}
