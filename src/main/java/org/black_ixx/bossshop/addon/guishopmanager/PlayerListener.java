package org.black_ixx.bossshop.addon.guishopmanager;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.black_ixx.bossshop.addon.guishopmanager.GSMItems.GSMGiveItemsReason;
import org.black_ixx.bossshop.events.BSRegisterTypesEvent;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.Event.Result;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

public class PlayerListener implements Listener, Reloadable {

	private GuiShopManager plugin;
	private boolean drop;
	private boolean move;
	private boolean place;
	private boolean death;
	private boolean respawn;
	private boolean accept_leftclick;
	private boolean clear_inv_on_join;
	private boolean clear_inv_on_world_change;
	private int join_delay;

	public PlayerListener(GuiShopManager plugin) {
		this.plugin = plugin;
		loadSettings(plugin);
	}
	@EventHandler
	public void onDeath(PlayerDeathEvent event) {
		if (!this.death){
			List<ItemStack> toRemove = new ArrayList<ItemStack>();

			for (ItemStack i : event.getDrops()){
				if (this.plugin.getGSMItems().isShopItem(i)) {
					toRemove.add(i);
				}

			}

			for (ItemStack i : toRemove) {
				event.getDrops().remove(i);
			}
			toRemove.clear();
		}
	}

	@EventHandler
	public void onRespawn(PlayerRespawnEvent event) {
		if (this.respawn) {
			this.plugin.getGSMItems().giveItems(event.getPlayer(), GSMGiveItemsReason.RESPAWN);
		}
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		if(clear_inv_on_join){
			event.getPlayer().getInventory().clear();
			event.getPlayer().updateInventory();
		}

		if(join_delay>0) {
			final UUID playerid = event.getPlayer().getUniqueId();
			new BukkitRunnable() {
				@Override
				public void run() {
					Player p = Bukkit.getPlayer(playerid);
					if(p != null){
						plugin.getGSMItems().giveItems(p, GSMGiveItemsReason.SERVER_JOIN);
					}
				}
			}.runTaskLater(plugin, join_delay);
		}else{
			this.plugin.getGSMItems().giveItems(event.getPlayer(), GSMGiveItemsReason.SERVER_JOIN);
		}

	}

	@EventHandler
	public void onWorldChange(PlayerChangedWorldEvent event) {
		if(clear_inv_on_world_change){
			event.getPlayer().getInventory().clear();
			event.getPlayer().updateInventory();
		}
		this.plugin.getGSMItems().giveItems(event.getPlayer(), GSMGiveItemsReason.WORLD_CHANGED);
	}

	@EventHandler
	public void onDrop(PlayerDropItemEvent event) {
		if ((!this.drop) && (this.plugin.getGSMItems().isShopItem(event.getItemDrop().getItemStack())))
			event.setCancelled(true);
	}

	@EventHandler
	public void onPlace(BlockPlaceEvent event) {
		if ((!this.place) && (this.plugin.getGSMItems().isShopItem(event.getItemInHand()))){
			event.setCancelled(true);
		}
	}

	@EventHandler
	public void onMove(InventoryClickEvent event) {
		if (!this.move) {
			if (event.getInventory() == null) {
				return;
			}
			if ((this.plugin.getGSMItems().isShopItem(event.getCurrentItem())) || (this.plugin.getGSMItems().isShopItem(event.getCursor()))){
				event.setCancelled(true);
				event.setResult(Result.DENY);
			}
		}
	}

	@EventHandler
	public void onInteract(PlayerInteractEvent event) {
		this.plugin.getGSMItems().playerClicked(event, accept_leftclick);
	}


	@EventHandler
	public void onRegister(BSRegisterTypesEvent event) {
		new ItemDataPartGuiShopManager().register();
	}

	public void reload(GuiShopManager plugin) {
		loadSettings(plugin);
	}

	private void loadSettings(GuiShopManager plugin) {
		ConfigurationSection s = plugin.getConfig().getConfigurationSection("Settings");

		this.drop = s.getBoolean("AllowDropItems");
		this.move = s.getBoolean("AllowMoveItems");
		this.place = s.getBoolean("AllowPlaceItems");
		this.accept_leftclick = s.getBoolean("AcceptLeftClick");
		this.death = s.getBoolean("DropItemsOnDeath");
		this.respawn = s.getBoolean("GetItemsOnRespawn");
		this.clear_inv_on_join = s.getBoolean("ClearInvOnJoin");
		this.clear_inv_on_world_change = s.getBoolean("ClearInvOnWorldChange");
		this.join_delay = s.getInt("JoinDelay");
	}
}