package adventuregame;

import java.util.ArrayList;

public class Trigger {

	enum TriggerRequirementType {
		COMMAND,
		TARGET_ITEM,
		ITEM_IN_ROOM,
		ITEM_IN_INVENTORY,
		OR,
	}
	
	enum TriggerActionType {
		MESSAGE,
		GIVE_ITEM,
		TAKE_ITEM,
		CREATE_EXIT,
		
	}
	
	static class ActionData {
		// message data
		String message;
	}
	
	static class RequirementData {
		// command data
		String command;
		
		// Target data
		Item target;
		
		// In Room data
		Item inroom;
		
		// Inventory data
		Item ininventory;
		
		// OR data
		Requirement[] ors;
	}

	static class Action {
		TriggerActionType t;
		ActionData d;
		public boolean perform() {
			switch(t) {
			case MESSAGE:
				System.out.println(d.message);
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
				if (userinput.equals(d.command)) {
					return true;
				}
				break;
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
			// TODO Auto-generated method stub
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
		for(Requirement requirement : requirements) {
			if (!requirement.met(userinput)) {
				return false;
			}
		}
		for (Action action : actions) {
			action.perform();
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

	public static Action createMessageAction(String string) {
		Trigger.Action act = new Trigger.Action();
		act.t = Trigger.TriggerActionType.MESSAGE;
		act.d = new Trigger.ActionData();
		act.d.message = string;
		return act;
	}

}
