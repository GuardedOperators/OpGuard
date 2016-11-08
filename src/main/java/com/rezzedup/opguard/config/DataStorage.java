package com.rezzedup.opguard.config;

import com.rezzedup.opguard.api.OpGuardAPI;
import com.rezzedup.opguard.api.Verifier;
import com.rezzedup.opguard.api.config.SavableConfig;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public final class DataStorage extends BaseConfig implements SavableConfig
{
    private final OpGuardAPI api;
    
    public DataStorage(OpGuardAPI api)
    {
        super(api.getPlugin(), ".opdata");
        this.api = api;
    }
    
    @Override
    protected void load()
    {
        if (!file.exists())
        {
            try
            {
                file.createNewFile();
            }
            catch (IOException io)
            {
                io.printStackTrace();
            }
        }
        
        FileConfiguration old = plugin.getConfig();
        
        if ((!config.contains("verified") || !config.contains("hash")) && (old.contains("verified") || old.contains("password.hash")))
        {
            config.set("hash", old.getString("password.hash"));
            config.set("verified", (old.contains("verified")) ? old.getStringList("verified") : null);
            save(false);
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
        
        List<String> uuids = new ArrayList<>();
        for (UUID uuid : verifier.getVerifiedUUIDs())
        {
            uuids.add(uuid.toString());
        }
        config.set("verified", uuids);
    }
}
