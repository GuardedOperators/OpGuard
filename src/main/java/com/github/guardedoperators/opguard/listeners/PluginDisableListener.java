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
package com.github.guardedoperators.opguard.listeners;

import com.github.guardedoperators.opguard.Messenger;
import com.github.guardedoperators.opguard.OpGuard;
import com.github.guardedoperators.opguard.OpGuardPlugin;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

import java.util.Objects;

public final class PluginDisableListener implements Listener
{
    private final OpGuard opguard;
    
    public PluginDisableListener(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
    }
    
    @EventHandler
    public void onPluginDisable(PluginDisableEvent event)
    {
        if (event.getPlugin() instanceof OpGuardPlugin && opguard.config().canShutDownOnDisable())
        {
            Messenger.console("&c[&fOpGuard was disabled&c] Shutting server down.");
            Bukkit.shutdown();
        }
    }
}
