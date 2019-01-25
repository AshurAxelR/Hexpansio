package com.xrbpowered.hexpansio;

import java.awt.Color;
import java.awt.RenderingHints;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.BottomPane;
import com.xrbpowered.hexpansio.ui.CityInfoPane;
import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.ui.TileInfoPane;
import com.xrbpowered.hexpansio.ui.TopPane;
import com.xrbpowered.hexpansio.ui.dlg.GameMenu;
import com.xrbpowered.hexpansio.ui.dlg.QuickExitDialog;
import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.world.Save;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.WorldSettings;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.swing.SwingFrame;
import com.xrbpowered.zoomui.swing.SwingWindowFactory;

public class Hexpansio extends UIContainer implements KeyInputHandler {

	public static Hexpansio instance = null;
	
	public static boolean saveOnExit = false;
	
	private static Save save = new Save("./hexpansion.save"); 
	private static World world = null;
	
	public static World getWorld() {
		return world;
	}
	
	public static boolean saveExists() {
		return save.exists();
	}
	
	public final TopPane top;
	public final BottomPane bottom;
	public final MapView.Zoom view;
	public final CityInfoPane cityInfo; 
	public final TileInfoPane tileInfo;
	
	public Hexpansio(UIContainer parent) {
		super(parent);
		instance = this;
		setVisible(false);
		
		view = MapView.createMapView(this);
		if(world!=null)
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
	
	private void setWorld(World world) {
		Hexpansio.world = world;
		if(world!=null) {
			view.view.setWorld(world);
			setVisible(true);
		}
		else {
			setVisible(false);
		}
	}
	
	public void newGame(WorldSettings settings) {
		save.delete();
		setWorld(save.startNew(settings));
	}
	
	public void saveGame() {
		if(world!=null)
			save.write();
	}
	
	public void loadGame() {
		if(saveExists()) {
			setWorld(save.read());
		}
	}
	
	public void nextTurn() {
		world.nextTurn();
		System.out.printf("Turn %d\n", world.turn);
		// saveGame();
		repaint();
	}
	
	public void browseCity(int delta) {
		int index = world.cities.indexOf(view.view.selectedCity) + delta;
		if(index<0)
			index = world.cities.size()-1;
		else if(index>=world.cities.size())
			index = 0;
		view.view.selectCity(world.cities.get(index), true);
		repaint();
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		switch(code) {
			case KeyEvent.VK_ESCAPE:
				new GameMenu();
				repaint();
				return true;
			case KeyEvent.VK_ENTER:
				if(world!=null)
					nextTurn();
				return true;
			case KeyEvent.VK_UP:
				if(world!=null)
					view.scale(view.getMinScale()/view.getScale(), 0f, 0f);
				repaint();
				return true;
			case KeyEvent.VK_DOWN:
				if(world!=null)
					view.scale(view.getMaxScale()/view.getScale(), 0f, 0f);
				repaint();
				return true;
			case KeyEvent.VK_LEFT:
				if(world!=null)
					browseCity(-1);
				return true;
			case KeyEvent.VK_RIGHT:
				if(world!=null)
					browseCity(1);
				return true;
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
			/*case KeyEvent.VK_F4: // TODO parse cheats 
				if(mods==UIElement.modCtrlMask) {
					world.debugDiscover(5);
					repaint();
					return true;
				}
				else
					return true;*/
			default:
				if(MapMode.checkHotkey(code)) {
					repaint();
					return true;
				}
				else
					return true;
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
		g.graph.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		try {
			super.paint(g);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.exit(1);
		}
	}

	public static void main(String[] args) {
		Res.restyleStdControls();
		SwingFrame frame = new SwingFrame(SwingWindowFactory.use(), "Hexpansio", 800, 600, false, true) {
			@Override
			public boolean onClosing() {
				new QuickExitDialog().repaint();
				return false;
			}
		};
		frame.maximize();
		new UIElement(frame.getContainer()) {
			@Override
			public void paint(GraphAssist g) {
				g.fill(this, Color.BLACK);
			}
		};
		new Hexpansio(frame.getContainer());
		new GameMenu();
		frame.show();
	}

}
