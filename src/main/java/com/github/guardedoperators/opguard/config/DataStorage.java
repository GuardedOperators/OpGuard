package com.github.guardedoperators.opguard.config;

import com.github.guardedoperators.opguard.Context;
import com.github.guardedoperators.opguard.OpGuard;
import com.github.guardedoperators.opguard.OpVerifier;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public final class DataStorage extends BaseConfig
{
    private final OpGuard api;
    
    public DataStorage(OpGuard api)
    {
        super(api.plugin(), ".opdata");
        this.api = api;
        init();
    }
    
    @Override
    protected void load() {}
    
    private void init()
    {
        boolean firstLoad = false;
        
        try
        {
            firstLoad = file.createNewFile();
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        
        if (firstLoad)
        {
            FileConfiguration old = plugin.getConfig();
            Context context = new Context(api);
            
            if (old.contains("verified") || old.contains("password.hash"))
            {
                // Transferring old data
                config.set("hash", old.getString("password.hash")); // "old" hash can potentially be null, which is okay.
                config.set("verified", (old.contains("verified")) ? old.getStringList("verified") : null);
                context.okay("Migrating old data to OpGuard's new data storage format...");
            }
            else 
            {
                // Fresh install: no old data to transfer
                config.set("verified", uuidStringList(Bukkit.getOperators()));
                context.okay("Loading for the first time... Adding all existing operators to the verified list");
            }
            api.warn(context).log(context);
            save(false); // Saving the new data file; must be in sync to properly save inside OpGuard's onEnable() method.
        }
    }
    
    // Don't ever reload...
    @Override
    public void reload() {}
    
    public boolean save(boolean async)
    {
        BukkitRunnable task = new BukkitRunnable() 
        {
            @Override
            public void run()
            {
                try { config.save(file); }
                catch (IOException io) { io.printStackTrace(); }
            }
        };
        
        if (async) { task.runTaskAsynchronously(plugin); }
        else { task.run(); }
        return true;
    }
    
    public void reset(OpVerifier verifier)
    {
        config.set("hash", (verifier.hasPassword()) ? verifier.getPassword().getHash() : null);
        config.set("verified", uuidStringList(verifier.getVerifiedOperators()));
    }
    
    private static List<String> uuidStringList(Collection<OfflinePlayer> offline)
    {
        return offline.stream()
            .map(OfflinePlayer::getUniqueId)
            .map(String::valueOf)
            .collect(Collectors.toList());
    }
}
