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

import org.bukkit.Server;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Optional;
import java.util.UUID;

public enum AuthenticationMode
{
    ONLINE,
    OFFLINE,
    PROXY;
    
    private static boolean isBungeecordEnabled(Server server)
    {
        try { return server.spigot().getConfig().getBoolean("bungeecord", false); }
        catch (RuntimeException ignored) { return false; }
    }
    
    private static Optional<Class<?>> paperConfigClass()
    {
        try { return Optional.of(Class.forName("com.destroystokyo.paper.PaperConfig")); }
        catch (ClassNotFoundException ignored) { return Optional.empty(); }
    }
    
    private static boolean isPaper()
    {
        return paperConfigClass().isPresent();
    }
    
    private static AuthenticationMode paperBungeecordMode()
    {
        try
        {
            Method isProxyOnlineMode = paperConfigClass().orElseThrow().getDeclaredMethod("isProxyOnlineMode");
            boolean online = (boolean) isProxyOnlineMode.invoke(null);
            return (online) ? ONLINE : OFFLINE;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return PROXY; }
    }
    
    private static boolean isVelocityEnabled()
    {
        try
        {
            Field velocitySupport = paperConfigClass().orElseThrow().getDeclaredField("velocitySupport");
            return (boolean) velocitySupport.get(null);
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return false; }
    }
    
    private static AuthenticationMode paperVelocityMode()
    {
        try
        {
            Field velocityOnlineMode = paperConfigClass().orElseThrow().getDeclaredField("velocityOnlineMode");
            boolean online = (boolean) velocityOnlineMode.get(null);
            return (online) ? ONLINE : OFFLINE;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return PROXY; }
    }
    
    public static AuthenticationMode ofServer(Server server)
    {
        if (server.getOnlineMode()) { return ONLINE; }
        
        boolean bungee = isBungeecordEnabled(server);
        if (!isPaper()) { return (bungee) ? PROXY : OFFLINE; }
        
        if (bungee) { return paperBungeecordMode(); }
        if (isVelocityEnabled()) { return paperVelocityMode(); }
        
        return OFFLINE;
    }
    
    public static AuthenticationMode ofUuid(UUID uuid)
    {
        return (uuid.version() == 4) ? ONLINE : OFFLINE;
    }
}
