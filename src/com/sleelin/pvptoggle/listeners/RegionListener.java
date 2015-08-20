package com.sleelin.pvptoggle.listeners;

import java.util.HashMap;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;
import com.sleelin.pvptoggle.PvPLocalisation;
import com.sleelin.pvptoggle.PvPToggle;

public class RegionListener implements Listener {
	public static PvPToggle plugin;
	private static HashMap<String, String[]> playerstatus = new HashMap<String, String[]>();

	public RegionListener(PvPToggle instance) {
		plugin = instance;
	}

	@EventHandler(priority = EventPriority.NORMAL)
	public void onPlayerMove(PlayerMoveEvent event) {
		// if
		// (((Boolean)plugin.getGlobalSetting("worldguard")).booleanValue())
		// {
		// Player player = event.getPlayer();
		// WorldGuardPlugin worldGuard =
		// (WorldGuardPlugin)plugin.getServer().getPluginManager().getPlugin("WorldGuard");
		// Boolean inRegion = Boolean.valueOf(false);

		/*
		 * ApplicableRegionSet set =
		 * worldGuard.getRegionManager(player.getWorld
		 * ()).getApplicableRegions
		 * (BukkitUtil.toVector(player.getLocation().getBlock()));
		 * 
		 * for (ProtectedRegion region : set) { if
		 * (RegionHandler.isApplicableRegion
		 * (player.getWorld().getName(), region.getId())) { if
		 * ((playerstatus.containsKey(player.getName())) &&
		 * (((String[])playerstatus
		 * .get(player.getName()))[1].equalsIgnoreCase(region.getId())))
		 * { inRegion = Boolean.valueOf(true); break; }
		 * 
		 * for (Flag flag : region.getFlags().keySet()) { if
		 * (flag.getName().equals("pvp")) { if
		 * (region.getFlag(flag).equals(StateFlag.State.ALLOW)) {
		 * plugin.setPlayerStatus(player, player.getWorld().getName(),
		 * true); playerstatus.put(player.getName(), new String[] {
		 * PvPLocalisation.Strings.PVP_ENABLED.toString(),
		 * region.getId() }); PvPLocalisation.display(player,
		 * region.getId(), null,
		 * PvPLocalisation.Strings.PVP_ENABLED.toString(),
		 * PvPLocalisation.Strings.WORLDGUARD_REGION_ENTERED); } else if
		 * (region.getFlag(flag).equals(StateFlag.State.DENY)) {
		 * plugin.setPlayerStatus(player, player.getWorld().getName(),
		 * false); playerstatus.put(player.getName(), new String[] {
		 * PvPLocalisation.Strings.PVP_DENIED.toString(), region.getId()
		 * }); PvPLocalisation.display(player, region.getId(), null,
		 * PvPLocalisation.Strings.PVP_DENIED.toString(),
		 * PvPLocalisation.Strings.WORLDGUARD_REGION_ENTERED); }
		 * inRegion = Boolean.valueOf(true); break; } } } } if
		 * ((!inRegion.booleanValue()) &&
		 * (playerstatus.containsKey(player.getName()))) {
		 * PvPLocalisation.display(player,
		 * ((String[])playerstatus.get(player.getName()))[1], null,
		 * ((String[])playerstatus.get(player.getName()))[0],
		 * PvPLocalisation.Strings.WORLDGUARD_REGION_EXIT);
		 * playerstatus.remove(player.getName()); } }
		 */
	}

	public boolean WorldGuardRegionCheck(Player player, String target) {
		if ((((Boolean) plugin.getGlobalSetting("worldguard")).booleanValue()) && (playerstatus.containsKey(player.getName()))) {
			if (((String[]) playerstatus.get(player.getName()))[0].equals(PvPLocalisation.Strings.PVP_ENABLED.toString()))
				PvPLocalisation.display(player, target, null, PvPLocalisation.Strings.PVP_FORCED.toString(), PvPLocalisation.Strings.WORLDGUARD_TOGGLE_DENIED);
			else if (((String[]) playerstatus.get(player.getName()))[0].equals(PvPLocalisation.Strings.PVP_DENIED.toString())) {
				PvPLocalisation.display(player, target, null, PvPLocalisation.Strings.PVP_DENIED.toString(), PvPLocalisation.Strings.WORLDGUARD_TOGGLE_DENIED);
			}
			return false;
		}

		return true;
	}
}

/*
 * Location: I:\MinecraftServer\Spigot\plugins\PvPToggle.jar Qualified Name:
 * com.sleelin.pvptoggle.listeners.RegionListener JD-Core Version: 0.6.2
 */