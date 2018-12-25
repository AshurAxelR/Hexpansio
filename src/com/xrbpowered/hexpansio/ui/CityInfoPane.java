package com.xrbpowered.hexpansio.ui;

import java.awt.Color;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.world.BuildingProgress;
import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class CityInfoPane extends UIContainer {

	public CityInfoPane(Hexpansio parent) {
		super(parent);
		setSize(250, 400);
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, Res.uiBgColor);
		
		City city = ((Hexpansio) getParent()).view.view.selectedCity;
		if(city==null)
			return;

		int x = 10;
		int y = 40;
		
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("Population: %d", city.population), x, y);

		y += 20;
		g.setFont(Res.font);
		int growth = city.getFoodGrowth();
		if(growth>0) {
			g.setColor(YieldResource.food.fill);
			g.drawString(String.format("Growth: %d/%d", city.growth, city.getTargetGrowth()), x, y);
			g.drawString(String.format("(%d turns)", (int)Math.ceil((city.getTargetGrowth()-city.growth)/(float)growth)),
					getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		}
		else if(growth==0) {
			g.setColor(Color.YELLOW);
			g.drawString("Stagnation", x, y);
		}
		else {
			g.setColor(Color.RED);
			g.drawString("Starvation", x, y);
		}

		Res.paintProgress(g, YieldResource.food, city.growth, city.getTargetGrowth(), growth,
				x, y+5, getWidth()-x*2, 6, GraphAssist.LEFT);

		y += 25;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 25;
		g.setColor(city.happiness.color);
		g.drawString(String.format("%s (%+d)", city.happiness.name.toUpperCase(), city.happyIn-city.happyOut), x, y);
		g.setFont(Res.font);
		if(city.happiness.growthPenalty>0) {
			y += 15;
			g.setColor(Color.GRAY);
			g.drawString(String.format("-%d%% growth, -%d%% production", city.happiness.growthPenalty, city.happiness.prodPenalty), x, y);
		}
		if(city.happiness==Happiness.raging) {
			y += 15;
			g.setColor(Color.RED);
			g.drawString("1 population will leave", x, y);
		}
		g.setColor(Color.WHITE);
		y += 20; g.drawString("+3 base happiness", x, y);
		y += 15; g.drawString(String.format("%+d from tiles", city.happyIn-3), x, y);
		g.setColor(Color.GRAY);
		if(city.population>1) {
			y += 15; g.drawString(String.format("%d from population", -(city.population-1)), x, y);
		}
		if(city.world.cities.size()>=4) {
			y += 15; g.drawString(String.format("%d from number of cities", -city.world.cities.size()/4), x, y);
		}
		g.setColor(Color.RED);
		if(city.unemployed>0) {
			y += 15; g.drawString(String.format("%d from unemployment", -(city.unemployed*city.unemployed)), x, y);
		}
		if(city.world.poverty>0) {
			y += 15; g.drawString(String.format("%d from poverty", -city.world.poverty), x, y);
		}
		if(city.foodIn<city.foodOut) {
			y += 15; g.drawString(String.format("%d from starvation", -city.population), x, y);
		}
		/*y += 15;
		g.setColor(Color.WHITE);
		g.drawString("Details...", getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);*/

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 40;
		if(city.unemployed>0) {
			g.pushPureStroke(true);
			g.setStroke(1.25f);
			Color fill = new Color(0xdd0000);
			Color border = new Color(0xeeaaaa);
			for(int i=0; i<city.unemployed; i++) {
				g.setColor(fill);
				g.graph.fillOval(i*10+10, y-30, 15, 15);
				g.setColor(border);
				g.graph.drawOval(i*10+10, y-30, 15, 15);
			}
			g.popPureStroke();
			g.setColor(Color.RED);
			g.drawString(String.format("Unemployed: %d", city.unemployed), x, y);
			g.setColor(Color.WHITE);
		}
		y += 15;
		g.setColor(city.workplaces<=city.population ? Color.YELLOW : Color.WHITE);
		g.drawString(String.format("Workplaces: %d", city.workplaces), x, y);

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 25;
		float cx = getWidth()*0.4f;
		g.setColor(YieldResource.food.fill);
		g.drawString("Food:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d / -%d)", city.foodIn-city.foodOut, city.foodIn, city.foodOut), cx, y);
		y += 15;
		g.setColor(YieldResource.gold.fill);
		g.drawString("Gold:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d / -%d)", city.goldIn-city.goldOut, city.goldIn, city.goldOut), cx, y);
		y += 15;
		g.setColor(YieldResource.production.fill);
		g.drawString("Production:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		if(city.happiness.prodPenalty>0)
			g.drawString(String.format("%+d (%+d -%d%%)", city.getProduction(), city.prodIn, city.happiness.prodPenalty), cx, y);
		else
			g.drawString(String.format("%+d", city.prodIn), cx, y);

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 25;
		g.setColor(Color.WHITE);
		g.drawString("Currently building:", x, y);
		y += 25;
		g.setFont(Res.fontLarge);
		if(city.buildingProgress==null) {
			int goods = city.getExcess(city.getProduction());
			g.setColor(goods>0 ? YieldResource.production.fill : Color.RED);
			g.drawString(String.format("%+d Goods", goods), x, y);
			g.setFont(Res.font);
		}
		else {
			BuildingProgress bp = city.buildingProgress;
			g.drawString(bp.getName(), x, y);
			g.setFont(Res.font);
			y += 20;
			g.setColor(YieldResource.production.fill);
			g.drawString(String.format("%d/%d", bp.progress, bp.getCost()), x, y);
			if(city.getProduction()>0) {
				g.drawString(String.format("(%d turns)", (int)Math.ceil((bp.getCost()-bp.progress)/(float)city.getProduction())),
						getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
			}
			
			Res.paintProgress(g, YieldResource.production, bp.progress, bp.getCost(), city.getProduction(),
					x, y+5, getWidth()-x*2, 6, GraphAssist.LEFT);
			
			y += 30;
			g.setColor(Color.WHITE);
			Res.paintCost(g, YieldResource.production, "(Ins) Hurry: ", bp.getCost()-bp.progress, null, city.world, x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
		}

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		g.resetStroke();
		g.vborder(this, GraphAssist.RIGHT, Color.WHITE);
	}

}
