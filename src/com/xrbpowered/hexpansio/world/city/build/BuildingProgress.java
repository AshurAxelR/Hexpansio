package com.xrbpowered.hexpansio.world.city.build;

import com.xrbpowered.hexpansio.world.city.City;
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
	
	public boolean progress(int prod) {
		progress += prod;
		return checkComplete();
	}
	
	public boolean checkComplete() {
		if(progress>=getCost()) {
			progress -= getCost();
			city.finishedBuilding = new FinishedBuilding(this);
			complete();
			city.world.goods += city.finishedBuilding.excess;
			city.buildingProgress = null;
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
