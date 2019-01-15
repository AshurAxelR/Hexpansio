package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public abstract class FarmUpgrades {

	public static void init() {
		new Improvement(farm, "Fertiliser", 30, 1).maintenance(1).yield(3, 0, 0, 0);
		new Improvement(farm, "Greenhouse", 40, 1).workplaces(1).yieldPerWorker(1, 0, 0, 0);
		new Improvement(farm, "Green Market", 60, 1).workplaces(1).yield(1, 0, 1, 0).yieldPerWorker(0, 0, 1, 0);
		
		new Improvement(farm, "Granary", 60, 1).cityUnique().maintenance(1).bonusResources(1)
			.effects(new YieldEffect.Resource(1, 0, 0, 0) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.grain ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Food from Grain";
				}
			});
		
		new Improvement(farm, "Distillery", 80, 2).cityUnique().workplaces(1).maintenance(2).yield(0, 2, 0, 0)
			.effects(new YieldEffect.Resource(0, 0, 0, 1) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.grain || resource==TokenResource.berries || resource==TokenResource.cacti ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Happiness from Grain, Berries, and Cacti";
				}
			});
	}

}
