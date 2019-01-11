package com.xrbpowered.hexpansio.world.resources;

import java.util.HashMap;

import com.xrbpowered.hexpansio.ui.modes.TradeMode;
import com.xrbpowered.hexpansio.world.city.City;

public class TradeList {

	public final City city;
	private HashMap<Integer, Trade> trades = new HashMap<>();
	
	public final ResourcePile totalIn = new ResourcePile();
	public final ResourcePile totalOut = new ResourcePile();
	
	public int countIn = 0;
	public int countOut = 0;
	public int profit = 0;

	public TradeList(City city) {
		this.city = city;
	}
	
	public void updateTotal() {
		totalIn.clear();
		totalOut.clear();
		for(Trade t : trades.values()) {
			totalIn.add(t.in);
			totalOut.add(t.out);
		}
		
		countIn = totalIn.totalCount();
		countOut = totalOut.totalCount();
		profit = countIn + countOut - Math.abs(countIn - countOut)*3;
	}
	
	public int getTotalOutExcluding(TokenResource res, City otherCity) {
		int count = 0;
		for(Trade t : trades.values()) {
			if(t.otherCity!=otherCity)
				count += t.out.count(res);
		}
		return count;
	}
	
	public Trade get(City otherCity) {
		return trades.get(otherCity.index);
	}

	public Trade getCopy(City otherCity) {
		Trade t = trades.get(otherCity.index);
		if(t==null)
			return new Trade(city, otherCity);
		else
			return t.copy();
	}

	public void accept(Trade trade) {
		if(trade.city!=this.city || this.city.tile.distTo(trade.otherCity.tile)>TradeMode.cityRange)
			return;
		if(trade.in.isEmpty() && trade.out.isEmpty()) {
			trades.remove(trade.otherCity.index);
			trade.otherCity.trades.trades.remove(this.city.index);
		}
		else {
			trades.put(trade.otherCity.index, trade);
			trade.otherCity.trades.trades.put(this.city.index, trade.reverse);
		}
		updateTotal();
	}

}
