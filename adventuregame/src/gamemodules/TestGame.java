package gamemodules;

import adventuregame.Item;
import adventuregame.Map;
import adventuregame.Player;
import adventuregame.Room;
import adventuregame.Trigger;

public class TestGame {

	public static void init() {
			Room dark = new Room("Dark Room",
					"You are in a dark room. To your north is a passageway leading north. It looks very... northward.");
			Room ncavern = new Room("North Cavern",
					"You are in the great north cavern.  It is really great! :-) To the south is a passageway leading back to the dark room. Also, you can go east.");
			Room fountain = new Room("Fountain Room",
					"You are in a mysterious room with a large fountain in the center. A door is to your west, returning you to the great cavern.");
	
			/* Item brasskey = */new Item("The Brass Key",
					"A key made of solid brass and encrusted with jewels. It shimmers in the torchlight of the dungeon, but only when you're in a room with torches.",
					Map.syn("key", "brass key", "the key", "the brass key")).inRoom(dark).isLightsource(true);
	
			dark.addexit("north", ncavern);
			ncavern.addexit("east", fountain);
			new Item("fountain",
					"An ornate fountain. You feel invigorated just being in its presence. Also, you love it.",
					Map.syn("fountain", "the fountain")).detailInRoom(fountain);
	
			dark.addTrigger(new Trigger().addRequirement(Trigger.createCommandReq("hello"))
					.addAction(Trigger.createMessageAction("eat dirt ok")));
	
			Item chest = new Item("Chest", "A wooden chest.", Map.syn("chest")).isFixed(true).isOpenable(true).inRoom(dark);
			Item coin = new Item("Coin", "A golden coin, that is made of gold.", Map.syn("coin")).inContainer(chest);
	
			Item whistle = new Item("Steel Whistle", "A steel whistle.", Map.syn("steel whistle", "whistle")).inRoom(dark);
			whistle.addTrigger(new Trigger().addRequirement(Trigger.createCommandReq("blow whistle"))
					.addRequirement(Trigger.createInInventoryReq(whistle))
					.addAction(Trigger.createMessageAction("Toot toot!")));
	
			Trigger cantpaytollaction = new Trigger().addRequirement(Trigger.createCommandReq("north"))
					.addRequirement(Trigger.createNotReq(Trigger.createInInventoryReq(coin)))
					.addAction(Trigger.createMessageAction("You can't pay the toll."));
	
			Trigger paytollaction = new Trigger().addRequirement(Trigger.createCommandReq("north"))
					.addRequirement(Trigger.createInInventoryReq(coin))
					.addAction(Trigger.createMessageAction("You pay the toll."))
					.addAction(Trigger.createTakeItemAction(coin)).addAction(Trigger.createMovePlayerAction(ncavern))
					.addAction(Trigger.createDisableTriggerAction(cantpaytollaction)).succeedOnce();
	
			dark.addTrigger(paytollaction);
	
			dark.addTrigger(cantpaytollaction);
	
			Item flashlight = new Item("Flashlight", "A yellow flashlight", Map.syn("flashlight")).inRoom(dark);
			flashlight.state = "off";
	
			flashlight.addTrigger(new Trigger().addRequirement(Trigger.createCommandReq("turn on flashlight"))
					.addRequirement(Trigger.createStateReq(flashlight, "off"))
					.addRequirement(Trigger.createTurnsLeftOnItemReq(flashlight))
					.addAction(Trigger.createMessageAction("You turn it on."))
					.addAction(Trigger.createSetLightOnItemAction(flashlight, true))
					.addAction(Trigger.createStateChangeAction(flashlight, "on")));
	
			flashlight.turntimer = 10;
	
			flashlight.addTrigger(new Trigger().addRequirement(Trigger.createCommandReq("turn on flashlight"))
					.addRequirement(Trigger.createStateReq(flashlight, "off"))
					.addRequirement(Trigger.createNotReq(Trigger.createTurnsLeftOnItemReq(flashlight)))
					.addAction(Trigger.createMessageAction("Batteries are dead."))
					.addAction(Trigger.createStateChangeAction(flashlight, "on")));
	
			flashlight.addTrigger(new Trigger().addRequirement(Trigger.createStateReq(flashlight, "on"))
					.addRequirement(Trigger.createNotReq(Trigger.createTurnsLeftOnItemReq(flashlight)))
					.addAction(Trigger.createMessageAction("The flashlight sputters out."))
					.addAction(Trigger.createSetLightOnItemAction(flashlight, false))
					.addAction(Trigger.createStateChangeAction(flashlight, "off")));
	
			Player.addTrigger(new Trigger().addRequirement(Trigger.createStateReq(flashlight, "on"))
					.addAction(Trigger.createMessageAction("flashlight on."))
					.addAction(Trigger.createChangeTimerAction(flashlight, -1)).shouldEatInput(true));
	
			dark.dark = true;
	
	//		Example of lock/key implementation		
	//		Item burger = new Item("burger", "a magic portal", syn("burger"));
	//		burger.addKey(brasskey, "You put the key in the burger. Monch Cronch", new Direction("east", dark), " You can go through the burger portal to the east.",
	//				" You see a magic burger portal.");
	//		fountain.details.add(burger);
	
			Map.doNavigate(dark);
		}

}
