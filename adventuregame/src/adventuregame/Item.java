package adventuregame;

import java.util.ArrayList;

public class Item {
	
	static class TokenMatch {
		String tokenFound = null;
		Item item = null;
		public TokenMatch() {
		}
		TokenMatch(String tokenFound, Item item) {
			this.tokenFound = tokenFound;
			this.item = item;
		}
		boolean found() {
			return item != null;
		}
	}
	
	private String name;
	String description;
	private String syn[];

	boolean openable = false;
	boolean open = false;
	boolean fixed = false;
	boolean locked = false;
	
	Item key = null;
	
	String unlocktext = "";
	
	Direction directiononunlock = null;
	
	public String exitappend;
	String lockedtext;
	ArrayList<Item> contents = new ArrayList<>();
	ArrayList<Trigger> triggers = new ArrayList<>();
	public boolean lightsource = false;
	public String state = null;
	public int turntimer = 0;
	public Item(String name, String description, String syn[]) {
		this.name = name;
		this.description = description;
		this.syn = syn;
	}
	public void addKey(Item key, String unlocktext, Direction onunlock, String exitappend, String lockedtext) {
		locked = true;
		this.lockedtext = lockedtext;
		this.key = key;
		this.unlocktext = unlocktext;
		this.directiononunlock = onunlock;
		this.exitappend = exitappend;
	}

	
	public Item addTrigger(Trigger trigger) {
		triggers.add(trigger);
		return this;
	}

	String getName() {		
		String ret = name;
		if (openable) {
			ret += (open ? " (open)": " (closed)");
		}
		if (lightsource && Map.currentroom.dark) {
			ret += " (providing light)";
		}
		if (state != null) {
			ret += " (" + state + ")";
		}
		return ret;
	}

	public Item inContainer(Item chest) {
		chest.contents.add(this);
		return this;
	}

	public Item inRoom(Room myroom) {
		myroom.additem(this);
		return this;
	}
	
	public Item isFixed(boolean fixed) {
		this.fixed = fixed;
		return this;
	}

	public Item isLightsource(boolean light) {
		this.lightsource = true;
		return this;
	}
	
	Item isOpen(boolean open) {
		this.open = open;
		return this;
	}

	public Item isOpenable(boolean openable) {
		this.openable = openable;
		return this;
	}
	public TokenMatch match(String itemtotake) {
		return match(itemtotake, false);
	}
	
	public TokenMatch match(String itemname, boolean allowPartialMatch) {
		for (int i = 0; i < syn.length; i++) {
			if (syn[i].equals(itemname) || (allowPartialMatch && itemname.startsWith(syn[i] + " "))) {
				return new TokenMatch(syn[i], this);
			}
		}
		return new TokenMatch();
	}
	
	public boolean processTriggers(String userinput) {
		for (Trigger trigger : triggers) {
			if (trigger.process(userinput)) {
				return true;
			}
		}
		return false;
	}
	public Item detailInRoom(Room room) {
		room.details.add(this);
		return this;
	}
	public void addSimpleTrigger(String string, String string2) {
		addTrigger(new Trigger()
				.addRequirement(Trigger.createCommandReq(string))
				.addAction(Trigger.createMessageAction(string2)));
	}
	public void addState(String string) {
		this.state = string;
	}
}
