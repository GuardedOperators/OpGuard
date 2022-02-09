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

import org.bstats.bukkit.Metrics;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public final class OpGuardPlugin extends JavaPlugin implements Listener
{
    // https://bstats.org/plugin/bukkit/OpGuard/540
    public static final int BSTATS = 540;
    
    @Override
    public void onEnable()
    {
        Path dir = getDataFolder().toPath();
        
        if (!Files.isDirectory(dir))
        {
            try { Files.createDirectories(dir); }
            catch (IOException e) { throw new RuntimeException(e); }
        }
        
        OpGuard opguard = new OpGuard(this);
        
        new UpdateCheckTask(opguard);
        
        if (opguard.config().metricsAreEnabled())
        {
            new Metrics(this, BSTATS);
        }
    }
}
