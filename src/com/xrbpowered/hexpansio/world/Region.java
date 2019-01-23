package com.xrbpowered.hexpansio.world;

import java.util.ArrayList;
import java.util.Random;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.utils.RandomUtils;

public class Region {

	public static final int voidBlockSide = 4;
	
	public static final int size = 64;
	public static final int sized = 6;

	public final Tile[][] tiles = new Tile[size+1][size+1];

	public final World world;
	public final int rx, ry;
	
	public final ArrayList<City> cities = new ArrayList<>();
	
	public boolean hasVoid = false;
	
	public Region(World world, int rx, int ry) {
		this.world = world;
		this.rx = rx;
		this.ry = ry;
		for(int x=0; x<=size; x++)
			for(int y=0; y<=size; y++)
				tiles[x][y] = new Tile(this, x, y);
		world.terrainGenerator.generateRegion(this);
	}
	
	public int getId() {
		return getId(rx, ry);
	}
	
	public static int getId(int rx, int ry) {
		return (rx<<16) | (ry & 0xffff);
	}
	
	public void spreadVoid() {
		if(hasVoid) {
			Random random = new Random(RandomUtils.seedXY(world.seed+world.turn, rx, ry));
			int d = voidBlockSide;
			for(int x=0; x<size; x+=d)
				for(int y=0; y<size; y+=d) {
					for(int i=0; i<world.settings.voidSpreadSpeed; i++) {
						int c = random.nextInt(d*d);
						tiles[x+c/d][y+c%d].spreadVoid();
					}
				}
		}
	}
}
