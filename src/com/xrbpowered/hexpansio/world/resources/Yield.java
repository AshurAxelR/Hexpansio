package com.xrbpowered.hexpansio.world.resources;

public abstract class Yield {

	public abstract int get(YieldResource res);

	public int countTypes() {
		int count = 0;
		for(YieldResource res : YieldResource.values())
			if(get(res)>0) count++;
		return count;
	}
	
	public int total() {
		int total = 0;
		for(YieldResource res : YieldResource.values())
			total += get(res);
		return total;
	}
	
	public static class Set extends Yield {
		protected final int[] yield;
		
		public Set() {
			this.yield = new int[YieldResource.values().length];
		}
		
		public void set(int food, int production, int gold, int happiness) {
			yield[YieldResource.food.ordinal()] = food;
			yield[YieldResource.production.ordinal()] = production;
			yield[YieldResource.gold.ordinal()] = gold;
			yield[YieldResource.happiness.ordinal()] = happiness;
		}
		
		@Override
		public int get(YieldResource res) {
			return yield[res.ordinal()];
		}
	}
	
	public static class Cache extends Yield.Set {
		public void clear() {
			for(int i=0; i<yield.length; i++)
				yield[i] = 0;
		}
		public void add(Yield yield) {
			for(YieldResource res : YieldResource.values())
				add(res, yield.get(res));
		}
		public void add(YieldResource res, int add) {
			yield[res.ordinal()] += add;
		}
	}

}
