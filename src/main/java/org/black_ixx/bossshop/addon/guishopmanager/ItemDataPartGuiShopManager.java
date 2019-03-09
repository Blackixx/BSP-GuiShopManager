package org.black_ixx.bossshop.addon.guishopmanager;

import java.util.ArrayList;
import java.util.List;

import org.black_ixx.bossshop.core.BSBuy;
import org.black_ixx.bossshop.managers.item.ItemDataPart;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ItemDataPartGuiShopManager extends ItemDataPart {

	@Override
	public String[] createNames() {
		return new String[]{"guishopmanager", "gsm"};
	}

	@Override
	public int getPriority() {
		return PRIORITY_MOST_EARLY;
	}

	@Override
	public boolean removeSpaces() {
		return false;
	}

	@Override
	public ItemStack transform(ItemStack item, String used_name, String argument) {
		ItemStack gi = GuiShopManager.gsm.getGSMItems().getItemByName(argument).getItemStack();
		if(gi != null){
			return gi;
		}
		return item;
	}

	@Override
	public List<String> read(ItemStack i, List<String> output) {
		for(GSMItem item : GuiShopManager.gsm.getGSMItems().getItems()){
			if(item.isCorrespondingItem(i)){
				ArrayList<String> list = new ArrayList<String>();
				list.add("guishopmanager:"+item.getPath());
				return list;
			}
		}
		return output;
	}

	@Override
	public boolean isSimilar(ItemStack shop_item, ItemStack player_item, BSBuy buy, Player p) {
		return true;
	}

}
