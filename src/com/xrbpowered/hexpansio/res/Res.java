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
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIButton;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIScrollBar;
import com.xrbpowered.zoomui.std.UIScrollContainer;

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
	public static final BufferedImage imgHappiness = loadImage("happiness");
	public static final BufferedImage imgRes = loadImage("res");
	
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
		UIScrollContainer.colorBorder = uiBorderDark;
		UIScrollBar.colorArrow = Color.WHITE;
		UIScrollBar.colorArrowDisabled = Color.GRAY;
		UIScrollBar.colorBg = uiBgMid;
		UIScrollBar.colorBorder = uiBorderDark;
		UIButton.colorGradTop = uiButtonTop;
		UIButton.colorGradBottom = uiBgBright;
		UIButton.colorBorder = uiBorderDark;
		UIButton.colorText = uiBorderDark;
		UIButton.colorDown = uiBgBright;
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
	
	public static void paintCost(GraphAssist g, YieldResource res, String prefix, String cost, String postfix,
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
	}

	public static void paintCost(GraphAssist g, YieldResource res, String prefix, int cost, String postfix,
			int avail, float x, float y, int halign, int valign) {
		paintCost(g, res, prefix, Integer.toString(cost), postfix, cost>avail ? Color.RED : Color.WHITE, x, y, halign, valign);
	}

	public static void paintIncome(GraphAssist g, YieldResource res, String prefix, int income, String postfix,
			float x, float y, int halign, int valign) {
		paintCost(g, res, prefix, String.format("%+d", income), postfix, income<0 ? Color.RED : income==0 ? Color.GRAY : Color.WHITE, x, y, halign, valign);
	}

	public static void paintToken(GraphAssist g, float scale, BufferedImage image, int subImage) {
		g.pushPureStroke(true);
		if(scale>1.25f) {
			int x = -MapView.w+MapView.a;
			int y = -MapView.h;
			g.resetStroke();
			g.fillRect(x, y+5, 10, 10);
			g.drawRect(x, y+5, 10, 10);
			g.graph.drawImage(image, x-10, y-5, x+10, y+15, subImage*imgSize, 0, (subImage+1)*imgSize, imgSize, null);
			g.graph.drawOval(x-10, y-5, 20, 20);
		}
		else {
			int r = MapView.h-5;
			g.setStroke(2f);
			g.graph.drawImage(image, -r, -r, r, r, subImage*imgSize, 0, (subImage+1)*imgSize, imgSize, null);
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

}
