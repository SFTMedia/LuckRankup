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
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (command.getName().equals("rankup") && sender.hasPermission("luckrankup.rankup")) {
			RankupCommand.doRankUp(getConfig().getString("backendName"), getConfig().getString("frontendName"),
					getConfig().getString("rankLadder"), ((Player) sender).getUniqueId().toString(),
					Bukkit.getServicesManager().getRegistration(LuckPerms.class).getProvider());
			return true;
		}
		return false;
	}
}