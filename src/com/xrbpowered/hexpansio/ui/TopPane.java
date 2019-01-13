package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.CityRenameDialog;
import com.xrbpowered.hexpansio.ui.dlg.GameMenu;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;

public class TopPane extends UIContainer {

	private final ClickButton menuButton;
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
		};
		menuButton.frameWidth = (int)menuButton.getWidth()-20;
		menuButton.frameHeight = ClickButton.defaultHeight;
		
		prevCityButton = new ArrowButton(this, -1, 36, (int)getHeight(), 36) {
			@Override
			public void onClick() {
				Hexpansio.instance.browseCity(delta);
			}
		};
		
		nextCityButton = new ArrowButton(this, 1, 36, (int)getHeight(), 36) {
			@Override
			public void onClick() {
				Hexpansio.instance.browseCity(delta);
			}
		};
		
		cityFrame = new FrameButton(this, 3, 3) {
			@Override
			public void paint(GraphAssist g) {
				super.paint(g);
				if(hover) {
					g.setFont(Res.font);
					String s = "Click to center. Right-click to rename.";
					FontMetrics fm = g.graph.getFontMetrics();
					float w = fm.stringWidth(s)+20;
					g.fillRect(getWidth()/2-w/2, getHeight()+5, w, 25, Color.BLACK);
					g.resetStroke();
					g.drawRect(getWidth()/2-w/2, getHeight()+5, w, 25, Color.LIGHT_GRAY);
					g.setColor(Color.WHITE);
					g.drawString(s, getWidth()/2, getHeight()+17.5f, GraphAssist.CENTER, GraphAssist.CENTER);
				}
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
		//g.setStroke(getPixelScale());
		g.hborder(this, GraphAssist.BOTTOM, Color.WHITE);
		
		World world = ((Hexpansio) getParent()).view.view.world;
		if(world==null)
			return;

		int x = (int)(menuButton.getX()-10);

		g.setColor(Color.WHITE);
		g.setFont(Res.font);
		g.drawString(String.format("Population: %d    Cities: %d    Turn: %d", world.totalPopulation, world.cities.size(), world.turn),
				x, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
		
		x = 10;
		
		g.setFont(Res.font);
		g.drawString(String.format("Gold: %d (%+d)    Goods: %d (%+d)", world.gold, world.totalGoldIn, world.goods, world.totalGoodsIn),
				x, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
		
		City city = ((Hexpansio) getParent()).view.view.selectedCity;
		if(city==null)
			return;
		g.setColor(Color.WHITE);
		g.setFont(Res.fontHuge);
		g.drawString(city.name.toUpperCase(), getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);

	}
}
