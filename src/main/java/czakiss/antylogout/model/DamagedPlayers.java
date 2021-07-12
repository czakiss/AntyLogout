package czakiss.antylogout.model;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class DamagedPlayers {
    public static List<DamagedPlayer> playerList = new ArrayList<>();

    public static void addPlayer(UUID uuid){
        playerList.add(new DamagedPlayer(
                uuid,
                new Date(),
                DamagedBossBar.getBossBar(uuid),
                DamagedPlayerStatus.ON_SYSTEM
        ));
    }

    public static DamagedPlayer getDamagedPlayerByPlayer(UUID uuid){
        return playerList.stream().filter(damagedPlayer -> damagedPlayer.getUuid() == uuid).findFirst().orElse(null);
    }

    public static void removePlayer(UUID uuid){
        playerList.remove(getDamagedPlayerByPlayer(uuid));
    }
}
