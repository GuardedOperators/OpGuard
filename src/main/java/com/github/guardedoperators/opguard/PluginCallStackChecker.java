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
package com.github.guardedoperators.opguard;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PluginCallStackChecker
{
	public static Stream<Plugin> pluginsOnStack()
	{
		return Arrays.stream(Thread.currentThread().getStackTrace())
			.filter(Predicate.not(StackTraceElement::isNativeMethod))
			.map(element -> {
				try { return (Plugin) JavaPlugin.getProvidingPlugin(Class.forName(element.getClassName())); }
				catch (Exception ignored) { return null; }
			})
			.filter(Objects::nonNull);
	}
	
	private final OpGuard opguard;
	
	PluginCallStackChecker(OpGuard opguard)
	{
		this.opguard = Objects.requireNonNull(opguard, "opguard");
	}
	
	public Result findPluginsOnStack()
	{
		return new Result(
			pluginsOnStack()
				.filter(plugin -> !(plugin instanceof OpGuardPlugin))
				.map(plugin ->
					(opguard.config().shouldExemptPlugins())
						? new FoundPlugin(plugin, opguard.config().getExemptPlugins().contains(plugin.getName()))
						: new FoundPlugin(plugin, false)
				)
				.collect(Collectors.toList())
		);
	}
	
	private static final class FoundPlugin
	{
		private final Plugin plugin;
		private final boolean isExempt;
		
		FoundPlugin(Plugin plugin, boolean isExempt)
		{
			this.plugin = plugin;
			this.isExempt = isExempt;
		}
		
		Plugin plugin() { return plugin; }
		
		boolean isExempt() { return isExempt; }
	}
	
	public static final class Result
	{
		private final List<FoundPlugin> plugins;
		private final List<Plugin> exemptPlugins;
		private final List<Plugin> caughtPlugins;
		
		Result(List<FoundPlugin> plugins)
		{
			this.plugins = plugins;
			
			this.exemptPlugins = plugins.stream()
				.filter(FoundPlugin::isExempt)
				.map(FoundPlugin::plugin)
				.collect(Collectors.toList());
			
			this.caughtPlugins = plugins.stream()
				.filter(Predicate.not(FoundPlugin::isExempt))
				.map(FoundPlugin::plugin)
				.collect(Collectors.toList());
		}
		
		public List<Plugin> allPlugins()
		{
			return plugins.stream().map(FoundPlugin::plugin).collect(Collectors.toList());
		}
		
		public List<Plugin> exemptPlugins() { return exemptPlugins; }
		
		public List<Plugin> caughtPlugins() { return caughtPlugins; }
	}
}
