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
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.File;
import java.io.IOException;
import java.util.Optional;
import java.util.function.Consumer;

public abstract class WrappedConfig
{
	private final Plugin plugin;
	protected final File file;
	protected FileConfiguration config;
	
	private @NullOr Consumer<? super Optional<Exception>> reloadHandler;
	
	public WrappedConfig(Plugin plugin, String filename, @NullOr FileConfiguration config)
	{
		this.plugin = plugin;
		this.file = new File(plugin.getDataFolder(), filename);
		this.config = (config != null) ? config : YamlConfiguration.loadConfiguration(file);
	}
	
	protected void reloadsWith(Consumer<? super Optional<Exception>> reloadHandler)
	{
		this.reloadHandler = reloadHandler;
		reloadHandler.accept(Optional.empty());
	}
	
	public FileConfiguration yaml() { return config; }
	
	public Plugin plugin() { return plugin; }
	
	public void reload()
	{
		@NullOr Exception exception = null;
		
		try
		{
			config.load(file);
		}
		catch (IOException | InvalidConfigurationException e)
		{
			exception = e;
			e.printStackTrace();
		}
		finally
		{
			if (reloadHandler != null) { reloadHandler.accept(Optional.ofNullable(exception)); }
		}
	}
}
