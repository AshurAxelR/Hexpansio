package com.xrbpowered.hexpansio.world.city.effect;

public class EffectTarget {
	
	public final String name;
	public final boolean countable;
	
	protected EffectTarget(String name, boolean countable) {
		this.name = name;
		this.countable = countable;
	}
	
	public String formatPluralDelta(int v) {
		return formatPluralDelta(v, name, countable);
	}
	
	public static String formatPluralDelta(int v, String name, boolean countable) {
		return (!countable || v==1 || v==-1) ?  String.format("%+d %s", v, name) : String.format("%+d %ss", v, name);
	} 

	public static String formatPluralDelta(int v, String name) {
		return formatPluralDelta(v, name, true);
	}

	public static final EffectTarget upgPoints = new EffectTarget("Upg. Point", true);
	public static final EffectTarget scouts = new EffectTarget("Scout", true);

}
