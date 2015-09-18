package com.sobot.chat.utils;

import android.content.Context;

public class ResourceUtils {

	public static int[] getResourseIdByName(String packageName, String className,
			String name) {

		Class r = null;
		int[] id = new int[1];
		try {
			r = Class.forName(packageName + ".R");

			Class[] classes = r.getClasses();
			Class desireClass = null;

			for (int i = 0; i < classes.length; i++) {
				if (classes[i].getName().split("\\$")[1].equals(className)) {
					desireClass = classes[i];
					break;
				}
			}

			if (desireClass != null)
				//id = desireClass.getField(name).getInt(desireClass);
			id = (int[]) desireClass.getField(name).get(desireClass);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		}

		return id;

	}

	public static int getIdByName(Context context, String className,
			String resName) {
		String packageName = context.getPackageName();
		int indentify = context.getResources().getIdentifier(resName,
				className, packageName);
		return indentify;
	}
}
