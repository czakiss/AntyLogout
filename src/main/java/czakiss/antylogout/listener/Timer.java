package czakiss.antylogout.listener;

import czakiss.antylogout.model.*;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Timer {
    public Timer(Plugin plugin) {
        new BukkitRunnable() {
            @Override
            public void run() {
            }
        }.runTaskTimerAsynchronously(plugin, ConfigText.TICKS, ConfigText.TICKS);
    }
}
