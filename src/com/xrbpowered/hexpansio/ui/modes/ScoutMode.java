package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class ScoutMode extends MapMode {

	public static final ScoutMode instance = new ScoutMode();
	
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
		return view!=null && view.selectedCity!=null && view.world.discoverThisTurn<view.world.maxDiscover;
	}
	
	@Override
	public String getButtonStatusText() {
		if(view!=null && view.selectedCity!=null)
			return String.format("%d / %d", view.world.maxDiscover - view.world.discoverThisTurn, view.world.maxDiscover);
		else
			return null;
	}
	
	public boolean canDiscover(Tile tile) {
		return tile!=null && tile.discovered && inRange(tile) &&
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
			Res.paintCost(g, YieldResource.gold, null, cost, null, view.world.gold,
					0, -MapView.h*2/3, GraphAssist.CENTER, GraphAssist.CENTER);
		}
	}
	
	@Override
	public String getDescription() {
		return String.format("Spend gold to discover new lands. Cost modifier: %d%%", (int)(view.world.discoverCostMod*100f));
	}
	
	/*@Override
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
		else if(view.world.discoverThisTurn>=view.world.maxDiscover)
			s = "Out of actions until the next turn";
		else if(!inRange(view.selectedCity.tile))
			s = "Out of range";
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
	}*/
	
	@Override
	public boolean showCityRange() {
		return true;
	}

	public boolean inRange(int wx, int wy) {
		return view.world.distToNearestCity(wx, wy)<=cityRange;
	}

	public boolean inRange(Tile tile) {
		return inRange(tile.wx, tile.wy);
	}
	
	@Override
	public boolean isRangeBorder(Tile tile, Dir d) {
		return inRange(tile.wx, tile.wy) ^ inRange(tile.wx+d.dx, tile.wy+d.dy);
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
			if(view.world.discoverThisTurn<view.world.maxDiscover && view.world.gold>=cost) {
				view.world.gold -= cost;
				view.world.discoverThisTurn++;
				view.world.discoverArea(hoverTile.wx, hoverTile.wy, areaRange);
				view.world.updateWorldTotals();
				return true;
			}
		}
		return false;
	}
}
