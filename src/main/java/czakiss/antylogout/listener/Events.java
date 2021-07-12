package czakiss.antylogout.listener;

import czakiss.antylogout.model.*;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Events implements Listener {

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        UUID uuid = p.getUniqueId();
        DamagedPlayer damagedPlayer = DamagedPlayers.getDamagedPlayerByPlayer(uuid);
        if(damagedPlayer != null){
            if(damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.LOGOUT) {
                p.sendTitle(
                        ChatColor.translateAlternateColorCodes('&', ConfigText.MESSAGE_TITLE),
                        ChatColor.translateAlternateColorCodes('&', ConfigText.MESSAGE_TITLE_SUB),
                        20, 80, 20);
                DamagedBossBar.removeBossBar(p.getUniqueId());
                DamagedPlayers.removePlayer(p.getUniqueId());
            }
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e){
        Player p = e.getPlayer();
        Location loc = p.getLocation();
        DamagedPlayer damagedPlayer = DamagedPlayers.getDamagedPlayerByPlayer(p.getUniqueId());
        if (damagedPlayer != null){
            long diff = TimeUnit.MILLISECONDS.toSeconds(new Date().getTime() - damagedPlayer.getDate().getTime());
            if (diff < ConfigText.SECONDS_COOLDOWN){
                DamagedPlayers.playerList.get(DamagedPlayers.playerList.indexOf(damagedPlayer))
                        .setDamagedPlayerStatus(DamagedPlayerStatus.LOGOUT);
                p.setHealth(0);
                Bukkit.broadcastMessage( ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BROADCAST.replace("[PLAYER]",p.getName())));
                List<Entity> nearby = p.getNearbyEntities(15, 15, 15);
                for(Entity near : nearby) {
                    near.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 14.0F, 1.4F);
                    BlockData fallingDustData = Material.REDSTONE_BLOCK.createBlockData();
                    p.getWorld().spawnParticle(Particle.BLOCK_DUST, p.getLocation(), (int) Math.ceil(2*32), 0.5, 0.5, 0.5, 0.0D, fallingDustData, true);
                    for(int i = 0; i <360; i+=10) {
                        loc.setZ(loc.getZ() + Math.cos(i) * 3);
                        loc.setX(loc.getX() + Math.sin(i) * 3);
                        loc.setY(loc.getY() + Math.sin(i) * 3);
                        near.getWorld().playEffect(loc, Effect.MOBSPAWNER_FLAMES, 1);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if( ((e.getDamager() instanceof Player) || ConfigText.HURT_BY_ENTITY != (e.getDamager() instanceof Player)) && ConfigText.WORLDS.contains(p.getWorld().getName())){

                Date now = new Date();
                if(e.getDamager() instanceof Player){
                    Player attacker = (Player) e.getDamager();
                    setDamage(attacker,now);
                }
                setDamage(p,now);
            }
        }
    }

    public void setDamage(Player p,Date date){
        DamagedPlayer damagedPlayer = DamagedPlayers.getDamagedPlayerByPlayer(p.getUniqueId());
        if (damagedPlayer == null){
            p.sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_ON_DAMAGE.replace("[SECONDS]", ConfigText.SECONDS_COOLDOWN.toString())));
            DamagedPlayers.addPlayer(p.getUniqueId());
        } else {
            DamagedPlayers.playerList.get(DamagedPlayers.playerList.indexOf(damagedPlayer)).setDate(date);
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player player = event.getPlayer();
        String command = event.getMessage();
        List<String> blockedCommands = ConfigText.BLOCKED_COMMANDS;

        for (String blockedComamnd : blockedCommands)
        {
            if(command.equalsIgnoreCase(blockedComamnd))
            {
                event.setCancelled(true);
                event.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BLOCKED_COMMAND));
            }
        }
    }
}
