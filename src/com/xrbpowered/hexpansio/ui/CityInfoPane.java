package com.xrbpowered.hexpansio.ui;

import java.awt.Color;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.ResourceInfoDialog;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.city.build.BuildingProgress;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class CityInfoPane extends UIContainer {

	private static final int margin = 10;
	
	private final ClickButton hurryButton;
	private final ClickButton cancelButton;
	
	private final FrameButton resFrame;
	
	public CityInfoPane(Hexpansio parent) {
		super(parent);
		setSize(250, 400);
		
		hurryButton = new ClickButton(this, "Hurry", 140) {
			@Override
			public boolean isEnabled() {
				City city = getMapView().selectedCity;
				if(city.buildingProgress!=null && city.buildingProgress.canHurry()) {
					int cost = city.buildingProgress.getCost() - city.buildingProgress.progress;
					if(cost >0 && city.world.goods>=cost) {
						return true;
					}
				}
				return false;
			}
			@Override
			public void onClick() {
				TileMode.instance.hurryBuilding();
				repaint();
			}
		};
		hurryButton.setLocation(getWidth()-hurryButton.getWidth()-margin, 0);
		hurryButton.setVisible(false);
		
		cancelButton = new ClickButton(this, "Cancel", (int)(getWidth()-hurryButton.getWidth()-margin*2-5)) {
			@Override
			public void onClick() {
				TileMode.instance.cancelBuilding();
				repaint();
			}
		};
		cancelButton.setLocation(margin, 0);
		cancelButton.setVisible(false);
		
		resFrame = new FrameButton(this, 3, 3) {
			@Override
			public void onClick() {
				new ResourceInfoDialog(getMapView().selectedCity).repaint();
			}
		};
		resFrame.setLocation(0, 0);
	}
	
	public MapView getMapView() {
		return ((Hexpansio) getParent()).view.view;
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, Res.uiBgColor);
		
		City city = getMapView().selectedCity;
		if(city==null)
			return;

		int x = margin;
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
			g.drawString(Res.calcTurnsStr(city.growth, city.getTargetGrowth(), growth, null),
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
		g.drawString(String.format("%s (%+d)", city.happiness.name.toUpperCase(), city.balance.get(YieldResource.happiness)), x, y);
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
		y += 20; g.drawString(String.format("%+d base happiness", city.world.baseHappiness), x, y);
		y += 15; g.drawString(String.format("%+d from tiles and resources", city.incomeTiles.get(YieldResource.happiness)+city.incomeResources.get(YieldResource.happiness)), x, y);
		g.setColor(Color.GRAY);
		if(city.population>1) {
			y += 15; g.drawString(String.format("%d from population", -(city.population-1)), x, y);
		}
		if(city.world.cities.size()>1) {
			y += 15; g.drawString(String.format("%d from number of cities", -city.world.cities.size()+1), x, y);
		}
		g.setColor(Color.RED);
		if(city.unemployed>0) {
			y += 15; g.drawString(String.format("%d from unemployment", -(city.unemployed*city.unemployed)), x, y);
		}
		if(city.world.poverty>0) {
			y += 15; g.drawString(String.format("%d from poverty", -city.world.poverty), x, y);
		}
		if(city.balance.get(YieldResource.food)<0) {
			y += 15; g.drawString(String.format("%d from starvation", -city.population), x, y);
		}

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 10;
		if(city.unemployed>0) {
			y += 35;
			g.setStroke(1.25f);
			Res.paintWorkerBubbles(g, x, y-25, 15, city.unemployed, city.unemployed, false, GraphAssist.LEFT);
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

		resFrame.setLocation(0, y);
		y += 25;
		float cx = getWidth()*0.4f;
		g.setColor(YieldResource.food.fill);
		g.drawString("Food:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d / -%d)", city.balance.get(YieldResource.food),
				city.incomeTiles.get(YieldResource.food)+city.incomeResources.get(YieldResource.food),
				city.expences.get(YieldResource.food)), cx, y);
		y += 15;
		g.setColor(YieldResource.gold.fill);
		g.drawString("Gold:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d / -%d)", city.balance.get(YieldResource.gold),
				city.incomeTiles.get(YieldResource.food)+city.incomeResources.get(YieldResource.gold),
				city.expences.get(YieldResource.gold)), cx, y);
		y += 15;
		g.setColor(YieldResource.production.fill);
		g.drawString("Production:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		if(city.happiness.prodPenalty>0)
			g.drawString(String.format("%+d (%+d -%d%%)", city.getProduction(), city.balance.get(YieldResource.production), city.happiness.prodPenalty), cx, y);
		else
			g.drawString(String.format("%+d", city.balance.get(YieldResource.production)), cx, y);
		
		if(!city.resourcesAvail.isEmpty()) {
			y += 15;
			city.resourcesAvail.paint(g, x, y, "%d");
			y += 40;
		}

		y += 15;
		g.setColor(Color.WHITE);
		g.drawString("Details...", getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);

		y += 15;
		resFrame.setSize(getWidth(), y-resFrame.getY());
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
			hurryButton.setVisible(false);
			cancelButton.setVisible(false);
		}
		else {
			BuildingProgress bp = city.buildingProgress;
			g.drawString(bp.getName(), x, y);
			g.setFont(Res.font);
			y += 20;
			g.setColor(YieldResource.production.fill);
			g.drawString(String.format("%d/%d", bp.progress, bp.getCost()), x, y);
			if(city.getProduction()>0) {
				g.drawString(Res.calcTurnsStr(bp.progress, bp.getCost(), city.getProduction(), null),
						getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
			}
			
			Res.paintProgress(g, YieldResource.production, bp.progress, bp.getCost(), city.getProduction(),
					x, y+5, getWidth()-x*2, 6, GraphAssist.LEFT);
			
			y += 25;
			hurryButton.setLocation(hurryButton.getX(), y);
			hurryButton.setVisible(true);
			cancelButton.setLocation(cancelButton.getX(), y);
			cancelButton.setVisible(true);
			y += hurryButton.getHeight();
			
			y += 15;
			g.setColor(Color.LIGHT_GRAY);
			if(bp.canHurry())
				Res.paintCost(g, YieldResource.production, "Hurry cost: ", bp.getCost()-bp.progress, " goods", city.world.goods, getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
			else
				g.drawString("Cannot hurry", getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		}

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		g.resetStroke();
		g.vborder(this, GraphAssist.RIGHT, Color.WHITE);
	}

}
