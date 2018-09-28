package adventuregame;

import java.util.Arrays;

public class Map {
	// TODO:
	// We need a real plot with real descriptions
	// place items in chest and take them out
	// triggers on items
	// darkness
	// fill out the requirements and actions on triggers 
	// NPCs
	// usability: multi-command on a line, again
	
	static Room currentroom = null;

	public static void init() {
		ogInit();
		//pitInit();
	}
	
	public static void pitInit() {
		Room cavern = new Room("Cavern", "This room has exits to the north and south. There is also a spiral staircase leading down.  There is an inscription on the wall.");
		
		new Item("Magic Lightbulb", "", syn("bulb", "magic lightbulb", "lightbulb")).inRoom(cavern);
		doNavigate(cavern);
	}
	
	public static void ogInit() {
		Room dark = new Room("Dark Room", "You are in a dark room. To your north is a passageway leading north. It looks very... northward.");
		Room ncavern = new Room("North Cavern", "You are in the great north cavern.  It is really great! :-) To the south is a passageway leading back to the dark room. Also, you can go east.");
		Room fountain = new Room("Fountain Room", "You are in a mysterious room with a large fountain in the center. A door is to your west, returning you to the great cavern.");

		/*Item brasskey = */new Item("The Brass Key",
				"A key made of solid brass and encrusted with jewels. It shimmers in the torchlight of the dungeon, but only when you're in a room with torches.",
				syn("key", "brass key", "the key", "the brass key")).inRoom(dark);
		
		dark.addexit("north", ncavern, Room.Special.AUTO_CREATE_REVERSE_ROOM);
		ncavern.addexit("east", fountain, Room.Special.AUTO_CREATE_REVERSE_ROOM);
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
		
		dark.triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq("north"))
				.addRequirement(Trigger.createNotReq(Trigger.createInInventoryReq(coin)))
				.addAction(Trigger.createMessageAction("You can't pay the toll."))
				.failOnce()
				);
		
		dark.triggers.add(new Trigger()
				.addRequirement(Trigger.createCommandReq("north"))
				.addRequirement(Trigger.createInInventoryReq(coin))
				.addAction(Trigger.createMessageAction("You pay the toll."))
				.addAction(Trigger.createTakeItemAction(coin))
				.addAction(Trigger.createMovePlayerAction(ncavern))
				.succeedOnce()
				);
		
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
