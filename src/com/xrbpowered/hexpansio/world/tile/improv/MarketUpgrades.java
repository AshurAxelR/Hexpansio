package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class MarketUpgrades {

	public static final Improvement office = new Improvement("market.office", market, "Office", 150, 1).workplaces(1).yield(0, 0, 2, 0).yieldPerWorker(0, 0, 1, 0).cityPrerequisite(CityUpgrades.townHall)
			.reject(Feature.forest, Feature.swamp);

	public static void init() {
		new Improvement("market.rows", market, "Trade Rows", 60, 1).workplaces(1).yield(0, 0, 1, 0).yieldPerWorker(0, 0, 1, 0);
		new Improvement("market.fair", market, "Fun Fair", 60, 1).maintenance(1).yield(0, 0, 0, 2).cityUnique();
		new Improvement("market.grocery", market, "Grocery", 30, 1).yield(1, 0, 1, 0);
		new Improvement("market.warehouse", market, "Warehouse", 40, 1).maintenance(1).yield(0, 2, 0, 0);
		
		new Improvement("market.bank", market, "Bank", 200, 2).maintenance(2).workplaces(1).cityPrerequisite(CityUpgrades.townHall).cannotHurry().cityUnique()
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
		
		new Improvement("market.hub", market, "Stock Exchange", 250, 2).cityUnique().workplaces(1).maintenance(2).cityPrerequisite(CityUpgrades.townHall).cannotHurry()
			.reject(Feature.forest, Feature.swamp).effects(new YieldEffect.Resource(0, 0, 1, 0) {
				@Override
				public String getDescription() {
					return "+1 Gold from all resources";
				}
			});
	}
	
}
