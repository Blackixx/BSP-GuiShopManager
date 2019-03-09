package org.black_ixx.bossshop.addon.guishopmanager;

import java.util.ArrayList;
import java.util.List;

import org.black_ixx.bossshop.api.BSAddonConfig;
import org.black_ixx.bossshop.api.BossShopAddonConfigurable;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;

public class GuiShopManager extends BossShopAddonConfigurable {

	public static GuiShopManager gsm;

	private GSMItems items;
	private PlayerListener listener;


	public GuiShopManager(){
		gsm = this;
	}

	public GSMItems getGSMItems() {
		return this.items;
	}

	public void reload()  {
		getAddonConfig().reload();
		addDefaultConfig();
		this.items.reload(this);
		this.listener.reload(this);
	}

	public void bossShopReloaded(CommandSender sender){
		reload();
	}

	public void disableAddon() {
	}
	@Override
	public void bossShopFinishedLoading() {		
	}

	public void enableAddon() {
		reloadConfig();
		addDefaultConfig();

		this.items = new GSMItems(this);
		this.listener = new PlayerListener(this);
		getServer().getPluginManager().registerEvents(this.listener, this);

		getCommand("gsm").setExecutor(new Commander(this));
	}

	public String getAddonName(){
		return "GuiShopManager";
	}

	public String getRequiredBossShopVersion(){
		return "1.6.8";
	}

	private void addDefaultConfig() {
		BSAddonConfig ac = getAddonConfig();
		FileConfiguration c = getConfig();


		if (getConfig().getString("CreationVersion") == null) {
			List<String> compass = new ArrayList<String>();
			compass.add("name:&aQuick Warp &7(Right Click) &6[x]");
			compass.add("type:COMPASS");
			compass.add("amount:1");

			List<String> book = new ArrayList<String>();
			book.add("name:&6[o] &4&lBossShop &r&6Menu &6[o]");
			book.add("lore:&7Right Click to open the Menu!");
			book.add("type:BOOK");
			book.add("amount:1");

			List<String> feather = new ArrayList<String>();
			feather.add("name:&e&lRight Click to &2&l&ntoggle Fly");
			feather.add("lore:&8A command example.");
			feather.add("type:FEATHER");
			feather.add("amount:1");
			feather.add("enchantment:DURABILITY#1");
			feather.add("hideflag:all");

			List<String> command_commands = new ArrayList<String>();
			command_commands.add("fly %player%");

			c.set("Items.BossShopMenu.Look", book);
			c.set("Items.BossShopMenu.GiveOnJoin", Boolean.valueOf(true));
			c.set("Items.BossShopMenu.InventoryLocation", Integer.valueOf(1));
			c.set("Items.BossShopMenu.OpenShop", "menu");

			c.set("Items.Servers.Look", compass);
			c.set("Items.Servers.GiveOnJoin", Boolean.valueOf(true));
			c.set("Items.Servers.InventoryLocation", Integer.valueOf(9));
			c.set("Items.Servers.OpenShop", "bungeecordservers");
			c.set("Items.Servers.World", "spawn:hub:world1");

			c.set("Items.Command.Look", feather);
			c.set("Items.Command.GiveOnJoin", Boolean.valueOf(true));
			c.set("Items.Command.InventoryLocation", Integer.valueOf(5));
			c.set("Items.Command.Command", command_commands);
		}


		ac.addDefault("CreationVersion", getDescription().getVersion());
		ac.addDefault("Settings.JoinDelay", -1);
		ac.addDefault("Settings.AllowPlaceItems", Boolean.valueOf(false));
		ac.addDefault("Settings.AllowMoveItems", Boolean.valueOf(false));
		ac.addDefault("Settings.AllowDropItems", Boolean.valueOf(false));
		ac.addDefault("Settings.AcceptLeftClick", Boolean.valueOf(false));
		ac.addDefault("Settings.DropItemsOnDeath", Boolean.valueOf(false));
		ac.addDefault("Settings.GetItemsOnRespawn", Boolean.valueOf(true));
		ac.addDefault("Settings.ClearInvOnJoin", Boolean.valueOf(false));
		ac.addDefault("Settings.ClearInvOnWorldChange", Boolean.valueOf(false));
		getAddonConfig().save();
	}

	@Override
	public boolean saveConfigOnDisable() {
		return false;
	}

}