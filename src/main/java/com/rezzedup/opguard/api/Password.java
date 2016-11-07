package com.rezzedup.opguard.api;

public interface Password extends Comparable<Password>
{
    String getHash();
}
