package com.xrbpowered.hexpansio.world.tile;

import java.awt.Color;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.resources.Yield;

public class TerrainType {

	public static final ObjectIndex<TerrainType> objectIndex = new ObjectIndex<>();

	public enum Feature {
		water, forest, desert, swamp, mountain, peak
	};
	
	public final String name;
	public final Color color;
	public final Yield.Set yield = new Yield.Set();
	
	public Feature feature = null;
	
	public TerrainType(String name, Color color) {
		objectIndex.put(name, this);
		this.name = name;
		this.color = color;
	}

	public TerrainType feature(Feature f) {
		this.feature = f;
		return this;
	}
	
	public TerrainType yield(int food, int production, int gold) {
		return yield(food, production, gold, 0);
	}

	public TerrainType yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}
	
	public boolean canSettleOn() {
		return feature!=Feature.water && feature!=Feature.mountain && feature!=Feature.peak && feature!=Feature.swamp;
	}

	public static final TerrainType valley = new TerrainType("Valley", new Color(0x73ad46)).yield(1, 1, 1);
	public static final TerrainType fertileValley = new TerrainType("Fertile valley", new Color(0x6aa848)).yield(2, 1, 2);
	public static final TerrainType plains = new TerrainType("Plains", new Color(0x80ac46)).yield(1, 0, 1);
	public static final TerrainType fertilePlains= new TerrainType("Fertile plains", new Color(0x7aaa3c)).yield(2, 0, 1);
	public static final TerrainType hills = new TerrainType("Hills", new Color(0x85a443)).yield(1, 1, 0);
	public static final TerrainType forestHills = new TerrainType("Forest hills", new Color(0x6e9845)).yield(1, 2, 0).feature(Feature.forest);
	public static final TerrainType barrenHills = new TerrainType("Barren hills", new Color(0x9eab63)).yield(0, 2, 0);
	public static final TerrainType barrenPlains = new TerrainType("Barren plains", new Color(0x9fac63)).yield(0, 1, 0);
	public static final TerrainType forest = new TerrainType("Forest", new Color(0x639038)).yield(2, 2, 0).feature(Feature.forest);
	public static final TerrainType mountains = new TerrainType("Mountains", new Color(0x9aa184)).yield(0, 2, 1).feature(Feature.mountain);
	public static final TerrainType snowpeak = new TerrainType("Snow Peak", new Color(0xb1b7bb)).yield(0, 0, 1, 1).feature(Feature.peak);
	public static final TerrainType water = new TerrainType("Coastal Waters", new Color(0x8bb4bf)).yield(1, 0, 1).feature(Feature.water);

	public static final TerrainType volcano = new TerrainType("Volcano", new Color(0x908577)).yield(0, 4, 0, 0).feature(Feature.peak);
	public static final TerrainType swamp = new TerrainType("Swamp", new Color(0x86ae6b)).yield(1, 0, 0).feature(Feature.swamp);
	public static final TerrainType desert = new TerrainType("Desert", new Color(0xcccc99)).yield(0, 0, 1).feature(Feature.desert);
	public static final TerrainType desertHills = new TerrainType("Desert Hills", new Color(0xb5b580)).yield(0, 1, 1).feature(Feature.desert);
	public static final TerrainType oasis = new TerrainType("Oasis", new Color(0x93c64f)).yield(2, 0, 2, 1);
	public static final TerrainType jungle = new TerrainType("Jungle", new Color(0x588a4b)).yield(3, 1, 0).feature(Feature.forest);

	public static final TerrainType deepWater = new TerrainType("Deep Waters", new Color(0x648ca3)).yield(0, 0, 0).feature(Feature.water);
	public static final TerrainType lagoon = new TerrainType("Lagoon", new Color(0x9bc4d5)).yield(1, 0, 2, 1).feature(Feature.water);
	
}
