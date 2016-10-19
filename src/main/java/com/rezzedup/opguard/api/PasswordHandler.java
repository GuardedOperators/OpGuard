package com.rezzedup.opguard.api;

import com.rezzedup.opguard.Password;

public interface PasswordHandler
{
    public Authenticator generateAuth();
    
    public void setPassword(Authenticator auth, Password password);
    
    public void removePassword(Authenticator auth, Password password);
    
    public Password getPassword(Authenticator auth);
}
