package com.xrbpowered.hexpansio.ui;

import java.awt.Color;
import java.awt.GradientPaint;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.MessageLogDialog;
import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class BottomPane extends UIContainer {

	private static final int buttonWidth = 70;
	private static final int buttonFrameSize = 50;
	
	public class ModeButton extends ClickButton {
		public final MapMode mode;
		
		public ModeButton(MapMode mode) {
			super(BottomPane.this, mode.keyName(), buttonWidth, (int)BottomPane.this.getHeight(), Res.fontHuge);
			this.mode = mode;
			setFrameSize(buttonFrameSize, buttonFrameSize);
		}
		
		@Override
		public boolean isModeActive() {
			return mode.isActive();
		}
		
		@Override
		public boolean isEnabled() {
			return mode.isEnabled();
		}
		
		@Override
		public boolean isHot() {
			return mode.isHighlighted();
		}
		
		@Override
		protected void paintFrame(GraphAssist g, boolean enabled, boolean hot) {
			float d = (getHeight()-buttonFrameSize)/4f;
			g.setColor(Color.WHITE);
			g.setFont(Res.font);
			g.drawString(mode.label.toUpperCase(), getWidth()/2f, d, GraphAssist.CENTER, GraphAssist.CENTER);
			String status = mode.getButtonStatusText();
			if(status!=null)
				g.drawString(status, getWidth()/2f, getHeight()-d, GraphAssist.CENTER, GraphAssist.CENTER);
			
			super.paintFrame(g, enabled, hot);
		}
		
		@Override
		public void onClick() {
			mode.activate();
			repaint();
		}
	};
	
	public class NextTurnButton extends ClickButton {
		public NextTurnButton() {
			super(BottomPane.this, "NEXT TURN", 200, (int)BottomPane.this.getHeight(), Res.fontLarge);
			setFrameSize(frameWidth-40, buttonFrameSize);
		}
		
		@Override
		public boolean isHot() {
			return Hexpansio.getWorld()!=null && Hexpansio.getWorld().problematicCities==0;
		}
		
		@Override
		protected void paintFrame(GraphAssist g, boolean enabled, boolean hot) {
			float d = (getHeight()-buttonFrameSize)/4f;
			g.setColor(Color.WHITE);
			g.setFont(Res.font);
			g.drawString(new SimpleDateFormat("HH:mm").format(new Date()),
					getWidth()/2+frameWidth/2, getHeight()-d, GraphAssist.RIGHT, GraphAssist.CENTER);
			
			String s = problematicCitiesWarning();
			if(s!=null) {
				g.drawString(s, getWidth()/2+frameWidth/2, d, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
			
			super.paintFrame(g, enabled, hot);
		}
		
		@Override
		public void onClick() {
			Hexpansio.instance.safeNextTurn();
		}
	}
	
	public static String problematicCitiesWarning() {
		int v = Hexpansio.getWorld()==null ? 0 : Hexpansio.getWorld().problematicCities;
		return v>0 ? String.format("%d %s may require attention", v, v==1 ? "city" : "cities") : null; 
	}

	private final ModeButton[] modeButtons;
	private final NextTurnButton nextTurnButton;
	private final ClickButton eventsButton;
	
	public BottomPane(UIContainer parent, final MapView view) {
		super(parent);
		setSize(0, 120);
		
		modeButtons = new ModeButton[MapMode.modes.length];
		for(int i=0; i<modeButtons.length; i++) {
			MapMode.modes[i].init(view);
			ModeButton b = new ModeButton(MapMode.modes[i]);
			b.setLocation(i*buttonWidth+10, 0);
			modeButtons[i] = b;
		}
		
		nextTurnButton = new NextTurnButton();
		
		eventsButton = new ClickButton(this, "MESSAGES", 160, (int)this.getHeight(), Res.fontLarge) {
			@Override
			public boolean isEnabled() {
				return MessageLogDialog.isEnabled();
			}
			@Override
			protected void paintFrame(GraphAssist g, boolean enabled, boolean hot) {
				World world = Hexpansio.getWorld();
				if(world!=null) {
					int n = world.pinnedMessages + world.events.size();
					if(n>0) {
						float d = (getHeight()-buttonFrameSize)/4f;
						g.setColor(Color.WHITE);
						g.setFont(Res.font);
						g.drawString(String.format("(%d)", n), getWidth(), getHeight()-d, GraphAssist.RIGHT, GraphAssist.CENTER);
					}
				}
				
				super.paintFrame(g, enabled, hot);
			}
			public void onClick() {
				Hexpansio.instance.showMessageLog(); // TODO toggle, not an overlay dialog
				repaint();
			}
		};
		eventsButton.setFrameSize(eventsButton.frameWidth, buttonFrameSize);
	}
	
	@Override
	public void layout() {
		nextTurnButton.setLocation(getWidth()-nextTurnButton.getWidth(), 0);
		eventsButton.setLocation(nextTurnButton.getX()-eventsButton.getWidth(), 0);
		super.layout();
	}

	@Override
	protected void paintSelf(GraphAssist g) {
		g.setPaint(new GradientPaint(0, 0, new Color(0x336699), 0, getHeight(), Color.BLACK));
		g.fill(this);
		g.resetStroke();
		g.hborder(this, GraphAssist.TOP, Color.WHITE);
		
		MapMode mode = MapMode.active;
		
		int y = 30;
		g.setColor(Color.WHITE);
		g.setFont(Res.fontLarge);
		g.drawString(mode.label.toUpperCase()+" MODE", getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		
		// FIXME change bottom pane hints, create hover tile tooltip
		// TODO message log 
		
		/*y += 25;
		g.setFont(Res.font);
		Tile tile = view.hoverTile; 
		if(tile!=null && tile.discovered) {
			String s;
			if(tile.isCityCenter())
				s = tile.city.name.toUpperCase();
			else {
				s = tile.terrain.name;
				if(tile.improvement!=null)
					s += ", "+tile.improvement.base.name;
				if(tile.city!=null)
					s += " worked by "+tile.city.name;
			}
			g.drawString(s, getWidth()/2f, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}
		
		y += 17;
		y = mode.paintHoverTileHint(g, (int)getWidth()/2, y);*/
	}
	
}
