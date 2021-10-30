package czakiss.antylogout.listener;

import czakiss.antylogout.model.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BossBar;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
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
                    if (damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.ON_SYSTEM ||
                            damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.DONE) {
                        long timeDifference = TimeUnit.MILLISECONDS.toSeconds(
                                new Date().getTime() - damagedPlayer.getDate().getTime()
                        );
                        long cooldown = ConfigText.SECONDS_COOLDOWN - timeDifference;
                        Player p = Bukkit.getPlayer(damagedPlayer.getUuid());
                        BossBar bossBar = damagedPlayer.getBossBar();
                        if (cooldown > 0) {
                            bossBar.setTitle(DamagedBossBar.getTitle(cooldown));
                            bossBar.setProgress(cooldown / (double) ConfigText.SECONDS_COOLDOWN);
                            bossBar.setColor(BarColor.RED);

                            if(p != null && !bossBar.getPlayers().contains(p)){
                                bossBar.addPlayer(p);
                            }
                            bossBar.setVisible(true);
                        } else if(cooldown == 0) {
                            bossBar.setTitle(ChatColor.translateAlternateColorCodes('&',ConfigText.STATUS_BAR_DONE));
                            bossBar.setProgress(1);
                            bossBar.setColor(BarColor.GREEN);
                            damagedPlayer.setDamagedPlayerStatus(DamagedPlayerStatus.DONE);
                            if(p != null && !bossBar.getPlayers().contains(p)){
                                bossBar.addPlayer(p);
                            }
                            bossBar.setVisible(true);

                        } else if (cooldown < -3 && damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.DONE){
                            bossBar.setVisible(false);
                            DamagedBossBar.removeBossBar(damagedPlayer.getUuid());
                            DamagedPlayers.playerList.remove(damagedPlayer);

                        }
                    }
                }
            }
        }.runTaskTimerAsynchronously(plugin, ConfigText.TICKS, ConfigText.TICKS);
    }
}
