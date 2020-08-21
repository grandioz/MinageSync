package fr.grandoz.minage;


import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;

import redis.clients.jedis.Jedis;

public class Listener implements org.bukkit.event.Listener {

	@EventHandler
	public void onQuit(PlayerQuitEvent event) {
		if(Main.get().getDepositmanager().getData().containsKey(event.getPlayer().getName())) {
			Jedis jedis = Main.get().getJedis();
			Player player = event.getPlayer();
			ItemStack stacks[] = Main.get().getDepositmanager().getData().get(player.getName());
			List<String> list = new ArrayList<>();
			if(stacks !=null) {
				for(ItemStack stack : stacks) {
					if(stack!=null) {
						String str = stack.getType().getId()+":"+stack.getAmount();
						list.add(str);

					}
				}
				if(jedis.exists(getKeyName(player.getName()))) jedis.del(getKeyName(player.getName()));
				if(list.size()!=0) {
					jedis.lpush(getKeyName(player.getName()), list.stream().toArray( n -> new String[n]));
				}else {
					jedis.lpush(getKeyName(player.getName()), "");
				}


			}
			Main.get().getDepositmanager().getData().remove(event.getPlayer().getName());
		}
	}

	public String getKeyName(String str) {
		StringBuilder builder = new StringBuilder("invs-");
		builder.append(str);
		return builder.toString();
	}

}
