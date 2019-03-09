package org.black_ixx.bossshop.addon.guishopmanager;

import java.util.List;
import java.util.Vector;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;

public class GSMItems implements Reloadable {

	enum GSMGiveItemsReason {
		RESPAWN,
		SERVER_JOIN,
		WORLD_CHANGED,
		COMMAND
	}


	private List<GSMItem> items = new Vector<GSMItem>();
	private GuiShopManager plugin;

	public GSMItems(GuiShopManager plugin) {
		this.plugin = plugin;
		loadItems(plugin);
	}

	private void loadItems(GuiShopManager plugin) {
		ConfigurationSection c = plugin.getConfig().getConfigurationSection("Items");
		if (c == null) {
			Bukkit.getLogger().severe("[GuiShopManager] No Items were found in the config :/ Delete your config and restart the server to generate a new config file.");
			return;
		}

		for (String path_name : c.getKeys(false))   {
			ConfigurationSection sec = c.getConfigurationSection(path_name);

			GSMItem item = new GSMItem(plugin, sec);

			if(item.isValid()){
				this.items.add(item);
			}
		}
	}

	public List<GSMItem> getItems(){
		return items;
	}

	public void reload(GuiShopManager plugin) {
		this.items.clear();
		loadItems(plugin);
	}

	public void giveItems(Player p, GSMGiveItemsReason reason) {
		for(GSMItem item : items) {
			item.giveItem(p, reason);
		}
	}


	public void playerClicked(PlayerInteractEvent e, boolean allow_left_click) {
		if(e.getItem() != null) {
			boolean click_left = e.getAction() == Action.LEFT_CLICK_AIR || e.getAction() == Action.LEFT_CLICK_BLOCK;
			boolean click_right = e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK;
			if (click_right || (click_left && allow_left_click)){

				for(GSMItem item : items){
					if(item.playerClicked(plugin, e)){
						return;
					}

				}
			}
		}
	}

	public boolean isShopItem(ItemStack s) {
		if (s == null) {
			return false;
		}

		for(GSMItem item : items) {
			if(item.isCorrespondingItem(s)) {
				return true;
			}
		}
		return false;
	}

	public GSMItem getItemByName(String name) {
		for(GSMItem item : items) {
			if(item.getPath().equalsIgnoreCase(name)) {
				return item;
			}
		}
		for(GSMItem item : items) {
			if(item.getPath().toLowerCase().startsWith(name.toLowerCase())) {
				return item;
			}
		}
		return null;
	}

}