package com.xrbpowered.hexpansio.world.tile;

import static com.xrbpowered.hexpansio.world.tile.TerrainType.*;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.xrbpowered.hexpansio.world.Region;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.utils.RandomUtils;

public class TerrainGenerator {

	private static final int dmax = 8;
	private static boolean verbose = false;
	
	public static class TerrainList {
		public final TerrainType[] list;
		public TerrainList(TerrainType... list) {
			this.list = list;
		}
		public TerrainList(List<TerrainType> list) {
			this.list = list.toArray(new TerrainType[list.size()]);
		}
		public TerrainList intersect(TerrainList other) {
			LinkedList<TerrainType> list = new LinkedList<>();
			for(int i=0; i<this.list.length; i++)
				for(int j=0; j<other.list.length; j++) {
					if(this.list[i]==other.list[j]) {
						list.add(this.list[i]);
					}
				}
			TerrainList out = new TerrainList(list);
			if(verbose) {
				System.out.println("this: "+this.toString());
				System.out.println("other: "+other.toString());
				System.out.println("\tcommon: "+out.toString());
			}
			return out;
		}
		@Override
		public String toString() {
			String s = "";
			for(int i=0; i<this.list.length; i++)
				s += list[i].name+"; ";
			return s;
		}
	}
	
	public static class Biome {
		public final TerrainList terrain;
		public final int[] w;
		public final float pwater;
		public Biome(float pwater, TerrainList terrain, int[] w) {
			this.terrain = terrain;
			this.w = w;
			this.pwater = pwater;
			if(w.length!=terrain.list.length)
				throw new RuntimeException();
		}
		public TerrainType getRandom(Random random) {
			if(random.nextFloat()<pwater)
				return water;
			else
				return terrain.list[RandomUtils.weighted(random, w)];
		}
		public int getw(TerrainType terrain) {
			for(int i=0; i<w.length; i++)
				if(this.terrain.list[i]==terrain)
					return w[i];
			return 0;
		}
	}
	
	protected class AdjDecl {
		public final TerrainType terrain;
		public final TerrainList adj;
		public AdjDecl(TerrainType terrain, TerrainList adj) {
			this.terrain = terrain;
			this.adj = adj;
		}
	}
	
	public final Biome moderate = new Biome(0.3f, new TerrainList(
			valley, fertileValley, plains, fertilePlains, hills,
			forestHills, barrenHills, barrenPlains, forest, mountains,
			snowpeak, water, swamp
		), new int[] {
			3, 1, 7, 5, 4,
			3, 3, 4, 5, 2,
			1, 1, 1
		});
	public final Biome sea = new Biome(0.8f, new TerrainList(
			water, plains, fertilePlains, forest, volcano, jungle
		), new int[] {
			5, 4, 6, 1, 1, 3
		});
	public final Biome highlands = new Biome(0.05f, new TerrainList(
			mountains, valley, fertileValley, hills, forestHills, 
			barrenHills, forest, snowpeak, water, volcano
		), new int[] {
			3, 3, 2, 1, 3,
			2, 1, 2, 3, 1
		});
	public final Biome taiga = new Biome(0.1f, new TerrainList(
			forest, plains, fertilePlains, hills, forestHills,
			mountains, snowpeak, water, volcano, swamp
		), new int[] {
			7, 1, 1, 1, 5,
			2, 1, 3, 1, 3
		});
	public final Biome dry = new Biome(0.02f, new TerrainList(
			desert, desertHills, oasis, jungle,
			plains, hills, barrenHills, barrenPlains, mountains
		), new int[] {
			6, 4, 1, 3,
			1, 1, 2, 2, 2
		});
	
	protected Biome[] biomes = {
			moderate, sea, highlands, taiga, dry
		};
	protected int[] wbiomes = {
			4, 3, 1, 2, 1
		};
	
	protected HashMap<String, TerrainList> adjRules; 

	private final Random random = new Random();
	
	public TerrainGenerator() {
		initRules();
	}
	
	protected AdjDecl[] getAdjDecl() {
		return new AdjDecl[] {
			new AdjDecl(valley, new TerrainList(hills, forestHills, barrenHills, forest, mountains, snowpeak, water, jungle)),
			new AdjDecl(fertileValley, new TerrainList(valley, hills, forestHills, mountains, snowpeak, water, jungle)),
			new AdjDecl(plains, new TerrainList(plains, fertilePlains, hills, forestHills, barrenHills, barrenPlains, forest, water, swamp, jungle)),
			new AdjDecl(fertilePlains, new TerrainList(plains, fertilePlains, hills, forestHills, forest, water, swamp, jungle)),
			new AdjDecl(hills, new TerrainList(valley, fertileValley, plains, fertilePlains, hills, forestHills, barrenHills, barrenPlains, forest, mountains, jungle, swamp)),
			new AdjDecl(forestHills, new TerrainList(valley, fertileValley, plains, fertilePlains, hills, forestHills, forest, mountains, jungle)),
			new AdjDecl(barrenHills, new TerrainList(valley, plains, hills, barrenPlains, barrenHills, mountains, volcano, desert, desertHills, snowpeak)),
			new AdjDecl(barrenPlains, new TerrainList(plains, hills, barrenPlains, barrenHills, desert, desertHills)),
			new AdjDecl(forest, new TerrainList(plains, fertilePlains, hills, forestHills, forest, mountains, water, swamp, jungle)),
			new AdjDecl(mountains, new TerrainList(valley, fertileValley, hills, forestHills, barrenPlains, forest, mountains, snowpeak, volcano, desertHills, jungle)),
			new AdjDecl(snowpeak, new TerrainList(valley, fertileValley, hills, barrenHills, mountains)),
			new AdjDecl(water, new TerrainList(water, valley, fertileValley, plains, fertilePlains, forest, volcano, swamp, jungle)),
			new AdjDecl(volcano, new TerrainList(barrenHills, mountains, water, jungle)),
			new AdjDecl(swamp, new TerrainList(swamp, plains, fertilePlains, forest, water, jungle, hills)),
			new AdjDecl(desert, new TerrainList(desert, desertHills, plains, barrenPlains, barrenHills, oasis)),
			new AdjDecl(desertHills, new TerrainList(desert, desertHills, barrenPlains, hills, barrenHills, mountains, oasis, jungle)),
			new AdjDecl(oasis, new TerrainList(desert, desertHills, barrenHills, jungle)),
			new AdjDecl(jungle, new TerrainList(jungle, valley, fertileValley, desertHills, plains, fertilePlains, hills, forestHills, forest, swamp, mountains, water, oasis, volcano)),
		};
	}
	
	protected void initRules() {
		AdjDecl[] adj = getAdjDecl();
		adjRules = new HashMap<>();
		for(int i=0; i<adj.length; i++)
			for(int j=i; j<adj.length; j++) {
				AdjDecl ai = adj[i];
				AdjDecl aj = adj[j];
				String key = getKey(ai.terrain, aj.terrain);
				if(verbose)
					System.out.println("\n"+key);
				if(i==j)
					adjRules.put(key, ai.adj);
				else {
					TerrainList common = ai.adj.intersect(aj.adj);
					if(common.list.length==0)
						System.out.println("***** No adjacency for "+key);
					adjRules.put(key, common);
					adjRules.put(getKey(aj.terrain, ai.terrain), common);
				}
			}
	}
	
	protected static String getKey(TerrainType t1, TerrainType t2) {
		return t1.name + "~" + t2.name;
	}
	
	private TerrainType selectMiddle(Biome biome, TerrainType t1, TerrainType t2) {
		if(random.nextFloat()<0.95f) {
			TerrainType t = random.nextBoolean() ? t1 : t2;
			if(t.feature==Feature.water)
				return water;
			if(t1.feature==Feature.desert && t2.feature==Feature.desert || t.feature==Feature.desert)
				return random.nextBoolean() ? t : dry.getRandom(random);
		}
		
		TerrainList adj = adjRules.get(getKey(t1, t2));
		if(adj==null || adj.list.length==0)
			return biome.getRandom(random);
		else {
			int w[] = new int[adj.list.length];
			for(int i=0; i<w.length; i++)
				w[i] = biome.getw(adj.list[i])*2+1;
			return adj.list[RandomUtils.weighted(random, w)];
		}
	}
	
	public void finaliseTile(Tile tile) {
		if(tile.terrain==oasis) {
			if(tile.countAdjTerrain(Feature.desert)<6) {
				tile.terrain = plains;
			}
		}
		else if(tile.terrain.feature==Feature.desert) {
			if(tile.countAdjTerrain(Feature.water)>0) {
				tile.terrain = plains;
			}
		}
		else if(tile.terrain==water) {
			int adjWater = tile.countAdjTerrain(Feature.water);
			
			if(adjWater==6)
				tile.terrain = deepWater;
			else if(adjWater>1 && adjWater<5) {
				random.setSeed(tile.getSeed(48619L));
				if(random.nextInt(5)==0)
					tile.terrain = lagoon;
			}
		}
		
		random.setSeed(tile.getSeed(59836L));
		tile.resource = tile.terrain.generateResource(random);
	}
	
	private Biome selectMiddleBiome(Biome b1, Biome b2) {
		return random.nextBoolean() ? b1 : b2;
	}

	public Biome biomeForRegion(long seed, int rx, int ry) {
		random.setSeed(RandomUtils.seedXY(seed+589L, rx, ry));
		return biomes[RandomUtils.weighted(random, wbiomes)];
	}

	public void generateRegion(Region region) {
		Biome[][] biomeMap = new Biome[Region.size+1][Region.size+1];
		
		for(int d=Region.size; d>0; d>>=1) {
			for(int y=0; y<=Region.size; y+=d)
				for(int x=0; x<=Region.size; x+=d) {
					Tile tile = region.tiles[x][y];
					
					boolean mx = x%(d*2)!=0; 
					boolean my = y%(d*2)!=0;
					
					Biome biome;
					if(World.dist(tile.wx, tile.wy, World.originwx, World.originwy)<=dmax)
						biome = moderate;
					else if(d==Region.size)
						biome = biomeForRegion(region.world.seed, x==0 ? region.rx : region.rx+1, y==0 ? region.ry : region.ry+1);
					else {
						random.setSeed(tile.getSeed(589732L));
						if(mx && my)
							biome = selectMiddleBiome(biomeMap[x-d][y-d], biomeMap[x+d][y+d]);
						else if(mx)
							biome = selectMiddleBiome(biomeMap[x-d][y], biomeMap[x+d][y]);
						else if(my)
							biome = selectMiddleBiome(biomeMap[x][y-d], biomeMap[x][y+d]);
						else
							biome = biomeMap[x][y];
					}
					biomeMap[x][y] = biome;
					
					
					random.setSeed(tile.getSeed(53441L));
					if(tile.wx==World.originwx && tile.wy==World.originwy)
						tile.terrain = World.originTerrain;
					else if(d>=dmax)
						tile.terrain = biome.getRandom(random);
					else {
						if(mx && my)
							tile.terrain = selectMiddle(biome, region.tiles[x-d][y-d].terrain, region.tiles[x+d][y+d].terrain);
						else if(mx)
							tile.terrain = selectMiddle(biome, region.tiles[x-d][y].terrain, region.tiles[x+d][y].terrain);
						else if(my)
							tile.terrain = selectMiddle(biome, region.tiles[x][y-d].terrain, region.tiles[x][y+d].terrain);
					}
				}
		}
	}

}
