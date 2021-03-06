package czakiss.antylogout.model;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Arrays;
import java.util.List;

public class ConfigText {
    public static Integer SECONDS_COOLDOWN;
    public static Integer TICKS;

    public static Boolean HURT_BY_ENTITY;
    public static String STATUS_BAR;
    public static String STATUS_BAR_DONE;
    public static String MESSAGE_ON_DAMAGE;
    public static String MESSAGE_STOPED;
    public static String MESSAGE_ON_JOIN;
    public static String MESSAGE_TITLE;
    public static String MESSAGE_TITLE_SUB;
    public static String MESSAGE_BROADCAST;
    public static List<String> WORLDS;
    public static List<String> ALLOWED_COMMANDS;
    public static String MESSAGE_BLOCKED_COMMAND;
    private static Plugin plugin;
    private static FileConfiguration config;


    public ConfigText(Plugin plugin){
        ConfigText.plugin = plugin;
        ConfigText.config = plugin.getConfig();
        load();
    }

    public static void load(){
        SECONDS_COOLDOWN = config.getInt("seconds_cooldown",15);
        HURT_BY_ENTITY = config.getBoolean("hurt_by_entity",true);
        STATUS_BAR = config.getString("status_bar","&cJestes w walce! Mozesz wylogowac sie za &4[SECONDS] &csekund.");
        STATUS_BAR_DONE = config.getString("status_bar_done","&aMozesz se wylogowac");
        MESSAGE_ON_DAMAGE = config.getString("message_on_damage","&4&l[AntyLogout] &eZostales zaatakowany, nie wychodz przez &c[SECONDS] &esekund");
        MESSAGE_STOPED = config.getString("message_stoped","&4&l[AntyLogout] &aMożesz się wylogowac!");
        MESSAGE_ON_JOIN = config.getString("message_on_join","&4&l[AntyLogout] &eWylogowales się podczas ostatniej walki przez co &czginąles&e!");
        MESSAGE_TITLE = config.getString("message_title","&cZostales zabity");
        MESSAGE_TITLE_SUB = config.getString("message_title_sub","&cPrzez &4&l[AntyLogout]");
        MESSAGE_BROADCAST = config.getString("message_broadcast","&4&l[AntyLogout] &c[PLAYER] &ewylogowal sie podczas ostatniej walki przez co &czginal&e!");
        MESSAGE_BLOCKED_COMMAND = config.getString("message_blocked_command","&4&l[AntyLogout] &cNie mozesz uzywac tej komendy podczas walki!");
        TICKS = config.getInt("ticks",20);
        WORLDS = config.getStringList("worlds");
        ALLOWED_COMMANDS = config.getStringList("allowed_commands");
        if(WORLDS.isEmpty()){
            WORLDS = Arrays.asList("world","world_nether","world_the_end");
        }
        if(ALLOWED_COMMANDS.isEmpty()){
            WORLDS = Arrays.asList("/msg","/r","/enderchest");
        }
    }
}
