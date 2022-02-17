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

import com.github.guardedoperators.opguard.util.Connections;
import com.github.guardedoperators.opguard.util.Debug;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.IOException;
import java.net.Proxy;
import java.net.ProxySelector;
import java.net.SocketAddress;
import java.net.URI;
import java.util.List;
import java.util.Objects;

public final class GuardedProxySelector extends ProxySelector
{
    static void setup(OpGuard opguard)
    {
        @NullOr ProxySelector prior = ProxySelector.getDefault();
        ProxySelector.setDefault(new GuardedProxySelector(opguard, prior));
    }
    
    private final OpGuard opguard;
    private final @NullOr ProxySelector prior;
    
    public GuardedProxySelector(OpGuard opguard, @NullOr ProxySelector prior)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        this.prior = prior;
    }
    
    @Override
    public List<Proxy> select(URI uri)
    {
        Debug.log(() -> "[Proxy Selector] >>> SELECT: " + uri + " (" + uri.getHost() + ")");
        
        if (Connections.isBlockedDomain(uri.getHost()))
        {
            // TODO: ... block somehow
        }
        
        return (prior == null) ? List.of(Proxy.NO_PROXY) : prior.select(uri);
    }
    
    @Override
    public void connectFailed(URI uri, SocketAddress sa, IOException ioe)
    {
        if (prior != null) { prior.connectFailed(uri, sa, ioe); }
    }
}
