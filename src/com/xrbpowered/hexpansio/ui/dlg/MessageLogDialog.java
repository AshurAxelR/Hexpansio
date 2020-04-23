package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.TurnEventMessage;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class MessageLogDialog extends PinDialog {

	private static final Shape marker = new Polygon(new int[] {0, 10, 10, 0}, new int[] {0, 0, 35, 45}, 4);
	
	private class MessageListItem extends UIListItem {
		public MessageListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 45);
		}
		@Override
		public void paint(GraphAssist g) {
			TurnEventMessage msg = (TurnEventMessage) object;
			if(hover) {
				g.fill(this, Res.uiBgBright);
				g.border(this, Res.uiBorderDark);
			}
			else {
				g.fill(this, msg.pinned ? Res.uiBgMid : Color.BLACK);
			}
			
			if(msg.color!=null) {
				g.setColor(msg.color);
				g.graph.fill(marker);
			}
			
			int y = 20;
			g.setColor(Color.WHITE);
			g.setFont(Res.fontBold);
			g.drawString(msg.city==null ? "ALL CITIES" : msg.city, 30, y);

			y += 15;
			g.setFont(Res.font);
			g.drawString(msg.message, 30, y);
			
			if(msg==sepMsg)
				g.line(0, 0, getWidth(), 0, Res.uiBorderDark);
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
				TurnEventMessage msg = (TurnEventMessage) object;
				if(msg.focusTile!=null)
					TileMode.instance.selectTile(msg.focusTile, true);
				repaint();
				return true;
			}
			else
				return false;
		}
	}

	private UIListBox list = null;
	private long lastUpdate = 0L;
	private boolean empty = true;
	private TurnEventMessage sepMsg = null;
	
	public MessageLogDialog(UIContainer parent) {
		super(parent, 400, 540, "MESSAGES");
		updateList();
	}

	private void updateList() {
		if(list!=null) {
			removeChild(list);
			list = null;
		}
		
		World world = Hexpansio.getWorld();
		if(world==null || world.events.isEmpty() && world.problematicCities==0) {
			empty = true;
			return;
		}
		empty = false;
		
		ArrayList<TurnEventMessage> msgList = new ArrayList<TurnEventMessage>();
		for(City city : world.cities)
			city.addPinnedMessages(msgList);
		int pinCount = msgList.size();
		msgList.addAll(world.events);
		if(pinCount>0 && msgList.size()>pinCount)
			sepMsg = msgList.get(pinCount);
		else
			sepMsg = null;

		list = new UIListBox(this,  msgList.toArray(new TurnEventMessage[msgList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new MessageListItem(this, index, object);
			}
		};
		list.setSize(380, 540-60);
		list.setLocation(10, 50);
		list.layout();
	}
	
	@Override
	public void layout() {
		setLocation(
				Hexpansio.instance.getWidth()-Hexpansio.instance.tileInfo.getWidth()-getWidth()-20,
				Hexpansio.instance.bottom.getY()-getHeight()-20);
		super.layout();
	}

	@Override
	public void paint(GraphAssist g) {
		if(checkUpdate())
			super.paint(g);
	}
	
	public boolean checkUpdate() {
		World world = Hexpansio.getWorld();
		long worldUpdate = world.getLastUpdate();
		if(lastUpdate!=worldUpdate) {
			updateList();
			lastUpdate = worldUpdate;
		}
		return !empty;
	}
	
	public boolean isEmpty() {
		return empty;
	}
	
}
