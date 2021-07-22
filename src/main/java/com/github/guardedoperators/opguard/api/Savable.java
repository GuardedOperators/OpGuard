package com.github.guardedoperators.opguard.api;

public interface Savable
{
    boolean save();
    
    boolean save(boolean async);
}
