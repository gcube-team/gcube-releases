package org.gcube.informationsystem.registry.impl.postprocessing.remove;

import java.util.HashMap;
import java.util.Map;

import org.gcube.common.core.utils.logging.GCUBELog;

public class AvailablePurgers {

	private static Map<String, Purger<?>> purgers = new HashMap<String, Purger<?>>();
	private static GCUBELog logger = new GCUBELog(AvailablePurgers.class);
	
	public static void register(Purger<?> purger) {
		purgers.put(purger.getName(), purger);
		logger.debug ("Purger " +  purger.getName() + " registered");
	}
	
	public static Purger<?> getPurger(String purgerName) {
		return purgers.get(purgerName);	
	}
	
}
