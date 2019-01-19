package com.xrbpowered.hexpansio.world.tile.improv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Comparator;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.effect.CityEffectStack;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class ImprovementStack {

	public final Improvement base;
	public final ArrayList<Improvement> upgrades;

	public int upgPoints;
	public int workplaces;
	public int maintenance;
	public int bonusResources;
	public final Yield.Cache yield = new Yield.Cache();
	public final Yield.Cache yieldPerWorker = new Yield.Cache();
	
	public ImprovementStack(Improvement base) {
		this.base = base;
		this.upgrades = new ArrayList<>();
		update();
	}

	public ImprovementStack(Improvement base, ArrayList<Improvement> upgrades) {
		this.base = base;
		this.upgrades = upgrades;
		update();
	}

	public void add(Improvement upg) {
		upgrades.add(upg);
		update();
	}
	
	public String getGlyph() {
		String g = base.glyph==null ? "?" : base.glyph;
		if(!upgrades.isEmpty())
			g += "+";
		return g;
	}
	
	public void collectEffects(CityEffectStack effects) {
		base.collectEffects(effects);
		for(Improvement upg : upgrades) {
			upg.collectEffects(effects);
		}
	}
	
	public void update() {
		this.upgrades.sort(viewComparator);
		
		upgPoints = 0;
		workplaces = base.workplaces;
		maintenance = base.maintenance;
		bonusResources = base.bonusResources;
		yield.clear();
		yieldPerWorker.clear();
		yield.add(base.yield);
		yieldPerWorker.add(base.yieldPerWorker);
		
		for(Improvement upg : upgrades) {
			upgPoints += upg.upgPoints;
			workplaces += upg.workplaces;
			maintenance += upg.maintenance;
			bonusResources += upg.bonusResources;
			yield.add(upg.yield);
			yieldPerWorker.add(upg.yieldPerWorker);
		}
	}
	
	public boolean isPermanent() {
		return base==Improvement.cityCenter;
	}

	public static boolean tileContains(Tile tile, Improvement imp) {
		if(tile.improvement!=null) {
			if(tile.improvement.base==imp)
				return true;
			for(Improvement upg : tile.improvement.upgrades) {
				if(upg==imp)
					return true;
			}
		}
		return false;
	}

	public static boolean cityContains(Tile tile, Improvement imp) {
		World world = tile.region.world;
		for(int x=-City.expandRange; x<=City.expandRange; x++)
			for(int y=-City.expandRange; y<=City.expandRange; y++) {
				Tile t = world.getTile(tile.wx+x, tile.wy+y);
				if(t!=null && t.city==tile.city && tileContains(t, imp))
					return true;
			}
		return false;
	}

	public static boolean isPrerequisite(Tile tile, Improvement imp) {
		if(imp.prerequisite==null)
			return tile.improvement==null;
		else
			return tileContains(tile, imp.prerequisite);
	}
	
	public static int getYield(Tile tile, YieldResource res) {
		return (tile.improvement==null) ? 0 : tile.improvement.yield.get(res) + tile.workers * tile.improvement.yieldPerWorker.get(res);
	}
	
	public static int getAvailUpgPoints(Tile tile) {
		return tile.city.maxUpgPointsForTile(tile)-(tile.improvement==null ? 0 : tile.improvement.upgPoints);
	}
	
	public static final Comparator<Improvement> viewComparator = new Comparator<Improvement>() {
		@Override
		public int compare(Improvement imp1, Improvement imp2) {
			int res = 0;
			res = -Boolean.compare(imp1.prerequisite==null, imp2.prerequisite==null);
			if(res==0) {
				res = Integer.compare(imp1.upgPoints, imp2.upgPoints);
				if(res==0) {
					res = imp1.compareTo(imp2);
				}
			}
			return res;
		}
	};

	public static ArrayList<Improvement> createViewList(Tile tile) {
		ArrayList<Improvement> impList = new ArrayList<>();
		if(tile!=null && tile.improvement!=null) {
			impList.add(tile.improvement.base);
			impList.addAll(tile.improvement.upgrades);
		}
		return impList;
	}
	
	public static void write(Tile tile, ObjectIndex<Improvement> conv, DataOutputStream out) throws IOException {
		if(tile.improvement==null) {
			out.writeShort(-1);
		}
		else {
			out.writeShort(conv.getIndex(tile.improvement.base.name));
			out.writeByte(tile.improvement.upgrades.size());
			for(Improvement upg : tile.improvement.upgrades)
				out.writeShort(conv.getIndex(upg.name));
		}
	}

	public static void read(Tile tile, ObjectIndex<Improvement> conv, DataInputStream in) throws IOException {
		Improvement imp = conv.get(in.readShort());
		if(imp==null) {
			tile.improvement = null;
		}
		else {
			int num = in.readByte();
			ArrayList<Improvement> upgrades = new ArrayList<>(num);
			for(int i=0; i<num; i++)
				upgrades.add(conv.get(in.readShort()));
			tile.improvement = new ImprovementStack(imp, upgrades);
		}
	}
}
