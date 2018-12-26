package com.xrbpowered.hexpansio.world;

import com.xrbpowered.hexpansio.world.tile.Improvement;
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

	public static class BuildImprovement extends BuildingProgress {
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
			tile.improvement = improvement;
		}
	}

	public static class RemoveImprovement extends BuildingProgress {
		public RemoveImprovement(Tile tile) {
			super(tile.city, tile);
		}
		
		@Override
		public String getName() {
			return "Remove "+tile.improvement.name;
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

	public static class BuiltSettlement extends BuildingProgress {
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
	
}
