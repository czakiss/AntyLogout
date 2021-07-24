package czakiss.antylogout.listener;

import czakiss.antylogout.model.*;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Events implements Listener {

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            UUID uuid = p.getUniqueId();
            DamagedPlayer damagedPlayer = DamagedPlayers.getDamagedPlayerByPlayer(uuid);
            if(damagedPlayer != null){
                DamagedBossBar.removeBossBar(p.getUniqueId());
            }
        }
    }

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
                DamagedBossBar.removeBossBar(p.getUniqueId());
                Bukkit.broadcastMessage( ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BROADCAST.replace("[PLAYER]",p.getName())));
                List<Entity> nearby = p.getNearbyEntities(15, 15, 15);
                for(Entity near : nearby) {
                    near.getWorld().playSound(p.getLocation(), Sound.ENTITY_ZOMBIE_ATTACK_IRON_DOOR, 14.0F, 1.4F);
                    BlockData fallingDustData = Material.REDSTONE_BLOCK.createBlockData();
                    p.getWorld().spawnParticle(Particle.BLOCK_DUST, p.getLocation(), 64, 1, 1, 1, 0.0D, fallingDustData, true);
                    ArrayList<Vector> points = DrawParticleShere.makeSphere(0.5,0.5,0.5,1,false);
                    for(Vector vector:points) {
                        near.getWorld().playEffect(loc.add(vector), Effect.MOBSPAWNER_FLAMES, 1);
                    }
                }
            }
        }
    }



    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(e.isCancelled()){
            return;
        }
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(ConfigText.WORLDS.contains(p.getWorld().getName())){
                Date now = new Date();

                boolean playerAttacker = (e.getDamager() instanceof Player);
                boolean mobAttacker = (e.getDamager() instanceof Monster) && ConfigText.HURT_BY_ENTITY;

                boolean isArrow = (e.getDamager() instanceof Arrow);
                boolean isTrident = (e.getDamager() instanceof Trident);
                boolean isAreaEffectCloud = (e.getDamager() instanceof AreaEffectCloud );
                boolean isThrownPotion = (e.getDamager() instanceof ThrownPotion);
                boolean isTNTPrimed = (e.getDamager() instanceof TNTPrimed );

                boolean battleEntitiesAttacker = isArrow || isTrident || isTNTPrimed || isAreaEffectCloud || isThrownPotion;

                Player attacker = null;
                if(playerAttacker) {
                    attacker = (Player) e.getDamager();
                } else if (isArrow){
                    Arrow a = (Arrow) e.getDamager();
                    if (a.getShooter() instanceof Player){
                        attacker = (Player) a.getShooter();
                    }
                } else if (isTrident){
                    Trident t = (Trident) e.getDamager();
                    if (t.getShooter() instanceof Player){
                        attacker = (Player) t.getShooter();
                    }
                }

                if(attacker != null && !attacker.hasPermission("antylogout.admin")){
                    setDamage(attacker,now);
                }

                if(!p.hasPermission("antylogout.admin") && (mobAttacker || playerAttacker || attacker != null)){
                    setDamage(p,now);
                }
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
        Player p = event.getPlayer();
        DamagedPlayer damagedPlayer = DamagedPlayers.getDamagedPlayerByPlayer(p.getUniqueId());
        if(damagedPlayer != null && damagedPlayer.getDamagedPlayerStatus() == DamagedPlayerStatus.ON_SYSTEM){
            String command = event.getMessage();
            List<String> blockedCommands = ConfigText.BLOCKED_COMMANDS;

            for (String blockedComamnd : blockedCommands)
            {
                if(command.equalsIgnoreCase(blockedComamnd))
                {
                    event.setCancelled(true);
                    p.sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BLOCKED_COMMAND));
                }
            }
        }
    }
}
