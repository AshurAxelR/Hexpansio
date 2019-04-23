package com.xrbpowered.hexpansio.genmanual;

import java.awt.event.KeyEvent;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;

import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;

public class HotkeysManual {

	public static final String name = "hotkeys";

	public static final String[][] globalHotkeys = {
		{"Esc", "Game menu"},
		{"Enter", "Next turn"},
		{"Tab", "Toggle message log"},
		{"F1", "Show history and statistics"},
		{"F2", "Show list of cities"},
		{"Up", "Maximum zoom out"},
		{"Down", "Maximum zoom in"},
		{"Left", "Select previous city"},
		{"Right", "Select next city"},
		{"Insert", "Hurry construction"},
		{"Delete", "Cancel construction"},
		{"Space", "Show build/upgrade dialog"},
		{"Backspace", "Remove tile improvement"}
	};
	
	public static void listKeys(PrintStream out, String[][] list) {
		for(String[] pair : list) {
			String key = pair[0];
			String desc = pair[1]; 
			out.printf("<tr><td style=\"width:0\"><b>%s</b></td><td class=\"info\">%s</td></tr>\n", key, desc);
		}
	}
	
	public static String[][] modeKeys() {
		int num = MapMode.modes.length;
		String[][] list = new String[num][2];
		for(int i=0; i<num; i++) {
			MapMode mode = MapMode.modes[i];
			list[i][0] = mode.keyName();
			list[i][1] = mode.label + " mode";
		}
		return list;
	}
	
	public static String[][] impKeys() {
		int num = Improvement.hotkeyMap.size();
		String[][] list = new String[num][2];
		ArrayList<Integer> keys = new ArrayList<>(Improvement.hotkeyMap.keySet());
		keys.sort(null);
		for(int i=0; i<num; i++) {
			int key = keys.get(i);
			list[i][0] = KeyEvent.getKeyText(key);
			list[i][1] = "Build "+Improvement.hotkeyMap.get(key).name;
		}
		return list;
	}
	
	public static void generate() throws IOException {
		PrintStream out = GenManual.createHtml(name, "Hotkeys");
		
		out.println("<table class=\"list\" style=\"width:100%\">");
		listKeys(out, globalHotkeys);
		listKeys(out, modeKeys());
		listKeys(out, impKeys());
		out.println("</table>");
		
		GenManual.finishHtml(out);
		System.out.println("Generated "+name+".html");
	}
}
