package com.xrbpowered.hexpansio.world.city.build;

import com.xrbpowered.hexpansio.world.tile.Tile;

public class RemoveImprovement extends BuildingProgress {
	
	public RemoveImprovement(Tile tile) {
		super(tile.city, tile);
	}
	
	@Override
	public String getName() {
		return "Remove "+tile.improvement.base.name;
	}
	
	@Override
	public int getCost() {
		return 10;
	}
	
	@Override
	public void complete() {
		tile.improvement = null;
	}
}