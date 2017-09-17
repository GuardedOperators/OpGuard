package com.rezzedup.opguard.users;

import com.google.gson.Gson;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class UserData
{
    private final Map<UUID, User> data = new HashMap<>();
    private final Map<User, Path> files = new HashMap<>();
    
    private final Gson gson;
    private final Path directory;
    
    public UserData(Gson gson, Path root) throws IOException
    {
        this.gson = gson;
        this.directory = Paths.get(root.toString(), "history");
        
        if (!Files.isDirectory(directory))
        {
            Files.createDirectory(directory);
        }
        
        Files.list(root).forEach(this::loadUserData);
    }
    
    private void loadUserData(Path path)
    {
        if (!Files.isRegularFile(path) || !Files.isReadable(path)) { return; }
        
        try
        {
            User user = gson.fromJson(String.join("", Files.readAllLines(path)), User.class);
            
            data.put(user.getUuid(), user);
            files.put(user, path);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
    }
    
    
}
