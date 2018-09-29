package adventuregame;

import java.util.Arrays;

import adventuregame.Room.Special;
import adventuregame.Trigger.Requirement;

public class Map {
	// TODO:
	// We need a real plot with real descriptions
	// place items in chest and take them out
	// triggers on items
	// darkness
	// fill out the requirements and actions on triggers 
	// NPCs
	// usability: multi-command on a line, again
	// turn timers
	
	// Story notes:
	// the existential loneliness of an archeologist in an ancient industrial society.
	
	static Room currentroom = null;

	public static void init() {
		ogInit();
		//pitInit();
	}
	
	public static void pitInit() {
		Room cavern = new Room("Cavern", "This room has exits to the north and south. There is also a spiral staircase leading down.  There is an inscription on the wall.").makeDark();
		new Item("inscription", "'Find the merchant to fulfill your destiny.'", syn("inscription")).detailInRoom(cavern);
		new Item("Magic Lightbulb", "", syn("bulb", "magic lightbulb", "lightbulb")).inRoom(cavern).isLightsource(true);
		Room smallcavern = new Room("Small Cavern", "You are in a small cavern. It has exits to the north and south.").makeDark();
		cavern.addexit("south", smallcavern);
		Room largecavern = new Room("Large Cavern", "This cavern has exits to the south and east. You notice a faint glow to the east.");
		cavern.addexit("north", largecavern);
		smallcavern.addexit("south", smallcavern, Special.NO_REVERSE_ROOM);

		Room whistleroom = new Room("Whistle Room", "There is an exit to the east and a spiral staircase leading upward into darkness.");
		cavern.addexit("down", whistleroom);

		cavern.triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq("down"))
				.addRequirement(Trigger.createNotReq(Trigger.createLightInInventoryReq()))
				.addAction(Trigger.createMessageAction("You shouldn't go down there without a light."))
				);

		whistleroom.triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq("up"))
				.addRequirement(Trigger.createNotReq(Trigger.createLightInInventoryReq()))
				.addAction(Trigger.createMessageAction("You shouldn't go up there without a light."))
				);

		Room whistleroom2 = new Room("Another Whistle Room", "TODO");
		
		Item whistle = new Item("Steel Whistle", "A steel whistle.", syn("steel whistle", "whistle")).inRoom(whistleroom);
		Requirement blowWhistleReq = Trigger.createOrReq(Trigger.createCommandReq("blow whistle"),Trigger.createCommandReq("play whistle"));
		whistle.addTrigger(new Trigger()
				.addRequirement(blowWhistleReq)
				.addRequirement(Trigger.createNotReq(Trigger.createInInventoryReq(whistle)))
				.addAction(Trigger.createMessageAction("You have to be holding it."))
				)
			.addTrigger(new Trigger()
				.addRequirement(blowWhistleReq)
				.addRequirement(Trigger.createInInventoryReq(whistle))
				.addRequirement(Trigger.createNotReq(Trigger.createPlayerInRoomReq(whistleroom)))
				.addAction(Trigger.createMessageAction("The whistle makes a hollow 'Toot toot!'"))
				)
			.addTrigger(new Trigger()
				.addRequirement(blowWhistleReq)
				.addRequirement(Trigger.createInInventoryReq(whistle))
				.addRequirement(Trigger.createPlayerInRoomReq(whistleroom))
				.addAction(Trigger.createMessageAction("The whistle makes a resounding 'Toot toot!'.  You feel a sensation of movement."))
				.addAction(Trigger.createMovePlayerAction(whistleroom2))
				);

		Room goldengate = new Room("Golden Gate", "You are standing at a large gate constructed of gold covered in intricate ingravings. Through the gate you see a magic carpet and high above you is a floating pedistal. The exit is to the west.");
		largecavern.addexit("east", goldengate);
		Room win = new Room("Win", "You win!"); 

		Item magickey = new Item("Magic Key", "With this key, you win.", syn("magic key", "key"));
		
		Item gate = new Item("golden gate", "Gate made of gold.  Don't steal it.", syn("golden gate", "gold gate", "gate")).detailInRoom(goldengate);
		gate.addSimpleTrigger("touch gate", "You get an electric shock that makes your fillings ache.");
		gate.addKey(magickey, "You put the key in the gate. Go ahead.", new Direction("east", win), " You can go through the gate to the east.", "");

		Room dungeon = new Room("Dungeon", "There are various torture devices in this room, all too heavy to take. On the rack is an unfortunate adventurer. The exits are to the south, north, and west.");
		/*Item adventurer = */new Item("adventurer", "It is pointless, he is dead.", syn("dead body", "body", "adventurer")).detailInRoom(dungeon);
		/*Item medallion = */new Item("Silver Medallion", "Lucky medal belonging to the adventurer.  It may not work.", syn("silver medallion", "medal", "medallion")).inRoom(dungeon);
		/*Item mace = */new Item("Mace", "A black war mace.", syn("mace")).inRoom(dungeon);
		/*Item axe = */new Item("Battle Axe", "A heave battle axe.", syn("axe", "battle axe")).inRoom(dungeon);
		whistleroom.addexit("east", dungeon);

		Room riverroom = new Room("river room", "A quiet north-south river flows to the west of you. There are exits on the other three sides of the room. A warm breeze is comming from the east.");
		dungeon.addexit("north", riverroom);
		Item canteen = new Item("Canteen", "A steel camping canteen.", syn("canteen")).inRoom(riverroom);
		canteen.addState("empty");

		canteen.addTrigger(new Trigger()
				.addRequirement(Trigger.createCommandReq("fill canteen"))
				.addRequirement(Trigger.createStateReq(canteen, "empty"))
				.addAction(Trigger.createMessageAction("You fill it up."))
				.addAction(Trigger.createStateChangeAction(canteen, "filled"))
				);

		doNavigate(cavern);
	}

	public static void ogInit() {
		Room dark = new Room("Dark Room", "You are in a dark room. To your north is a passageway leading north. It looks very... northward.");
		Room ncavern = new Room("North Cavern", "You are in the great north cavern.  It is really great! :-) To the south is a passageway leading back to the dark room. Also, you can go east.");
		Room fountain = new Room("Fountain Room", "You are in a mysterious room with a large fountain in the center. A door is to your west, returning you to the great cavern.");

		/*Item brasskey = */new Item("The Brass Key",
				"A key made of solid brass and encrusted with jewels. It shimmers in the torchlight of the dungeon, but only when you're in a room with torches.",
				syn("key", "brass key", "the key", "the brass key")).inRoom(dark).isLightsource(true);

		dark.addexit("north", ncavern);
		ncavern.addexit("east", fountain);
		fountain.details.add(new Item("fountain", "An ornate fountain. You feel invigorated just being in its presence. Also, you love it.", syn("fountain", "the fountain")));

		dark.triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq("hello"))
				.addAction(Trigger.createMessageAction("eat dirt ok")));

		Item chest = new Item("Chest", "A wooden chest.", syn("chest")).isFixed(true).isOpenable(true).inRoom(dark);
		Item coin = new Item("Coin", "A golden coin, that is made of gold.", syn("coin")).inContainer(chest);

		Item whistle = new Item("Steel Whistle", "A steel whistle.", syn("steel whistle", "whistle")).inRoom(dark);
		whistle.addTrigger(new Trigger()
				.addRequirement(Trigger.createCommandReq("blow whistle"))
				.addRequirement(Trigger.createInInventoryReq(whistle))
				.addAction(Trigger.createMessageAction("Toot toot!"))
				);
		
		Trigger cantpaytollaction = new Trigger()
				.addRequirement(Trigger.createCommandReq("north"))
				.addRequirement(Trigger.createNotReq(Trigger.createInInventoryReq(coin)))
				.addAction(Trigger.createMessageAction("You can't pay the toll."));

		Trigger paytollaction = new Trigger()
				.addRequirement(Trigger.createCommandReq("north"))
				.addRequirement(Trigger.createInInventoryReq(coin))
				.addAction(Trigger.createMessageAction("You pay the toll."))
				.addAction(Trigger.createTakeItemAction(coin))
				.addAction(Trigger.createMovePlayerAction(ncavern))
				.addAction(Trigger.createDisableTriggerAction(cantpaytollaction))
				.succeedOnce();

		dark.triggers.add(paytollaction);
		
		dark.triggers.add(cantpaytollaction);


		Item flashlight = new Item("Flashlight", "A yellow flashlight", syn("flashlight")).inRoom(dark);
		flashlight.state = "off";

		flashlight.addTrigger(new Trigger()
				.addRequirement(Trigger.createCommandReq("turn on flashlight"))
				.addRequirement(Trigger.createStateReq(flashlight, "off"))
				.addRequirement(Trigger.createTurnsLeftOnItemReq(flashlight))
				.addAction(Trigger.createMessageAction("You turn it on."))
				.addAction(Trigger.createSetLightOnItemAction(flashlight, true))
				.addAction(Trigger.createStateChangeAction(flashlight, "on"))
				);

		flashlight.turntimer = 10;
		
		flashlight.addTrigger(new Trigger()
				.addRequirement(Trigger.createCommandReq("turn on flashlight"))
				.addRequirement(Trigger.createStateReq(flashlight, "off"))
				.addRequirement(Trigger.createNotReq(Trigger.createTurnsLeftOnItemReq(flashlight)))
				.addAction(Trigger.createMessageAction("Batteries are dead."))
				.addAction(Trigger.createStateChangeAction(flashlight, "on"))
				);

		flashlight.addTrigger(new Trigger()
				.addRequirement(Trigger.createStateReq(flashlight, "on"))
				.addRequirement(Trigger.createNotReq(Trigger.createTurnsLeftOnItemReq(flashlight)))
				.addAction(Trigger.createMessageAction("The flashlight sputters out."))
				.addAction(Trigger.createSetLightOnItemAction(flashlight, false))
				.addAction(Trigger.createStateChangeAction(flashlight, "off"))
				);

		Player.addTrigger(new Trigger()
				.addRequirement(Trigger.createStateReq(flashlight, "on"))
				.addAction(Trigger.createMessageAction("flashlight on."))
				.addAction(Trigger.createChangeTimerAction(flashlight, -1))
				.shouldEatInput(true)
				);
		
		dark.dark = true;
		
//		Example of lock/key implementation		
//		Item burger = new Item("burger", "a magic portal", syn("burger"));
//		burger.addKey(brasskey, "You put the key in the burger. Monch Cronch", new Direction("east", dark), " You can go through the burger portal to the east.",
//				" You see a magic burger portal.");
//		fountain.details.add(burger);

		doNavigate(dark);
	}

	static void doNavigate(Room newRoom) {
		currentroom = newRoom;
		newRoom.print();
		currentroom.onEnter();
		currentroom.hasEntered = true;
	}
	
	static String[] syn(String... arg0) {
		return (String[]) Arrays.asList(arg0).toArray();
	}
}
