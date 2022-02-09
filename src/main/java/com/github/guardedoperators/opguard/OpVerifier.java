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

import com.github.guardedoperators.opguard.config.WrappedConfig;
import com.github.guardedoperators.opguard.util.Cooldown;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.IOException;
import java.nio.file.Files;
import java.time.Instant;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public final class OpVerifier
{
    private final Map<UUID, OfflinePlayer> verifiedOperators = new LinkedHashMap<>();
    
    private final OpGuard opguard;
    private final OpData storage;
    
    private Password password = Password.NO_PASSWORD;
    private @NullOr Instant lastOpListSizeWarning = null;
    
    OpVerifier(OpGuard opguard)
    {
        this.opguard = Objects.requireNonNull(opguard, "opguard");
        this.storage = new OpData();
        
        FileConfiguration data = storage.yaml();
        @NullOr String hash = data.getString("hash");
        
        if (hash != null)
        {
            this.password = (hash.indexOf('$') >= 0)
                ? Password.Algorithm.BCRYPT.passwordFromHash(hash)
                : Password.Algorithm.SHA_256.passwordFromHash(hash);
        }
        
        if (data.contains("verified"))
        {
            for (String operator : data.getStringList("verified"))
            {
                UUID uuid = UUID.fromString(operator);
                OfflinePlayer player = Bukkit.getOfflinePlayer(uuid);
                verifiedOperators.put(uuid, player);
            }
        }
        
        // Verify op list once per tick
        opguard.server().getScheduler().runTaskTimer(opguard.plugin(), this::verifyOpList, 0L, 1L);
    }
    
    private void verifyOpList()
    {
        Set<OfflinePlayer> operators = opguard.server().getOperators();
        
        if (operators.size() > 75 && Cooldown.of30Minutes().since(lastOpListSizeWarning))
        {
            lastOpListSizeWarning = Instant.now();
            opguard.logger().warning(
                "The op list is large: there are currently " + operators.size() + " operators on this server. " +
                "Consider deopping any unnecessary operators, since OpGuard continuously checks the list. " +
                "There may be a performance impact if the op list continues to grow."
            );
        }
        
        for (OfflinePlayer operator : operators)
        {
            if (!isVerified(operator.getUniqueId()))
            {
                @NullOr String name = operator.getName();
                operator.setOp(false);
                Context context = new Context(opguard).pluginAttempt().setOp().warning(
                    "An unknown plugin attempted to op <!>" + name + "&f. A recently-installed plugin may be to blame"
                );
                opguard.warn(context).log(context).punish(context, name);
            }
        }
    }
    
    Password password() { return password; }
    
    boolean hasPassword() { return password.algorithm() != Password.Algorithm.NONE; }
    
    Password.Algorithm algorithm() { return (hasPassword()) ? password.algorithm() : Password.Algorithm.BCRYPT; }
    
    void updatePassword(String plainTextPassword)
    {
        if (hasPassword())
        {
            throw new IllegalStateException("Password already exists: remove current password first");
        }
        
        this.password = algorithm().passwordFromPlainText(plainTextPassword);
        save();
    }
    
    boolean removePassword(String plainTextPassword)
    {
        if (isPassword(plainTextPassword))
        {
            this.password = Password.NO_PASSWORD;
            return save();
        }
        return false;
    }
    
    boolean isPassword(String plainTextPassword)
    {
        return password.equalsPlainText(plainTextPassword);
    }
    
    Collection<OfflinePlayer> getVerifiedOperators()
    {
        return Collections.unmodifiableCollection(verifiedOperators.values());
    }
    
    boolean op(OfflinePlayer player, String plainTextPassword)
    {
        if (isPassword(plainTextPassword))
        {
            verifiedOperators.put(player.getUniqueId(), player);
            player.setOp(true);
            return save();
        }
        return false;
    }
    
    boolean deop(OfflinePlayer player, String plainTextPassword)
    {
        if (isPassword(plainTextPassword))
        {
            verifiedOperators.remove(player.getUniqueId());
            player.setOp(false);
            return save();
        }
        return false;
    }
    
    public boolean isVerified(UUID uuid)
    {
        return verifiedOperators.containsKey(uuid);
    }
    
    public boolean isVerified(CommandSender sender)
    {
        if (sender instanceof ConsoleCommandSender)
        {
            return true;
        }
        else if (sender instanceof Player)
        {
            Player player = (Player) sender;
            return isVerified(player.getUniqueId());
        }
        return false;
    }
    
    boolean save()
    {
        return save(true);
    }
    
    boolean save(boolean async)
    {
        storage.reset();
        storage.save(async);
        return true;
    }
    
    private final class OpData extends WrappedConfig
    {
        public OpData()
        {
            super(opguard.plugin(), ".opdata");
            
            if (!Files.isRegularFile(path()))
            {
                try { Files.createFile(path()); }
                catch (IOException io) { io.printStackTrace(); }
                
                FileConfiguration old = plugin().getConfig();
                Context context = new Context(opguard);
                
                if (old.contains("verified") || old.contains("password.hash"))
                {
                    // Transferring old data
                    yaml().set("hash", old.getString("password.hash")); // "old" hash can potentially be null, which is okay.
                    yaml().set("verified", (old.contains("verified")) ? old.getStringList("verified") : null);
                    context.okay("Migrating old data to OpGuard's new data storage format...");
                }
                else
                {
                    // Fresh install: no old data to transfer
                    yaml().set("verified", uuidStringList(Bukkit.getOperators()));
                    context.okay("Loading for the first time... Adding all existing operators to the verified list");
                }
                opguard.warn(context).log(context);
                save(false); // Saving the new data file; must be in sync to properly save inside OpGuard's onEnable() method.
            }
        }
        
        // Don't ever reload...
        @Override
        public void reload() { }
        
        public void save(boolean async)
        {
            BukkitRunnable task = new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    try { yaml().save(path().toFile()); }
                    catch (IOException io) { io.printStackTrace(); }
                }
            };
            
            if (async) { task.runTaskAsynchronously(plugin()); }
            else { task.run(); }
        }
        
        private void reset()
        {
            OpVerifier verifier = OpVerifier.this;
            yaml().set("hash", (verifier.hasPassword()) ? verifier.password().hash() : null);
            yaml().set("verified", uuidStringList(verifier.getVerifiedOperators()));
        }
        
        private List<String> uuidStringList(Collection<OfflinePlayer> offline)
        {
            return offline.stream()
                .map(OfflinePlayer::getUniqueId)
                .map(String::valueOf)
                .collect(Collectors.toList());
        }
    }
}
