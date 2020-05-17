package com.blalp.luckrankup;

import com.blalp.luckrankup.common.RankupCommand;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import net.luckperms.api.LuckPerms;

public class BukkitLuckRankup extends JavaPlugin {

	@Override
	public void onEnable() {
		getConfig().options().copyDefaults(true);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("rankup") && sender.hasPermission("luckrankup.rankup")) {
			sender.sendMessage(
					RankupCommand.doRankUp(getConfig().getString("backendName"), getConfig().getString("frontendName"),
							getConfig().getString("rankLadder"), ((Player) sender).getUniqueId().toString(),
							Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider(),
							getConfig().getBoolean("debug") || !getConfig().contains("debug")));
			return true;
		}
		return false;
	}
}