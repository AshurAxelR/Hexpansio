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

	public boolean isCityCenter() {
		return false;
	}
	
	public boolean canBuildOn(Tile tile) {
		for(Feature f : rejectFeatures) {
			if(tile.terrain.feature==f)
				return false;
		}
		return true;
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
	}
	
	public static final Improvement city = new CityCenter("City");
	public static final Improvement farm = new Improvement("Farm", 30).setGlyph("F").yield(2, 0, 0, 0).reject(Feature.values());
	public static final Improvement mine = new Improvement("Mine", 40).setGlyph("M").maintenance(1).yield(0, 3, 0, 0);
	public static final Improvement lumberMill = new Improvement("Lumber mill", 60) {
		@Override
		public boolean canBuildOn(Tile tile) {
			return tile.terrain.feature==Feature.forest;
		}
	}.setGlyph("L").yield(0, 2, 0, 0);
	public static final Improvement gatherer = new Improvement("Gatherer", 20) {
		@Override
		public boolean canBuildOn(Tile tile) {
			return tile.terrain.feature==Feature.forest || tile.terrain.feature==Feature.swamp;
		}
	}.setGlyph("G").yield(1, 1, 0, 0);
	public static final Improvement market = new Improvement("Market", 60).setGlyph("T").yield(0, 0, 2, 0).reject(Feature.water, Feature.mountain, Feature.peak);
	public static final Improvement park = new Improvement("Park", 30).setGlyph("P").yield(0, 0, 0, 2).maintenance(2).reject(Feature.water, Feature.desert);

	public static final Improvement[] buildMenu = {
			farm, mine	, market, park, lumberMill, gatherer
		};
	public static final Integer[] hotkeys = {
			KeyEvent.VK_F, KeyEvent.VK_M, KeyEvent.VK_T, KeyEvent.VK_P, KeyEvent.VK_L, KeyEvent.VK_G 
		};
	public static Improvement buildFromHotkey(int key) {
		for(int i=0; i<hotkeys.length; i++)
			if(hotkeys[i]==key)
				return buildMenu[i];
		return null;
	}
	
}
