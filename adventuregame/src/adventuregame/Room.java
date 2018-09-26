package adventuregame;

import java.util.ArrayList;

public class Room {
	enum special {
		NONE,
		AUTO_CREATE_REVERSE_ROOM;
	}

	String name;
	String desc;
	
	ArrayList<Direction> directions = new ArrayList<>();
	
	ArrayList<Item> items = new ArrayList<>();
	ArrayList<Item> details = new ArrayList<>();
	
	Room(String n, String d) {
		name = n;
		desc = d;
	}
	public void print() {
		System.out.printf("%s\n%s", name, desc);
		for (Item detail : details) {
			if (detail.locked) {
				if (detail.lockedtext != null && !detail.lockedtext.isEmpty()) {
					System.out.print(detail.lockedtext);
				}
			} else {
				if (detail.exitappend != null && !detail.exitappend.isEmpty()) {
					System.out.print(detail.exitappend);
				}
			}
		}
		System.out.print("\n");
		if (!items.isEmpty()) {
			System.out.println("In this room you see: ");
			for (Item item : items) {
				System.out.println(" - " + item.name);
			}
		}
	}
	
	public void addexit(String string, Room r2) {
		addexit(string, r2, special.NONE);
	}
	
	public void addexit(String exitname, Room r2, special s) {
		directions.add(new Direction(exitname, r2));
		if (s == special.AUTO_CREATE_REVERSE_ROOM) {
			r2.directions.add(new Direction(Direction.opposite(exitname), this));
		}

		
	}
	public Room ProcessDirection(String userinput) {
		for (Direction direction : directions) {
			if (userinput.equals(direction.exitname)) {
				return direction.room;
			}
		}
		return null;
	}
	public void additem(Item item) {
		items.add(item);
	}

	public Item TakeItem(String userinput) {
		if (userinput.startsWith("take ") && !items.isEmpty()) {
			String itemtotake = userinput.substring(5);
			for(Item item : items) {
				if (item.match(itemtotake)) {
					return item;
				}
			}
		}
		return null;
	}
	
}
