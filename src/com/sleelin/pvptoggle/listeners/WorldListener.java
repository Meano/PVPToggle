/*    */package com.sleelin.pvptoggle.listeners;

/*    */
/*    *//*    */
import org.bukkit.event.EventHandler;
/*    */
import org.bukkit.event.EventPriority;
/*    */
import org.bukkit.event.Listener;
/*    */
import org.bukkit.event.world.WorldLoadEvent;
import com.sleelin.pvptoggle.PvPToggle;

/*    */
/*    */public class WorldListener
/*    */implements Listener
/*    */{
	/*    */public static PvPToggle plugin;

	/*    */
	/*    */public WorldListener(PvPToggle instance)
	/*    */{
		/* 15 */plugin = instance;
		/*    */}

	/*    */
	/*    */@EventHandler(priority = EventPriority.NORMAL)
	/*    */public void onWorldLoad(WorldLoadEvent event) {
		/* 20 */plugin.loadWorld(event.getWorld());
		/*    */}
	/*    */
}

/*
 * Location: I:\MinecraftServer\Spigot\plugins\PvPToggle.jar Qualified Name:
 * com.sleelin.pvptoggle.listeners.WorldListener JD-Core Version: 0.6.2
 */