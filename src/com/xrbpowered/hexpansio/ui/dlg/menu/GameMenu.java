package com.xrbpowered.hexpansio.ui.dlg.menu;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.LinearGradientPaint;
import java.awt.Paint;
import java.awt.Shape;
import java.awt.font.GlyphVector;
import java.awt.geom.AffineTransform;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.dlg.OverlayDialog;
import com.xrbpowered.hexpansio.ui.dlg.QuickExitDialog;
import com.xrbpowered.zoomui.GraphAssist;

public class GameMenu extends OverlayDialog {

	private static final int buttonWidth = 240;
	private static final int buttonHeight = 45;
	
	private final ClickButton resumeButton;
	private final ClickButton newButton;
	private final ClickButton saveButton;
	private final ClickButton loadButton;
	private final ClickButton settingsButton;
	private final ClickButton exitButton;

	public GameMenu() {
		super(Hexpansio.instance.getBase(), buttonWidth+200, buttonHeight*6+260, null);
		
		int y = 180;
		resumeButton = new ClickButton(box, "RESUME", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public boolean isEnabled() {
				return Hexpansio.getWorld()!=null || Hexpansio.currentSave!=null;
			}
			@Override
			public void onClick() {
				if(!isEnabled())
					return;
				if(Hexpansio.getWorld()==null)
					Hexpansio.instance.loadGame(Hexpansio.currentSave);
				dismissNow();
			}
		};
		resumeButton.setLocation(100, y);
		y += resumeButton.getHeight() + 5;

		newButton = new ClickButton(box, "START NEW", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				new WorldSettingsDialog();
				dismissNow();
			}
		};
		newButton.setLocation(100, y);
		y += newButton.getHeight() + 5;

		saveButton = new ClickButton(box, "SAVE", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public boolean isEnabled() {
				return Hexpansio.getWorld()!=null;
			}
			@Override
			public void onClick() {
				if(!isEnabled())
					return;
				dismissNow();
				new SaveDialog(true, Hexpansio.currentSave).repaint();
			}
		};
		saveButton.setLocation(100, y);
		y += saveButton.getHeight() + 5;
		
		loadButton = new ClickButton(box, "LOAD", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				if(!isEnabled())
					return;
				dismissNow();
				SaveDialog.load().repaint();
			}
		};
		loadButton.setLocation(100, y);
		y += loadButton.getHeight() + 5;

		settingsButton = new ClickButton(box, "SETTINGS", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				dismissNow();
				new GlobalSettingsDialog();
				repaint();
			}
		};
		settingsButton.setLocation(100, y);
		y += settingsButton.getHeight() + 5;


		exitButton = new ClickButton(box, "EXIT", buttonWidth, buttonHeight, Res.fontLarge) {
			@Override
			public void onClick() {
				new QuickExitDialog().repaint();
			}
		};
		exitButton.setLocation(100, y);
	}
	
	private void dismissNow() {
		super.dismiss();
	}
	
	@Override
	public void dismiss() {
		if(Hexpansio.getWorld()!=null)
			super.dismiss();
	}
	
	private static Paint titleBg = null;
	private static Shape titleText = null;
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		int y = 90;
		
		if(titleBg==null)
			titleBg = new LinearGradientPaint(0, y-120, 0, y+120, new float[] {0, 0.5f, 1}, new Color[] {Res.uiBgColor, Res.uiBgBright, Res.uiBgColor});
		g.setPaint(titleBg);
		g.fillRect(0, 0, box.getWidth(), y+120);
		
		if(titleText==null) {
			Font font = Res.fontHuge.deriveFont(Res.fontHuge.getSize()*1.75f);
			String text = "HEXPANSIO";
			FontMetrics fm = g.graph.getFontMetrics(font);
			GlyphVector v = font.createGlyphVector(fm.getFontRenderContext(), text);
	        titleText = v.getOutline();
	        
	        float w = fm.stringWidth(text);
			float h = fm.getAscent() - fm.getDescent();
			float tx = box.getWidth()/2f - w/2f;
			float ty = y + h/2f;
			AffineTransform t = AffineTransform.getTranslateInstance(tx, ty);
			titleText = t.createTransformedShape(titleText);
		}

		g.pushPureStroke(true);
		g.setColor(new Color(0x44000000, true));
		g.graph.setStroke(new BasicStroke(5f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
		g.graph.draw(titleText);
		g.setColor(Color.WHITE);
		g.graph.fill(titleText);
		g.pushPureStroke(false);
		
		super.paintBoxContents(g);
	}

}
