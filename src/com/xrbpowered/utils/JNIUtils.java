package com.xrbpowered.utils;

import java.lang.reflect.Field;
import java.util.Arrays;

public abstract class JNIUtils {

	// http://fahdshariff.blogspot.co.uk/2011/08/changing-java-library-path-at-runtime.html
	public static void addLibraryPath(String pathToAdd) {
		try {
			final Field usrPathsField = ClassLoader.class.getDeclaredField("usr_paths");
			usrPathsField.setAccessible(true);
			final String[] paths = (String[]) usrPathsField.get(null);
			for(String path : paths) {
				if(path.equals(pathToAdd)) {
					return;
				}
			}
			final String[] newPaths = Arrays.copyOf(paths, paths.length + 1);
			newPaths[newPaths.length - 1] = pathToAdd;
			usrPathsField.set(null, newPaths);
		}
		catch(IllegalAccessException e) {
		}
		catch (NoSuchFieldException e) {
		}
	}

}
