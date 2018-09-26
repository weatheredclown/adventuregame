package adventuregame;

import java.util.ArrayList;

public class Item {
	private String name;
	String description;
	private String syn[];
	
	boolean openable = false;
	boolean open = false;
	boolean fixed = false;
	
	Item isOpen(boolean open) {
		this.open = open;
		return this;
	}
	
	Item isFixed(boolean fixed) {
		this.fixed = fixed;
		return this;
	}
	
	Item isOpenable(boolean openable) {
		this.openable = openable;
		return this;
	}
	
	Item inRoom(Room myroom) {
		myroom.additem(this);
		return this;
	}
	
	boolean locked = false;
	Item key = null;
	String unlocktext = "";
	Direction directiononunlock = null;
	public String exitappend;
	String lockedtext;
	ArrayList<Item> contents = new ArrayList<>();

	
	Item(String name, String description, String syn[]) {
		this.name = name;
		this.description = description;
		this.syn = syn;
	}
	
	String getName() {		
		if (openable) {
			return name + (open ? " (open)": " (closed)");
		} else {
			return name;
		}
	}
	
	void addKey(Item key, String unlocktext, Direction onunlock, String exitappend, String lockedtext) {
		locked = true;
		this.lockedtext = lockedtext;
		this.key = key;
		this.unlocktext = unlocktext;
		this.directiononunlock = onunlock;
		this.exitappend = exitappend;
	}

	public boolean match(String itemtotake) {
		for (int i = 0; i < syn.length; i++) {
			if (syn[i].equals(itemtotake)) {
				return true;
			}
		}
		return false;
	}
}
