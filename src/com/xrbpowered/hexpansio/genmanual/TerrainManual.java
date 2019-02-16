package com.xrbpowered.hexpansio.genmanual;

import static com.xrbpowered.hexpansio.genmanual.GenManual.*;
import static com.xrbpowered.hexpansio.ui.MapView.*;

import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;

public class TerrainManual {

	public static final String name = "terrain";
	
	private static final String svgHexagon = String.format(
			"<svg width=\"%d\" height=\"%d\">\n<g transform=\"scale(1.5)\">\n<polygon points=\"%d,%d %d,%d %d,%d %d,%d %d,%d %d,%d\" ",
			Math.round((w+a*2)*1.5), Math.round((h*2)*1.5),
			0, h, a, 0, a+w, 0, 2*a+w, h, a+w, h*2, a, h*2);
	
	private static void paintYieldItem(PrintStream out, int x, int y) {
		out.printf("<circle cx=\"%d\" cy=\"%d\" r=\"%d\" />", x, y, 3);
	}
	
	private static void terrainSvg(PrintStream out, TerrainType terrain) {
		out.print(svgHexagon);
		out.printf("style=\"fill:%s;stroke:none\" />\n", color(terrain.color));

		int dxs = (w+a*2) / (terrain.yield.countTypes()+1);
		int xs = dxs;
		for(YieldResource res : YieldResource.values()) {
			int count = terrain.yield.get(res);
			int x = xs;
			int y = h;
			if(count >0) {
				out.printf("<g style=\"fill:%s;stroke:%s;stroke-width:1px\">\n", color(res.fill), color(res.border));
				switch(count) {
					case 1:
						paintYieldItem(out, x, y);
						break;
					case 2:
						paintYieldItem(out, x, y-3);
						paintYieldItem(out, x, y+3);
						break;
					case 3:
						paintYieldItem(out, x, y-3);
						paintYieldItem(out, x-3, y+3);
						paintYieldItem(out, x+3, y+3);
						break;
					case 4:
						paintYieldItem(out, x-3, y-3);
						paintYieldItem(out, x-3, y+3);
						paintYieldItem(out, x+3, y-3);
						paintYieldItem(out, x+3, y+3);
						break;
				}
				out.println("</g>");
				
				xs += dxs;
			}
		}

		out.println("</g>\n</svg>");
	}

	public static String featureLink(Feature f) {
		return String.format("<a href=\"%s.html#%s\">%s</a>", name, f.name(), f.name);
	}
	
	public static String formatListFeatures(ArrayList<Feature> features) {
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<features.size(); i++) {
			if(i>0) {
				if(features.size()==2)
					sb.append(" or ");
				else if(i==features.size()-1)
					sb.append(", or ");
				else
					sb.append(", ");
			}
			sb.append(featureLink(features.get(i)));
		}
		return sb.toString();
	}
	
	public static void listTerrains(PrintStream out, String section, Feature[] filter) {
		if(section!=null) {
			out.print("<h3>");
			if(filter!=null) {
				for(Feature f : filter)
					out.printf("<span id=\"%s\"></span>", f.name());
			}
			out.print(section);
			out.println("</h3>");
		}
		out.println("<table class=\"layout\" style=\"width:100%\">");
		for(int i=0; i<TerrainType.objectIndex.size(); i++) {
			TerrainType terrain = TerrainType.objectIndex.get(i);
			if(terrain.feature==null && filter!=null || terrain.feature!=null && !terrain.hasFeature(filter))
				continue;
			out.println("<tr id=\""+terrain.id+"\"><td class=\"img\">");
			terrainSvg(out, terrain);
			out.println("</td><td>");
			out.print("<p style=\"margin-top:0\"><b>"+terrain.name+"</b>");
			if(terrain.feature!=null)
				out.printf("<br/><span style=\"color:%s\">%s</span>", color(terrain.feature.color), terrain.feature.name);
			out.println("</p>");
			String yield = GenManual.formatYield(terrain.yield, "<br/>", null);
			if(yield!=null)
				out.printf("<p>%s</p>\n", yield);
			
			if(terrain.workplaces==0)
				out.println("<p class=\"dark\">Not workable</p>");
			else if(terrain.workplaces>1)
				out.printf("<p class=\"info\">Workplaces: %d</p>\n", terrain.workplaces);
			
			if(!terrain.canSettleOn())
				out.println("<p class=\"dark\">Can't settle on this terrain</p>");
			
			if(terrain.tokens!=null && terrain.tokens.length>0) {
				out.print("<p class=\"info\">Resources:<br/>");
				int wtotal = 0;
				for(int j=0; j<terrain.tokens.length; j++)
					wtotal += terrain.wtokens[j];
				for(int j=0; j<terrain.tokens.length; j++) {
					if(j>0) out.print(", ");
					out.printf("%s&nbsp;(%.1f%%)", TokenResManual.resLink(terrain.tokens[j]), terrain.wtokens[j]*terrain.tokenChance*100f/(float)wtotal);
				}
				out.println("</p>");
			}
			
			out.print("<p class=\"info\">Buildable improvements:<br/>");
			boolean first = true;
			for(int j=0; j<Improvement.objectIndex.size(); j++) {
				Improvement imp = Improvement.objectIndex.get(j);
				if(imp!=Improvement.cityCenter && imp.prerequisite==null && imp.canBuildOn(terrain)) {
					if(!first)
						out.print(", ");
					else
						first = false;
					out.print(ImpManual.impLink(imp));
				}
			}
			if(first)
				out.println("none");
			else
				out.println();
			
			out.println("</td></tr>\n");
		}
		out.println("</table>");
	}
	
	public static void generate() throws IOException {
		PrintStream out = GenManual.createHtml(name, "Terrain Types");
		
		listTerrains(out, null, null);
		listTerrains(out, "Water terrain", new Feature[] {Feature.water});
		listTerrains(out, "Forest and Swamp terrain", new Feature[] {Feature.forest, Feature.swamp});
		listTerrains(out, "Desert terrain", new Feature[] {Feature.desert});
		listTerrains(out, "Mountains, peaks, and volcanoes", new Feature[] {Feature.mountain, Feature.peak, Feature.volcano});
		listTerrains(out, "Special terrain", new Feature[] {Feature.thevoid, Feature.ruins});
		
		GenManual.finishHtml(out);
		System.out.println("Generated "+name+".html");
	}
	
}
