package com.xrbpowered.hexpansio.world.tile.improv;

import com.xrbpowered.hexpansio.world.city.effect.CityEffect;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

public abstract class CityUpgrades {

	public static void init() {
		new Improvement(cityCenter, "Town Hall", 100, 0).maintenance(3).yield(0, 0, 0, 2).effects(CityEffect.add(EffectTarget.upgPoints, 2));
		
		new Improvement(cityCenter, "Harbour", 40, 1).maintenance(2).requireCoastalCity()
				.effects(new YieldEffect.Tile(1, 0, 1, 0) {
					@Override
					public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
						return tile.terrain.feature==Feature.water ? this.yield.get(res) : 0;
					}
					@Override
					public String getDescription() {
						return "+1 Food and +1 Gold from water tiles";
					}
				});
	}
	
}
