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

import com.github.guardedoperators.opguard.Password;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Connections
{
    private Connections() { throw new UnsupportedOperationException(); }
    
    private static final Pattern TOP_LEVEL_DOMAIN_PATTERN = Pattern.compile("(?<domain>[\\w-]+\\.\\w+)\\.*$");
    
    private static String topLevelDomain(String domain)
    {
        Matcher matcher = TOP_LEVEL_DOMAIN_PATTERN.matcher(domain);
        return (matcher.find()) ? matcher.group("domain") : domain;
    }
    
    private static boolean isAnyDomainHashBlocked(String ... hashedDomains)
    {
        String[] blocked = new String[] { // :^)
            "a3fac1661e70bafef3286dfb1e952d9540e5db4013210270b4adacb255489ec3",
            "b4e5ea175906c5b037cdb760c8a1652db3fae5a61fe4136e77cccbb0559d62f1"
        };
        
        for (String hash : blocked)
        {
            for (String hashedDomain : hashedDomains)
            {
                if (hash.equals(hashedDomain)) { return true; }
            }
        }
        
        return false;
    }
    
    public static boolean isBlockedDomain(String domain)
    {
        String lowerCaseDomain = domain.toLowerCase(Locale.ROOT);
        return isAnyDomainHashBlocked(
            Password.Algorithm.SHA_256.passwordFromPlainText(lowerCaseDomain).hash(),
            Password.Algorithm.SHA_256.passwordFromPlainText(topLevelDomain(lowerCaseDomain)).hash()
        );
    }
}
