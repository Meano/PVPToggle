/*    */package com.sleelin.pvptoggle.handlers;

/*    */
/*    *//*    */
import org.bukkit.command.Command;
/*    */
import org.bukkit.command.CommandExecutor;
/*    */
import org.bukkit.command.CommandSender;
/*    */
import org.bukkit.entity.Player;
import com.sleelin.pvptoggle.PvPToggle;
/*    */
import com.sleelin.pvptoggle.commands.Global;
/*    */
import com.sleelin.pvptoggle.commands.Help;
/*    */
import com.sleelin.pvptoggle.commands.Region;
/*    */
import com.sleelin.pvptoggle.commands.Reset;
/*    */
import com.sleelin.pvptoggle.commands.Status;
/*    */
import com.sleelin.pvptoggle.commands.Toggle;
/*    */
import com.sleelin.pvptoggle.commands.World;

/*    */
/*    */public class CommandHandler
/*    */implements CommandExecutor
/*    */{
	/*    */private final PvPToggle plugin;

	/*    */
	/*    */public CommandHandler(PvPToggle instance)
	/*    */{
		/* 16 */this.plugin = instance;
		/*    */}

	/*    */
	/*    */public boolean onCommand(CommandSender sender, Command command, String label, String[] args)
	/*    */{
		/* 21 */Player player = null;
		/*    */
		/* 23 */if ((sender instanceof Player)) {
			/* 24 */player = (Player) sender;
			/*    */}
		/*    */
		/* 27 */if (args.length == 0) {
			/* 28 */if (player != null)
				/* 29 */new Toggle(this.plugin, sender, command, label, args).exec();
			/*    */else
				/* 31 */new Global(this.plugin, sender, command, label, args).exec();
			/*    */}
		/* 33 */else if ((args[0].equalsIgnoreCase("on")) || (args[0].equalsIgnoreCase("enable")) || (args[0].equalsIgnoreCase("off")) || (args[0].equalsIgnoreCase("disable")))
			/* 34 */new Toggle(this.plugin, sender, command, label, args).exec();
		/* 35 */else if ((args[0].equalsIgnoreCase("status")) || (args[0].equalsIgnoreCase("s")))
			/* 36 */new Status(this.plugin, sender, command, label, args).exec();
		/* 37 */else if ((args[0].equalsIgnoreCase("reset")) || (args[0].equalsIgnoreCase("r")))
			/* 38 */new Reset(this.plugin, sender, command, label, args).exec();
		/* 39 */else if ((args[0].startsWith("w:")) || (args[0].equalsIgnoreCase("world")) || (args[0].equalsIgnoreCase("w")))
			/* 40 */new World(this.plugin, sender, command, label, args).exec();
		/* 41 */else if ((args[0].equalsIgnoreCase("global")) || (args[0].equalsIgnoreCase("g")))
			/* 42 */new Global(this.plugin, sender, command, label, args).exec();
		/* 43 */else if (args[0].equalsIgnoreCase("region"))
			/* 44 */new Region(this.plugin, sender, command, label, args).exec();
		/* 45 */else if (args[0].equalsIgnoreCase("help"))
			/* 46 */new Help(this.plugin, sender, command, label, args).exec();
		/*    */else {
			/* 48 */new Help(this.plugin, sender);
			/*    */}
		/*    */
		/* 51 */return true;
		/*    */}
	/*    */
}

/*
 * Location: I:\MinecraftServer\Spigot\plugins\PvPToggle.jar Qualified Name:
 * com.sleelin.pvptoggle.handlers.CommandHandler JD-Core Version: 0.6.2
 */