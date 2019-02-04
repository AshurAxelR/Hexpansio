package com.xrbpowered.hexpansio.ui.dlg.stats;

import java.awt.Color;

import com.xrbpowered.hexpansio.world.TurnStatistics;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.zoomui.GraphAssist;

public abstract class Diagram {

	public final String title;
	public final String[] plotNames;
	public final Color[] plotColors;
	protected final String format;
	
	public Diagram(String title, String[] plotNames, Color[] plotColors, String format) {
		this.title = title;
		this.plotNames = plotNames;
		this.plotColors = plotColors;
		this.format = format;
	}

	public Diagram(String title, String[] plotNames, Color[] plotColors) {
		this(title, plotNames, plotColors, "%d");
	}

	public abstract int getValue(TurnStatistics s, int plot);

	protected String getFormat(int plot) {
		return format;
	}
	
	public int paintLegend(GraphAssist g, int x, int y, TurnStatistics current, World world) {
		for(int i=0; i<plotNames.length; i++) {
			g.setColor(plotColors[i]);
			g.fillRect(x, y-15, 15, 15);
			y += 15;
			g.drawString(String.format("%s: "+getFormat(i), plotNames[i], getValue(current, i)), x, y);
			y += 25;
		}
		return y;
	}

	public static final Diagram cities = new Diagram("Cities",
			new String[] {"Cities", "Baseline happiness"},
			new Color[] {Color.WHITE, Happiness.angry.color}) {
		@Override
		protected String getFormat(int plot) {
			return plot==0 ? "%d" : "%+d";
		}
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 1: return s.baseHapiness;
				default: return s.numCities;
			}
		}
	};

	public static final Diagram populationDensity = new Diagram("Population density",
			new String[] {"Population", "Territory", "Workplaces"},
			new Color[] {Color.WHITE, TerrainType.fertilePlains.color, Color.GRAY}) {
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 2: return s.workplaces;
				case 1: return s.territory;
				default: return s.population;
			}
		}
	};

	public static final Diagram happiness = new Diagram("Happiness",
			new String[] {"Max happiness", "Min happiness", "Average happiness"},
			new Color[] {new Color(0x88ddbb), Happiness.angry.color, Happiness.unhappy.color}, "%+d") {
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 2: return s.aveHappy;
				case 1: return s.minHappy;
				default: return s.maxHappy;
			}
		}
	};

	public static final Diagram goldAndGoods = new Diagram("Gold and goods",
			new String[] {"Gold", "Goods"},
			new Color[] {YieldResource.gold.fill, YieldResource.production.dark}) {
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 1: return s.goods;
				default: return s.gold;
			}
		}
	};

	public static final Diagram yield = new Diagram("Income",
			new String[] {YieldResource.happiness.name, YieldResource.food.name, YieldResource.gold.name, YieldResource.production.name},
			new Color[] {YieldResource.happiness.fill, YieldResource.food.fill, YieldResource.gold.fill, YieldResource.production.fill}, "%+d") {
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 3: return s.yield.get(YieldResource.production);
				case 2: return s.yield.get(YieldResource.gold);
				case 1: return s.yield.get(YieldResource.food);
				default: return s.yield.get(YieldResource.happiness);
			}
		}
	};

	public static final Diagram resources = new Diagram("Resources",
			new String[] {"Resources produced", "Resources traded"},
			new Color[] {Color.WHITE, YieldResource.gold.fill}) {
		@Override
		protected String getFormat(int plot) {
			return plot==0 ? "%+d" : "%d";
		}
		@Override
		public int getValue(TurnStatistics s, int plot) {
			switch(plot) {
				case 1: return s.resTraded;
				default: return s.resProduced;
			}
		}
	};
	
	public static final Diagram voidStorms = new Diagram("The Void",
			new String[] {"Void storms"},
			new Color[] {TerrainType.Feature.thevoid.color}) {
		@Override
		public int getValue(TurnStatistics s, int plot) {
			return s.voidStorms;
		}
		@Override
		public int paintLegend(GraphAssist g, int x, int y, TurnStatistics current, World world) {
			if(!world.settings.voidEnabled) {
				g.setColor(Color.WHITE);
				g.drawString("The Void is not enabled", x, y);
				y += 25;
			}
			else if(!world.hasVoid()) {
				g.setColor(Color.WHITE);
				g.drawString(String.format("The void starts on turn %d", world.settings.voidStartTurn), x, y);
				y += 25;
			}
			else {
				y = super.paintLegend(g, x, y, current, world);
				g.setColor(Color.LIGHT_GRAY);
				y += 15;
				g.drawString(String.format("Started on turn %d", world.settings.voidStartTurn), x, y);
				y += 15;
				g.drawString(String.format("Void sources: %d", world.settings.voidStartSources), x, y);
				y += 15;
				g.drawString(String.format("Distance: %d-%d", world.settings.voidMinDistance, world.settings.voidMaxDistance), x, y);
				y += 15;
				g.drawString(String.format("Spread speed: x%d", world.settings.voidSpreadSpeed), x, y);
			}
			return y;
		}
	};
}
