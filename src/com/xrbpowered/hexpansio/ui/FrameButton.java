package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIHoverElement;

public class FrameButton extends UIHoverElement {

	public static final int defaultHeight = 25;
	
	protected Font font;
	protected int frameWidth, frameHeight;
	protected String label;
	
	public FrameButton(UIContainer parent, String label, int w, int h, Font font) {
		super(parent);
		this.font = font;
		this.label = label;
		setSize(w, h);
		setFrameSize(w, h);
	}

	public FrameButton(UIContainer parent, String label, int w) {
		this(parent, label, w, defaultHeight, Res.font);
	}

	public FrameButton setFrameSize(int w, int h) {
		this.frameWidth = w;
		this.frameHeight = h;
		return this;
	}

	public boolean isModeActive() {
		return false;
	}
	
	public boolean isEnabled() {
		return true;
	}
	
	public boolean isHot() {
		return false;
	}
	
	protected void paintFrame(GraphAssist g, boolean enabled, boolean hot) {
		g.setColor(Color.WHITE);
		if(isModeActive()) {
			g.setStroke(6f);
			g.graph.drawRoundRect((int)getWidth()/2-frameWidth/2, (int)getHeight()/2-frameHeight/2, frameWidth, frameHeight, 5, 5);
		}
		if(!enabled)
			g.setColor(Color.BLACK);
		else if(hot)
			g.setPaint(new GradientPaint(0, getHeight()/2f-frameHeight/2f, new Color(0xffbb33), 0, getHeight()/2f+frameHeight/2f, new Color(0x996600)));
		else
			g.setPaint(new GradientPaint(0, getHeight()/2f-frameHeight/2f, Res.uiButtonTop, 0, getHeight()/2f+frameHeight/2f, Res.uiBgBright));
		g.graph.fillRoundRect((int)getWidth()/2-frameWidth/2, (int)getHeight()/2-frameHeight/2, frameWidth, frameHeight, 5, 5);
		g.setColor(hover ? Color.WHITE : hot ? new Color(0xffdd99) : Res.uiBorderLight);
		g.resetStroke();
		g.graph.drawRoundRect((int)getWidth()/2-frameWidth/2, (int)getHeight()/2-frameHeight/2, frameWidth, frameHeight, 5, 5);
	}
	
	protected void paintLabel(GraphAssist g, boolean enabled, String label) {
		g.setColor(enabled ? Color.WHITE : Color.GRAY);
		g.setFont(font);
		g.drawString(label, getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
	}

	protected void paintLabel(GraphAssist g, boolean enabled) {
		paintLabel(g, enabled, label);
	}

	@Override
	public void paint(GraphAssist g) {
		g.pushAntialiasing(true);

		boolean enabled = isEnabled();
		boolean hot = isHot();
		paintFrame(g, enabled, hot);
		paintLabel(g, enabled);
		
		g.popAntialiasing();
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
