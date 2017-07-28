package com.rezzedup.opguard;

import com.rezzedup.opguard.util.Version;
import org.junit.Test;

import static org.junit.Assert.*;

public class VersionTest
{
    @Test
    public void testVersionEquality()
    {
        assertEquals(Version.of(2, 4, 1), Version.from("2.4.1"));
        assertEquals(2, Version.of(1, 2, 4).getMinor());
    }
    
    public void testVersionComparisons()
    {
        assertTrue(Version.from("1.0.0").isAtLeast(1));
        assertTrue(Version.of(1, 0, 1).isAtLeast(1));
        assertFalse(Version.from("0.9.9").isAtLeast(1));
        assertTrue(Version.of(9).isAtMost(9, 0, 1));
    }
}
