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

import pl.tlinkowski.annotation.basic.NullOr;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;
import java.util.regex.Pattern;

public class Placeholders
{
    public static Pattern PATTERN = Pattern.compile("%(.+?)%");
    
    protected final Map<String, Supplier<?>> placeholders = new HashMap<>();
    
    public String get(@NullOr String placeholder)
    {
        if (Strings.isNullOrEmpty(placeholder)) { return ""; }
        
        Supplier<?> supplier = placeholders.get(placeholder.toLowerCase(Locale.ROOT));
        if (supplier == null) { return ""; }
        
        @NullOr Object result = null;
        try { result = supplier.get(); }
        catch (Exception e) { e.printStackTrace(); }
        
        return (result == null) ? "" : String.valueOf(result);
    }
    
    private static String escape(String literal)
    {
        return literal.replace("\\", "\\\\").replace("$", "\\$");
    }
    
    public String update(String message)
    {
        return PATTERN.matcher(message).replaceAll(mr -> {
            String value = get(mr.group(1));
            return escape((value.isEmpty()) ? mr.group() : value);
        });
    }
    
    public Putter map(String ... placeholders)
    {
        Objects.requireNonNull(placeholders, "placeholders");
        if (placeholders.length <= 0) { throw new IllegalArgumentException("Empty placeholders array"); }
        return new Putter(placeholders);
    }
    
    public void inherit(Placeholders from) { placeholders.putAll(from.placeholders); }
    
    public class Putter
    {
        private final String[] aliases;
        
        private Putter(String[] aliases) { this.aliases = aliases; }
        
        public void to(Supplier<?> supplier)
        {
            Objects.requireNonNull(supplier, "supplier");
            
            for (String alias : aliases)
            {
                if (Strings.isNullOrEmpty(alias)) { continue; }
                placeholders.put(alias.toLowerCase(Locale.ROOT), supplier);
            }
        }
    }
}
