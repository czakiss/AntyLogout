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
                List<DamagedPlayer> damagedPlayers = new ArrayList<>(DamagedPlayers.playerList);
                for (DamagedPlayer damagedPlayer : damagedPlayers) {
                    if (damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.ON_SYSTEM) {

                        long timeDifference = TimeUnit.MILLISECONDS.toSeconds(
                                new Date().getTime() - damagedPlayer.getDate().getTime()
                        );
                        long cooldown = ConfigText.SECONDS_COOLDOWN - timeDifference;

                        BossBar bossBar = damagedPlayer.getBossBar();
                        if (cooldown >= 0) {
                            bossBar.setTitle(DamagedBossBar.getTitle(cooldown));
                            bossBar.setProgress(cooldown / (double) ConfigText.SECONDS_COOLDOWN);
                            bossBar.setColor(DamagedBossBar.getRandomBarColor());
                        } else {
                            DamagedBossBar.removeBossBar(damagedPlayer.getUuid());
                            DamagedPlayers.playerList.remove(damagedPlayer);
                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, ConfigText.TICKS, ConfigText.TICKS);
    }
}
