package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public abstract class MarketUpgrades {

	public static void init() {
		new Improvement(market, "Trade Rows", 30, 1).workplaces(1).yieldPerWorker(0, 0, 1, 0);
		new Improvement(market, "Fun Fair", 60, 1).workplaces(1).yield(0, 0, 1, 1).yieldPerWorker(0, 0, 0, 1);
		new Improvement(market, "Grocery", 30, 1).yield(1, 0, 0, 0);
		new Improvement(market, "Warehouse", 40, 1).maintenance(1).yield(0, 2, 0, 0);
		new Improvement(market, "Office", 80, 1).workplaces(1).yield(0, 0, 2, 0).yieldPerWorker(0, 0, 1, 0);
		
		new Improvement(market, "Bank", 100, 2).workplaces(1).maintenance(1)
		.effects(new YieldEffect.Tile(0, 0, 1, 0) {
			@Override
			public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
				return ImprovementStack.tileContains(tile, market) ? this.yield.get(res) : 0;
			}
			@Override
			public String getDescription() {
				return "+1 Gold from every Market";
			}
		});
		
		new Improvement(market, "Trade Hub", 120, 3).cityUnique().maintenance(2)
			.effects(new YieldEffect.Resource(0, 0, 1, 0) {
				@Override
				public String getDescription() {
					return "+1 Gold from all resources";
				}
			});
	}
	
}
