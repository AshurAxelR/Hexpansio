package com.xrbpowered.hexpansio.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Polygon;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.Stroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.dlg.popup.InformationDialog;
import com.xrbpowered.hexpansio.ui.modes.MapMode;
import com.xrbpowered.hexpansio.ui.modes.TileMode;
import com.xrbpowered.hexpansio.world.Dir;
import com.xrbpowered.hexpansio.world.Region;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.hexpansio.world.city.City;
import com.xrbpowered.hexpansio.world.resources.YieldResource;
import com.xrbpowered.hexpansio.world.tile.Tile;
import com.xrbpowered.hexpansio.world.tile.improv.CityUpgrades;
import com.xrbpowered.hexpansio.world.tile.improv.ImprovementStack;
import com.xrbpowered.zoomui.GraphAssist;
import com.xrbpowered.zoomui.UIContainer;
import com.xrbpowered.zoomui.UIElement;
import com.xrbpowered.zoomui.base.UIZoomView;

public class MapView extends UIElement {

	public static final Color cityColor = new Color(0x666666);
	public static final Color playerColor = new Color(0x990000);
	public static final Color upgImpColor = new Color(0xdadada);
	
	public static Stroke borderStroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f);
	public static Stroke borderStrokeThick = new BasicStroke(4f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_MITER, 10f);

	public static final int w = 34;
	public static final int h = 28;
	public static final int a = 16;
	public static final Shape hexagon = new Polygon(
			new int[] {-w/2-a, -w/2, w/2, w/2+a, w/2, -w/2},
			new int[] {0, -h, -h, 0, h, h},
			6);
	public static final Shape smallHexagon = AffineTransform.getScaleInstance(0.9, 0.9).createTransformedShape(hexagon);
	public static final float tileCircleR = w/2+a+3;
	public static final Shape tileCircle = new Ellipse2D.Float(-tileCircleR, -tileCircleR, tileCircleR*2, tileCircleR*2);

	public static class Zoom extends UIZoomView {
		public final MapView view;
		
		private Zoom(UIContainer parent) {
			super(parent);
			view = new MapView(this);
		}
		
		@Override
		public boolean onMouseScroll(float x, float y, float delta, int mods) {
			scale(1.0f-delta*0.1f, 0f, 0f);
			float cx = parentToLocalX(x);
			float cy = parentToLocalY(y);
			view.updateHoverTile(cx, cy);
			repaint();
			return true;
		}
		
		@Override
		protected float parentToLocalX(float x) {
			return super.parentToLocalX(x-getWidth()/2f);
		}

		@Override
		protected float parentToLocalY(float y) {
			return super.parentToLocalY(y-getHeight()/2f);
		}
		
		@Override
		protected void applyTransform(GraphAssist g) {
			g.translate(-panX+getWidth()/2f, -panY+getHeight()/2f);
			g.scale(scale);
		}
		
		@Override
		protected void paintSelf(GraphAssist g) {
			g.fill(this, new Color(0xddd0cc));
		}
		
		@Override
		protected void paintChildren(GraphAssist g) {
			g.pushAntialiasing(false);
			super.paintChildren(g);
			g.popAntialiasing();
		}
	}
	
	public World world = null;
	public City selectedCity = null;
	public Tile selectedTile = null;
	public Tile hoverTile = null;
	
	private MapView(Zoom parent) {
		super(parent);
	}
	
	public static Zoom createMapView(UIContainer parent) {
		return new Zoom(parent);
	}

	public void setWorld(World world) {
		this.world = world;
		((UIZoomView)getParent()).setScale(3f);
		selectCity(world.cities.get(0), true);
	}
	
	public void selectCity(City city, boolean center) {
		this.selectedCity = city;
		this.selectedTile = city.tile;
		if(center)
			panToTile(city.tile);
	}

	public void selectCity(City city) {
		selectCity(city, false);
	}

	public void panToTile(Tile tile) {
		int x0 = tile.wx*(w+a);
		int y0 = (-tile.wx+2*tile.wy)*h;
		((UIZoomView)getParent()).setPan(x0, y0);
	}
	
	public boolean updateHoverTile(float x, float y) {
		int mx = (int) Math.floor(x/(float)(w+a));
		int my = (int) Math.floor((y/(float)h+mx)/2f);
		loop:
		for(int ix=0; ix<=1; ix++)
			for(int iy=0; iy<=1; iy++) {
				float x0 = (mx+ix)*(w+a);
				float y0 = (-(mx+ix)+2*(my+iy))*h;
				if(hexagon.contains(x-x0, y-y0)) {
					mx += ix;
					my += iy;
					break loop;
				}
			}
		Tile t = world.getTile(mx, my);
		if(t!=hoverTile) {
			hoverTile = t;
			return true;
		}
		else
			return false;
	}
	
	@Override
	public void onMouseMoved(float x, float y, int mods) {
		if(updateHoverTile(x, y))
			repaint();
	}
	
	@Override
	public void onMouseOut() {
		hoverTile = null;
		repaint();
	}
	
	@Override
	public boolean onMouseDown(float x, float y, Button button, int mods) {
		updateHoverTile(x, y);
		MapMode mode = MapMode.active;
		if(button==Button.left && mods==UIElement.modNone) {
			if((!mode.isTileEnabled(hoverTile) || !mode.action()) && Hexpansio.settings.explainNoAction) {
				String explain = mode.explainNoAction();
				if(explain!=null) {
					new InformationDialog(0, "NOT AVAILABLE", explain, "OK");
				}
			}
			repaint();
			return true;
		}
		else
			return false;
	}
	
	@Override
	public boolean isInside(float x, float y) {
		return true;
	}

	@Override
	public boolean isVisible(Rectangle clip) {
		return true;
	}
	
	public float getScale() {
		return ((UIZoomView)getParent()).getScale();
	}
	
	public void drawLink(GraphAssist g, Tile t0, Tile t1) {
		float x0 = t0.wx*(w+a);
		float y0 = (-t0.wx+2*t0.wy)*h;
		float dx = t1.wx*(w+a) - x0;
		float dy = (-t1.wx+2*t1.wy)*h - y0;
		float len = (float)Math.sqrt(dx*dx+dy*dy);
		float rx = tileCircleR*dx/len;
		float ry = tileCircleR*dy/len;
		g.line(rx, ry, dx-rx, dy-ry);
	}
	
	private void paintRegion(GraphAssist g, Rectangle clip, Region region) {
		if(region==null)
			return;
	
		for(int x=0; x<Region.size; x++)
			for(int y=0; y<Region.size; y++) {
				Tile tile = region.tiles[x][y];
				if(!tile.discovered)
					continue;
				
				int x0 = tile.wx*(w+a);
				int y0 = (-tile.wx+2*tile.wy)*h;
				if(x0+w/2+a<clip.x || x0-w/2-a>clip.x+clip.width || y0+h<clip.y || y0-h>clip.y+clip.height)
					continue;
				
				g.pushTx();
				g.translate(x0, y0);
				
				if(getScale()<=0.75f && tile.city!=null && tile.city.tile==tile)
					g.setColor(tile.city==selectedCity ? Color.WHITE : playerColor);
				else if(tile.isCityCenter())
					g.setColor(cityColor);
				else
					g.setColor(tile.terrain.color);
				if(getScale()>0.25f) {
					g.graph.fill(hexagon);

					g.pushAntialiasing(true);

					g.graph.setStroke(getScale()>0.75f ? borderStroke : borderStrokeThick);
					g.setColor(playerColor);
					if(tile.isCityBorder(Dir.NW))
						g.line(-w/2-a, 0, -w/2, -h);
					if(tile.isCityBorder(Dir.N))
						g.line(-w/2, -h, w/2, -h);
					if(tile.isCityBorder(Dir.SW))
						g.line(-w/2-a, 0, -w/2, h);

					if(MapMode.active.showCityRange()) {
						g.resetStroke();
						g.setColor(Color.WHITE);
						if(MapMode.active.isRangeBorder(tile, Dir.NW))
							g.line(-w/2-a, 0, -w/2, -h);
						if(MapMode.active.isRangeBorder(tile, Dir.N))
							g.line(-w/2, -h, w/2, -h);
						if(MapMode.active.isRangeBorder(tile, Dir.SW))
							g.line(-w/2-a, 0, -w/2, h);
					}
					
					g.popAntialiasing();
				}
				else
					g.graph.fillRect(-w/2-a/2, -h, w+a, h*2);
				
				if(getScale()>1.25f) {
					g.pushAntialiasing(true);
					YieldResource.preparePaint(g);
					int dxs = (w+a*2) / (tile.yield.countTypes()+1);
					int xs = -w/2-a + dxs;
					for(YieldResource res : YieldResource.values()) {
						int v = tile.yield.get(res);
						if(v>0) {
							res.paintYield(g, xs, 0, v);
							xs += dxs;
						}
					}

					if(tile.city==selectedCity && !tile.isCityCenter() && tile.improvement!=null) {
						g.setColor(tile.improvement.upgPoints<tile.city.maxUpgPointsForTile(tile) ? upgImpColor : Color.WHITE);
						g.setFont(Res.font);
						g.drawString(tile.improvement.getGlyph(), 0, h-3, GraphAssist.CENTER, GraphAssist.BOTTOM);
					}
					
					g.popAntialiasing();
				}
				
				g.popTx();
			}
	}
	
	private void paintRegionOverlay(GraphAssist g, Rectangle clip, int rx, int ry) {
		Region region = world.getRegion(rx, ry);

		g.pushAntialiasing(true);
		for(int x=0; x<Region.size; x++)
			for(int y=0; y<Region.size; y++) {
				Tile tile = region==null ? null : region.tiles[x][y];
				
				int mx = x+(rx<<Region.sized);
				int my = y+(ry<<Region.sized);
				int x0 = mx*(w+a);
				int y0 = (-mx+2*my)*h;
				if((x0+w/2+a<clip.x || x0-w/2-a>clip.x+clip.width || y0+h<clip.y || y0-h>clip.y+clip.height) && (tile==null || !MapMode.active.hasOverlayLinks(tile)))
					continue;
				
				g.pushTx();
				g.translate(x0, y0);

				if(getScale()>0.25f && tile!=null && tile==selectedTile) {
					g.graph.setStroke(getScale()>0.75f ? borderStroke : borderStrokeThick);
					if(tile==selectedTile) {
						g.setColor(new Color(0x99ffffff, true));
						g.graph.draw(smallHexagon);
					}
				}

				MapMode.active.paintTileOverlay(g, mx, my, tile);
				
				if(tile!=null && tile.resource!=null && getScale()>0.5f) {
					g.setColor(tile.city==null ? Color.DARK_GRAY : tile.hasResourceImprovement() ? Color.WHITE : Color.LIGHT_GRAY);
					Res.paintToken(g, getScale(), tile.resource.atlas, tile.resource.subImage);
				}

				g.popTx();
			}
		g.popAntialiasing();
	}
	
	private void paintCity(GraphAssist g, City city) {
		if(getScale()>0.5f) {
			g.pushAntialiasing(true);
			g.pushTx();
			int x0 = city.tile.wx*(w+a);
			int y0 = (-city.tile.wx+2*city.tile.wy)*h;
			g.translate(x0, y0);
			
			g.setColor(ImprovementStack.tileContains(city.tile, CityUpgrades.utopia) ? Color.WHITE : Color.DARK_GRAY);
			Res.paintToken(g, getScale(), Res.imgHappiness, city.happiness.ordinal());
			
			if(getScale()>0.75f) {
				g.setFont(Res.font);
				String str = String.format("%d. %s", city.population, city.name.toUpperCase());
			
				if(getScale()>1.25f) {
					if(city.unemployed>0)
						TileMode.paintWorkerBubbles(g, city.unemployed, city.unemployed, false);
				}

				FontMetrics fm = g.graph.getFontMetrics();
				float tw = fm.stringWidth(str);
				if(city.buildingProgress==null)
					tw += 12;
				float th = fm.getAscent() - fm.getDescent();
				float tx = -tw/2f;
				float ty = h*0.75f + th - th/2f;
				
				g.setColor(city==selectedCity ? Color.WHITE : playerColor);
				g.graph.fillRect((int)(tx-5), (int)(ty-th-3), (int)(tw+10), (int)(th+6));
				g.setColor(city==selectedCity ? playerColor : Color.WHITE);
				g.graph.drawString(str, tx, ty);
				Res.paintProgress(g, YieldResource.food, city.growth, city.getTargetGrowth(), city.getFoodGrowth(),
						0, (int)(ty+3), (int)(tw+10), 3, GraphAssist.CENTER);
				if(city.buildingProgress==null) {
					g.pushPureStroke(true);
					g.setColor(city.getExcess(city.getProduction())>0 ? YieldResource.production.fill : Color.BLACK);
					g.graph.fillOval((int)(tx+tw-8), (int)(ty-th/2-5), 10, 10);
					g.popPureStroke();
				}
				else if(city.buildingProgress.tile.isCityCenter()) {
					Res.paintProgress(g, YieldResource.production, city.buildingProgress.progress, city.buildingProgress.getCost(), city.getProduction(),
							0, (int)(ty+6), (int)(tw+10), 3, GraphAssist.CENTER);
					th += 3;
					ty += 3;
				}
				g.resetStroke();
				g.setColor(Color.BLACK);
				g.graph.drawRect((int)(tx-5), (int)(ty-th-3), (int)(tw+10), (int)(th+9));
				
				g.popAntialiasing();
				g.popTx();
				
				if(city.buildingProgress!=null && !city.buildingProgress.tile.isCityCenter()) {
					g.pushTx();
					x0 = city.buildingProgress.tile.wx*(w+a);
					y0 = (-city.buildingProgress.tile.wx+2*city.buildingProgress.tile.wy)*h;
					g.translate(x0, y0);
					
					Res.paintProgress(g, YieldResource.production, city.buildingProgress.progress, city.buildingProgress.getCost(), city.getProduction(),
							0, h-6, w+a, 3, GraphAssist.CENTER);
					
					g.popTx();
				}
			}
			else {
				g.popTx();
			}
		}
	}
	
	@Override
	public void paint(GraphAssist g) {
		if(world==null)
			return;
		
		Rectangle clip = g.graph.getClipBounds();
		int mx = (int) Math.floor(clip.x/(float)(w+a));
		int my = (int) Math.floor((clip.y/(float)h+mx)/2f);
		int rx0 = (mx>>Region.sized);
		int ry0 = (my>>Region.sized);
		
		int nx = clip.width/(Region.size*(w+a))+1;
		int ny = clip.width/(Region.size*h*3/2)+1;
		
		g.pushPureStroke(true);
		for(int x=0; x<=nx; x++)
			for(int y=0; y<=ny; y++) {
				Region r = world.getRegion(rx0+x, ry0+y);
				paintRegion(g, clip, r);
			}
		
		if(getScale()>0.5f && hoverTile!=null && hoverTile.discovered && MapMode.active.isTileEnabled(hoverTile)) {
			g.pushAntialiasing(true);
			g.pushTx();
			int x0 = hoverTile.wx*(w+a);
			int y0 = (-hoverTile.wx+2*hoverTile.wy)*h;
			g.translate(x0, y0);
			g.setColor(Color.WHITE);
			if(hoverTile==selectedTile)
				g.graph.setStroke(borderStroke);
			else
				g.resetStroke();
			g.graph.draw(smallHexagon);
			g.popTx();
			g.popAntialiasing();
		}
		g.popPureStroke();

		for(int x=0; x<=nx; x++)
			for(int y=0; y<=ny; y++) {
				paintRegionOverlay(g, clip, rx0+x, ry0+y);
			}
		for(int x=0; x<=nx; x++)
			for(int y=0; y<=ny; y++) {
				Region r = world.getRegion(rx0+x, ry0+y);
				if(r!=null) {
					for(City city : r.cities)
						paintCity(g, city);
				}
			}
	}

}
