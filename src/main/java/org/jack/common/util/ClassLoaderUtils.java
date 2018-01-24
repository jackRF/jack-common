package org.jack.common.util;

public class ClassLoaderUtils {
	public boolean isPresent(String className){
		return loadClass(className)!=null;
	}
	public static Class<?> loadClass(String className){
		try {
			return Class.forName(className);
		} catch (ClassNotFoundException e) {
		}
		return null;
	}
}
