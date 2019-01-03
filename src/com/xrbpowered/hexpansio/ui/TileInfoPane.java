package com.xrbpowered.hexpansio.ui;

import java.awt.Color;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.BuildDialog;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.BuildingProgress;
import com.xrbpowered.hexpansio.world.tile.improv.RemoveImprovement;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class TileInfoPane extends UIContainer {

	private static final int margin = 10;

	private final FrameButton buildButton;
	private final FrameButton upgButton;
	private final FrameButton removeButton;

	public TileInfoPane(UIContainer parent) {
		super(parent);
		setSize(250, 400);
		
		buildButton = new FrameButton(this, "Build", (int)(getWidth()-margin*2)) {
			@Override
			public void onClick() {
				new BuildDialog(getMapView().selectedTile);
				repaint();
			}
		};
		buildButton.setLocation(margin, 0);
		buildButton.setVisible(false);
		
		upgButton = new FrameButton(this, "Upgrade", 140) {
			@Override
			public boolean isEnabled() {
				return false;
			}
		};
		upgButton.setLocation(margin, 0);
		upgButton.setVisible(false);
		
		removeButton = new FrameButton(this, "Remove", (int)(getWidth()-upgButton.getWidth()-margin*2-5)) {
			@Override
			public boolean isEnabled() {
				Tile tile= getMapView().selectedTile;
				return tile.improvement!=null && !tile.improvement.isPermanent();
			}
			@Override
			public void onClick() {
				TileMode.instance.removeBuilding();
				repaint();
			}
		};
		removeButton.setLocation(getWidth()-removeButton.getWidth()-margin, 0);
		removeButton.setVisible(false);
	}
	
	public MapView getMapView() {
		return ((Hexpansio) getParent()).view.view;
	}
	
	@Override
	protected void paintSelf(GraphAssist g) {
		g.fill(this, Res.uiBgColor);
		g.resetStroke();
		g.vborder(this, GraphAssist.LEFT, Color.WHITE);
		
		Tile tile= getMapView().selectedTile;
		if(tile==null || !tile.discovered)
			return;

		int x = margin;
		int y = 30;
		
		g.setColor(Color.WHITE);
		g.setFont(Res.fontBold);
		g.drawString(tile.terrain.name, getWidth()/2, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		g.setFont(Res.font);
		if(tile.terrain.feature!=null) {
			y += 15;
			g.setColor(Color.LIGHT_GRAY);
			g.drawString(tile.terrain.feature.name(), getWidth()/2, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}

		y += 10;
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
			g.resetStroke();
			g.line(0, y, getWidth(), y, Res.uiBorderDark);
			
			y += 15;
			g.setColor(Color.WHITE);
			tile.resource.paint(g, x+5, y, tile.hasResourceImprovement() ? "+1" : null);
			cx = x+45;
			y += 10;
			g.setFont(Res.fontBold);
			g.drawString(tile.resource.name, cx, y);
			g.setFont(Res.font);
			y += 15;
			if(tile.hasResourceImprovement()) {
				g.setColor(Color.LIGHT_GRAY);
				g.drawString(tile.resource.improvement.name, cx, y);
			}
			else {
				g.setColor(Color.RED);
				g.drawString("Requires "+tile.resource.improvement.name, cx, y);
			}
			for(YieldResource res : YieldResource.values()) {
				int yield = tile.resource.yield.get(res);
				if(yield!=0) {
					y += 15;
					g.setColor(tile.hasResourceImprovement() ? res.fill : Color.GRAY);
					g.drawString(String.format("%+d %s", yield, res.name), cx, y);
				}
			}
		}
		
		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);
		
		if(tile.city==null) {
			buildButton.setVisible(false);
			upgButton.setVisible(false);
			removeButton.setVisible(false);
			return;
		}

		y += 25;
		BuildingProgress bp = tile.city.buildingProgress==null || tile.city.buildingProgress.tile!=tile ? null : tile.city.buildingProgress;
		if(tile.improvement==null && bp==null) {
			g.setColor(Color.LIGHT_GRAY);
			g.drawString("No improvement", x, y);
			
			y += 15;
			buildButton.setLocation(buildButton.getX(), y);
			buildButton.setVisible(true);
			upgButton.setVisible(false);
			removeButton.setVisible(false);
			y += buildButton.getHeight();
		}
		else if(tile.improvement==null) {
			y += 5;
			g.setColor(Color.GRAY);
			g.setFont(Res.fontLarge);
			g.drawString(tile.city.buildingProgress.getName(), x, y);
			g.setFont(Res.font);
			g.setColor(Color.LIGHT_GRAY);
			y += 20;
			g.drawString("Under construction "+Res.calcTurnsStr(bp.progress, bp.getCost(), tile.city.getProduction(), "(stall)"), x, y);
			
			buildButton.setVisible(false);
			upgButton.setVisible(false);
			removeButton.setVisible(false);
		}
		else if(bp!=null && bp instanceof RemoveImprovement) {
			y += 5;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontLarge);
			g.drawString(tile.improvement.base.name, x, y);
			g.setFont(Res.font);
			g.setColor(Color.RED);
			y += 20;
			g.drawString("(Removing)", x, y);
			
			buildButton.setVisible(false);
			upgButton.setVisible(false);
			removeButton.setVisible(false);
		}
		else {
			y += 5;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontLarge);
			g.drawString(tile.improvement.base.name, x, y);
			g.setFont(Res.font);
			
			y += 20;
			g.setColor(Color.GRAY);
			g.drawString("No upgrades", x, y);
			y += 20;
			g.setColor(Color.WHITE);
			g.drawString("Upg. points: 0 / 0", x, y);
			
			y += 15;
			upgButton.setLocation(upgButton.getX(), y);
			removeButton.setLocation(removeButton.getX(), y);
			buildButton.setVisible(false);
			upgButton.setVisible(true);
			removeButton.setVisible(true);
			y += upgButton.getHeight();
		}		
		
		y += 15;
		g.resetStroke();
		g.line(0, y, getWidth(), y, Res.uiBorderDark);
	}
}
