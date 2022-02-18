package jshdesktop;

public class Clipboard {
	private static Object current;

	public static void setClipboard(Object cur) {
		current = cur;
	}

	public static Object getClipboard() {
		return current;
	}

}
