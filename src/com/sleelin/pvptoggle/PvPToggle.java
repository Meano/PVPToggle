package com.sleelin.pvptoggle;

//import com.nijiko.permissions.PermissionHandler;
//import com.nijikokun.bukkit.Permissions.Permissions;
import java.net.URL;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilderFactory;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sleelin.pvptoggle.handlers.CommandHandler;
import com.sleelin.pvptoggle.handlers.RegionHandler;
import com.sleelin.pvptoggle.listeners.EntityListener;
import com.sleelin.pvptoggle.listeners.PlayerListener;
import com.sleelin.pvptoggle.listeners.RegionListener;
import com.sleelin.pvptoggle.listeners.WorldListener;

public class PvPToggle extends JavaPlugin {
	public Logger log = Logger.getLogger("Minecraft");
	// private static PermissionHandler permissionHandler;
	private final PlayerListener playerListener = new PlayerListener(this);
	private final EntityListener entityListener = new EntityListener(this);
	private final WorldListener worldListener = new WorldListener(this);
	public RegionListener regionListener;
	private HashMap<String, Object> globalsettings = new HashMap<String, Object>();
	protected HashMap<String, PvPWorld> worlds = new HashMap<String, PvPWorld>();
	private HashMap<Player, PvPAction> lastaction = new HashMap<Player, PvPAction>();

	public void onEnable() {
		this.log.info("[" + getDescription().getName() + "] Loading...");

		PvPLocalisation.loadProcedure(this);
		loadProcedure();

		getServer().getPluginManager().registerEvents(this.playerListener, this);
		getServer().getPluginManager().registerEvents(this.entityListener, this);
		getServer().getPluginManager().registerEvents(this.worldListener, this);

		getDescription().getVersion();
		getDescription().getName();

		if ((((String) this.globalsettings.get("command")).equalsIgnoreCase("tpvp")) || (((String) this.globalsettings.get("command")).equalsIgnoreCase("pvpt")))
			getCommand((String) this.globalsettings.get("command")).setExecutor(new CommandHandler(this));
		else {
			getCommand("pvp").setExecutor(new CommandHandler(this));
		}

		if (((Boolean) this.globalsettings.get("worldguard")).booleanValue()) {
			RegionHandler.loadProcedure(this);
			this.regionListener = new RegionListener(this);
			getServer().getPluginManager().registerEvents(this.regionListener, this);
		}

		System.out.println("[" + getDescription().getName() + "] v" + getDescription().getVersion() + " enabled!");
	}

	public void onDisable() {
		this.worlds.clear();
		this.globalsettings.clear();
		this.lastaction.clear();
		this.log.info("[PvPToggle] Disabled");
	}

	public PvPToggle getHandler() {
		return this;
	}

	private void loadProcedure() {
		if (!getConfig().isSet("plugin.enabled"))
			getConfig().set("plugin.enabled", Boolean.valueOf(!getConfig().getBoolean("globalDisabled", false)));
		if (!getConfig().isSet("plugin.debug"))
			getConfig().set("plugin.debug", Boolean.valueOf(getConfig().getBoolean("debug", false)));
		if (!getConfig().isSet("plugin.updateinterval"))
			getConfig().set("plugin.updateinterval", Integer.valueOf(getConfig().getInt("updateinterval", 21600)));
		if (!getConfig().isSet("plugin.command"))
			getConfig().set("plugin.command", "pvp");
		if (!getConfig().isSet("plugin.worldguard-integration"))
			getConfig().set("plugin.worldguard-integration", Boolean.valueOf(false));

		getConfig().set("cooldown", null);
		getConfig().set("warmup", null);
		getConfig().set("globalDisabled", null);
		getConfig().set("debug", null);
		getConfig().set("updateinterval", null);

		saveConfig();

		this.globalsettings.put("enabled", Boolean.valueOf(getConfig().getBoolean("plugin.enabled", true)));
		this.globalsettings.put("debug", Boolean.valueOf(getConfig().getBoolean("plugin.debug", false)));
		this.globalsettings.put("updateinterval", Integer.valueOf(getConfig().getInt("plugin.updateinterval", 21600)));
		this.globalsettings.put("command", getConfig().getString("plugin.command", "pvp"));
		this.globalsettings.put("citizens", Boolean.valueOf(false));
		this.globalsettings.put("worldguard", Boolean.valueOf(getConfig().getBoolean("plugin.worldguard-integration", false)));

		for (World world : getServer().getWorlds()) {
			loadWorld(world);
		}

		for (Player player : getServer().getOnlinePlayers()) {
			this.lastaction.put(player, new PvPAction(Long.valueOf(0L), "login"));
		}

		/*
		 * if (getServer().getPluginManager().getPlugin("Permissions")
		 * != null) { if
		 * (!getServer().getPluginManager().getPlugin("Permissions"
		 * ).getDescription().getVersion().equalsIgnoreCase("2.7.7")) {
		 * //permissionHandler =
		 * ((Permissions)getServer().getPluginManager
		 * ().getPlugin("Permissions")).getHandler(); this.log.info("["
		 * + getDescription().getName() + "] Legacy Permissions " +
		 * getServer
		 * ().getPluginManager().getPlugin("Permissions").getDescription
		 * ().getVersion() + " detected"); } else { this.log.info("[" +
		 * getDescription().getName() +
		 * "] Permissions bridge detected, using SuperPerms instead!");
		 * } } else this.log.info("[" + getDescription().getName() +
		 * "] Using SuperPerms for permissions checking");
		 */

		if (getServer().getPluginManager().getPlugin("Citizens") != null) {
			this.globalsettings.put("citizens", Boolean.valueOf(true));
			this.log.info("[" + getDescription().getName() + "] Citizens Plugin detected");
		}

		if ((getServer().getPluginManager().getPlugin("WorldGuard") != null) && ((getServer().getPluginManager().getPlugin("WorldGuard") instanceof WorldGuardPlugin))) {
			this.log.info("[" + getDescription().getName() + "] WorldGuard Plugin detected...");
			if (((Boolean) this.globalsettings.get("worldguard")).booleanValue())
				this.log.info("[" + getDescription().getName() + "] WorldGuard integration enabled!");
			else
				this.log.info("[" + getDescription().getName() + "] WorldGuard integration disabled via options!");
		}
	}

	public void loadWorld(World world) {
		PvPWorld pvpworld = new PvPWorld();

		if (!getConfig().isSet("worlds." + world.getName() + ".enabled"))
			getConfig().set("worlds." + world.getName() + ".enabled", Boolean.valueOf(getConfig().getBoolean("worlds." + world.getName() + ".pvpenabled", true)));
		if (!getConfig().isSet("worlds." + world.getName() + ".default"))
			getConfig().set("worlds." + world.getName() + ".default", Boolean.valueOf(getConfig().getBoolean("worlds." + world.getName() + ".logindefault", true)));
		if (!getConfig().isSet("worlds." + world.getName() + ".cooldown"))
			getConfig().set("worlds." + world.getName() + ".cooldown", Integer.valueOf(getConfig().getInt("cooldown", 0)));
		if (!getConfig().isSet("worlds." + world.getName() + ".warmup"))
			getConfig().set("worlds." + world.getName() + ".warmup", Integer.valueOf(getConfig().getInt("warmup", 0)));

		getConfig().set("worlds." + world.getName().toString() + ".pvpenabled", null);
		getConfig().set("worlds." + world.getName().toString() + ".logindefault", null);

		saveConfig();

		pvpworld.cooldown = getConfig().getInt("worlds." + world.getName() + ".cooldown", 0);
		pvpworld.warmup = getConfig().getInt("worlds." + world.getName() + ".warmup", 0);
		pvpworld.enabled = getConfig().getBoolean("worlds." + world.getName() + ".enabled", true);
		pvpworld.logindefault = getConfig().getBoolean("worlds." + world.getName() + ".default", true);

		for (Player player : getServer().getOnlinePlayers()) {
			pvpworld.players.put(player, Boolean.valueOf(pvpworld.logindefault));
		}

		this.worlds.put(world.getName(), pvpworld);

		this.log.info("[" + getDescription().getName() + "] found and loaded world " + world.getName());
	}

	protected void setWorldStatus(String world, boolean enabled) {
		((PvPWorld) this.worlds.get(world)).enabled = enabled;
	}

	public boolean getWorldStatus(String world) {
		if (world != null)
			return true;
		return ((PvPWorld) this.worlds.get(world)).enabled;
	}

	public boolean getWorldDefault(String world) {
		if (world != null) {
			return ((PvPWorld) this.worlds.get(world)).logindefault;
		}
		return true;
	}

	public PvPWorld getWorld(String world) {
		return (PvPWorld) this.worlds.get(world);
	}

	public String checkWorldName(String targetworld) {
		String output = null;
		for (World world : getServer().getWorlds()) {
			if (world.getName().toLowerCase().contains(targetworld.toLowerCase())) {
				output = world.getName();
				break;
			}
		}
		return output;
	}

	public void setPlayerStatus(Player player, String world, boolean status) {
		if ((checkWorldName(world) != null) && (player != null))
			((PvPWorld) this.worlds.get(checkWorldName(world))).players.put(player, Boolean.valueOf(status));
	}

	public boolean checkPlayerStatus(Player player, String world) {
		if (!((PvPWorld) this.worlds.get(world)).players.containsKey(player)) {
			this.lastaction.put(player, new PvPAction(Long.valueOf(0L), "login"));
			((PvPWorld) this.worlds.get(world)).players.put(player, Boolean.valueOf(((PvPWorld) this.worlds.get(world)).logindefault));
		}

		if (permissionsCheck(player, "pvptoggle.pvp.force", false))
			return true;
		if (permissionsCheck(player, "pvptoggle.pvp.deny", false))
			return false;

		return ((Boolean) ((PvPWorld) this.worlds.get(world)).players.get(player)).booleanValue();
	}

	public Object getGlobalSetting(String setting) {
		return this.globalsettings.get(setting);
	}

	protected void setGlobalSetting(String setting, Object value) {
		this.globalsettings.put(setting, value);
	}

	protected void toggleGlobalStatus(Boolean newval) {
		setGlobalSetting("enabled", newval);
	}

	public Boolean checkGlobalStatus() {
		return (Boolean) this.globalsettings.get("enabled");
	}

	public void setLastAction(Player player, String action) {
		this.lastaction.put(player, new PvPAction(Long.valueOf(new GregorianCalendar().getTime().getTime()), action));
	}

	public boolean checkLastAction(Player player, String action, String world) {
		GregorianCalendar cal = new GregorianCalendar();
		Long difference = Long.valueOf(cal.getTime().getTime() - ((PvPAction) this.lastaction.get(player)).time.longValue());
		int before = 0;
		if (action.equalsIgnoreCase("combat")) {
			if (((PvPAction) this.lastaction.get(player)).action.equalsIgnoreCase("toggle"))
				before = difference.compareTo(Long.valueOf(((PvPWorld) this.worlds.get(world)).warmup * 1000L));
		} else if ((action.equalsIgnoreCase("toggle")) && (((PvPAction) this.lastaction.get(player)).action.equalsIgnoreCase("combat"))) {
			before = difference.compareTo(Long.valueOf(((PvPWorld) this.worlds.get(world)).cooldown * 1000L));
		}

		if (before >= 0) {
			return true;
		}
		return false;
	}

	public boolean permissionsCheck(CommandSender sender, String permissions, boolean opdefault) {
		boolean haspermissions = opdefault;
		Player player;
		if ((sender instanceof Player))
			player = (Player) sender;
		else
			return true;
		if (((Boolean) this.globalsettings.get("debug")).booleanValue())
			this.log.info(player.getName().toString() + "/" + permissions + "/Start: " + haspermissions);

		/*
		 * if (permissionHandler != null) { haspermissions =
		 * permissionHandler.has(player, permissions); if
		 * (((Boolean)this.globalsettings.get("debug")).booleanValue())
		 * this.log.info(player.getName().toString() + "/" + permissions
		 * + "/LegPerms: " + haspermissions); if
		 * (permissionHandler.has(player, "*")) haspermissions =
		 * opdefault; } else { haspermissions =
		 * player.hasPermission(permissions); if
		 * (((Boolean)this.globalsettings.get("debug")).booleanValue())
		 * this.log.info(player.getName().toString() + "/" + permissions
		 * + "/Before*: " + haspermissions); if
		 * (player.hasPermission("*")) { haspermissions = opdefault; }
		 * if
		 * (((Boolean)this.globalsettings.get("debug")).booleanValue())
		 * this.log.info(player.getName().toString() + "/" + permissions
		 * + "/After*: " + haspermissions); }
		 */

		if (((Boolean) this.globalsettings.get("debug")).booleanValue())
			this.log.info(player.getName().toString() + "/" + permissions + "/Final: " + haspermissions);
		return haspermissions;
	}

	public String updateCheck(String currentVersion) {
		try {
			URL url = new URL("http://dev.bukkit.org/server-mods/PvPToggle/files.rss");
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openConnection().getInputStream());
			doc.getDocumentElement().normalize();
			NodeList nodes = doc.getElementsByTagName("item");
			Node firstNode = nodes.item(0);
			if (firstNode.getNodeType() == 1) {
				Element firstElement = (Element) firstNode;
				NodeList firstElementTagName = firstElement.getElementsByTagName("title");
				Element firstNameElement = (Element) firstElementTagName.item(0);
				NodeList firstNodes = firstNameElement.getChildNodes();
				return firstNodes.item(0).getNodeValue();
			}
		} catch (Exception e) {
			return currentVersion;
		}
		return currentVersion;
	}

	public class PvPAction {
		Long time;
		String action;

		public PvPAction(Long itime, String iaction) {
			this.time = itime;
			this.action = iaction;
		}
	}

	public class PvPWorld {
		int cooldown;
		int warmup;
		boolean enabled;
		boolean logindefault;
		HashMap<Player, Boolean> players = new HashMap<Player, Boolean>();

		public PvPWorld() {
		}
	}
}

/*
 * Location: I:\MinecraftServer\Spigot\plugins\PvPToggle.jar Qualified Name:
 * com.sleelin.pvptoggle.PvPToggle JD-Core Version: 0.6.2
 */