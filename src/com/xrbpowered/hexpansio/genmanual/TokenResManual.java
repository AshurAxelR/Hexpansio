package com.xrbpowered.hexpansio.genmanual;

import java.io.IOException;
import java.io.PrintStream;

import com.xrbpowered.hexpansio.world.resources.TokenResource;

public class TokenResManual {

	public static final String name = "resources";
	
	private static int cols = 10;
	private static int size = 80;
	
	private static void resImg(PrintStream out, TokenResource res) {
		int x = res.subImage % cols;
		int y = res.subImage / cols;
		out.printf("<img alt=\"%s\" title=\"%s\" src=\"res.png\" "
				+ "style=\"object-fit:none;object-position:%dpx %dpx; width:%dpx;height:%dpx\" />", res.name, res.name, -x*size, -y*size, size, size);
	}
	
	public static String resLink(TokenResource res) {
		return String.format("<a style=\"color:#fff;font-weight:bold\" href=\"%s.html#%s\">%s</a>", name, res.id, res.name);
	}
	
	public static void listTokenResources(PrintStream out) {
		out.println("<table class=\"layout\" style=\"width:100%\">");
		for(int i=0; i<TokenResource.objectIndex.size(); i++) {
			TokenResource res = TokenResource.objectIndex.get(i);
			out.println("<tr id=\""+res.id+"\"><td class=\"img\">");
			resImg(out, res);
			out.println("</td><td>");
			out.println("<p style=\"margin-top:0\"><b>"+res.name+"</b><br/>Requires "+ImpManual.impLink(res.improvement)+"</p>");
			out.printf("<p>%s</p>\n", GenManual.formatYield(res.yield, "<br/>", "&nbsp;"));
			out.printf("<p class=\"info\">Terrain bonus: %s</p>\n", GenManual.formatYield(res.terrainBonus, ", ", "none"));
			
			out.println("</td></tr>\n");
		}
		out.println("</table>");
	}
	
	public static void generate() throws IOException {
		PrintStream out = GenManual.createHtml(name, "Resources");
		
		listTokenResources(out);
		
		GenManual.finishHtml(out);
		System.out.println("Generated "+name+".html");
	}
	
}
