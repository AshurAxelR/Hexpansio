package com.xrbpowered.hexpansio.res;

import java.awt.image.BufferedImage;

import com.xrbpowered.zoomui.GraphAssist;

public class ImageAtlas {

	public final int imgSize;
	public final BufferedImage image;
	private final int cols;

	public ImageAtlas(BufferedImage image, int imgSize) {
		this.image = image;
		this.imgSize = imgSize;
		this.cols = image.getWidth() / imgSize;
	}
	
	public void draw(GraphAssist g, int x, int y, int size, int subImage) {
		int sy = (subImage / cols) * imgSize;
		int sx = (subImage % cols) * imgSize;
		g.graph.drawImage(image, x, y, x+size, y+size, sx, sy, sx+imgSize, sy+imgSize, null);
	}

}
