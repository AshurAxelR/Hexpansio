package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class MarketUpgrades {

	public static final Improvement office = new Improvement(market, "Office", 150, 1).workplaces(1).yield(0, 0, 2, 0).yieldPerWorker(0, 0, 1, 0).cityPrerequisite(CityUpgrades.townHall)
			.reject(Feature.forest, Feature.swamp);

	public static void init() {
		new Improvement(market, "Trade Rows", 30, 1).workplaces(1).yield(0, 0, 1, 0).yieldPerWorker(0, 0, 1, 0);
		new Improvement(market, "Fun Fair", 80, 1).yield(0, 0, 1, 1);
		new Improvement(market, "Grocery", 20, 1).yield(1, 0, 1, 0);
		new Improvement(market, "Warehouse", 40, 1).maintenance(1).yield(0, 2, 0, 0);
		
		new Improvement(market, "Bank", 250, 2).maintenance(1).cityPrerequisite(CityUpgrades.townHall)
			.reject(Feature.forest, Feature.swamp).effects(new YieldEffect.Tile(0, 0, 1, 0) {
				@Override
				public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
					return ImprovementStack.tileContains(tile, office) ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Gold from every Office";
				}
			});
		
		new Improvement(market, "Stock Exchange", 150, 3).cityUnique().maintenance(2).cityPrerequisite(CityUpgrades.townHall)
			.reject(Feature.forest, Feature.swamp).effects(new YieldEffect.Resource(0, 0, 1, 0) {
				@Override
				public String getDescription() {
					return "+1 Gold from all resources";
				}
			});
	}
	
}
