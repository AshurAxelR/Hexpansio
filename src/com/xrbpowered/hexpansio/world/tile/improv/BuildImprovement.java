package com.xrbpowered.hexpansio.world.tile.improv;

import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class BuildImprovement extends BuildingProgress {
	
	public final Improvement improvement;

	public BuildImprovement(City city, Improvement improvement) {
		super(city, null);
		this.improvement = improvement;
	}

	public BuildImprovement(Tile tile, Improvement improvement) {
		super(tile.city, tile);
		this.improvement = improvement;
	}
	
	@Override
	public String getName() {
		return improvement.name;
	}
	
	@Override
	public int getCost() {
		return improvement.buildCost;
	}
	
	@Override
	public void complete() {
		tile.improvement = new ImprovementStack(improvement);
	}
}