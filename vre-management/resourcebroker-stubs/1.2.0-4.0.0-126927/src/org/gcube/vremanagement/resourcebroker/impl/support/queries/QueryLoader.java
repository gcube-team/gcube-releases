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

package org.gcube.vremanagement.resourcebroker.impl.support.queries;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.Configuration;
import org.gcube.vremanagement.resourcebroker.impl.resources.ResourceStorageManager;
import org.gcube.vremanagement.resourcebroker.impl.resources.SingletonResourceStorage;

/**
 * Support utility to load at runtime the customized queries to submit to
 * the IS.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class QueryLoader {
	private static GCUBELog logger = new GCUBELog(QueryLoader.class, Configuration.LOGGING_PREFIX);
	private static final String MEM_XQUERY_KEY = "CustomXQueries";

	@SuppressWarnings("unchecked")
	public static String getQuery(final QueryPath query) throws Exception {
		logger.info("[QUERY] loading " + query.name());

		HashMap<QueryPath, String> cachedQueries = null;
		try {
			SingletonResourceStorage status = ResourceStorageManager.INSTANCE.getResource();
			if (status != null) {
				if (!status.containsKey(MEM_XQUERY_KEY)) {
					status.addElement(MEM_XQUERY_KEY, new HashMap<QueryPath, String>());
				} else {
					cachedQueries = (HashMap<QueryPath, String>) status.getElem(MEM_XQUERY_KEY);
				}
			} else {
				logger.error("[QUERY] STATUS NOT AVAILABLE");
			}
		} catch (GCUBEFault e) {
			logger.error("[QUERY] Resource not ready");
		}

		if (cachedQueries != null && cachedQueries.containsKey(query)) {
			return cachedQueries.get(query);
		}

		BufferedReader in = new BufferedReader(new InputStreamReader(query.getFileName()));
		StringBuilder retval = new StringBuilder();
		String currLine = null;

		while ((currLine = in.readLine()) != null) {
			// a comment
			if (currLine.trim().length() > 0 && currLine.trim().startsWith(Configuration.QUERY_COMMENT_TOKEN)) { continue; }
			if (currLine.trim().length() == 0) { continue; }
			retval.append(currLine + System.getProperty("line.separator"));
		}
		in.close();

		if (cachedQueries != null) {
			cachedQueries.put(query, retval.toString());
		}

		return retval.toString();
	}
}
