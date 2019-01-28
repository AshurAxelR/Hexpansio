package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.CityRenameDialog;
import com.xrbpowered.hexpansio.ui.dlg.menu.GameMenu;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class TopPane extends UIContainer {

	private final ClickButton menuButton;
	private final ClickButton cityListButton;
	private final ClickButton statsButton;
	private final ClickButton prevCityButton, nextCityButton;
	private final FrameButton cityFrame;

	public TopPane(UIContainer parent) {
		super(parent);
		setSize(0, 60);
		
		menuButton = new ClickButton(this, "Menu", 80, (int)getHeight(), Res.font) {
			@Override
			public void onClick() {
				new GameMenu().repaint();
			}
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover && Hexpansio.settings.hotkeyTooltips)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, "Hotkey: Esc", GraphAssist.BOTTOM);
			}
		};
		menuButton.frameWidth = (int)menuButton.getWidth()-20;
		menuButton.frameHeight = ClickButton.defaultHeight;

		cityListButton = new ClickButton(this, "Cities", 80, (int)getHeight(), Res.font) {
			@Override
			public void onClick() {
				// TODO city list dialog
			}
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover && Hexpansio.settings.hotkeyTooltips)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, "Hotkey: F2", GraphAssist.BOTTOM);
			}
		};
		cityListButton.frameWidth = (int)cityListButton.getWidth()-20;
		cityListButton.frameHeight = ClickButton.defaultHeight;

		statsButton = new ClickButton(this, "Stats", 60, (int)getHeight(), Res.font) {
			@Override
			public void onClick() {
				// TODO stats dialog
			}
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover && Hexpansio.settings.hotkeyTooltips)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, "Hotkey: F1", GraphAssist.BOTTOM);
			}
		};
		statsButton.frameHeight = ClickButton.defaultHeight;

		prevCityButton = new ArrowButton(this, -1, 36, (int)getHeight(), 36) {
			@Override
			public void onClick() {
				Hexpansio.instance.browseCity(delta);
			}
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, Hexpansio.settings.hotkeyTooltips ? "Previous city. Hotkey: Left" : "Previous city", GraphAssist.BOTTOM);
			}
		};
		
		nextCityButton = new ArrowButton(this, 1, 36, (int)getHeight(), 36) {
			@Override
			public void onClick() {
				Hexpansio.instance.browseCity(delta);
			}
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, Hexpansio.settings.hotkeyTooltips ? "Next city. Hotkey: Right" : "Next city", GraphAssist.BOTTOM);
			}
		};
		
		cityFrame = new FrameButton(this, 3, 3) {
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover)
					Res.paintTooltip(g, getWidth()/2, getHeight()+5, "Click to center. Right-click to rename.", GraphAssist.BOTTOM);
			}
			@Override
			public void onClick() {
				Hexpansio.instance.browseCity(0);
			}
			@Override
			public boolean onMouseDown(float x, float y, Button button, int mods) {
				if(button==Button.right && mods==UIElement.modNone) {
					City city = Hexpansio.instance.view.view.selectedCity;
					new CityRenameDialog(city).repaint();
					return true;
				}
				else
					return super.onMouseDown(x, y, button, mods);
			}
		};
		cityFrame.setSize(540, getHeight());
	}
	
	@Override
	public void layout() {
		menuButton.setLocation(getWidth()-menuButton.getWidth(), 0);
		statsButton.setLocation(menuButton.getX()-statsButton.getWidth()+5, 0);
		cityListButton.setLocation(0, 0);
		prevCityButton.setLocation(getWidth()/2-prevCityButton.getWidth()-cityFrame.getWidth()/2-10, 0);
		nextCityButton.setLocation(getWidth()/2+cityFrame.getWidth()/2+10, 0);
		cityFrame.setLocation(getWidth()/2-cityFrame.getWidth()/2, 0);
		super.layout();
	}

	@Override
	protected void paintSelf(GraphAssist g) {
		g.setPaint(new GradientPaint(0, 0, Res.uiBgColor, 0, getHeight(), new Color(0x336699)));
		g.fill(this);
		g.resetStroke();
		g.hborder(this, GraphAssist.BOTTOM, Color.WHITE);
		
		World world = ((Hexpansio) getParent()).view.view.world;
		if(world==null)
			return;

		int x = (int)statsButton.getX()-10;
		int y = (int)getHeight()/2;

		g.setColor(Color.WHITE);
		g.setFont(Res.font);
		g.drawString(String.format("%s    Population: %d    Cities: %d    Turn: %d", world.cheater ? "CHEATER!" : "", world.totalPopulation, world.cities.size(), world.turn),
				x, y, GraphAssist.RIGHT, GraphAssist.CENTER);
		
		x = (int)cityListButton.getWidth();

		g.setFont(Res.font);
		g.setColor(Color.WHITE);
		g.drawString(String.format("Gold: %d (%+d)    Goods: %d (%+d)", world.gold, world.totalGoldIn, world.goods, world.totalGoodsIn),
				x, y, GraphAssist.LEFT, GraphAssist.CENTER);
		
		City city = ((Hexpansio) getParent()).view.view.selectedCity;
		if(city==null)
			return;
		g.setColor(Color.WHITE);
		g.setFont(Res.fontHuge);
		g.drawString(city.name.toUpperCase(), getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);

	}
}
