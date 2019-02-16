package com.xrbpowered.hexpansio.genmanual;

import static com.xrbpowered.hexpansio.genmanual.GenManual.*;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.world.city.build.BuildSettlement;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.TokenResource;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.TerrainType.Feature;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;

public class ImpManual {

	public static final String name = "improvements";

	public static String impLink(Improvement imp) {
		return String.format("<a style=\"color:#fff\" href=\"%s.html#%s\">%s</a>", name, imp.id, imp.name);
	}
	
	private static String resourceList(Improvement imp) {
		ArrayList<TokenResource> list = new ArrayList<>();
		for(int i=0; i<TokenResource.objectIndex.size(); i++) {
			TokenResource res = TokenResource.objectIndex.get(i);
			if(res.improvement==imp)
				list.add(res);
		}
		if(!list.isEmpty()) {
			StringBuilder sb = new StringBuilder();
			boolean first = true;
			for(TokenResource res : list) {
				if(!first)
					sb.append(", ");
				else
					first = false;
				sb.append(TokenResManual.resLink(res));
			}
			return sb.toString();
		}
		else
			return null;
	}
	
	private static void producesResources(PrintStream out, Improvement imp) {
		if(imp.prerequisite!=null)
			return;
		String res = resourceList(imp);
		if(res!=null)
			out.println("<p>Can produce resources: "+res+"</p>");
	}
	
	private static boolean reqCoastalCity(Improvement imp) {
		if(imp.reqCoastalCity)
			return true;
		if(imp.reqFeatures!=null) {
			for(Feature f : imp.reqFeatures)
				if(f==Feature.water)
					return true;
		}
		return false;
	}

	private static boolean mayReqCoastalCity(Improvement imp) {
		if(imp.reqFeatures!=null)
			return false;
		if(imp.rejectFeatures!=null) {
			for(Feature f : imp.rejectFeatures)
				if(f==Feature.water)
					return false;
		}
		return true;
	}

	private static ArrayList<Feature> combineReqFeatures(Improvement imp, ArrayList<Feature> list) {
		if(list==null) {
			list = new ArrayList<>();
			if(imp.reqFeatures!=null) {
				for(Feature f : imp.reqFeatures)
					list.add(f);
			}
		}
		else {
			if(imp.reqFeatures!=null) {
				for(Feature f : imp.reqFeatures)
					list.remove(f);
			}
		}
		if(imp.prerequisite!=null)
			combineReqFeatures(imp.prerequisite, list);
		return list;
	}
	
	private static ArrayList<Feature> combineRejectFeatures(Improvement imp, ArrayList<Feature> list) {
		if(list==null) {
			list = new ArrayList<>();
			if(imp.rejectFeatures!=null) {
				for(Feature f : imp.rejectFeatures)
					list.add(f);
			}
		}
		else {
			if(imp.rejectFeatures!=null) {
				for(Feature f : imp.rejectFeatures)
					list.remove(f);
			}
		}
		if(imp.prerequisite!=null)
			combineRejectFeatures(imp.prerequisite, list);
		return list;
	}
	
	public static void listRequirements(PrintStream out, Improvement imp) {
		if(imp.prerequisite!=null) {
			out.printf("Requires %s<br/>\n", impLink(imp.prerequisite));
		}
		if(imp.cityPrerequisite!=null) {
			out.printf("Requires %s in the city<br/>\n", impLink(imp.cityPrerequisite));
		}
		if(imp.reqFeatures!=null) {
			ArrayList<Feature> list = combineReqFeatures(imp, null);
			if(!list.isEmpty())
				out.printf("Requires %s terrain<br/>\n", TerrainManual.formatListFeatures(list));
		}
		else if(imp.rejectFeatures!=null) {
			ArrayList<Feature> list = combineRejectFeatures(imp, null);
			if(!list.isEmpty())
				out.printf("Cannot be built on %s terrain<br/>\n", TerrainManual.formatListFeatures(list));
		}
		if(imp.reqResource) {
			String res = resourceList(imp.getBase());
			if(res!=null)
				out.printf("Requires appropriate resource (%s)<br/>\n", res);
		}
		if(reqCoastalCity(imp) || reqCoastalCity(imp.getBase())) {
			out.println("Requires coastal city<br/>");
		}
		else if(mayReqCoastalCity(imp.getBase()) && mayReqCoastalCity(imp)) {
			out.printf("Building on %s terrain requires coastal city<br/>", TerrainManual.featureLink(Feature.water));
		}
		if(imp.reqPopulation>0) {
			out.printf("Requires %d population<br/>", imp.reqPopulation);
		}
	}
	
	public static ArrayList<Improvement> listImprovements(PrintStream out, String section, Improvement prereq) {
		if(section!=null) {
			out.print("<h3>");
			if(prereq!=null)
				out.printf("<span id=\"%s.upg\"></span>", prereq.id);
			out.print(section);
			out.println("</h3>");
		}
		
		out.println("<table class=\"layout\" style=\"width:100%\">");
		ArrayList<Improvement> list = new ArrayList<>();
		for(int i=0; i<Improvement.objectIndex.size(); i++) {
			Improvement imp = Improvement.objectIndex.get(i);
			if(imp.prerequisite!=null && imp.prerequisite.getBase()!=prereq || imp.prerequisite==null && prereq!=null)
				continue;
			list.add(imp);
			out.println("<tr id=\""+imp.id+"\"><td style=\"width:160px\">");
			out.print("<p style=\"margin-top:0\"><b>"+imp.name+"</b>");
			if(imp.glyph!=null)
				out.print("<br/><span style=\"font-size:18pt\">"+imp.glyph+"</span>");
			out.println("</p>");
			out.println("</td><td>");
			
			if(imp==Improvement.cityCenter) {
				out.printf("<p style=\"margin-top:0\">Use Settler to build for <span title=\"Production/Goods\" style=\"color:%s\">&#11044;</span>&thinsp;%d</p>", color(YieldResource.production.fill), BuildSettlement.cost);
			}
			else {
				out.printf("<p style=\"margin-top:0\">Build cost: <span title=\"Production/Goods\" style=\"color:%s\">&#11044;</span>&thinsp;%d", color(YieldResource.production.fill), imp.buildCost);
				if(imp.cityUnique)
					out.print("<br/>City unique");
				if(!imp.canHurry)
					out.print("<br/>Cannot hurry construction");
				if(imp.upgPoints>0)
					out.printf("<br/>Upg. Points required: %d", imp.upgPoints);
				out.println("</p>");
			}
			producesResources(out, imp);
			
			if(imp.maintenance>0) {
				out.printf("<p class=\"info\">Maintenance: <span title=\"Gold\" style=\"color:%s\">&#11044;</span><span style=\"color:#f00\">&thinsp;%d</span> gold</p>",
						color(YieldResource.gold.fill), -imp.maintenance);
			}
			if(imp.workplaces!=0)
				out.printf("<p class=\"info\">%s</p>", EffectTarget.formatPluralDelta(imp.workplaces, "workplace", true));
			
			String yield = GenManual.formatYield(imp.yield, "<br/>", null);
			String yieldPerWorker = GenManual.formatYield(imp.yieldPerWorker, " per worker", "<br/>", null);
			if(yield!=null && yieldPerWorker!=null)
				out.printf("<p>%s<br/>%s</p>\n", yield, yieldPerWorker);
			else if(yield!=null)
				out.printf("<p>%s</p>\n", yield);
			else if(yieldPerWorker!=null)
				out.printf("<p>%s</p>\n", yieldPerWorker);

			if(imp.bonusResources>0)
				out.printf("<p class=\"info\">%s harvested</p>", EffectTarget.formatPluralDelta(imp.bonusResources, "Resource", true));

			if(imp.effect!=null) {
				out.println("<p class=\"info\"><span style=\"color:#fff\">City-wide effects:</span>");
				String[] desc = imp.effect.getDescription().split("\\n");
				for(String s : desc)
					out.println("<br/>"+s);
				out.println("</p>");
			}
			
			out.println("<p class=\"dark\">");
			listRequirements(out, imp);
			out.println("</p>");
			
			if(imp.hotkey!=0)
				out.printf("<p class=\"info\">Hotkey: %s</p>", KeyEvent.getKeyText(imp.hotkey));
			
			if(prereq==null)
				out.printf("<p><a href=\"#%s.upg\">View upgrades</a></p>", imp.id);
			
			out.println("</td></tr>\n");
		}
		out.println("</table>");
		if(list.isEmpty())
			out.println("<p class=\"dark\">No upgrades</p>");
		return list;
	}
	
	public static void generate() throws IOException {
		PrintStream out = GenManual.createHtml(name, "Tile Improvements");
		
		ArrayList<Improvement> base = listImprovements(out, "Basic Improvements", null);
		for(Improvement prereq : base) {
			listImprovements(out, prereq.name+" Upgrades", prereq);
		}
		
		GenManual.finishHtml(out);
		System.out.println("Generated "+name+".html");
	}
}
