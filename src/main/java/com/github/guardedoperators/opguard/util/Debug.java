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

import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.logging.Logger;

public class Debug
{
    private static final boolean ENABLED = Boolean.getBoolean("com.github.guardedoperators.opguard.debug");
    
    private static final Logger LOGGER = Logger.getLogger("OpGuard (Debug)");
    
    public static void log(Supplier<String> message) { if (ENABLED) { LOGGER.info(message); } }
    
    public static void with(Consumer<Logger> action) { if (ENABLED) { action.accept(LOGGER); } }
}
