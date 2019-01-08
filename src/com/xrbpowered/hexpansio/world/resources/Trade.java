package com.xrbpowered.hexpansio.world.resources;

import com.xrbpowered.hexpansio.world.city.City;

public class Trade {

	public final City city;
	public final City otherCity;
	public final ResourcePile in;
	public final ResourcePile out;
	
	public final Trade reverse;

	private Trade(Trade reverse) {
		this.city = reverse.otherCity;
		this.otherCity = reverse.city;
		this.in = reverse.out;
		this.out = reverse.in;
		this.reverse = reverse;
	}

	public Trade(City city, City otherCity, ResourcePile in, ResourcePile out) {
		this.city = city;
		this.otherCity = otherCity;
		this.in = in;
		this.out = out;
		this.reverse = new Trade(this);
	}
	
	public Trade(City city, City otherCity) {
		this.city = city;
		this.otherCity = otherCity;
		this.in = new ResourcePile();
		this.out = new ResourcePile();
		this.reverse = new Trade(this);
	}
	
	public Trade copy() {
		return new Trade(city, otherCity, in.copy(), out.copy());
	}

}
