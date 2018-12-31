package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;

public class HurryDialog extends ConfirmationDialog {

	private final int cost;
	
	public HurryDialog(int cost) {
		super(150, "HURRY BUILDING", (String[])null, "HURRY", "CANCEL");
		this.cost = cost;
	}

	@Override
	protected void paintBoxContents(GraphAssist g) {
		int y = 60;
		Res.paintCost(g, YieldResource.production, "Spend ", cost, " to hurry building progress?", cost, box.getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		y += 15;
		g.setColor(Color.WHITE);
		g.drawString("It will be complete next turn.", box.getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		
		super.paintBoxContents(g);
	}
	
}
