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
package com.github.guardedoperators.opguard.util;

import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.nio.file.Path;
import java.util.Optional;

public class Plugins
{
    private Plugins() { throw new UnsupportedOperationException(); }
    
    public static Optional<Plugin> pluginOfClass(Class<?> clazz)
    {
        try { return Optional.of(JavaPlugin.getProvidingPlugin(clazz)); }
        catch (Exception ignored) { return Optional.empty(); }
    }
    
    public static Optional<Plugin> pluginOfClassByName(String fullyQualifiedClassName)
    {
        try { return pluginOfClass(Class.forName(fullyQualifiedClassName)); }
        catch (ClassNotFoundException ignored) { return Optional.empty(); }
    }
    
    public static Path jarFilePath(Plugin plugin)
    {
        Class<?> clazz = plugin.getClass();
        return Path.of(clazz.getProtectionDomain().getCodeSource().getLocation().getFile());
    }
}
