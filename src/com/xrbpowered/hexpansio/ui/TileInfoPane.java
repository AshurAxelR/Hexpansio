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

		y += 5;
		g.setFont(Res.font);
		float cx = getWidth()*0.4f;
		for(YieldResource res : YieldResource.values()) {
			y += 15;
			g.setColor(res.fill);
			g.drawString(res.name+":", cx-10f, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
			g.drawString(String.format("%+d (%+d / %+d)", tile.yield.get(res),
					tile.terrainYield.get(res), tile.yield.get(res) - tile.terrainYield.get(res)), cx, y);
		}

		if(tile.resource!=null) {
			y += 15;
			tile.resource.paint(g, x, y);
			cx = x+40;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(tile.resource.name, cx, y+12, GraphAssist.LEFT, GraphAssist.BOTTOM);
			g.setFont(Res.font);
			if(tile.improvement!=tile.resource.improvement) {
				g.setColor(Color.RED);
				g.drawString("Requires "+tile.resource.improvement.name, cx, y+18, GraphAssist.LEFT, GraphAssist.TOP);
			}
			else {
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(tile.resource.improvement.name, cx, y+18, GraphAssist.LEFT, GraphAssist.TOP);
			}
			y += 30;
			for(YieldResource res : YieldResource.values()) {
				int yield = tile.resource.yield.get(res);
				if(yield!=0) {
					y += 15;
					g.setColor(tile.improvement!=tile.resource.improvement ? Color.GRAY : res.fill);
					g.drawString(String.format("%+d %s", yield, res.name), cx, y);
				}
			}
		}
		
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
