package adventuregame;

import java.util.ArrayList;

public class Player {
	static int inventoryLimit = -1;
	static ArrayList<Item> inventory = new ArrayList<>();
	public static boolean inventoryFull() {
		return inventoryLimit != -1 && inventory.size() >= inventoryLimit;
	}
	public static boolean hasLight() {
		for(Item item : inventory) {
			if (item.lightsource)
				return true;
		}
		
		return false;
	}
}
