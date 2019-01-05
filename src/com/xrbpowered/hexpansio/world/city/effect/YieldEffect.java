package com.xrbpowered.hexpansio.world.city.effect;

import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public abstract class YieldEffect extends CityEffect {

	protected final Yield.Set yield = new Yield.Set();
	
	public YieldEffect(int food, int production, int gold, int happiness) {
		this.yield.set(food, production, gold, happiness);
	}

	public static abstract class Tile extends YieldEffect {
		public Tile(int food, int production, int gold, int happiness) {
			super(food, production, gold, happiness);
		}
		@Override
		public int addTileYield(com.xrbpowered.hexpansio.world.tile.Tile tile, YieldResource res) {
			return this.yield.get(res);
		}
	}
}
