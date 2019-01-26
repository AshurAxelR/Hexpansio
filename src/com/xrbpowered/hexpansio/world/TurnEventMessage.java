package com.xrbpowered.hexpansio.world;

import java.awt.Color;

import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.tile.Tile;

public class TurnEventMessage {

	public final String city;
	public final String message;
	public final Tile focusTile;
	
	public Color color = null;
	public boolean pinned = false;
	
	public TurnEventMessage(String city, String message, Tile focusTile) {
		this.city = city;
		this.message = message;
		this.focusTile = focusTile;
	}

	public TurnEventMessage(City city, String message) {
		this(city.name, message, city.tile);
	}

	public TurnEventMessage setColor(Color color) {
		this.color = color;
		return this;
	}
	
	public TurnEventMessage pin() {
		this.pinned = true;
		return this;
	}
}
