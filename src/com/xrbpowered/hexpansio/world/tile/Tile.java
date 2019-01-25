package com.xrbpowered.hexpansio.world.tile;

import java.util.Random;

import com.xrbpowered.hexpansio.ui.modes.ScoutMode;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.Region;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.build.BuiltSettlement;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;
import com.xrbpowered.utils.RandomUtils;

public class Tile {

	public TerrainType terrain;
	public TokenResource resource = null;
	public boolean discovered = false;

	public City city = null;
	public BuiltSettlement settlement = null;
	public ImprovementStack improvement = null;
	public int workers = 0;

	public final Region region;
	public final int x, y, wx, wy;

	public boolean areaDiscovered = false;
	
	public int voidTurn = -1;
	
	public final Yield yield = new Yield() {
		@Override
		public int get(YieldResource res) {
			return terrainYield.get(res) + ImprovementStack.getYield(Tile.this, res);
		}
	};
	
	public final Yield terrainYield = new Yield() {
		@Override
		public int get(YieldResource res) {
			int y = terrain.yield.get(res);
			if(resource!=null)
				y += resource.terrainBonus.get(res);
			if(city!=null)
				y += city.effects.addTileYield(Tile.this, res);
			return y;
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
		return improvement!=null && improvement.base==Improvement.cityCenter;
	}
	
	public boolean hasResourceImprovement() {
		return improvement!=null && resource!=null && resource.improvement==improvement.base;
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
	
	public int countAdjTerrain(Feature feature) {
		int adj = 0;
		for(Dir d : Dir.values()) {
			Tile t = getAdj(d);
			if(t!=null && t.terrain.feature==feature)
				adj++;
		}
		return adj;
	}

	public int getWorkplaces() {
		if(isCityCenter())
			return 0;
		int w = terrain.workplaces;
		if(improvement!=null)
			w += improvement.workplaces;
		if(city!=null)
			w += city.effects.tileEffect(EffectTarget.workplaces, this);
		return w;
	}
	
	public boolean assignWorker() {
		if(city!=null && !isCityCenter() && workers<getWorkplaces() && city.unemployed>0) {
			workers++;
			city.unemployed--;
			return true;
		}
		else
			return false;
	}

	public boolean unassignWorkers() {
		if(city!=null && !isCityCenter() && workers>0) {
			city.unemployed += workers;
			workers = 0;
			return true;
		}
		else
			return false;
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
	
	public boolean isRangeBorder(Dir d, Tile dst, int range) {
		Tile t = getAdj(d);
		return t!=null && (distTo(dst)<=range) != (t.distTo(dst)<=range);
	}
	
	public boolean isVoid() {
		return terrain.feature==Feature.thevoid;
	}
	
	public void makeVoid() {
		if(isCityCenter())
			return; // TODO consume city?
		
		if(settlement!=null)
			settlement.city.setBuilding(null);
		if(city!=null && city.buildingProgress!=null && city.buildingProgress.tile==this)
			city.setBuilding(null);
		unassignWorkers();
		improvement = null;
		terrain = TerrainType.voidEdge;
		resource = null;
		
		voidTurn = region.world.turn;
		region.hasVoid = true;
		region.world.discoverArea(wx, wy, 1);
	}
	
	public void checkVoidDepth() {
		if(terrain==TerrainType.voidEdge) {
			if(countAdjTerrain(Feature.thevoid)==6)
				terrain = TerrainType.deepVoid;
		}
	}
	
	public void spreadVoid() {
		int turn = region.world.turn;
		if(terrain==TerrainType.voidEdge && voidTurn<turn) {
			Random random = new Random(this.getSeed(turn + 4983L));
			int[] dw = new int[6];
			int countVoid = 0;
			for(int d=0; d<6; d++) {
				boolean isVoid = getAdj(Dir.values()[d]).isVoid();
				if(isVoid)
					countVoid++;
				dw[d] = isVoid ? 0 : 1;
			}
			if(countVoid<6) {
				Dir dir = Dir.values()[RandomUtils.weighted(random, dw)];
				Tile t = getAdj(dir);
				if(t.city==null || random.nextInt(100)>t.city.getVoidResist())
					t.makeVoid();
				if(random.nextInt(2)==0)
					terrain = TerrainType.deepVoid;
				else
					voidTurn = turn;
				checkVoidDepth();
			}
			else {
				terrain = TerrainType.deepVoid;
			}
		}
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
