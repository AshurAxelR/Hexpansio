package com.xrbpowered.hexpansio.ui.dlg.stats;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Shape;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.geom.AffineTransform;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;

import com.xrbpowered.hexpansio.Hexpansio;
import com.xrbpowered.hexpansio.res.Res;
import com.xrbpowered.hexpansio.ui.ClickButton;
import com.xrbpowered.hexpansio.ui.OptionBox;
import com.xrbpowered.hexpansio.ui.dlg.OverlayDialog;
import com.xrbpowered.hexpansio.ui.dlg.popup.ConfirmationDialog;
import com.xrbpowered.hexpansio.world.TurnStatistics;
import com.xrbpowered.hexpansio.world.World;
import com.xrbpowered.zoomui.GraphAssist;

public class HistoryDialog extends OverlayDialog {

	private final float plotWidth = 800f;
	private final float plotHeight = 400f;
	
	public final World world;
	
	private static final Diagram[] diagramOptions = {
			Diagram.cities, Diagram.populationDensity, Diagram.happiness, Diagram.gold, Diagram.goods, Diagram.yield, Diagram.resources, Diagram.voidStorms
		};

	private static Diagram diagram = null;
	
	private final OptionBox diagramOption;

	private final ClickButton closeButton;
	private final ClickButton seedButton;
	
	public HistoryDialog() {
		super(Hexpansio.instance.getBase(), 1020, 520, "STATISTICS AND HISTORY");
		this.world = Hexpansio.getWorld();
		
		closeButton = new ClickButton(box, "Close", 100) {
			@Override
			public void onClick() {
				dismiss();
			}
		};
		closeButton.setLocation(10, box.getHeight()-closeButton.getHeight()-10);

		seedButton = new ClickButton(box, "Seed", 100) {
			@Override
			public void onClick() {
				if(world.settings.seedString!=null) {
					new ConfirmationDialog(0, "WORLD SEED", world.settings.seedString, "COPY", "CLOSE") {
						@Override
						public void onEnter() {
							Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
							StringSelection con = new StringSelection(world.settings.seedString);
							clipboard.setContents(con, con);
							super.onEnter();
						}
					}.repaint();
				}
			}
		};
		seedButton.setLocation(closeButton.getX()+closeButton.getWidth()+5, closeButton.getY());
		seedButton.setVisible(world.settings.seedString!=null);

		diagramOption = new OptionBox(box, "Show:", diagramOptions.length, null) {
			@Override
			protected void selectOption(int value) {
				diagram = diagramOptions[value];
				createPlots();
			}
			@Override
			protected String formatOption(int value) {
				return diagramOptions[value].title;
			}
		};
		diagramOption.setSize(260, diagramOption.getHeight());
		diagramOption.setLocation(box.getWidth()-diagramOption.getWidth()-10, closeButton.getY()+closeButton.getHeight()/2f-diagramOption.getHeight()/2f);
		diagram = diagramOptions[diagramOption.findOption(diagramIndex(diagram))];
		createPlots();
	}
	
	private int diagramIndex(Diagram diagram) {
		for(int i=0; i<diagramOptions.length; i++) {
			if(diagramOptions[i]==diagram)
				return i;
		}
		return 0;
	}

	private Shape[] plots = null;
	private int plotMin, plotMax;
	private float scaleX, scaleY;
	private Point2D zero = new Point2D.Float();

	private void createPlots() {
		int numPlots = diagram.plotNames.length;
		int numPoints = world.history.numPoints();
		if(numPoints<2)
			numPoints = 2;
		
		plots = new Shape[numPlots];
		
		plotMin = 0;
		plotMax = 0;
		for(int i=0; i<numPlots; i++) {
			Path2D p = new Path2D.Float();
			for(int x=0; x<numPoints; x++) {
				int y = diagram.getValue(world.history.getPoint(x), i);
				if(y<plotMin)
					plotMin = y;
				if(y>plotMax)
					plotMax = y;
				if(x==0)
					p.moveTo(x, -y);
				else
					p.lineTo(x, -y);
			}
			plots[i] = p;
		}
		
		AffineTransform tx = new AffineTransform();
		scaleX = plotWidth / (float)(numPoints-1);
		scaleY = plotHeight / (float)(plotMax-plotMin+1);
		tx.scale(scaleX, scaleY);
		tx.translate(0, plotMin);
		for(int i=0; i<numPlots; i++) {
			plots[i] = tx.createTransformedShape(plots[i]);
		}
		tx.transform(new Point2D.Float(0, 0), zero);
	}
	
	@Override
	protected void paintBoxContents(GraphAssist g) {
		int x = 10;
		int y = 50;
		
		TurnStatistics stats = world.history.stats();
		g.setFont(Res.fontLarge);
		g.setColor(Color.WHITE);
		g.drawString(String.format("Turn: %s", stats.turn), x, y, GraphAssist.LEFT, GraphAssist.TOP);
		
		y+= 50;
		g.setFont(Res.font);
		diagram.paintLegend(g, x, y, stats, world);
		
		g.pushTx();
		g.translate(box.getWidth()-plotWidth-10, 50+plotHeight);
		g.fillRect(0, -plotHeight, plotWidth, plotHeight, Color.BLACK);
		g.resetStroke();
		if(plotMin<0) {
			g.fillRect(0, (float)zero.getY(), plotWidth, (float)-zero.getY(), new Color(0x220000));
			g.line(0, (float)zero.getY(), plotWidth, (float)zero.getY(), new Color(0x550000));
		}
		g.drawRect(0, -plotHeight, plotWidth, plotHeight, Res.uiBorderDark);
		g.pushAntialiasing(true);
		g.pushPureStroke(true);
		int numPlots = diagram.plotNames.length;
		g.graph.setStroke(new BasicStroke(2f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL));
		for(int i=numPlots-1; i>=0; i--) {
			g.setColor(diagram.plotColors[i]);
			g.graph.draw(plots[i]);
		}
		g.popPureStroke();
		g.popAntialiasing();
		g.popTx();
		
		super.paintBoxContents(g);
	}
	
}
