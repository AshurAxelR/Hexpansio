package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public abstract class MapMode {

	public final String label;
	public final int key;
	
	protected MapView view;
	
	public MapMode(String label, int key) {
		this.label = label;
		this.key = key;
	}
	
	public void init(MapView view) {
		this.view = view;
		if(active==null)
			activate();
	}
	
	public String keyName() {
		return KeyEvent.getKeyText(key);
	}
	
	public void activate() {
		active = this;
	}
	
	public boolean isActive() {
		return active==this;
	}
	
	public boolean isTileEnabled(Tile tile) {
		return false;
	}
	
	public boolean isEnabled() {
		return true;
	}

	public boolean isHighlighted() {
		return false;
	}
	
	public String getButtonStatusText() {
		return null;
	}
	
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
	}

	public int paintHoverTileHint(GraphAssist g, String s, Color color, int x, int y) {
		g.setColor(color);
		g.drawString(s, x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		return y+15;
	}

	public int paintHoverTileHint(GraphAssist g, int x, int y) {
		return y;
	}
	
	public int showCityRange() {
		return 0;
	}
	
	public boolean action() {
		return false;
	}

	public boolean hotkeyAction(int code) {
		return false;
	}

	public static MapMode active = null;
	
	public static final MapMode[] modes = {TileMode.instance, ExpandMode.instance, TradeMode.instance, SettleMode.instance, ScoutMode.instance};
	
	public static boolean checkHotkey(int code) {
		for(int i=0; i<modes.length; i++) {
			if(code==modes[i].key) {
				modes[i].activate();
				return true;
			}
		}
		return active.isTileEnabled(active.view.selectedTile) && active.hotkeyAction(code);
	}
	
	
}
