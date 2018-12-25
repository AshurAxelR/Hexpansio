package com.xrbpowered.hexpansio.res;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.FontMetrics;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

import javax.imageio.ImageIO;

import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.utils.MathUtils;
import com.xrbpowered.zoomui.GraphAssist;

public class Res {

	public static final Font font;
	public static final Font fontTiny;
	public static final Font fontBold;
	public static final Font fontLarge;
	public static final Font fontHuge;
	public static final int fontHeight;
	public static final int fontLargeHeight;
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
		fontHeight = GraphAssist.ptToPixels(9f);
		fontLargeHeight = GraphAssist.ptToPixels(14f);
		font = f.deriveFont((float)fontHeight);
		fontTiny = f.deriveFont(9f);
		fontBold = fb.deriveFont((float)fontHeight);
		fontLarge = f.deriveFont((float)fontLargeHeight);
		fontHuge = f.deriveFont((float)GraphAssist.ptToPixels(24f));
	}

	public static final BufferedImage imgHappiness;
	public static final int imgSize = 80;
	static {
		String imgPathFormat = "com/xrbpowered/hexpansio/res/img/%s.png";
		BufferedImage img;
		try {
			InputStream in = ClassLoader.getSystemResourceAsStream(String.format(imgPathFormat, "happiness"));
			img = ImageIO.read(in);
			in.close();
		}
		catch(IOException e) {
			e.printStackTrace();
			img = null;
		}
		imgHappiness = img;
	}
	
	public static final Color uiBgColor = new Color(0x001122);
	public static final Color uiBorderDark = new Color(0x445566);

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
	
	public static void paintCost(GraphAssist g, YieldResource res, String prefix, int cost, String postfix,
			World world, float x, float y, int halign, int valign) {
		String s = Integer.toString(cost);
		
		FontMetrics fm = g.graph.getFontMetrics();
		float wstart = prefix==null ? 0 : fm.stringWidth(prefix);
		float ws = fm.stringWidth(s);
		float wend = postfix==null ? 0 : fm.stringWidth(postfix);
		float h = fm.getAscent() - fm.getDescent();
		float w = wstart+ws+wend+h*1.5f;
		float tx = x - GraphAssist.align(w, halign);
		float ty = y + h - GraphAssist.align(h, valign);
		
		if(prefix!=null)
			g.graph.drawString(prefix, tx, ty);
		if(postfix!=null)
			g.graph.drawString(postfix, tx+wstart+ws+h*1.5f, ty);
		g.setColor(cost>world.gold ? Color.RED : Color.WHITE);
		g.graph.drawString(s, tx+wstart+h*1.5f, ty);
		
		g.setColor(res.fill);
		g.graph.fillOval((int)(tx+wstart+h*0.25f), (int)(ty-h), (int)h, (int)h);
	}

}
