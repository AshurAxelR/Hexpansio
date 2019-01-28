package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Polygon;
import java.awt.Shape;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.TurnEventMessage;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class MessageLogDialog extends OverlayDialog {

	public static boolean active = false;
	
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
				if(msg.focusTile!=null) {
					TileMode.instance.selectTile(msg.focusTile, true);
					dismiss();
				}
				else
					repaint();
				return true;
			}
			else
				return false;
		}
	}

	private UIListBox list;

	private final ClickButton closeButton;

	public MessageLogDialog() {
		super(Hexpansio.instance.getBase(), 400, 600, "MESSAGES");
		
		World world = Hexpansio.getWorld();
		ArrayList<TurnEventMessage> msgList = new ArrayList<TurnEventMessage>(world.events);
		for(City city : world.cities)
			city.addPinnedMessages(msgList);
		
		list = new UIListBox(box,  msgList.toArray(new TurnEventMessage[msgList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new MessageListItem(this, index, object);
			}
		};
		list.setSize(380, 600-60-60);
		list.setLocation(10, 60);
		
		list.select(0);
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
		
		active = true;
	}

	@Override
	public void layout() {
		box.setLocation(
				getWidth()-Hexpansio.instance.tileInfo.getWidth()-box.getWidth()-20,
				Hexpansio.instance.bottom.getY()-box.getHeight()-20);
		box.layout();
	}

	public static boolean isEnabled() {
		World world = Hexpansio.getWorld();
		return (world!=null && (!world.events.isEmpty() || world.problematicCities>0));
	}
	
	@Override
	public void dismiss() {
		active = false;
		super.dismiss();
	}
	
}
