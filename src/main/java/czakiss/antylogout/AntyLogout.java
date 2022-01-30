package czakiss.antylogout;

import czakiss.antylogout.model.ConfigText;
import czakiss.antylogout.model.RandomColor;
import czakiss.antylogout.service.AntyLogoutService;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Iterator;

public final class AntyLogout extends JavaPlugin {

    private FileConfiguration config;

    @Override
    public void onEnable() {
        this.saveDefaultConfig();
        new ConfigText(this);
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        while(bossBars.hasNext()){
            KeyedBossBar keyedBossBar = bossBars.next();
            if(keyedBossBar.getKey().getKey().contains("antylogout")){
                keyedBossBar.removeAll();
            }
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/bossbar remove "+keyedBossBar.getKey());
        }
        new AntyLogoutService();
        getServer().getPluginManager().registerEvents(new AntyLogoutService(), this);
        this.getCommand("antylogout").setExecutor(new czakiss.antylogout.commands.AntyLogout());
        getLogger().info(ChatColor.translateAlternateColorCodes('&',"&6[&c&lAnty&f&lLogout&6] &aCreated by &l&"+ RandomColor.getRandom()  + "Czakiss"));
        getLogger().info(ChatColor.translateAlternateColorCodes('&',"&6[&c&lAnty&f&lLogout&6] &aSuccessful loaded"));
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic

    }
}
