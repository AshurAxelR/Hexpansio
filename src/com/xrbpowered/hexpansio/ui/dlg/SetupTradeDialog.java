package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.FontMetrics;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ArrowButton;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.ResourcePile;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.Trade;
import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.base.UIListBoxBase;
import com.xrbpowered.zoomui.std.UIArrowButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIScrollBar;
import com.xrbpowered.zoomui.std.UIScrollContainer;

public class SetupTradeDialog extends OverlayDialog {

	public final City city, otherCity;
	public final ResourcePile resourcePool = new ResourcePile();
	public final Yield.Cache cityYield = new Yield.Cache();
	public final Yield.Cache otherCityYield = new Yield.Cache();
	private int countIn, countOut;

	private class BalanceArrowButton extends ArrowButton {
		public final TokenResource resource;
		
		public BalanceArrowButton(ResourceListItem parent, TokenResource resource, int delta) {
			super(parent, delta, 30, 40, 30);
			this.resource = resource;
		}
		
		@Override
		public boolean isEnabled() {
			if(delta>0)
				return getAvailOut(resource, true)>0;
			else
				return getAvailIn(resource, true)>0;
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
			((ResourceListItem) getParent()).setHover(true);
			super.onMouseIn();
		}
		
		@Override
		public void onMouseOut() {
			((ResourceListItem) getParent()).setHover(false);
			super.onMouseOut();
		}
	}
	
	private class ResourceListItem extends UIContainer {
		private final ResourcePile.Entry e;
		public boolean hover = false; 
		
		private final BalanceArrowButton left, right;
		
		public ResourceListItem(UIContainer parent, ResourcePile.Entry e) {
			super(parent);
			this.e = e;
			
			left = new BalanceArrowButton(this, e.resource, -1);
			right = new BalanceArrowButton(this, e.resource, 1);
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
				int avail = getAvailOut(e.resource, false);
				g.setColor(Color.WHITE);
				g.drawString(String.format("%d / %d", avail, prod), xl, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			else {
				g.setColor(Color.GRAY);
				g.drawString("-", xl, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			prod = otherCity.resourcesProduced.count(e.resource);
			if(prod>0) {
				int avail = getAvailIn(e.resource, false);
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
			else
				paintBalanceCounter(g, e.count, (int)getWidth()/2, y);
		}
		
		public void setHover(boolean hover) {
			this.hover = hover;
			hoverResource = hover ? this.e.resource : null;
		}
		
		@Override
		public void onMouseIn() {
			setHover(true);
			repaint();
		}
		
		@Override
		public void onMouseOut() {
			setHover(false);
			repaint();
		}
	}
	
	private void paintBalanceCounter(GraphAssist g, int count, int x, int y) {
		g.setFont(Res.fontLarge);
		String s = String.format("%d", Math.abs(count));
		FontMetrics fm = g.graph.getFontMetrics();
		float w = fm.stringWidth(s);
		g.drawString(s, x, y, GraphAssist.CENTER, GraphAssist.CENTER);
		if(count>0)
			UIArrowButton.drawRightArrow(g, (int)(x+w/2f+10), y, 6);
		else
			UIArrowButton.drawLeftArrow(g, (int)(x-w/2f-10), y, 6);
	}
	
	private UIListBoxBase<ResourceListItem> list;

	private final ClickButton closeButton;
	private final ClickButton revertButton, resetButton, acceptButton;
	
	private TokenResource hoverResource = null;
	
	public SetupTradeDialog(City city, City otherCity) {
		super(Hexpansio.instance.getBase(), 1020, 620, "SET UP TRADE ROUTE");
		this.city = city;
		this.otherCity = otherCity;
		resourcePool.add(city.resourcesProduced);
		resourcePool.add(otherCity.resourcesProduced);
		
		ArrayList<ResourcePile.Entry> resList = resourcePool.getSortedList();
		
		list = new UIListBoxBase<ResourceListItem>(box, resList.toArray(new ResourcePile.Entry[resList.size()])) {
			@Override
			protected UIScrollBar createScroll() {
				return UIScrollContainer.createScroll(this);
			}
			@Override
			protected ResourceListItem createItem(int index, Object object) {
				return new ResourceListItem(this.getView(), (ResourcePile.Entry)object);
			}
			@Override
			protected void paintSelf(GraphAssist g) {
				g.fill(this, UIListBox.colorBackground);
			}
			@Override
			protected void paintBorder(GraphAssist g) {
				g.border(this, UIListBox.colorBorder);
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
		list.setSize(1000, 600-70-110-100);
		list.setLocation(10, 110);
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);

		acceptButton = new ClickButton(box, "ACCEPT", 100) {
			@Override
			public void onClick() {
				accept();
				dismiss();
			}
		};
		acceptButton.setLocation(box.getWidth()-acceptButton.getWidth()-10, closeButton.getY());

		resetButton = new ClickButton(box, "Reset", 100) {
			@Override
			public void onClick() {
				reset();
				repaint();
			}
		};
		resetButton.setLocation(10+closeButton.getWidth()+5, closeButton.getY());

		revertButton = new ClickButton(box, "Revert", 100) {
			@Override
			public void onClick() {
				revert();
				repaint();
			}
		};
		revertButton.setLocation(10+closeButton.getWidth()*2+10, closeButton.getY());

		revert();
	}
	
	public void updateCityStats(City city, Yield.Cache cityYield, City otherCity, int dir) {
		cityYield.clear();
		cityYield.add(YieldResource.happiness, city.world.baseHappiness);
		cityYield.add(city.incomeTiles);
		cityYield.subtract(city.expences);
		
		ResourcePile cityRes = new ResourcePile();
		cityRes.add(city.resourcesProduced);
		cityRes.remove(city.trades.totalOut);
		cityRes.add(city.trades.totalIn);
		
		Trade trade = city.trades.get(otherCity);
		if(trade!=null) {
			cityRes.add(trade.out);
			cityRes.remove(trade.in);
		}
		
		for(ResourcePile.Entry e : cityRes.getUnsorted()) {
			for(YieldResource res : YieldResource.values()) {
				cityYield.add(res, cityRes.count(e.resource) *
						(e.resource.yield.get(res) + city.effects.addResourceBonusYield(e.resource, res)));
			}
		}
		
		for(ResourcePile.Entry e : resourcePool.getUnsorted()) {
			for(YieldResource res : YieldResource.values()) {
				cityYield.add(res, e.count*dir *
						(e.resource.yield.get(res) + city.effects.addResourceBonusYield(e.resource, res)));
			}
		}
		cityYield.add(YieldResource.gold, city.trades.getCityProfitExcluding(otherCity));
		
		Happiness happiness = Happiness.get(cityYield.get(YieldResource.happiness), city.population);
		cityYield.set(YieldResource.production, cityYield.get(YieldResource.production)*(100-happiness.prodPenalty)/100);
	}
	
	public void updateStats() {
		updateCityStats(city, cityYield, otherCity, -1);
		updateCityStats(otherCity, otherCityYield, city, 1);
		
		countIn = 0;
		countOut = 0;
		for(ResourcePile.Entry e : resourcePool.getUnsorted()) {
			if(e.count>0)
				countOut += e.count;
			else
				countIn += -e.count;
		}
	}
	
	public int getAvailOut(TokenResource resource, boolean anyOut) {
		int avail = city.resourcesProduced.count(resource);
		avail -= city.trades.getTotalOutExcluding(resource, otherCity);
		int out = resourcePool.count(resource);
		if(anyOut || out>0)
			avail -= out;
		return avail;
	}

	public int getAvailIn(TokenResource resource, boolean anyOut) {
		int avail = otherCity.resourcesProduced.count(resource);
		avail -= otherCity.trades.getTotalOutExcluding(resource, city);
		int out = resourcePool.count(resource);
		if(anyOut || out<0)
			avail -= -out;
		return avail;
	}

	public void reset() {
		for(ResourcePile.Entry e : resourcePool.getUnsorted())
			e.count = 0;
		updateStats();
	}
	
	public void revert() {
		Trade trade = city.trades.get(otherCity);
		if(trade==null) {
			revertButton.setVisible(false);
			reset();
		}
		else {
			for(ResourcePile.Entry e : resourcePool.getUnsorted())
				e.count = trade.out.count(e.resource) - trade.in.count(e.resource);
			updateStats();
		}
	}
	
	public void accept() {
		ResourcePile in = new ResourcePile();
		ResourcePile out = new ResourcePile();
		for(ResourcePile.Entry e : resourcePool.getUnsorted()) {
			if(e.count>0)
				out.add(e.resource, e.count);
			else if(e.count<0)
				in.add(e.resource, -e.count);
		}
		city.trades.accept(new Trade(city, otherCity, in, out));
		city.updateStats();
		otherCity.updateStats();
		city.world.updateWorldTotals();
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(city.name.toUpperCase(), 20, 60, GraphAssist.LEFT, GraphAssist.CENTER);
		g.drawString(otherCity.name.toUpperCase(), box.getWidth()-20, 60, GraphAssist.RIGHT, GraphAssist.CENTER);

		int y0 = (int)box.getHeight()-110-70;
		g.resetStroke();
		g.drawRect(10, y0-10, box.getWidth()-20, 90, Res.uiBorderDark);
		
		int y = y0+85+20;
		if(hoverResource!=null) {
			g.fillRect(10, y0+85, box.getWidth()-20, 40, Color.BLACK);
			g.drawRect(10, y0+85, box.getWidth()-20, 40, Res.uiBorderDark);
			
			int x = 20;
			hoverResource.paint(g, x, y-15, null);
			x += 40;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(hoverResource.name, x, y, GraphAssist.LEFT, GraphAssist.CENTER);
			
			x = 230;
			g.setFont(Res.font);
			FontMetrics fm = g.graph.getFontMetrics();
			float h = fm.getAscent() - fm.getDescent();
			float ty = y + h/2f;
			String sep = ", ";
			float sepw = fm.stringWidth(sep);
			boolean first = true;
			for(YieldResource res : YieldResource.values()) {
				int base = hoverResource.yield.get(res);
				if(base!=0) {
					if(!first) {
						g.setColor(Color.LIGHT_GRAY);
						g.graph.drawString(sep, x, ty);
						x += sepw;
					}
					g.setColor(res.fill);
					String str = String.format("%+d %s", base, res.name);
					g.graph.drawString(str, x, ty);
					x += fm.stringWidth(str);
					first = false;
				}
			}
		}
		
		y = y0;
		g.setFont(Res.font);
		for(YieldResource res : YieldResource.values()) {
			y += 15;
			g.setColor(res.fill);
			g.drawString(String.format("%+d %s", cityYield.get(res), res.name), 20, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
			g.drawString(String.format("%+d %s", otherCityYield.get(res), res.name), box.getWidth()-20, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		}
		
		y = y0;
		y += 15;
		g.setColor(Color.WHITE);
		Res.paintIncome(g, YieldResource.gold, "Profit: ", countOut, null, 230, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
		g.setColor(Color.WHITE);
		Res.paintIncome(g, YieldResource.gold, "Profit: ", countIn, null, list.getView().getWidth()-210, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		y += 15;
		if(countIn>countOut) {
			g.setColor(Color.WHITE);
			Res.paintIncome(g, YieldResource.gold, "Cover expenses: ", -(countIn - countOut)*3, null, 230, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
		}
		if(countOut>countIn) {
			g.setColor(Color.WHITE);
			Res.paintIncome(g, YieldResource.gold, "Cover expenses: ", -(countOut - countIn)*3, null, list.getView().getWidth()-210, y, GraphAssist.RIGHT, GraphAssist.BOTTOM);
		}

		g.setColor(Color.WHITE);
		Res.paintIncome(g, YieldResource.gold, "Deal balance: ", countIn+countOut-Math.abs(countIn-countOut)*3,
				null, acceptButton.getX()-20, acceptButton.getY()+acceptButton.getHeight()/2, GraphAssist.RIGHT, GraphAssist.CENTER);

		if(countIn!=countOut) {
			g.setColor(Color.RED);
			paintBalanceCounter(g, countOut-countIn, (int)list.getView().getWidth()/2+10, y0+10);
		}

		super.paintBoxContents(g);
	}

}
