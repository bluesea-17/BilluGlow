package com.billuglow;

import java.util.LinkedHashMap;
import java.util.Map;

public final class ColorUtil {
	private static final Map<String, Integer> NAMED = new LinkedHashMap<>();

	static {
		NAMED.put("white", 0xFFFFFF);
		NAMED.put("black", 0x000000);
		NAMED.put("gray", 0x555555);
		NAMED.put("red", 0xFF5555);
		NAMED.put("green", 0x55FF55);
		NAMED.put("lime", 0x00FF00);
		NAMED.put("blue", 0x5555FF);
		NAMED.put("yellow", 0xFFFF55);
		NAMED.put("cyan", 0x00FFFF);
		NAMED.put("aqua", 0x55FFFF);
		NAMED.put("teal", 0x16A2A2);
		NAMED.put("pink", 0xFF55FF);
		NAMED.put("magenta", 0xFF00FF);
		NAMED.put("purple", 0xAA00AA);
		NAMED.put("orange", 0xFFAA00);
	}

	private ColorUtil() {}

	/**
	 * Parses a color name (e.g. "cyan") or a hex string ("#1abc9c" or "1abc9c").
	 * Returns null if the input isn't a recognized color.
	 */
	public static Integer parse(String input) {
		if (input == null || input.isEmpty()) {
			return null;
		}
		String key = input.trim().toLowerCase();
		if (NAMED.containsKey(key)) {
			return NAMED.get(key);
		}
		String hex = key.startsWith("#") ? key.substring(1) : key;
		if (hex.matches("^[0-9a-f]{6}$")) {
			try {
				return Integer.parseInt(hex, 16);
			} catch (NumberFormatException e) {
				return null;
			}
		}
		return null;
	}

	public static Iterable<String> names() {
		return NAMED.keySet();
	}
}
