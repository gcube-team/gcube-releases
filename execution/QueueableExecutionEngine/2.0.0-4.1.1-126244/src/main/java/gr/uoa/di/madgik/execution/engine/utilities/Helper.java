package gr.uoa.di.madgik.execution.engine.utilities;

import gr.uoa.di.madgik.environment.exception.EnvironmentInformationSystemException;
import gr.uoa.di.madgik.environment.hint.EnvHintCollection;
import gr.uoa.di.madgik.execution.engine.ExecutionHandle;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement;
import gr.uoa.di.madgik.execution.plan.element.IPlanElement.PlanElementType;
import gr.uoa.di.madgik.is.InformationSystem;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

/**
 * A helper class with useful utilities
 * 
 * @author john.gerbesiotis - DI NKUA
 * 
 */
public class Helper {
	
	private static String localhost;
	// Static initialization of the localhost name
	private static String getLocalhost(EnvHintCollection envHints) {
		if (localhost == null) {
			try {
				localhost = InformationSystem.GetLocalNodeHostName();
				localhost += ":";
				localhost += InformationSystem.GetLocalNodePE2ngPort(envHints);
			} catch (EnvironmentInformationSystemException e) {
				localhost = null;
			}
		}
		
		return localhost;
	}
	
	/**
	 * Parse a plan and return all required hosting nodes.
	 * 
	 * @param handle
	 * @return A set of Hosting Nodes
	 */
	public static Set<String> getHostingNodes(ExecutionHandle handle) {
		Set<String> set = new HashSet<String>();

		IPlanElement root = handle.GetPlan().Root;

		if (!root.GetPlanElementType().equals(PlanElementType.Boundary)) {
			set.add(getLocalhost(handle.GetPlan().EnvHints));
		}

		try {
			InputStream is = new ByteArrayInputStream(root.ToXML().getBytes());
			DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			Document doc = builder.parse(is);

			XPath xpath = XPathFactory.newInstance().newXPath();
			XPathExpression expr = xpath.compile("//planElement[@type=\"Boundary\"]/boundaryConfig/@*");
			NodeList nodelist = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
			for (int i = 0; i < nodelist.getLength(); i++) {
				set.add(nodelist.item(i).getNodeValue() + ":" + nodelist.item(++i).getNodeValue());
			}
		} catch (Exception e) {
			return null;
		}

		return set;
	}
}
