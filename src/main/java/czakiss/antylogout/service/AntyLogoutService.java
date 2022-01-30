package czakiss.antylogout.service;

import czakiss.antylogout.AntyLogout;
import czakiss.antylogout.dto.CombatPlayer;
import czakiss.antylogout.model.ConfigText;
import czakiss.antylogout.model.DrawParticleShere;
import org.bukkit.*;
import org.bukkit.block.data.BlockData;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;
import org.bukkit.util.Vector;

import java.util.*;
import java.util.concurrent.TimeUnit;

public class AntyLogoutService implements Listener {
    private final Map<UUID, CombatPlayer> combatPlayerMap = new HashMap<>();
    private final Map<UUID, Player> respawnedPlayer = new HashMap<>();
    public static BukkitTask timer;

    public AntyLogoutService(){
        timer = new BukkitRunnable() {
            @Override
            public void run() {
                Map<UUID,CombatPlayer> iterator = new HashMap<>(combatPlayerMap);
                iterator.forEach((uuid, combatPlayer) -> {

                    long time = getDateDiff(combatPlayer.getDate(),new Date());
                    long timeCooldown = ConfigText.SECONDS_COOLDOWN * 1000;

                    if(time < timeCooldown){
//                        combatPlayer.getPlayer().setFlying(false);
//                        combatPlayer.getPlayer().setAllowFlight(false);

                        combatPlayer.getBossBar().setTitle(
                            ChatColor.translateAlternateColorCodes(
                                    '&',
                                    ConfigText.STATUS_BAR.replace("[SECONDS]", String.valueOf( (int)((timeCooldown - time)/1000)+1))
                            )
                        );
                        combatPlayer.getBossBar().setProgress(1 - (double)time/(double)timeCooldown);
                    } else {
                        combatPlayer.getPlayer().sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_STOPED));
                        removeCombatPlayer(combatPlayer.getPlayer());
                    }
                });
            }
        }.runTaskTimerAsynchronously(AntyLogout.getPlugin(AntyLogout.class), 1L, ConfigText.TICKS);
    }

    public static long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return TimeUnit.MILLISECONDS.convert(diffInMillies, TimeUnit.MILLISECONDS);
    }

    public void setCombatPlayerMap(Player player){
        if(ConfigText.WORLDS.contains(player.getWorld().getName())){
            if(!combatPlayerMap.containsKey(player.getUniqueId())){
                player.sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_ON_DAMAGE.replace("[SECONDS]", ConfigText.SECONDS_COOLDOWN.toString())));
                BossBar bossBar = Bukkit.createBossBar(
//                        NamespacedKey.minecraft(("AntyLogout-" + player.getUniqueId()).toLowerCase()),
                        ChatColor.translateAlternateColorCodes(
                                '&',
                                ConfigText.STATUS_BAR.replace("[SECONDS]", String.valueOf(ConfigText.SECONDS_COOLDOWN))
                        ),
                        BarColor.RED,
                        BarStyle.SOLID
                );
                bossBar.addPlayer(player);
                bossBar.setVisible(true);
                combatPlayerMap.put(
                        player.getUniqueId(),
                        CombatPlayer.
                                builder().
                                player(player).
                                bossBar(bossBar).
                                date(new Date()).
//                                isFlying(player.getAllowFlight()).
                                build()
                );
            } else {
                CombatPlayer combatPlayer = combatPlayerMap.get(player.getUniqueId());
                combatPlayer.getBossBar().setProgress(1);
                combatPlayer.setDate(new Date());
            }

        }

    }

    public CombatPlayer getCombatPlayer(Player player){
        return combatPlayerMap.get(player.getUniqueId());
    }

    public void removeCombatPlayer(Player player){
        if(combatPlayerMap.containsKey(player.getUniqueId())){
            CombatPlayer combatPlayer = combatPlayerMap.get(player.getUniqueId());
            combatPlayer.getBossBar().setVisible(false);
            combatPlayer.getBossBar().removePlayer(player);
//            combatPlayer.getPlayer().setAllowFlight(combatPlayer.getIsFlying());
            new BukkitRunnable(){
                @Override
                public void run() {
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "/bossbar remove "+ ("antylogout-" + player.getUniqueId()).toLowerCase());
                }
            };
            combatPlayerMap.remove(player.getUniqueId());
        }
    }

    @EventHandler
    public void entityDeath(EntityDeathEvent e) {
        if (e.getEntity() instanceof Player) {
            Player p = (Player) e.getEntity();
            removeCombatPlayer(p);
        }
    }

    @EventHandler
    public void onPlayerRespawn(PlayerRespawnEvent e){
        Player p = e.getPlayer();
        if(respawnedPlayer.containsKey(p.getUniqueId())){
            p.sendTitle(
                ChatColor.translateAlternateColorCodes('&', ConfigText.MESSAGE_TITLE),
                ChatColor.translateAlternateColorCodes('&', ConfigText.MESSAGE_TITLE_SUB),
            20, 80, 20);
            respawnedPlayer.remove(p.getUniqueId());
        }
    }

    @EventHandler
    public void onPlayerLogout(PlayerQuitEvent e){
        Player p = e.getPlayer();
        if(combatPlayerMap.containsKey(p.getUniqueId())){
            Bukkit.broadcastMessage( ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BROADCAST.replace("[PLAYER]",p.getName())));

            List<Entity> nearby = p.getNearbyEntities(15, 15, 15);
            for(Entity near : nearby) {
                near.getWorld().playSound(p.getLocation(), Sound.ENTITY_LIGHTNING_BOLT_THUNDER, 14.0F, 1.4F);
                BlockData fallingDustData = Material.REDSTONE_BLOCK.createBlockData();
                p.getWorld().spawnParticle(Particle.BLOCK_DUST, p.getLocation(), 64, 1, 1, 1, 0.0D, fallingDustData, true);
                ArrayList<org.bukkit.util.Vector> points = DrawParticleShere.makeSphere(0.5,0.5,0.5,1,false);
                for(Vector vector:points) {
                    near.getWorld().playEffect(p.getLocation().add(vector), Effect.MOBSPAWNER_FLAMES, 1);
                }
            }
            combatPlayerMap.remove(p.getUniqueId());
            p.setHealth(0);
        }
    }


    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageByEntityEvent e){
        if(e.isCancelled()){
            return;
        }
        if(e.getEntity() instanceof Player){
            Player p = (Player) e.getEntity();
            if(ConfigText.WORLDS.contains(p.getWorld().getName())){

                boolean playerAttacker = (e.getDamager() instanceof Player);
                boolean mobAttacker = (e.getDamager() instanceof Monster) && ConfigText.HURT_BY_ENTITY;

                boolean isArrow = (e.getDamager() instanceof Arrow);
                boolean isTrident = (e.getDamager() instanceof Trident);
                boolean isAreaEffectCloud = (e.getDamager() instanceof AreaEffectCloud );
                boolean isThrownPotion = (e.getDamager() instanceof ThrownPotion);
                boolean isTNTPrimed = (e.getDamager() instanceof TNTPrimed );

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
                    setCombatPlayerMap(attacker);
                }

//
                if(!p.hasPermission("antylogout.admin") && ( mobAttacker || playerAttacker || attacker != null)){
                    setCombatPlayerMap(p);
//                    p.setAllowFlight(false);
//                    p.setFlying(false);
                }
            }
        }
    }


    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPreprocess(PlayerCommandPreprocessEvent event)
    {
        Player p = event.getPlayer();
        if(combatPlayerMap.containsKey(p.getUniqueId())){
            String command = event.getMessage();
            List<String> allowedCommands = ConfigText.ALLOWED_COMMANDS;

            boolean empty = true;
            for (String allowedCommand : allowedCommands)
            {
                if (command.split(" ")[0].equalsIgnoreCase(allowedCommand)) {
                    empty = false;
                    break;
                }
            }
            if(empty){
                event.setCancelled(true);
                p.sendMessage(ChatColor.translateAlternateColorCodes('&',ConfigText.MESSAGE_BLOCKED_COMMAND));
            }
        }
    }
    @EventHandler
    public void move(PlayerMoveEvent e) {

        final Player p = e.getPlayer();

        if (combatPlayerMap.containsKey(p.getUniqueId()) && p.isFlying()) {
            p.setFlying(false);
        }
    }
}
