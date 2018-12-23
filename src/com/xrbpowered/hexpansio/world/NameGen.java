package com.xrbpowered.hexpansio.world;

import java.util.Random;

import com.xrbpowered.utils.RandomUtils;

public abstract class NameGen {

	private static String[] c = {
			"b", "br", "c", "cl", "cr",
			"ch", "d", "dr", "f", "fl",
			"fr", "g", "gl", "gn", "gr",
			"gh", "kh", "j", "k", "kl",
			"kn", "kr", "l", "m", "n",
			"p", "pl", "pr", "ph", "qu",
			"r", "s", "sm", "sn", "st",
			"sh", "t", "tr", "th", "thr",
			"v", "w", "wr", "x", "z",
			"zd", "zn", "zm", "zh", ""
		};
	private static int[] cw = {
			3, 2, 4, 2, 2,
			1, 6, 3, 3, 1,
			2, 4, 1, 1, 3,
			1, 1, 1, 3, 1,
			1, 2, 4, 5, 6,
			3, 1, 2, 2, 0,
			6, 6, 1, 2, 3,
			2, 6, 3, 3, 1,
			3, 2, 1, 3, 3,
			1, 1, 1, 1, 0
		};
	private static int[] cwstart = {
			3, 2, 4, 2, 2,
			1, 6, 3, 3, 1,
			2, 4, 1, 1, 3,
			1, 1, 5, 3, 1,
			1, 2, 4, 5, 6,
			3, 1, 2, 2, 2,
			6, 6, 1, 2, 3,
			2, 6, 3, 3, 1,
			3, 2, 1, 3, 3,
			1, 1, 1, 1, 15
		};
	private static String[] v = {
			"a", "e", "i", "o", "u",
			"ai", "ae", "au", "ea", "ee",
			"ei", "eo", "eu", "ia", "ie",
			"io", "ou", "ua"
		};
	private static int[] vw = {
			12, 12, 8, 5, 3,
			5, 1, 1, 3, 2,
			5, 2, 1, 4, 4,
			3, 2, 1
		};
	private static int[] vwend = {
			10, 1, 2, 2, 1,
			3, 0, 0, 6, 0,
			3, 1, 0, 6, 1,
			3, 0, 2
		};
	private static String[] vc = {
			"", "r", "n", "l", "d", "s"
		};
	private static int[] vcw = {
			15, 3, 1, 2, 0, 0
		};
	private static int[] vcwend = {
			10, 3, 1, 2, 1, 3
		};

	
	private static boolean close(String s1, String s2) {
		int n1 = s1.length();
		int n2 = s2.length();
		for(int i=0; i<n1; i++)
			for(int j=0; j<n2; j++) {
				if(s1.charAt(i)==s2.charAt(j))
						return true;
			}
		return false;
	}
	
	public static String generate(Random random, int min, int max) {
		if(max<min) max = min;
		int n = random.nextInt(max-min+1)+min;
		String[] nc = new String[n];
		String[] nv = new String[n];
		String[] nvc = new String[n];
		
		StringBuilder sb = new StringBuilder();
		for(int i=0; i<n; i++) {
			do {
				nc[i] = c[RandomUtils.weighted(random, i==0 ? cwstart : cw)];
			} while(i>0 && (close(nc[i], nc[i-1]) || nc[i].length()>1 && !nvc[i-1].isEmpty()));
			do {
				nv[i] = v[RandomUtils.weighted(random, i==n-1 ? vwend : vw)];
			} while(i>0 && (i<n-1 && nv[i].length()>1 && nv[i-1].length()>1) || close(nv[i], nc[i]));
			nvc[i] =(nv[i].length()>1) ? "" : vc[RandomUtils.weighted(random, i==n-1 ? vcwend : vcw)];
			
			sb.append(nc[i]);
			sb.append(nv[i]);
			sb.append(nvc[i]);
		}
		
		if(sb.length()>2)
			return sb.substring(0, 1).toUpperCase() + sb.substring(1);
		else
			return generate(random, min, max);
	}
	
	public static String generate(Random random) {
		return generate(random, 1, 3);
	}
	
	public static void main(String[] args) {
		Random random = new Random();
		for(int i=0; i<50; i++)
			System.out.println(generate(random));
	}
}
