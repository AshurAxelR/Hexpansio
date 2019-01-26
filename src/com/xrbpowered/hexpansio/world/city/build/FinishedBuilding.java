package com.xrbpowered.hexpansio.world.city.build;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import com.xrbpowered.hexpansio.world.TurnEventMessage;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.YieldResource;

public class FinishedBuilding {

	public final String name;
	public int excess;
	public int wx, wy;
	
	public FinishedBuilding(BuildingProgress bp) {
		this.name = bp.getName();
		this.excess = bp.city.getExcess(bp.progress);
		this.wx = bp.tile.wx;
		this.wy = bp.tile.wy;
	}

	public FinishedBuilding(String name) {
		this.name = name;
	}

	public TurnEventMessage createMessage(City city) {
		return new TurnEventMessage(city.name, "finished building "+name, city.world.getTile(wx, wy)).setColor(YieldResource.production.fill);
	}
	
	public static void write(DataOutputStream out, FinishedBuilding fb) throws IOException {
		if(fb==null)
			out.writeUTF("");
		else {
			out.writeUTF(fb.name);
			out.writeShort(fb.excess);
			out.writeInt(fb.wx);
			out.writeInt(fb.wy);
		}
	}

	public static FinishedBuilding read(DataInputStream in) throws IOException {
		String name = in.readUTF();
		if(!name.isEmpty()) {
			FinishedBuilding fb = new FinishedBuilding(name);
			fb.excess = in.readShort();
			fb.wx = in.readInt();
			fb.wy = in.readInt();
			return fb;
		}
		else
			return null;
	}
}
