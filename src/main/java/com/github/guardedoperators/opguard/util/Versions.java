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

import com.github.zafarkhaja.semver.Version;
import pl.tlinkowski.annotation.basic.NullOr;

public class Versions
{
    private Versions() { throw new UnsupportedOperationException(); }
    
    public static final Version ZERO = Version.forIntegers(0);
    
    public static Version parseOrZero(@NullOr String versionString)
    {
        if (versionString == null) { return ZERO; }
        try { return Version.valueOf(versionString); }
        catch (RuntimeException e) { return ZERO; }
    }
}
