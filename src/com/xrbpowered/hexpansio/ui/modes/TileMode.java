package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.ui.dlg.BuildDialog;
import com.xrbpowered.hexpansio.ui.dlg.ConfirmationDialog;
import com.xrbpowered.hexpansio.ui.dlg.HurryDialog;
import com.xrbpowered.hexpansio.ui.dlg.ProductionLossDialog;
import com.xrbpowered.hexpansio.world.BuildingProgress;
import com.xrbpowered.hexpansio.world.City;
import com.xrbpowered.hexpansio.world.tile.Improvement;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class TileMode extends MapMode {

	public static final TileMode instance = new TileMode();
	
	public TileMode() {
		super("Tile", KeyEvent.VK_Q);
	}

	@Override
	public boolean isTileEnabled(Tile tile) {
		return tile!=null && tile.city==view.selectedCity 
				|| tile.isCityCenter() && tile.city!=view.selectedCity;
	}
	
	@Override
	public boolean isHighlighted() {
		return view!=null && view.selectedCity!=null && view.selectedCity.unemployed>0;
	}
	
	public boolean canBuild(Tile tile) {
		return tile!=null && tile.city==view.selectedCity && !tile.isCityCenter() && tile.improvement==null;
	}

	@Override
	public String getButtonStatusText() {
		if(view!=null && view.selectedCity!=null)
			return String.format("%d / %d", view.selectedCity.unemployed, view.selectedCity.population);
		else
			return null;
	}

	public static void paintWorkerBubble(GraphAssist g, int x, int y, Color fill, Color border) {
		g.pushPureStroke(true);
		g.resetStroke();
		g.setColor(fill);
		g.graph.fillOval(x-5, y-5, 10, 10);
		g.setColor(border);
		g.graph.drawOval(x-5, y-5, 10, 10);
		g.popPureStroke();
	}

	public static void paintWorkerBubble(GraphAssist g, Color fill, Color border) {
		paintWorkerBubble(g, 0, -MapView.h*2/3, fill, border);
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if(view.getScale()>1.25f && tile!=null && tile.city==view.selectedCity && !tile.isCityCenter()) {
			if(tile.workers==0)
				paintWorkerBubble(g, Color.BLACK, Color.DARK_GRAY);
			else
				paintWorkerBubble(g, new Color(0x88ddbb), new Color(0xaaffdd));
		}
	}
	
	@Override
	public int paintHoverTileHint(GraphAssist g, int x, int y) {
		String s;
		Color c = Color.GRAY;
		if(view.hoverTile==null || !view.hoverTile.discovered)
			s = "Undiscovered area";
		else if(view.hoverTile.isCityCenter()) {
			if(view.hoverTile.city==view.selectedCity)
				return y;
			s = "Click to select "+view.hoverTile.city.name;
			c = Color.WHITE;
		}
		else if(view.hoverTile.city!=view.selectedCity)
			return y;
		else if(view.hoverTile!=view.selectedTile) {
			s = "Click to select tile";
			c = Color.WHITE;
		}
		else if(view.hoverTile.isCityCenter())
			return y;
		else if(view.hoverTile.workers>0) {
			s = "Click to unassign workers";
			c = Color.WHITE;
		}
		else if(view.selectedCity.unemployed==0)
			s = "Requires 1 unemployed worker";
		else {
			 s = "Click to assign a worker";
			 c = Color.WHITE;
		}
		return paintHoverTileHint(g, s, c, x, y);
	}

	@Override
	public boolean action() {
		Tile hoverTile = view.hoverTile;
		if(hoverTile.isCityCenter()) {
			view.selectCity(hoverTile.city);
			return true;
		}
		else if(hoverTile!=null && hoverTile.city==view.selectedCity && hoverTile!=view.selectedTile) {
			view.selectedTile = hoverTile;
			return true;
		}
		else if(hoverTile.city==view.selectedCity && !hoverTile.isCityCenter()) {
			if(hoverTile.workers==0) {
				if(hoverTile.assignWorker()) {
					view.selectedCity.updateStats();
					view.world.updateWorldTotals();
					return true;
				}
			}
			else if(hoverTile.workers>0) {
				if(hoverTile.unassignWorkers()) {
					view.selectedCity.updateStats();
					view.world.updateWorldTotals();
					return true;
				}
			}
		}
		return false;
	}
	
	public void switchBuildingProgress(BuildingProgress bp) {
		City city = view.selectedCity;
		int loss = city.buildingProgress!=null ? city.buildingProgress.progress : 0;
		if(loss>0) {
			new ProductionLossDialog(city.buildingProgress, bp) {
				@Override
				public void onEnter() {
					city.setBuilding(bp);
					dismiss();
				}
			};
		}
		else
			city.setBuilding(bp);
	}
	
	public boolean cancelBuilding() {
		if(view.selectedCity.buildingProgress!=null) {
			switchBuildingProgress(null);
			return true;
		}
		else
			return false;
	}

	public boolean hurryBuilding() {
		if(view.selectedCity.buildingProgress!=null && view.selectedCity.buildingProgress.canHurry()) {
			int cost = view.selectedCity.buildingProgress.getCost() - view.selectedCity.buildingProgress.progress;
			if(cost >0 && view.world.goods>=cost) {
				new HurryDialog(cost) {
					@Override
					public void onEnter() {
						view.selectedCity.buildingProgress.progress += cost;
						view.world.goods -= cost;
						dismiss();
					}
				};
				return true;
			}
		}
		return false;
	}

	public boolean removeBuilding() {
		if(view.selectedTile.improvement!=null && !view.selectedTile.improvement.isPermanent()) {
			new ConfirmationDialog(0, "REMOVE", "Remove tile improvement and all upgrades?", "REMOVE", "CANCEL") {
				@Override
				public void onEnter() {
					dismiss();
					switchBuildingProgress(new BuildingProgress.RemoveImprovement(view.selectedTile));
					repaint();
				}
			};
			return true;
		}
		else
			return false;
	}

	@Override
	public boolean hotkeyAction(int code) {
		Tile tile = view.selectedTile;
		if(code==KeyEvent.VK_DELETE) {
			if(view.selectedCity.buildingProgress!=null && view.selectedCity.buildingProgress.tile==tile) {
				cancelBuilding();
				return true;
			}
			else
				return false;
		}
		else if(code==KeyEvent.VK_BACK_SPACE) {
			if(tile.improvement!=null && !tile.improvement.isPermanent()) {
				removeBuilding();
				return true;
			}
			else
				return false;
		}
		else if(code==KeyEvent.VK_INSERT) {
			return hurryBuilding();
		}
		else if(code==KeyEvent.VK_SPACE) {
			BuildingProgress bp = tile.city==null ? null : tile.city.buildingProgress==null || tile.city.buildingProgress.tile!=tile ? null : tile.city.buildingProgress;
			if(tile.improvement==null && bp==null) {
				new BuildDialog(tile);
				return true;
			}
			else if(tile.improvement==null || bp!=null && bp instanceof BuildingProgress.RemoveImprovement) {
				return false;
			}
			else {
				// new UpgradeDialog(tile);
				return true;
			}
		}
		
		Improvement imp = Improvement.buildFromHotkey(code);
		if(imp!=null && canBuild(tile) && imp.canBuildOn(tile)) {
			switchBuildingProgress(new BuildingProgress.BuildImprovement(tile, imp));
			return true;
		}
		else
			return false;
	}
}
