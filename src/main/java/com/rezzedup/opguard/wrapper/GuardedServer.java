package com.rezzedup.opguard.wrapper;

import org.bukkit.Server;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class GuardedServer extends WrappedServer
{
    public GuardedServer(Server implementation)
    {
        super(implementation);
    }
    
    @Override
    public List<Player> getOnlinePlayers()
    {
        List<Player> wrapped = new ArrayList<>();
        
        for (Player player : server.getOnlinePlayers())
        {
            wrapped.add(new GuardedPlayer(player));
        }
        
        return wrapped;
    }
    
    @Override
    public Player[] _INVALID_getOnlinePlayers()
    {
        List<Player> players = this.getOnlinePlayers();
        return players.toArray(new Player[players.size()]);
    }
    
    @Override 
    public Player getPlayer(String name)
    {
        return new GuardedPlayer(server.getPlayer(name));
    }
    
    @Override
    public Player getPlayerExact(String name)
    {
        return new GuardedPlayer(server.getPlayerExact(name));
    }
    
    @Override
    public Player getPlayer(UUID uuid)
    {
        return new GuardedPlayer(server.getPlayer(uuid));
    }
}
