package com.xrbpowered.hexpansio.world.tile;

import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;

public class Improvement {

	public static final ObjectIndex<Improvement> objectIndex = new ObjectIndex<>();
	
	public final String name;
	public final int buildCost;
	public final Yield.Set yield = new Yield.Set();
	
	public String glyph = null;
	
	public int maintenance = 0;
	private Feature[] rejectFeatures = {Feature.water, Feature.peak};
	private Feature[] reqFeatures = null;
	private boolean reqResource = false;
	
	public Improvement(String name, int buildCost) {
		objectIndex.put(name, this);
		this.name = name;
		this.buildCost = buildCost;
	}
	
	public Improvement yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}
	
	public Improvement setGlyph(String glyph) {
		this.glyph = glyph;
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

	public boolean isCityCenter() {
		return false;
	}
	
	public boolean isPermanenet() {
		return false;
	}
	
	public boolean canBuildOn(Tile tile) {
		return (reqFeatures==null || tile.terrain.hasFeature(reqFeatures)) &&
				!tile.terrain.hasFeature(rejectFeatures) &&
				(!reqResource || tile.resource!=null && tile.resource.improvement==this);
	}
	
	public static class CityCenter extends Improvement {
		public CityCenter(String name) {
			super(name, 0);
			yield(1, 1, 1, 0);
		}
		@Override
		public boolean isCityCenter() {
			return true;
		}
		@Override
		public boolean isPermanenet() {
			return true;
		}
	}
	
	public static final Improvement city = new CityCenter("City");
	public static final Improvement farm = new Improvement("Farm", 30).setGlyph("F").yield(2, 0, 0, 0).reject(Feature.values());
	public static final Improvement mine = new Improvement("Mine", 40).setGlyph("M").maintenance(1).yield(0, 3, 0, 0).requireResource();
	public static final Improvement lumberMill = new Improvement("Lumber mill", 60).setGlyph("L").yield(0, 2, 0, 0).require(Feature.forest);
	public static final Improvement gatherer = new Improvement("Gatherer", 20).setGlyph("G").yield(1, 1, 0, 0).require(Feature.forest, Feature.swamp);
	public static final Improvement market = new Improvement("Market", 60).setGlyph("T").yield(0, 0, 2, 0).reject(Feature.water, Feature.mountain, Feature.peak);
	public static final Improvement park = new Improvement("Park", 30).setGlyph("P").yield(0, 0, 0, 2).maintenance(2).reject(Feature.water, Feature.desert);
	public static final Improvement pasture = new Improvement("Pasture", 30).setGlyph("U").yield(1, 0, 0, 0)
			.reject(Feature.water, Feature.desert, Feature.forest, Feature.swamp, Feature.peak);
	public static final Improvement plantation = new Improvement("Plantation", 40).setGlyph("N").yield(1, 0, 1, 0)
			.reject(Feature.water, Feature.peak).requireResource();

	public static final Improvement[] buildMenu = {
			farm, mine	, market, park, lumberMill, gatherer, pasture, plantation
		};
	public static final Integer[] hotkeys = {
			KeyEvent.VK_F, KeyEvent.VK_M, KeyEvent.VK_T, KeyEvent.VK_P, KeyEvent.VK_L, KeyEvent.VK_G, KeyEvent.VK_U, KeyEvent.VK_N 
		};
	public static Improvement buildFromHotkey(int key) {
		for(int i=0; i<hotkeys.length; i++)
			if(hotkeys[i]==key)
				return buildMenu[i];
		return null;
	}
	
}
