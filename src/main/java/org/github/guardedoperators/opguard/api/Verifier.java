package org.github.guardedoperators.opguard.api;

import org.bukkit.OfflinePlayer;
import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.UUID;

public interface Verifier extends PasswordHandler, Savable
{
    Collection<OfflinePlayer> getVerifiedOperators();
    
    Collection<UUID> getVerifiedUUIDs();
    
    boolean op(OfflinePlayer player, Password password);
    
    boolean deop(OfflinePlayer player, Password password);
    
    boolean isVerified(UUID uuid);
    
    boolean isVerified(OfflinePlayer player);
    
    boolean isVerified(CommandSender sender);
}
