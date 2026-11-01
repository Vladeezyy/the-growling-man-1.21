package net.voxla.growlingman.port.bus;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

/**
 * The mod's own event bus: listeners per event class (exact class, no hierarchy), in priority order. Posting an event
 * nobody listens to costs one map lookup.
 */
public final class GMBus {
	private static final Map<Class<?>, Listener<?>[]> LISTENERS = new IdentityHashMap<>();
	private static final Listener<?>[] NONE = new Listener<?>[0];

	private record Listener<E>(EventPriority priority, Consumer<E> handler) {
	}

	private GMBus() {
	}

	public static synchronized <E extends Event> void add(Class<E> type, EventPriority priority, Consumer<E> handler) {
		List<Listener<?>> list = new ArrayList<>(List.of(LISTENERS.getOrDefault(type, NONE)));
		list.add(new Listener<>(priority, handler));
		list.sort(Comparator.comparing(Listener::priority));
		LISTENERS.put(type, list.toArray(NONE));
	}

	public static boolean hasListeners(Class<? extends Event> type) {
		return LISTENERS.containsKey(type);
	}

	/**
	 * port/PerfScene: listeners off (the baseline; the client tick keeps running, the scenes are its listeners) and the
	 * time of the listeners per event class and thread
	 */
	public static volatile boolean muted, profiling;
	public static final Map<String, long[]> PROFILE = new java.util.concurrent.ConcurrentHashMap<>();

	/** port/PerfScene: time spent outside the bus (the Growling Man's own tick) */
	public static void record(String what, long nanos) {
		String key = (Thread.currentThread().getName().equals("Server thread") ? "server " : "client ") + what;
		long[] total = PROFILE.computeIfAbsent(key, k -> new long[2]);
		total[0] += nanos;
		total[1]++;
	}

	/** Runs the listeners; returns true if the event was canceled */
	@SuppressWarnings("unchecked")
	public static <E extends Event> boolean post(E event) {
		Listener<?>[] listeners = LISTENERS.getOrDefault(event.getClass(), NONE);
		long start = profiling && listeners.length > 0 ? System.nanoTime() : 0;
		if (muted && !(event instanceof ClientTickEvent))
			return false;
		for (Listener<?> listener : listeners) {
			((Consumer<E>) listener.handler()).accept(event);
		}
		if (start != 0) {
			String key = (Thread.currentThread().getName().equals("Server thread") ? "server " : "client ") + event.getClass().getName().replace("net.voxla.growlingman.port.bus.", "");
			long[] total = PROFILE.computeIfAbsent(key, k -> new long[2]);
			total[0] += System.nanoTime() - start;
			total[1]++;
		}
		return event.canceled();
	}
}
