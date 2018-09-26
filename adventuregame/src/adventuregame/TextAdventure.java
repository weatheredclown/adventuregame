package adventuregame;

import java.util.ArrayList;
import java.util.Scanner;

public class TextAdventure {

	static ArrayList<Item> inventory = new ArrayList<>();
	
	public static void main(String[] args) {
		doDeath();

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
			
			if (userinput.startsWith("close ")) {
				doClose(userinput);
				continue;
			}
			
			Item takeItem = Map.currentroom.TakeItem(userinput);
			if (takeItem != null) {
				doTake(takeItem);
				continue;
			}
			
			if (userinput.equals("restart")) {
				doDeath();
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

	private static void doDeath() {
		Map.init();
		Map.currentroom.print();
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

	static Item getitembyname(String itemtofind, ArrayList<Item> items) {
		for(Item item : items) {
			if (item.match(itemtofind)) {
				return item;
			}
			if (item.open) {
				Item content = getitembyname(itemtofind, item.contents);
				if (content != null) {
					return content;
				}
			}
		}
		return null;
	}

	private static void doTake(Item takeItem) {
		if (takeItem.fixed) {
			System.out.println("Sorry, you can't pick that up.");
			return;
		}
		Map.currentroom.items.remove(takeItem);
		inventory.add(takeItem);
		System.out.println("You have succesfully swindled " + takeItem.getName() + ".");
	}

	private static void doClose(String userinput) {
		int firstSpace = userinput.indexOf(" ");
		String itemtoclose = userinput.substring(firstSpace + 1);
		Item thingtoclose = getitembyname(itemtoclose, Map.currentroom.details);
		if (thingtoclose == null) {
			thingtoclose = getitembyname(itemtoclose, Map.currentroom.items);
		}
		if (thingtoclose == null) {
			thingtoclose = getitembyname(itemtoclose, inventory);
		}
		
		if (thingtoclose != null && thingtoclose.openable) {
			if (thingtoclose.open) {
				thingtoclose.open = false;
				System.out.println("Closed.");
			} else {
				System.out.println("That's already closed.");
			}
		} else {
			System.out.println("You can't close that.");
		}
	}
	
	private static void doUnlock(String userinput) {
		int firstSpace = userinput.indexOf(" ");
		String itemtounlock = userinput.substring(firstSpace + 1);

		Item thingtounlock = getitembyname(itemtounlock, Map.currentroom.details);
		if (thingtounlock == null) {
			thingtounlock = getitembyname(itemtounlock, Map.currentroom.items);
		}
		if (thingtounlock == null) {
			thingtounlock = getitembyname(itemtounlock, inventory);
		}
		
		if (thingtounlock != null) {
			if (thingtounlock.locked) {
				if (inventory.contains(thingtounlock.key)) {
					Map.currentroom.details.remove(thingtounlock);
					Map.currentroom.addexit(thingtounlock.directiononunlock.exitname, thingtounlock.directiononunlock.room, Room.Special.AUTO_CREATE_REVERSE_ROOM);
					thingtounlock.directiononunlock.room.desc += " You can go " + Direction.opposite(thingtounlock.directiononunlock.exitname);
					thingtounlock.locked = false;
					System.out.println(thingtounlock.unlocktext);
				} else {
					System.out.println("You don't have the key.");
				}
			} else if (thingtounlock.openable) {
				if (thingtounlock.open) {
					System.out.println("That's already open.");
				} else {
					System.out.println("You open it.");
					thingtounlock.open = true;
				}
				
			} else {
				System.out.println("That doesn't open.");
			}
		} else {
			System.out.println("You can't unlock that.");
		}
	}

	private static void doDropItem(Item dropItem) {
		Map.currentroom.items.add(dropItem);
		inventory.remove(dropItem);
		System.out.println("You have succesfully de-swindled " + dropItem.getName() + ".");
	}

	private static void doInventory() {
		System.out.println("You are carrying:");
		for (Item item : inventory) {
			System.out.println(" - " + item.getName());
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
