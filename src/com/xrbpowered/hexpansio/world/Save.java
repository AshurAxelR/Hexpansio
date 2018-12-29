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

import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.tile.Improvement;
import com.xrbpowered.hexpansio.world.tile.TerrainGenerator;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.Tile.DummyTile;

public class Save {

	public static final int formatCode = 632016289;
	
	public static final int saveVersion = 2;
	
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
		out.writeByte(convTerrainType.getIndex(tile.terrain.name));
		out.writeByte(tile.resource==null ? -1 : convResources.getIndex(tile.resource.name));
		out.writeByte(tile.workers);
		out.writeShort(tile.city==null ? -1 : tile.city.index);
		out.writeShort(tile.settlement==null ? -1 : tile.settlement.city.index);
		out.writeShort(tile.improvement==null ? -1 : convImprovement.getIndex(tile.improvement.name));
	}

	protected void readTile(DataInputStream in, Tile tile) throws IOException {
		tile.terrain = convTerrainType.get(in.readByte());
		tile.resource = convResources.get(in.readByte());
		tile.workers = in.readByte();
		int cityIndex = in.readShort();
		tile.city = cityIndex<0 ? null : tile.region.world.cities.get(cityIndex);
		cityIndex = in.readShort();
		tile.settlement = cityIndex<0 ? null : (BuildingProgress.BuiltSettlement)tile.region.world.cities.get(cityIndex).buildingProgress;
		tile.improvement = convImprovement.get(in.readShort());
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
			if(bp instanceof BuildingProgress.BuiltSettlement)
				x = buildSettlement;
			else if(bp instanceof BuildingProgress.RemoveImprovement)
				x = removeImprovement;
			else {
				Improvement imp = ((BuildingProgress.BuildImprovement) bp).improvement;
				x = convImprovement.getIndex(imp.name);
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
		if(x==noBuildingProgress)
			return null;
		else if(x==buildSettlement)
			bp = new BuildingProgress.BuiltSettlement(city, null);
		else if(x==removeImprovement)
			bp = new BuildingProgress.RemoveImprovement(null);
		else {
			Improvement imp = convImprovement.get(x);
			bp = new BuildingProgress.BuildImprovement(city, imp);
		}
		
		bp.progress = in.readInt();
		int wx = in.readInt();
		int wy = in.readInt();
		bp.tile = new Tile.DummyTile(wx, wy);
		return bp;
	}
	
	protected void writeCity(DataOutputStream out, City city) throws IOException {
		out.writeInt(city.population);
		out.writeInt(city.growth);
		writeBuildingProgress(out, city.buildingProgress);
		out.writeByte(city.availDiscover);
		out.writeByte(city.availExpand);
	}
	
	protected void readCity(DataInputStream in, City city) throws IOException {
		city.population = in.readInt();
		city.growth = in.readInt();
		city.buildingProgress = readBuildingProgress(in, city);
		city.availDiscover = in.readByte();
		city.availExpand = in.readByte();
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

		out.writeInt(world.cities.size());
		for(City city : world.cities) {
			out.writeUTF(city.name);
			writeCity(out, city);
		}

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

		int numCities = in.readInt();
		for(int i=0; i<numCities; i++) {
			String name = in.readUTF();
			City city = new City(world, null, name);
			readCity(in, city);
		}
		
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
			
			out.writeLong(world.seed);
			writeWorld(out, world);
			
			zip.closeEntry();
			zip.close();
			
			System.out.println("World saved");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	public World startNew(long seed) {
		this.world = new World(this, seed, terrainGenerator()).create();
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
			
			long seed = in.readLong();
			world = new World(this, seed, terrainGenerator());
			readWorld(in, world);

			zip.close();
		}
		catch(IOException e) {
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
