package com.xrbpowered.hexpansio.world.resources;

import java.awt.Color;
import java.util.ArrayList;
import java.util.HashMap;

import com.xrbpowered.zoomui.GraphAssist;

public class ResourcePile {

	public class Entry implements Comparable<Entry> {
		public final TokenResource resource;
		public int count = 0;
		
		public Entry(TokenResource res) {
			this.resource = res;
		}
		
		@Override
		public int compareTo(Entry o) {
			return this.resource.name.compareTo(o.resource.name);
		}
	}
	
	private HashMap<String, Entry> map = new HashMap<>();
	private ArrayList<Entry> sortedList = null;
	
	public Entry get(TokenResource res) {
		Entry e = map.get(res.name);
		if(e==null) {
			e = new Entry(res);
			map.put(res.name, e);
			sortedList = null;
		}
		return e;
	}
	
	public void clear() {
		map.clear();
		sortedList = null;
	}
	
	public boolean isEmpty() {
		return map.isEmpty();
	}
	
	public int add(TokenResource res, int count) {
		Entry e = get(res);
		e.count += count;
		if(e.count==0) {
			map.remove(res.name);
			sortedList = null;
		}
		return e.count;
	}
	
	public int remove(TokenResource res, int count) {
		return add(res, -count);
	}
	
	public ArrayList<Entry> getSortedList() {
		if(sortedList==null) {
			sortedList = new ArrayList<>();
			sortedList.addAll(map.values());
			sortedList.sort(null);
		}
		return sortedList;
	}
	
	public void paint(GraphAssist g, int x, int y, String format) {
		ArrayList<Entry> list = getSortedList();
		for(Entry e : list) {
			g.setColor(e.count>=0 ? Color.WHITE : Color.RED);
			e.resource.paint(g, x, y, format==null ? null : String.format(format, e.count));
			x += 20;
		}
	}
}
