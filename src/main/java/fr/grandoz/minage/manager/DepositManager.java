package fr.grandoz.minage.manager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class DepositManager {

	private HashMap<String, ItemStack[]> data;
	private List<Material> white;
	private boolean IsMinage = false;
	public DepositManager() {
		this.data = new HashMap<>();
		this.white = new ArrayList<>();
	}
	
	
	public HashMap<String, ItemStack[]> getData() {
		return data;
	}

	public void setData(HashMap<String, ItemStack[]> data) {
		this.data = data;
	}


	public boolean isIsMinage() {
		return IsMinage;
	}


	public void setIsMinage(boolean isMinage) {
		IsMinage = isMinage;
	}


	public List<Material> getWhite() {
		return white;
	}


	public void setWhite(List<Material> white) {
		this.white = white;
	}
	
	
	
	
}
