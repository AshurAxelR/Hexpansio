package com.xrbpowered.hexpansio.world.tile.improv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class ImprovementStack {

	public final Improvement base;
	public ArrayList<Improvement> upgrades = new ArrayList<>();
	
	public ImprovementStack(Improvement base) {
		this.base = base;
	}
	
	public String getGlyph() {
		String g = base.glyph==null ? "?" : base.glyph;
		if(!upgrades.isEmpty())
			g += "+";
		return g;
	}
	
	public boolean isPermanent() {
		return base==Improvement.cityCenter;
	}
	
	public int getMaintenance() {
		int m = base.maintenance;
		for(Improvement upg : upgrades)
			m += upg.maintenance;
		return m;
	}

	public static int getYield(Tile tile, YieldResource res) {
		return (tile.improvement==null) ? 0 : tile.improvement.base.yield.get(res);
	}
	
	public static void write(Tile tile, ObjectIndex<Improvement> conv, DataOutputStream out) throws IOException {
		out.writeShort(tile.improvement==null ? -1 : conv.getIndex(tile.improvement.base.name));
	}

	public static void read(Tile tile, ObjectIndex<Improvement> conv, DataInputStream in) throws IOException {
		Improvement imp = conv.get(in.readShort());
		if(imp==null)
			tile.improvement = null;
		else
			tile.improvement = new ImprovementStack(imp);
	}
}
