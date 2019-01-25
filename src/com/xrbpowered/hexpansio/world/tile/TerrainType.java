package com.xrbpowered.hexpansio.world.tile;

import static com.xrbpowered.hexpansio.world.resources.TokenResource.*;

import java.awt.Color;
import java.util.Random;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.utils.RandomUtils;

public class TerrainType {

	public static final ObjectIndex<TerrainType> objectIndex = new ObjectIndex<>();

	public enum Feature {
		thevoid("void"),
		water("water"),
		forest("forest"),
		desert("desert"),
		swamp("swamp"),
		mountain("mountain"),
		peak("peak"),
		volcano("volcano");
		public final String name;
		private Feature(String name) {
			this.name = name;
		}
	};
	
	public static final Color voidColor = new Color(0xdd33cc);
	
	public final String id;
	public final String name;
	public final Color color;
	public final Yield.Set yield = new Yield.Set();
	
	public Feature feature = null;
	public int workplaces = 1;
	
	public float tokenChance = 0f;
	public TokenResource[] tokens = null;
	public int[] wtokens = null;
	
	public TerrainType(String id, String name, Color color) {
		this.id = id;
		objectIndex.put(id, this);
		this.name = name;
		this.color = color;
	}

	public TerrainType feature(Feature f) {
		this.feature = f;
		return this;
	}
	
	public TerrainType workplaces(int w) {
		this.workplaces = w;
		return this;
	}
	
	public TerrainType yield(int food, int production, int gold) {
		return yield(food, production, gold, 0);
	}

	public TerrainType yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}
	
	public TerrainType resources(float chance, TokenResource... res) {
		this.tokenChance = chance;
		this.tokens = res;
		this.wtokens = new int[res.length];
		for(int i=0; i<wtokens.length; i++)
			wtokens[i] = res[i].rarity;
		return this;
	}
	
	public boolean hasFeature(Feature[] features) {
		if(features!=null) {
			for(Feature f : features)
				if(this.feature==f)
					return true;
		}
		return false;
	}
	
	public TokenResource generateResource(Random random) {
		if(tokens==null || random.nextFloat()>=tokenChance)
			return null;
		else
			return tokens[RandomUtils.weighted(random, wtokens)];
	}
	
	public boolean canSettleOn() {
		return feature!=Feature.water && feature!=Feature.mountain && feature!=Feature.peak && feature!=Feature.swamp;
	}

	public static final TerrainType valley = new TerrainType("valley", "Valley", new Color(0x73ad46)).yield(1, 1, 1)
			.resources(0.25f, grain, fruits, flowers);
	public static final TerrainType fertileValley = new TerrainType("valley.f", "Fertile valley", new Color(0x6aa848)).yield(2, 1, 2)
			.resources(0.5f, grain, fruits, flowers);
	public static final TerrainType plains = new TerrainType("plains", "Plains", new Color(0x80ac46)).yield(1, 0, 1)
			.resources(0.15f, grain, cattle, wool, flowers, clay);
	public static final TerrainType fertilePlains= new TerrainType("plains.f", "Fertile plains", new Color(0x7aaa3c)).yield(2, 0, 1)
			.resources(0.2f, grain, fruits, cattle, wool, flowers);
	public static final TerrainType hills = new TerrainType("hills", "Hills", new Color(0x85a443)).yield(1, 1, 0)
			.resources(0.2f, cattle, wool, iron, gold, flowers, stone);
	public static final TerrainType forestHills = new TerrainType("forest.hills", "Forest hills", new Color(0x6e9845)).yield(1, 2, 0).feature(Feature.forest)
			.resources(0.1f, iron, gold, flowers, berries);
	public static final TerrainType barrenHills = new TerrainType("barren.hills", "Barren hills", new Color(0x9eab63)).yield(0, 2, 0)
			.resources(0.2f, wool, iron, gold, gems, stone, clay, sand, cacti);
	public static final TerrainType barrenPlains = new TerrainType("barren", "Barren plains", new Color(0x9fac63)).yield(0, 1, 0)
		.resources(0.1f, wool, clay, sand, cacti);
	public static final TerrainType forest = new TerrainType("forest", "Forest", new Color(0x639038)).yield(2, 2, 0).feature(Feature.forest)
			.resources(0.05f, flowers, berries, berries, fuel);
	public static final TerrainType mountains = new TerrainType("mountains", "Mountains", new Color(0x9aa184)).yield(0, 2, 1).feature(Feature.mountain)
			.resources(0.3f, wool, iron, gold, gems, stone);
	public static final TerrainType snowpeak = new TerrainType("peack", "Snow Peak", new Color(0xb1b7bb)).yield(0, 0, 1, 1).feature(Feature.peak);
	public static final TerrainType water = new TerrainType("water", "Coastal Waters", new Color(0x8bb4bf)).yield(1, 0, 1).feature(Feature.water)
			.resources(0.1f, fish, pearls, corals, kelp, fuel);

	public static final TerrainType volcano = new TerrainType("volcano", "Volcano", new Color(0x908577)).yield(0, 4, 0, 0).feature(Feature.volcano);
	public static final TerrainType swamp = new TerrainType("swamp", "Swamp", new Color(0x86ae6b)).yield(1, 0, 0).feature(Feature.swamp)
			.resources(0.15f, berries, fuel);
	public static final TerrainType desert = new TerrainType("desert", "Desert", new Color(0xcccc99)).yield(0, 0, 1).feature(Feature.desert)
			.resources(0.1f, sand, clay, cacti, fuel, fuel, fuel);
	public static final TerrainType desertHills = new TerrainType("desert.hills", "Desert Hills", new Color(0xb5b580)).yield(0, 1, 1).feature(Feature.desert)
			.resources(0.15f, sand, clay, stone, cacti);
	public static final TerrainType oasis = new TerrainType("oasis", "Oasis", new Color(0x93c64f)).yield(2, 0, 2, 1)
			.resources(0.5f, flowers, fruits);
	public static final TerrainType jungle = new TerrainType("jungle", "Jungle", new Color(0x588a4b)).yield(3, 1, 0).feature(Feature.forest)
			.resources(0.25f, flowers, fruits, coffee);

	public static final TerrainType deepWater = new TerrainType("waters.deep", "Deep Waters", new Color(0x648ca3)).yield(0, 0, 0).feature(Feature.water)
			.resources(0.02f, fish, fuel, fuel);
	public static final TerrainType lagoon = new TerrainType("lagoon", "Lagoon", new Color(0x9bc4d5)).yield(1, 0, 2, 1).feature(Feature.water)
			.resources(0.5f, fish, pearls, corals, kelp);

	public static final TerrainType voidEdge = new TerrainType("void.edge", "Void Storm", new Color(0x775566)).workplaces(0).feature(Feature.thevoid);
	public static final TerrainType deepVoid = new TerrainType("void.deep", "Deep Void", new Color(0x442233)).workplaces(0).feature(Feature.thevoid);
	
	public static final TerrainType defaultFallback = plains;

}
