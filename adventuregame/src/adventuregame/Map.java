package adventuregame;

import java.util.Arrays;

import gamemodules.TestGame;

public class Map {
	// TODO:
	// fill out the requirements and actions on triggers
	// NPCs
	// usability: multi-command on a line, again
	// turn timers

	// Story notes:
	// the existential loneliness of an archeologist in an ancient industrial
	// society.

	static Room currentroom = null;

	public static void init() {
		TestGame.init();
		// PitGame.init();
	}

	public static void doNavigate(Room newRoom) {
		currentroom = newRoom;
		newRoom.print();
		currentroom.onEnter();
		currentroom.hasEntered = true;
	}

	public static String[] syn(String... arg0) {
		return (String[]) Arrays.asList(arg0).toArray();
	}
}
