package czakiss.antylogout.model;

import lombok.*;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Date;
import java.util.UUID;


@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
public class DamagedPlayer {
    private UUID uuid;
    private Date date;
    private BossBar bossBar;
    private DamagedPlayerStatus damagedPlayerStatus;
}
