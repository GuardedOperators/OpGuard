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

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public final class OpPassword
{
	private String hash;
	
	public OpPassword(OpPassword clone)
	{
		this.hash = clone.hash;
	}
	
	public OpPassword(String plaintext)
	{
		this(plaintext, false);
	}
	
	OpPassword(String input, boolean isHash)
	{
		if (isHash)
		{
			this.hash = input;
			return;
		}
		
		String pass = input + " :^) Enjoy!";
		
		try
		{
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			byte[] hash = digest.digest(pass.getBytes(StandardCharsets.UTF_8));
			StringBuilder hashed = new StringBuilder();
			
			for (byte b : hash)
			{
				hashed.append(String.format("%02X", b));
			}
			this.hash = hashed.toString().toLowerCase();
		}
		catch (NoSuchAlgorithmException e) { throw new RuntimeException(e); }
	}
	
	public String getHash()
	{
		return this.hash;
	}
}
