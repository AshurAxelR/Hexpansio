package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.KeyInputHandler;
import com.xrbpowered.zoomui.UIContainer;

public class OverlayDialog extends UIContainer implements KeyInputHandler {

	private class Box extends UIContainer {
		public Box() {
			super(OverlayDialog.this);
		}
		@Override
		protected void paintSelf(GraphAssist g) {
			paintBoxBackground(g);
			paintBoxContents(g);
		}
		@Override
		public boolean onMouseDown(float x, float y, Button button, int mods) {
			return true;
		}
	}
	
	protected final UIContainer box;
	private final KeyInputHandler focus;
	public String title;
	
	public OverlayDialog(UIContainer parent, float width, float height, String title) {
		super(parent);
		this.title = title;
		focus = getBase().getFocus();
		getBase().resetFocus();
		
		this.box = new Box();
		box.setSize(width, height);
	}
	
	@Override
	public void layout() {
		box.setLocation((getWidth()-box.getWidth())/2f, (getHeight()-box.getHeight())/2f);
		super.layout();
	}

	@Override
	public void onFocusGained() {
	}
	
	@Override
	public void onFocusLost() {
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		if(code==KeyEvent.VK_ESCAPE) {
			dismiss();
			return true;
		}
		else if(code==KeyEvent.VK_ENTER) {
			onEnter();
			return true;
		}
		return false;
	}
	
	public void onEnter() {
		dismiss();
	}
	
	public void dismiss() {
		getParent().removeChild(this);
		getBase().setFocus(focus);
		repaint();
	}
	
	protected void paintBoxBackground(GraphAssist g) {
		g.setStroke(8f);
		g.border(box, Color.BLACK);
		g.fill(box, Res.uiBgColor);
		
		g.setPaint(new GradientPaint(0, box.getHeight()-60, Res.uiBgColor, 0, box.getHeight(), Res.uiBgBright));
		g.fillRect(0, box.getHeight()-60, box.getWidth(), 60);
		
		g.setColor(Color.WHITE);
		if(title!=null) {
			g.setFont(Res.fontLarge);
			g.drawString(title, box.getWidth()/2f, 30, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}
		g.setFont(Res.font);
	}

	protected void paintBoxContents(GraphAssist g) {
		g.resetStroke();
		g.border(box, Color.WHITE);
	}

	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		dismiss();
		return true;
	}
	
	@Override
	public boolean onMouseScroll(float x, float y, float delta, int mods) {
		return true;
	}
}
