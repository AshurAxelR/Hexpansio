package com.xrbpowered.hexpansio.ui.dlg;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.world.city.build.BuildingProgress;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;

public class ProductionLossDialog extends ConfirmationDialog {

	private final BuildingProgress current, other;
	
	public ProductionLossDialog(BuildingProgress current, BuildingProgress other) {
		super(150, "LOSS OF PRODUCTION", (String[])null, "SWITCH", "CANCEL");
		this.current = current;
		this.other = other;
	}

	@Override
	protected void paintBoxContents(GraphAssist g) {
		int y = 60;
		g.drawString(String.format("The city is currently building %s.", current.getName()), box.getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		y += 15;
		Res.paintCost(g, YieldResource.production,
				String.format("If %s, ", other==null ? "cancelled" : "switched"), current.progress, " production will be lost.",
				current.progress, box.getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		
		super.paintBoxContents(g);
	}
	
}
