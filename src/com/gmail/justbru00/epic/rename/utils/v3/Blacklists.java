/**
 * @author Justin "JustBru00" Brubaker
 * 
 * This is licensed under the MPL Version 2.0. See license info in LICENSE.txt
 */
package com.gmail.justbru00.epic.rename.utils.v3;

import org.bukkit.Material;
import org.bukkit.command.CommandExecutor;

import com.gmail.justbru00.epic.rename.main.v3.Main;

public class Blacklists {
	
	// VERSION 3
	/**
	 * 
	 * @param m The Material from the {@link CommandExecutor}
	 * @return True if NO blacklisted material found. False if a blacklisted material is FOUND.
	 */
	public static boolean checkMaterialBlacklist(Material m) {		
		
		for (String s : Main.getInstance().getConfig().getStringList("blacklists.material")) {
			if (s != null) {
				if (m == Material.getMaterial(s)) {
					Debug.send("Material blacklist detected blacklisted material. (" + m.toString() + ")");
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 
	 * @param args The arguments from the {@link CommandExecutor}
	 * @return True if NO blacklisted word found. False if a blacklisted word is FOUND.
	 */
	public static boolean checkTextBlacklist(String[] args) {
		
		for (String s : Main.getInstance().getConfig().getStringList("blacklists.text")) {
			if (s != null) {
				for (String arg : args) {
					if (arg.toLowerCase().contains(s.toLowerCase())) {
						Debug.send("Text blacklist has found blacklisted word. (" + s + ")");
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
}
