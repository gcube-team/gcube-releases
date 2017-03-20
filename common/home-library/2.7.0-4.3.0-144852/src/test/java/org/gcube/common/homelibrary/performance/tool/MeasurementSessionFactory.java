/**
 * 
 */
package org.gcube.common.homelibrary.performance.tool;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class MeasurementSessionFactory {
	
	protected static final Map<String, MeasurementSession> sessions = new LinkedHashMap<String, MeasurementSession>();

	/**
	 * @param name the session name.
	 * @return the session.
	 */
	public static MeasurementSession getSession(String name)
	{
		if (sessions.containsKey(name)) return sessions.get(name);
		MeasurementSession session = new MeasurementSession(name);
		sessions.put(name, session);
		return session;
	}
	
	/**
	 * @return the sessions
	 */
	public static Map<String, MeasurementSession> getSessions() {
		return sessions;
	}
}
