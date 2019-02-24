package com.xrbpowered.hexpansio.res;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.world.resources.Happiness;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIArrowButton;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIScrollBar;
import com.xrbpowered.zoomui.std.UIScrollContainer;
import com.xrbpowered.zoomui.std.text.UITextBox;

public class Res {

	public static final Font font;
	public static final Font fontTiny;
	public static final Font fontBold;
	public static final Font fontLarge;
	public static final Font fontHuge;
	static {
		String fontPathFormat = "com/xrbpowered/hexpansio/res/fonts/Montserrat-%s.ttf";
		Font f, fb;
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(String.format(fontPathFormat, "Medium"));
			f = Font.createFont(Font.TRUETYPE_FONT, in);
			in.close();
			in = ClassLoader.getSystemResourceAsStream(String.format(fontPathFormat, "Bold"));
			fb = Font.createFont(Font.TRUETYPE_FONT, in);
			in.close();
		}
		catch (IOException | FontFormatException e) {
			e.printStackTrace();
			f = new Font("Sans", Font.PLAIN, 15);
			fb = new Font("Sans", Font.BOLD, 15);
		}
		float fontHeight = GraphAssist.ptToPixels(9f);
		float fontLargeHeight = GraphAssist.ptToPixels(14f);
		font = f.deriveFont(fontHeight);
		fontTiny = f.deriveFont(9f);
		fontBold = fb.deriveFont(fontHeight);
		fontLarge = f.deriveFont(fontLargeHeight);
		fontHuge = f.deriveFont((float)GraphAssist.ptToPixels(24f));
	}

	public static final int imgSize = 80;
	public static final ImageAtlas imgHappiness = new ImageAtlas(loadImage("happiness"), imgSize);
	public static final ImageAtlas imgRes = new ImageAtlas(loadImage("res"), imgSize);
	
	public static BufferedImage loadImage(String name) {
		String imgPathFormat = "com/xrbpowered/hexpansio/res/img/%s.png";
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(String.format(imgPathFormat, name));
			return ImageIO.read(in);
		}
		catch(IOException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static final Color uiBgColor = new Color(0x001122);
	public static final Color uiBgMid = new Color(0x112233);
	public static final Color uiBorderDark = new Color(0x445566);
	public static final Color uiBgBright = new Color(0x336699);
	public static final Color uiButtonTop = new Color(0x77bbff);
	public static final Color uiBorderLight = new Color(0xbbddff);

	public static void restyleStdControls() {
		UIListBox.colorBackground = Color.BLACK;
		UIListBox.colorBorder = uiBorderDark;
		UIScrollContainer.colorBorder = uiBorderDark;
		UIArrowButton.colorArrow = Color.WHITE;
		UIArrowButton.colorArrowDisabled = Color.GRAY;
		UIArrowButton.colorHover = uiBorderDark;
		UIScrollBar.colorBg = uiBgMid;
		UIScrollBar.colorBorder = uiBorderDark;
		UIButton.colorGradTop = uiButtonTop;
		UIButton.colorGradBottom = uiBgBright;
		UIButton.colorBorder = uiBorderDark;
		UIButton.colorText = uiBorderDark;
		UIButton.colorDown = uiBgBright;
		UITextBox.font = fontLarge;
		UITextBox.colorBackground = Color.BLACK;
		UITextBox.colorText = Color.WHITE;
		UITextBox.colorSelection = Color.WHITE;
		UITextBox.colorSelectedText = Color.BLACK;
		UITextBox.colorBorder = uiBorderLight;
	}
	
	public static void paintProgress(GraphAssist g, YieldResource res, int prog, int total, int add, int x, int y, float w, float h, int halign) {
		float wp = Math.round(w*prog/(float)total);
		int next = MathUtils.snap(prog+add, 0, total);
		float wadd = Math.round(w*next/(float)total);
		float tx = x - GraphAssist.align(w, halign);
		g.fillRect(tx, y, w, h, res.border);
		if(add<0) {
			g.fillRect(tx, y, wp, h, Color.RED);
			g.fillRect(tx, y, wadd, h, res.fill);
		}
		else {
			g.fillRect(tx, y, wadd, h, res.dark);
			g.fillRect(tx, y, wp, h, res.fill);
		}
	}
	
	public static float paintCost(GraphAssist g, YieldResource res, String prefix, String cost, String postfix,
			Color costColor, float x, float y, int halign, int valign) {
		FontMetrics fm = g.graph.getFontMetrics();
		float wstart = prefix==null ? 0 : fm.stringWidth(prefix);
		float ws = fm.stringWidth(cost);
		float wend = postfix==null ? 0 : fm.stringWidth(postfix);
		float h = fm.getAscent() - fm.getDescent();
		float w = wstart+ws+wend+h*1.5f;
		float tx = x - GraphAssist.align(w, halign);
		float ty = y + h - GraphAssist.align(h, valign);
		
		if(prefix!=null)
			g.graph.drawString(prefix, tx, ty);
		if(postfix!=null)
			g.graph.drawString(postfix, tx+wstart+ws+h*1.5f, ty);
		g.setColor(costColor);
		g.graph.drawString(cost, tx+wstart+h*1.5f, ty);
		
		g.setColor(res.fill);
		g.graph.fillOval((int)(tx+wstart+h*0.25f), (int)(ty-h), (int)h, (int)h);
		
		return w;
	}

	public static float paintCost(GraphAssist g, YieldResource res, String prefix, int cost, String postfix,
			int avail, float x, float y, int halign, int valign) {
		return paintCost(g, res, prefix, Integer.toString(cost), postfix, cost>avail ? Color.RED : Color.WHITE, x, y, halign, valign);
	}

	public static float paintIncome(GraphAssist g, YieldResource res, String prefix, int income, String postfix,
			float x, float y, int halign, int valign) {
		return paintCost(g, res, prefix, String.format("%+d", income), postfix, income<0 ? Color.RED : income==0 ? Color.LIGHT_GRAY : Color.WHITE, x, y, halign, valign);
	}

	public static float paintLimit(GraphAssist g, YieldResource res, String prefix, int value, int limit, float x, float y, int halign, int valign) {
		return paintCost(g, res, prefix, String.format("%d / %d", value, limit), null, value<limit ? Color.WHITE : Color.YELLOW, x, y, halign, valign);
	}

	public static void paintToken(GraphAssist g, float scale, ImageAtlas atlas, int subImage) {
		g.pushPureStroke(true);
		if(scale>1.25f) {
			int x = -MapView.w+MapView.a;
			int y = -MapView.h;
			g.resetStroke();
			g.fillRect(x, y+5, 10, 10);
			g.drawRect(x, y+5, 10, 10);
			atlas.draw(g, x-10, y-5, 20, subImage);
			g.graph.drawOval(x-10, y-5, 20, 20);
		}
		else {
			int r = MapView.h-5;
			g.setStroke(2f);
			atlas.draw(g, -r, -r, r*2, subImage);
			g.graph.drawOval(-r, -r, r*2, r*2);
		}
		g.popPureStroke();
	}
	
	public static int calcTurns(int prog, int total, int add) {
		return add<=0 ? -1 : (int)Math.ceil((total-prog)/(float)add);
	}

	public static String calcTurnsStr(int prog, int total, int add, String stall) {
		int n = calcTurns(prog, total, add);
		return n<0 ? stall : n==1 ? "(1 turn)" : String.format("(%d turns)", n);
	}
	
	public static void paintWorkerBubble(GraphAssist g, int x, int y, int size, Color fill, Color border) {
		int r = size/2;
		g.setColor(fill);
		g.graph.fillOval(x-r, y-r, size, size);
		g.setColor(border);
		g.graph.drawOval(x-r, y-r, size, size);
	}

	public static void paintWorkerBubbles(GraphAssist g, int x, int y, int size, int count, int total, boolean employed, int halign) {
		g.pushPureStroke(true);
		int dx = size*2/3;
		int w = size+dx*(total-1);
		int tx = x - (int)GraphAssist.align(w, halign) + size/2;
		Color fill = employed ? new Color(0x88ddbb) : new Color(0xdd0000);
		Color border = employed ? new Color(0xaaffdd) : new Color(0xeeaaaa);
		Color fillEmpty = new Color(0x333333);
		Color borderEmpty = new Color(0x555555);
		for(int i=total-1; i>=0; i--) {
			paintWorkerBubble(g, tx+i*dx, y, size, (i<count) ? fill : fillEmpty, (i<count) ? border : borderEmpty);
		}
		g.popPureStroke();
	}
	
	public static void paintTooltip(GraphAssist g, float x, float y, String s, int valign) {
		g.setFont(Res.font);
		FontMetrics fm = g.graph.getFontMetrics();
		float w = fm.stringWidth(s)+20;
		float ty = valign==GraphAssist.BOTTOM ? y : y - 25;
		g.fillRect(x-w/2, ty, w, 25, Color.BLACK);
		g.resetStroke();
		g.drawRect(x-w/2, ty, w, 25, Color.LIGHT_GRAY);
		g.setColor(Color.WHITE);
		g.drawString(s, x, ty+12.5f, GraphAssist.CENTER, GraphAssist.CENTER);
	}
	
	public static void paintHappiness(GraphAssist g, int x, int y, Happiness happiness) {
		int r = 15;
		g.setColor(Color.DARK_GRAY);
		g.resetStroke();
		imgHappiness.draw(g, x-r, y-r, r*2, happiness.ordinal());
		g.graph.drawOval(x-r, y-r, r*2, r*2);
	}

}
