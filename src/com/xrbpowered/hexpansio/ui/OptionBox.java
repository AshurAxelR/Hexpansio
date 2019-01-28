package com.xrbpowered.hexpansio.ui;

import java.awt.Color;

import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;

public class OptionBox extends UIContainer {

	public static final int defaultWidth = 180;
	public static final int defaultHeight = 30;
	
	private class FlipButton extends ArrowButton {
		public FlipButton(OptionBox parent, int delta) {
			super(parent, delta, OptionBox.defaultHeight-6, OptionBox.defaultHeight, OptionBox.defaultHeight-6);
		}
		
		@Override
		public void onClick() {
			if(isEnabled()) {
				optionIndex = (optionIndex + delta + options.length) % options.length;
				selectOption(options[optionIndex]);
				repaint();
			}
		}
		
		@Override
		public void onMouseIn() {
			((OptionBox) getParent()).hover = true;
			super.onMouseIn();
		}
		
		@Override
		public void onMouseOut() {
			((OptionBox) getParent()).hover = false;
			super.onMouseOut();
		}
	}
	
	public final int[] options;
	public String label, format;
	
	public int optionIndex = 0;
	
	public boolean hover = false;
	
	private final FlipButton left, right;

	public OptionBox(UIContainer parent, String label, int[] options, String format) {
		super(parent);
		this.options = options;
		this.label = label;
		this.format = format;
		
		left = new FlipButton(this, -1);
		right = new FlipButton(this, 1);
		setSize(defaultWidth, defaultHeight);
	}

	public OptionBox(UIContainer parent, String label, int[] options) {
		this(parent, label, options, "%d");
	}
	
	@Override
	public void setSize(float width, float height) {
		super.setSize(width, height);
		left.setLocation(3, 0);
		right.setLocation(getWidth()-right.getWidth()-3, 0);
	}

	public int getSelectedValue() {
		return options[optionIndex];
	}
	
	public int findOption(int value) {
		optionIndex = 0;
		for(int i=0; i<options.length; i++) {
			if(options[i]==value) {
				optionIndex = i;
				return value;
			}
		}
		return options[0];
	}
	
	protected void selectOption(int value) {
	}
	
	protected String formatOption(int value) {
		return String.format(format, value);
	}
	
	@Override
	public void paintSelf(GraphAssist g) {
		g.fill(this, Color.BLACK);
		g.resetStroke();
		g.border(this, Res.uiBorderDark);
		
		g.setColor(Color.WHITE);
		g.setFont(Res.font);
		g.drawString(formatOption(getSelectedValue()), getWidth()/2f, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
		g.drawString(label, -20, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
	}
	
	@Override
	public void onMouseIn() {
		hover = true;
		repaint();
	}
	
	@Override
	public void onMouseOut() {
		hover = false;
		repaint();
	}
}
