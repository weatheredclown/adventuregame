package adventuregame;

import java.util.ArrayList;

public class Trigger {

	enum TriggerRequirementType {
		COMMAND,
		TARGET_ITEM,
		ITEM_IN_ROOM,
		ITEM_IN_INVENTORY,
		PLAYER_IN_ROOM,
		OR,
		NOT
	}
	
	enum TriggerActionType {
		MESSAGE,
		GIVE_ITEM,
		TAKE_ITEM,
		CREATE_EXIT,
		MOVE_PLAYER
	}
	
	static class ActionData {
		// message data
		String message;
		public Item takeItem;
		public Room room;
	}
	
	static class RequirementData {
		// Command data
		String command;
		
		// Target data
		Item target;
		
		// In Room data
		Item inroom;
		
		// Inventory data
		Item ininventory;
		
		// OR data
		Requirement[] ors;
		
		// NOT data
		Requirement not;
	}

	static class Action {
		TriggerActionType t;
		ActionData d;
		public boolean perform() {
			switch(t) {
			case MESSAGE:
				System.out.println(d.message);
				break;
			case MOVE_PLAYER:
				Map.doNavigate(d.room);
				break;
			case TAKE_ITEM:
				Player.inventory.remove(d.takeItem);
				break;
			default:
				System.out.println("Unsupported type: " + t);
			}
			return true;
		}
	}
	
	static class Requirement {
		TriggerRequirementType t;
		RequirementData d;
		public boolean met(String userinput) {
			switch(t) {
			case COMMAND:
				return userinput.equals(d.command);
			case ITEM_IN_INVENTORY:
				return Player.inventory.contains(d.ininventory);
			case NOT: {
				return !d.not.met(userinput);
			}
			case OR: {
				for (int i = 0; i < d.ors.length; i++) {
					if (d.ors[i].met(userinput)) {
						return true;
					}
				}
			}
			default:
				System.out.println("Unsupported type: " + t);
			}
			return false;
		}
	}

	private ArrayList<Requirement> requirements = new ArrayList<>();
	private ArrayList<Action> actions = new ArrayList<>();
	
	Trigger() {
		
	}
	
	Trigger addRequirement(Requirement requirement) {
		this.requirements.add(requirement);
		return this;
	}
	
	Trigger addAction(Action action) {
		this.actions.add(action);
		return this;
	}
	
	public boolean process(String userinput) {
		if (disabled) {
			return false;
		}
		for(Requirement requirement : requirements) {
			if (!requirement.met(userinput)) {
				if (failOnce) {
					disabled = true;
				}
				return false;
			}
		}
		for (Action action : actions) {
			action.perform();
		}
		if (succeedOnce) {
			disabled = false;
		}
		return true;
	}

	public static Requirement createCommandReq(String string) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.COMMAND;
		req.d = new Trigger.RequirementData();
		req.d.command = string;		
		return req;
	}

	public static Requirement createInInventoryReq(Item item) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.ITEM_IN_INVENTORY;
		req.d = new Trigger.RequirementData();
		req.d.ininventory = item;		
		return req;
	}
	
	public static Requirement createNotReq(Requirement requirement) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.NOT;
		req.d = new Trigger.RequirementData();
		req.d.not = requirement;		
		return req;
	}
	
	public static Action createMessageAction(String string) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.MESSAGE;
		act.d = new Trigger.ActionData();
		act.d.message = string;
		return act;
	}

	boolean failOnce = false;
	boolean succeedOnce = false;
	boolean disabled = false;

	public Trigger succeedOnce() {
		succeedOnce = true;
		return this;
	}
	
	public Trigger failOnce() {
		failOnce = true;
		return this;
	}

	public static Action createTakeItemAction(Item item) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.TAKE_ITEM;
		act.d = new Trigger.ActionData();
		act.d.takeItem = item;
		return act;
	}

	public static Action createMovePlayerAction(Room room) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.MOVE_PLAYER;
		act.d = new Trigger.ActionData();
		act.d.room = room;
		return act;
	}

}
