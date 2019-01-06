package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public abstract class ParkUpgrades {

	public static void init() {
		new Improvement(park, "Community Garden", 30, 1).yield(1, 0, 0, 1);
		new Improvement(park, "Monument", 10, 0).maintenance(1).yield(0, 0, 0, 1);
		new Improvement(park, "Museum", 80, 2).maintenance(3).yield(0, 0, 0, 3);
		
		final Improvement shrine = new Improvement(park, "Shrine", 20, 1).maintenance(1).yieldPerWorker(0, 0, 0, 1);
		new Improvement(shrine, "Cathedral", 100, 2).workplaces(1).maintenance(3).yieldPerWorker(0, 0, 1, 2);
		
		final Improvement school = new Improvement(park, "School", 60, 1).workplaces(1).maintenance(2).yieldPerWorker(0, 1, 0, 1);
		new Improvement(school, "Library", 60, 1).maintenance(1).yieldPerWorker(0, 0, 0, 1);
		new Improvement(school, "University", 300, 2).cityUnique().maintenance(5).workplaces(3).yield(0, 0, 0, 3)
			.effects(new YieldEffect.Tile(0, 0, 0, 1) {
				@Override
				public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
					return ImprovementStack.tileContains(tile, school) ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Happiness from every School";
				}
			});
	}
	
}
