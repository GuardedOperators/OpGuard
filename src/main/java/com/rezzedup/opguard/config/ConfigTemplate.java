package com.rezzedup.opguard.config;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ConfigTemplate
{
    private final BufferedReader reader;
    
    public ConfigTemplate(BufferedReader reader)
    {
        this.reader = reader;
    }
    
    public ConfigTemplate(InputStreamReader reader)
    {
        this(new BufferedReader(reader));
    }
    
    public ConfigTemplate(InputStream stream)
    {
        this(new InputStreamReader(stream));
    }
    
    public List<String> apply(FileConfiguration config)
    {
        List<String> lines = new ArrayList<>();
        
        try
        {
            lines = applyTemplate(config);
        }
        catch (IOException io)
        {
            io.printStackTrace();
        }
        
        return lines;
    }
    
    private List<String> applyTemplate(FileConfiguration config) throws IOException
    {
        List<String> lines = new ArrayList<>();
        String regex = "@\\((.+)\\)$";
        Pattern replaceable = Pattern.compile(regex);
    
        String line;
        
        while ((line = reader.readLine()) != null)
        {
            String pure = line;
            line = line.replaceAll("#@.*$", "");
            
            if (line.isEmpty())
            {
                if (lines.isEmpty() || !line.equals(pure))
                {
                    continue;
                }
            }
            
            Matcher matcher = replaceable.matcher(line);
            
            if (matcher.find())
            {
                String origin = matcher.group(1);
                line = line.replaceAll(regex, config.getString(origin, "not found"));
            }
            
            lines.add(line);
        }
        
        reader.close();
        
        return lines;
    }
}
