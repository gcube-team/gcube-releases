package gr.uoa.di.madgik.execution.utils;

import gr.uoa.di.madgik.execution.plan.ExecutionPlan;

import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;

public class ExecutionPlanAnalyser {
	private static int executeXPath(String xmlInput, String xslt) {
		try {
			InputSource source = new InputSource(new StringReader(xmlInput));

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(source);

			XPathFactory xpathfactory = XPathFactory.newInstance();
			XPath xpath = xpathfactory.newXPath();

			XPathExpression expr = xpath.compile(xslt);
			Object result = expr.evaluate(doc, XPathConstants.NUMBER);

			return ((Double) result).intValue();
		} catch (Exception e) {
			return -1;
		}
	}

	public static int countBoundaries(ExecutionPlan plan) {
		final String xslt = "count(/executionPlan/tree//planElement/boundaryConfig/@hostname[not(. = preceding::boundaryConfig/@hostname)])";
		String xmlInput;
		try {
			xmlInput = plan.Serialize();
		} catch (Exception e) {
			return -1;
		}
		return executeXPath(xmlInput, xslt);
	}

	public static int countOutputFiles(ExecutionPlan plan) {	//*[not(ancestor::planElement/@type = 'Boundary')]
		final String xslt = "count(/executionPlan/tree/planElement[@type='FileTransfer']/direction[@value='Store'])";
		String xmlInput;
		try {
			xmlInput = plan.Serialize();
		} catch (Exception e) {
			return -1;
		}
		return executeXPath(xmlInput, xslt);
	}

	public static int countInputFiles(ExecutionPlan plan) {
		final String xslt = "count(/executionPlan/tree/planElement[@type='FileTransfer']/direction[@value='Retrieve'])";
		String xmlInput;
		try {
//			xmlInput = Charset.forName("UTF-8").decode(ByteBuffer.wrap(Files.readAllBytes(Paths.get("bigPlan.xml")))).toString();
			xmlInput = plan.Serialize();
		} catch (Exception e) {
			return -1;
		}
		return executeXPath(xmlInput, xslt);
	}

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		System.out.println(ExecutionPlanAnalyser.countInputFiles(null));
	}
}
