/**
 * 
 */
package fr.grandoz.minage.inventories;

import java.lang.reflect.Field;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Damageable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.SkullMeta;


public class ItemBuilder {
	private Material m;
	private int count;
	private int damage;
	private String name;
	private List<String> lores;
	private String texture;
	int data=0;

	private ItemBuilder() {
	}

	public static ItemBuilder of(Material m) {
		return of(m, 1);
	}

	public static ItemBuilder of(Material m, int count) {
		return of(m, count, (short) 0);
	}

	public static ItemBuilder of(Material m, int count, int data) {
		ItemBuilder ib = new ItemBuilder();
		ib.m = m;
		ib.count = count;
		ib.data=data;
		return ib;
	}

	public ItemBuilder name(String name) {
		this.name = name;
		return this;
	}

	public ItemBuilder lore(List<String> lores) {
		this.lores = lores;
		return this;
	}

	public ItemBuilder texture(String texture) {
		this.texture = texture;
		return this;
	}

	public ItemBuilder data(int data) {
		this.data=data;
		return this;
	}
	public ItemStack build() {
		ItemStack is = new ItemStack(m, count,(short)data);
		if (is instanceof Damageable) {
			Damageable d = (Damageable) is;
			d.damage(damage);
		}
		ItemMeta im = is.hasItemMeta() ? is.getItemMeta() : Bukkit.getItemFactory().getItemMeta(m);
		if (im != null) {
			im.setDisplayName(name);
			im.setLore(lores);
			is.setItemMeta(im);
		}

		return is;
	}
}
