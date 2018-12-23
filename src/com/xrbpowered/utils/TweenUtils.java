package com.xrbpowered.utils;

public abstract class TweenUtils {

	public static double tween(long t, long start, long duration) {
		return (t - start) / (double) duration;
	}

	public static double easeOut(double s) {
		return s*s;
	}

	public static double easeIn(double s) {
		s = 1.0-s;
		return 1.0-s*s;
	}

	public static double easeInOut(double s) {
		s *= 2.0;
		if(s<1) return 0.5*s*s;
		s -= 1.0;
		return -0.5*(s*(s-2.0)-1.0);
	}
	
	public static double wave(long t, long period) {
		return Math.sin(Math.PI * 2.0 * (double)t / (double)period)*0.5+0.5;
	}

}
