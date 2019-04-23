package com.xrbpowered.hexpansio.ui.modes;

import java.awt.Color;
import java.awt.event.KeyEvent;

import com.xrbpowered.hexpansio.ui.MapView;
import com.xrbpowered.hexpansio.ui.dlg.popup.ConfirmationDialog;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.city.build.BuildMigration;
import com.xrbpowered.hexpansio.world.city.build.BuildSettlement;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.zoomui.GraphAssist;

public class SettleMode extends MapMode {

	public static final SettleMode instance = new SettleMode();
	
	public static final int cityRange = ScoutMode.cityRange;
	public static final int minCityDist = 5;

	public SettleMode() {
		super("Settle", KeyEvent.VK_S);
	}

	@Override
	public boolean isTileEnabled(Tile tile) {
		return canSettle(tile) || canMigrate(tile) ||
				tile!=null && tile.settlement!=null && tile.settlement.city==view.selectedCity;
	}
	
	@Override
	public boolean isEnabled() {
		return view!=null && view.selectedCity!=null && view.selectedCity.population>1 && view.selectedCity.unemployed>0;
	}
	
	public boolean canSettle(Tile tile) {
		return tile!=null && tile.discovered && view.selectedCity.tile.distTo(tile)<=cityRange &&
				view.world.distToNearestCityOrSettler(tile.wx, tile.wy)>=minCityDist && tile.terrain.canSettleOn();
	}

	public boolean canMigrate(Tile tile) {
		return tile!=null && tile.isCityCenter() && tile.city!=view.selectedCity && view.selectedCity.tile.distTo(tile)<=cityRange &&
				tile.city.hasMigrationCentre() && view.selectedCity.hasMigrationCentre();
	}

	@Override
	public boolean hasOverlayLinks(Tile tile) {
		return (view.getScale()>0.25f && tile.isCityCenter() && (tile.city.isBuildingSettlement() || tile.city.isBuildingMigration()));
	}
	
	@Override
	public void paintTileOverlay(GraphAssist g, int wx, int wy, Tile tile) {
		if(view.getScale()>0.25f && tile!=null) {
			boolean source = tile.isCityCenter() && (tile.city.isBuildingSettlement() || tile.city==view.selectedCity && tile.city.hasMigrationCentre());
			boolean target = tile.settlement!=null || tile.isCityCenter() && canMigrate(tile);
			if(source || target) {
				g.resetStroke();
				g.setColor(Color.WHITE);
				if(source) {
					if(tile.city.isBuildingSettlement() || tile.city==view.selectedCity && tile.city.isBuildingMigration())
						view.drawLink(g, tile, tile.city.buildingProgress.tile);
					g.graph.setStroke(MapView.borderStroke);
				}
				g.pushPureStroke(true);
				g.graph.draw(MapView.tileCircle);
				g.popPureStroke();
			}
		}
	}
	
	@Override
	public String getDescription() {
		return "Build new settlements or migrate population between cities.";
	}
	
	@Override
	public String explainNoAction() {
		if(view.hoverTile==null || !view.hoverTile.discovered)
			return "Undiscovered area.";
		else if(view.hoverTile.isCityCenter()) {
			return null;
		}
		else if(view.hoverTile!=null && view.hoverTile.settlement!=null) {
			return null;
		}
		else {
			String pre = String.format("%s cannot start a settlement.\n", view.selectedCity.name);
			if(view.selectedCity.population<=1)
				return pre+"The city must have at least 2 population.";
			else if(view.selectedCity.unemployed==0)
				return pre+"Requires 1 unemployed worker.";
			else if(view.selectedCity.tile.distTo(view.hoverTile)>cityRange)
				return "Out of range.";
			else if(view.world.distToNearestCityOrSettler(view.hoverTile.wx, view.hoverTile.wy)<minCityDist)
				return String.format("Must be at least %d tiles away from any other city.", minCityDist);
			else if(!view.hoverTile.terrain.canSettleOn())
				return "Cannot build a settlement on this terrain.";
		}
		return null;
	}
	
	@Override
	public boolean showCityRange() {
		return true;
	}
	
	@Override
	public boolean isRangeBorder(Tile tile, Dir d) {
		return tile.isRangeBorder(d, view.selectedCity.tile, cityRange);
	}
	
	private void startSettle(Tile tile) {
		TileMode.instance.switchBuildingProgress(new BuildSettlement(view.selectedCity, tile));
	}
	
	@Override
	public boolean action() {
		final Tile hoverTile = view.hoverTile;
		if((view.selectedCity.isBuildingMigration() || view.selectedCity.isBuildingSettlement()) && view.selectedCity.buildingProgress.tile==hoverTile) {
			TileMode.instance.switchBuildingProgress(null);
			return true;
		}
		else if(view.selectedCity.population>1 && view.selectedCity.unemployed>0 && canSettle(hoverTile)) {
			if(hoverTile.resource!=null) {
				new ConfirmationDialog(0, "LOSING RESOURCE",
						String.format("Building a settlement in this location\nwill destroy the source of %s.", hoverTile.resource.name),
						"PROCEED", "CANCEL") {
					@Override
					public void onEnter() {
						dismiss();
						startSettle(hoverTile);
						repaint();
					}
				};
				return true;
			}
			else
				startSettle(hoverTile);
			return true;
		}
		else if(view.selectedCity.population>1 && view.selectedCity.unemployed>0 && canMigrate(hoverTile)) {
			TileMode.instance.switchBuildingProgress(new BuildMigration(view.selectedCity, hoverTile.city));
			return true;
		}
		return false;
	}
}
