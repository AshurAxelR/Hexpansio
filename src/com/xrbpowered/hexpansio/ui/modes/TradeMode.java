package com.xrbpowered.hexpansio.ui.modes;

import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.dlg.SetupTradeDialog;
import com.xrbpowered.hexpansio.world.tile.Tile;

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
		return false;
	}

	@Override
	public String getButtonStatusText() {
		return "0 / 0";
	}
	
	@Override
	public int showCityRange() {
		return cityRange;
	}
	
	@Override
	public boolean action() {
		Tile hoverTile = view.hoverTile;
		if(hoverTile.city==view.selectedCity) {
			// TODO show city traders
			return true;
		}
		else if(hoverTile.isCityCenter()) {
			// TODO show city-city trade
			new SetupTradeDialog(view.selectedCity, hoverTile.city);
			return true;
		}
		return false;
	}
	
}
