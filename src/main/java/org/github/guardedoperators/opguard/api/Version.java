package org.github.guardedoperators.opguard.api;

public class Version implements Comparable<Version>
{
    public static Version of(String value)
    {
        Version version = new Version(0, 0, 0);
        
        if (value != null && !value.isEmpty())
        {
            try
            {
                setup(version, value);
            }
            catch (Exception e) {}
        }
        
        return version;
    }
    
    public static Version strict(String value) throws ArrayIndexOutOfBoundsException, NumberFormatException
    {
        Version version = new Version(0, 0, 0);
        setup(version, value);
        return version;
    }
    
    private static void setup(Version instance, String version) throws ArrayIndexOutOfBoundsException, NumberFormatException
    {
        String[] parts = version.split("\\.");
        
        instance.major = Integer.valueOf(parts[0]);
        instance.minor = Integer.valueOf(parts[1]);
        instance.patch = Integer.valueOf(parts[2]);
    }
    
    private int major;
    private int minor;
    private int patch;
    
    public Version(int major, int minor, int patch)
    {
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public int getMajor()
    {
        return this.major;
    }
    
    public int getMinor()
    {
        return this.minor;
    }
    
    public int getPatch()
    {
        return this.patch;
    }
    
    public boolean isAtLeast(int major)
    {
        return this.major >= major;
    }
    
    public boolean isAtLeast(int major, int minor)
    {
        if (this.major == major)
        {
            return this.minor >= minor;
        }
        return isAtLeast(major);
    }
    
    public boolean isAtLeast(int major, int minor, int patch)
    {
        if (this.major == major && this.minor == minor)
        {
            return this.patch >= patch;
        }
        return isAtLeast(major, minor);
    }
    
    public boolean isAtLeast(Version version)
    {
        return isAtLeast(version.major, version.minor, version.patch);
    }
    
    public boolean isAtMost(int major)
    {
        return this.major <= major;
    }
    
    public boolean isAtMost(int major, int minor)
    {
        if (this.major == major)
        {
            return this.minor <= minor;
        }
        return isAtMost(major);
    }
    
    public boolean isAtMost(int major, int minor, int patch)
    {
        if (this.major == major && this.minor == minor)
        {
            return this.patch <= patch;
        }
        return isAtMost(major, minor);
    }
    
    public boolean isAtMost(Version version)
    {
        return isAtMost(version.major, version.minor, version.patch);
    }
    
    @Override
    public boolean compare(Version version)
    {
        return this.major == version.major && this.minor == version.minor && this.patch == version.patch;
    }
    
    @Override
    public boolean equals(Object object)
    {
        return object instanceof Version && compare((Version) object);
    }
    
    @Override
    public String toString()
    {
        return major + "." + minor + "." + patch;
    }
}
