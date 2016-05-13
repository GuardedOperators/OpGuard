package space.rezz.opguard;

import org.bukkit.OfflinePlayer;

public class Verify
{
    public static void op(OfflinePlayer player, Password password) throws Exception
    {   
        if (checkPassword(password))
        {
            VerifiedOperators.addOperator(player);
        }
        else
        {
            throw new Exception("&cIncorrect password.");
        }
    }
    
    public static void deop(OfflinePlayer player, Password password) throws Exception
    {
        if (checkPassword(password))
        {
            player.setOp(false);
            OpGuard.warn("status", "&f[&6&lVERIFIED&f] &e" + player.getName() + "&f is no longer op.");
        }
        else
        {
            throw new Exception("&cIncorrect password.");
        }
    }
    
    private static boolean checkPassword(Password password)
    {
        String hash = OpGuard.getInstance().getConfig().getString("password.hash");
        
        if (hash != null)
        {
            return (password.getHash().equals(hash));
        }
        return true;
    }
}
