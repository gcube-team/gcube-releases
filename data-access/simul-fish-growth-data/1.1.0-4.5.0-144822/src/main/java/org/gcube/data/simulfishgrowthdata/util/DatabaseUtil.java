package org.gcube.data.simulfishgrowthdata.util;

import java.util.ArrayList;
import java.util.TreeSet;
import java.util.List;
import java.util.Set;

import gr.i2s.fishgrowth.model.EntityWithId;

public class DatabaseUtil {
	static public final String GLOBAL_OWNER = "global";
	static private final String globalNameSeparator = "_";

	// concatenate the ids in order to produce the name
	static public synchronized String getGlobalName(Set<Long> sites) {

		return String.format("%s%s%s", globalNameSeparator, com.google.common.base.Joiner.on("_").skipNulls().join(sites),
				globalNameSeparator);
	}

	static public synchronized String getGlobalNameEnt(List<? extends EntityWithId> entities) {
		Set<Long> ids = new TreeSet<>();
		for (EntityWithId entityWithId : entities) {
			ids.add(entityWithId.getId());
		}
		return getGlobalName(ids);
	}
}
