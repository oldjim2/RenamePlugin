package com.gmail.justbru00.epic.rename.main;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gmail.justbru00.epic.rename.commands.EpicRename;
import com.gmail.justbru00.epic.rename.commands.Lore;
import com.gmail.justbru00.epic.rename.commands.Rename;
import com.gmail.justbru00.epic.rename.commands.RenameEntity;
import com.gmail.justbru00.epic.rename.commands.Renameany;

/**
 *******************************************
 * @author Justin Brubaker Plugin name: EpicRename
 *
 *         Copyright (C) 2015 Justin Brubaker
 *
 *         This program is free software; you can redistribute it and/or modify
 *         it under the terms of the GNU General Public License as published by
 *         the Free Software Foundation; either version 2 of the License, or (at
 *         your option) any later version.
 *
 *         This program is distributed in the hope that it will be useful, but
 *         WITHOUT ANY WARRANTY; without even the implied warranty of
 *         MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 *         General Public License for more details.
 *
 *         You should have received a copy of the GNU General Public License
 *         along with this program; if not, write to the Free Software
 *         Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA
 *         02110-1301 USA.
 * 
 *         You can contact the author @ justbru00@gmail.com
 */

public class RenameRewrite extends JavaPlugin {

	public static Economy econ = null;
	public Boolean useEconomy = false;
	public final Logger logger = Logger.getLogger("Minecraft");
	public ConsoleCommandSender clogger = this.getServer().getConsoleSender();
	public static String Prefix = color("&8[&bEpic&fRename&8] &f");
	public FileConfiguration config = getConfig();
	public List<String> blacklist;
	public String[] colorLetters = {"a", "b", "c", "d", "e", "f"};

	@Override
	public boolean onCommand(CommandSender sender, Command command,	String label, String[] args) {				
		
		return false;
	}

	public ItemStack renameItemStack(Player player, String displayname, ItemStack tobeRenamed) {
		ItemStack newitem = new ItemStack(tobeRenamed);
		ItemMeta im = tobeRenamed.getItemMeta();
		im.setDisplayName(color(displayname));
		newitem.setItemMeta(im);
		clogger.sendMessage(Prefix
				+ ChatColor.RED
				+ player.getName()
				+ ChatColor.translateAlternateColorCodes('&', config
						.getString("your msg")) + color(displayname));
		return newitem;		
	}
	
	public ItemStack renameItemStack(Player player, ArrayList<String> lore, ItemStack tobeRenamed) {
		ItemStack newitem = new ItemStack(tobeRenamed);
		ItemMeta im = tobeRenamed.getItemMeta();
		im.setLore(lore);
		newitem.setItemMeta(im);		
		return newitem;
	}

	/**
	 * @param player
	 *            Player you want to msg
	 * @param msg
	 *            message.
	 */
	public static void msg(Player player, String msg) {
		if (msg == null) {
			Bukkit.broadcastMessage(Prefix
					+ color("&4ERROR: null message in msg()"));
		}
		player.sendMessage(Prefix + color(msg));
	}

	/**
	 * @param uncoloredstring
	 *            String with & color codes.
	 * @return Returns string with ChatColor.[colorhere] instead of &b ect.
	 */
	public static String color(String uncoloredstring) {
		String colored = uncoloredstring.replace('_', ' ');
		colored = ChatColor.translateAlternateColorCodes('&', colored);
		return colored;
	}

	@Override
	public void onDisable() {

		clogger.sendMessage(Prefix + ChatColor.RED + "Has Been Disabled.");

	}

	@Override
	public void onEnable() {
		PluginDescriptionFile pdfFile = this.getDescription();
		clogger.sendMessage(color(Prefix + "&bThis plugin is made by Justin Brubaker."));
		clogger.sendMessage(color(Prefix + "&bEpicRename version " + pdfFile.getVersion() + " is Copyright (C) 2015 Justin Brubaker"));
		clogger.sendMessage(color(Prefix + "&bSee LICENSE infomation here: http://tinyurl.com/epicrename1"));
		
		this.saveDefaultConfig();
		
		try {
	        Metrics metrics = new Metrics(this);
	        metrics.start();
	    } catch (IOException e) {
	        // Failed to submit the stats :-(
	    }
		
		Prefix = color(config.getString("prefix"));
		clogger.sendMessage(color(Prefix + "&6Prefix has been set to the one in the config."));		

		if (config.getBoolean("economy.use")) {
			useEconomy = true;
			clogger.sendMessage(Prefix + ChatColor.GOLD	+ "Use economy in the config is true. Enabling Economy.");
		}
		
		if (!setupEconomy()) {
			clogger.sendMessage(Prefix
					+ color("&cVault not found disabling support for economy. If you would like economy download Vault at: "
							+ "http://dev.bukkit.org/bukkit-plugins/vault/"));
			useEconomy = false;
		}

		Bukkit.getServer().getPluginManager().registerEvents(new Watcher(), this);
		Bukkit.getPluginCommand("rename").setExecutor(new Rename(this));
		Bukkit.getPluginCommand("renameany").setExecutor(new Renameany(this));
		Bukkit.getPluginCommand("lore").setExecutor(new Lore(this));
		Bukkit.getPluginCommand("renameentity").setExecutor(new RenameEntity(this));
		Bukkit.getPluginCommand("epicrename").setExecutor(new EpicRename(this));
		
		clogger.sendMessage(Prefix + ChatColor.GOLD + "Version: " + pdfFile.getVersion() + " Has Been Enabled.");

	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer()
				.getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}
	/**
	 * 
	 * @param checking String to check for a blacklisted word.
	 * @return False if ok. True if found in blacklist
	 */
	public boolean checkBlacklist(String checking) {
		
		this.blacklist = config.getStringList("blacklist");
		
		int i = 0;
		while (i < blacklist.size()){
			if (blacklist.get(i) == null) {
				break;
			} else if (checking.toLowerCase().contains(blacklist.get(i).toLowerCase())) {				
				return true;
			}	
			
			i++;
		}
		
		return false;
	}
	
	/**
	 * 
	 * @return True if player can use the color. False if player can't
	 */
	public boolean checkColorPermissions(Player player, String proposedItemName) {
		boolean color = false;
		boolean format = false;
		
		// Check 0-9
		int i = 0;
		while (i < 10){
			if (proposedItemName.contains("&" + i)) {
				color = true;
			}
			i++;
		}
		
		// Check a-f
		i = 0;
		while (i < 6){
			if (proposedItemName.contains("&" + colorLetters[i])) {
				color = true;
			}
			i++;
		}
		
		// Check format
		if (proposedItemName.contains("&k")) format = true;
		if (proposedItemName.contains("&l")) format = true;
		if (proposedItemName.contains("&m")) format = true;
		if (proposedItemName.contains("&n")) format = true;
		if (proposedItemName.contains("&o")) format = true;
		if (proposedItemName.contains("&r")) format = true;
		
		
		if (color) {
			if (player.hasPermission("epicrename.color.*")) {				
				if (format) {
					if (player.hasPermission("epicrename.format.*")) {
						return true;
					} else {
						return false;
					}
				}
				return true;
			} else {
				return false;
			}			
		}	
			return false;
	}
	
	
}