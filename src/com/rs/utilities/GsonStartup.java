package com.rs.utilities;


import java.util.*;

public class GsonStartup {

	/**
	 * Adds all gson loaders to the list to load
	 */
	public static void loadAll() {
		//loaders.add(new LentItemsLoader());
		loaders.forEach(GsonCollections::initialize);
	}

	/**
	 * Gets the class for a loader
	 *
	 * @param clazz
	 * 		The class of the loader
	 */
	@SuppressWarnings("unchecked")
	public static <T> Optional<T> getOptional(Class<T> clazz) {
		GsonCollections<T> loader = getLoaderClass(clazz);
		if (loader == null) {
			return Optional.empty();
		}
		return (Optional<T>) Optional.of(loader);
	}
	
	/**
	 * Gets the class for a loader
	 *
	 * @param clazz
	 * 		The class of the loader
	 */
	@SuppressWarnings("unchecked")
	public static <T> T getClass(Class<T> clazz) {
		GsonCollections<T> loader = getLoaderClass(clazz);
		if (loader == null) {
			throw new IllegalStateException("Couldn't find GsonCollection loader for class:\t" + clazz);
		}
		return (T) (loader);
	}

	/**
	 * Gets the {@link GsonCollections} class instance of the class
	 *
	 * @param clazz
	 * 		The class
	 */
	@SuppressWarnings("unchecked")
	private static <T> GsonCollections<T> getLoaderClass(Class<T> clazz) {
		if (CACHED_CLASSES.get(clazz.getName()) != null) {
			return (GsonCollections<T>) CACHED_CLASSES.get(clazz.getName());
		}
		for (GsonCollections<?> loader : loaders) {
			if (loader.getClass().equals(clazz)) {
				CACHED_CLASSES.put(clazz.getName(), loader);
				return (GsonCollections<T>) loader;
			}
		}
		throw new IllegalStateException("Couldn't find GsonCollection loader for class:\t" + clazz);
	}

	private static final Map<String, GsonCollections<?>> CACHED_CLASSES = new HashMap<>();
 	private static final List<GsonCollections<?>> loaders = new ArrayList<>();
}
