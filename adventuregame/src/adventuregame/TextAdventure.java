package adventuregame;

import java.util.ArrayList;
import java.util.Scanner;

public class TextAdventure {

	static ArrayList<Item> inventory = new ArrayList<>();
	
	public static void main(String[] args) {
		Map.init();
		Map.currentroom.print();

		String userinput = "";
		Scanner scan = new Scanner(System.in);
		while (!userinput.equals("quit")) {
			System.out.print("> ");
			userinput = scan.nextLine();
			userinput = fixup(userinput);
			if (userinput.isEmpty()) {
				continue;
			}
            if (userinput.equals("help")) {
            	doHelp();
            	continue;
            }
			if (userinput.equals("look")) {
				Map.currentroom.print();
				continue;
			}

			if (userinput.startsWith("unlock ") || userinput.startsWith("open ")) {
				doUnlock(userinput);
				continue;
			}
			
			Item takeItem = Map.currentroom.TakeItem(userinput);
			if (takeItem != null) {
				doTake(takeItem);
				continue;
			}
			
			Item dropItem = DropItem(userinput);
			if (dropItem != null) {
				doDropItem(dropItem);
				continue;
			}
			
			Room newRoom = Map.currentroom.ProcessDirection(userinput);
			if (newRoom != null) {
				Map.currentroom = newRoom;
				newRoom.print();
				continue;
			}
			if (userinput.equals("inventory")) {
				doInventory();
				continue;
			}
			if (isDirection(userinput)) {
				System.out.println("You can't go " + userinput + "!");
				continue;
			}
			if (userinput.startsWith("examine ")) {
				boolean founditem = false;
				founditem = doExamine(userinput, founditem);
				if (founditem) {
					continue;
				} else {
					System.out.println("You don't see that here.");
					continue;
				}
				
			}
			System.out.println("The command '" + userinput +"' is not recognized.");
		}
		scan.close();
	}

	private static void doHelp() {
		System.out.println("Help");
		System.out.println("Travel by typing directions (north, south, east, west, up, down) or their abbreviated counterparts (n, s, e, w, u, d).");
		System.out.println("Pick up items by typing 'take [item]' and drop them with 'drop [item]'.");
		System.out.println("Examine your surroundings using 'look' or 'l', and examine items with 'examine [item]' or 'x [item]'. Many things in a room can be examined, and some objects may reveal hints, so be observant!");
	}

	private static boolean doExamine(String userinput, boolean founditem) {
		String itemtoexamine = userinput.substring(8);
		founditem = examineItem(founditem, itemtoexamine, inventory);
		founditem = examineItem(founditem, itemtoexamine, Map.currentroom.items);
		founditem = examineItem(founditem, itemtoexamine, Map.currentroom.details);
		return founditem;
	}

	private static boolean examineItem(boolean founditem, String itemtoexamine, ArrayList<Item> items) {
		if (founditem) {
			return founditem;
		}
		Item itemFound = getitembyname(itemtoexamine, items);
		if (itemFound != null) {
			System.out.println(itemFound.description);
		}
		return itemFound != null;
	}

	private static Item getitembyname(String itemtofind, ArrayList<Item> items) {
		for(Item item : items) {
			if (item.match(itemtofind)) {
				return item;
			}
		}
		return null;
	}

	private static void doTake(Item takeItem) {
		Map.currentroom.items.remove(takeItem);
		inventory.add(takeItem);
		System.out.println("You have succesfully swindled " + takeItem.name + ".");
	}

	private static void doUnlock(String userinput) {
		int firstSpace = userinput.indexOf(" ");
		String itemtounlock = userinput.substring(firstSpace + 1);
		boolean foundthingtounlock = false;
		Item thingtounlock = null;
		for(Item item : Map.currentroom.details) {
			if (foundthingtounlock) {
				break;
			}
			if (item.match(itemtounlock)) {
					foundthingtounlock = true;
					thingtounlock = item;
					break;
			}
		}
		if (foundthingtounlock) {
			if (inventory.contains(thingtounlock.key)) {
				Map.currentroom.details.remove(thingtounlock);
				Map.currentroom.addexit(thingtounlock.directiononunlock.exitname, thingtounlock.directiononunlock.room, Room.Special.AUTO_CREATE_REVERSE_ROOM);
				thingtounlock.directiononunlock.room.desc += " You can go " + Direction.opposite(thingtounlock.directiononunlock.exitname);
				thingtounlock.locked = false;
				System.out.println(thingtounlock.unlocktext);
			} else {
				System.out.println("You don't have the key.");
			}
		} else {
			System.out.println("You can't unlock " + thingtounlock + ".");
		}
	}

	private static void doDropItem(Item dropItem) {
		Map.currentroom.items.add(dropItem);
		inventory.remove(dropItem);
		System.out.println("You have succesfully de-swindled " + dropItem.name + ".");
	}

	private static void doInventory() {
		System.out.println("You are carrying:");
		for (Item item : inventory) {
			System.out.println(" - " + item.name);
		}
		if (inventory.isEmpty()) {
			System.out.println("Nothing! You're poor!");
		}
	}

	private static boolean isDirection(String userinput) {
		if (userinput.equals(Direction.opposite(userinput))) {
			return false;
		} else {
			return true;
		}
	}

	static String fixup(String in) {
		in = in.trim().toLowerCase();
		if (in.equals("q")) {
			return "quit";
		} else if (in.equals("n")) {
			return "north";
		} else if (in.equals("s")) {
			return "south";
		} else if (in.equals("e")) {
			return "east";
		} else if (in.equals("w")) {
			return "west";
		} else if (in.equals("l") || in.equals("x")) {
			return "look";
		} else if (in.equals("i")) {
			return "inventory";
		}
		if (in.startsWith("x ")) {
			return "examine " + in.substring(2);
		}
		return in;
	}
	
	public static Item DropItem(String userinput) {
		if (userinput.startsWith("drop ") && !inventory.isEmpty()) {
			String itemtotake = userinput.substring(5);
			for(Item item : inventory) {
				if (item.match(itemtotake)) {
					return item;
				}
			}
		}
		return null;
	}
	
}
