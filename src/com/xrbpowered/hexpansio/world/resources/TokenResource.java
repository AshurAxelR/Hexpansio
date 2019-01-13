package com.xrbpowered.hexpansio.world.resources;

import com.xrbpowered.hexpansio.res.ImageAtlas;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.zoomui.GraphAssist;

public class TokenResource {

	public static final ObjectIndex<TokenResource> objectIndex = new ObjectIndex<>();

	public static final int legendary = 1;
	public static final int superRare = 2;
	public static final int rare = 4;
	public static final int uncommon = 8;
	public static final int common = 16;

	public final int rarity;
	public final String name;
	public final Yield.Set yield = new Yield.Set();
	public final Yield.Set terrainBonus = new Yield.Set();

	public final ImageAtlas atlas;
	public final int subImage;
	
	public Improvement improvement = null;
	
	public TokenResource(int rarity, String name, ImageAtlas atlas, int subImage) {
		objectIndex.put(name, this);
		this.rarity = rarity;
		this.name = name;
		this.atlas = atlas;
		this.subImage = subImage;
	}

	public TokenResource yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}

	public TokenResource terrain(int food, int production, int gold, int happiness) {
		terrainBonus.set(food, production, gold, happiness);
		return this;
	}
	
	public TokenResource requires(Improvement imp) {
		this.improvement = imp;
		return this;
	}
	
	public void paint(GraphAssist g, int x, int y, String sub) {
		atlas.draw(g, x, y, 30, subImage);
		if(sub!=null)
			g.drawString(sub, x+15, y+35, GraphAssist.CENTER, GraphAssist.TOP);
	}
	
	public static final TokenResource grain = new TokenResource(common, "Grain", Res.imgRes, 0)
			.requires(Improvement.farm).terrain(1, 0, 0, 0).yield(1, 0, 0, 0);
	public static final TokenResource cattle = new TokenResource(common, "Cattle", Res.imgRes, 4)
			.requires(Improvement.pasture).terrain(0, 1, 0, 0).yield(1, 1, 0, 0);
	public static final TokenResource wool = new TokenResource(uncommon, "Wool", Res.imgRes, 5)
			.requires(Improvement.pasture).terrain(0, 1, 0, 0).yield(0, 1, 1, 0);

	public static final TokenResource berries = new TokenResource(uncommon, "Berries", Res.imgRes, 6)
			.requires(Improvement.gatherer).terrain(1, 0, 0, 0).yield(2, 0, 0, 0);
	public static final TokenResource fruits = new TokenResource(common, "Friuts", Res.imgRes, 7)
			.requires(Improvement.plantation).terrain(1, 0, 0, 0).yield(1, 0, 0, 1);
	public static final TokenResource flowers = new TokenResource(uncommon, "Flowers", Res.imgRes, 3)
			.requires(Improvement.park).terrain(0, 0, 0, 1).yield(0, 0, 0, 2);
	public static final TokenResource coffee = new TokenResource(superRare, "Coffee", Res.imgRes, 8)
			.requires(Improvement.plantation).terrain(1, 1, 0, 0).yield(1, 2, 0, 1);
	
	public static final TokenResource iron = new TokenResource(uncommon, "Iron", Res.imgRes, 1)
			.requires(Improvement.mine).terrain(0, 1, 0, 0).yield(0, 2, 0, 0);
	public static final TokenResource gold = new TokenResource(rare, "Gold", Res.imgRes, 2)
			.requires(Improvement.mine).terrain(0, 0, 2, 0).yield(0, 0, 2, 0);
	public static final TokenResource gems = new TokenResource(superRare, "Gems", Res.imgRes, 9)
			.requires(Improvement.mine).terrain(0, 0, 2, 0).yield(0, 0, 2, 2);

	public static final TokenResource stone = new TokenResource(common, "Stone", Res.imgRes, 10)
			.requires(Improvement.quarry).terrain(0, 1, 0, 0).yield(0, 1, 0, 0);
	public static final TokenResource clay = new TokenResource(common, "Clay", Res.imgRes, 11)
			.requires(Improvement.quarry).terrain(0, 0, 1, 0).yield(0, 1, 0, 0);
	public static final TokenResource sand = new TokenResource(common, "Sand", Res.imgRes, 12)
			.requires(Improvement.quarry).terrain(0, 1, 0, 0).yield(0, 1, 0, 0);
	public static final TokenResource cacti = new TokenResource(uncommon, "Cacti", Res.imgRes, 16)
			.requires(Improvement.plantation).terrain(1, 0, 1, 0).yield(1, 0, 0, 0);
	public static final TokenResource fuel = new TokenResource(superRare, "Fuel", Res.imgRes, 18)
			.requires(Improvement.drill).terrain(0, 1, 1, 0).yield(0, 2, 2, 0);

	public static final TokenResource fish = new TokenResource(common, "Fish", Res.imgRes, 13)
			.requires(Improvement.boat).terrain(1, 0, 0, 0).yield(1, 0, 0, 0);
	public static final TokenResource pearls = new TokenResource(superRare, "Pearls", Res.imgRes, 14)
			.requires(Improvement.boat).terrain(0, 0, 2, 0).yield(0, 0, 3, 0);
	public static final TokenResource corals = new TokenResource(rare, "Corals", Res.imgRes, 15)
			.requires(Improvement.boat).terrain(1, 0, 1, 0).yield(0, 0, 1, 1);
	public static final TokenResource kelp = new TokenResource(uncommon, "Kelp", Res.imgRes, 17)
			.requires(Improvement.boat).terrain(0, 0, 1, 0).yield(1, 0, 1, 0);

}
