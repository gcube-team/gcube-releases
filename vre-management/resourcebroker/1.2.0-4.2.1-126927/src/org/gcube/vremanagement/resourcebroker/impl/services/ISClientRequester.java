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
 * Filename: ISClientRequester.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.services;

import java.util.List;
import java.util.Vector;

import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.faults.GCUBEFault;
import org.gcube.common.core.informationsystem.client.ISClient;
import org.gcube.common.core.informationsystem.client.QueryParameter;
import org.gcube.common.core.informationsystem.client.XMLResult;
import org.gcube.common.core.informationsystem.client.XMLResult.ISResultEvaluationException;
import org.gcube.common.core.informationsystem.client.queries.GCUBEGenericQuery;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.resourcebroker.impl.configuration.BrokerConfiguration;
import org.gcube.vremanagement.resourcebroker.impl.support.queries.QueryLoader;
import org.gcube.vremanagement.resourcebroker.impl.support.queries.QueryPath;
import org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor;
import org.gcube.vremanagement.resourcebroker.impl.support.types.Tuple;
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;

/**
 * Consists of a proxy between the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService} and the gCube
 * Information System.
 * Holds the persistent stand-alone resource where stores the status
 * of monitored gCube Resources.
 * After a delay the information about the monitored resources is refreshed
 * by {@link org.gcube.vremanagement.resourcebroker.impl.support.threads.TUpdateGHNProfiles} thread.
 *
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ISClientRequester {
	/** For internal use only. For logging facilities. */
	private static GCUBELog logger = new GCUBELog(ISClientRequester.class, BrokerConfiguration.getProperty("LOGGING_PREFIX") + "::[IS-REQ]");

	/**
	 * Returns the list of {@link org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor} declared in this scope.
	 *
	 * @param queryScope the scope in which the query must be executed.
	 * @throws Exception if something goes wrong.
	 * @return a list of {@link org.gcube.vremanagement.resourcebroker.impl.support.types.GHNDescriptor} elems.
	 */
	public static final List<GHNDescriptor> getRIOnGHNs(final GCUBEScope queryScope) throws Exception {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery isQuery = null;
		isQuery = client.getQuery(GCUBEGenericQuery.class);
		isQuery.setExpression(QueryLoader.getQuery(QueryPath.COUNT_RI_ON_DYNAMIC_ALIVE_GHN));
		isQuery.addParameters(new QueryParameter("MAXWAIT", BrokerConfiguration.getProperty("LIVE_GHN_MAX_MINUTES")));
		List<XMLResult> results = client.execute(isQuery, queryScope);
		List<GHNDescriptor> newGHNList = new Vector<GHNDescriptor>();

		if (results == null || results.size() == 0) {
			logger.warn("no GHNDescriptor found for scope: " + queryScope.toString());
			return null;
		}

		logger.debug("found *(" + results.size() + ")* GHN in scope: " + queryScope.toString());

		for (XMLResult elem : results) {
			try {
				List<String> riNums =  elem.evaluate("/RIONGHN/AllocatedRI/text()");
				List<String> ghnIDs = elem.evaluate("/RIONGHN/ID/text()");
				if (riNums != null && riNums.size() > 0 && ghnIDs != null && ghnIDs.size() > 0) {
					newGHNList.add(new GHNDescriptor(
						// The number of allocated RI
						Integer.parseInt(riNums.get(0)),
						// The ID of ghn
						ghnIDs.get(0),
						queryScope,
						elem
					));
				}
			} catch (java.lang.NumberFormatException e){}
		}
		return newGHNList;
	}

	/**
	 * <p>
	 * Given an RI identifier and the scope in which it shoud be retrieved,
	 * the IS is queried about the corresponding GHN on which the RI
	 * is actually running.
	 * </p>
	 * <p>
	 * Returns a {@link Tuple} of two elements RI_ID and GHN_ID.
	 * </>
	 * @param queryScope the scope of RI
	 * @param riID the unique ID of the RunningInstance
	 * @return null if no correspondence is found
	 * @throws Exception if IS query fails
	 */
	public static final Tuple<String> getRIAndGHN(final GCUBEScope queryScope, final String riID) throws Exception  {
		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery isQuery = null;
		isQuery = client.getQuery(GCUBEGenericQuery.class);
		isQuery.setExpression(QueryLoader.getQuery(QueryPath.GET_GHN_FOR_RI));
		isQuery.addParameters(
				new QueryParameter(
						"RI_ID",
						riID));

		List<XMLResult> results = client.execute(isQuery, queryScope);

		if (results == null || results.size() == 0) {
			logger.warn("no GHNDescriptor found for scope: " + queryScope.toString());
			return null;
		}

		logger.debug("found *(" + results.size() + ")* GHN in scope: " + queryScope.toString());

		// No more than one elements expected
		// since a single RI can run on an unique GHN.
		for (XMLResult elem : results) {
			try {
				return new Tuple<String>(
							elem.evaluate("/RunningInstance/RiID/text()").get(0),
							elem.evaluate("/RunningInstance/GHNID/text()").get(0));
			} catch (ISResultEvaluationException e) {
				logger.error(e);
			}
		}
		// No correspondence found
		return null;
	}

	/**
	 * Returns the {@link GHNDescriptor} for a given GHN_ID in a given scope.
	 * @param queryScope the scope in which the query must be executed.
	 * @param ghnID the ghn identifier to lookup.
	 * @throws Exception if something goes wrong.
	 */
	public static final GHNDescriptor getRIOnGHNByID(final GCUBEScope queryScope, final String ghnID) throws Exception {

		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(queryScope != null, new GCUBEFault("Invalid scope parameter. null not allowed."));
		checker.validate(ghnID != null && ghnID.trim().length() > 0, new GCUBEFault("Invalid ghnID parameter received."));

		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery isQuery = null;
		isQuery = client.getQuery(GCUBEGenericQuery.class);
		isQuery.setExpression(QueryLoader.getQuery(QueryPath.COUNT_RI_ON_DYNAMIC_ALIVE_GHN_BY_ID));
		isQuery.addParameters(
				new QueryParameter(
						"MAXWAIT",
						BrokerConfiguration.getProperty("LIVE_GHN_MAX_MINUTES")),
				new QueryParameter(
						"PARAM_GHNID",
						ghnID)
				);
		List<XMLResult> results = client.execute(isQuery, queryScope);

		if (results == null || results.size() == 0) {
			logger.warn("no GHNDescriptor found for scope: " + queryScope.toString());
			return null;
		}

		logger.debug("found *(" + results.size() + ")* GHN in scope: " + queryScope.toString());

		for (XMLResult elem : results) {
			try {
				return new GHNDescriptor(
							Integer.parseInt(elem.evaluate("/RIONGHN/AllocatedRI/text()").get(0)),
							elem.evaluate("/RIONGHN/ID/text()").get(0),
							queryScope,
							elem);
			} catch (ISResultEvaluationException e) {
				logger.error(e);
			}
		}
		return null;
	}

}
