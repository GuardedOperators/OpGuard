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

import com.github.guardedoperators.opguard.util.Plugins;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class PluginStackTrace
{
    public static Stream<Plugin> pluginsOnStack()
    {
        return Arrays.stream(Thread.currentThread().getStackTrace())
            .filter(Predicate.not(StackTraceElement::isNativeMethod))
            .flatMap(element -> Plugins.pluginOfClassByName(element.getClassName()).stream());
    }
    
    static PluginStackTrace generate(OpGuard opguard)
    {
        return new PluginStackTrace(
            opguard,
            pluginsOnStack()
                .filter(plugin -> plugin.getClass() != OpGuardPlugin.class)
                .map(plugin -> new PluginOnStack(
                    plugin, opguard.config().getExemptPlugins().contains(plugin.getName())
                ))
                .collect(Collectors.toList())
        );
    }
    
    private final List<PluginOnStack> allFoundPlugins;
    private final List<PluginOnStack> exemptPlugins;
    private final List<PluginOnStack> caughtPlugins;
    
    PluginStackTrace(OpGuard opguard, List<PluginOnStack> plugins)
    {
        Objects.requireNonNull(opguard, "opguard");
        this.allFoundPlugins = List.copyOf(plugins);
        
        List<PluginOnStack> exempt = new ArrayList<>();
        List<PluginOnStack> caught = new ArrayList<>();
        
        for (PluginOnStack found : plugins)
        {
            List<PluginOnStack> list =
                (found.isExempt() && opguard.config().shouldExemptPlugins())
                    ? exempt : caught;
            
            list.add(found);
        }
        
        this.exemptPlugins = List.copyOf(exempt);
        this.caughtPlugins = List.copyOf(caught);
    }
    
    public List<PluginOnStack> allPlugins() { return allFoundPlugins; }
    
    public List<PluginOnStack> exemptPlugins() { return exemptPlugins; }
    
    public boolean hasExemptPlugins() { return exemptPlugins.size() > 0; }
    
    public boolean hasOnlyExemptPlugins() { return hasExemptPlugins() && !hasCaughtPlugins(); }
    
    public PluginOnStack topExemptPlugin() { return exemptPlugins.get(0); }
    
    public List<PluginOnStack> caughtPlugins() { return caughtPlugins; }
    
    public boolean hasCaughtPlugins() { return caughtPlugins.size() > 0; }
    
    public PluginOnStack topCaughtPlugin() { return caughtPlugins.get(0); }
}
