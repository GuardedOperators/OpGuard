package com.github.guardedoperators.opguard.api;

public interface Password extends Comparable<Password>
{
    String getHash();
}
