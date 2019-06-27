package org.gcube.common.resources.gcore;

import org.gcube.common.resources.gcore.Resources;

public class TestUtils {

	// helper: we use T instead of Resource as service instance does not extend it
	public static <T> T unmarshal(Class<T> clazz, String sample) throws Exception {

		return Resources.unmarshal(clazz,TestUtils.class.getClassLoader().getResourceAsStream(sample));
	}
}
