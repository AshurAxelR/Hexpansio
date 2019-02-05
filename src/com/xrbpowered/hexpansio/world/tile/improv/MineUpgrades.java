package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.CityEffect;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class MineUpgrades {

	public static void init() {
		new Improvement("mine.guild", mine, "Miners' Guild", 40, 1).workplaces(2).yieldPerWorker(0, 1, 0, 0);
		new Improvement("mine.strip", mine, "Strip Mine", 80, 2).requireResource().workplaces(1).maintenance(3).bonusResources(2);
		new Improvement("mine.auto", mine, "Automated Mining", 60, 1).maintenance(1).bonusResources(1).yieldPerWorker(0, 2, 0, 0);
		new Improvement("mine.factory", mine, "Factory", 100, 2).workplaces(3).yieldPerWorker(0, 1, 0, 0)
			.reject(Feature.mountain, Feature.peak).effects(CityEffect.add(EffectTarget.maxGoods, 20));
		
		new Improvement("mine.forge", mine, "Forge", 60, 1).cityUnique().maintenance(1)
			.effects(new YieldEffect.Resource(0, 2, 0, 0) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.iron ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+2 Production from Iron";
				}
			});
		
		new Improvement("mine.jewelcrafts", mine, "Jewelcrafts", 100, 2).cityUnique().workplaces(1).yieldPerWorker(0, 0, 1, 0)
			.effects(new YieldEffect.Resource(0, 0, 2, 0) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.gold || resource==TokenResource.gems ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+2 Gold from Gold and Gems";
				}
			});
	}
	
}
