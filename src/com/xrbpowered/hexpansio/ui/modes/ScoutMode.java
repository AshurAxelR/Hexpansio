package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class ScoutMode extends MapMode {

	public static final int areaRange = 2;
	public static final int cityRange = 10;
	
	public ScoutMode() {
		super("Scout", KeyEvent.VK_D);
	}

	@Override
	public boolean isTileEnabled(Tile tile) {
		return canDiscover(tile) || tile.isCityCenter() && tile.city!=view.selectedCity;
	}
	
	@Override
	public boolean isEnabled() {
		return view!=null && view.selectedCity!=null && view.selectedCity.availDiscover>0;
	}
	
	@Override
	public String getButtonStatusText() {
		if(view!=null && view.selectedCity!=null)
			return String.format("%d / 1", view.selectedCity.availDiscover);
		else
			return null;
	}
	
	public boolean canDiscover(Tile tile) {
		return tile!=null && tile.discovered && view.selectedCity.tile.distTo(tile)<=cityRange &&
				!tile.isAreaDiscovered();
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if((tile==null || !tile.discovered) && canDiscover(view.hoverTile) && view.hoverTile.distTo(wx, wy)<=areaRange) {
			g.setColor(new Color(0xeee0dd));
			g.graph.fill(MapView.hexagon);
		}
		else if(view.getScale()>1f && canDiscover(tile)) {
			int cost = view.world.costDiscover(tile);
			g.setFont(Res.fontTiny);
			Res.paintCost(g, YieldResource.gold, null, cost, null, view.world,
					0, -MapView.h*2/3, GraphAssist.CENTER, GraphAssist.CENTER);
		}
	}
	
	@Override
	public int paintHoverTileHint(GraphAssist g, int x, int y) {
		String s;
		Color c = Color.GRAY;
		if(view.hoverTile==null || !view.hoverTile.discovered)
			s = "Scout must be sent to a discovered tile";
		else if(view.hoverTile.isCityCenter()) {
			if(view.hoverTile.city==view.selectedCity)
				return y;
			s = "Click to select "+view.hoverTile.city.name;
			c = Color.WHITE;
		}
		else if(view.selectedCity.availDiscover==0)
			s = "Out of actions until the next turn";
		else if(view.hoverTile.distTo(view.selectedCity.tile)>cityRange)
			s = "Out of city range";
		else if(view.hoverTile.isAreaDiscovered())
			s = "Area already discovered";
		else {
			int cost = view.world.costDiscover(view.hoverTile);
			if(cost>view.world.gold) {
				s = String.format("Action requires %d gold", cost);
				c = Color.RED;
			}
			else {
				 s = String.format("Click to discover area for %d gold", cost);
				 c = Color.WHITE;
			}
		}
		return paintHoverTileHint(g, s, c, x, y);
	}
	
	@Override
	public boolean action() {
		Tile hoverTile = view.hoverTile;
		if(hoverTile.isCityCenter()) {
			view.selectCity(hoverTile.city);
			return true;
		}
		else if(canDiscover(hoverTile)) {
			int cost = view.world.costDiscover(hoverTile);
			if(view.selectedCity.availDiscover>0 && view.world.gold>=cost) {
				view.world.gold -= cost;
				view.selectedCity.availDiscover--;
				view.world.discoverArea(hoverTile.wx, hoverTile.wy, areaRange);
				return true;
			}
		}
		return false;
	}
}
