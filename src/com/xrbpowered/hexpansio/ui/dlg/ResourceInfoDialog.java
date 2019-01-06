package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class ResourceInfoDialog extends OverlayDialog {

	public final City city;

	private class ResourceListItem extends UIListItem {
		public ResourceListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 40);
		}
		@Override
		public void paint(GraphAssist g) {
			ResourcePile.Entry e = (ResourcePile.Entry) object;
			int produced = city.resourcesProduced.count(e.resource);
			boolean enabled = produced>0;
			if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			
			int x = 10;
			int y = (int)(getHeight()/2f);
			e.resource.paint(g, x, y-15, null);
			
			x += 40;
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			g.setFont(Res.fontBold);
			g.drawString(e.resource.name, 50, y, GraphAssist.LEFT, GraphAssist.CENTER);
			
			x += 180;
			g.setFont(Res.font);
			g.drawString(String.format("%+d / %d", produced, e.count), x, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			x += 80;
			g.setColor(Color.GRAY);
			g.drawString("-", x, y, GraphAssist.CENTER, GraphAssist.CENTER);
			x += 80;
			g.drawString("-", x, y, GraphAssist.CENTER, GraphAssist.CENTER);

			x += 120;
			g.setFont(Res.fontLarge);
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			g.drawString(produced==0 ? "-" : Integer.toString(produced), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
			g.setFont(Res.font);

			x += 40;
			for(YieldResource res : YieldResource.values()) {
				x += 80;
				int bonus = produced * city.effects.addResourceBonusYield(e.resource, res);
				int base = produced * e.resource.yield.get(res);
				if(base+bonus!=0) {
					g.setColor(enabled ? res.fill : Color.GRAY);
					if(bonus!=0)
						g.drawString(String.format("%+d (%+d)", base, base+bonus), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
					else
						g.drawString(String.format("%+d", base), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
				}
				else {
					g.setColor(Color.GRAY);
					base = e.resource.yield.get(res);
					g.drawString(base==0 ? "-" : String.format("(%+d)", base), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}
		}
	}

	private UIListBox list;

	private final ClickButton closeButton;

	public ResourceInfoDialog(City city) {
		super(Hexpansio.instance.getBase(), 1020, 660, "CITY RESOURCES");
		this.city = city;
		
		ArrayList<ResourcePile.Entry> resList = city.resourcesOnMap.getSortedList();
		
		list = new UIListBox(box, resList.toArray(new ResourcePile.Entry[resList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new ResourceListItem(this, index, object);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				
				int x = 10;
				int y = -10;
				g.setFont(Res.font);
				g.setColor(Color.WHITE);
				g.drawString("Resource", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				x += 40;

				x += 180;
				g.drawString("Produced", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				x += 80;
				g.drawString("Import", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				x += 80;
				g.drawString("Export", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				
				x += 120;
				g.drawString("Total", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);

				x += 40;
				for(YieldResource res : YieldResource.values()) {
					x += 80;
					g.setColor(res.fill);
					g.drawString(res.name, x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				}
			}
		};
		list.setSize(1000, 660-100-100-60);
		list.setLocation(10, 100+100);
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
	}

	@Override
	protected void paintBoxContents(GraphAssist g) {
		int x = 30;
		g.resetStroke();
		g.drawRect(x-20, 50, 250, 100, Res.uiBorderDark);
		
		int y = 80;
		g.setColor(YieldResource.happiness.fill);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("%+d Happiness", city.happyIn-city.happyOut), x, y);
		y += 25;
		g.setFont(Res.font);
		g.drawString(String.format("%+d income", city.happyIn), x, y);
		y += 15;
		g.drawString(String.format("-%d expenses", city.happyOut), x, y);
		
		x += 250; 
		g.resetStroke();
		g.drawRect(x-20, 50, 250, 100, Res.uiBorderDark);
		
		y = 80;
		g.setColor(YieldResource.food.fill);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("%+d Food", city.foodIn-city.foodOut), x, y);
		y += 25;
		g.setFont(Res.font);
		g.drawString(String.format("%+d income", city.foodIn), x, y);
		y += 15;
		g.drawString(String.format("-%d expenses", city.foodOut), x, y);

		x += 250; 
		g.resetStroke();
		g.drawRect(x-20, 50, 250, 100, Res.uiBorderDark);

		y = 80;
		g.setColor(YieldResource.gold.fill);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("%+d Gold", city.goldIn-city.goldOut), x, y);
		y += 25;
		g.setFont(Res.font);
		g.drawString(String.format("%+d income", city.goldIn), x, y);
		y += 15;
		g.drawString(String.format("-%d expenses", city.goldOut), x, y);

		x += 250; 
		g.resetStroke();
		g.drawRect(x-20, 50, 250, 100, Res.uiBorderDark);

		y = 80;
		g.setColor(YieldResource.production.fill);
		g.setFont(Res.fontLarge);
		g.drawString(String.format("%+d Production", city.getProduction()), x, y);
		y += 25;
		g.setFont(Res.font);
		g.drawString(String.format("%+d income", city.prodIn), x, y);
		y += 15;
		if(city.happiness.prodPenalty>0)
			g.drawString(String.format("-%d%% penalty", city.happiness.prodPenalty), x, y);

		super.paintBoxContents(g);
	}
	
}
