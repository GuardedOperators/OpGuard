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
import com.github.guardedoperators.opguard.util.Plugins;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.logging.Level;

public final class PunishmentHandler
{
    private final OpGuard opguard;
    
    PunishmentHandler(OpGuard opguard)
    {
        this.opguard = opguard;
    }
    
    public void punishUsername(PunishmentReason reason, String username)
    {
        Placeholders placeholders = new Placeholders();
        
        placeholders.map("player", "username").to(() -> username);
        
        for (String command : opguard.config().getPunishmentCommands())
        {
            Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), placeholders.update(command));
        }
        
        opguard.notifications().usernamePunished(username);
    }
    
    public void punishPlayer(PunishmentReason reason, Player player)
    {
        punishUsername(reason, player.getName());
    }
    
    private static Path renameJarFile(Path path) throws IOException
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
    public void handleCaughtPlugins(PunishmentReason reason, PluginStackTrace stack)
    {
        if (!stack.hasCaughtPlugins()) { return; }
        
        PluginOnStack caught = stack.topCaughtPlugin();
        
        if (caught.isExempt())
        {
            opguard.logger().warning(
                "The plugin " + caught.name() + " is defined in the exempt-plugins list, " +
                "but plugin exemptions are currently disabled."
            );
        }
        
        if (opguard.config().canDisableOtherPlugins())
        {
            opguard.server().getPluginManager().disablePlugin(caught.plugin());
            
            opguard.logger().warning(
                "Disabled plugin " + caught.name() + ". Remove it from the server immediately!"
            );
            
            if (opguard.config().canRenameOtherPlugins())
            {
                Path jar = Plugins.jarFilePath(caught.plugin());
                
                if (!Files.isRegularFile(jar))
                {
                    opguard.logger().warning("Could not find jar file for plugin: " + caught.name());
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
