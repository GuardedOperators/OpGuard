/*
 * OpGuard - Password protected op.
 * Copyright © 2016-2021 OpGuard Contributors (https://github.com/GuardedOperators/OpGuard)
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

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Objects;
import java.util.Set;

final class VerifyOpListTask extends BukkitRunnable
{
	private final OpGuard opguard;
	
	public VerifyOpListTask(OpGuard opguard)
	{
		this.opguard = Objects.requireNonNull(opguard, "opguard");
		
		long interval = opguard.config().getOpListInspectionInterval();
		
		if (interval <= 0)
		{
			Messenger.console("[OpGuard] Invalid inspection interval " + interval + ". Defaulting to 4 ticks.");
			interval = 4;
		}
		
		runTaskTimer(opguard.plugin(), 1L, interval);
	}
	
	@Override
	public void run()
	{
		OpVerifier verifier = opguard.verifier();
		Set<OfflinePlayer> operators = Bukkit.getOperators();
		
		for (OfflinePlayer operator : operators)
		{
			if (!verifier.isVerified(operator))
			{
				String name = operator.getName();
				operator.setOp(false);
				Context context = new Context(opguard).pluginAttempt().setOp().warning(
					"An unknown plugin attempted to op <!>" + name + "&f. A recently-installed plugin may be to blame"
				);
				opguard.warn(context).log(context).punish(context, name);
			}
		}
	}
}
