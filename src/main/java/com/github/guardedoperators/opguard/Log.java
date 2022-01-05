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

import org.bukkit.ChatColor;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Log
{
    private final File file;
    
    public Log(Plugin plugin, String name)
    {
        this.file = new File(plugin.getDataFolder(), name + ".log");
        
        try { this.file.createNewFile(); }
        catch (IOException e) { e.printStackTrace(); }
    }
    
    private String now()
    {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss a");
        return "[" + LocalDateTime.now().format(formatter) + "]";
    }
    
    public void append(String message)
    {
        message = ChatColor.stripColor(ChatColor.translateAlternateColorCodes('&', message));
        message = now() + " " + message + "\n";
        byte[] msg = message.getBytes();
        
        try
        {
            Files.write(this.file.toPath(), msg, StandardOpenOption.APPEND);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }
}
