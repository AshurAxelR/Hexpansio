package com.xrbpowered.hexpansio.world;

import java.util.Random;

import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Improvement;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class City {

	public static final int expandRange = 3;
	public static final int growthCostFactor = 10;
	
	// game state
	public String name;
	public int population = 1;
	public int growth = 0;
	public BuildingProgress buildingProgress = null;
	//public Tile settlement = null;

	// intra-turn state
	public int availDiscover = 1;
	public int availExpand = 1;

	// data
	public final int index;
	public final World world; // init/load ...
	public Tile tile = null; // init vs load

	public int numTiles = 0; // updateStats ...
	public int foodIn, prodIn, goldIn, happyIn;
	public int foodOut, goldOut, happyOut;
	public int unemployed, workplaces;
	public Happiness happiness = Happiness.content;
	
	public City(World world, Tile tile, String name) {
		this.world = world;
		
		if(name==null || !rename(name))
			rename(generateName(world, tile));
		
		this.index = world.cities.size();
		world.cities.add(this);
		
		if(tile!=null) {
			setTile(tile);
			world.discoverArea(tile.wx, tile.wy, 1);
			for(Dir d : Dir.values())
				addTile(tile.wx+d.dx, tile.wy+d.dy);
		}
	}
	
	public static String generateName(World world, Tile tile) {
		Random random = tile==null ? new Random() : new Random(tile.getSeed(3856L));
		int i = 0;
		String name;
		do {
			if(i>=5) world.cityNameBaseLength++;
			name = NameGen.generate(random, world.cityNameBaseLength, 3);
			i++;
		} while(world.citiesByName.containsKey(name));
		return name;
	}
	
	public boolean rename(String name) {
		if(world.citiesByName.containsKey(name))
			return false;
		if(this.name!=null)
			world.citiesByName.remove(this.name);
		this.name = name;
		world.citiesByName.put(name, this);
		return true;
	}
	
	protected void setTile(Tile tile) {
		this.tile = tile;
		tile.region.cities.add(this);
		tile.city = this;
		tile.improvement = Improvement.city;
	}
	
	public void addTile(int wx, int wy) {
		world.addToCity(wx, wy, this);
	}
	
	public int getTargetGrowth() {
		return population*growthCostFactor;
	}
	
	public void setBuilding(BuildingProgress b) {
		if(buildingProgress!=null)
			buildingProgress.cancel();
		buildingProgress = b;
		updateStats();
		world.updateWorldTotals();
	}
	
	public boolean isBuildingSettlement() {
		return buildingProgress!=null && (buildingProgress instanceof BuildingProgress.BuiltSettlement);
	}
	
	public Tile getSettlement() {
		return isBuildingSettlement() ? buildingProgress.tile : null; 
	}
	
	public int getFoodGrowth() {
		return (foodIn>foodOut) ? (foodIn-foodOut)*(100-happiness.growthPenalty)/100 : (foodIn-foodOut);
	}

	public int getProduction() {
		return prodIn*(100-happiness.prodPenalty)/100;
	}

	public int getExcess(int prod) {
		return prod/4;
	}
	
	public void nextTurn() {
		updateStats();
		
		growth +=getFoodGrowth();
		if(growth<0) {
			if(isBuildingSettlement())
				buildingProgress.cancel();
			if(population>1) {
				population--;
				// FIXME unassign worker
				growth += getTargetGrowth();
			}
			else {
				growth = 0;
			}
		}
		else if(growth>=getTargetGrowth()) {
			growth -= getTargetGrowth();
			population++;
			// TODO auto assign
		}
		
		if(buildingProgress!=null) {
			if(buildingProgress.nextTurn(getProduction())) {
				world.gold += getExcess(buildingProgress.progress);
				buildingProgress = null;
			}
		}
		else {
			world.goods += getExcess(getProduction());
		}
		
		world.gold += goldIn-goldOut;
		
		availDiscover = 1;
		availExpand = 1;
		
		world.totalPopulation += population;
	}
	
	public void updateStats() {
		numTiles = 0;
		foodIn = 0;
		prodIn = 0;
		goldIn = 0;
		happyIn = 3;
		workplaces = 0;
		goldOut = 0;
		int workers = 0;
		for(int x=-expandRange; x<=expandRange; x++)
			for(int y=-expandRange; y<=expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==this) {
					numTiles++;
					if(!t.isCityCenter())
						workplaces++;
					if(t.workers>0 || t.isCityCenter()) {
						foodIn += t.yield.get(YieldResource.food);
						prodIn += t.yield.get(YieldResource.production);
						goldIn += t.yield.get(YieldResource.gold);
						happyIn += t.yield.get(YieldResource.happiness);
					}
					if(t.workers>0) {
						workers += t.workers;
					}
					if(t.improvement!=null)
						goldOut += t.improvement.maintenance;
				}
			}
		if(isBuildingSettlement())
			workers++;
		unemployed = population - workers;
		
		foodOut = population;
		
		happyOut = (population-1) + unemployed*unemployed + world.cities.size()/4 + world.poverty;
		if(foodIn<foodOut)
			happyOut += population;
		happiness = Happiness.get(happyIn-happyOut, population);
	}

}
