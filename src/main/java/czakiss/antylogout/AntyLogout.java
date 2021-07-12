package czakiss.antylogout;

import czakiss.antylogout.listener.Events;
import czakiss.antylogout.listener.Timer;
import czakiss.antylogout.model.ConfigText;
import czakiss.antylogout.model.DamagedBossBar;
import czakiss.antylogout.model.RandomColor;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

public final class AntyLogout extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        new ConfigText(this);
        new Timer(this);
        getServer().getPluginManager().registerEvents(new Events(), this);
        this.getCommand("antylogout").setExecutor(new czakiss.antylogout.commands.AntyLogout());
        DamagedBossBar.removeAllBossBars();
        getLogger().info(ChatColor.translateAlternateColorCodes('&',"&6[&c&lAnty&f&lLogout&6] &aCreated by &l&"+ RandomColor.getRandom()  + "Czakiss"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&',"&6[&c&lAnty&f&lLogout&6] &aSuccessful loaded"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        DamagedBossBar.removeAllBossBars();

    }
}
