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
package com.github.guardedoperators.opguard.config;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import pl.tlinkowski.annotation.basic.NullOr;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

class ConfigurationTemplate
{
    private static BufferedReader resourceReader(Class<?> clazz, String resource)
    {
        return new BufferedReader(new InputStreamReader(
            Objects.requireNonNull(
                clazz.getClassLoader().getResourceAsStream(resource),
                "Could not get resource: " + resource
            )
        ));
    }
    
    private static List<String> resourceLines(Class<?> clazz, String resource)
    {
        List<String> template = new ArrayList<>();
        
        try (BufferedReader reader = resourceReader(clazz, resource))
        {
            for (;;)
            {
                @NullOr String line = reader.readLine();
                if (line == null) { break; }
                template.add(line);
            }
        }
        catch (IOException e) { throw new RuntimeException(e); }
        
        return template;
    }
    
    private final List<String> templateLines;
    
    public ConfigurationTemplate(String resource)
    {
        this.templateLines = resourceLines(getClass(), resource);
    }
    
    public List<String> apply(FileConfiguration config)
    {
        try { return applyTemplate(config); }
        catch (IOException e) { throw new RuntimeException(e); }
    }
    
    private static final List<String> CONTROL_CHARACTERS =
        List.of("@", "&", "#", ":", "\n", "\"", "'", "[", "]", "{", "}");
    
    private static String sanitize(String value)
    {
        for (String control : CONTROL_CHARACTERS)
        {
            if (value.contains(control))
            {
                String escaped = value.replace("\"", "\\\"").replace("\n", "\\n");
                return "\"" + escaped + "\"";
            }
        }
        
        return value;
    }
    
    private static List<String> getSanitizedExistingValues(ConfigurationSection config, String ... keys)
    {
        for (String key : keys)
        {
            if (key.endsWith("[]"))
            {
                List<String> existing =
                    config.getStringList(key.substring(0, key.length() - 2)).stream()
                        .map(ConfigurationTemplate::sanitize)
                        .collect(Collectors.toList());
                
                if (!existing.isEmpty()) { return existing; }
            }
            else
            {
                @NullOr String value = config.getString(key);
                if (value != null) { return List.of(sanitize(value)); }
            }
        }
        
        return List.of(sanitize(keys[keys.length - 1])); // default value
    }
    
    private static final Pattern TEMPLATE_COMMENT_PATTERN = Pattern.compile("#@.*$");
    
    private static final Pattern OPTIONS_PATTERN = Pattern.compile("@\\(\\s*(?<options>.+)\\s*\\)\\s*$");
    
    public static final Pattern OPTIONS_DELIMITER_PATTERN = Pattern.compile("\\s*\\|\\s*");
    
    private static final Pattern LIST_HYPHEN_PATTERN = Pattern.compile("^(?<hyphen>\\s*-)\\s");
    
    private List<String> applyTemplate(FileConfiguration config) throws IOException
    {
        List<String> lines = new ArrayList<>();
        
        for (final String templateLine : templateLines)
        {
            final String uncommentedLine = TEMPLATE_COMMENT_PATTERN.matcher(templateLine).replaceAll("");
            
            if (uncommentedLine.isEmpty())
            {
                if (lines.isEmpty()) { continue; }
                if (!templateLine.equals(uncommentedLine)) { continue; }
            }
            
            Matcher optionMatcher = OPTIONS_PATTERN.matcher(uncommentedLine);
            
            if (!optionMatcher.find())
            {
                lines.add(uncommentedLine);
                continue;
            }
            
            List<String> values =
                getSanitizedExistingValues(
                    config,
                    OPTIONS_DELIMITER_PATTERN.split(optionMatcher.group("options"))
                );
            
            Matcher hyphenMatcher = LIST_HYPHEN_PATTERN.matcher(uncommentedLine);
            
            // List
            if (hyphenMatcher.find())
            {
                String hyphen = hyphenMatcher.group("hyphen");
                for (String value : values) { lines.add(hyphen + " " + value); }
            }
            // Single value
            else
            {
                lines.add(optionMatcher.replaceAll(values.get(0)));
            }
        }
        
        return lines;
    }
}
