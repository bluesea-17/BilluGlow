package com.billuglow;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Holds the client-only "who is glowing, and in what color" state.
 * Nothing here ever leaves your client — it's purely local rendering state.
 */
public final class GlowManager {
	private static final Map<UUID, Integer> GLOWING = new ConcurrentHashMap<>();

	private GlowManager() {}

	public static void setGlow(UUID uuid, int colorRgb) {
		GLOWING.put(uuid, colorRgb);
	}

	/** Returns the 0xRRGGBB color for this UUID, or null if it isn't glowing. */
	public static Integer getColor(UUID uuid) {
		return GLOWING.get(uuid);
	}

	public static boolean remove(UUID uuid) {
		return GLOWING.remove(uuid) != null;
	}

	public static void clearAll() {
		GLOWING.clear();
	}

	public static int size() {
		return GLOWING.size();
	}
}
