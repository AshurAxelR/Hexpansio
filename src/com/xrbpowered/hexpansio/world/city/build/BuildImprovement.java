package com.xrbpowered.hexpansio.world.city.build;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;

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
	public boolean canHurry() {
		return improvement.canHurry;
	}
	
	@Override
	public void complete() {
		if(tile.improvement==null)
			tile.improvement = new ImprovementStack(improvement);
		else
			tile.improvement.add(improvement);
	}
}