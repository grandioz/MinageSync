package fr.grandoz.minage;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.grandoz.minage.interfaces.Deposit;
import fr.grandoz.minage.inventories.ItemBuilder;
import redis.clients.jedis.Jedis;

public class DepositCmd implements CommandExecutor {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg, String[] args) {



		if(args.length !=0 && args[0].equalsIgnoreCase("reload")) {
			Main.get().onDisable();
			Main.get().onEnable();
			sender.sendMessage("§6§lMinage>> §cReload complete");
			return true;
		}

		if(!(sender instanceof Player)) {
			sender.sendMessage("Cette commande ne peut être executée par un joueur");
			return true;
		}

		if(!Main.get().getConf().canRun()) {
			sender.sendMessage("§cCette command a été désactivée par un Admin");
			return false;
		}

		Player p = (Player)sender;
		p.sendMessage("§6§lMinage>> §7Vous venez d'ouvrir votre réserve");
		if(!Main.get().getDepositmanager().getData().containsKey(p.getName())) {
			Jedis jedis = Main.get().getJedis();
			if(jedis.exists(getKeyName(p.getName()))) {
				List<String> list = jedis.lrange(getKeyName(p.getName()), 0,27);
				List<ItemStack> stacks = new ArrayList<>();
				for(String str : list) {
					String split[] = str.split(":");
					if(split.length == 2) {
						String id = split[0];
						String num = split[1];


						stacks.add(ItemBuilder.of(Material.getMaterial(Integer.parseInt(id)), Integer.parseInt(num)).build());
					}

				}
				jedis.del(getKeyName(p.getName()));
				Main.get().getDepositmanager().getData().put(p.getName(), stacks.stream().toArray( n -> new ItemStack[n]));
			}else {
				Main.get().getLogger().info("Getting from SQL");
				Main.get().getSql().createProfile(p);
				Main.get().getDepositmanager().getData().put(p.getName(), Main.get().getSql().getInv(p));
			}
		}
		Main.get().getInvmanager().openInventory(new Deposit(),(Player)sender , i->{

		});
		return false;
	}

	public String getKeyName(String str) {
		StringBuilder builder = new StringBuilder("invs-");
		builder.append(str);
		return builder.toString();
	}

}
