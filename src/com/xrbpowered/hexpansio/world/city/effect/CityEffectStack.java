package com.xrbpowered.hexpansio.world.city.effect;

import java.util.ArrayList;

import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class CityEffectStack extends CityEffect {

	public ArrayList<CityEffect> effects = new ArrayList<>();

	public CityEffectStack() {
	}

	public CityEffectStack(CityEffect[] effects) {
		for(CityEffect eff : effects)
			eff.addTo(this);
	}
	
	@Override
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		for(CityEffect eff : effects) {
			if(sb.length()>0)
				sb.append("\n");
			sb.append(eff.getDescription());
		}
		return sb.toString();
	}

	@Override
	public void addTo(CityEffectStack effects) {
		for(CityEffect eff : this.effects)
			eff.addTo(effects);
	}
	
	@Override
	protected int addCityValue(EffectTarget key) {
		int value = 0;
		for(CityEffect eff : effects)
			value += eff.addCityValue(key);
		return value;
	}

	@Override
	protected float multiplyCityValue(EffectTarget key) {
		float value = 1f;
		for(CityEffect eff : effects)
			value *= eff.multiplyCityValue(key);
		return value;
	}
	
	@Override
	public int addTileYield(Tile tile, YieldResource res) {
		int value = 0;
		for(CityEffect eff : effects)
			value += eff.addTileYield(tile, res);
		return value;
	}
	
	@Override
	public int addResourceBonusYield(TokenResource resource, YieldResource res) {
		int value = 0;
		for(CityEffect eff : effects)
			value += eff.addResourceBonusYield(resource, res);
		return value;
	}
	
	@Override
	public int tileEffect(EffectTarget key, Tile tile) {
		int value = 0;
		for(CityEffect eff : effects)
			value += eff.tileEffect(key, tile);
		return value;
	}

}
