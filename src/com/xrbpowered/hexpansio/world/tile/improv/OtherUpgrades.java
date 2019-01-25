package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class OtherUpgrades {

	public static void init() {
		new Improvement(lumberMill, "Forester", 80, 1).yield(1, 1, 1, 0);
		new Improvement(lumberMill, "Sawmill", 60, 1).maintenance(1).yield(0, 3, 0, 0);

		new Improvement(pasture, "Animal Housing", 60, 1).yield(1, 1, 0, 0).bonusResources(1);
		new Improvement(pasture, "Stables", 60, 1).maintenance(1).yield(0, 1, 0, 1);
		new Improvement(pasture, "Mountaineer", 80, 1).yield(0, 1, 1, 1)
			.require(Feature.mountain);
		
		new Improvement(plantation, "Irrigation Systems", 40, 1).yield(1, 0, 1, 0).bonusResources(1)
			.require(Feature.desert);
		new Improvement(plantation, "Intensive Farming", 60, 1).workplaces(1).yieldPerWorker(1, 0, 1, 0).bonusResources(1)
			.reject(Feature.desert);
		new Improvement(plantation, "Fair Market", 60, 1).workplaces(1).yield(0, 0, 2, 1);
		
		new Improvement(quarry, "Excavator", 60, 1).yield(0, 1, 1, 0).bonusResources(1);
		new Improvement(quarry, "Deep Quarry", 40, 1).workplaces(1).yieldPerWorker(0, 1, 0, 0);
		new Improvement(quarry, "Giant Excavator", 150, 2).cityUnique().maintenance(1).yield(0, 3, 0, 0).bonusResources(2);
		new Improvement(quarry, "Civil Engineering", 80, 2).cityUnique().workplaces(2).maintenance(1).cityPrerequisite(CityUpgrades.townHall)
			.effects(new YieldEffect.Resource(0, 1, 0, 0) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.stone || resource==TokenResource.clay || resource==TokenResource.sand ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Production from Stone, Clay, and Sand";
				}
			});

		new Improvement(boat, "Crew Training", 60, 1).workplaces(1).yieldPerWorker(1, 0, 1, 0).bonusResources(1)
			.reject((Feature[])null);
		new Improvement(boat, "Luxury Yacht", 60, 1).maintenance(3).yield(0, 0, 0, 3)
		.reject((Feature[])null);
		new Improvement(boat, "Cargo Ship", 80, 1).maintenance(2).yield(0, 2, 0, 0)
			.reject((Feature[])null);

		new Improvement(drill, "Power Pump", 40, 1).yield(0, 1, 1, 0).bonusResources(1)
			.reject((Feature[])null);
		new Improvement(drill, "Refinery", 80, 1).workplaces(1).maintenance(1).yieldPerWorker(0, 2, 0, 0).bonusResources(1);
		new Improvement(drill, "Chemical Plant", 80, 2).cityUnique().workplaces(1).maintenance(2)
		.effects(new YieldEffect.Resource(0, 1, 1, 0) {
			@Override
			public int addResourceBonusYield(TokenResource resource, YieldResource res) {
				return resource==TokenResource.fuel ? this.yield.get(res) : 0;
			}
			@Override
			public String getDescription() {
				return "+1 Gold and +1 Production from Fuel";
			}
		});
	}
	
}
