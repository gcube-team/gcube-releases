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
 * Filename: ReqConverter.java
 ****************************************************************************
 * @author <a href="mailto:daniele.strollo@isti.cnr.it">Daniele Strollo</a>
 ***************************************************************************/

package org.gcube.vremanagement.resourcebroker.local.testsuite;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;
import java.util.Vector;

import org.apache.xerces.parsers.DOMParser;
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
 * @author Daniele Strollo (ISTI-CNR)
 */
public class ReqConverter {

	private static List<Requirement> doJob() {
		String rawRequirements = "<GHNRequirements>\n"
				+ "<Requirement category=\"MEM_RAM_AVAILABLE\" operator=\"le\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement category=\"INVALIDFIELD\" operator=\"le\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement category=\"INVALIDFIELD\" operator=\"\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement category=\"INVALIDFIELD\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement category=\"\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement category=\"\" operator=\"le\" requirement=\"\" value=\"1000\"/>\n"
				+ "<Requirement />\n"
				+ "<Requirement category=\"INVALIDFIELD\" requirement=\"\" />\n"
				+ "<Requirement category=\"INVALIDFIELD\" requirement=\"\" value=\"\"/>\n"
				+ "<Requirement category=\"PLATFORM\" operator=\"eq\" requirement=\"\" value=\"i368\"/>\n"
				+ "<Requirement category=\"OS\" operator=\"exist\" requirement=\"\" value=\"Linux\"/>\n"
				+ "</GHNRequirements>\n";

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
			e.printStackTrace();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		List<Requirement> vls = doJob();
		System.out.println("Valid requirements: " + vls.size());
	}

}
