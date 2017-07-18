package com.rezzedup.opguard.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Version implements Comparable<Version>
{
    public static final Version EMPTY = new Version(Collections.emptyList());
    
    public static Version of(long ... elements)
    {
        if (elements == null || elements.length <= 0)
        {
            return EMPTY;
        }
        
        List<Long> collector = new ArrayList<>();
        
        for (long element : elements)
        {
            collector.add(element);
        }
        
        return new Version(collector);
    }
    
    public static Version from(String version)
    {
        List<Long> results = new ArrayList<>();
        
        try
        {
            parseAndPopulateList(version, results);
        }
        catch (IllegalArgumentException ignored) {}
        
        return (results.size() <= 0) ? EMPTY : new Version(results);
    }
    
    public static Version strictlyFrom(String version) throws NumberFormatException
    {
        List<Long> results = parseAndPopulateList(version, new ArrayList<>());
        return (results.size() <= 0) ? EMPTY : new Version(results);
    }
    
    private static List<Long> parseAndPopulateList(String version, List<Long> list) throws NumberFormatException
    {
        for (String element : version.split("\\."))
        {
            list.add(Long.parseLong(element));
        }
        return list;
    }
    
    private final List<Long> elements;
    
    private Version(List<Long> elements)
    {
        this.elements = Collections.unmodifiableList(elements);
    }
    
    public List<Long> getElements()
    {
        return elements;
    }
    
    public long getElementAt(int index)
    {
        return (index >= elements.size()) ? 0 : elements.get(index);
    }
    
    public long getMajor()
    {
        return getElementAt(0);
    }
    
    public long getMinor()
    {
        return getElementAt(1);
    }
    
    public long getPatch()
    {
        return getElementAt(2);
    }
    
    public boolean isAtLeast(Version other)
    {
        return compareTo(other) >= 0;
    }
    
    public boolean isAtMost(Version other)
    {
        return compareTo(other) <= 0;
    }
    
    @Override
    public int compareTo(Version other)
    {
        if (other == null)
        {
            throw new NullPointerException("Invalid other version: null");
        }
        
        int maximum = Math.max(this.elements.size(), other.elements.size());
        
        for (int index = 0; index < maximum; index++)
        {
            int comparison = Long.compare(this.getElementAt(index), other.getElementAt(index));
            
            if (comparison != 0)
            {
                return comparison;
            }
        }
        
        return 0; // Versions are equal.
    }
    
    @Override
    public boolean equals(Object other)
    {
        return other instanceof Version && compareTo((Version) other) == 0;
    }
    
    @Override
    public int hashCode()
    {
        return elements.hashCode();
    }
}
