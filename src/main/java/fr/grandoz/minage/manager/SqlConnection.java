package fr.grandoz.minage.manager;


import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import fr.grandoz.minage.Main;
import redis.clients.jedis.Jedis;


public class SqlConnection {
	public Main main = Main.get();
	public Connection connx;
	String url , host,database,user,pass;
	public SqlConnection(String url , String host , String database, String user , String pass ) {
		this.url =url;
		this.host=host;
		this.database=database;
		this.user=user;
		this.pass=pass;
		connect();
		createTable();
	}

	public boolean IsConnected() {
		if(connx == null) {
			return false;
		}
		return true;
	}

	public void connect() {
		try {
			Class.forName("com.mysql.jdbc.Driver");
		} catch (ClassNotFoundException e1) {
			e1.printStackTrace();
		}
		try {
			connx = DriverManager.getConnection(url+host+"/" + database , user , pass);
		} catch (SQLException e) {
			Main.get().getLogger().info(Level.SEVERE + "La connection avec la dase de donné a échoué");
			e.printStackTrace();
			Main.get().getServer().getPluginManager().disablePlugin(Main.get());
		}
	}
	public void disconnect() {
		try {
			connx.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void updatePlayerInv(Player player) {
		try {
			PreparedStatement q = connx.prepareStatement("UPDATE invs SET inv = ? WHERE name = ?");
			ItemStack stacks[] = Main.get().getDepositmanager().getData().get(player.getName());
			String str = "";
			if(stacks !=null) {
				for(ItemStack stack : stacks) {
					if(stack!=null) {
						str =str + stack.getType().getId()+":"+stack.getAmount()+"!!";
					}
				}
			}
			q.setString(1, str);
			q.setString(2, player.getName());
			q.execute();
			q.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public void updateAllPlayerInv() {



		try {


			boolean isCache = false;
			boolean isRedis = false;
			String ex = "";

			Jedis jedis = main.getJedis();
			Set<String> keys = jedis.keys("*");
			String str1 = "";
			for(String key : keys) {
				if(key.contains("invs-")) {
					isRedis = true;
					List<String> list = new ArrayList<>();
					list = jedis.lrange(key, 0,27);
					System.out.println(list);
					for(String line : list) {
						str1 =str1+line+"!!";
					}
					ex = ex+ " WHEN \""+ key.split("-")[1]+  "\" THEN \""+str1+"\"";
					jedis.del(key);
				}

			}




			String str = "";


			for(String key : Main.get().getDepositmanager().getData().keySet()) {
				ItemStack stacks[] = Main.get().getDepositmanager().getData().get(key);

				if(stacks !=null) {
					isCache = true;
					for(ItemStack stack : stacks) {
						if(stack!=null) {
							str =str + stack.getType().getId()+":"+stack.getAmount()+"!!";
						}
					}
				}
				ex = ex+ " WHEN \""+ key+  "\" THEN \""+str+"\"";





			}
			if(isCache || isRedis) {
				PreparedStatement q = connx.prepareStatement( "UPDATE invs SET inv = Case name"+ex + "ELSE inv END");
				q.execute();
				q.close();
			}

		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}


	public ItemStack[] getInv(Player player) {
		List<ItemStack> stacks = new ArrayList<>();
		try {
			PreparedStatement q = connx.prepareStatement("SELECT inv FROM invs WHERE name = ?");
			q.setString(1, player.getName());
			ResultSet rs = q.executeQuery();
			String inv_str = "";
			while(rs.next()) {
				inv_str = rs.getString("inv");
			}
			if(!inv_str.contains(":"))return stacks.toArray(new ItemStack[stacks.size()]);
			String split1[] = inv_str.split("!!");
			for(String stacksam : split1) {
				if(stacksam !=null) {
					String split2[] = stacksam.split(":");
					Material mat = Material.getMaterial(Integer.parseInt(split2[0]));
					int quant = Integer.parseInt(split2[1]);
					stacks.add(new ItemStack(mat ,quant));
				}

			}
			q.close();


			return stacks.toArray(new ItemStack[stacks.size()]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return  stacks.toArray(new ItemStack[stacks.size()]);

	}
	public void createProfile(Player player) {
		if(!hasAccount(player)) {
			try {
				PreparedStatement q = connx.prepareStatement("INSERT INTO invs(name, inv) VALUES(?, ?)");
				q.setString(1 , player.getName());
				q.setString(2 , " ");
				q.execute();
				q.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}


	}



	public boolean hasAccount(Player player) {

		try {
			PreparedStatement q = connx.prepareStatement("SELECT inv FROM invs WHERE name = ?");
			q.setString(1, player.getName());
			q.executeQuery();
			ResultSet rs = q.executeQuery();
			boolean hasacc = rs.next();
			q.close();
			return hasacc;
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return false;
	}


	private void createTable(){
		String sqlCreate = "CREATE TABLE IF NOT EXISTS " + "invs"
				+ "  (name VARCHAR(200),"
				+ "inv VARCHAR(500))";

		Statement stmt = null;
		try {
			stmt = connx.createStatement();
		} catch (SQLException e) {
			e.printStackTrace();
		}
		try {
			stmt.execute(sqlCreate);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

}
