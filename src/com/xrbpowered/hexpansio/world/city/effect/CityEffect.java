package com.xrbpowered.hexpansio.world.city.effect;

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
}
