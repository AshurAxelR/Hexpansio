package com.xrbpowered.hexpansio.world.tile.improv;

import static com.xrbpowered.hexpansio.world.tile.improv.Improvement.*;

import com.xrbpowered.hexpansio.world.city.effect.CityEffect;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.city.effect.YieldEffect;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public abstract class VoidUpgrades {

	public static void init() {
		new Improvement("void.farm", voidworks, "Hydropinics", 40, 2).maintenance(1).yield(1, 0, 0, 0).yieldPerWorker(1, 0, 0, 0)
			.reject((Feature[])null);
		new Improvement("void.booster", voidworks, "Booster", 20, 1).yieldPerWorker(0, 1, 0, 0)
			.reject((Feature[])null);
		new Improvement("void.hologram", voidworks, "Hologram", 100, 2).maintenance(2).yield(0, 0, 0, 1).yieldPerWorker(0, 0, 0, 1)
			.reject((Feature[])null);
		new Improvement("void.power", voidworks, "Power Plant", 50, 1).yieldPerWorker(0, 0, 1, 0)
			.reject((Feature[])null);
		
		new Improvement("void.economy", voidworks, "Void Economy", 200, 3).cityUnique().yield(0, 0, 2, 0).yieldPerWorker(0, 0, 1, 0)
			.reject((Feature[])null)
			.effects(new CityEffect() {
				@Override
				public int tileEffect(EffectTarget key, Tile tile) {
					return key==EffectTarget.workplaces && ImprovementStack.tileContains(tile, voidworks) ? 1 : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Workplace to every Voidworks";
				}
			}, new YieldEffect.Tile(0, 0, 1, 0) {
				@Override
				public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
					return ImprovementStack.tileContains(tile, voidworks) ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Gold from Voidworks";
				}
			});
		
		new Improvement("void.forge", voidworks, "Void Forge", 150, 2).maintenance(2).cityUnique().yield(0, 3, 0, 0)
			.reject((Feature[])null)
			.effects(new YieldEffect.Tile(0, 1, 0, 0) {
				@Override
				public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
					return ImprovementStack.tileContains(tile, voidworks) ? this.yield.get(res) : 0;
				}
				@Override
				public String getDescription() {
					return "+1 Production from Voidworks";
				}
			});
	}

}
