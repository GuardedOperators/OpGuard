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
import pl.tlinkowski.annotation.basic.NullOr;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Locale;
import java.util.Optional;
import java.util.UUID;

public enum AuthenticationMode
{
    ONLINE,
    OFFLINE,
    PROXY;
    
    public enum ProxyType
    {
        BUNGEE("BungeeCord"),
        VELOCITY("Velocity");
        
        final String branding;
        
        ProxyType(String branding) { this.branding = branding; }
        
        @Override
        public String toString() { return branding; }
    }
    
    public static AuthenticationMode ofUuid(UUID uuid)
    {
        return (uuid.version() == 4) ? ONLINE : OFFLINE;
    }
    
    public static Detected ofServer(Server server)
    {
        // Online mode - server cannot be behind a proxy
        if (server.getOnlineMode()) { return new Detected(ONLINE); }
        
        boolean bungee = isBungeeCordEnabled(server);
        
        // Spigot server - check if BungeeCord enabled in spigot.yml
        if (!isPaper())
        {
            return (bungee)
                ? new Detected(PROXY, ProxyType.BUNGEE)
                : new Detected(OFFLINE);
        }
        
        // Paper server - could have BungeeCord or Velocity
        if (bungee) { return new Detected(paperBungeeCordMode(), ProxyType.BUNGEE); }
        if (isVelocityEnabled()) { return new Detected(paperVelocityMode(), ProxyType.VELOCITY); }
        
        // Offline mode without any configured proxy settings
        return new Detected(OFFLINE);
    }
    
    private static boolean isBungeeCordEnabled(Server server)
    {
        try { return server.spigot().getConfig().getBoolean("bungeecord", false); }
        catch (RuntimeException ignored) { return false; }
    }
    
    private static Class<?> paperConfigClass() throws ClassNotFoundException
    {
        return Class.forName("com.destroystokyo.paper.PaperConfig");
    }
    
    private static boolean isPaper()
    {
        try
        {
            paperConfigClass();
            return true;
        }
        catch (ClassNotFoundException ignored) { return false; }
    }
    
    private static AuthenticationMode paperBungeeCordMode()
    {
        try
        {
            Method isProxyOnlineMode = paperConfigClass().getDeclaredMethod("isProxyOnlineMode");
            boolean online = (boolean) isProxyOnlineMode.invoke(null);
            return (online) ? ONLINE : OFFLINE;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return PROXY; }
    }
    
    private static boolean isVelocityEnabled()
    {
        try
        {
            Field velocitySupport = paperConfigClass().getDeclaredField("velocitySupport");
            return (boolean) velocitySupport.get(null);
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return false; }
    }
    
    private static AuthenticationMode paperVelocityMode()
    {
        try
        {
            Field velocityOnlineMode = paperConfigClass().getDeclaredField("velocityOnlineMode");
            boolean online = (boolean) velocityOnlineMode.get(null);
            return (online) ? ONLINE : OFFLINE;
        }
        catch (ReflectiveOperationException | RuntimeException ignored) { return PROXY; }
    }
    
    public static class Detected
    {
        private final AuthenticationMode mode;
        private final @NullOr ProxyType proxy;
        
        public Detected(AuthenticationMode mode, @NullOr ProxyType proxy)
        {
            if (mode == PROXY && proxy == null)
            {
                throw new IllegalArgumentException("Received PROXY authentication mode but missing proxy type");
            }
            
            this.mode = mode;
            this.proxy = proxy;
        }
        
        public Detected(AuthenticationMode mode)
        {
            this(mode, null);
        }
    
        public AuthenticationMode mode() { return mode; }
        
        public Optional<ProxyType> proxy() { return Optional.ofNullable(proxy); }
        
        @Override
        public String toString()
        {
            String lowercaseMode = mode.name().toLowerCase(Locale.ROOT);
            
            // No proxy configured
            if (proxy == null)
            {
                return lowercaseMode + " authentication mode accepting direct connections (no proxy)";
            }
            
            return (mode == PROXY)
                ? "undetermined authentication mode behind " + proxy + " proxy"
                : lowercaseMode + " authentication mode behind " + proxy + " proxy";
        }
    }
}
