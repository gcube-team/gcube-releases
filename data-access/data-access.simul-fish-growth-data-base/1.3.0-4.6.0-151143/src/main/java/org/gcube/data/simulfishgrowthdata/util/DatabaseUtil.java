package org.gcube.data.simulfishgrowthdata.util;

import java.util.Set;
import java.util.TreeSet;

public class DatabaseUtil {
	static public final String GLOBAL_OWNER = "global";
	static private final String globalNameSeparator = "_";

	// concatenate the ids in order to produce the name
	static public synchronized String implodeGlobalName(final Set<Long> sites) {
		return String.format("%s%s%s", globalNameSeparator,
				com.google.common.base.Joiner.on(globalNameSeparator).skipNulls().join(sites), globalNameSeparator);
	}

	static public synchronized Set<Long> explodeGlobalName(final String name) {
		Iterable<String> parts = com.google.common.base.Splitter.on(globalNameSeparator).trimResults()
				.omitEmptyStrings().split(name);
		Set<Long> toRet = new TreeSet<>();
		for (String part : parts) {
			toRet.add(Long.parseLong(part));
		}
		return toRet;
	}
}
