package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class ExpandMode extends MapMode {

	public static final ExpandMode instance = new ExpandMode();
	
	public ExpandMode() {
		super("Expand", KeyEvent.VK_E);
	}

	@Override
	public boolean isTileEnabled(Tile tile) {
		return canExpand(tile) || tile.isCityCenter() && tile.city!=view.selectedCity;
	}
	
	@Override
	public boolean isEnabled() {
		return view.selectedCity.availExpand;
	}
	
	@Override
	public String getButtonStatusText() {
		return String.format("%d / 1", view.selectedCity.availExpand ? 1 : 0);
	}
	
	public boolean canExpand(Tile tile) {
		return tile!=null && tile.discovered && view.world.canAddToCity(tile, view.selectedCity);
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if(view.getScale()>1f && canExpand(tile)) {
			g.setColor(new Color(0x55ffffff, true));
			g.resetStroke();
			g.graph.draw(MapView.smallHexagon);
			
			int cost = view.world.costAddToCity(tile, view.selectedCity);
			g.setFont(Res.fontTiny);
			Res.paintCost(g, YieldResource.gold, null, cost, null, view.world.gold,
					0, -MapView.h*2/3, GraphAssist.CENTER, GraphAssist.CENTER);
		}
	}
	
	@Override
	public String getDescription() {
		return "Spend gold to expand city borders.";
	}
	
	@Override
	public String explainNoAction() {
		if(view.hoverTile==null || !view.hoverTile.discovered)
			return "Undiscovered area";
		else if(view.hoverTile.isCityCenter()) {
			return null;
		}
		else {
			String pre = String.format("Cannot expand %s.\n", view.selectedCity.name);
			if(!view.selectedCity.availExpand)
				return pre+"Cities can only expand once per turn.";
			else if(view.hoverTile.city==view.selectedCity)
				return String.format("%sTile already belongs to %s.", pre, view.selectedCity.name);
			else if(view.hoverTile.countAdjCityTiles(view.selectedCity)==0)
				return pre+"Tile must be adjacent to the city border.";
			else if(view.hoverTile.distTo(view.selectedCity.tile)>City.expandRange)
				return String.format("%sMaximum expansion range is %d tiles.", pre, City.expandRange);
			else {
				int cost = view.world.costAddToCity(view.hoverTile, view.selectedCity);
				if(cost>view.world.gold)
					return String.format("Action requires %d gold.", cost);
			}
		}
		return null;
	}
	
	@Override
	public boolean action() {
		Tile hoverTile = view.hoverTile;
		if(hoverTile.isCityCenter()) {
			view.selectCity(hoverTile.city);
			return true;
		}
		else if(canExpand(hoverTile)) {
			int cost = view.world.costAddToCity(hoverTile, view.selectedCity);
			if(view.selectedCity.availExpand && view.world.gold>=cost) {
				view.world.gold -= cost;
				view.selectedCity.availExpand = false;
				view.world.addToCity(hoverTile.wx, hoverTile.wy, view.selectedCity);
				view.selectedCity.updateStats();
				view.world.updateWorldTotals();
				view.selectedTile = hoverTile;
				TileMode.instance.activate();
				return true;
			}
		}
		return false;
	}
}
