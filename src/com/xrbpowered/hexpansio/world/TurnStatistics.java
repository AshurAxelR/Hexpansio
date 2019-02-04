package com.xrbpowered.hexpansio.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public class TurnStatistics {

	public final int turn;
	
	public int numCities;
	public int baseHapiness;
	
	public int territory;
	public int population;
	public int workplaces;
	
	public int minHappy, aveHappy, maxHappy;
	public Happiness minHappiness = Happiness.happy; // no history
	
	public int gold, goods;
	public Yield.Cache yield = new Yield.Cache();
	public int goodsIn = 0; // no history
	
	public int resProduced, resTraded;
	public int voidStorms;
	
	public TurnStatistics(int turn) {
		this.turn = turn;
	}
	
	public void update(World world) {
		numCities = world.cities.size();
		baseHapiness = world.baseHappiness;

		territory = 0;
		population = 0;
		workplaces = 0;

		boolean first = true;
		int sumHappy = 0;
		minHappiness = Happiness.happy;

		gold = world.gold;
		goods = world.goods;
		yield.clear();
		goodsIn = 0;
		
		resProduced = 0;
		resTraded = 0;
		
		voidStorms = world.countVoidStorms();
		
		for(City city : world.cities) {
			territory += city.numTiles;
			population += city.population;
			workplaces += city.workplaces;

			int happy = city.balance.get(YieldResource.happiness);
			if(first || happy<minHappy)
				minHappy = happy;
			if(first || happy>maxHappy)
				maxHappy = happy;
			sumHappy += happy;
			if(city.happiness.ordinal()>minHappiness.ordinal())
				minHappiness = city.happiness;

			yield.add(YieldResource.happiness, city.balance.get(YieldResource.happiness));
			yield.add(YieldResource.food, city.balance.get(YieldResource.food));
			yield.add(YieldResource.gold, city.balance.get(YieldResource.gold));
			yield.add(YieldResource.production, city.getProduction());
			
			if(city.buildingProgress==null)
				goodsIn += city.getExcess(city.getProduction());
		
			resProduced += city.resourcesProduced.totalCount();
			city.trades.updateTotal();
			resTraded += city.trades.countOut;
			
			first = false;
		}
		aveHappy = sumHappy / numCities;
	}
	
	public void write(DataOutputStream out) throws IOException {
		out.writeInt(numCities);
		out.writeInt(baseHapiness);
		out.writeInt(territory);
		out.writeInt(population);
		out.writeInt(workplaces);
		out.writeInt(minHappy);
		out.writeInt(aveHappy);
		out.writeInt(maxHappy);
		out.writeInt(gold);
		out.writeInt(goods);
		for(YieldResource res : YieldResource.values())
			out.writeInt(yield.get(res));
		out.writeInt(resProduced);
		out.writeInt(resTraded);
		out.writeInt(voidStorms);
	}
	
	public void read(DataInputStream in) throws IOException {
		numCities = in.readInt();
		baseHapiness = in.readInt();
		territory = in.readInt();
		population = in.readInt();
		workplaces = in.readInt();
		minHappy = in.readInt();
		aveHappy = in.readInt();
		maxHappy = in.readInt();
		gold = in.readInt();
		goods = in.readInt();
		yield.clear();
		for(YieldResource res : YieldResource.values())
			yield.add(res, in.readInt());
		resProduced = in.readInt();
		resTraded = in.readInt();
		voidStorms = in.readInt();
	}


}
