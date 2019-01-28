package com.xrbpowered.hexpansio.world;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.utils.RandomUtils;

public class WorldSettings {

	public long seed = System.currentTimeMillis();
	public boolean customSeed = false;
	public String seedString = null;

	public int initialGold = 10;
	public int initialBaseHappiness = 5;
	
	public boolean voidEnabled = true;
	public int voidStartTurn = 20;
	public int voidStartSources = 1;
	public int voidMinDistance = 16; 
	public int voidMaxDistance = voidMinDistance+8;
	public int voidSpreadSpeed = 2;

	public int getDifficultyRating() {
		float d = 1f;
		d *= (1f - (initialBaseHappiness-5)*0.25f);
		d *= (1f - (initialGold-10)*0.01f);
		if(customSeed)
			d *= 0.5f;
		if(voidEnabled) {
			d *= 30f / (voidStartTurn+10f);
			d *= 1f + (voidStartSources-1)*0.25f;
			d *= (1f + (16f / (float)voidMinDistance - 1f)*0.25);
			d *= 0.5f+voidSpreadSpeed*0.25f;
		}
		else {
			d *= 0.25f;
		}
		return (int)(d*100f);
	}
	
	public long getSeed() {
		if(customSeed)
			return RandomUtils.seedXY(0, seedString.hashCode(), 23336126L);
		else
			return seed;
	}
	
	public void setVoidDistance(int min) {
		voidMinDistance = min;
		voidMaxDistance = min+8;
	}
	
	public WorldSettings copy() {
		WorldSettings w = new WorldSettings();
		// w.seed = this.seed;
		w.customSeed = this.customSeed;
		w.seedString = this.seedString;
		w.initialBaseHappiness = this.initialBaseHappiness;
		w.voidEnabled = this.voidEnabled;
		w.voidStartTurn = this.voidStartTurn;
		w.voidStartSources = this.voidStartSources;
		w.voidMinDistance = this.voidMinDistance;
		w.voidMaxDistance = this.voidMaxDistance;
		w.voidSpreadSpeed = this.voidSpreadSpeed;
		return w;
	}
	
	public static void write(DataOutputStream out, WorldSettings w) throws IOException {
		out.writeByte(w.customSeed ? 1 : 0);
		if(w.customSeed)
			out.writeUTF(w.seedString);
		else
			out.writeLong(w.seed);
		out.writeByte(w.initialBaseHappiness);
		out.writeByte(w.voidEnabled ? 1 : 0);
		if(w.voidEnabled) {
			out.writeShort(w.voidStartTurn);
			out.writeByte(w.voidStartSources);
			out.writeByte(w.voidMinDistance);
			out.writeByte(w.voidSpreadSpeed);
		}
	}
	
	public static WorldSettings read(DataInputStream in) throws IOException {
		WorldSettings w = new WorldSettings();
		w.customSeed = in.readByte()!=0;
		if(w.customSeed)
			w.seedString = in.readUTF();
		else
			w.seed = in.readLong();
		w.initialBaseHappiness = in.readByte();
		w.voidEnabled = in.readByte()!=0;
		if(w.voidEnabled) {
			w.voidStartTurn = in.readShort();
			w.voidStartSources = in.readByte();
			w.setVoidDistance(in.readByte());
			w.voidSpreadSpeed =in.readByte();
		}
		return w;
	}


}
