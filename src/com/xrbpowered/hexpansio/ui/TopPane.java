package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.GameMenu;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class TopPane extends UIContainer {

	private final FrameButton menuButton;

	public TopPane(UIContainer parent) {
		super(parent);
		setSize(0, 60);
		
		menuButton = new FrameButton(this, "Menu", 80, (int)getHeight(), Res.font) {
			@Override
			public void onClick() {
				new GameMenu().repaint();
			}
		};
		menuButton.frameWidth = (int)menuButton.getWidth()-20;
		menuButton.frameHeight = FrameButton.defaultHeight;
	}
	
	@Override
	public void layout() {
		menuButton.setLocation(getWidth()-menuButton.getWidth(), 0);
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
