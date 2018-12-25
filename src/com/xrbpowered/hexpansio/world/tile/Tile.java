package com.xrbpowered.hexpansio.world.tile;

import com.xrbpowered.hexpansio.ui.modes.ScoutMode;
import com.xrbpowered.hexpansio.world.BuildingProgress;
import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.Region;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.utils.RandomUtils;

public class Tile {

	public TerrainType terrain;
	public boolean discovered = false;

	public City city = null;
	public BuildingProgress.BuiltSettlement settlement = null;
	public Improvement improvement = null;
	public int workers = 0;

	public final Region region;
	public final int x, y, wx, wy;

	public boolean areaDiscovered = false;
	
	public final Yield yield = new Yield() {
		@Override
		public int get(YieldResource res) {
			return terrain.yield.get(res) + (improvement==null ? 0 : improvement.yield.get(res));
		}
	};
	
	private Tile(int wx, int wy) {
		this.region = null;
		this.x = -1;
		this.y = -1;
		this.wx = wx;
		this.wy = wy;
	}
	
	public Tile(Region region, int x, int y) {
		this.region = region;
		this.x = x;
		this.y = y;
		this.wx = (region.rx<<Region.sized)+x;
		this.wy = (region.ry<<Region.sized)+y;
	}
	
	public long getSeed(long offs) {
		return RandomUtils.seedXY(region.world.seed+offs, wx, wy);
	}
	
	public boolean isCityCenter() {
		return improvement!=null && improvement.isCityCenter();
	}
	
	public boolean isAreaDiscovered() {
		if(!areaDiscovered)
			areaDiscovered = region.world.isAreaDiscovered(wx, wy, ScoutMode.areaRange);
		return areaDiscovered;
	}
	
	public Tile getAdj(Dir d) {
		return region.world.getTile(wx+d.dx, wy+d.dy);
	}
	
	public int countAdjCityTiles(City city) {
		int adj = 0;
		for(Dir d : Dir.values()) {
			Tile t = getAdj(d);
			if(t!=null && t.city==city)
				adj++;
		}
		return adj;
	}
	
	public int distTo(int wx, int wy) {
		return World.dist(this.wx, this.wy, wx, wy);
	}

	public int distTo(Tile tile) {
		return World.dist(this.wx, this.wy, tile.wx, tile.wy);
	}

	public boolean isCityBorder(Dir d) {
		Tile t = getAdj(d);
		return t!=null && t.city!=this.city;
	}
	
	public static class DummyTile extends Tile {
		public DummyTile(int wx, int wy) {
			super(wx, wy);
		}
		public Tile realise(World world) {
			return world.getTile(wx, wy);
		}
	}

}
