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

import com.github.guardedoperators.opguard.util.Debug;
import pl.tlinkowski.annotation.basic.NullOr;

import java.security.Permission;
import java.util.Objects;

final class GuardedSecurityManager extends SecurityManager
{
    static void setup(OpGuard opguard)
    {
        @NullOr SecurityManager existing = System.getSecurityManager();
        GuardedSecurityManager manager = new GuardedSecurityManager(opguard, existing);
        System.setSecurityManager(manager);
    }
    
    private final OpGuard opguard;
    private final @NullOr SecurityManager prior;
    
    private GuardedSecurityManager(OpGuard opguard, @NullOr SecurityManager prior)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        this.prior = prior;
    }
    
    @Override
    public void checkPermission(Permission perm, Object context)
    {
        if (prior != null) { prior.checkPermission(perm, context); }
    }
    
    @Override
    public void checkPermission(Permission perm)
    {
        if (prior != null) { prior.checkPermission(perm); }
    }
    
    @Override
    public void checkAccept(String host, int port)
    {
        Debug.log(() -> "[Security Manager] >>> CHECK ACCEPT: " + host + ":" + port);
        if (prior != null) { prior.checkAccept(host, port); }
    }
    
    @Override
    public void checkConnect(String host, int port, Object context)
    {
        Debug.log(() -> "[Security Manager] >>> CHECK CONNECT: " + host + ":" + port + " (" + context + ")");
        if (prior != null) { prior.checkConnect(host, port, context); }
    }
    
    @Override
    public void checkConnect(String host, int port)
    {
        Debug.log(() -> "[Security Manager] >>> CHECK CONNECT: " + host + ":" + port);
        if (prior != null) { prior.checkConnect(host, port); }
    }
}
