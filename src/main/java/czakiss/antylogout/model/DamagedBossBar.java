package czakiss.antylogout.model;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.NamespacedKey;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.boss.KeyedBossBar;
import org.bukkit.entity.Player;


import java.util.*;

public class DamagedBossBar {
    private static final String KEY_BOSS = "antylogout";
    private static final Random rand = new Random();

    private static final BarColor[] barColors = {BarColor.BLUE,BarColor.GREEN,BarColor.PINK,BarColor.PURPLE,BarColor.RED,BarColor.YELLOW};

//    public static BarColor getRandomBarColor(){
//        return barColors[rand.nextInt(barColors.length)];
//    }
    public static BarColor getRandomBarColor(){
        return BarColor.RED;
    }


    public static BossBar getBossBar(UUID uuid){
        BossBar bossBar =  Bukkit.createBossBar(
                NamespacedKey.minecraft(KEY_BOSS+uuid),
                getTitle(ConfigText.SECONDS_COOLDOWN),
                getRandomBarColor(),
                BarStyle.SOLID);
        bossBar.setProgress(1);
        bossBar.setVisible(true);
        bossBar.addPlayer(Bukkit.getPlayer(uuid));
        return bossBar;
    }

    public static void removeAllBossBars(){
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        ArrayList<KeyedBossBar> keyedBossBars = new ArrayList<>();
        while (bossBars.hasNext()) {
            keyedBossBars.add(bossBars.next());
        }
        for (KeyedBossBar keyedBossBar : keyedBossBars) {
            NamespacedKey namespacedKey = keyedBossBar.getKey();
            if (namespacedKey.getKey().contains(KEY_BOSS)){
                Bukkit.getBossBar(namespacedKey).removeAll();
                Bukkit.removeBossBar(namespacedKey);
            }
        }
    }

    public static void removeBossBar(UUID uuid){
        Iterator<KeyedBossBar> bossBars = Bukkit.getBossBars();
        ArrayList<KeyedBossBar> keyedBossBars = new ArrayList<>();
        while (bossBars.hasNext()) {
            keyedBossBars.add(bossBars.next());
        }
        for (KeyedBossBar keyedBossBar : keyedBossBars) {
            NamespacedKey namespacedKey = keyedBossBar.getKey();
            if (namespacedKey.getKey().contains(KEY_BOSS+uuid)){
                Bukkit.getBossBar(namespacedKey).removeAll();
                Bukkit.removeBossBar(namespacedKey);
            }
        }
    }

    public static String getTitle(long cooldown){
        return ChatColor.translateAlternateColorCodes('&',ConfigText.STATUS_BAR.replace("[SECONDS]",String.valueOf(cooldown)));
    }
}
