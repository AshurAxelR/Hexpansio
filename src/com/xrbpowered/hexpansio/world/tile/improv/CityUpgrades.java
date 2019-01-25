package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.CityEffect;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class CityUpgrades {

	public static final float beaconOfHopeEffect = 0.8f;
	public static final float highriseEffect = 0.5f;

	public static final Improvement townHall = new Improvement(cityCenter, "Town Hall", 100, 0).requirePopulation(5).maintenance(5).yield(0, 0, 0, 2).effects(CityEffect.add(EffectTarget.upgPoints, 2));
	
	public static final Improvement beaconOfHope = new Improvement(cityCenter, "Beacon of Hope", 50, 0).maintenance(5).yield(0, 0, 0, 2).voidUnlock()
			.effects(
					CityEffect.dummy(String.format("Unhappiness from void -%d%%", (int)(beaconOfHopeEffect*100f))),
					CityEffect.dummy("Allows building Voidworks")
				);
	
	public static Improvement highrise = null;

	public static void init() {
		new Improvement(cityCenter, "Food Reserve", 30, 1).maintenance(1).yield(2, 0, 0, 0);
		new Improvement(townHall, "Utopia", 200, 1).requirePopulation(5).cannotHurry().maintenance(5).effects(CityEffect.add(EffectTarget.baseHappiness, 1));
		
		new Improvement(cityCenter, "Harbour", 60, 1).maintenance(2).requireCoastalCity()
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

		new Improvement(cityCenter, "Solar Power", 40, 1).maintenance(2)
		.effects(new YieldEffect.Tile(0, 1, 0, 0) {
			@Override
			public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
				return tile.terrain.feature==Feature.desert ? this.yield.get(res) : 0;
			}
			@Override
			public String getDescription() {
				return "+1 Production from desert tiles";
			}
		});

		new Improvement(cityCenter, "Holiday Resort", 200, 2).yield(0, 0, 1, 1).requireCoastalCity()
			.effects(new YieldEffect.Tile(0, 0, 0, 1) {
				@Override
				public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
					return tile.terrain.feature==Feature.water ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Happiness from water tiles";
				}
			});
		
		new Improvement(cityCenter, "Industrial Zone", 150, 1).maintenance(3).yield(0, 2, 0, 0)
			.effects(new YieldEffect.Resource(0, 1, 0, 0) {
				@Override
				public int addResourceBonusYield(TokenResource resource, YieldResource res) {
					return resource==TokenResource.iron || resource==TokenResource.fuel ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Production from Iron and Fuel";
				}
			});
		
		highrise = new Improvement(townHall, "Highrise", 300, 3).requirePopulation(15).cannotHurry().maintenance(10)
				.effects(CityEffect.dummy(String.format("Unhappiness from population -%d%%", (int)(highriseEffect*100f))));
		new Improvement(highrise, "Downtown", 400, 1).cannotHurry().yield(0, 0, 3, 3);

	}
	
}
