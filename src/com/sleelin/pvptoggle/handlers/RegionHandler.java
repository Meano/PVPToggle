package com.sleelin.pvptoggle.handlers;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import com.sleelin.pvptoggle.PvPToggle;

public class RegionHandler {
	private static FileConfiguration customConfig = null;
	private static File customConfigFile = null;
	private static PvPToggle plugin = null;
	public static HashMap<String, ArrayList<String>> worldregions = new HashMap<String, ArrayList<String>>();

	public static void loadProcedure(PvPToggle p) {
		plugin = p;
		reloadCustomConfig();
		loadValues();
	}

	public static void writeValues() {
		for (World world : plugin.getServer().getWorlds()) {
			getCustomConfig().set(world.getName(), worldregions.get(world.getName()));
		}
		saveCustomConfig();
	}

	public static void loadValues() {
		worldregions.clear();
		Iterator<?> localIterator2;
		for (Iterator<?> localIterator1 = plugin.getServer().getWorlds().iterator(); localIterator1.hasNext(); localIterator2.hasNext()) {
			World world = (World) localIterator1.next();
			worldregions.put(world.getName(), new ArrayList<String>());
			List<?> regions = getCustomConfig().getStringList(world.getName());
			localIterator2 = regions.iterator();
			continue;
			// String region = (String) localIterator2.next();
			// ((ArrayList)
			// worldregions.get(world.getName())).add(region);
		}

		plugin.log.info("[" + plugin.getDescription().getName() + "] Loaded region assignment information from regions file");
	}

	private static void reloadCustomConfig() {
		if (customConfigFile == null) {
			customConfigFile = new File(plugin.getDataFolder(), "regions.yml");
		}
		customConfig = YamlConfiguration.loadConfiguration(customConfigFile);
	}

	private static FileConfiguration getCustomConfig() {
		if (customConfig == null) {
			reloadCustomConfig();
		}
		return customConfig;
	}

	private static void saveCustomConfig() {
		if ((customConfig == null) || (customConfigFile == null))
			return;
		try {
			customConfig.save(customConfigFile);
		} catch (IOException ex) {
			Logger.getLogger(JavaPlugin.class.getName()).log(Level.SEVERE, "Could not save config to " + customConfigFile, ex);
		}
	}

	public static void addRegion(CommandSender sender, String world, String region) {
		if (plugin.permissionsCheck(sender, "pvptoggle.regions.add", true)) {
			if (!worldregions.containsKey(world))
				worldregions.put(world, new ArrayList<String>());
			((ArrayList<String>) worldregions.get(world)).add(region);
			writeValues();
		}
	}

	public static void removeRegion(CommandSender sender, String world, String region) {
		if (worldregions.containsKey(world)) {
			((ArrayList<?>) worldregions.get(world)).remove(region);
			writeValues();
		}
	}

	public static boolean isApplicableRegion(String world, String region) {
		if ((worldregions.containsKey(world)) && (((ArrayList<?>) worldregions.get(world)).contains(region)))
			return true;

		return false;
	}

}
