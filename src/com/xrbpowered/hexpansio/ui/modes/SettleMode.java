package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.city.build.BuiltSettlement;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class SettleMode extends MapMode {

	public static final SettleMode instance = new SettleMode();
	
	public static final int cityRange = ScoutMode.cityRange;
	public static final int minCityDist = 5;

	public SettleMode() {
		super("Settle", KeyEvent.VK_S);
	}

	@Override
	public boolean isTileEnabled(Tile tile) {
		return canSettle(tile) || tile!=null && tile.settlement!=null
				|| tile.isCityCenter() && tile.city!=view.selectedCity;
	}
	
	@Override
	public boolean isEnabled() {
		return view!=null && view.selectedCity!=null && view.selectedCity.population>1 && view.selectedCity.unemployed>0;
	}
	
	public boolean canSettle(Tile tile) {
		return tile!=null && tile.discovered && view.selectedCity.tile.distTo(tile)<=cityRange &&
				view.world.distToNearestCityOrSettler(tile.wx, tile.wy)>=minCityDist && tile.terrain.canSettleOn();
	}

	@Override
	public boolean hasOverlayLinks(Tile tile) {
		return (view.getScale()>0.25f && tile.isCityCenter() && tile.city.isBuildingSettlement());
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if(view.getScale()>0.25f && tile!=null && (tile.isCityCenter() && tile.city.isBuildingSettlement() || tile.settlement!=null)) {
			g.resetStroke();
			g.setColor(Color.WHITE);
			if(tile.isCityCenter()) {
				view.drawLink(g, tile, tile.city.getSettlement());
				g.graph.setStroke(MapView.borderStroke);
			}
			g.pushPureStroke(true);
			g.graph.draw(MapView.tileCircle);
			g.popPureStroke();
		}
	}
	
	@Override
	public int paintHoverTileHint(GraphAssist g, int x, int y) {
		String s;
		Color c = Color.GRAY;
		if(view.hoverTile==null || !view.hoverTile.discovered)
			s = "Undiscovered area";
		else if(view.hoverTile.isCityCenter()) {
			if(view.hoverTile.city==view.selectedCity)
				return y;
			s = "Click to select "+view.hoverTile.city.name;
			c = Color.WHITE;
		}
		else if(view.hoverTile!=null && view.hoverTile.settlement!=null) {
			s = String.format("Click to cancel this settlement (lose %d production)", view.hoverTile.settlement.progress);
			c = Color.YELLOW;
		}
		else if(view.selectedCity.population<=1)
			s = "The city must have at least 2 population";
		else if(view.selectedCity.unemployed==0)
			s = "Requires 1 unemployed worker";
		else if(view.selectedCity.tile.distTo(view.hoverTile)>cityRange)
			s = "Out of city range";
		else if(view.world.distToNearestCityOrSettler(view.hoverTile.wx, view.hoverTile.wy)<minCityDist)
			s = "Too close to other cities";
		else if(!view.hoverTile.terrain.canSettleOn())
			s = "Cannot build a settlement on this terrain";
		else {
			y = paintHoverTileHint(g, "Click to start new settlement", Color.WHITE, x, y);
			if(view.selectedCity.buildingProgress!=null)
				return paintHoverTileHint(g, String.format("Will cancel current construction (lose %d production)", view.selectedCity.buildingProgress.progress), Color.YELLOW, x, y);
			else
				return y;

		}
		return paintHoverTileHint(g, s, c, x, y);
	}
	
	@Override
	public boolean showCityRange() {
		return true;
	}
	
	@Override
	public boolean isRangeBorder(Tile tile, Dir d) {
		return tile.isRangeBorder(d, view.selectedCity.tile, cityRange);
	}
	
	@Override
	public boolean action() {
		Tile hoverTile = view.hoverTile;
		if(hoverTile.isCityCenter()) {
			view.selectCity(hoverTile.city);
			return true;
		}
		else if(view.selectedCity.population>1 && view.selectedCity.unemployed>0 && canSettle(hoverTile)) {
			// TODO warn if settlement destroys resource
			// TODO city-city migration
			TileMode.instance.switchBuildingProgress(new BuiltSettlement(view.selectedCity, hoverTile));
			return true;
		}
		else if(hoverTile!=null && hoverTile.settlement!=null && hoverTile.settlement.city==view.selectedCity) {
			TileMode.instance.switchBuildingProgress(null);
			return true;
		}
		return false;
	}
}
