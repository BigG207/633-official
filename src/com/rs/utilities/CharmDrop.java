// This program is free software: you can redistribute it and/or modify
package com.rs.utilities;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Scanner;

import com.rs.game.item.ItemConstants;
import com.rs.game.npc.drops.DropTable;

public class CharmDrop {

	private final static String PACKED_PATH = "data/npcs/charmDrops.txt";
	private static HashMap<String, int[]> charmDrops;
	final static Charset ENCODING = StandardCharsets.UTF_8;

	public static int getCharmAmount(String npcName) {
		switch(npcName) {
		case "brutal green dragon":
		case "iron dragon":
		case "rock lobster":
		case "skeletal wyvern":
			return 2;
		case "black dragon":
		case "giant rock crab":
		case "glacor":
		case "steel dragon":
		case "tormented demon":
			return 3;
		case "king black dragon":
		case "mithril dragon":
			return 4;
		case "corporeal beast":
			return 13;
		case "nex":
			return 20;
		default:
			return 1;
		}
	}

	public static int getCharmType(int[] chances) {
		int goldRate = chances[0];
		int greenRate = chances[1];
		int crimRate = chances[2];
		int blueRate = chances[3];

		ArrayList<Integer> possibleCharms = new ArrayList<>();

		int rand = RandomUtils.inclusive(100);
		if (rand <= (blueRate) && blueRate != 0)
			possibleCharms.add(3);
		if (rand <= (crimRate) && crimRate != 0)
			possibleCharms.add(2);
		if (rand <= (greenRate) && greenRate != 0)
			possibleCharms.add(1);
		if (rand <= (goldRate) && goldRate != 0)
			possibleCharms.add(0);
		if (possibleCharms.isEmpty())
			return -1;
		Collections.shuffle(possibleCharms);
		return possibleCharms.get(RandomUtils.random(possibleCharms.size()));
	}

	public static DropTable getCharmDrop(String npcName) {
		int[] chances = charmDrops.get(npcName.toLowerCase().replace(" ", "_"));
		if (chances == null)
			return null;
		int charmIndex = getCharmType(chances);
		int amount = getCharmAmount(npcName.toLowerCase());

		if (charmIndex == -1)
			return null;

		DropTable charm = new DropTable(0, 0, ItemConstants.CHARM_IDS[charmIndex], amount, amount);
		return charm;
	}

	public static void loadCharmDrops() {
		try {
			charmDrops = new HashMap<>();
			Path path = Paths.get(PACKED_PATH);
			try (Scanner scanner = new Scanner(path, ENCODING.name())) {
				String npcName = null;
				int[] charmPerc = new int[4];

				while (scanner.hasNextLine()) {
					String line = scanner.nextLine();

					if (line.startsWith("//") || line.isEmpty())
						continue;

					String[] subs = line.split(":");
					String[] info = subs[1].split("-");

					npcName = subs[0];
					charmPerc[0] = Integer.parseInt(info[0]);
					charmPerc[1] = Integer.parseInt(info[1]);
					charmPerc[2] = Integer.parseInt(info[2]);
					charmPerc[3] = Integer.parseInt(info[3]);
					charmDrops.put(npcName, new int[] {charmPerc[0], charmPerc[1], charmPerc[2], charmPerc[3]});
				}

			}
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

}
