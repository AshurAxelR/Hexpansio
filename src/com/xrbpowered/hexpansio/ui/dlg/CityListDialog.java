package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.Cursor;
import java.util.ArrayList;
import java.util.Comparator;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.OptionBox;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class CityListDialog extends OverlayDialog {

	public final World world;
	public ArrayList<City> cityList;

	private class CityListItem extends UIListItem {
		public CityListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 40);
		}
		@Override
		public void paint(GraphAssist g) {
			City city = (City) object;
			if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			
			int x = 10;
			int y = (int)(getHeight()/2f);
			Res.paintHappiness(g, x+15, y, city.happiness);
			
			x += 40;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(city.name, x, y, GraphAssist.LEFT, GraphAssist.CENTER);

			x += 180;
			g.setFont(Res.fontLarge);
			g.drawString(Integer.toString(city.population), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
			g.setFont(Res.font);
			
			x += 20;
			for(YieldResource res : YieldResource.values()) {
				x += 70;
				g.setColor(res.fill);
				g.drawString(String.format("%+d", city.balance.get(res)), x, y, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			
			x += 90;
			g.setColor(Color.WHITE);
			g.drawString(String.format("%+d", city.resourcesAvail.totalCount()), x, y, GraphAssist.CENTER, GraphAssist.CENTER);

			x += 80;
			g.setColor(TerrainType.Feature.thevoid.color);
			g.drawString(String.format("%d%%",city.getVoidResist()), x, y, GraphAssist.CENTER, GraphAssist.CENTER);

			x += 60;
			if(city.buildingProgress==null) {
				int goods = city.getExcess(city.getProduction());
				g.setColor(goods>0 ? YieldResource.production.fill : Color.GRAY);
				g.drawString(String.format("%+d Goods", goods), x, y, GraphAssist.LEFT, GraphAssist.CENTER);
			}
			else {
				g.setColor(Color.WHITE);
				g.drawString(city.buildingProgress.getName(), x, y, GraphAssist.LEFT, GraphAssist.CENTER);
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
				dismiss();
				Hexpansio.instance.view.view.selectCity((City)object, true);
				return true;
			}
			else
				return false;
		}
	}

	private static final String[] sortOptionNames = {
		"Build order", "Name", "Population",
		YieldResource.happiness.name, YieldResource.food.name, YieldResource.gold.name, YieldResource.production.name,
		"Resources", "Void resistance"
	};
	
	private static int sortMode = 0;
	
	private UIListBox list;

	private final OptionBox sortOption;

	private final ClickButton closeButton;

	public CityListDialog() {
		super(Hexpansio.instance.getBase(), 1020, 660, "CITIES");
		this.world = Hexpansio.getWorld();
		
		cityList = new ArrayList<>(world.cities);
		
		list = new UIListBox(box, cityList.toArray(new City[cityList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new CityListItem(this, index, object);
			}
			@Override
			protected void paintChildren(GraphAssist g) {
				super.paintChildren(g);
				
				int x = 10+40;
				int y = -10;
				g.setFont(Res.font);
				g.setColor(Color.WHITE);
				g.drawString("City", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);

				x += 180;
				g.drawString("Population", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				
				x += 20;
				for(YieldResource res : YieldResource.values()) {
					x += 70;
					g.setColor(res.fill);
					g.drawString(res.name, x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				}

				x += 90;
				g.setColor(Color.WHITE);
				g.drawString("Resources", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				x += 80;
				g.setColor(TerrainType.Feature.thevoid.color);
				g.drawString("Resist", x, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
				x += 60;
				g.setColor(Color.WHITE);
				g.drawString("Currently building", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
			}
		};
		list.setSize(1000, 660-100-30);
		list.setLocation(10, 70);
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
		
		sortOption = new OptionBox(box, "Sort by:", sortOptionNames.length, null) {
			@Override
			protected void selectOption(int value) {
				sortMode = value;
				sort();
			}
			@Override
			protected String formatOption(int value) {
				return sortOptionNames[value];
			}
		};
		sortOption.setSize(260, sortOption.getHeight());
		sortOption.setLocation(box.getWidth()-sortOption.getWidth()-10, closeButton.getY()+closeButton.getHeight()/2f-sortOption.getHeight()/2f);
		sortOption.findOption(sortMode);
		
		sort();
	}
	
	private void sort() {
		cityList.sort(new Comparator<City>() {
			@Override
			public int compare(City city1, City city2) {
				int res = 0;
				switch(sortMode) {
					case 1:
						res = city1.name.compareToIgnoreCase(city2.name);
						break;
					case 2:
						res = -Integer.compare(city1.population, city2.population);
						break;
					case 3:
						res = -Integer.compare(city1.balance.get(YieldResource.happiness), city2.balance.get(YieldResource.happiness));
						break;
					case 4:
						res = -Integer.compare(city1.balance.get(YieldResource.food), city2.balance.get(YieldResource.food));
						break;
					case 5:
						res = -Integer.compare(city1.balance.get(YieldResource.gold), city2.balance.get(YieldResource.gold));
						break;
					case 6:
						res = -Integer.compare(city1.balance.get(YieldResource.production), city2.balance.get(YieldResource.production));
						break;
					case 7:
						res = -Integer.compare(city1.resourcesAvail.totalCount(), city2.resourcesAvail.totalCount());
						break;
					case 8:
						res = -Integer.compare(city1.getVoidResist(), city2.getVoidResist());
						break;
				}
				if(res==0)
					res = Integer.compare(city1.id, city2.id);
				return res;
			}
		});
		list.setItems(cityList.toArray(new City[cityList.size()]));
	}

}
