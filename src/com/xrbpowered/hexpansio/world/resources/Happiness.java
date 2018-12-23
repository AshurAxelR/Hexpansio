package com.xrbpowered.hexpansio.world.resources;

import java.awt.Color;

public enum Happiness {

	happy("Happy", Color.WHITE, 0, 0),
	content("Content", new Color(0xbbbbbb), 0, 0),
	unhappy("Unhappy", Color.YELLOW, 50, 25),
	angry("Angry", Color.ORANGE, 100, 50),
	raging("Raging", Color.RED, 100, 100);
	
	public final String name;
	public final Color color;
	public final int growthPenalty;
	public final int prodPenalty;
	
	private Happiness(String name, Color color, int foodPenalty, int prodPenalty) {
		this.name = name;
		this.color = color;
		this.growthPenalty = foodPenalty;
		this.prodPenalty = prodPenalty;
	}

	public static Happiness get(int h, int pop) {
		if(h>0)
			return Happiness.happy;
		else if(h==0)
			return Happiness.content;
		else if(h>-pop)
			return Happiness.unhappy;
		else if(h>-pop*2 || pop<2)
			return Happiness.angry;
		else
			return Happiness.raging;
	}
	
}
