package org.black_ixx.bossshop.addon.guishopmanager;

import org.black_ixx.bossshop.addon.guishopmanager.GSMItems.GSMGiveItemsReason;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commander implements CommandExecutor {
	private GuiShopManager plugin;

	public Commander(GuiShopManager plugin) {
		this.plugin = plugin;
	}

	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {
		if ((args.length == 1) || (args.length == 2) || (args.length == 3))   {
			if (args[0].equalsIgnoreCase("reload")) {
				if (!sender.hasPermission("GUIShopManager.reload")) {
					sender.sendMessage(ChatColor.RED + "No Permissions!");
					return false;
				}

				this.plugin.reload();
				sender.sendMessage(ChatColor.RED + "Reloaded " + ChatColor.YELLOW + "GUIShopManager");
				return true;
			}

			if (args[0].equalsIgnoreCase("items")) {
				if (!sender.hasPermission("GUIShopManager.Items")) {
					sender.sendMessage(ChatColor.RED + "No Permissions!");
					return false;
				}

				sender.sendMessage(ChatColor.YELLOW + "" + ChatColor.BOLD + "GuiShopManager Items");
				for (GSMItem item : this.plugin.getGSMItems().getItems()) {
					sender.sendMessage(ChatColor.YELLOW + "- " + item.getPath());
				}
				return true;
			}

			if (args[0].equalsIgnoreCase("give")) {
				if (!sender.hasPermission("GUIShopManager.give")) {
					sender.sendMessage(ChatColor.RED + "No Permissions!");
					return false;
				}

				if ((args.length != 2) && (args.length != 3)) {
					sendInfo(sender);
					return false;
				}

				String itemname = args[1];
				String playername = sender.getName();
				if (args.length == 3) {
					playername = args[2];
				}
				else if (!(sender instanceof Player)) {
					sendInfo(sender);
					return false;
				}

				GSMItem item = this.plugin.getGSMItems().getItemByName(itemname);

				if (item == null) {
					sender.sendMessage(ChatColor.RED + "GuiShopManager Item " + ChatColor.GOLD + itemname + ChatColor.RED + " not found!");
					return false;
				}

				Player player = getPlayer(playername);
				if (player == null) {
					sender.sendMessage(ChatColor.RED + "Player " + ChatColor.GOLD + playername + ChatColor.RED + " not found!");
					return false;
				}

				item.giveItem(player, GSMGiveItemsReason.COMMAND);
				sender.sendMessage(ChatColor.YELLOW + "Added GSM Item " + ChatColor.RED + itemname + ChatColor.YELLOW + " to " + ChatColor.RED + playername + "'s" + ChatColor.YELLOW + " Inventory.");

				return true;
			}

		}

		sendInfo(sender);
		return false;
	}

	private void sendInfo(CommandSender s) {
		s.sendMessage(ChatColor.YELLOW + "/GSM reload " + ChatColor.RED + "- Reloads GUIShopManager");
		s.sendMessage(ChatColor.YELLOW + "/GSM items " + ChatColor.RED + "- Lists all GSM Items");
		s.sendMessage(ChatColor.YELLOW + "/GSM give <itemname> [Player] " + ChatColor.RED + "- Gives [Player] the named item");
	}

	private Player getPlayer(String name) {
		Player p = getPlayer(name, true);
		if (p!=null) {
			return p;
		}
		return getPlayer(name, false);
	}

	private Player getPlayer(String name, boolean exact) {
		for (Player p : Bukkit.getOnlinePlayers()) {
			if (exact) {
				if (p.getName().equalsIgnoreCase(name)) {
					return p;
				}
			} else {
				if (p.getName().toLowerCase().startsWith(name.toLowerCase())) {
					return p;
				}
			}
		}
		return null;
	}
}