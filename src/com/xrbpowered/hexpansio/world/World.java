package com.xrbpowered.hexpansio.world;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainGenerator;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class World {

	public static final int initialBaseHappiness = 5;
	
	public static final int voidStartTurn = 20;
	public static final int voidStartSources = 3;
	public static final int voidMinDistance = 8; 
	public static final int voidMaxDistance = 16; 

	public static final int originwx = Region.size/2;
	public static final int originwy = Region.size/2;
	public static final TerrainType originTerrain = TerrainType.fertileValley;
	
	public final long seed;
	public int cityNameBaseLength = 1;

	public int turn = 0;
	public int gold = 0;
	public int goods = 0;
	public int poverty = 0;
	
	public int maxDiscover;
	public int discoverThisTurn = 0;

	private HashMap<Integer, Region> regions = new HashMap<>();
	public ArrayList<City> cities = new ArrayList<>();

	public final Save save;
	public final TerrainGenerator terrainGenerator;
	public Tile origin;

	public HashMap<String, City> citiesByName = new HashMap<>();
	
	public int totalPopulation = 1;
	public int totalGoldIn = 0;
	public int totalGoodsIn = 0;
	public int baseHappiness = 0;
	
	public Happiness minHappiness = Happiness.happy;
	
	public ArrayList<Tile> newCities = new ArrayList<>();

	public World(Save save, long seed, TerrainGenerator terrain) {
		this.save = save;
		this.seed = seed;
		this.terrainGenerator = terrain;
	}
	
	public World create() {
		origin = discoverTile(originwx, originwy);
		discoverArea(origin.wx, origin.wy, 5);
		
		new City(this, origin, null);
		updateCities();
		updateWorldTotals();
		
		return this;
	}
	
	public void debugDiscover(int range) {
		for(int rx=-range-1; rx<=range+1; rx++)
			for(int ry=-range-1; ry<=range+1; ry++)
				discoverRegion(rx, ry);
		for(int rx=-range; rx<=range; rx++)
			for(int ry=-range; ry<=range; ry++) {
				Region r = getRegion(rx, ry);
				for(int x=0; x<Region.size; x++)
					for(int y=0; y<Region.size; y++) {
						Tile t = r.tiles[x][y];
						if(!t.discovered) {
							t.discovered = true;
							terrainGenerator.finaliseTile(t);
						}
					}
			}
	}
	
	protected Region getRegionById(int id) {
		return regions.get(id);
	}
	
	protected ArrayList<Integer> regionIds() {
		ArrayList<Integer> list = new ArrayList<>(regions.keySet());
		list.sort(null);
		return list;
	}
	
	public Region getRegion(int rx, int ry) {
		return regions.get(Region.getId(rx, ry));
	}
	
	public Region discoverRegion(int rx, int ry) {
		Region r = getRegion(rx, ry);
		if(r==null) {
			r = new Region(this, rx, ry);
			regions.put(r.getId(), r);
		}
		return r;
	}

	public Tile getTile(int wx, int wy) {
		Region r = regions.get(Region.getId(wx>>Region.sized, wy>>Region.sized));
		if(r==null)
			return null;
		else {
			int x = wx & (Region.size-1);
			int y = wy & (Region.size-1);
			return r.tiles[x][y];
		}
	}
	
	public Tile discoverTile(int wx, int wy) {
		Region r = discoverRegion(wx>>Region.sized, wy>>Region.sized);
		int x = wx & (Region.size-1);
		int y = wy & (Region.size-1);
		Tile t = r.tiles[x][y];
		if(!t.discovered) {
			terrainGenerator.finaliseTile(t);
			t.discovered = true;
		}
		return t;
	}

	public boolean isTileDiscovered(int wx, int wy) {
		Tile tile = getTile(wx, wy);
		return tile!=null && tile.discovered; 
	}

	public void discoverArea(int wx, int wy, int radius) {
		for(int r=1; r<=radius; r++)
			for(int s=0; s<=r; s++)
				for(Dir d : Dir.values()) {
					Dir ds = d.cw(2);
					int x = wx + r*d.dx + s*ds.dx;
					int y = wy + r*d.dy + s*ds.dy;
					discoverTile(x, y);
				}
	}
	
	public boolean isAreaDiscovered(int wx, int wy, int radius) {
		for(int r=1; r<=radius; r++)
			for(int s=0; s<=r; s++)
				for(Dir d : Dir.values()) {
					Dir ds = d.cw(2);
					int x = wx + r*d.dx + s*ds.dx;
					int y = wy + r*d.dy + s*ds.dy;
					if(!isTileDiscovered(x, y))
						return false;
				}
		return true;
	}
	
	public int costDiscover(Tile t) {
		float dist = (float)origin.distTo(t)/3f;
		return (int)(dist*dist);
	}
	
	public boolean canAddToCity(Tile t, City city) {
		if(t.city!=city && t.distTo(city.tile)<=City.expandRange) {
			return t.countAdjCityTiles(city)>0;
		}
		else
			return false;
	}
	
	public int costAddToCity(Tile t, City city) {
		int cost = 10*(t.distTo(city.tile)-1) + (city.numTiles-7)*5;
		return cost*(6-t.countAdjCityTiles(city))/5;
	}
	
	public void addToCity(int wx, int wy, City city) {
		Tile t = discoverTile(wx, wy);
		if(canAddToCity(t, city)) {
			discoverArea(wx, wy, 1);
			if(t.city!=null) {
				City other = t.city;
				t.workers = 0;
				t.city = null;
				other.updateStats();
			}
			t.city = city;
		}
	}
	
	private int distToNearestCityOrSettler(int wx, int wy, boolean cityOnly) {
		int rx = wx>>Region.sized;
		int ry = wy>>Region.sized;
		int dist = Region.size*2;
		for(int x=-1; x<=1; x++)
			for(int y=-1; y<=1; y++) {
				Region r = getRegion(rx+x, ry+y);
				if(r!=null) {
					for(City c : r.cities) {
						int d = c.tile.distTo(wx, wy);
						if(d<dist) dist = d;
						if(!cityOnly && c.isBuildingSettlement()) {
							d = c.getSettlement().distTo(wx, wy);
							if(d<dist) dist = d;
						}
					}
				}
			}
		return dist;
	}

	public int distToNearestCity(int wx, int wy) {
		return distToNearestCityOrSettler(wx, wy, true);
	}

	public int distToNearestCityOrSettler(int wx, int wy) {
		return distToNearestCityOrSettler(wx, wy, false);
	}

	public void nextTurn() {
		for(City city : cities) {
			city.nextTurn();
		}
		for(Tile tile: newCities) {
			new City(this, tile, null);
		}
		newCities.clear();
		
		if(turn==voidStartTurn)
			startVoid();
		if(hasVoid()) {
			for(Region r : regions.values())
				r.spreadVoid();
		}
		
		if(gold<0) {
			gold = 0;
			poverty++;
		}
		else {
			poverty = 0;
		}
		updateCities();
		updateWorldTotals();
		
		discoverThisTurn = 0;
		turn++;
	}
	
	public void updateCities() {
		for(City city : cities) {
			city.updateStats();
		}
	}
	
	public void updateWorldTotals() {
		totalPopulation = 0;
		totalGoldIn = 0;
		totalGoodsIn = 0;
		maxDiscover = 0;
		int prevBH = baseHappiness;
		baseHappiness = initialBaseHappiness;
		minHappiness = Happiness.happy;
		for(City city : cities) {
			totalPopulation += city.population;
			totalGoldIn += city.balance.get(YieldResource.gold);
			if(city.buildingProgress==null)
				totalGoodsIn += city.getExcess(city.getProduction());
			maxDiscover += city.effects.modifyCityValue(EffectTarget.scouts, 0);
			baseHappiness += city.effects.modifyCityValue(EffectTarget.baseHappiness, 0);
			if(city.happiness.ordinal()>minHappiness.ordinal())
				minHappiness = city.happiness;
		}
		if(prevBH!=baseHappiness) {
			updateCities();
			updateWorldTotals();
		}
	}
	
	public boolean hasVoid() {
		return turn>voidStartTurn;
	}
	
	public void startVoid() {
		Random random = new Random(seed+4983L);
		int toAdd = voidStartSources;
		for(int r = (voidMaxDistance+voidMinDistance)/2;; r+=2) {
			Dir d = Dir.values()[random.nextInt(6)];
			Dir dc = d.cw(2);
			int i = random.nextInt(r);
			int wx = originwx + r*d.dx + i*dc.dx;
			int wy = originwy +r*d.dy + i*dc.dy;
			int dist = distToNearestCity(wx, wy);
			if(dist>=voidMinDistance && dist<=voidMaxDistance) {
				startVoidAt(wx, wy);
				toAdd--;
				if(toAdd==0)
					return;
			}
		}
	}
	
	public void startVoidAt(int wx, int wy) {
		Tile t = discoverTile(wx, wy);
		t.makeVoid();
		for(Dir d : Dir.values())
			t.getAdj(d).makeVoid();
		t.checkVoidDepth();
	}
	
	public static int dist(int wx1, int wy1, int wx2, int wy2) {
		int dx = wx2-wx1;
		int dy = wy2-wy1;
		if(dx<0 && dy>0 || dx>0 && dy<0)
			return Math.abs(dx)+Math.abs(dy);
		else
			return Math.max(Math.abs(dx), Math.abs(dy));
	}
	
}
