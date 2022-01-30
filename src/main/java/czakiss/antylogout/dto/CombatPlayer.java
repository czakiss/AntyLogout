package czakiss.antylogout.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

import java.util.Date;

@Builder
@Getter
@Setter
public class CombatPlayer {
    private Player player;
    private BossBar bossBar;
    private Date date;
    private Boolean isFlying;
}
