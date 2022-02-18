package jshdesktop;

public class Clipboard {
	private static Object current;
	private static String type;

	public static void setClip(Object cur) {
		current = cur;
	}
	
	public static void setClip(Object cur, String type) {
		current = cur;
		Clipboard.type = type;
	}

	public static Object getClip() {
		return current;
	}

	public static String getClipType() {
		return type;
	}

}
