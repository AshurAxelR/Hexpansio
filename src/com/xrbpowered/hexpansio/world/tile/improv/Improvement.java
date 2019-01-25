package com.xrbpowered.hexpansio.world.tile.improv;

import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.effect.CityEffect;
import com.xrbpowered.hexpansio.world.city.effect.CityEffectStack;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class Improvement implements Comparable<Improvement> {

	public static final ObjectIndex<Improvement> objectIndex = new ObjectIndex<>();
	public static final HashMap<Integer, Improvement> hotkeyMap = new HashMap<>();
	
	public final String id;
	public final Improvement prerequisite;
	public final String name;
	public final int buildCost;
	public final int upgPoints;
	public final Yield.Set yield = new Yield.Set();
	public final Yield.Set yieldPerWorker = new Yield.Set();
	
	public String glyph = null;
	public int hotkey = 0;
	
	public CityEffect effect = null;
	
	public int workplaces = 0;
	public int maintenance = 0;
	public int bonusResources = 0;
	private Feature[] rejectFeatures = {Feature.water, Feature.peak, Feature.volcano, Feature.thevoid};
	private Feature[] reqFeatures = null;
	private boolean reqResource = false;
	private boolean reqCoastalCity = false;
	private int reqPopulation = 0;
	public Improvement cityPrerequisite = null;

	public boolean cityUnique = false;
	public boolean canHurry = true;
	public boolean voidUnlock = false;
	
	public Improvement(String id, Improvement prerequisite, String name, int buildCost, int upgPoints) {
		this.id = id;
		objectIndex.put(id, this);
		this.prerequisite = prerequisite;
		this.name = name;
		this.buildCost = buildCost;
		this.upgPoints = upgPoints;
	}

	public Improvement(String id, String name, int buildCost) {
		this(id, null, name, buildCost, 0);
	}
	
	@Override
	public int compareTo(Improvement o) {
		return name.compareTo(o.name);
	}
	
	public Improvement yield(int food, int production, int gold, int happiness) {
		yield.set(food, production, gold, happiness);
		return this;
	}

	public Improvement yieldPerWorker(int food, int production, int gold, int happiness) {
		yieldPerWorker.set(food, production, gold, happiness);
		return this;
	}

	public Improvement setGlyph(String glyph) {
		this.glyph = glyph;
		return this;
	}

	public Improvement hotkey(int key) {
		this.hotkey = key;
		hotkeyMap.put(key, this);
		return this;
	}

	public Improvement bonusResources(int r) {
		this.bonusResources = r;
		return this;
	}

	public Improvement maintenance(int m) {
		this.maintenance = m;
		return this;
	}

	public Improvement workplaces(int w) {
		this.workplaces = w;
		return this;
	}

	public Improvement reject(Feature... features) {
		this.rejectFeatures = features;
		return this;
	}

	public Improvement require(Feature... features) {
		this.reqFeatures = features;
		return this;
	}
	
	public Improvement requireResource() {
		this.reqResource = true;
		return this;
	}

	public Improvement cannotHurry() {
		this.canHurry = false;
		return this;
	}

	public Improvement voidUnlock() {
		this.voidUnlock = true;
		return this;
	}

	public Improvement requireCoastalCity() {
		this.reqCoastalCity = true;
		return this;
	}

	public Improvement requirePopulation(int pop) {
		this.reqPopulation = pop;
		return this;
	}

	public Improvement cityPrerequisite(Improvement imp) {
		this.cityPrerequisite = imp;
		return this;
	}

	public Improvement cityUnique() {
		this.cityUnique = true;
		return this;
	}

	public Improvement effects(CityEffect... effects) {
		if(effects==null)
			this.effect = null;
		else if(effects.length==1)
			this.effect = effects[0];
		else
			this.effect = new CityEffectStack(effects);
		return this;
	}
	
	public Improvement getBase() {
		return prerequisite==null ? this : prerequisite.getBase();
	}
	
	public void collectEffects(CityEffectStack effects) {
		if(effect!=null)
			effect.addTo(effects);
	}

	public boolean canBuildOn(Tile tile) {
		return (prerequisite==null || ImprovementStack.isPrerequisite(tile, this)) &&
				(!cityUnique || !ImprovementStack.cityContains(tile, this)) &&
				(cityPrerequisite==null || ImprovementStack.cityContains(tile, cityPrerequisite)) &&
				(reqFeatures==null || tile.terrain.hasFeature(reqFeatures)) &&
				!tile.terrain.hasFeature(rejectFeatures) &&
				(!reqResource || tile.resource!=null && tile.resource.improvement==this.getBase()) &&
				(tile.city.coastalCity || !reqCoastalCity && tile.terrain.feature!=Feature.water) &&
				(tile.city.population>=reqPopulation) &&
				(upgPoints==0 || upgPoints<=ImprovementStack.getAvailUpgPoints(tile));
	}
	
	public String requirementExplained(Tile tile) {
		if(!(prerequisite==null || ImprovementStack.isPrerequisite(tile, this))) {
			return String.format("Requires %s", prerequisite.name);
		}
		else if(!(!cityUnique || !ImprovementStack.cityContains(tile, this))) {
			return "Already built in this city";
		}
		else if(!(cityPrerequisite==null || ImprovementStack.cityContains(tile, cityPrerequisite))) {
			return String.format("Requires %s", cityPrerequisite.name);
		}
		else if(!(reqFeatures==null || tile.terrain.hasFeature(reqFeatures))) {
			StringBuilder sb = new StringBuilder();
			for(int i=0; i<reqFeatures.length; i++) {
				if(i>0 && i==rejectFeatures.length-1)
					sb.append(" or ");
				else if(i>0)
					sb.append(", ");
				sb.append(reqFeatures[i].name);
			}
			return String.format("Requires %s terrain", sb.toString());
		}
		else if(tile.terrain.hasFeature(rejectFeatures)) {
			return String.format("Cannot be built on %s terrain", tile.terrain.feature.name);
		}
		else if(!(!reqResource || tile.resource!=null && tile.resource.improvement==this.getBase())) {
			return "Requires appropriate resource";
		}
		else if(!(tile.city.coastalCity || !reqCoastalCity && tile.terrain.feature!=Feature.water)) {
			return "Requires coastal city";
		}
		else if(!(tile.city.population>=reqPopulation)) {
			return String.format("Requires %d population", reqPopulation);
		}
		else if(!(upgPoints==0 || upgPoints<=ImprovementStack.getAvailUpgPoints(tile))) {
			return "Not enough upgrade points";
		}
		else
			return null;
	}
	
	public boolean isRecommendedFor(Tile tile) {
		return canBuildOn(tile) &&
				(tile.resource!=null && tile.resource.improvement==this);
	}
	
	public String recommendationExplained(Tile tile) {
		if(tile.resource!=null && tile.resource.improvement==this) {
			return "Will produce "+tile.resource.name;
		}
		else
			return null;
	}
	
	public static ArrayList<Improvement> createBuildList(Tile tile) {
		ArrayList<Improvement> impList = new ArrayList<>();
		for(int i=0; i<Improvement.objectIndex.size(); i++) {
			Improvement imp = Improvement.objectIndex.get(i);
			if(imp!=Improvement.cityCenter && !ImprovementStack.tileContains(tile, imp) &&
					(!imp.voidUnlock || tile.region.world.hasVoid()) &&
					(imp.prerequisite==null && tile.improvement==null || imp.prerequisite!=null && ImprovementStack.tileContains(tile, imp.prerequisite.getBase()))) {
				impList.add(imp);
			}
		}
		return impList;
	}
	
	public static final Improvement cityCenter = new Improvement("city", "City", 0).workplaces(0).yield(1, 1, 1, 0)
			.effects(
					CityEffect.add(EffectTarget.scouts, 1),
					CityEffect.add(EffectTarget.upgPoints, 1),
					CityEffect.dummy(EffectTarget.upgPoints.formatPluralDelta(City.cityUpgPoints)+" in the City tile")
				);
	
	public static final Improvement farm = new Improvement("farm", "Farm", 30).hotkey(KeyEvent.VK_F).setGlyph("F")
			.yield(2, 0, 0, 0).reject(Feature.values());
	public static final Improvement mine = new Improvement("mine", "Mine", 40).hotkey(KeyEvent.VK_M).setGlyph("M")
			.maintenance(1).yield(0, 3, 0, 0).requireResource();
	public static final Improvement lumberMill = new Improvement("lumbermill", "Lumber Mill", 60).hotkey(KeyEvent.VK_L).setGlyph("L")
			.yield(0, 2, 0, 0).require(Feature.forest);
	public static final Improvement gatherer = new Improvement("gatherer", "Gatherer", 20).hotkey(KeyEvent.VK_G).setGlyph("G")
			.yield(1, 1, 0, 0).require(Feature.forest, Feature.swamp);
	public static final Improvement market = new Improvement("market", "Market", 50).hotkey(KeyEvent.VK_T).setGlyph("T")
			.yield(0, 0, 2, 0).reject(Feature.water, Feature.mountain, Feature.peak, Feature.volcano, Feature.thevoid);
	public static final Improvement park = new Improvement("park", "Park", 30).hotkey(KeyEvent.VK_P).setGlyph("P")
			.yield(0, 0, 0, 1).maintenance(1).reject(Feature.water, Feature.desert, Feature.volcano, Feature.thevoid);
	public static final Improvement pasture = new Improvement("pasture", "Pasture", 30).hotkey(KeyEvent.VK_U).setGlyph("U")
			.yield(1, 0, 0, 0)	.reject(Feature.water, Feature.desert, Feature.forest, Feature.swamp, Feature.peak, Feature.volcano, Feature.thevoid);
	public static final Improvement plantation = new Improvement("plantation", "Plantation", 40).hotkey(KeyEvent.VK_N).setGlyph("N")
			.yield(1, 0, 1, 0)	.reject(Feature.water, Feature.peak, Feature.volcano, Feature.thevoid).requireResource();
	public static final Improvement quarry = new Improvement("quarry", "Quarry", 30).hotkey(KeyEvent.VK_Y).setGlyph("Y")
			.yield(0, 1, 1, 0).reject(Feature.water, Feature.forest, Feature.peak, Feature.volcano, Feature.thevoid);
	public static final Improvement boat = new Improvement("boat", "Fishing Boat", 20).hotkey(KeyEvent.VK_B).setGlyph("B")
			.reject((Feature[])null).require(Feature.water).yield(1, 0, 0, 0).requireResource();
	public static final Improvement drill = new Improvement("drill", "Drill", 60).hotkey(KeyEvent.VK_I).setGlyph("I")
			.reject(Feature.thevoid).yield(0, 2, 1, 0).requireResource();

	public static final Improvement voidworks = new Improvement("voidworks", "Voidworks", 20).hotkey(KeyEvent.VK_V).setGlyph("V")
			.voidUnlock().cityPrerequisite(CityUpgrades.beaconOfHope)
			.reject((Feature[])null).require(Feature.thevoid).yield(0, 1, 1, 0).workplaces(1);

	static {
		CityUpgrades.init();
		FarmUpgrades.init();
		MineUpgrades.init();
		MarketUpgrades.init();
		ParkUpgrades.init();
		OtherUpgrades.init();
	}

}
