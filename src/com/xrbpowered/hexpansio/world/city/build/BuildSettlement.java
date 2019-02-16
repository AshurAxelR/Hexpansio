package com.xrbpowered.hexpansio.world.city.build;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class BuildSettlement extends BuildingProgress {
	
	public static final int cost = 50;
	
	public BuildSettlement(City city, Tile tile) {
		super(city, tile);
	}
	
	@Override
	public void setTile(Tile tile) {
		super.setTile(tile);
		if(tile!=null)
			tile.settlement = this;
	}
	
	@Override
	public String getName() {
		return "Settlement";
	}
	
	@Override
	public int getCost() {
		return cost;
	}
	
	@Override
	public boolean canHurry() {
		return false;
	}
	
	@Override
	public void complete() {
		tile.settlement = null;
		city.population--;
		city.world.newCities.add(tile);
	}
	
	@Override
	public void cancel() {
		tile.settlement = null;
		city.unemployed++;
	}
}