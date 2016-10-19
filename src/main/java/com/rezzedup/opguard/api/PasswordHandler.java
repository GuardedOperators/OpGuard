package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Password;

public interface PasswordHandler
{
    public void setPassword(Password password);
    
    public void removePassword(Password password);
    
    public Password getPassword();
    
    public boolean check(Password password);
}
