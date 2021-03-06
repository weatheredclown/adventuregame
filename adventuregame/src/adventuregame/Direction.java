package adventuregame;

public class Direction {
	String exitname;
	Room room;
	public Direction(String exitname, Room r) {
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
		} else if (direction.equals("northeast")) {
			return "southwest";
		} else if (direction.equals("southeast")) {
			return "northwest";
		} else if (direction.equals("northwest")) {
			return "southeast";
		} else if (direction.equals("southwest")) {
			return "northeast";
		} else if (direction.equals("in")) {
			return "in";
		} else if (direction.equals("out")) {
			return "out";
		}
		return direction;
	}
}
