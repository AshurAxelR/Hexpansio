package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.world.BuildingProgress;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Improvement;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class TileInfoPane extends UIContainer {

	public TileInfoPane(UIContainer parent) {
		super(parent);
		setSize(250, 400);
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, Res.uiBgColor);
		g.resetStroke();
		g.vborder(this, GraphAssist.LEFT, Color.WHITE);
		
		Tile tile= ((Hexpansio) getParent()).view.view.selectedTile;
		if(tile==null || !tile.discovered)
			return;

		int x = 10;
		int y = 40;
		
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(tile.terrain.name, x, y);

		y += 20;
		g.setFont(Res.font);
		float cx = getWidth()*0.4f;
		g.setColor(YieldResource.happiness.fill);
		g.drawString("Happiness:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d)", tile.terrain.yield.get(YieldResource.happiness), tile.yield.get(YieldResource.happiness)), cx, y);
		y += 15;
		g.setColor(YieldResource.food.fill);
		g.drawString("Food:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d)", tile.terrain.yield.get(YieldResource.food), tile.yield.get(YieldResource.food)), cx, y);
		y += 15;
		g.setColor(YieldResource.gold.fill);
		g.drawString("Gold:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d)", tile.terrain.yield.get(YieldResource.gold), tile.yield.get(YieldResource.gold)), cx, y);
		y += 15;
		g.setColor(YieldResource.production.fill);
		g.drawString("Production:", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		g.drawString(String.format("%+d (+%d)", tile.terrain.yield.get(YieldResource.production), tile.yield.get(YieldResource.production)), cx, y);

		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);

		y += 25;
		g.setColor(Color.WHITE);
		g.drawString("Improvement:", x, y);
		y += 25;
		g.setFont(Res.fontLarge);
		if(tile.improvement==null) {
			g.setColor(Color.GRAY);
			BuildingProgress bp = tile.city.buildingProgress;
			if(bp!=null && bp.tile==tile) {
				g.drawString(tile.city.buildingProgress.getName(), x, y);
				y += 20;
				g.setColor(Color.WHITE);
				g.setFont(Res.font);
				if(tile.city.getProduction()>0) {
					g.drawString(String.format("(built in %d turns)", (int)Math.ceil((bp.getCost()-bp.progress)/(float)tile.city.getProduction())), x, y);
				}
				else
					g.drawString("(stall)", x, y);
			}
			else {
				g.drawString("None", x, y);
				
				// TODO build ui
				y += 20;
				g.setFont(Res.font);
				g.setColor(Color.WHITE);
				g.drawString("Select to build:", x, y);
				y += 5;
				for(int i=0; i<Improvement.buildMenu.length; i++) {
					y += 15;
					Improvement imp = Improvement.buildMenu[i];
					String s = imp.name;
					Integer key = Improvement.hotkeys[i];
					if(key!=null)
						s += String.format(" (%s)", KeyEvent.getKeyText(key));
					g.setColor(imp.canBuildOn(tile) ? Color.WHITE : Color.GRAY);
					g.setFont(Res.fontBold);
					g.drawString(s, x+10, y);
					g.setFont(Res.font);
					if(imp.canBuildOn(tile) && tile.city.getProduction()>0) {
						g.setColor(Color.GRAY);
						g.drawString(String.format("(%d turns)", (int)Math.ceil(imp.buildCost/(float)tile.city.getProduction())),
								getWidth()-x, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
					}
				}
			}
		}
		else
			g.drawString(tile.improvement.name, x, y);
		
		
		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);
	}
}
