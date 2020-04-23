package com.xrbpowered.hexpansio.ui.dlg;

import java.awt.Color;
import java.awt.GradientPaint;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Comparator;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.city.build.BuildImprovement;
import com.xrbpowered.hexpansio.world.city.effect.EffectTarget;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.Improvement;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.std.UIListBox;
import com.xrbpowered.zoomui.std.UIListItem;

public class BuildDialog extends OverlayDialog {

	public final boolean viewOnly;
	public final Tile tile;
	
	private class BuildingListItem extends UIListItem {
		public BuildingListItem(UIListBox list, int index, Object object) {
			super(list, index, object);
			setSize(0, 30);
		}
		@Override
		public void paint(GraphAssist g) {
			Improvement imp = (Improvement) object;
			boolean enabled = viewOnly || imp.canBuildOn(tile);
			boolean recomm = viewOnly && imp.prerequisite==null || !viewOnly && imp.isRecommendedFor(tile);
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
			
			g.setColor(enabled ? (recomm ? Color.YELLOW : Color.WHITE) : Color.GRAY);
			g.setFont(Res.fontBold);
			g.drawString(imp.name, 30, getHeight()/2f, GraphAssist.LEFT, GraphAssist.CENTER);
			
			g.setFont(Res.font);
			if(imp.prerequisite!=null) {
				g.setColor(Color.GRAY);
				g.drawString(Integer.toString(imp.upgPoints), 15, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			}
			g.setColor(enabled ? Color.WHITE : Color.GRAY);
			if(imp.glyph!=null)
				g.drawString(imp.glyph, 15, getHeight()/2f, GraphAssist.CENTER, GraphAssist.CENTER);
			if(!viewOnly && enabled && tile.city.getProduction()>0) {
				g.drawString(Res.calcTurnsStr(0, imp.buildCost, tile.city.getProduction(), null),
						getWidth()-10, getHeight()/2f, GraphAssist.RIGHT, GraphAssist.CENTER);
			}
		}
	}
	
	private class BuildComparator implements Comparator<Improvement> {
		@Override
		public int compare(Improvement imp1, Improvement imp2) {
			int res = 0;
			res = -Boolean.compare(imp1.isRecommendedFor(tile), imp2.isRecommendedFor(tile));
			if(res==0) {
				res = -Boolean.compare(imp1.canBuildOn(tile), imp2.canBuildOn(tile));
				if(res==0) {
					res = Integer.compare(imp1.upgPoints, imp2.upgPoints);
					if(res==0) {
						res = imp1.compareTo(imp2);
					}
				}
			}
			return res;
		}
	}

	private UIListBox list;
	
	private final ClickButton buildButton;
	private final ClickButton closeButton;

	public BuildDialog(final Tile tile) {
		this(tile, false);
	}

	public BuildDialog(final Tile tile, final boolean viewOnly) {
		super(Hexpansio.instance.getBase(), 600, 400, viewOnly ? "IMPROVEMENT INFO" : tile.improvement==null ? "BUILD IMPROVEMENT" : "ADD UPGRADE");
		this.tile = tile;
		this.viewOnly = viewOnly;
		
		ArrayList<Improvement> impList;
		if(viewOnly) {
			impList = ImprovementStack.createViewList(tile);
		}
		else {
			impList = Improvement.createBuildList(tile);
			impList.sort(new BuildComparator());
		}

		if(impList.isEmpty())
			list = null;
		else {
			list = new UIListBox(box, impList.toArray(new Improvement[impList.size()])) {
				@Override
				protected UIListItem createItem(int index, Object object) {
					return new BuildingListItem(this, index, object);
				}
			};
			list.setSize(300, 400-60-60);
			list.setLocation(10, 60);
			
			list.select(0);
		}
		
		if(!viewOnly) {
			buildButton = new ClickButton(box, "START", 140) {
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
		}
		else {
			buildButton = null;
		}

		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);
	}
	
	@Override
	public void layout() {
		box.setLocation(getWidth()-Hexpansio.instance.tileInfo.getWidth()-box.getWidth()-20, Hexpansio.instance.top.getHeight()+20);
		box.layout();
	}
	
	private Improvement selectedImprovement() {
		return list==null || list.getSelectedItem()==null ? null : (Improvement) list.getSelectedItem().object;
	}

	@Override
	public void onEnter() {
		Improvement imp = selectedImprovement();
		if(imp!=null && imp.canBuildOn(tile)) {
			TileMode.instance.switchBuildingProgress(new BuildImprovement(tile, imp));
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
		if(list==null) {
			g.setColor(Color.GRAY);
			g.drawString("No upgrades available", box.getWidth()/2, y, GraphAssist.CENTER, GraphAssist.BOTTOM);
		}
		else {
			g.setColor(Color.WHITE);
			g.drawString(viewOnly ? "Upgrades:" : "Select to build:", x, y);
	
			Improvement imp = list.getSelectedItem()==null ? null : (Improvement) list.getSelectedItem().object;
			
			if(imp!=null) {
				x = (int)(list.getX()+list.getWidth()+20);
				y = (int)list.getY();
				g.setFont(Res.fontLarge);
				g.drawString(imp.name, x, y, GraphAssist.LEFT, GraphAssist.TOP);
				y += 20;
				g.setFont(Res.font);
				if(!viewOnly && imp.isRecommendedFor(tile)) {
					g.setColor(Color.YELLOW);
					y += 15;
					g.drawString("Recommended:", x, y);
					y += 15;
					g.drawString(imp.recommendationExplained(tile), x, y);
				}
				if(viewOnly && tile.hasResourceImprovement() && imp.prerequisite==null) {
					g.setColor(Color.YELLOW);
					y += 15;
					g.drawString("Produces "+tile.resource.name, x, y);
				}
				g.setColor(Color.WHITE);
				if(imp.cityUnique) {
					y += 15;
					g.drawString("City unique", x, y);
				}
				if(imp.buildCost>0) {
					y += 15;
					Res.paintCost(g, YieldResource.production, "Build cost: ", imp.buildCost, viewOnly ? null : " "+Res.calcTurnsStr(0, imp.buildCost, tile.city.getProduction(), ""),
							imp.buildCost, x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				}
				if(!viewOnly && !imp.canHurry) {
					y += 15;
					g.setColor(Color.WHITE);
					g.drawString("Cannot hurry construction", x, y);
				}
				if(imp.upgPoints>0) {
					y += 15;
					g.setColor(viewOnly || ImprovementStack.getAvailUpgPoints(tile)>=imp.upgPoints ? Color.WHITE : Color.RED);
					g.drawString(String.format(viewOnly ? "Upg. Points: %d" : "Upg. Points required: %d", imp.upgPoints), x, y);
				}
				
				y += 10;
				if(imp.maintenance>0) {
					y += 15;
					g.setColor(Color.LIGHT_GRAY);
					Res.paintIncome(g, YieldResource.gold, "Maintenance: ", -imp.maintenance, " gold", x, y, GraphAssist.LEFT, GraphAssist.BOTTOM);
				}
				if(imp.workplaces!=0) {
					y += 15;
					g.setColor(Color.LIGHT_GRAY);
					g.drawString(EffectTarget.formatPluralDelta(imp.workplaces, "workplace", true), x, y);
				}
				for(YieldResource res : YieldResource.values()) {
					int yield = imp.yield.get(res);
					if(yield!=0) {
						y += 15;
						g.setColor(yield<0 ? Color.RED : res.fill);
						g.drawString(String.format("%+d %s", yield, res.name), x, y);
					}
					yield = imp.yieldPerWorker.get(res);
					if(yield!=0) {
						y += 15;
						g.setColor(yield<0 ? Color.RED : res.fill);
						g.drawString(String.format("%+d %s per worker", yield, res.name), x, y);
					}
				}
				if(imp.bonusResources>0) {
					y += 15;
					g.setColor(Color.LIGHT_GRAY);
					g.drawString(EffectTarget.formatPluralDelta(imp.bonusResources, "Resource")+" harvested", x, y);
				}
				if(imp.effect!=null) {
					y += 25;
					g.setColor(Color.WHITE);
					g.drawString("City-wide effects:", x, y);
					g.setColor(Color.LIGHT_GRAY);
					String[] desc = imp.effect.getDescription().split("\\n");
					for(String s : desc) {
						y += 15;
						g.drawString(s, x, y);
					}
				}
				
				if(!viewOnly && !imp.canBuildOn(tile)) {
					y += 25;
					g.setColor(Color.RED);
					g.drawString(imp.requirementExplained(tile), x, y);
				}
				if(imp.hotkey!=0) {
					y += 25;
					g.setFont(Res.fontBold);
					g.setColor(Color.WHITE);
					g.drawString(String.format("Hotkey: %s", KeyEvent.getKeyText(imp.hotkey)), x, y);
				}
			}
		}

		super.paintBoxContents(g);
	}
	
	@Override
	public boolean onKeyPressed(char c, int code, int mods) {
		if(!viewOnly) {
			Improvement imp = Improvement.hotkeyMap.get(code);
			if(imp!=null && list!=null) {
				for(int i=0; i<list.getNumItems(); i++)
					if(list.getItem(i).object==imp) {
						list.select(i);
						repaint();
						break;
					}
				return true;
			}
		}
		return super.onKeyPressed(c, code, mods);
	}
}
