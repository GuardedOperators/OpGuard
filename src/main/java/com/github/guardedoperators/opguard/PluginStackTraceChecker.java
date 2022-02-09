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
package com.github.guardedoperators.opguard;

import com.github.guardedoperators.opguard.util.Placeholders;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PluginStackTraceChecker
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
    
    PluginStackTraceChecker(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
    }
    
    public Result findPluginsOnStack()
    {
        return new Result(
            pluginsOnStack()
                .filter(plugin -> plugin.getClass() != OpGuardPlugin.class)
                .map(plugin -> new FoundPlugin(
                    plugin, opguard.config().getExemptPlugins().contains(plugin.getName())
                ))
                .collect(Collectors.toList())
        );
    }
    
    static final class FoundPlugin
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
    
    public final class Result
    {
        private final Map<String, FoundPlugin> foundPluginsByName;
        private final List<Plugin> exemptPlugins;
        private final List<Plugin> caughtPlugins;
        
        Result(List<FoundPlugin> plugins)
        {
            Map<String, FoundPlugin> nameToFound = new LinkedHashMap<>();
            List<Plugin> exempt = new ArrayList<>();
            List<Plugin> caught = new ArrayList<>();
            
            for (FoundPlugin found : plugins)
            {
                nameToFound.put(found.plugin.getName(), found);
                
                List<Plugin> list =
                    (found.isExempt() && opguard.config().shouldExemptPlugins())
                        ? exempt : caught;
                
                list.add(found.plugin());
             }
            
            this.foundPluginsByName = Map.copyOf(nameToFound);
            this.exemptPlugins = List.copyOf(exempt);
            this.caughtPlugins = List.copyOf(caught);
        }
        
        public List<Plugin> allPlugins()
        {
            return foundPluginsByName.values().stream().map(FoundPlugin::plugin).collect(Collectors.toList());
        }
        
        public List<Plugin> exemptPlugins() { return exemptPlugins; }
        
        public boolean hasExemptPlugins() { return exemptPlugins.size() > 0; }
        
        public boolean hasOnlyExemptPlugins() { return hasExemptPlugins() && !hasCaughtPlugins(); }
        
        public Plugin topExemptPlugin() { return exemptPlugins.get(0); }
        
        public List<Plugin> caughtPlugins() { return caughtPlugins; }
        
        public boolean hasCaughtPlugins() { return caughtPlugins.size() > 0; }
        
        public Plugin topCaughtPlugin() { return caughtPlugins.get(0); }
        
        private Path jarFilePath(Plugin plugin)
        {
            Class<?> clazz = plugin.getClass();
            return Path.of(clazz.getProtectionDomain().getCodeSource().getLocation().getFile());
        }
        
        private Path renameJarFile(Path path) throws IOException
        {
            Path dir = path.getParent();
            String name = path.getFileName() + ".opguard-disabled";
            
            for (int i = 1 ;; i++)
            {
                String numericName = (i <= 1) ? name : name + "." + i;
                Path renamedPath = dir.resolve(numericName);
                
                if (!Files.exists(renamedPath))
                {
                    Files.move(path, renamedPath);
                    return renamedPath;
                }
            }
        }
        
        // TODO: announce these log warnings
        public void handleCaughtPlugins(String message)
        {
            if (!hasCaughtPlugins()) { return; }
            
            Plugin caught = topCaughtPlugin();
            PluginStackTraceChecker.FoundPlugin found = foundPluginsByName.get(caught.getName());
    
            Placeholders placeholders = new Placeholders();
            placeholders.map("plugin").to(caught::getName);
            
            opguard.logger().warning(placeholders.update(message));
            
            if (found.isExempt())
            {
                
                opguard.logger().warning(
                    "The plugin " + caught.getName() + " is defined in the exempt-plugins list, " +
                    "but plugin exemptions are currently disabled."
                );
            }
            
            if (opguard.config().canDisableOtherPlugins())
            {
                opguard.server().getPluginManager().disablePlugin(caught);
                
                opguard.logger().warning(
                    "Disabled plugin " + caught.getName() + ". Remove it from the server immediately!"
                );
                
                if (opguard.config().canRenameOtherPlugins())
                {
                    Path jar = jarFilePath(caught);
                    
                    if (!Files.isRegularFile(jar))
                    {
                        opguard.logger().warning("Could not find jar file for plugin: " + caught.getName());
                        return;
                    }
                    
                    try
                    {
                        Path renamed = renameJarFile(jar);
                        
                        opguard.logger().warning(
                            "Renamed plugin jar '" + jar.getFileName() + " to prevent it from re-enabling."
                        );
                        opguard.logger().warning(
                            "New jar name: '" + renamed.getFileName() + "' - If you believe this was a mistake, " +
                            "you can get the plugin working again by simply renaming it back to what it was."
                        );
                    }
                    catch (IOException e)
                    {
                        opguard.logger().log(
                            Level.SEVERE, "Could not rename plugin jar: '" + jar.getFileName() + "'", e
                        );
                    }
                }
            }
        }
    }
}
