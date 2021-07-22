package org.github.guardedoperators.opguard.api;

public interface PasswordHandler
{
    boolean hasPassword();
    
    boolean setPassword(Password password);
    
    Password getPassword();
    
    boolean removePassword(Password password);
    
    boolean check(Password password);
}
