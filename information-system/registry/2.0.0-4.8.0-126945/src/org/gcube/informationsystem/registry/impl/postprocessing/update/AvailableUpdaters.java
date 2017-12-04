package org.gcube.informationsystem.registry.impl.postprocessing.update;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.utils.logging.GCUBELog;

public class AvailableUpdaters {
	
	private static Map<String, Updater<?>> updaters = new HashMap<String, Updater<?>>();
	private static GCUBELog logger = new GCUBELog(AvailableUpdaters.class);
	
	public static void register(Updater<?> updater) {
		updaters.put(updater.getName(), updater);
		logger.debug ("Updater " +  updater.getName() + " registered");
	}
	
	public static Updater<?> getPurger(String updaterName) {
		return updaters.get(updaterName);	
	}
}
