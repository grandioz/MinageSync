package fr.grandoz.minage.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import fr.grandoz.minage.Main;
import fr.grandoz.minage.inventories.ClickableItem;
import fr.grandoz.minage.inventories.Inventory;
import fr.grandoz.minage.inventories.InventoryProvider;
import fr.grandoz.minage.inventories.ItemBuilder;

public class Deposit implements InventoryProvider {
	boolean IsMinage = Main.get().getDepositmanager().isIsMinage();
	@Override
	public String title(Inventory inv) {
		if(IsMinage) {
			return "§6§lDéposez vos minerais";
		}
		return "§6§lRécupérez vos minerais";
	}

	@Override
	public int rows(Inventory inv) {
		// TODO Auto-generated method stub
		return 2	;
	}

	@Override
	public void init(Inventory inv) {
		int  i = 0;
		if(IsMinage) {
			inv.setHasWhite(true);
			for(ItemStack stack : Main.get().getDepositmanager().getData().get(inv.getPlayer().getName())) {
				inv.set(i, ClickableItem.of(stack));
				i++;
			}
		}else {
			for(ItemStack stack : Main.get().getDepositmanager().getData().get(inv.getPlayer().getName())) {
				inv.set(i, ClickableItem.of(ItemBuilder.of(stack.getType(), stack.getAmount()).lore(Arrays.asList("§aClic pour récupérer cet item")).build() ,e->{
					Player p = (Player) e.getWhoClicked();
					if(hasAvaliableSlot(p)) {
						p.getInventory().addItem(ItemBuilder.of(e.getCurrentItem().getType() , e.getCurrentItem().getAmount()).build());
						inv.set(e.getSlot(), ClickableItem.of(ItemBuilder.of(org.bukkit.Material.AIR).build()));
					}else {
						p.sendMessage("§cVotre inventaire est plein !");

					}
				}));
				i++;	
			}
		}

	}

	@Override
	public void onClose(InventoryCloseEvent e, Inventory inv) {
		List<ItemStack> stacks = new ArrayList<>();
		for(ItemStack stack : Arrays.asList(inv.getBukkitInventory().getContents())){
			if(stack!=null)stacks.add(stack);

		}


		Main.get().getDepositmanager().getData().put(e.getPlayer().getName(), stacks.toArray(new ItemStack[stacks.size()]));
	}

	@Override
	public void update(Inventory inv) {


	}
	@Override
	public List<Integer> excluseCases(Inventory inv) {
		List<Integer> list = new ArrayList<>();
		if(!Main.get().getDepositmanager().isIsMinage()) {
			return list;
		}
		for(int i = 0 ; i<=54 ; i++) {
			list.add(i);
		}
		return list;
		
	}
	public boolean hasAvaliableSlot(Player player){
		org.bukkit.inventory.Inventory inv = player.getInventory();
		for (ItemStack item: inv.getContents()) {
			if(item == null) {
				return true;
			}
		}
		return false;
	}
}
