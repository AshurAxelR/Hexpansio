package com.xrbpowered.hexpansio.ui.modes;

import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.dlg.SetupTradeDialog;
import com.xrbpowered.hexpansio.world.Dir;
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
		return view.world.cities.size()>1;
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
			// TODO show city traders
			return true;
		}
		else if(hoverTile.isCityCenter()) {
			new SetupTradeDialog(view.selectedCity, hoverTile.city);
			return true;
		}
		return false;
	}
	
}
