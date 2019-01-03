package com.xrbpowered.hexpansio.world.tile.improv;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class Improvement implements Comparable<Improvement> {

	public static final ObjectIndex<Improvement> objectIndex = new ObjectIndex<>();
	public static final HashMap<Integer, Improvement> keyMap = new HashMap<>();
	
	public final String name;
	public final int buildCost;
	public final Yield.Set yield = new Yield.Set();
	
	public String glyph = null;
	public int hotkey = 0;
	
	public int maintenance = 0;
	private Feature[] rejectFeatures = {Feature.water, Feature.peak};
	private Feature[] reqFeatures = null;
	private boolean reqResource = false;
	
	public Improvement(String name, int buildCost) {
		objectIndex.put(name, this);
		this.name = name;
		this.buildCost = buildCost;
	}
	
	@Override
	public int compareTo(Improvement o) {
		return name.compareTo(o.name);
	}
	
	public Improvement yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}
	
	public Improvement setGlyph(String glyph) {
		this.glyph = glyph;
		return this;
	}

	public Improvement hotkey(int key) {
		this.hotkey = key;
		keyMap.put(key, this);
		return this;
	}

	public Improvement maintenance(int m) {
		this.maintenance = m;
		return this;
	}

	public Improvement reject(Feature... features) {
		this.rejectFeatures = features;
		return this;
	}

	public Improvement require(Feature... features) {
		this.reqFeatures = features;
		return this;
	}
	
	public Improvement requireResource() {
		this.reqResource = true;
		return this;
	}

	public boolean canBuildOn(Tile tile) {
		return (reqFeatures==null || tile.terrain.hasFeature(reqFeatures)) &&
				!tile.terrain.hasFeature(rejectFeatures) &&
				(!reqResource || tile.resource!=null && tile.resource.improvement==this);
	}
	
	public String requirementExplained(Tile tile) {
		if(reqFeatures!=null && !tile.terrain.hasFeature(reqFeatures)) {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<reqFeatures.length; i++) {
				if(i>0 && i==rejectFeatures.length-1)
					sb.append(" or ");
				else if(i>0)
					sb.append(", ");
				sb.append(reqFeatures[i].name());
			}
			return String.format("Requires %s terrain", sb.toString());
		}
		else if(tile.terrain.hasFeature(rejectFeatures)) {
			return String.format("Cannot be built on %s terrain", tile.terrain.feature.name());
		}
		else if(reqResource && (tile.resource==null || tile.resource.improvement!=this)) {
			return "Requires appropriate resource";
		}
		else
			return null;
	}
	
	public boolean isRecommendedFor(Tile tile) {
		return tile.resource!=null && tile.resource.improvement==this;
	}
	
	public String recommendationExplained(Tile tile) {
		if(tile.resource!=null && tile.resource.improvement==this)
			return "Will produce "+tile.resource.name;
		else
			return null;
	}
	
	public static ArrayList<Improvement> createBuildList(Tile tile) {
		ArrayList<Improvement> impList = new ArrayList<>();
		for(int i=0; i<Improvement.objectIndex.size(); i++) {
			Improvement imp = Improvement.objectIndex.get(i);
			if(imp!=Improvement.cityCenter) {
				if(tile.improvement==null) {
					// TODO check pre-req
					impList.add(imp);
				}
			}
		}
		return impList;
	}
	
	public static final Improvement cityCenter = new Improvement("City", 0).yield(1, 1, 1, 0);
	
	public static final Improvement farm = new Improvement("Farm", 30).hotkey(KeyEvent.VK_F).setGlyph("F")
			.yield(2, 0, 0, 0).reject(Feature.values());
	public static final Improvement mine = new Improvement("Mine", 40).hotkey(KeyEvent.VK_M).setGlyph("M")
			.maintenance(1).yield(0, 3, 0, 0).requireResource();
	public static final Improvement lumberMill = new Improvement("Lumber mill", 60).hotkey(KeyEvent.VK_L).setGlyph("L")
			.yield(0, 2, 0, 0).require(Feature.forest);
	public static final Improvement gatherer = new Improvement("Gatherer", 20).hotkey(KeyEvent.VK_G).setGlyph("G")
			.yield(1, 1, 0, 0).require(Feature.forest, Feature.swamp);
	public static final Improvement market = new Improvement("Market", 60).hotkey(KeyEvent.VK_T).setGlyph("T")
			.yield(0, 0, 2, 0).reject(Feature.water, Feature.mountain, Feature.peak);
	public static final Improvement park = new Improvement("Park", 30).hotkey(KeyEvent.VK_P).setGlyph("P")
			.yield(0, 0, 0, 2).maintenance(2).reject(Feature.water, Feature.desert);
	public static final Improvement pasture = new Improvement("Pasture", 30).hotkey(KeyEvent.VK_U).setGlyph("U")
			.yield(1, 0, 0, 0)	.reject(Feature.water, Feature.desert, Feature.forest, Feature.swamp, Feature.peak);
	public static final Improvement plantation = new Improvement("Plantation", 40).hotkey(KeyEvent.VK_N).setGlyph("N")
			.yield(1, 0, 1, 0)	.reject(Feature.water, Feature.peak).requireResource();
	public static final Improvement quarry = new Improvement("Quarry", 30).hotkey(KeyEvent.VK_Y).setGlyph("Y")
			.yield(0, 1, 1, 0).requireResource();
	public static final Improvement boat = new Improvement("Fishing Boat", 20).hotkey(KeyEvent.VK_B).setGlyph("B")
			.reject((Feature[])null).require(Feature.water).yield(1, 0, 0, 0).requireResource();
	public static final Improvement drill = new Improvement("Drill", 80).hotkey(KeyEvent.VK_I).setGlyph("I")
			.reject((Feature[])null).yield(0, 2, 1, 0).requireResource();

}
