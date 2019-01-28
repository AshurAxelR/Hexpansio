package com.xrbpowered.hexpansio.world.city.build;

import com.xrbpowered.hexpansio.world.city.City;

public class BuildMigration extends BuildingProgress {
	
	public static final int addImmigration = 6;
	public static final int unhappinessPerImmigration = 3;
	
	public BuildMigration(City city, City otherCity) {
		super(city, otherCity==null ? null : otherCity.tile);
	}
	
	@Override
	public String getName() {
		return "Migration";
	}
	
	@Override
	public int getCost() {
		return 20;
	}
	
	@Override
	public boolean canHurry() {
		return false;
	}
	
	@Override
	public void complete() {
		city.population--;
		tile.city.population++;
		tile.city.immigration += addImmigration;
	}
	
	@Override
	public void cancel() {
		city.unemployed++;
	}
}