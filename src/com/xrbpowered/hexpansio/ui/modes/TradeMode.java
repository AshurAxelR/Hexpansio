package com.xrbpowered.hexpansio.ui.modes;

import java.awt.event.KeyEvent;

public class TradeMode extends MapMode {

	public static final TradeMode instance = new TradeMode();
	
	public TradeMode() {
		super("Trade", KeyEvent.VK_A);
	}
	
	@Override
	public boolean isEnabled() {
		return false;
	}

	@Override
	public String getButtonStatusText() {
		return "0 / 0";
	}
	
}
