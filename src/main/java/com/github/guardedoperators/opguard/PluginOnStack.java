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

import org.bukkit.plugin.Plugin;

public final class PluginOnStack
{
    private final Plugin plugin;
    private final boolean isExempt;
    
    PluginOnStack(Plugin plugin, boolean isExempt)
    {
        this.plugin = plugin;
        this.isExempt = isExempt;
    }
    
    public Plugin plugin() { return plugin; }
    
    public String name() { return plugin.getName(); }
    
    public boolean isExempt() { return isExempt; }
}
