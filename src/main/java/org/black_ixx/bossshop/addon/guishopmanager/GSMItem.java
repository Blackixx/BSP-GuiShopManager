package org.black_ixx.bossshop.addon.guishopmanager;

import java.util.ArrayList;
import java.util.List;

import org.black_ixx.bossshop.addon.guishopmanager.GSMItems.GSMGiveItemsReason;
import org.black_ixx.bossshop.managers.ClassManager;
import org.black_ixx.bossshop.managers.misc.InputReader;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GSMItem {

	private String path;
	private ItemStack item;
	private String open_shop;
	private List<String> commands;
	private List<String> playercommands;
	private int inventory_location;
	private boolean give_on_join;
	private String[] worlds;


	public GSMItem(GuiShopManager plugin, ConfigurationSection section) {
		this.path = section.getName();

		open_shop = section.getString("OpenShop");
		commands = InputReader.readStringList(section.get("Command"));
		playercommands = InputReader.readStringList(section.get("PlayerCommand"));
		inventory_location = InputReader.getInt(section.get("InventoryLocation"), 1) - 1;
		give_on_join = InputReader.getBoolean(section.getString("GiveOnJoin"), true);

		List<String> list = InputReader.readStringList(section.get("Look"));
		if (list != null) {
			this.item = plugin.getBossShop().getClassManager().getItemStackCreator().createItemStack(list, false);
		}

		String world = section.getString("World");
		if (world != null){
			worlds = world.split(":");
		}
	}

	public String getPath(){
		return path;
	}

	public ItemStack getItemStack(){
		return item;
	}

	public boolean isValid() {
		if (item == null) {
			return false;
		}
		if (!item.hasItemMeta()) {
			return false;
		}
		if (!item.getItemMeta().hasDisplayName()) {
			return false;
		}
		return true;
	}

	public boolean isCorrespondingItem(ItemStack item) {
		if (!isValid()) {
			return false;
		}

		if (!item.hasItemMeta()) {
			return false;
		}

		if (!item.getItemMeta().hasDisplayName()) {
			return false;
		}

		if (!ClassManager.manager.getItemStackChecker().isEqualShopItemAdvanced(item, this.item, false,
				false, true, null)) {
			return false;
		}


		if (!item.getItemMeta().getDisplayName().equals(this.item.getItemMeta().getDisplayName())) {
			return false;
		}

		return true;
	}


	public void giveItem(Player p, GSMGiveItemsReason reason) {
		if (reason == GSMGiveItemsReason.COMMAND) {
			giveItem(p);
			return;
		}

		if (reason == GSMGiveItemsReason.WORLD_CHANGED) {
			if ((p.getInventory().contains(item.getType())) && (hasItem(p))) { //Player already has item
				if (!isWorldSupported(p.getWorld())) { //World does not support item
					removeItem(p);
					return;
				}
			}
		}


		if (give_on_join) {
			if ((p.getInventory().contains(item.getType())) && (hasItem(p))) { //Player already has item
				return;
			}
			if (!isWorldSupported(p.getWorld())) {
				return;
			}

			giveItem(p);
		}

	}

	@Deprecated
	public void giveItem(Player p) {
		boolean bad_location = false;

		int loc = inventory_location;

		if ((loc >= p.getInventory().getSize()) || (loc < 0)) {
			loc = 0;
			bad_location = true;
		}

		ItemStack real_item = ClassManager.manager.getItemStackTranslator().translateItemStack(null, null, null, item.clone(), p, true);

		if ((!bad_location) || (p.getInventory().getItem(loc) == null)) {
			p.getInventory().setItem(loc, real_item);
		} else {
			p.getInventory().addItem(real_item);
		}
	}

	public void removeItem(Player p) {
		List<ItemStack> to_remove = null;
		for (ItemStack s : p.getInventory().getContents()) {
			if (s != null) {
				if (isSameItem(s, p)) {
					if (to_remove == null) {
						to_remove = new ArrayList<ItemStack>();
					}
					to_remove.add(s);
				}
			}
		}

		for (ItemStack remove : to_remove) {
			p.getInventory().remove(remove);
		}
	}

	public boolean hasItem(Player p) {
		for (ItemStack s : p.getInventory().getContents()) {
			if (s != null) {
				if (isSameItem(s, p)) {
					return true;
				}
			}
		}

		return false;
	}

	public boolean isSameItem(ItemStack i, Player p) {
		if (ClassManager.manager.getItemStackChecker().isEqualShopItemAdvanced(item, i, false,
				false, true, p)) {

			String displayname = item.getItemMeta().getDisplayName();

			if (ClassManager.manager.getStringManager().checkStringForFeatures(null, null, null,
					displayname)) { //contains special variables
				displayname = ClassManager.manager.getStringManager().transform(displayname, p);
			}

			if (displayname.equalsIgnoreCase(i.getItemMeta().getDisplayName())) {
				return true;
			}

		}
		return false;
	}

	public boolean isWorldSupported(World w) {
		if (worlds == null) {
			return true;
		}
		for (String world : worlds) {
			if (world.equalsIgnoreCase(w.getName())) {
				return true;
			}
		}
		return false;
	}


	public boolean playerClicked(GuiShopManager plugin, PlayerInteractEvent e) {
		if (!isCorrespondingItem(e.getItem())) {
			return false;
		}

		if (open_shop != null) {
			plugin.getBossShop().getAPI().openShop(e.getPlayer(), open_shop);
		}

		if (commands != null) {
			for (String command : commands) {
				Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(),
						ClassManager.manager.getStringManager().transform(command, e.getPlayer()));
			}
		}

		if (playercommands != null) {
			for (String command : playercommands) {
				e.getPlayer().performCommand(ClassManager.manager.getStringManager().transform(command, e.getPlayer()));
			}
		}

		e.getPlayer().updateInventory();
		return true;
	}

}
