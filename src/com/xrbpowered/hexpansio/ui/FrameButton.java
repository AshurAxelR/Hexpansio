package com.xrbpowered.hexpansio.ui;

import java.awt.Cursor;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIHoverElement;

public class FrameButton extends UIHoverElement {

	private float insetX, insetY;
	
	public FrameButton(UIContainer parent, float insetX, float insetY) {
		super(parent);
		this.insetX = insetX;
		this.insetY = insetY;
	}

	@Override
	public void paint(GraphAssist g) {
		if(hover) {
			g.resetStroke();
			g.drawRect(insetX, insetY, getWidth()-insetX*2f-0.5f, getHeight()-insetY*2-0.5f, Res.uiBgBright);
		}
	}
	
	@Override
	public void onMouseIn() {
		getBase().getWindow().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
		super.onMouseIn();
	}
	
	@Override
	public void onMouseOut() {
		getBase().getWindow().setCursor(Cursor.getDefaultCursor());
		super.onMouseOut();
	}
	
	public void onClick() {
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		if(button==Button.left && mods==UIElement.modNone) {
			onClick();
			return true;
		}
		else
			return false;
	}
}
