package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.Trade;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListBoxBase;
import com.xrbpowered.zoomui.std.UIScrollBar;

public class SetupTradeDialog extends OverlayDialog {

	public final City city, otherCity;
	public final ResourcePile resourcePool = new ResourcePile();
	public final Yield.Cache cityYield = new Yield.Cache();
	public final Yield.Cache otherCityYield = new Yield.Cache();

	private class ArrowButton extends ClickButton {
		public final TokenResource resource;
		public final int delta;
		
		public ArrowButton(ResourceListItem parent, TokenResource resource, int delta) {
			super(parent, delta>0 ? ">" : "<", 30, 30, Res.font);
			this.resource = resource;
			this.delta = delta;
		}
		
		protected void paintLabel(GraphAssist g, boolean enabled, String label) {
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			if(delta>0)
				UIScrollBar.drawRightArrow(g, (int)getWidth()/2, (int)getHeight()/2, 6);
			else
				UIScrollBar.drawLeftArrow(g, (int)getWidth()/2, (int)getHeight()/2, 6);
		}
		
		@Override
		public boolean isEnabled() {
			if(delta>0) {
				int prod = city.resourcesProduced.count(resource);
				int avail = prod - city.trades.totalOut.count(resource);
				int out = resourcePool.count(resource);
				avail -= out;
				return avail>0;
			}
			else {
				int prod = otherCity.resourcesProduced.count(resource);
				int avail = prod - otherCity.trades.totalOut.count(resource);
				int out = resourcePool.count(resource);
				avail -= -out;
				return avail>0;
			}
		}
		
		@Override
		public void onClick() {
			if(isEnabled()) {
				resourcePool.map.get(resource.name).count += delta;
				updateStats();
				repaint();
			}
		}
		
		@Override
		public void onMouseIn() {
			((ResourceListItem) getParent()).hover = true;
			super.onMouseIn();
		}
		
		@Override
		public void onMouseOut() {
			((ResourceListItem) getParent()).hover = false;
			super.onMouseOut();
		}
	}
	
	private class ResourceListItem extends UIContainer {
		private final ResourcePile.Entry e;
		public boolean hover = false; 
		
		private final ArrowButton left, right;
		
		public ResourceListItem(UIContainer parent, ResourcePile.Entry e) {
			super(parent);
			this.e = e;
			
			left = new ArrowButton(this, e.resource, -1);
			right = new ArrowButton(this, e.resource, 1);
			setSize(0, 40);
		}
		
		@Override
		public void setSize(float width, float height) {
			super.setSize(width, height);
			left.setLocation(getWidth()/2-70-left.getWidth()/2, getHeight()/2-left.getHeight()/2);
			right.setLocation(getWidth()/2+70-right.getWidth()/2, left.getY());
		}
		
		@Override
		public void paintSelf(GraphAssist g) {
			if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			left.setVisible(hover);
			right.setVisible(hover);
			
			int xl = 10;
			int xr = (int)getWidth()-10;
			int y = (int)(getHeight()/2f);
			e.resource.paint(g, xl, y-15, null);
			e.resource.paint(g, xr-30, y-15, null);
			
			xl += 40;
			xr -= 40;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(e.resource.name, xl, y, GraphAssist.LEFT, GraphAssist.CENTER);
			g.drawString(e.resource.name, xr, y, GraphAssist.RIGHT, GraphAssist.CENTER);

			xl += 180;
			xr -= 180;
			g.setFont(Res.font);
			int prod = city.resourcesProduced.count(e.resource);
			if(prod>0) {
				int avail = prod - city.trades.totalOut.count(e.resource);
				int out = resourcePool.count(e.resource);
				if(out>0) avail -= out;
				g.setColor(Color.WHITE);
				g.drawString(String.format("%d / %d", avail, prod), xl, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", xl, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			prod = otherCity.resourcesProduced.count(e.resource);
			if(prod>0) {
				int avail = prod - otherCity.trades.totalOut.count(e.resource);
				int out = resourcePool.count(e.resource);
				if(out<0) avail -= -out;
				g.setColor(Color.WHITE);
				g.drawString(String.format("%d / %d", avail, prod), xr, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", xr, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}

			g.setFont(Res.fontLarge);
			g.setColor(e.count==0 ? Color.GRAY : Color.WHITE);
			if(e.count==0)
				g.drawString("-", getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.CENTER);
			else {
				String s = String.format("%d", Math.abs(e.count));
				FontMetrics fm = g.graph.getFontMetrics();
				float w = fm.stringWidth(s);
				g.drawString(s, getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.CENTER);
				if(e.count>0)
					UIScrollBar.drawRightArrow(g, (int)(getWidth()/2f+w/2f+10), y, 6);
				else
					UIScrollBar.drawLeftArrow(g, (int)(getWidth()/2f-w/2f-10), y, 6);
			}
		}
		
		@Override
		public void onMouseIn() {
			hover = true;
			repaint();
		}
		
		@Override
		public void onMouseOut() {
			hover = false;
			repaint();
		}
	}
	
	private UIListBoxBase<ResourceListItem> list;

	private final ClickButton viewCityButton, viewOtherCityButton;
	private final ClickButton closeButton;
	
	public SetupTradeDialog(City city, City otherCity) {
		super(Hexpansio.instance.getBase(), 1020, 600, "SET UP TRADE ROUTE");
		this.city = city;
		this.otherCity = otherCity;
		resourcePool.add(city.resourcesProduced);
		resourcePool.add(otherCity.resourcesProduced);
		revert();
		
		ArrayList<ResourcePile.Entry> resList = resourcePool.getSortedList();
		
		list = new UIListBoxBase<ResourceListItem>(box, resList.toArray(new ResourcePile.Entry[resList.size()])) {
			@Override
			protected ResourceListItem createItem(int index, Object object) {
				return new ResourceListItem(this.getView(), (ResourcePile.Entry)object);
			}
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, UIListBox.colorBackground);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				
				int xl = 10;
				int xr = (int)(getView().getWidth()-10);
				int y = -10;
				g.setFont(Res.font);
				g.setColor(Color.WHITE);
				g.drawString("Resource", xl, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				g.drawString("Resource", xr, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
				xl += 220;
				xr -= 220;
				g.drawString("Available", xl, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				g.drawString("Available", xr, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				g.drawString("Trade", getView().getWidth()/2, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
			}
		};
		list.setSize(1000, 600-70-110-60);
		list.setLocation(10, 110);
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
		
		viewCityButton = new ClickButton(box, "View", 60);
		viewCityButton.setLocation(10, 60-viewCityButton.getHeight()/2);
		viewOtherCityButton = new ClickButton(box, "View", 60);
		viewOtherCityButton.setLocation(box.getWidth()-viewOtherCityButton.getWidth()-10, viewCityButton.getY());
	}
	
	public void updateStats() {
		cityYield.clear();
		otherCityYield.clear();
		for(ResourcePile.Entry e : resourcePool.getUnsorted())
			for(YieldResource res : YieldResource.values()) {
				cityYield.add(res, -e.count * (e.resource.yield.get(res) + city.effects.addResourceBonusYield(e.resource, res)));
				otherCityYield.add(res, e.count * (e.resource.yield.get(res) + otherCity.effects.addResourceBonusYield(e.resource, res)));
			}
	}
	
	public void reset() {
		for(ResourcePile.Entry e : resourcePool.getUnsorted())
			e.count = 0;
		updateStats();
	}
	
	public void revert() {
		Trade trade = city.trades.get(otherCity);
		if(trade==null)
			reset();
		else {
			for(ResourcePile.Entry e : resourcePool.getUnsorted())
				e.count = trade.in.count(e.resource) - trade.out.count(e.resource);
			updateStats();
		}
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(city.name.toUpperCase(), 20+viewCityButton.getX()+viewCityButton.getWidth(), 60, GraphAssist.LEFT, GraphAssist.CENTER);
		g.drawString(otherCity.name.toUpperCase(), viewOtherCityButton.getX()-20, 60, GraphAssist.RIGHT, GraphAssist.CENTER);

		int y = (int)box.getHeight()-50-70;
		g.setFont(Res.font);
		for(YieldResource res : YieldResource.values()) {
			y += 15;
			g.setColor(res.fill);
			g.drawString(String.format("%+d %s", cityYield.get(res), res.name), 20, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
			g.drawString(String.format("%+d %s", otherCityYield.get(res), res.name), box.getWidth()-20, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		}

		super.paintBoxContents(g);
	}

}
