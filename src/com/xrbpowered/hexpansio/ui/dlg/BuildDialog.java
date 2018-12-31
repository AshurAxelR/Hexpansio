package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.FrameButton;
import com.xrbpowered.hexpansio.world.BuildingProgress;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Improvement;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class BuildDialog extends OverlayDialog {

	public final Tile tile;
	
	private class BuildingListItem extends UIListItem {
		public BuildingListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 30);
		}
		@Override
		public void paint(GraphAssist g) {
			Improvement imp = (Improvement) object;
			boolean enabled = imp.canBuildOn(tile);
			boolean sel = (index==list.getSelectedIndex());
			if(sel && enabled) {
				g.setPaint(new GradientPaint(0, 0, Res.uiBgBright, 0, getHeight(), Res.uiButtonTop));
				g.fill(this);
				g.border(this, Res.uiBorderLight);
			}
			else if(sel) {
				g.setPaint(new GradientPaint(0, 0, Res.uiBgMid, 0, getHeight(), Res.uiBgBright));
				g.fill(this);
				g.border(this, Res.uiBgBright);
			}
			else if(hover) {
				g.fill(this, Res.uiBgMid);
				g.border(this, Res.uiBorderDark);
			}
			
			g.setColor(enabled ? (imp.isRecommendedFor(tile) ? Color.YELLOW : Color.WHITE) : Color.GRAY);
			g.setFont(Res.fontBold);
			g.drawString(imp.name, 30, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
			
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			g.setFont(Res.font);
			if(imp.glyph!=null)
				g.drawString(imp.glyph, 15, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			if(enabled && tile.city.getProduction()>0) {
				g.drawString(Res.calcTurnsStr(0, imp.buildCost, tile.city.getProduction(), null),
						getWidth()-10, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}
	}
	
	private UIListBox list;
	
	private final FrameButton buildButton;
	private final FrameButton closeButton;
	
	public BuildDialog(Tile tile) {
		super(Hexpansio.instance.getBase(), 600, 400, "BUILD IMPROVEMENT");
		this.tile = tile;
		
		ArrayList<Improvement> impList = new ArrayList<>();
		for(int i=0; i<Improvement.objectIndex.size(); i++) {
			Improvement imp = Improvement.objectIndex.get(i);
			if(!imp.isCityCenter()) {
				// TODO check pre-req
				impList.add(imp);
			}
		}
		impList.sort(new Comparator<Improvement>() {
			@Override
			public int compare(Improvement o1, Improvement o2) {
				return o1.name.compareTo(o2.name);
			}
		});
		
		list = new UIListBox(box, impList.toArray(new Improvement[impList.size()])) {
			@Override
			protected UIListItem createItem(int index, Object object) {
				return new BuildingListItem(this, index, object);
			}
		};
		list.setSize(300, 400-60-60);
		list.setLocation(10, 60);
		
		buildButton = new FrameButton(box, "START", 140) {
			@Override
			public boolean isEnabled() {
				Improvement imp = selectedImprovement();
				return imp!=null && imp.canBuildOn(tile);
			}
			@Override
			public void onClick() {
				onEnter();
			}
		};
		buildButton.setLocation(box.getWidth()-buildButton.getWidth()-10, box.getHeight()-buildButton.getHeight()-10);

		closeButton = new FrameButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-buildButton.getHeight()-10);
	}
	
	@Override
	public void layout() {
		box.setLocation(getWidth()-Hexpansio.instance.tileInfo.getWidth()-box.getWidth()-20, Hexpansio.instance.top.getHeight()+20);
		box.layout();
	}
	
	private Improvement selectedImprovement() {
		return list.getSelectedItem()==null ? null : (Improvement) list.getSelectedItem().object;
	}

	@Override
	public void onEnter() {
		Improvement imp = selectedImprovement();
		if(imp!=null && imp.canBuildOn(tile)) {
			tile.city.setBuilding(new BuildingProgress.BuildImprovement(tile, imp));
			dismiss();
		}
	}

	@Override
	protected void paintBoxBackground(GraphAssist g) {
		super.paintBoxBackground(g);
		
		g.setPaint(new GradientPaint(0, box.getHeight()-60, Res.uiBgColor, 0, box.getHeight(), new Color(0x336699)));
		g.fillRect(0, box.getHeight()-60, box.getWidth(), 60);
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		int x = 20;
		int y = 50;
		g.setFont(Res.font);
		g.setColor(Color.WHITE);
		g.drawString("Select to build:", x, y);

		Improvement imp = list.getSelectedItem()==null ? null : (Improvement) list.getSelectedItem().object;
		
		if(imp!=null) {
			x = (int)(list.getX()+list.getWidth()+20);
			y = (int)list.getY();
			g.setFont(Res.fontLarge);
			g.drawString(imp.name, x, y, GraphAssist.LEFT, GraphAssist.TOP);
			y += 25;
			g.setFont(Res.font);
			if(imp.isRecommendedFor(tile)) {
				g.setColor(Color.YELLOW);
				y += 15;
				g.drawString("Recommended:", x, y);
				y += 15;
				g.drawString(imp.recommendationExplained(tile), x, y);
			}
			y += 15;
			g.setColor(Color.WHITE);
			g.drawString(String.format("Build cost: %d  %s", imp.buildCost, Res.calcTurnsStr(0, imp.buildCost, tile.city.getProduction(), "")), x, y);
			if(imp.maintenance>0) {
				y += 15;
				Res.paintIncome(g, YieldResource.gold, "Maintenance: ", -imp.maintenance, " gold", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
			}
			for(YieldResource res : YieldResource.values()) {
				int yield = imp.yield.get(res);
				if(yield!=0) {
					y += 15;
					g.setColor(res.fill);
					g.drawString(String.format("%+d %s", yield, res.name), x, y);
				}
			}
			int key = 0;
			for(int i=0; i<Improvement.buildMenu.length; i++) {
				if(Improvement.buildMenu[i]==imp) {
					key = Improvement.hotkeys[i];
					break;
				}
			}
			if(!imp.canBuildOn(tile)) {
				y += 15;
				g.setColor(Color.RED);
				g.drawString(imp.requirementExplained(tile), x, y);
			}
			if(key!=0) {
				y += 25;
				g.setFont(Res.fontBold);
				g.setColor(Color.WHITE);
				g.drawString(String.format("Hotkey: %s", KeyEvent.getKeyText(key)), x, y);
			}
		}

		super.paintBoxContents(g);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		Improvement imp = Improvement.buildFromHotkey(code);
		if(imp!=null) {
			for(int i=0; i<list.getNumItems(); i++)
				if(list.getItem(i).object==imp) {
					list.select(i);
					repaint();
					break;
				}
			return true;
		}
		return super.onKeyPressed(c, code, mods);
	}
}
