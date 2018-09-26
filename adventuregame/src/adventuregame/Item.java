package adventuregame;

public class Item {
	String name;
	String description;
	private String syn[];
	
	
	boolean locked = false;
	Item key = null;
	String unlocktext = "";
	Direction directiononunlock = null;
	public String exitappend;
	String lockedtext;
	
	Item(String name, String description, String syn[]) {
		this.name = name;
		this.description = description;
		this.syn = syn;
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
