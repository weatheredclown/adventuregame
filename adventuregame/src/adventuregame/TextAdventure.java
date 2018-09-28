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
            if (Map.currentroom.processTriggers(userinput)) {
            	continue;
            }
			if (userinput.equals("look")) {
				Map.currentroom.print();
				continue;
			}

			if (userinput.startsWith("put ")) {
				doPut(userinput);
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
				Map.doNavigate(newRoom);
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

	// Some day:
	// MatchResult matchResult = stringMatch("take %s(room)");
	// MatchResult matchResult = stringMatch("put %s(inventory) in %o(*)");
	// if (matchResult.found()) {
	//   print(matchResult.s.getName());
	//   print(matchResult.o.getName());

	private static void doPut(String userinput) { // input: 'put coin in chest'
		String itemname = userinput.substring(4); // after 'put '
		Item.TokenMatch subject = getitembyname(itemname, inventory, true); // finds item for 'coin in chest'
		if (subject.found()) { // found 'coin' object
			String directObjectName = itemname.substring(subject.tokenFound.length() + 1);  // 'in chest'
			if (directObjectName.startsWith("in ")) {
				Item directObject = findItem(directObjectName.substring(3));
				if (directObject != null) {
					System.out.println("You put " + subject.item.getName() + " into " + directObject.getName() + ".");
					inventory.remove(subject.item);
					directObject.contents.add(subject.item);
				}
			}
		}
	}

	private static void doDeath() {
		Map.init();
	}

	private static void doHelp() {
		System.out.println("Help");
		System.out.println("Travel by typing directions (north, south, east, west, up, down) or their abbreviated counterparts (n, s, e, w, u, d).");
		System.out.println("Pick up items by typing 'take [item]' and drop them with 'drop [item]'.");
		System.out.println("Examine your surroundings using 'look' or 'l', and examine items with 'examine [item]' or 'x [item]'. Many things in a room can be examined, and some objects may reveal hints, so be observant!");
	}

	private static boolean doExamine(String userinput, boolean founditem) {
		String itemtoexamine = userinput.substring(8);
		Item item = findItem(itemtoexamine);
		if (item != null) {
			System.out.println(item.description);
		}
		return item != null;
	}

	static Item.TokenMatch getitembyname(String itemtofind, ArrayList<Item> items, boolean allowPartialMatch) {
		for(Item item : items) {
			Item.TokenMatch match = item.match(itemtofind, allowPartialMatch);
			if (match.found()) {
				return match;
			}
			if (item.open) {
				Item.TokenMatch contenttoken = getitembyname(itemtofind, item.contents, allowPartialMatch);
				if (contenttoken.found()) {
					return contenttoken;
				}
			}
		}
		return new Item.TokenMatch();
	}

	static Item.TokenMatch getitembyname(String itemtofind, ArrayList<Item> items) {
		return getitembyname(itemtofind, items, false);
	}

	private static void doTake(Item takeItem) {
		ArrayList<Item> location = findContainer(takeItem);
		if (takeItem.fixed || location == null) {
			System.out.println("Sorry, you can't pick that up.");
			return;
		}

		location.remove(takeItem);
		inventory.add(takeItem);
		Map.currentroom.onTakeItem(takeItem);
		System.out.println("You have succesfully swindled " + takeItem.getName() + ".");
	}

	static ArrayList<Item> findContainer(Item takeItem) {
		if (Map.currentroom.items.contains(takeItem)) {
			return Map.currentroom.items;
		}
		ArrayList<Item> location = null;
		if (location == null) {
			location = findContainer(takeItem, Map.currentroom.items);
		}
		if (location == null) {
			location = findContainer(takeItem, Map.currentroom.details);
		}
		if (location == null) {
			location = findContainer(takeItem, inventory);
		}
		return location;
	}
	
	private static ArrayList<Item> findContainer(Item takeItem, ArrayList<Item> location) {
		for (Item item : location) {
			if (item.contents.contains(takeItem)) {
				return item.contents;
			}
		}
		return null;
	}

	private static void doClose(String userinput) {
		int firstSpace = userinput.indexOf(" ");
		String itemtoclose = userinput.substring(firstSpace + 1);
		Item thingtoclose = findItem(itemtoclose);
		
		if (thingtoclose != null && thingtoclose.openable) {
			if (thingtoclose.open) {
				thingtoclose.open = false;
				Map.currentroom.onCloseItem(thingtoclose);
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

		Item thingtounlock = findItem(itemtounlock);
		
		if (thingtounlock != null) {
			if (thingtounlock.locked) {
				if (inventory.contains(thingtounlock.key)) {
					Map.currentroom.details.remove(thingtounlock);
					Map.currentroom.addexit(thingtounlock.directiononunlock.exitname, thingtounlock.directiononunlock.room, Room.Special.AUTO_CREATE_REVERSE_ROOM);
					thingtounlock.directiononunlock.room.desc += " You can go " + Direction.opposite(thingtounlock.directiononunlock.exitname);
					thingtounlock.locked = false;
					Map.currentroom.onUnlockItem(thingtounlock);
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
					Map.currentroom.onOpenItem(thingtounlock);
				}
				
			} else {
				System.out.println("That doesn't open.");
			}
		} else {
			System.out.println("You can't unlock that.");
		}
	}

	private static Item findItem(String itemname) {
		return findItemToken(itemname, false).item;
	}

	private static Item.TokenMatch findItemToken(String itemtounlock, boolean allowPartialMatch) {
		Item.TokenMatch thingtounlock = getitembyname(itemtounlock, Map.currentroom.details, allowPartialMatch);
		if (!thingtounlock.found()) {
			thingtounlock = getitembyname(itemtounlock, Map.currentroom.items, allowPartialMatch);
		}
		if (!thingtounlock.found()) {
			thingtounlock = getitembyname(itemtounlock, inventory, allowPartialMatch);
		}
		return thingtounlock;
	}

	private static void doDropItem(Item dropItem) {
		Map.currentroom.items.add(dropItem);
		inventory.remove(dropItem);
		Map.currentroom.onDropItem(dropItem);
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
		} else if (in.equals("ne")) {
			return "northeast";
		} else if (in.equals("se")) {
			return "southeast";
		} else if (in.equals("sw")) {
			return "southwest";
		} else if (in.equals("nw")) {
			return "northwest";
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
				if (item.match(itemtotake).found()) {
					return item;
				}
			}
		}
		return null;
	}
}
