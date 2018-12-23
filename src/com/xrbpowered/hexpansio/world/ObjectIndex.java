package com.xrbpowered.hexpansio.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ObjectIndex<T> {

	public class Entry {
		public final int index;
		public final String name;
		public final T object;
		public Entry(String name, T object) {
			this.name = name;
			this.index = size();
			this.object = object;
			list.add(this);
			map.put(name, this);
		}
	}
	
	private final HashMap<String, Entry> map = new HashMap<>();
	private final ArrayList<Entry> list = new ArrayList<>();

	public void put(String name, T object) {
		if(map.containsKey(name))
			throw new RuntimeException("Duplicate index entry: "+name);
		new Entry(name, object);
	}
	
	public T get(String name) {
		return map.get(name).object;
	}
	
	public int getIndex(String name) {
		return map.get(name).index;
	}
	
	public T get(int index) {
		return index<0 ? null : list.get(index).object;
	}
	
	public String getName(int index) {
		return index<0 ? null : list.get(index).name;
	}
	
	public int size() {
		return list.size();
	}
	
	public ObjectIndex<T> write(DataOutputStream out) throws IOException {
		out.writeInt(size());
		for(Entry e : list)
			out.writeUTF(e.name);
		return this;
	}

	public ObjectIndex<T> read(DataInputStream in) throws IOException {
		ObjectIndex<T> conv = new ObjectIndex<>();
		int num = in.readInt();
		for(int i=0; i<num; i++) {
			String name = in.readUTF();
			T object = get(name);
			if(object==null)
				throw new IOException("Index entry does not exist: "+name);
			conv.put(name, object);
		}
		return conv;
	}
}
