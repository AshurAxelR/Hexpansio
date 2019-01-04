package com.xrbpowered.hexpansio.world.tile.improv;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.city.effect.CityEffectStack;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class ImprovementStack {

	public final Improvement base;
	public ArrayList<Improvement> upgrades = new ArrayList<>();

	public int upgPoints;
	public int workplaces; // TODO use workplaces
	public int maintenance;
	public final Yield.Cache yield = new Yield.Cache();
	
	public ImprovementStack(Improvement base) {
		this.base = base;
		update();
	}
	
	public void add(Improvement upg) {
		upgrades.add(upg);
		update();
	}
	
	public String getGlyph() {
		String g = base.glyph==null ? "?" : base.glyph;
		if(!upgrades.isEmpty())
			g += "+";
		return g;
	}
	
	public void collectEffects(CityEffectStack effects) {
		base.collectEffects(effects);
		for(Improvement upg : upgrades) {
			upg.collectEffects(effects);
		}
	}
	
	public void update() {
		upgPoints = 0;
		workplaces = base.workplaces;
		maintenance = base.maintenance;
		yield.clear();
		yield.add(base.yield);
		
		for(Improvement upg : upgrades) {
			upgPoints += upg.upgPoints;
			workplaces += upg.workplaces;
			maintenance += upg.maintenance;
			yield.add(upg.yield);
		}
	}
	
	public boolean isPermanent() {
		return base==Improvement.cityCenter;
	}

	public static boolean tileContains(Tile tile, Improvement imp) {
		if(tile.improvement!=null) {
			if(tile.improvement.base==imp)
				return true;
			for(Improvement upg : tile.improvement.upgrades) {
				if(upg==imp)
					return true;
			}
		}
		return false;
	}

	public static boolean isPrerequisite(Tile tile, Improvement imp) {
		if(imp.prerequisite==null)
			return tile.improvement==null;
		else
			return tileContains(tile, imp.prerequisite);
	}
	
	public static int getYield(Tile tile, YieldResource res) {
		return (tile.improvement==null) ? 0 : tile.improvement.yield.get(res);
	}
	
	public static void write(Tile tile, ObjectIndex<Improvement> conv, DataOutputStream out) throws IOException {
		out.writeShort(tile.improvement==null ? -1 : conv.getIndex(tile.improvement.base.name)); // TODO save upgrades
	}

	public static void read(Tile tile, ObjectIndex<Improvement> conv, DataInputStream in) throws IOException {
		Improvement imp = conv.get(in.readShort()); // TODO load upgrades
		if(imp==null)
			tile.improvement = null;
		else
			tile.improvement = new ImprovementStack(imp);
	}
}
