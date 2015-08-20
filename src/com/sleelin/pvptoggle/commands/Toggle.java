package com.sleelin.pvptoggle.commands;

import org.bukkit.ChatColor;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import com.sleelin.pvptoggle.PvPCommand;
import com.sleelin.pvptoggle.PvPLocalisation;
import com.sleelin.pvptoggle.PvPToggle;

public class Toggle extends PvPCommand {
	public Toggle(PvPToggle plugin, CommandSender sender, Command command, String label, String[] args) {
		super(plugin, sender, command, label, args);
	}

	public Toggle(PvPToggle plugin, CommandSender sender) {
		super(plugin, sender);
	}

	protected boolean processCommand() {
		switch (this.args.length) {
			case 0:
				togglePlayer(this.sender, this.player, this.player.getWorld().getName(), !this.plugin.checkPlayerStatus(this.player, this.player.getWorld().getName()));
				break;
			case 1:
				if (this.player != null)
					togglePlayer(this.sender, this.player, this.player.getWorld().getName(), checkNewValue(this.args[0]).booleanValue());
				else {
					PvPLocalisation.display(this.sender, "", "", "", PvPLocalisation.Strings.CONSOLE_ERROR);
				}
				break;
			case 2:
				Player retrieved = getPlayer(this.sender, this.args[1], true);
				if (retrieved != null) {
					togglePlayer(this.sender, retrieved, retrieved.getWorld().getName(), checkNewValue(this.args[0]).booleanValue());
				}
				break;
			case 3:
				togglePlayer(this.sender, getPlayer(this.sender, this.args[1], true), isWorld(this.sender, this.args[2]), checkNewValue(this.args[0]).booleanValue());
				break;
			default:
				sendUsage(this.sender);
		}
		return true;
	}

	protected void sendUsage(CommandSender sender) {
		sender.sendMessage(this.helpHeader);
		ChatColor messagecolour = ChatColor.GOLD;
		if (this.plugin.permissionsCheck(sender, "pvptoggle.self.toggle", true)) {
			sender.sendMessage(messagecolour + "/pvp " + ChatColor.GRAY + "- Toggles own PvP status");
			sender.sendMessage(messagecolour + "/pvp on " + ChatColor.GRAY + "- Sets own PvP status to on");
			sender.sendMessage(messagecolour + "/pvp off " + ChatColor.GRAY + "- Sets own PvP status to off");
		}
		if (this.plugin.permissionsCheck(sender, "pvptoggle.other.toggle", true)) {
			sender.sendMessage(messagecolour + "/pvp on [player] " + ChatColor.GRAY + "- Sets player's PvP status to on in current world");
			sender.sendMessage(messagecolour + "/pvp on [player] [world] " + ChatColor.GRAY + "- Sets player's PvP status to on in specified world");
			sender.sendMessage(messagecolour + "/pvp off [player] " + ChatColor.GRAY + "- Sets player's PvP status to off in current world");
			sender.sendMessage(messagecolour + "/pvp off [player] [world] " + ChatColor.GRAY + "- Sets player's PvP status to off in specified world");
		}
	}

	private void togglePlayer(CommandSender sender, Player player, String worldname, boolean newval) {
		if ((player == null) || (worldname == null)) {
			return;
		}

		if (((!this.plugin.permissionsCheck(sender, "pvptoggle.self.toggle", true)) || (!sender.getName().equalsIgnoreCase(player.getName()))) && (!this.plugin.permissionsCheck(sender, "pvptoggle.other.toggle", true))) {
			PvPLocalisation.display(sender, "", "", "", PvPLocalisation.Strings.NO_PERMISSION);
			return;
		}

		if (sender.getName().equalsIgnoreCase(player.getName())) {
			if (!WorldGuardRegionCheck(player, "this"))
				return;

			if (newval) {
				this.plugin.setPlayerStatus(player, player.getWorld().getName(), true);
				PvPLocalisation.display(sender, "", player.getWorld().getName(), PvPLocalisation.Strings.PVP_ENABLED.toString(), PvPLocalisation.Strings.PVP_PLAYER_SELF_TOGGLE);
				this.plugin.log.info("[PvPToggle] Player " + player.getDisplayName() + " enabled pvp");
				this.plugin.setLastAction(player, "toggle");
			} else if (!newval) {
				if (this.plugin.checkLastAction(player, "toggle", player.getWorld().getName())) {
					this.plugin.setPlayerStatus(player, player.getWorld().getName(), false);
					PvPLocalisation.display(sender, "", player.getWorld().getName(), PvPLocalisation.Strings.PVP_DISABLED.toString(), PvPLocalisation.Strings.PVP_PLAYER_SELF_TOGGLE);
					this.plugin.log.info("[PvPToggle] Player " + player.getDisplayName() + " disabled pvp");
				} else {
					PvPLocalisation.display(sender, "", "", "", PvPLocalisation.Strings.PLAYER_COOLDOWN);
				}
			}
		} else {
			if (!WorldGuardRegionCheck(player, player.getName() + "'s current "))
				return;

			String message = null;
			if (newval)
				message = PvPLocalisation.Strings.PVP_ENABLED.toString();
			else {
				message = PvPLocalisation.Strings.PVP_DISABLED.toString();
			}

			if (worldname.equalsIgnoreCase("*")) {
				for (World world : this.plugin.getServer().getWorlds()) {
					this.plugin.setPlayerStatus(player, world.getName(), newval);
				}
				PvPLocalisation.display(player, "", "", message, PvPLocalisation.Strings.PVP_PLAYER_GLOBAL_TOGGLE);
				PvPLocalisation.display(sender, player.getName(), "", message, PvPLocalisation.Strings.PVP_PLAYER_GLOBAL_TOGGLE_SENDER);
			} else {
				this.plugin.setPlayerStatus(player, worldname, newval);
				PvPLocalisation.display(player, "", worldname, message, PvPLocalisation.Strings.PVP_PLAYER_OTHER_TOGGLE);
				PvPLocalisation.display(sender, player.getName(), worldname, message, PvPLocalisation.Strings.PVP_PLAYER_OTHER_TOGGLE_SENDER);
			}
			this.plugin.setLastAction(player, "toggle");
		}
	}

	private boolean WorldGuardRegionCheck(Player player, String target) {
		if (((Boolean) this.plugin.getGlobalSetting("worldguard")).booleanValue()) {
			return this.plugin.regionListener.WorldGuardRegionCheck(player, target);
		}
		return true;
	}

}
