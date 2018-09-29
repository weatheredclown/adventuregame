package adventuregame;

import java.util.ArrayList;

public class Room {
	enum Special {
		NONE,
		NO_REVERSE_ROOM;
	}

	String name;
	String desc;
	public boolean hasEntered = false;
	public boolean dark = false;

	ArrayList<Direction> directions = new ArrayList<>();

	ArrayList<Item> items = new ArrayList<>();
	ArrayList<Trigger> triggers = new ArrayList<>();
	ArrayList<Item> details = new ArrayList<>();

	Room(String name, String desc) {
		this.name = name;
		this.desc = desc;
	}

	public void addexit(String string, Room r2) {
		addexit(string, r2, Special.NONE);
	}

	public void addexit(String exitname, Room destination, Special special) {
		directions.add(new Direction(exitname, destination));
		if (special != Special.NO_REVERSE_ROOM) {
			destination.directions.add(new Direction(Direction.opposite(exitname), this));
		}
	}

	public void additem(Item item) {
		items.add(item);
	}

	public void print() {
		System.out.println(name);
		if (isDark()) {
			System.out.println("It is dark here. You can't see anything.");
			return;
		}
		System.out.println(desc);
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
		if (!items.isEmpty()) {
			System.out.print("\n");
			System.out.println("In this room you see: ");
			for (Item item : items) {
				System.out.println(" - " + item.getName());
				if (item.open) {
					for (Item content : item.contents) {
						System.out.println("   - " + content.getName());
					}
				}
			}
		}
	}

	boolean isDark() {
		return dark && !Player.hasLight() && !Map.currentroom.hasLight();
	}

	private boolean hasLight() {
		for (Item item : items) {
			if (item.lightsource)
				return true;
		}

		return false;
	}

	public Room ProcessDirection(String userinput) {
		for (Direction direction : directions) {
			if (userinput.equals(direction.exitname)) {
				return direction.room;
			}
		}
		return null;
	}

	public Item TakeItem(String userinput) {
		int index = -1;
		if (userinput.startsWith("take ")) {
			index = 5;
		} else if (userinput.startsWith("pick up ")) {
			index = 8;
		} else if (TextAdventure.askAboutTake) {
			index = 0;
		}

		if (index != -1 && !items.isEmpty()) {
			String itemtotake = userinput.substring(index);
			return TextAdventure.getitembyname(itemtotake, items).item;
		}
		return null;
	}

	public void onTakeItem(Item takeItem) {
	}

	public void onCloseItem(Item closeItem) {
	}

	public void onUnlockItem(Item unlockItem) {
	}

	public void onOpenItem(Item openItem) {
	}

	public void onDropItem(Item itemDropped) {
	}

	public void onEnter() {
	}

	Room makeDark() {
		dark = true;
		return this;
	}

	public boolean processTriggers(String userinput) {
		for (Trigger trigger : triggers) {
			if (trigger.process(userinput)) {
				return true;
			}
		}
		return false;
	}

	public void addSimpleTrigger(String string, String string2) {
		triggers.add(new Trigger().addRequirement(Trigger.createCommandReq(string))
				.addAction(Trigger.createMessageAction(string2)));
	}
}
