package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIHoverElement;

public class CheckBox extends UIHoverElement {

	public static final int defaultHeight = 20;

	protected String label;

	public CheckBox(UIContainer parent, String label) {
		super(parent);
		this.label = label;
		setSize(defaultHeight, defaultHeight);
	}

	public boolean isEnabled() {
		return true;
	}
	
	public boolean isSelected() {
		return true;
	}

	protected void paintBox(GraphAssist g, boolean enabled, boolean selected) {
		if(!enabled)
			g.setColor(Color.BLACK);
		else
			g.setPaint(new GradientPaint(0, 0, Res.uiBgMid, 0, getHeight(), Res.uiBgBright));
		g.fill(this);
		g.resetStroke();
		g.border(this, enabled ? (hover ? Color.WHITE : Res.uiBorderLight) : Res.uiBorderDark);
		if(selected) {
			g.pushAntialiasing(true);
			g.pushPureStroke(true);
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			g.setStroke(3f);
			int h = (int)getHeight();
			int w = (int)getWidth();
			g.graph.drawPolyline(new int[] {3, w/3+1, w-3}, new int[] {h/2+2, h-3, 3}, 3);
			g.resetStroke();
			g.popAntialiasing();
			g.popPureStroke();
		}
	}
	
	protected void paintLabel(GraphAssist g, boolean enabled, String label) {
		g.setColor(enabled ? Color.WHITE : Color.GRAY);
		g.setFont(Res.font);
		g.drawString(label, getWidth()+10f, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
	}

	protected void paintLabel(GraphAssist g, boolean enabled) {
		paintLabel(g, enabled, label);
	}
	
	@Override
	public void paint(GraphAssist g) {
		boolean enabled = isEnabled();
		boolean selected = isSelected();
		paintBox(g, enabled, selected);
		paintLabel(g, enabled);
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
