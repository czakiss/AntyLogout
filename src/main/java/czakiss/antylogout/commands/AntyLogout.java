package czakiss.antylogout.commands;

import czakiss.antylogout.model.RandomColor;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class AntyLogout implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull org.bukkit.command.Command command, @NotNull String label, @NotNull String[] args) {
        sender.sendMessage(ChatColor.translateAlternateColorCodes('&',"&6[&c&lAnty&f&lLogout&6] &aCreated by &l&"+ RandomColor.getRandom()  + "Czakiss"));
        return true;
    }




}
