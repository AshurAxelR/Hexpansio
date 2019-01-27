package com.xrbpowered.hexpansio.world.city;

import java.awt.Color;
import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.NameGen;
import com.xrbpowered.hexpansio.world.TurnEventMessage;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.build.BuildingProgress;
import com.xrbpowered.hexpansio.world.city.build.BuiltSettlement;
import com.xrbpowered.hexpansio.world.city.build.FinishedBuilding;
import com.xrbpowered.hexpansio.world.city.effect.CityEffectStack;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.TradeList;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.CityUpgrades;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;

public class City {

	public static final int expandRange = 3;
	public static final int growthCostFactor = 10;
	public static final int cityUpgPoints = 3;

	public String name;
	public int population = 1;
	public int growth = 0;
	public BuildingProgress buildingProgress = null;
	public CityEffectStack effects = new CityEffectStack();
	
	public final TradeList trades = new TradeList(this);

	public boolean availExpand = true;

	public final int id;
	public final World world;
	public Tile tile = null;
	public boolean coastalCity = false;

	public int numTiles = 0;
	public final Yield.Cache incomeTiles = new Yield.Cache();
	public final Yield.Cache incomeResources = new Yield.Cache();
	public final Yield.Cache expences = new Yield.Cache();
	public final Yield.Cache balance = new Yield.Cache();
	
	private int upgPoints;
	
	public final ResourcePile resourcesOnMap = new ResourcePile();
	public final ResourcePile resourcesProduced = new ResourcePile();
	public final ResourcePile resourcesAvail = new ResourcePile();
	
	public int unemployed, workplaces;
	public int adjVoid;
	public Happiness happiness = Happiness.content;

	public FinishedBuilding finishedBuilding = null;

	public City(int id, World world, Tile tile, String name) {
		this.id = id;
		this.world = world;
		
		if(name==null || !rename(name))
			rename(generateName(world, tile));
		
		world.registerNewCity(this);
		
		if(tile!=null) {
			setTile(tile);
			world.discoverArea(tile.wx, tile.wy, 1);
			for(Dir d : Dir.values())
				addTile(tile.wx+d.dx, tile.wy+d.dy);
		}
	}
	
	public ArrayList<City> getNeighbours(int range) {
		ArrayList<City> list = new ArrayList<>();
		for(City c : world.cities) {
			if(c!=this && tile.distTo(c.tile)<=range) {
				list.add(c);
			}
		}
		return list;
	}

	public static String generateName(World world, Tile tile) {
		Random random = tile==null ? new Random() : new Random(tile.getSeed(3856L));
		return generateName(world, random);
	}

	public static String generateName(World world, Random random) {
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
		if(tile.improvement==null)
			tile.improvement = new ImprovementStack(Improvement.cityCenter);
		coastalCity = tile.countAdjTerrain(Feature.water)>0;
	}
	
	public void destroyCity() {
		population = 0;
		setBuilding(null);
		trades.cancelAll();
		for(int x=-expandRange; x<=expandRange; x++)
			for(int y=-expandRange; y<=expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==this) {
					if(t.isCityCenter())
						t.terrain = TerrainType.ruins;
					t.improvement = null;
					t.city = null;
				}
			}
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
	}
	
	public boolean isBuildingSettlement() {
		return buildingProgress!=null && (buildingProgress instanceof BuiltSettlement);
	}
	
	public Tile getSettlement() {
		return isBuildingSettlement() ? buildingProgress.tile : null; 
	}
	
	public int getFoodGrowth() {
		int food = balance.get(YieldResource.food);
		return food>0 ? food*(100-happiness.growthPenalty)/100 : food;
	}

	public int getProduction() {
		return balance.get(YieldResource.production)*(100-happiness.prodPenalty)/100;
	}
	
	public int getVoidResist() {
		int h = balance.get(YieldResource.happiness);
		if(h<=0)
			return 0;
		else
			return 100 - 100 / (h*h+1);
	}

	public int getExcess(int prod) {
		return prod/2;
	}
	
	public int maxUpgPointsForTile(Tile tile) {
		return tile.isCityCenter() ? upgPoints + cityUpgPoints : upgPoints;
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
		population--;
		if(unemployed>0)
			unemployed--;
		else
			unassignWorker();
		if(growth<0)
			growth += getTargetGrowth();
		if(population<1)
			growth = 0;
	}
	
	public void nextTurn() {
		updateStats();
		finishedBuilding = null;
		
		growth +=getFoodGrowth();
		
		if(buildingProgress!=null) {
			if(buildingProgress.progress(getProduction()))
				world.events.add(finishedBuilding.createMessage(this));
		}
		else
			world.goods += getExcess(getProduction());
		
		if(happiness==Happiness.raging) {
			reducePopulation();
			world.events.add(new TurnEventMessage(this, "1 population rage quit").setColor(Color.RED));
		}
		if(growth<0) {
			reducePopulation();
			world.events.add(new TurnEventMessage(this, "1 population lost to starvation").setColor(Color.RED));
		}
		else if(population>0){
			int pop = 0;
			while(growth>=getTargetGrowth()) {
				growth -= getTargetGrowth();
				pop++;
				population++;
			}
			if(pop>0)
				world.events.add(new TurnEventMessage(this, String.format("population grows %+d", pop)).setColor(YieldResource.food.fill));
		}
		
		world.gold += balance.get(YieldResource.gold);
		availExpand = true;
	}
	
	protected void collectEffects() {
		effects.effects.clear();
		for(int x=-expandRange; x<=expandRange; x++)
			for(int y=-expandRange; y<=expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==this && t.improvement!=null) {
					t.improvement.collectEffects(effects);
				}
			}
	}

	protected void updateIncomeTiles() {
		upgPoints = effects.modifyCityValue(EffectTarget.upgPoints, 0);
		
		resourcesOnMap.clear();
		resourcesProduced.clear();
		numTiles = 0;
		
		incomeTiles.clear();
		expences.clear();
		workplaces = 0;
		adjVoid = 0;
		int workers = 0;
		for(int x=-expandRange-1; x<=expandRange+1; x++)
			for(int y=-expandRange-1; y<=expandRange+1; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.isVoid()) {
					if(t.city==this || t.countAdjCityTiles(this)>0)
						adjVoid++;
				}
				if(t!=null && t.city==this) {
					numTiles++;
					workplaces += t.getWorkplaces();
					if(t.workers>0 || t.isCityCenter())
						incomeTiles.add(t.yield);
					if(t.workers>0)
						workers += t.workers;
					if(t.improvement!=null)
						expences.add(YieldResource.gold, t.improvement.maintenance);
					if(t.resource!=null)
						resourcesOnMap.add(t.resource, 1);
					if(t.hasResourceImprovement()) {
						resourcesProduced.add(t.resource, 1+t.improvement.bonusResources);
					}
				}
			}
		if(isBuildingSettlement())
			workers++;
		unemployed = population - workers;
		
		expences.add(YieldResource.food, population);
	}

	protected void updateIncomeResources() {
		incomeResources.clear();
		incomeResources.add(YieldResource.gold, trades.profit);
		for(ResourcePile.Entry e : resourcesAvail.getUnsorted()) {
			for(YieldResource res : YieldResource.values()) {
				incomeResources.add(res, e.count * (e.resource.yield.get(res) + effects.addResourceBonusYield(e.resource, res)));
			}
		}
	}

	public int getPopulationUnhappiness() {
		int u = population-1;
		if(ImprovementStack.cityContains(this, CityUpgrades.highrise))
			u = (int)(u * (1f - CityUpgrades.highriseEffect));
		return u;
	}
	
	public int getVoidUnhappiness() {
		int u = adjVoid;
		if(ImprovementStack.cityContains(this, CityUpgrades.beaconOfHope))
			u = (int)(u * (1f - CityUpgrades.beaconOfHopeEffect));
		return u;
	}
	
	protected void updateBalance() {
		expences.add(YieldResource.happiness,
				getPopulationUnhappiness() + unemployed*unemployed + (world.cities.size()-1) + world.poverty + getVoidUnhappiness());
		
		balance.clear();
		balance.add(YieldResource.happiness, world.baseHappiness);
		balance.add(incomeTiles);
		balance.add(incomeResources);
		balance.subtract(expences);
		
		if(balance.get(YieldResource.food)<0) {
			expences.add(YieldResource.happiness, population);
			balance.add(YieldResource.happiness, -population);
		}
		
		happiness = Happiness.get(balance.get(YieldResource.happiness), population);
	}

	protected boolean updateTrade() {
		resourcesAvail.clear();
		resourcesAvail.add(resourcesProduced);
		resourcesAvail.remove(trades.totalOut);
		for(ResourcePile.Entry e : resourcesAvail.getUnsorted()) {
			if(e.count<0) {
				trades.cancelResource(e.resource);
				return false;
			}
		}
		resourcesAvail.add(trades.totalIn);
		return true;
	}
	
	public void updateStats() {
		collectEffects();
		updateIncomeTiles();
		if(updateTrade()) {
			updateIncomeResources();
			updateBalance();
		}
	}
	
	public int countPinnedMessages() {
		int n = 0;
		if(balance.get(YieldResource.happiness)<0)
			n++;
		if(getFoodGrowth()<0)
			n++;
		if(unemployed>0)
			n++;
		return n;
	}
	
	public void addPinnedMessages(ArrayList<TurnEventMessage> msgList) {
		if(balance.get(YieldResource.happiness)<0)
			msgList.add(new TurnEventMessage(this, "population is "+happiness.name).setColor(happiness.color).pin());
		if(getFoodGrowth()<0)
			msgList.add(new TurnEventMessage(this, "is starving").setColor(Color.RED).pin());
		if(unemployed>0)
			msgList.add(new TurnEventMessage(this, String.format("has %d unemployed %s", unemployed, unemployed==1 ? "worker" : "workers")).setColor(new Color(0xdd0000)).pin());
	}

}
