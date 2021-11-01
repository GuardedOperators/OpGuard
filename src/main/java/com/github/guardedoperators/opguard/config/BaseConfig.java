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

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

abstract class BaseConfig
{
    protected final Plugin plugin;
    protected final File file;
    protected FileConfiguration config;
    
    BaseConfig(Plugin plugin, String filename)
    {
        this.plugin = plugin;
        
        if (filename == null)
        {
            this.file = new File(plugin.getDataFolder(), "config.yml");
            this.config = plugin.getConfig();
        }
        else
        {
            this.file = new File(plugin.getDataFolder(), filename);
            this.config = YamlConfiguration.loadConfiguration(file);
        }
        
        load();
    }
    
    BaseConfig(Plugin plugin)
    {
        this(plugin, null);
    }
    
    protected abstract void load();
    
    public FileConfiguration yaml()
    {
        return config;
    }
    
    public void reload()
    {
        try { config.load(file); }
        catch (IOException | InvalidConfigurationException e) { e.printStackTrace(); }
    }
}
