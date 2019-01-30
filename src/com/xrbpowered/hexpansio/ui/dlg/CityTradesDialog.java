package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Comparator;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.modes.TradeMode;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.Trade;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class CityTradesDialog extends OverlayDialog {

	public final City city;

	private class TradeListItem extends UIListItem {
		public TradeListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 55);
		}
		@Override
		public void paint(GraphAssist g) {
			City otherCity = (City) object;
			Trade trade = city.trades.get(otherCity);
			if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			
			int x = 10;
			int y = 20;
			g.setColor(trade!=null ? Color.WHITE : Color.GRAY);
			g.setFont(Res.fontBold);
			g.drawString(otherCity.name, x, y, GraphAssist.LEFT, GraphAssist.CENTER);
			g.setFont(Res.font);

			x += 200;
			if(trade!=null) {
				Res.paintIncome(g, YieldResource.gold, null, trade.getProfit(), null, x, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			
			x += 80;
			if(trade!=null && !trade.in.isEmpty()) {
				trade.in.paint(g, x, 5, "%d");
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", x+15, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			x += 220;
			if(trade!=null && !trade.out.isEmpty()) {
				trade.out.paint(g, x, 5, "%d");
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", x+15, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			x += 220;
			ResourcePile avail = new ResourcePile();
			avail.add(otherCity.resourcesProduced);
			avail.remove(otherCity.trades.totalOut);
			if(!avail.isEmpty()) {
				avail.paint(g, x, 5, "%d");
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", x+15, y, GraphAssist.CENTER, GraphAssist.CENTER);
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
		
		@Override
		public boolean onMouseDown(float x, float y, Button button, int mods) {
			if(button==Button.left) {
				new SetupTradeDialog(city, (City)object).repaint();
				return true;
			}
			else
				return false;
		}
	}

	private UIListBox list;

	private final ClickButton closeButton;

	public CityTradesDialog(final City city) {
		super(Hexpansio.instance.getBase(), 1020, 600, "CITY TRADES: "+city.name.toUpperCase());
		this.city = city;
		
		ArrayList<City> cityList = new ArrayList<>(city.getNeighbours(TradeMode.cityRange));
		cityList.sort(new Comparator<City>() {
			@Override
			public int compare(City city1, City city2) {
				return city1.name.compareToIgnoreCase(city2.name);
			}
		});
		
		list = new UIListBox(box, cityList.toArray(new City[cityList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new TradeListItem(this, index, object);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				
				int x = 10;
				int y = -10;
				g.setFont(Res.font);
				g.setColor(Color.WHITE);
				g.drawString("With city", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);

				x += 200;
				g.drawString("Balance", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				x += 80;
				g.drawString("Import", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				x += 220;
				g.drawString("Export", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				x += 220;
				g.drawString("Available", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				
				y = (int)getHeight()+10;
				g.drawString("Available for export:", x-20, y+15, GraphAssist.RIGHT, GraphAssist.CENTER);
				ResourcePile avail = new ResourcePile();
				avail.add(city.resourcesProduced);
				avail.remove(city.trades.totalOut);
				if(!avail.isEmpty()) {
					avail.paint(g, x, y, "%d");
				}
				else {
					g.setColor(Color.GRAY);
					g.drawString("-", x+15, y+15, GraphAssist.CENTER, GraphAssist.CENTER);
				}
			}
		};
		list.setSize(1000, 600-100-60);
		list.setLocation(10, 70);
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
	}

}
