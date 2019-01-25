package com.xrbpowered.hexpansio.world.resources;

import java.awt.Color;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;

import com.xrbpowered.hexpansio.world.ObjectIndex;
import com.xrbpowered.zoomui.GraphAssist;

public class ResourcePile {

	public class Entry implements Comparable<Entry> {
		public final TokenResource resource;
		public int count = 0;
		
		public Entry(TokenResource res) {
			this.resource = res;
		}

		public Entry copy() {
			Entry e = new Entry(resource);
			e.count = this.count;
			return e;
		}

		@Override
		public int compareTo(Entry o) {
			return this.resource.name.compareTo(o.resource.name);
		}
	}
	
	public HashMap<String, Entry> map = new HashMap<>();
	private ArrayList<Entry> sortedList = null;
	
	public ResourcePile copy() {
		ResourcePile pile = new ResourcePile();
		for(Entry e : map.values())
			pile.map.put(e.resource.name, e.copy());
		return pile;
	}
	
	private Entry get(TokenResource res) {
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
	
	public void add(ResourcePile pile) {
		for(Entry e : pile.map.values())
			add(e.resource, e.count);
	}
	
	public int remove(TokenResource res, int count) {
		return add(res, -count);
	}

	public void remove(ResourcePile pile) {
		for(Entry e : pile.map.values())
			remove(e.resource, e.count);
	}

	public int count(TokenResource res) {
		Entry e = map.get(res.name);
		return e==null ? 0 : e.count;
	}
	
	public int totalCount() {
		int total = 0;
		for(Entry e : map.values()) {
			total += e.count;
		}
		return total;
	}
	
	public Collection<Entry> getUnsorted() {
		return map.values();
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
	
	public static void write(ResourcePile pile, ObjectIndex<TokenResource> conv, DataOutputStream out) throws IOException {
		ArrayList<Entry> list = pile.getSortedList();
		out.writeByte(list.size());
		for(Entry e : list) {
			out.writeByte(conv.getIndex(e.resource.id));
			out.writeByte(e.count);
		}
	}

	public static void read(ResourcePile pile, ObjectIndex<TokenResource> conv, DataInputStream in) throws IOException {
		int n = in.readByte();
		for(int i=0; i<n; i++) {
			TokenResource res = conv.get(in.readByte());
			int count = in.readByte();
			if(res!=null)
				pile.add(res, count);
		}
	}
}
