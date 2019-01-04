package com.xrbpowered.hexpansio.world.city;

import java.util.Random;

import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.NameGen;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.build.BuildingProgress;
import com.xrbpowered.hexpansio.world.city.build.BuiltSettlement;
import com.xrbpowered.hexpansio.world.city.effect.CityEffectStack;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;

public class City {

	public static final int expandRange = 3;
	public static final int growthCostFactor = 10;
	public static final int baseHappiness = 5;
	
	public String name;
	public int population = 1;
	public int growth = 0;
	public BuildingProgress buildingProgress = null;
	public CityEffectStack effects = new CityEffectStack();

	public int availDiscover = 1;
	public int availExpand = 1;

	public final int index;
	public final World world;
	public Tile tile = null;

	public int numTiles = 0;
	public int foodIn, prodIn, goldIn, happyIn;
	public int foodOut, goldOut, happyOut;
	
	public int upgPoints;
	
	public ResourcePile resources = new ResourcePile();
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
		} while(name.length()<4 || world.citiesByName.containsKey(name));
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
	
	public void setTile(Tile tile) {
		this.tile = tile;
		world.discoverTile(tile.wx, tile.wy);
		tile.resource = null;
		tile.region.cities.add(this);
		tile.city = this;
		tile.improvement = new ImprovementStack(Improvement.cityCenter);
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
		return buildingProgress!=null && (buildingProgress instanceof BuiltSettlement);
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
	
	public boolean unassignWorker() {
		if(unemployed>0)
			return true;
		Tile minTile = null;
		int minYield = 0;
		for(int x=-expandRange; x<=expandRange; x++)
			for(int y=-expandRange; y<=expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==this && t.workers>0 && !t.isCityCenter()) {
					int yield = 0;
					yield += t.yield.total();
					if(t.hasResourceImprovement())
						yield += t.resource.yield.total();
					if(minTile==null || yield<minYield) {
						minTile = t;
						minYield = yield;
					}
				}
			}
		if(minTile!=null)
			return minTile.unassignWorkers();
		else
			return false;
	}
	
	private void reducePopulation() {
		if(isBuildingSettlement())
			buildingProgress.cancel();
		if(population>1) {
			population--;
			if(unemployed>0)
				unemployed--;
			else
				unassignWorker();
			if(growth<0)
				growth += getTargetGrowth();
		}
		else {
			growth = 0;
		}
	}
	
	public void nextTurn() {
		updateStats();
		
		growth +=getFoodGrowth();
		
		if(buildingProgress!=null) {
			if(buildingProgress.nextTurn(getProduction())) {
				world.goods += getExcess(buildingProgress.progress);
				buildingProgress = null;
			}
		}
		else {
			world.goods += getExcess(getProduction());
		}

		if(happiness==Happiness.raging)
			reducePopulation();
		if(growth<0) {
			reducePopulation();
		}
		else {
			while(growth>=getTargetGrowth()) {
				growth -= getTargetGrowth();
				population++;
			}
		}
		
		world.gold += goldIn-goldOut;
		
		availDiscover = 1;
		availExpand = 1;
		
		world.totalPopulation += population;
	}
	
	private void appendYield(Yield yield) {
		foodIn += yield.get(YieldResource.food);
		prodIn += yield.get(YieldResource.production);
		goldIn += yield.get(YieldResource.gold);
		happyIn += yield.get(YieldResource.happiness);
	}
	
	public void collectEffects() {
		effects.effects.clear();
		for(int x=-expandRange; x<=expandRange; x++)
			for(int y=-expandRange; y<=expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==this && t.improvement!=null) {
					t.improvement.collectEffects(effects);
				}
			}
	}

	public void updateStats() {
		collectEffects();
		upgPoints = effects.modifyCityValue(EffectTarget.upgPoints, 0);
		
		resources.clear();
		numTiles = 0;
		foodIn = 0;
		prodIn = 0;
		goldIn = 0;
		happyIn = baseHappiness;
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
					if(t.workers>0 || t.isCityCenter())
						appendYield(t.yield);
					if(t.workers>0)
						workers += t.workers;
					if(t.improvement!=null)
						goldOut += t.improvement.maintenance;
					if(t.hasResourceImprovement()) {
						resources.add(t.resource, 1);
						appendYield(t.resource.yield);
					}
				}
			}
		if(isBuildingSettlement())
			workers++;
		unemployed = population - workers;
		
		foodOut = population;
		
		happyOut = (population-1) + unemployed*unemployed + (world.cities.size()-1) + world.poverty;
		if(foodIn<foodOut)
			happyOut += population;
		happiness = Happiness.get(happyIn-happyOut, population);
	}

}
