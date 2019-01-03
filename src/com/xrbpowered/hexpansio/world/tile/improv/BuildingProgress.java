package com.xrbpowered.hexpansio.world.tile.improv;

import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.tile.Tile;

public abstract class BuildingProgress {

	public final City city;
	public Tile tile;
	public int progress = 0;
	
	public BuildingProgress(City city, Tile tile) {
		this.city = city;
		setTile(tile);
	}
	
	public void setTile(Tile tile) {
		this.tile = tile;
	}
	
	public boolean nextTurn(int prod) {
		progress += prod;
		if(progress>=getCost()) {
			progress -= getCost();
			complete();
			return true;
		}
		else
			return false;
	}
	
	public abstract String getName();
	public abstract int getCost();
	public abstract void complete();
	
	public void cancel() {
	}
	
	public boolean canHurry() {
		return true;
	}
	
}
