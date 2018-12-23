package com.xrbpowered.hexpansio.world;

import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.hexpansio.world.tile.TerrainGenerator;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class World {

	public static final int originwx = Region.size/2;
	public static final int originwy = Region.size/2;
	public static final TerrainType originTerrain = TerrainType.fertileValley;
	
	public final long seed;
	public int cityNameBaseLength = 1;

	public int turn = 0;
	public int gold = 0;
	public int goods = 0;
	public int poverty = 0;

	// game state
	private HashMap<Integer, Region> regions = new HashMap<>();
	public ArrayList<City> cities = new ArrayList<>();

	// data
	public final Save save;
	public final TerrainGenerator terrainGenerator;
	public Tile origin;

	public HashMap<String, City> citiesByName = new HashMap<>(); // new/load City
	
	public int totalPopulation = 1; // updateWorldTotals ...
	public int totalGoldIn = 0;
	public int totalGoodsIn = 0;
	
	public ArrayList<Tile> newCities = new ArrayList<>(); // nextTurn

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
						t.discovered = true;
						terrainGenerator.finaliseTile(t);
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
		terrainGenerator.finaliseTile(t);
		t.discovered = true;
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
		int dist = origin.distTo(t)/3;
		return dist*dist;
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
	
	public int distToNearestCityOrSettler(int wx, int wy) {
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
						if(c.isBuildingSettlement()) {
							d = c.getSettlement().distTo(wx, wy);
							if(d<dist) dist = d;
						}
					}
				}
			}
		return dist;
	}
	
	public void nextTurn() {
		for(City city : cities) {
			city.nextTurn();
		}
		for(Tile tile: newCities) {
			new City(this, tile, null);
		}
		newCities.clear();
		
		if(gold<0) {
			gold = 0;
			poverty++;
		}
		else {
			poverty = 0;
		}
		updateCities();
		updateWorldTotals();
		
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
		for(City city : cities) {
			totalPopulation += city.population;
			totalGoldIn += city.goldIn-city.goldOut;
			if(city.buildingProgress==null)
				totalGoodsIn += city.getExcess(city.getProduction());
		}
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
