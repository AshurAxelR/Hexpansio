package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.ui.dlg.CityTradesDialog;
import com.xrbpowered.hexpansio.ui.dlg.SetupTradeDialog;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.resources.Trade;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class TradeMode extends MapMode {

	public static final TradeMode instance = new TradeMode();
	
	public static final int cityRange = ScoutMode.cityRange;

	public TradeMode() {
		super("Trade", KeyEvent.VK_A);
	}
	
	@Override
	public boolean isTileEnabled(Tile tile) {
		return tile!=null && tile.isCityCenter() && view.selectedCity.tile.distTo(tile)<=cityRange;
	}
	
	@Override
	public boolean isEnabled() {
		return view.world.cities.size()>1;
	}
	
	@Override
	public boolean hasOverlayLinks(Tile tile) {
		return (view.getScale()>0.25f && tile.city==view.selectedCity);
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if(view.getScale()>0.25f && isTileEnabled(tile)) {
			Trade trade = view.selectedCity.trades.get(tile.city);
			g.resetStroke();
			if(tile.city==view.selectedCity) {
				for(Trade t : view.selectedCity.trades.getAll()) {
					int profit = t.getProfit();
					g.setColor(profit>0 ? Color.WHITE : profit<0 ? Color.RED : Color.LIGHT_GRAY);
					view.drawLink(g, tile, t.otherCity.tile);
				}
				g.graph.setStroke(MapView.borderStroke);
			}
			g.setColor(Color.WHITE);
			g.pushPureStroke(true);
			g.graph.draw(MapView.tileCircle);
			g.popPureStroke();
			if(trade!=null && view.getScale()>1f) {
				g.setFont(Res.fontTiny);
				Res.paintIncome(g, YieldResource.gold, null, trade.getProfit(), null,
						0, -MapView.h*2/3, GraphAssist.CENTER, GraphAssist.CENTER);
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "Manage trade routes between cities.";
	}
	
	@Override
	public String explainNoAction() {
		if(view.hoverTile==null || !view.hoverTile.isCityCenter())
			return "Select a city to trade with.";
		else if(view.selectedCity.tile.distTo(view.hoverTile)>cityRange)
			return String.format("%s is too far from %s.", view.hoverTile.city.name, view.selectedCity.name);
		return null;
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
		if(hoverTile.city==view.selectedCity) {
			new CityTradesDialog(view.selectedCity);
			return true;
		}
		else if(hoverTile.isCityCenter()) {
			new SetupTradeDialog(view.selectedCity, hoverTile.city);
			return true;
		}
		return false;
	}
	
}
