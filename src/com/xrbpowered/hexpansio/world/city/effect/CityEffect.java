package com.xrbpowered.hexpansio.world.city.effect;

import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;

public abstract class CityEffect {

	public void addTo(CityEffectStack effects) {
		effects.effects.add(this);
	}
	
	public abstract String getDescription();
	
	protected int addCityValue(EffectTarget key) {
		return 0;
	}

	protected float multiplyCityValue(EffectTarget key) {
		return 1f;
	}
	
	public int modifyCityValue(EffectTarget key, int value) {
		return (int)((value + addCityValue(key) * multiplyCityValue(key)));
	}
	
	public int addTileYield(Tile tile, YieldResource res) {
		return 0;
	}

	public int addResourceBonusYield(TokenResource resource, YieldResource res) {
		return 0;
	}
	
	public int tileEffect(EffectTarget key, Tile tile) {
		return 0;
	}

	private static class AddCityValue extends CityEffect {
		public final EffectTarget key;
		public final int add;
		public AddCityValue(EffectTarget key, int add) {
			this.key = key;
			this.add = add;
		}
		@Override
		protected int addCityValue(EffectTarget key) {
			return key==this.key ? add : 0;
		}
		@Override
		public String getDescription() {
			return key.formatPluralDelta(add);
		}
	}
	
	public static CityEffect add(EffectTarget key, int add) {
		return new AddCityValue(key, add);
	}
	
	private static class Dummy extends CityEffect {
		public final String desc;
		public Dummy(String desc) {
			this.desc = desc;
		}
		@Override
		public String getDescription() {
			return desc;
		}
	}
	
	public static CityEffect dummy(String desc) {
		return new Dummy(desc);
	}
}
