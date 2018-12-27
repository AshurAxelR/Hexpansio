package com.xrbpowered.hexpansio.world.resources;

import java.awt.Color;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.utils.ColorUtils;
import com.xrbpowered.zoomui.GraphAssist;

public enum YieldResource {

	happiness("Happiness", new Color(0x33ccdd), new Color(0x115566)),
	food("Food", new Color(0x99dd55), new Color(0x446622)),
	gold("Gold", new Color(0xddcc33), new Color(0x665511)),
	production("Production", new Color(0xcb965d), new Color(0x554422));
	
	public final String name;
	public final Color fill;
	public final Color border;
	public final Color dark;
	
	private YieldResource(String name, Color fill, Color border) {
		this.name = name;
		this.fill = fill;
		this.border = border;
		this.dark = ColorUtils.blend(border, fill, 0.33);
	}
	
	private void paintYieldItem(GraphAssist g, int x, int y) {
		g.setColor(fill);
		g.graph.fillOval(x-3, y-3, 6, 6);
		g.setColor(border);
		g.graph.drawOval(x-3, y-3, 6, 6);
	}
	
	public void paintYield(GraphAssist g, int x, int y, int count) {
		if(count<=0)
			return;
		switch(count) {
			case 1:
				paintYieldItem(g, x, y);
				return;
			case 2:
				paintYieldItem(g, x, y-3);
				paintYieldItem(g, x, y+3);
				return;
			case 3:
				paintYieldItem(g, x, y-3);
				paintYieldItem(g, x-3, y+3);
				paintYieldItem(g, x+3, y+3);
				return;
			case 4:
				paintYieldItem(g, x-3, y-3);
				paintYieldItem(g, x-3, y+3);
				paintYieldItem(g, x+3, y-3);
				paintYieldItem(g, x+3, y+3);
				return;
			default:
				g.setColor(fill);
				g.graph.fillOval(x-7, y-7, 14, 14);
				g.setColor(border);
				g.graph.drawOval(x-7, y-7, 14, 14);
				g.drawString(Integer.toString(count), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
		}
	}
	
	public static void preparePaint(GraphAssist g) {
		g.resetStroke();
		g.setFont(Res.fontTiny);
	}
	
}
