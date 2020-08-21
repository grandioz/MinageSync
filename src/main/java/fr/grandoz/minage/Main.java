package fr.grandoz.minage;

import fr.grandoz.minage.inventories.InventoryManager;
import fr.grandoz.minage.manager.Config;
import fr.grandoz.minage.manager.DepositManager;
import fr.grandoz.minage.manager.SqlConnection;

import org.bukkit.plugin.java.JavaPlugin;
import redis.clients.jedis.Jedis;


public class Main extends JavaPlugin {
	private InventoryManager invmanager;
	private DepositManager depositmanager;
	private static Main main;
	private SqlConnection sql;
	private Config conf;
	private Jedis jedis;

	@Override
	public void onEnable() {
		main = this;

		invmanager = new InventoryManager();
		invmanager.init();

		depositmanager = new DepositManager();

		conf = new Config(this);
		sql = new SqlConnection("jdbc:mysql://", conf.address, conf.db, conf.user, conf.pass);

		this. jedis = new Jedis(conf.redisHost);
		System.out.println(jedis.isConnected());
		System.out.println(conf.redisHost);



		getServer().getPluginManager().registerEvents(new Listener(), main);

		getCommand("reserve").setExecutor(new DepositCmd());

	}

	@Override
	public void onDisable() {
		sql.updateAllPlayerInv();
		this.jedis.disconnect();
		this.sql.disconnect();
	}

	public InventoryManager getInvmanager() {
		return invmanager;
	}

	public static Main get() {
		return main;
	}

	public DepositManager getDepositmanager() {
		return depositmanager;
	}

	public SqlConnection getSql() {
		return sql;
	}

	public Config getConf() {
		return conf;
	}

	public Jedis getJedis() {
		return jedis;
	}
}
