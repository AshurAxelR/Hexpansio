package com.xrbpowered.hexpansio.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class History {

	public final World world;
	private final ArrayList<TurnStatistics> history = new ArrayList<>();
	
	public History(World world) {
		this.world = world;
	}

	public int numPoints() {
		return history.size();
	}
	
	public TurnStatistics getPoint(int i) {
		if(i<0)
			return history.get(0);
		else if(i>=history.size())
			return history.get(history.size()-1);
		else
			return history.get(i);
	}
	
	protected TurnStatistics push() {
		TurnStatistics s = new TurnStatistics(world.turn);
		s.update(world);
		history.add(s);
		return s;
	}
	
	public TurnStatistics stats() {
		if(history.isEmpty())
			return push();
		else {
			TurnStatistics s = history.get(history.size()-1);
			return (s.turn!=world.turn) ? push() : s;
		}
	}
	
	public void write(DataOutputStream out) throws IOException {
		int num = history.size();
		out.writeInt(num);
		for(int i=0; i<num; i++) {
			TurnStatistics s = history.get(i);
			out.writeInt(s.turn);
			s.write(out);
		}
	}
	
	public void read(DataInputStream in) throws IOException {
		history.clear();
		int num = in.readInt();
		for(int i=0; i<num; i++) {
			TurnStatistics s = new TurnStatistics(in.readInt());
			s.read(in);
			history.add(s);
		}
	}

}
