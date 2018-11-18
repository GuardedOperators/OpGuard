package com.rezzedup.opguard;

import com.rezzedup.opguard.api.OpGuardAPI;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

final class VerifyJarTask extends BukkitRunnable {

    private OpGuardAPI api;
    private final File jar;
    private final Long jarSize;
    private final byte[] jarHash;
    private final ConcurrentHashMap<File,Long> watchedFilesSizes;
    private final ConcurrentHashMap<File,byte[]> watchedFilesHash;
    private static boolean updated;

    static {
        updated = false;
    }

    public VerifyJarTask(OpGuardAPI ogAPI)
    {
        watchedFilesSizes = new ConcurrentHashMap<>();
        watchedFilesHash = new ConcurrentHashMap<>();
        api = ogAPI;
        jar = getPluginJar();
        jarSize = jar.length();
        jarHash = checksum(jar);
        reloadFileData();
        long interval = ogAPI.getConfig().getCheckInspectionInterval();

        if (interval <= 0)
        {
            Messenger.send("[OpGuard] Invalid integrity inspection interval " + interval + ". Defaulting to 100 ticks.");
            interval = 100;
        }
        runTaskTimerAsynchronously(api.getPlugin(), 1L, interval);
    }

    @Override
    public void run()
    {
        boolean violation = false;
        if (updated){
            reloadFileData();
            updated = false;
        }
        else if(api.getConfig().useFastCheck()){
            if (api.getConfig().isJarCheckEnabled() &&
                jar.length() != jarSize){
                violation = true;
                warn("jar");
            }else if (api.getConfig().isDataCheckEnabled() &&
                watchedFilesSizes.entrySet().stream().anyMatch(e -> e.getKey().length() != e.getValue())){
                violation = true;
                warn("data/config files");
            }
        }
        else{
            if (api.getConfig().isJarCheckEnabled() &&
                !Arrays.equals(checksum(jar), jarHash)){
                violation = true;
                warn("jar");
            }else if (api.getConfig().isDataCheckEnabled() &&
                watchedFilesHash.entrySet().stream().anyMatch(e ->
                    !Arrays.equals(checksum(e.getKey()), e.getValue()))){
                violation = true;
                warn("data/config files");
            }
        }
        if (violation && api.getConfig().canShutDownOnCheckFail()){
            Messenger.send("&c[&fOpGuard&c] Detected compromised files. Shutting server down.");
            Bukkit.shutdown();
        }
    }

    private void warn(String msg){
        Context context = new Context(api);
        context.overwriteAttempt().warning
            (
                "&cSomething modified OpGuard's &6" + msg +
                    " &cYou should stop the server and check every new plugin!"
            );
        api.warn(context).log(context);
    }

    private File getPluginJar()
    {
        Class clazz = api.getPlugin().getClass();
        return new File(clazz.getProtectionDomain().getCodeSource().getLocation().getFile());
    }

    static void updateGuardData(){
        updated = true;
    }

    private byte[] checksum(File input) {
        try (InputStream in = new FileInputStream(input)) {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] block = new byte[1024];
            int length;
            while ((length = in.read(block)) > 0) {
                digest.update(block, 0, length);
            }
            return digest.digest();
        } catch (Exception e) {
            // happens when the jar, or files deleted
        }
        return null;
    }

    private void reloadFileData() {
        watchedFilesHash.clear();
        watchedFilesSizes.clear();
        for (File f : Objects.requireNonNull(api.getPlugin().getDataFolder().listFiles())) {
            if (!(f.isDirectory() || f.getName().endsWith(".log"))) {
                watchedFilesSizes.put(f,f.length());
                watchedFilesHash.put(f,checksum(f));
            }
        }

    }


}
