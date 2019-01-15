package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

public abstract class OtherUpgrades {

	public static void init() {
		new Improvement(lumberMill, "Forestry", 60, 1).yield(1, 1, 0, 0);
		new Improvement(lumberMill, "Sawmill", 60, 1).yield(0, 2, 0, 0);

		new Improvement(gatherer, "Cabin", 40, 1).yield(1, 1, 0, 0).bonusResources(1);
		new Improvement(gatherer, "Pathfinder", 40, 1).yield(1, 1, 0, 0);
		
		new Improvement(pasture, "Animal Housing", 60, 1).yield(1, 1, 0, 0).bonusResources(1);
		new Improvement(pasture, "Stables", 60, 1).maintenance(1).yield(0, 1, 0, 1);
		
		new Improvement(plantation, "Intensive Farming", 40, 1).yield(1, 0, 1, 0).bonusResources(1);
		new Improvement(plantation, "Cheap Labour", 60, 1).workplaces(1).yieldPerWorker(1, 0, 1, 0).bonusResources(1);
	}
	
}
