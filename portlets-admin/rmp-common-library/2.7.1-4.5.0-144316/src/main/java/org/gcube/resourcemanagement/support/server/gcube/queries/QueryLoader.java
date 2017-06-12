/****************************************************************************
 *  This software is part of the gCube Project.
 *  Site: http://www.gcube-system.org/
 ****************************************************************************
 * The gCube/gCore software is licensed as Free Open Source software
 * conveying to the EUPL (http://ec.europa.eu/idabc/eupl).
 * The software and documentation is provided by its authors/distributors
 * "as is" and no expressed or
 * implied warranty is given for its use, quality or fitness for a
 * particular case.
 ****************************************************************************
 * Filename: QueryLoader.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.resourcemanagement.support.server.gcube.queries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.gcube.resourcemanagement.support.server.utils.ServerConsole;

/**
 * Resource_support utility to load at runtime the customized queries to submit to
 * the IS.
 * @author Daniele Strollo
 * @author Massimiliano Assante (ISTI-CNR)
 */
public class QueryLoader {
	private static final HashMap<QueryLocation, String> cachedQueries = new HashMap<QueryLocation, String>();
	private static final String LOG_PREFIX = "[QUERY-LOADER]";

	/**
	 * @param query the location of query file to load
	 * @return the string consisting of the xquery to submit to the server
	 * @throws Exception
	 */
	public static String getQuery(final QueryLocation query) throws Exception {
		if (query == null) {
			throw new Exception("Invalid query parameter. Null not allowed.");
		}

		ServerConsole.trace(LOG_PREFIX, "loading " + query.name());

		if (query != null && cachedQueries.containsKey(query)) {
			return cachedQueries.get(query);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(query.getFileName()));
		StringBuilder retval = new StringBuilder();
		String currLine = null;

		while ((currLine = in.readLine()) != null) {
			// a comment
			if (currLine.trim().length() > 0 && currLine.trim().startsWith("#")) {
				continue;
			}
			if (currLine.trim().length() == 0) { continue; }
			retval.append(currLine + System.getProperty("line.separator"));
		}
		in.close();

		String tmp = retval.toString();
		if (cachedQueries != null) {
			cachedQueries.put(query, tmp);
		}

		return tmp;
	}
}

