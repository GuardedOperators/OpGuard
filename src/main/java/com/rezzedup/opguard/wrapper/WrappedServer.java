package com.rezzedup.opguard.wrapper;

import com.avaje.ebean.config.ServerConfig;
import org.bukkit.BanList;
import org.bukkit.GameMode;
import org.bukkit.OfflinePlayer;
import org.bukkit.Server;
import org.bukkit.UnsafeValues;
import org.bukkit.Warning;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarFlag;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.generator.ChunkGenerator;
import org.bukkit.help.HelpMap;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFactory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Recipe;
import org.bukkit.map.MapView;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.util.CachedServerIcon;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

@SuppressWarnings("deprecation")
public class WrappedServer implements Server
{
    protected final Server server;
    
    public WrappedServer(Server implementation)
    {
        server = implementation;
    }
    
    public Server getImplementation()
    {
        return server;
    }
    
    @Override
    public String getName()
    {
        return server.getName();
    }
    
    @Override
    public String getVersion()
    {
        return server.getVersion();
    }
    
    @Override
    public String getBukkitVersion()
    {
        return server.getBukkitVersion();
    }
    
    @Override
    public Player[] _INVALID_getOnlinePlayers()
    {
        return server._INVALID_getOnlinePlayers();
    }
    
    @Override
    public Collection<? extends Player> getOnlinePlayers()
    {
        return server.getOnlinePlayers();
    }
    
    @Override
    public int getMaxPlayers()
    {
        return server.getMaxPlayers();
    }
    
    @Override
    public int getPort()
    {
        return server.getPort();
    }
    
    @Override
    public int getViewDistance()
    {
        return server.getViewDistance();
    }
    
    @Override
    public String getIp()
    {
        return server.getIp();
    }
    
    @Override
    public String getServerName()
    {
        return server.getServerName();
    }
    
    @Override
    public String getServerId()
    {
        return server.getServerId();
    }
    
    @Override
    public String getWorldType()
    {
        return server.getWorldType();
    }
    
    @Override
    public boolean getGenerateStructures()
    {
        return server.getGenerateStructures();
    }
    
    @Override
    public boolean getAllowEnd()
    {
        return server.getAllowEnd();
    }
    
    @Override
    public boolean getAllowNether()
    {
        return server.getAllowNether();
    }
    
    @Override
    public boolean hasWhitelist()
    {
        return server.hasWhitelist();
    }
    
    @Override
    public void setWhitelist(boolean b)
    {
        server.setWhitelist(b);
    }
    
    @Override
    public Set<OfflinePlayer> getWhitelistedPlayers()
    {
        return server.getWhitelistedPlayers();
    }
    
    @Override
    public void reloadWhitelist()
    {
        server.reloadWhitelist();
    }
    
    @Override
    public int broadcastMessage(String s)
    {
        return server.broadcastMessage(s);
    }
    
    @Override
    public String getUpdateFolder()
    {
        return server.getUpdateFolder();
    }
    
    @Override
    public File getUpdateFolderFile()
    {
        return server.getUpdateFolderFile();
    }
    
    @Override
    public long getConnectionThrottle()
    {
        return server.getConnectionThrottle();
    }
    
    @Override
    public int getTicksPerAnimalSpawns()
    {
        return server.getTicksPerAnimalSpawns();
    }
    
    @Override
    public int getTicksPerMonsterSpawns()
    {
        return server.getTicksPerMonsterSpawns();
    }
    
    @Override
    public Player getPlayer(String s)
    {
        return server.getPlayer(s);
    }
    
    @Override
    public Player getPlayerExact(String s)
    {
        return server.getPlayerExact(s);
    }
    
    @Override
    public List<Player> matchPlayer(String s)
    {
        return server.matchPlayer(s);
    }
    
    @Override
    public Player getPlayer(UUID uuid)
    {
        return server.getPlayer(uuid);
    }
    
    @Override
    public PluginManager getPluginManager()
    {
        return server.getPluginManager();
    }
    
    @Override
    public BukkitScheduler getScheduler()
    {
        return server.getScheduler();
    }
    
    @Override
    public ServicesManager getServicesManager()
    {
        return server.getServicesManager();
    }
    
    @Override
    public List<World> getWorlds()
    {
        return server.getWorlds();
    }
    
    @Override
    public World createWorld(WorldCreator worldCreator)
    {
        return server.createWorld(worldCreator);
    }
    
    @Override
    public boolean unloadWorld(String s, boolean b)
    {
        return server.unloadWorld(s, b);
    }
    
    @Override
    public boolean unloadWorld(World world, boolean b)
    {
        return server.unloadWorld(world, b);
    }
    
    @Override
    public World getWorld(String s)
    {
        return server.getWorld(s);
    }
    
    @Override
    public World getWorld(UUID uuid)
    {
        return server.getWorld(uuid);
    }
    
    @Override
    public MapView getMap(short i)
    {
        return server.getMap(i);
    }
    
    @Override
    public MapView createMap(World world)
    {
        return server.createMap(world);
    }
    
    @Override
    public void reload()
    {
        server.reload();
    }
    
    @Override
    public Logger getLogger()
    {
        return server.getLogger();
    }
    
    @Override
    public PluginCommand getPluginCommand(String s)
    {
        return server.getPluginCommand(s);
    }
    
    @Override
    public void savePlayers()
    {
        server.savePlayers();
    }
    
    @Override
    public boolean dispatchCommand(CommandSender commandSender, String s) throws CommandException
    {
        return server.dispatchCommand(commandSender, s);
    }
    
    @Override
    public void configureDbConfig(ServerConfig serverConfig)
    {
        server.configureDbConfig(serverConfig);
    }
    
    @Override
    public boolean addRecipe(Recipe recipe)
    {
        return server.addRecipe(recipe);
    }
    
    @Override
    public List<Recipe> getRecipesFor(ItemStack itemStack)
    {
        return server.getRecipesFor(itemStack);
    }
    
    @Override
    public Iterator<Recipe> recipeIterator()
    {
        return server.recipeIterator();
    }
    
    @Override
    public void clearRecipes()
    {
        server.clearRecipes();
    }
    
    @Override
    public void resetRecipes()
    {
        server.resetRecipes();
    }
    
    @Override
    public Map<String, String[]> getCommandAliases()
    {
        return server.getCommandAliases();
    }
    
    @Override
    public int getSpawnRadius()
    {
        return server.getSpawnRadius();
    }
    
    @Override
    public void setSpawnRadius(int i)
    {
        server.setSpawnRadius(i);
    }
    
    @Override
    public boolean getOnlineMode()
    {
        return server.getOnlineMode();
    }
    
    @Override
    public boolean getAllowFlight()
    {
        return server.getAllowFlight();
    }
    
    @Override
    public boolean isHardcore()
    {
        return server.isHardcore();
    }
    
    @Override
    public boolean useExactLoginLocation()
    {
        return server.useExactLoginLocation();
    }
    
    @Override
    public void shutdown()
    {
        server.shutdown();
    }
    
    @Override
    public int broadcast(String s, String s1)
    {
        return server.broadcast(s, s1);
    }
    
    @Override
    public OfflinePlayer getOfflinePlayer(String s)
    {
        return server.getOfflinePlayer(s);
    }
    
    @Override
    public OfflinePlayer getOfflinePlayer(UUID uuid)
    {
        return server.getOfflinePlayer(uuid);
    }
    
    @Override
    public Set<String> getIPBans()
    {
        return server.getIPBans();
    }
    
    @Override
    public void banIP(String s)
    {
        server.banIP(s);
    }
    
    @Override
    public void unbanIP(String s)
    {
        server.unbanIP(s);
    }
    
    @Override
    public Set<OfflinePlayer> getBannedPlayers()
    {
        return server.getBannedPlayers();
    }
    
    @Override
    public BanList getBanList(BanList.Type type)
    {
        return server.getBanList(type);
    }
    
    @Override
    public Set<OfflinePlayer> getOperators()
    {
        return server.getOperators();
    }
    
    @Override
    public GameMode getDefaultGameMode()
    {
        return server.getDefaultGameMode();
    }
    
    @Override
    public void setDefaultGameMode(GameMode gameMode)
    {
        server.setDefaultGameMode(gameMode);
    }
    
    @Override
    public ConsoleCommandSender getConsoleSender()
    {
        return server.getConsoleSender();
    }
    
    @Override
    public File getWorldContainer()
    {
        return server.getWorldContainer();
    }
    
    @Override
    public OfflinePlayer[] getOfflinePlayers()
    {
        return server.getOfflinePlayers();
    }
    
    @Override
    public Messenger getMessenger()
    {
        return server.getMessenger();
    }
    
    @Override
    public HelpMap getHelpMap()
    {
        return server.getHelpMap();
    }
    
    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, InventoryType inventoryType)
    {
        return server.createInventory(inventoryHolder, inventoryType);
    }
    
    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, InventoryType inventoryType, String s)
    {
        return server.createInventory(inventoryHolder, inventoryType, s);
    }
    
    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, int i) throws IllegalArgumentException
    {
        return server.createInventory(inventoryHolder, i);
    }
    
    @Override
    public Inventory createInventory(InventoryHolder inventoryHolder, int i, String s) throws IllegalArgumentException
    {
        return server.createInventory(inventoryHolder, i, s);
    }
    
    @Override
    public int getMonsterSpawnLimit()
    {
        return server.getMonsterSpawnLimit();
    }
    
    @Override
    public int getAnimalSpawnLimit()
    {
        return server.getAnimalSpawnLimit();
    }
    
    @Override
    public int getWaterAnimalSpawnLimit()
    {
        return server.getWaterAnimalSpawnLimit();
    }
    
    @Override
    public int getAmbientSpawnLimit()
    {
        return server.getAmbientSpawnLimit();
    }
    
    @Override
    public boolean isPrimaryThread()
    {
        return server.isPrimaryThread();
    }
    
    @Override
    public String getMotd()
    {
        return server.getMotd();
    }
    
    @Override
    public String getShutdownMessage()
    {
        return server.getShutdownMessage();
    }
    
    @Override
    public Warning.WarningState getWarningState()
    {
        return server.getWarningState();
    }
    
    @Override
    public ItemFactory getItemFactory()
    {
        return server.getItemFactory();
    }
    
    @Override
    public ScoreboardManager getScoreboardManager()
    {
        return server.getScoreboardManager();
    }
    
    @Override
    public CachedServerIcon getServerIcon()
    {
        return server.getServerIcon();
    }
    
    @Override
    public CachedServerIcon loadServerIcon(File file) throws IllegalArgumentException, Exception
    {
        return server.loadServerIcon(file);
    }
    
    @Override
    public CachedServerIcon loadServerIcon(BufferedImage bufferedImage) throws IllegalArgumentException, Exception
    {
        return server.loadServerIcon(bufferedImage);
    }
    
    @Override
    public void setIdleTimeout(int i)
    {
        server.setIdleTimeout(i);
    }
    
    @Override
    public int getIdleTimeout()
    {
        return server.getIdleTimeout();
    }
    
    @Override
    public ChunkGenerator.ChunkData createChunkData(World world)
    {
        return server.createChunkData(world);
    }
    
    @Override
    public BossBar createBossBar(String s, BarColor barColor, BarStyle barStyle, BarFlag... barFlags)
    {
        return server.createBossBar(s, barColor, barStyle, barFlags);
    }
    
    @Override
    public UnsafeValues getUnsafe()
    {
        return server.getUnsafe();
    }
    
    @Override
    public Spigot spigot()
    {
        return server.spigot();
    }
    
    @Override
    public void sendPluginMessage(Plugin plugin, String s, byte[] bytes)
    {
        server.sendPluginMessage(plugin, s, bytes);
    }
    
    @Override
    public Set<String> getListeningPluginChannels()
    {
        return server.getListeningPluginChannels();
    }
}
