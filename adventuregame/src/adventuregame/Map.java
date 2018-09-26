package adventuregame;

import java.util.Arrays;

public class Map {
	// TODO:
	// easy system for arbitrary actions
	// We need a real plot with real descriptions
	
	static Room currentroom = null;

	public static void init() {
		Room dark = new Room("Dark Room", "You are in a dark room. To your north is a passageway leading north. It looks very... northward.");
		Room ncavern = new Room("North Cavern", "You are in the great north cavern.  It is really great! :-) To the south is a passageway leading back to the dark room. Also, you can go east.");
		Room fountain = new Room("Fountain Room", "You are in a mysterious room with a large fountain in the center. A door is to your west, returning you to the great cavern.");
		
		dark.addexit("north", ncavern, Room.special.AUTO_CREATE_REVERSE_ROOM);
		ncavern.addexit("east", fountain, Room.special.AUTO_CREATE_REVERSE_ROOM);
		Item brasskey = new Item("The Brass Key",
				"A key made of solid brass and encrusted with jewels. It shimmers in the torchlight of the dungeon, but only when you're in a room with torches.",
				syn("key", "brass key", "the key", "the brass key"));
		dark.additem(brasskey);
		fountain.details.add(new Item("fountain", "An ornate fountain. You feel invigorated just being in its presence. Also, you love it.", syn("fountain", "the fountain")));
		
//		Example of lock/key implementation		
//		Item burger = new Item("burger", "a magic portal", syn("burger"));
//		burger.addKey(brasskey, "You put the key in the burger. Monch Cronch", new Direction("east", dark), " You can go through the burger portal to the east.",
//				" You see a magic burger portal.");
//		fountain.details.add(burger);
		
		currentroom = dark;
	}
	
	static String[] syn(String... arg0) {
		return (String[]) Arrays.asList(arg0).toArray();
	}
}
