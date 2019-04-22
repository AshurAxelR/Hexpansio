package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class PinDialog extends UIContainer {

	public String title;
	
	public PinDialog(UIContainer parent, float width, float height, String title) {
		super(parent);
		this.title = title;
		setSize(width, height);
	}

	@Override
	protected void paintSelf(GraphAssist g) {
		paintBackground(g);
		paintContents(g);
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		return true;
	}
	
	protected void paintBackground(GraphAssist g) {
		g.fill(this, Res.uiBgColor);
		g.setColor(Color.WHITE);
		if(title!=null) {
			g.setFont(Res.fontLarge);
			g.drawString(title, getWidth()/2f, 30, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}
		g.setFont(Res.font);
	}

	protected void paintContents(GraphAssist g) {
		g.resetStroke();
		g.border(this, Color.WHITE);
	}
}
