package com.xrbpowered.hexpansio;

import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.BottomPane;
import com.xrbpowered.hexpansio.ui.CityInfoPane;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.ui.TileInfoPane;
import com.xrbpowered.hexpansio.ui.TopPane;
import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.world.Save;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class Hexpansio extends UIContainer implements KeyInputHandler {

	public static Hexpansio instance = null;
	
	private World world = loadOrCreateWorld();
	
	public static World loadOrCreateWorld() {
		Save save = new Save("./hexpansion.save");
		if(save.exists()) {
			World world = save.read();
			if(world!=null)
				return world;
		}
		return save.startNew(System.currentTimeMillis());
	}
	
	public final TopPane top;
	public final BottomPane bottom;
	public final MapView.Zoom view;
	public final CityInfoPane cityInfo; 
	public final TileInfoPane tileInfo;
	
	public Hexpansio(UIContainer parent) {
		super(parent);
		instance = this;
		
		view = MapView.createMapView(this);
		view.view.setWorld(world);
		cityInfo = new CityInfoPane(this);
		tileInfo = new TileInfoPane(this);
		top = new TopPane(this);
		bottom = new BottomPane(this, view.view);
		
		getBase().setFocus(this);
	}
	
	@Override
	public void layout() {
		top.setLocation(0, 0);
		top.setSize(getWidth(), top.getHeight());
		bottom.setLocation(0, getHeight()-bottom.getHeight());
		bottom.setSize(getWidth(), bottom.getHeight());
		
		float viewHeight = getHeight()-top.getHeight()-bottom.getHeight();
		cityInfo.setLocation(0, top.getHeight());
		cityInfo.setSize(cityInfo.getWidth(), viewHeight);
		tileInfo.setLocation(getWidth()-tileInfo.getWidth(), top.getHeight());
		tileInfo.setSize(tileInfo.getWidth(), viewHeight);
		
		view.setLocation(cityInfo.getWidth(), top.getHeight());
		view.setSize(getWidth()-cityInfo.getWidth()-tileInfo.getWidth(), viewHeight);
		super.layout();
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		switch(code) {
			case KeyEvent.VK_ENTER:
				world.nextTurn();
				world.save.write();
				repaint();
				return true;
			case KeyEvent.VK_LEFT: {
				int index = world.cities.indexOf(view.view.selectedCity) - 1;
				if(index<=0)
					index = world.cities.size()-1;
				view.view.selectCity(world.cities.get(index), true);
				repaint();
				return true;
			}
			case KeyEvent.VK_RIGHT: {
				int index = world.cities.indexOf(view.view.selectedCity) + 1;
				if(index>=world.cities.size())
					index = 0;
				view.view.selectCity(world.cities.get(index), true);
				repaint();
				return true;
			}
			case KeyEvent.VK_F1: {
				float scale = getBase().getBaseScale();
				if(scale>1f) {
					getBase().setBaseScale(scale-0.25f);
					repaint();
				}
				return true;
			}
			case KeyEvent.VK_F2: {
				float scale = getBase().getBaseScale();
				if(scale<2f) {
					getBase().setBaseScale(scale+0.25f);
					repaint();
				}
				return true;
			}
			case KeyEvent.VK_F4: 
				if(mods==UIElement.modCtrlMask) {
					world.debugDiscover(5);
					repaint();
					return true;
				}
				else
					return false;
			default:
				if(MapMode.checkHotkey(code)) {
					repaint();
					return true;
				}
				else
					return false;
		}
	}

	@Override
	public void onFocusGained() {
	}

	@Override
	public void onFocusLost() {
	}
	
	@Override
	public void paint(GraphAssist g) {
		g.graph.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		super.paint(g);
	}

	public static void main(String[] args) {
		SwingFrame frame = SwingWindowFactory.use().createFullscreen();
		new Hexpansio(frame.getContainer());
		frame.show();
	}

}
