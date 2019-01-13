package com.xrbpowered.hexpansio.ui;

import java.awt.Color;

import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIScrollBar;

public class ArrowButton extends ClickButton {

	public final int delta;

	public ArrowButton(UIContainer parent, int delta, int w, int h, int frame) {
		super(parent, null, w, h, null);
		setFrameSize(frame, frame);
		this.delta = delta;
	}

	protected void paintLabel(GraphAssist g, boolean enabled, String label) {
		g.setColor(enabled ? Color.WHITE : Color.GRAY);
		if(delta>0)
			UIScrollBar.drawRightArrow(g, (int)getWidth()/2, (int)getHeight()/2, 6);
		else
			UIScrollBar.drawLeftArrow(g, (int)getWidth()/2, (int)getHeight()/2, 6);
	}
	
}
