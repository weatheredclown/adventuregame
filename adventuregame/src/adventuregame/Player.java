package adventuregame;

import java.util.ArrayList;

public class Player {
	static int inventoryLimit = -1;
	static ArrayList<Item> inventory = new ArrayList<>();
	static ArrayList<Trigger> triggers = new ArrayList<>();
	
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
	public static boolean processTriggers(String userinput) {
		for (Trigger trigger : triggers) {
			if (trigger.process(userinput)) {
				return true;
			}
		}
		return false;
	}
	static public void addSimpleTrigger(String string, String string2) {
		triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq(string))
				.addAction(Trigger.createMessageAction(string2)));
	}
	public static void addTrigger(Trigger trigger) {
		triggers.add(trigger);		
	}
}
