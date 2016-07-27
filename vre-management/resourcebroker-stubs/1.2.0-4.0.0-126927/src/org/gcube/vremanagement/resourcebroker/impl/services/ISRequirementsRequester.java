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
 * Filename: ISRequirementsRequester.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.impl.services;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.apache.xerces.parsers.DOMParser;
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
import org.gcube.vremanagement.resourcebroker.utils.assertions.Assertion;
import org.gcube.vremanagement.resourcebroker.utils.console.PrettyFormatter;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.PackageElem;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementElemPath;
import org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.RequirementRelationType;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Consists of a proxy between the {@link org.gcube.vremanagement.resourcebroker.impl.services.BrokerService} and the gCube
 * Information System.
 * Gets the requirements for a given package.
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ISRequirementsRequester {
	/** For internal use only. For logging facilities. */
	private static GCUBELog logger = new GCUBELog(ISRequirementsRequester.class, BrokerConfiguration.getProperty("LOGGING_PREFIX"));

	public static final List<Requirement> getRequirements(final GCUBEScope queryScope, final PackageElem pkg)
	throws Exception {
		Assertion<GCUBEFault> checker = new Assertion<GCUBEFault>();
		checker.validate(queryScope != null, new GCUBEFault("Invalid scope"));
		checker.validate(
				pkg != null &&
				pkg.getServiceClass() != null &&
				pkg.getServiceName() != null &&
				pkg.getPackageName() != null,
				new GCUBEFault("Invalid package param"));

		ISClient client = GHNContext.getImplementation(ISClient.class);
		GCUBEGenericQuery isQuery = null;
		isQuery = client.getQuery(GCUBEGenericQuery.class);
		isQuery.setExpression(QueryLoader.getQuery(QueryPath.GET_SERVICE_REQS));

		isQuery.addParameters(
				new QueryParameter(
						"SERVICE_CLASS",
						pkg.getServiceClass()));
		isQuery.addParameters(
				new QueryParameter(
						"SERVICE_NAME",
						pkg.getServiceName()));
		isQuery.addParameters(
				new QueryParameter(
						"SERVICE_VERSION",
						pkg.getServiceVersion()));
		isQuery.addParameters(
				new QueryParameter(
						"PKG_NAME",
						pkg.getPackageName()));

		List<XMLResult> results = client.execute(isQuery, queryScope);
		if (results == null) {
			return null;
		}

		logger.info(PrettyFormatter.bold("[IS-REQS]") + "found: " +
				PrettyFormatter.underlined("[" + results.size() + "] matching elems"));

		if (results != null && results.size() > 0) {
			List<Requirement> reqs = new Vector<Requirement>();
			String rawRequirements = null;

			for (XMLResult elem : results) {
				// Now the required software can be both a main or a (sub-)package
				// The requirements mu be taken for the chose one.

				try {
					rawRequirements = elem.evaluate("/RequirementQuery/Results/Result/GHNRequirements").get(0);
				} catch (ISResultEvaluationException e) {
					continue;
				}
				logger.info(rawRequirements);

				reqs = convertRowReqs(rawRequirements);
				logger.info(PrettyFormatter.bold("[IS-REQS]") + "found: " +
						PrettyFormatter.underlined("[" + reqs.size() + "] requirements for matching elems"));
			}
			return reqs;
		}
		return null;
	}

	/**
	 * <p>
	 * Converts requirements from a row form:
	 * <pre>
	 * &lt;GHNRequirements&gt;
	 *   &lt;Requirement category="MEM_RAM_AVAILABLE" operator="le" requirement="" value="1000"/&gt;
	 *   &lt;Requirement category="PLATFORM" operator="eq" requirement="" value="i368"/&gt;
	 *   &lt;Requirement category="OS" operator="exist" requirement="" value="Linux"/&gt;
	 * &lt;/GHNRequirements&gt;
	 * </pre>
	 * to a list of object {@link org.gcube.vremanagement.resourcebroker.utils.serialization.types.requirements.Requirement}
	 * representation of such requirements.
	 * </p>
	 * @param rawRequirements
	 * @return
	 */
	private static List<Requirement> convertRowReqs(final String rawRequirements) {
		if (rawRequirements == null || rawRequirements.trim().length() == 0) {
			return null;
		}

		List<Requirement> retval = new Vector<Requirement>();

		try {
			DOMParser parser = new DOMParser();
			parser.parse(new InputSource(new StringReader(rawRequirements)));
			Document doc = parser.getDocument();
			NodeList ns = doc.getElementsByTagName("Requirement");
			for (int i = 0; i < ns.getLength(); i++) {
				try {
					Node node = ns.item(i);
					NamedNodeMap attrs = node.getAttributes();

					// Converts the operators
					String operator = attrs.getNamedItem("operator").getNodeValue();
					RequirementRelationType convertedOperator = null;
					if (operator != null && operator.trim().length() > 0) {
						if (operator.trim().compareToIgnoreCase("le") == 0) {
							convertedOperator = RequirementRelationType.LESS_OR_EQUAL;
						}
						if (operator.trim().compareToIgnoreCase("eq") == 0) {
							convertedOperator = RequirementRelationType.EQUAL;
						}
						if (operator.trim().compareToIgnoreCase("exist") == 0) {
							convertedOperator = RequirementRelationType.CONTAINS;
						}
						if (operator.trim().compareToIgnoreCase("gt") == 0) {
							convertedOperator = RequirementRelationType.GREATER;
						}
						if (operator.trim().compareToIgnoreCase("ge") == 0) {
							convertedOperator = RequirementRelationType.GREATER_OR_EQUAL;
						}
						if (operator.trim().compareToIgnoreCase("ne") == 0) {
							convertedOperator = RequirementRelationType.NOT_EQUAL;
						}
						if (operator.trim().compareToIgnoreCase("lt") == 0) {
							convertedOperator = RequirementRelationType.LESS;
						}
					}

					String key = attrs.getNamedItem("requirement").getNodeValue();
					if (key != null && key.trim().length() > 0) {
						// Creates the requirement
						try {
							retval.add(
									new Requirement(
											RequirementElemPath.valueOf(attrs.getNamedItem("category").getNodeValue()),
											attrs.getNamedItem("requirement").getNodeValue(),
											convertedOperator,
											attrs.getNamedItem("value").getNodeValue()));
						} catch (java.lang.IllegalArgumentException e) {} // category not valid the req will be skipped
					} else {
						try {
							// Creates the requirement
							retval.add(new Requirement(RequirementElemPath
									.valueOf(attrs.getNamedItem("category")
											.getNodeValue()), convertedOperator, attrs
											.getNamedItem("value").getNodeValue()));
						} catch (java.lang.IllegalArgumentException e) {}
					}
				} catch (NullPointerException e) {} // some attributes have not been specified
			} // end of loop on single requirement

			return retval;
		} catch (SAXException e) {
			logger.error(e);
			return null;
		} catch (IOException e) {
			logger.error(e);
			return null;
		}
	}

}
