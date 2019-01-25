package com.xrbpowered.hexpansio.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.build.BuildImprovement;
import com.xrbpowered.hexpansio.world.city.build.BuildingProgress;
import com.xrbpowered.hexpansio.world.city.build.BuiltSettlement;
import com.xrbpowered.hexpansio.world.city.build.RemoveImprovement;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.Trade;
import com.xrbpowered.hexpansio.world.tile.TerrainGenerator;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.Tile.DummyTile;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;

public class Save {

	public static final int formatCode = 632016289;
	
	public static final int saveVersion = 6;
	
	public final String path;
	public final File file;

	public World world = null;
	
	protected int version = 0;
	protected ObjectIndex<TerrainType> convTerrainType = null;
	protected ObjectIndex<TokenResource> convResources = null;
	protected ObjectIndex<Improvement> convImprovement = null;
	
	public Save(String path) {
		this.path = path;
		this.file = new File(path);
	}
	
	public boolean exists() {
		return file.exists();
	}
	
	public boolean delete() {
		if(exists())
			return file.delete();
		else
			return true;
	}
	
	protected TerrainGenerator terrainGenerator() {
		return new TerrainGenerator();
	}

	protected void writeTile(DataOutputStream out, Tile tile) throws IOException {
		out.writeByte(convTerrainType.getIndex(tile.terrain.id));
		out.writeByte(tile.resource==null ? -1 : convResources.getIndex(tile.resource.id));
		out.writeByte(tile.workers);
		out.writeInt(tile.city==null ? -1 : tile.city.index);
		out.writeInt(tile.settlement==null ? -1 : tile.settlement.city.index);

		ImprovementStack.write(tile, convImprovement, out);
	}

	protected void readTile(DataInputStream in, Tile tile) throws IOException {
		tile.terrain = convTerrainType.get(in.readByte());
		if(tile.terrain==null)
			tile.terrain = TerrainType.defaultFallback;
		if(tile.isVoid())
			tile.region.hasVoid = true;
		tile.resource = convResources.get(in.readByte());
		tile.workers = in.readByte();
		int cityIndex = in.readInt();
		tile.city = cityIndex<0 ? null : tile.region.world.cities.get(cityIndex);
		cityIndex = in.readInt();
		tile.settlement = cityIndex<0 ? null : (BuiltSettlement)tile.region.world.cities.get(cityIndex).buildingProgress;
		
		ImprovementStack.read(tile, convImprovement, in);
		if(tile.isCityCenter())
			tile.city.setTile(tile);
	}

	protected void writeRegion(DataOutputStream out, Region region) throws IOException {
		for(int x=0; x<=Region.size; x++)
			for(int y=0; y<=Region.size; y++) {
				Tile t = region.tiles[x][y]; 
				if(t.discovered) {
					out.writeByte(x);
					out.writeByte(y);
					writeTile(out, t);
				}
			}
		out.writeByte(-1);
	}
	
	protected void readRegion(DataInputStream in, Region region) throws IOException {
		for(;;) {
			int x = in.readByte();
			if(x<0)
				return;
			int y = in.readByte();
			Tile t = region.tiles[x][y];
			t.discovered = true;
			readTile(in, t);
		}
	}

	private static final int noBuildingProgress = -1;
	private static final int buildSettlement = -2;
	private static final int removeImprovement = -3;
	
	protected void writeBuildingProgress(DataOutputStream out, BuildingProgress bp) throws IOException {
		if(bp==null)
			out.writeShort(noBuildingProgress);
		else {
			int x;
			if(bp instanceof BuiltSettlement)
				x = buildSettlement;
			else if(bp instanceof RemoveImprovement)
				x = removeImprovement;
			else {
				Improvement imp = ((BuildImprovement) bp).improvement;
				x = convImprovement.getIndex(imp.id);
			}
			out.writeShort(x);
			
			out.writeInt(bp.progress);
			out.writeInt(bp.tile.wx);
			out.writeInt(bp.tile.wy);
		}
	}
	
	protected BuildingProgress readBuildingProgress(DataInputStream in, City city) throws IOException {
		int x = in.readShort();
		BuildingProgress bp;
		boolean skip = false;
		if(x==noBuildingProgress)
			return null;
		else if(x==buildSettlement)
			bp = new BuiltSettlement(city, null);
		else if(x==removeImprovement)
			bp = new RemoveImprovement(null);
		else {
			Improvement imp = convImprovement.get(x);
			skip = imp==null;
			bp = new BuildImprovement(city, imp);
		}
		
		bp.progress = in.readInt();
		int wx = in.readInt();
		int wy = in.readInt();
		bp.tile = new Tile.DummyTile(wx, wy);
		return skip ? null : bp;
	}
	
	protected void writeTrade(DataOutputStream out, Trade trade) throws IOException {
		if(trade.city.index<trade.otherCity.index) {
			out.writeInt(trade.city.index);
			out.writeInt(trade.otherCity.index);
			ResourcePile.write(trade.out, convResources, out);
			ResourcePile.write(trade.in, convResources, out);
		}
	}
	
	protected boolean readTrade(DataInputStream in, World world) throws IOException {
		int cityIndex = in.readInt();
		if(cityIndex<0)
			return false;
		else {
			City city = world.cities.get(cityIndex);
			City otherCity = world.cities.get(in.readInt());
			ResourcePile resOut = new ResourcePile();
			ResourcePile.read(resOut, convResources, in);
			ResourcePile resIn = new ResourcePile();
			ResourcePile.read(resIn, convResources, in);
			city.trades.accept(new Trade(city, otherCity, resIn, resOut), false);
			return true;
		}
	}
	
	protected void writeCity(DataOutputStream out, City city) throws IOException {
		out.writeInt(city.population);
		out.writeInt(city.growth);
		writeBuildingProgress(out, city.buildingProgress);
		out.writeByte(city.availExpand ? 1 : 0);
	}
	
	protected void readCity(DataInputStream in, City city) throws IOException {
		city.population = in.readInt();
		city.growth = in.readInt();
		city.buildingProgress = readBuildingProgress(in, city);
		city.availExpand = in.readByte()>0;
	}
	
	protected void realiseCity(City city) {
		if(city.buildingProgress!=null)
			city.buildingProgress.setTile(((DummyTile) city.buildingProgress.tile).realise(city.world));
	}
	
	protected void writeWorld(DataOutputStream out, World world) throws IOException {
		out.writeByte(world.cityNameBaseLength);
		
		out.writeInt(world.turn);
		out.writeInt(world.poverty);
		out.writeInt(world.gold);
		out.writeInt(world.goods);
		out.writeInt(world.discoverThisTurn);

		out.writeInt(world.cities.size());
		for(City city : world.cities) {
			out.writeUTF(city.name);
			writeCity(out, city);
		}

		for(City city : world.cities) {
			for(Trade trade : city.trades.getAll())
				writeTrade(out, trade);
		}
		out.writeInt(-1);
		
		ArrayList<Integer> regions = world.regionIds();
		out.writeInt(regions.size());
		for(Integer id : regions) {
			Region r = world.getRegionById(id);
			out.writeShort(r.rx);
			out.writeShort(r.ry);
			writeRegion(out, r);
		}
	}
	
	protected void readWorld(DataInputStream in, World world) throws IOException {
		world.cityNameBaseLength = in.readByte();
		
		world.turn = in.readInt();
		world.poverty = in.readInt();
		world.gold = in.readInt();
		world.goods = in.readInt();
		world.discoverThisTurn = in.readInt();

		int numCities = in.readInt();
		for(int i=0; i<numCities; i++) {
			String name = in.readUTF();
			City city = new City(world, null, name);
			readCity(in, city);
		}
		
		while(readTrade(in, world));
		
		int numRegions = in.readInt();
		for(int i=0; i<numRegions; i++) {
			int rx = in.readShort();
			int ry = in.readShort();
			Region r = world.discoverRegion(rx, ry);
			readRegion(in, r);
		}
		
		world.origin = world.getTile(World.originwx, World.originwy);
		for(City city : world.cities)
			realiseCity(city);
	}
	
	public void write() {
		if(world==null)
			return;
		
		try {
			ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(file));
			zip.putNextEntry(new ZipEntry("savedata"));
			DataOutputStream out = new DataOutputStream(zip);
			
			out.writeInt(formatCode);
			
			version = saveVersion;
			out.writeInt(version);
			convTerrainType = TerrainType.objectIndex.write(out);
			convResources = TokenResource.objectIndex.write(out);
			convImprovement = Improvement.objectIndex.write(out);
			
			WorldSettings.write(out, world.settings);
			writeWorld(out, world);
			
			zip.closeEntry();
			zip.close();
			
			System.out.println("World saved");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public World startNew(WorldSettings settings) {
		System.out.println("New world");
		this.world = new World(this, settings, terrainGenerator()).create();
		return this.world;
	}
	
	public World read() {
		World world;
		try {
			ZipInputStream zip = new ZipInputStream(new FileInputStream(file));
			zip.getNextEntry();
			DataInputStream in = new DataInputStream(zip);
			
			if(in.readInt()!=formatCode) {
				in.close();
				throw new IOException("Not a save file.");
			}
			
			version = in.readInt();
			if(version!=saveVersion)
				System.err.println("Save version is different");
			convTerrainType = TerrainType.objectIndex.read(in);
			convResources = TokenResource.objectIndex.read(in);
			convImprovement = Improvement.objectIndex.read(in);
			
			WorldSettings settings = WorldSettings.read(in);
			world = new World(this, settings, terrainGenerator());
			readWorld(in, world);

			zip.close();
		}
		catch(Exception e) {
			e.printStackTrace();
			return null;
		}
		world.updateCities();
		world.updateWorldTotals();
		System.out.println("World loaded");
		
		this.world = world;
		return world;
	}

}
