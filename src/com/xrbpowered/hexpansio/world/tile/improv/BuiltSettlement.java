package com.xrbpowered.hexpansio.world.tile.improv;

import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class BuiltSettlement extends BuildingProgress {
	
	public BuiltSettlement(City city, Tile tile) {
		super(city, tile);
		city.unemployed--;
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
		return 50;
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
		city.updateStats();
		city.world.updateWorldTotals();
	}
}