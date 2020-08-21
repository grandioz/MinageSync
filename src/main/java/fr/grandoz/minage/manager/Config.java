package fr.grandoz.minage.manager;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;

import fr.grandoz.minage.Main;

public class Config {
	public final boolean isMainage, canRun;
	public final String db, address, user, redisHost, redisPass, pass;

	public Config(Main plugin) {
		FileConfiguration conf = plugin.getConfig();
		plugin.reloadConfig();
		plugin.saveDefaultConfig();
//
		this.isMainage = conf.getBoolean("Minage");
		this.db = conf.getString("mysql.base");
		this.address = conf.getString("mysql.address");
		this.user = conf.getString("mysql.user");
		this.redisHost = conf.getString("redis.host");
		this.redisPass = conf.getString("redis.mdp");
		this.pass = conf.getString("mysql.mdp");
		this.canRun = conf.getBoolean("command", true);

		loadWhite();
	}

	public void loadWhite() {
		List<Integer> list = Main.get().getConfig().getIntegerList("white");
		List<Material> white = new ArrayList<>();
		for(int id : list) {
			white.add(Material.getMaterial(id));
		}
		Main.get().getDepositmanager().setWhite(white);
	}

	public boolean isMainage() {
		return isMainage;
	}

	public boolean canRun() {
		return canRun;
	}

	public String getDb() {
		return db;
	}

	public String getAddress() {
		return address;
	}

	public String getUser() {
		return user;
	}

	public String getRedisHost() {
		return redisHost;
	}

	public String getRedisPass() {
		return redisPass;
	}

	public String getPass() {
		return pass;
	}
}
