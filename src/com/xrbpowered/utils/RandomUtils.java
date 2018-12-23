package com.xrbpowered.utils;

import java.util.List;
import java.util.Random;

public abstract class RandomUtils {

	public static int weighted(Random random, int[] w) {
		int max = 0;
		for(int i = 0; i < w.length; i++)
			max += w[i];
		if(max == 0)
			return 0;
		int x = random.nextInt(max);
		for(int i = 0;; i++) {
			if(x < w[i])
				return i;
			x -= w[i];
		}
	}

	public static <T> T item(Random random, T[] array) {
		return array[random.nextInt(array.length)];
	}

	public static <T> T item(Random random, List<T> list) {
		return list.get(random.nextInt(list.size()));
	}

	public static int range(Random random, int min, int max) {
		return random.nextInt(max - min + 1) + min;
	}

	public static long nextSeed(long seed, long add) {
		// Multiply by Knuth's Random (Linear congruential generator) and add offset
		seed *= seed * 6364136223846793005L + 1442695040888963407L;
		seed += add;
		return seed;
	}

	public static long seedXY(long seed, long x, long y) {
		seed = nextSeed(seed, x);
		seed = nextSeed(seed, y);
		seed = nextSeed(seed, x);
		seed = nextSeed(seed, y);
		return seed;
	}

	public static double gamma(Random random, double a, double b) {
		// Translated from GSL library
		// Based on Marsaglia and Tsang’s Method
		if(a < 1) {
			double u = random.nextDouble();
			return gamma(random, 1.0 + a, b) * Math.pow(u, 1.0 / a);
		}
		else {
			double x, v, u;
			double d = a - 1.0 / 3.0;
			double c = (1.0 / 3.0) / Math.sqrt(d);
			for(;;) {
				do {
					x = random.nextGaussian();
					v = 1.0 + c * x;
				} while (v <= 0);
				v = v * v * v;
				u = random.nextDouble();
				if(u < 1 - 0.0331 * x * x * x * x)
					break;
				if(Math.log(u) < 0.5 * x * x + d * (1 - v + Math.log(v)))
					break;
			}
			return b * d * v;
		}
	}
	
	public static double beta(Random random, double a, double b) {
		double x = gamma(random, a, 1.0);
		double y = gamma(random, b, 1.0);
		return x / (x+y);
	}
}
