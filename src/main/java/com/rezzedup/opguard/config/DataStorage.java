package com.rezzedup.opguard.config;

import com.rezzedup.opguard.Context;
import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.api.config.SavableConfig;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public final class DataStorage extends BaseConfig implements SavableConfig
{
    private final OpGuardAPI api;
    
    public DataStorage(OpGuardAPI api)
    {
        super(api.getPlugin(), ".opdata");
        this.api = api;
        load(); // Second load() call so that API instance is not null.
    }
    
    @Override
    protected void load()
    {
        // Prevent running without API instance.
        if (api == null)
        {
            return;
        }
        
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
                config.set("verified", getUUIDs(Bukkit.getOperators()));
                context.okay("Loading for the first time... Adding all existing operators to the verified list");
            }
            api.warn(context).log(context);
            save(false); // Saving the new data file; must be in sync to properly save inside OpGuard's onEnable() method.
        }
    }
    
    // Don't ever reload...
    @Override
    public void reload() {}
    
    @Override
    public boolean save()
    {
        return true;
    }
    
    @Override
    public boolean save(boolean async)
    {
        BukkitRunnable task = new BukkitRunnable() 
        {
            @Override
            public void run()
            {
                try
                {
                    config.save(file);
                }
                catch (IOException io)
                {
                    io.printStackTrace();
                }
            }
        };
        
        if (async)
        {
            task.runTaskAsynchronously(plugin);
        }
        else 
        {
            task.run();
        }
        return true;
    }
    
    public void reset(Verifier verifier)
    {
        config.set("hash", (verifier.hasPassword()) ? verifier.getPassword().getHash() : null);
        config.set("verified", getUUIDs(verifier.getVerifiedOperators()));
    }
    
    private List<String> getUUIDs(Collection<OfflinePlayer> from)
    {
        List<String> uuids = new ArrayList<>();
        for (OfflinePlayer operator : from)
        {
            uuids.add(operator.getUniqueId().toString());
        }
        return uuids;
    }
}
