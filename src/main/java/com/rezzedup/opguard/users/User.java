package com.rezzedup.opguard.users;

import java.util.UUID;

public class User
{
    private final UUID uuid;
    private final String name;
    
    public User(UUID uuid, String name)
    {
        this.uuid = uuid;
        this.name = name;
    }
    
    public UUID getUuid()
    {
        return uuid;
    }
    
    public String getName()
    {
        return name;
    }
    
    
}
