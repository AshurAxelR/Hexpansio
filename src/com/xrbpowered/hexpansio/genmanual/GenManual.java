package com.xrbpowered.hexpansio.genmanual;

import java.awt.Color;
import java.io.File;
import java.io.IOException;
import java.io.PrintStream;

import com.xrbpowered.hexpansio.world.resources.Yield;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public class GenManual {

	public static final String basePath = "./manual";
	
	public static PrintStream createHtml(String name, String title) throws IOException {
		PrintStream out = new PrintStream(new File(basePath, name+".html"));
		out.println("<!DOCTYPE html>\n<html>\n<head>" +
				"<title>"+title+"</title>\n" +
				"<link href=\"manual.css\" rel=\"stylesheet\">\n" +
				"</head>\n<body><div id=\"body\">\n<h1>"+title+"</h1>");
		return out;
	}
	
	public static void finishHtml(PrintStream out) {
		out.println("</div></body>\n</html>");
		out.close();
	}
	
	public static String color(Color c) {
		return String.format("#%6x", c.getRGB() & 0xffffff);
	}

	public static String formatYield(Yield yield, String sep, String none) {
		return formatYield(yield, "", sep, none);
	}

	public static String formatYield(Yield yield, String postfix, String sep, String none) {
		StringBuilder sb = new StringBuilder();
		boolean first = true;
		for(YieldResource res : YieldResource.values()) {
			int count = yield.get(res);
			if(count!=0) {
				if(!first)
					sb.append(sep);
				else
					first = false;
				sb.append(String.format("<span style=\"color:%s\">%+d %s%s</span>", color(count>=0 ? res.fill : Color.RED), count, res.name, postfix));
			}
		}
		if(first)
			return none;
		else
			return sb.toString();
	}
	
	public static void main(String[] args) {
		try {
			TerrainManual.generate();
			TokenResManual.generate();
			ImpManual.generate();
			System.out.println("Done.");
		}
		catch(IOException e) {
			e.printStackTrace();
		}
	}

}
