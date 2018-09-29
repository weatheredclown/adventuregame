package adventuregame;

import java.util.Arrays;

import adventuregame.Room.Special;
import adventuregame.Trigger.Requirement;

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
		// ogInit();
		pitInit();
	}

	public static void pitInit() {
		Room cavern = new Room("Cavern",
				"This room has exits to the north and south. There is also a spiral staircase leading down.  There is an inscription on the wall.")
						.makeDark();
		new Item("inscription", "'Find the merchant to fulfill your destiny.'", syn("inscription"))
				.detailInRoom(cavern);
		new Item("Magic Lightbulb", "", syn("bulb", "magic lightbulb", "lightbulb")).inRoom(cavern).isLightsource(true);
		Room smallcavern = new Room("Small Cavern", "You are in a small cavern. It has exits to the north and south.")
				.makeDark();
		cavern.addexit("south", smallcavern);
		Room largecavern = new Room("Large Cavern",
				"This cavern has exits to the south and east. You notice a faint glow to the east.");
		cavern.addexit("north", largecavern);
		smallcavern.addexit("south", smallcavern, Special.NO_REVERSE_ROOM);

		Room whistleroom = new Room("Whistle Room",
				"There is an exit to the east and a spiral staircase leading upward into darkness.");
		cavern.addexit("down", whistleroom);

		cavern.triggers.add(new Trigger().addRequirement(Trigger.createCommandReq("down"))
				.addRequirement(Trigger.createNotReq(Trigger.createLightInInventoryReq()))
				.addAction(Trigger.createMessageAction("You shouldn't go down there without a light.")));

		whistleroom.triggers.add(new Trigger().addRequirement(Trigger.createCommandReq("up"))
				.addRequirement(Trigger.createNotReq(Trigger.createLightInInventoryReq()))
				.addAction(Trigger.createMessageAction("You shouldn't go up there without a light.")));

		Room whistleroom2 = new Room("Another Whistle Room",
				"There is an exit to the east and a spiral staircase leading upward into darkness.");

		Item whistle = new Item("Steel Whistle", "A steel whistle.", syn("steel whistle", "whistle"))
				.inRoom(whistleroom);
		Requirement blowWhistleReq = Trigger.createOrReq(Trigger.createCommandReq("blow whistle"),
				Trigger.createCommandReq("play whistle"));
		whistle.addTrigger(new Trigger().addRequirement(blowWhistleReq)
				.addRequirement(Trigger.createNotReq(Trigger.createInInventoryReq(whistle)))
				.addAction(Trigger.createMessageAction("You have to be holding it.")))
				.addTrigger(new Trigger().addRequirement(blowWhistleReq)
						.addRequirement(Trigger.createInInventoryReq(whistle))
						.addRequirement(Trigger.createPlayerInRoomReq(whistleroom))
						.addAction(Trigger.createMessageAction(
								"The whistle makes a resounding 'Toot toot!'.  You feel a sensation of movement."))
						.addAction(Trigger.createMovePlayerAction(whistleroom2)))
				.addTrigger(new Trigger().addRequirement(blowWhistleReq)
						.addRequirement(Trigger.createInInventoryReq(whistle))
						.addRequirement(Trigger.createPlayerInRoomReq(whistleroom2))
						.addAction(Trigger.createMessageAction(
								"The whistle makes a resounding 'Toot toot!'.  You feel a sensation of movement."))
						.addAction(Trigger.createMovePlayerAction(whistleroom)))
				.addTrigger(new Trigger().addRequirement(blowWhistleReq)
						.addRequirement(Trigger.createInInventoryReq(whistle))
						.addRequirement(Trigger.createNotReq(Trigger.createPlayerInRoomReq(whistleroom)))
						.addAction(Trigger.createMessageAction("The whistle makes a hollow 'Toot toot!'")));

		Room goldengate = new Room("Golden Gate",
				"You are standing at a large gate constructed of gold covered in intricate ingravings. Through the gate you see a magic carpet and high above you is a floating pedistal. The exit is to the west.");
		largecavern.addexit("east", goldengate);
		Room win = new Room("Win", "You win!");

		Item magickey = new Item("Magic Key", "With this key, you win.", syn("magic key", "key"));

		Item gate = new Item("golden gate", "Gate made of gold.  Don't steal it.",
				syn("golden gate", "gold gate", "gate")).detailInRoom(goldengate);
		gate.addSimpleTrigger("touch gate", "You get an electric shock that makes your fillings ache.");
		gate.addKey(magickey, "You put the key in the gate. Go ahead.", new Direction("east", win),
				" You can go through the gate to the east.", "");

		Room dungeon = new Room("Dungeon",
				"There are various torture devices in this room, all too heavy to take. On the rack is an unfortunate adventurer. The exits are to the south, north, and west.");
		/* Item adventurer = */new Item("adventurer", "It is pointless, he is dead.",
				syn("dead body", "body", "adventurer")).detailInRoom(dungeon);
		/* Item medallion = */new Item("Silver Medallion", "Lucky medal belonging to the adventurer.  It may not work.",
				syn("silver medallion", "medal", "medallion")).inRoom(dungeon);
		/* Item mace = */new Item("Mace", "A black war mace.", syn("mace")).inRoom(dungeon);
		/* Item axe = */new Item("Battle Axe", "A heave battle axe.", syn("axe", "battle axe")).inRoom(dungeon);
		whistleroom.addexit("east", dungeon);

		Room riverroom = new Room("river room",
				"A quiet north-south river flows to the west of you. There are exits on the other three sides of the room. A warm breeze is comming from the east.");
		dungeon.addexit("north", riverroom);
		Item canteen = new Item("Canteen", "A steel camping canteen.", syn("canteen")).inRoom(riverroom);
		canteen.addState("empty");

		riverroom.addSimpleTrigger("fish", "You can't go fishing here.");

		canteen.addTrigger(new Trigger().addRequirement(Trigger.createCommandReq("fill canteen"))
				.addRequirement(Trigger.createStateReq(canteen, "empty"))
				.addAction(Trigger.createMessageAction("You fill it up."))
				.addAction(Trigger.createStateChangeAction(canteen, "filled")));

		Room glassroom = new Room("glass room",
				"The floor is made of a transparent substance. There is an exit in to the east. You see a crystal staircase leading down.");
		dungeon.addexit("south", glassroom, Special.NO_REVERSE_ROOM);
		glassroom.addexit("east", dungeon, Special.NO_REVERSE_ROOM);

//		800 print "bandit room"
//		801 print "the exit is to the south."

//		900 print "green house"
//		902 print "there are many potted plants in this room.  all need water.  the exit is to the west."
//		940 if a$="water plants" then goto 950

//		1000 print "river bed"
//		1005 print "you are at the bottom of the river.  you can go east or north."
//		1007 if ij$<>"diamond ring" then print "there is a diamond ring here"
//		1010 print "you are under water."

//		1100 print "troll room"
//		1102 print "the troll disapears into an unseen passage.  you are in a small room with exits to the east and the west.  there is a glass staircase leading up.  big scratchesand blood stains cover the walls."

//		1200 print "trophy room"
//		1204 print "you are in a hidden room with treasures piled to the cieling.  upon closer      examination you realize all of the treasured have traps hooked to them."
//		1209 if ih$<>"sword" then print "there is an elvin sword here that is not hooked up to a trap."

//		1300 print "drain"
//		1310 print "you are under water."
//		1312 print "the exits are to the west and to the south.  you must struggle to stop yourself from being dragged down into the drain."
//		1315 if ik$<>"safe" then print "there is a safe here."

//		1400 print "rag room"
//		1401 print "there are exits to the north and east.  you hear a faint murmur to the east."
//		1409 if il$<>"rags" then print "there is a pile of rags here."
//		1460 print "suprize...   you found a pair of sunglasses in the rags."

//		1500 print "bright room"
//		1510 print "you are in a room with exits to the north and east.  a green staircase leads up"
//		1513 if in$<>"sphere" then print "there is a bright sphere in the middle of the room"
//		1515 if iy$<>"gold statue" then print "there is a gold statue here"

//		1600 print "rapid water"
//		1610 print "you are under water"
//		1615 print "holy perchheads batman! the exits are to the east and west."

//		1700 print "pointless room"
//		1702 print "this room has a brilliant red floor with three squares of yellow on it.  the    walls are grey and painted on the south wall is a picture of the great dimwit   flathead.  the room smells like roses and a flourescent green staircase leads "
//		1704 if cup$="" then print "down.  there is an empty cup here that is bolted to the floor." else print "down.  there is a cup here filled with water and a door to the west."
//		1732 if iff$="canteen and water" then print "sorry, wrong kind of water" else goto 1734
//		1738 iff$="canteen":cup$="cup and waterfall water":print "a door opens to the west":west=1

//		1800 print "white room"
//		1801 print "you are in a white room with pretty black curtians over the window.  out side   the window is a train station.  there is a large control panel in the middle of the room which apears to be securly bolted to the floor."
//		1802 print "there is a bucket type part on the machine. it is labeled                       'infinite improbability drive'.  there are exits to the west and south.":if itea$<>"cup of tea" then print "there is a cup of tea here."
//		1803 if io$<>"sheet of music" then print "there is a sheet of music here."

//		1900 print "shore"
//		1901 print "you are on a beach.  exits are to the west or up a ladder.  the river is to the east."

//		2000 print "whisper room"
//		2001 print "in this room, thousands of voices whisper to you.  the exits are to   the west, north, and northeast."
//		2060 if a$="listen" then print"you hear a little poem:     when lost within the halls of glass, don't wave the wand - you'll roast your...rump!"

//		2100 print "wizard room"
//		2101 print "the room is filled with mist.  you can go east or west.  to the south is a      magical box you can get into."

//		2200 print "roof of house"
//		2201 print "up, down, or west?"
//		2202 if ip$<>"lightning rod" then print "there is a lightning rod in the room."

//		2300 print "alice room"
//		2301 print "you are in a room with an exit to the east and a mirror on the west wall."

//		2400 print "lightning room"
//		2401 print "this is a large room.  exits are north and down"
//		2402 if ip$<>"lightning rod" then print "bolts of electricity criss-cross the room.  you are likely to be struck by      lightning."
//		2403 if ip$="lightning rod" then rytufjnreu$="lw;ekfj":if ir$<>"magic wand" then print "there is a wand here."
//		2404 if x=1 then print "you have been struck by lightning!":end

//		2500 print "north/south passage"
//		2501 print "there is an exit to the west also."

//		2600 print "in-a-box"

//		2700 print "magician's book room"
//		2701 print "exits are to the west and down.  there is a box here that you can get into."
//		2702 if ist$<>"magic book" then print "there is a magic book here"

//		2780 if a$="take book" then klhbb$="ljk":if ir$<>"magic wand" then print "you can't take the book because of a magical force field protecting it!":goto 2790

//		2800 print "maze"
//		2805 print "north or south"

//		2800 print "maze"
//		2805 print "north or south"
//		2810 gosub 25000
//		2820 if a$="north" then goto 3300
//		2830 if a$="south" then goto 2000
//		2840 if a$="look" then goto 2800
//		2898 print "you can't do that here"
//		2899 goto 2810
//		2900 print "maze"
//		2905 print "east or south"
//		2910 gosub 25000
//		2920 if a$="east" then goto 3500
//		2930 if a$="south" then goto 3900
//		2940 if a$="look" then goto 2900
//		2998 print "you can't do that here"
//		2999 goto 2900

//		3000 print "alice room"
//		3005 print "you are in a room with an exit to the west and a mirror on the east wall."

//		3100 print "air room"
//		3101 print "you are in a small room with exits to the east and northwest"
//		3102 if iii$<>"air tanks" then print "there are air tanks here"

//		3300 print "maze"
//		3305 print "east, west, or north?"
//		3310 gosub 25000
//		3320 if a$="east" then goto 3500
//		3330 if a$="west" then goto 2800
//		3340 if a$="north" then goto 3600
//		3350 if a$="look" then goto 3360
//		3398 print "you can't do that here"
//		3399 goto 3310

//		3400 print "in-a-box"
//		3500 print "maze"
//		3505 print "west, south, or east?"
//		3510 gosub 25000
//		3520 if a$="west" then goto 3300
//		3530 if a$="south" then goto 2900
//		3540 if a$="east" then goto 3700
//		3550 if a$="look" then goto 3500
//		3598 print "try again"
//		3599 goto 3510
//
//		3600 print "small room in the maze"
//		3605 print "north, west, or east?"
//		3606 if is$<>"sticky substance" then sklfihv$="sakgb":if is$<>"honey" then print "there is a sticky subtance here."
//		3610 gosub 25000
//		3620 if left$(a$,4)="take" then print "done":print:is$="sticky substance":goto 3610
//		3630 if a$="east" then goto 3700
//		3640 if a$="west" then goto 3500
//		3650 if a$="north" then goto 3800
//		3660 if a$="look" then goto 3600
//		3698 print "you can't do that.(dork)"
//		3699 goto 3610
//		3700 print "maze"
//		3705 print "north, south, east, or west?"
//		3710 gosub 25000
//		3720 if a$="north" then goto 3600
//		3725 if a$="look" then goto 3700
//		3730 if a$="west" then goto 3500
//		3740 if a$="east" then goto 4200
//		3750 if a$="south" then goto 4300
//		3798 print "try again"
//		3799 goto 3710
//		3800 print "dead end"
//		3805 print "you can go south. (hint: he was a hero)"
//		3810 gosub 25000
//		3820 if a$="south" then goto 3600
//		3830 if a$="look" then goto 3800
//		3840 if a$="north" then print"you stumble upon a secret passage.":goto 6100
//		3898 print "try again"
//		3899 goto 3810

//		3900 print "bee room"
//		3901 print "this is a small room.  in the middle of the room is a bee hive.  the exits are  to the east, north, and southwest.  the room is filled with bees."
//		3902 if is$<>"" then print "the bees are flocking to your honey.":if it$<>"gold coin" then print "  there is a coin here.":is$="honey"

//		4000 print "merchant room"
//		4001 print "a small room with four walls.  there is a merchant here.  you can go west."
//		4002 if iz$="key" then goto 4010
//		4003 print "the merchant will give you a key if you give him your valuables."
//		4004 print "to give stuff to the merchant type 'give ";:color 12:print "item";:color 15:print "'"

//		4100 print "flute room"
//		4101 print "a small room with boards nailed over doors to the north and south.  exit are to the east and up."
//		4102 if iu$<>"flute" then print "there is a flute here."

//		4200 print "maze"
//		4205 print "east, west, or south?"
//		4210 gosub 25000
//		4220 if a$="east" then goto 4200
//		4230 if a$="west" then goto 3700
//		4240 if a$="south" then goto 4300
//		4250 if a$="look" then goto 4200
//		4298 print "try another command"
//		4299 goto 4210
//		4300 print "maze"
//		4305 print "up, north, or east?"
//		4310 gosub 25000
//		4320 if a$="look" then goto 4300
//		4330 if a$="up" then goto 5000
//		4340 if a$="north" then goto 3700
//		4350 if a$="east" then goto 4200
//		4360 if a$="look" then goto 4300
//		4398 print "try something new"
//		4399 goto 4310

//		4400 print "slope room"
//		4405 print "the whole room is at an angle with an exit downward."
//		4406 if iv$<>"strong whisky" then print "there is a bottle here."

//		4500 print "clockwork room"
//		4505 print "above you head there is an intricate system of clockwork gears operating a bell every 10 seconds.  an exit lead south and another lead east."

//		4600 print "prisoner room"
//		4602 print "there is an exit to the north and south."
//		4604 if iw$<>"bloody knife" then print "there is a body here with a knife sticking in to it."

//		4700 print "bandit room ii"
//		4702 print "exits:north and east"

//		4800 print "ogre room"
//		4805 print "in this room there are about five complete skeletons and at least twenty-five   parts of other adventurers.  the exit is to the northwest.":if ix$<>"jade staff" then print "there is a jade staff here."
//		4806 if ogre$<>"ogre" then print "there is and ogre here finishing his last meal." else print "there is a distinct lack of an ogre here"

//		4900 print "west hall"
//		4901 print "in this room the wall gradually slopes to form the high ceiling.  there are many  benches here.  the exits are to the southeast, south, and north."

//		5000 print "maze"
//		5002 print "down or southeast?"
//		5010 gosub 25000
//		5020 if a$="look" then goto 5000
//		5030 if a$="southeast" then goto 4800
//		5040 if a$="down" then goto 4300
//		5098 print "ok":input "",bgt$
//		5099 goto 5010

//		5102 print "dark room"
//		5104 print "this room is filled with a magic darkness so powerful that only your bright     sphere can cut through it.  the exit is to the south."
//		5106 if iaa$<>"gold watch" then print "the floor consists of several loose boards that look like they can be moved."

//		5200 print "gas room"
//		5202 let mg=mg+1:if mg=1 then print "a gas begins to fill the room!"
//		5204 if mg>1 then print "there is a gas in this room!"
//		5206 print "there are exits to the north and southeast."

//		5300 print "bandit hall"
//		5302 print "this is a long wide room with tall ceilings.  beams with intricut ingravings    suport the wall.  exits are to the west and northwest."
//		5304 if iab$<>"brass shield" then print "there is a brass shield on the wall"

//		5400 print "music room":flute=1
//				5402 if music=0 then print "the exit is to the east.  the room is drab.  the walls are gray."
//				5404 if music=1 then print "the room seems to come alive...  you begin to notice bright patches of color    which you did not notice before.  a red door appears to the south.  there is an exit to the east."

//		5500 if falss<>2 then print "waterfall" else print "base of cliff"
//		5502 if falss<>2 then print "you are the foot of the spectacular flathead falls.  next to you is the water   crashing down on the rocks creating a refreshing mist.  there is a red door in  the cliff face to the north.":goto 5504
//		5503 print "you are standing at the foot of the spectacular flathead no-falls. next to you  is the no-water crashing down creating a not-refreshing no-mist.  there is a reddoor to the north."
//		5504 if falls=1 then print "you can also go east."
//		5505 if falls=0 then print "(hint:  it involves a small spark of minor magic.)"

//		5600 print "behind waterfall"
//		5602 print "there is an exit to the west":if iac$<>"pixie dust" then print "there is some pixie dust laying on the rocks here."

//		5900 print"dungeon":print "this room is a dark and dirty dungeon cell much like many others.  there are    skeletons chainned to the wall here.    there are exits to the north, south, and west."

// 		6000 print "closet"
//		6002 print "you are standing in a closet.  the exit is to the east."
//		6004 if iad$<>"pearl necklace" then print "you see a pearl necklace on a shelf here."
//		6100 print "pointless room ii"
//		6102 print "you can hear what appears to be children laughing in the distance.  on the northwall in an inscription.  above you is a songbird flying in circles.  above the  door to the south is a picture.  there is a window here through which you see a"
//		6103 print "dork.  oh never mind. it wasn't a window after all...just a little mirror.      there is also a door to the north."
//		6200 print"specious shrine"
//		6202 print"as you take a quick look around the room, you immediately realize that a large  altar occupies much of the northern half of the room.  on the altar is a burnt  offering of some kind, and behind the altar is a huge carving with something ":
//		6203 print"written at its base.  at your feet lies a monk.  you can go east or south."
//		6300 print "the room to end all rooms"
//		6302 print "the room you are standing in is apparently part of a house owned by a very      insane or very eccentric dwarf.  the exits are to the north, into what seems to be the entrance to a cave;  west through a revolving glass door;  and down a  "
//		6303 print "spiral staircase;  there is an exit to the east through a hole in the wall just large enough to crawl through."
//		6400 print "the floor is mushy.  north or south."
//		6440 print "dead end... blocked by hamburgers."
//		6600 print "beaver room"
//		6602 print "there is an exit to the west.  there is a no-beaver here."
//		6700 print "yet another gnome room"
//		6702 if inot$<>"book" then print "there is a book here and a rope ladder leading up." else print "you can go up."

		doNavigate(cavern);
	}

	public static void ogInit() {
		Room dark = new Room("Dark Room",
				"You are in a dark room. To your north is a passageway leading north. It looks very... northward.");
		Room ncavern = new Room("North Cavern",
				"You are in the great north cavern.  It is really great! :-) To the south is a passageway leading back to the dark room. Also, you can go east.");
		Room fountain = new Room("Fountain Room",
				"You are in a mysterious room with a large fountain in the center. A door is to your west, returning you to the great cavern.");

		/* Item brasskey = */new Item("The Brass Key",
				"A key made of solid brass and encrusted with jewels. It shimmers in the torchlight of the dungeon, but only when you're in a room with torches.",
				syn("key", "brass key", "the key", "the brass key")).inRoom(dark).isLightsource(true);

		dark.addexit("north", ncavern);
		ncavern.addexit("east", fountain);
		fountain.details.add(new Item("fountain",
				"An ornate fountain. You feel invigorated just being in its presence. Also, you love it.",
				syn("fountain", "the fountain")));

		dark.triggers.add(new Trigger().addRequirement(Trigger.createCommandReq("hello"))
				.addAction(Trigger.createMessageAction("eat dirt ok")));

		Item chest = new Item("Chest", "A wooden chest.", syn("chest")).isFixed(true).isOpenable(true).inRoom(dark);
		Item coin = new Item("Coin", "A golden coin, that is made of gold.", syn("coin")).inContainer(chest);

		Item whistle = new Item("Steel Whistle", "A steel whistle.", syn("steel whistle", "whistle")).inRoom(dark);
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

		dark.triggers.add(paytollaction);

		dark.triggers.add(cantpaytollaction);

		Item flashlight = new Item("Flashlight", "A yellow flashlight", syn("flashlight")).inRoom(dark);
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
