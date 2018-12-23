package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.GradientPaint;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.UIHoverElement;

public class BottomPane extends UIContainer {

	private static final int buttonWidth = 70;
	private static final int buttonFrameSize = 50;
	
	public class ModeButton extends UIHoverElement {
		public final MapMode mode;
		
		public ModeButton(MapMode mode) {
			super(BottomPane.this);
			this.mode = mode;
			setSize(buttonWidth, getParent().getHeight());
		}

		@Override
		public void paint(GraphAssist g) {
			g.pushAntialiasing(true);

			float d = (getHeight()-buttonFrameSize)/4f;
			g.setColor(Color.WHITE);
			g.setFont(Res.font);
			g.drawString(mode.label.toUpperCase(), getWidth()/2f, d, GraphAssist.CENTER, GraphAssist.CENTER);
			String status = mode.getButtonStatusText();
			if(status!=null)
				g.drawString(status, getWidth()/2f, getHeight()-d, GraphAssist.CENTER, GraphAssist.CENTER);
			
			g.setColor(Color.WHITE);
			if(mode.isActive()) {
				g.setStroke(6f);
				g.graph.drawRoundRect((int)getWidth()/2-buttonFrameSize/2, (int)getHeight()/2-buttonFrameSize/2, buttonFrameSize, buttonFrameSize, 5, 5);
			}
			boolean enabled = mode.isEnabled();
			boolean hot = mode.isHighlighted();
			if(!enabled)
				g.setColor(Color.BLACK);
			else if(hot)
				g.setPaint(new GradientPaint(0, getHeight()/2f-buttonFrameSize/2f, new Color(0xffbb33), 0, getHeight()/2f+buttonFrameSize/2f, new Color(0x996600)));
			else
				g.setPaint(new GradientPaint(0, getHeight()/2f-buttonFrameSize/2f, new Color(0x77bbff), 0, getHeight()/2f+buttonFrameSize/2f, new Color(0x336699)));
			g.graph.fillRoundRect((int)getWidth()/2-buttonFrameSize/2, (int)getHeight()/2-buttonFrameSize/2, buttonFrameSize, buttonFrameSize, 5, 5);
			g.setColor(hover ? Color.WHITE : hot ? new Color(0xffdd99) : new Color(0xbbddff));
			g.resetStroke();
			g.graph.drawRoundRect((int)getWidth()/2-buttonFrameSize/2, (int)getHeight()/2-buttonFrameSize/2, buttonFrameSize, buttonFrameSize, 5, 5);
			
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			g.setFont(Res.fontHuge);
			g.drawString(mode.keyName(), getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			
			g.popAntialiasing();
		}
		
		@Override
		public boolean onMouseDown(float x, float y, Button button, int mods) {
			if(button==Button.left && mods==UIElement.modNone) {
				mode.activate();
				repaint();
				return true;
			}
			else
				return false;
		}
	};

	private ModeButton[] modeButtons;
	private MapView view;
	
	public BottomPane(UIContainer parent, final MapView view) {
		super(parent);
		setSize(0, 120);
		
		this.view = view;
		
		modeButtons = new ModeButton[MapMode.modes.length];
		for(int i=0; i<modeButtons.length; i++) {
			MapMode.modes[i].init(view);
			ModeButton b = new ModeButton(MapMode.modes[i]);
			b.setLocation(i*buttonWidth+10, 0);
			modeButtons[i] = b;
		}
	}

	@Override
	protected void paintSelf(GraphAssist g) {
		g.setPaint(new GradientPaint(0, 0, new Color(0x336699), 0, getHeight(), Color.BLACK));
		g.fill(this);
		g.resetStroke();
		//g.setStroke(getPixelScale());
		g.hborder(this, GraphAssist.TOP, Color.WHITE);
		
		MapMode mode = MapMode.active;
		
		int y = 30;
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(mode.label.toUpperCase()+" MODE", getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		
		y += 25;
		g.setFont(Res.font);
		Tile tile = view.hoverTile; 
		if(tile!=null && tile.discovered) {
			String s;
			if(tile.isCityCenter())
				s = tile.city.name.toUpperCase();
			else {
				s = tile.terrain.name;
				if(tile.improvement!=null)
					s += ", "+tile.improvement.name;
				if(tile.city!=null)
					s += " worked by "+tile.city.name;
			}
			g.drawString(s, getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}
		
		y += 17;
		y = mode.paintHoverTileHint(g, (int)getWidth()/2, y);
	}
	
}
