package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class OtherUpgrades {

	public static void init() {
		new Improvement("lumbermill.forester", lumberMill, "Forester", 80, 1).yield(1, 1, 1, 0);
		new Improvement("lumbermill.saw", lumberMill, "Sawmill", 60, 1).maintenance(1).yield(0, 3, 0, 0);

		new Improvement("pasture.housing", pasture, "Animal Housing", 60, 1).yield(1, 1, 0, 0).bonusResources(1);
		new Improvement("pasture.stables", pasture, "Stables", 60, 1).maintenance(1).yield(0, 1, 0, 1);
		new Improvement("pasture.mountaineer", pasture, "Mountaineer", 80, 1).yield(0, 1, 1, 1)
			.require(Feature.mountain);
		
		new Improvement("plantation.irrigation", plantation, "Irrigation Systems", 40, 1).yield(1, 0, 1, 0).bonusResources(1)
			.require(Feature.desert);
		new Improvement("plantation.intensive", plantation, "Intensive Farming", 60, 1).workplaces(1).yieldPerWorker(1, 0, 1, 0).bonusResources(1)
			.reject(Feature.desert);
		
		new Improvement("quarry.excavator", quarry, "Excavator", 60, 1).maintenance(1).yield(0, 1, 0, 0).bonusResources(1);
		new Improvement("quarry.deep", quarry, "Deep Quarry", 40, 1).workplaces(1).yieldPerWorker(0, 1, 0, 0);
		new Improvement("quarry.giant", quarry, "Giant Excavator", 150, 3).cityUnique().maintenance(3).yield(0, 3, 0, 0).bonusResources(2);
		new Improvement("quarry.civileng", quarry, "Civil Engineering", 80, 2).cityUnique().workplaces(2).maintenance(1).cityPrerequisite(CityUpgrades.townHall)
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

		new Improvement("boat.crew", boat, "Crew Training", 60, 1).workplaces(1).yieldPerWorker(1, 0, 1, 0).bonusResources(1)
			.reject((Feature[])null);
		new Improvement("boat.yacht", boat, "Luxury Yacht", 60, 1).maintenance(2).yield(0, 0, 0, 3).cityPrerequisite(CityUpgrades.resort)
			.reject((Feature[])null);
		new Improvement("boat.cargo", boat, "Cargo Ship", 80, 1).maintenance(2).yield(0, 2, 0, 0)
			.reject((Feature[])null);

		new Improvement("boat.pump", drill, "Power Pump", 60, 1).yield(0, 1, 1, 0).bonusResources(1)
			.reject((Feature[])null);
		new Improvement("boat.refinery", drill, "Refinery", 80, 1).workplaces(1).maintenance(1).yieldPerWorker(0, 2, 0, 0).bonusResources(1);
		new Improvement("boat.chemical", drill, "Chemical Plant", 120, 2).cityUnique().workplaces(1).maintenance(2)
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
