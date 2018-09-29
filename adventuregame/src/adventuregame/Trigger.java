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
		NOT,
		LIGHT_IN_INVENTORY,
		ITEM_STATE,
		ITEM_TIMER
	}

	enum TriggerActionType {
		MESSAGE,
		GIVE_ITEM,
		TAKE_ITEM,
		CREATE_EXIT,
		MOVE_PLAYER,
		DISABLE_TRIGGER,
		ENABLE_TRIGGER,
		CHANGE_ITEM_STATE,
		CHANGE_ITEM_TIMER,
		CHANGE_ITEM_LIGHT_STATE
	}

	static class ActionData {
		// message data
		String message;
		// take item
		public Item takeItem;
		// move player
		public Room room;
		// disable trigger
		public Trigger disabletrigger;
		// enable trigger
		public Trigger enabletrigger;
		public Item item;
		public String newstate;
		public int timerdelta;
		public boolean lightstate;
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

		// In room
		Room room;

		public Item statecheckitem;

		public String itemstate;
	}

	public static class Action {
		TriggerActionType t;
		ActionData d;

		public boolean perform() {
			switch (t) {
			case MESSAGE:
				System.out.println(d.message);
				break;
			case MOVE_PLAYER:
				Map.doNavigate(d.room);
				break;
			case TAKE_ITEM:
				Player.inventory.remove(d.takeItem);
				break;
			case DISABLE_TRIGGER:
				d.disabletrigger.disabled = true;
				break;
			case ENABLE_TRIGGER:
				d.enabletrigger.disabled = true;
				break;
			case CHANGE_ITEM_STATE:
				d.item.state = d.newstate;
				break;
			case CHANGE_ITEM_TIMER:
				d.item.turntimer += d.timerdelta;
				break;
			case CHANGE_ITEM_LIGHT_STATE:
				d.item.lightsource = d.lightstate;
				break;
			default:
				System.out.println("Unsupported action type: " + t);
			}
			return true;
		}
	}

	public static class Requirement {
		TriggerRequirementType t;
		RequirementData d;

		public boolean met(String userinput) {
			switch (t) {
			case COMMAND:
				return userinput.equals(d.command);
			case ITEM_IN_INVENTORY:
				return Player.inventory.contains(d.ininventory);
			case LIGHT_IN_INVENTORY:
				return Player.hasLight();
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
			case PLAYER_IN_ROOM:
				return Map.currentroom == d.room;
			case ITEM_STATE:
				return d.itemstate.equals(d.statecheckitem.state);
			case ITEM_TIMER:
				return d.statecheckitem.turntimer > 0;
			default:
				System.out.println("Unsupported requirement type: " + t);
			}
			return false;
		}
	}

	private ArrayList<Requirement> requirements = new ArrayList<>();
	private ArrayList<Action> actions = new ArrayList<>();

	public Trigger() {
	}

	public Trigger addRequirement(Requirement requirement) {
		this.requirements.add(requirement);
		return this;
	}

	public Trigger addAction(Action action) {
		this.actions.add(action);
		return this;
	}

	public boolean process(String userinput) {
		if (disabled) {
			return false;
		}
		for (Requirement requirement : requirements) {
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
		return !eatsInput;
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
	boolean eatsInput = false;

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

	public static Action createDisableTriggerAction(Trigger trigger) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.DISABLE_TRIGGER;
		act.d = new Trigger.ActionData();
		act.d.disabletrigger = trigger;
		return act;
	}

	public static Action createEnableTriggerAction(Trigger trigger) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.ENABLE_TRIGGER;
		act.d = new Trigger.ActionData();
		act.d.enabletrigger = trigger;
		return act;
	}

	public static Requirement createLightInInventoryReq() {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.LIGHT_IN_INVENTORY;
		req.d = new Trigger.RequirementData();
		return req;
	}

	public static Requirement createPlayerInRoomReq(Room room) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.PLAYER_IN_ROOM;
		req.d = new Trigger.RequirementData();
		req.d.room = room;
		return req;
	}

	public static Requirement createOrReq(Requirement... reqs) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.OR;
		req.d = new Trigger.RequirementData();
		req.d.ors = reqs;
		return req;
	}

	public static Requirement createStateReq(Item item, String string) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.ITEM_STATE;
		req.d = new Trigger.RequirementData();
		req.d.statecheckitem = item;
		req.d.itemstate = string;
		return req;
	}

	public static Action createStateChangeAction(Item item, String string) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.CHANGE_ITEM_STATE;
		act.d = new Trigger.ActionData();
		act.d.item = item;
		act.d.newstate = string;
		return act;
	}

	public static Action createChangeTimerAction(Item item, int i) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.CHANGE_ITEM_TIMER;
		act.d = new Trigger.ActionData();
		act.d.item = item;
		act.d.timerdelta = i;
		return act;
	}

	public static Requirement createTurnsLeftOnItemReq(Item item) {
		Requirement req = new Requirement();
		req.t = Trigger.TriggerRequirementType.ITEM_TIMER;
		req.d = new Trigger.RequirementData();
		req.d.statecheckitem = item;
		return req;
	}

	public Trigger shouldEatInput(boolean b) {
		this.eatsInput = b;
		return this;
	}

	public static Action createSetLightOnItemAction(Item item, boolean b) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.CHANGE_ITEM_LIGHT_STATE;
		act.d = new Trigger.ActionData();
		act.d.item = item;
		act.d.lightstate = b;
		return act;
	}
}
