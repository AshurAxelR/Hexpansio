package com.xrbpowered.utils;

import java.awt.Color;

public abstract class ColorUtils {

	public static Color alpha(Color color, double s) {
		s = MathUtils.snap(s);
		return new Color(
				color.getRed(),
				color.getGreen(),
				color.getBlue(),
				(int)Math.round(s*255.0)
			);
	}

	public static Color blend(Color color0, Color color1, double s) {
		s = MathUtils.snap(s);
		return new Color(
				(int)Math.round(MathUtils.lerp(color0.getRed(), color1.getRed(), s)),
				(int)Math.round(MathUtils.lerp(color0.getGreen(), color1.getGreen(), s)),
				(int)Math.round(MathUtils.lerp(color0.getBlue(), color1.getBlue(), s))
			);
	}

}
