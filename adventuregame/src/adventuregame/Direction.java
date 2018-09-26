package adventuregame;

public class Direction {
	String exitname;
	Room room;
	Direction(String exitname, Room r) {
		this.exitname = exitname;
		room = r;
	}
	
	static String opposite(String direction) {
		if (direction.equals("north")) {
			return "south";
		} else if (direction.equals("south")) {
			return "north";
		} else if (direction.equals("east")) {
			return "west";
		} else if (direction.equals("west")) {
			return "east";
		} else if (direction.equals("up")) {
			return "down";
		} else if (direction.equals("down")) {
			return "up";
		}
		return direction;
	}
}
