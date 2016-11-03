package org.gcube.test;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
/**
 * 
 */

/**
 * @author ceras
 *
 */
public class TestIsResources {

	private static final String GR_SECONDARY_TYPE = "StatisticalManager";
	private static final String GR_NAME = "Operators";

	/**
	 * @param args
	 */
//	public static void main(String[] args) {
//		try {
//			GCUBEScope scope = GCUBEScope.getScope("/gcube");
//
//			ISClient client = GHNContext.getImplementation(ISClient.class);
//
//			GCUBEGenericResourceQuery rtrQuery = client.getQuery(GCUBEGenericResourceQuery.class);
//			rtrQuery.addAtomicConditions(new AtomicCondition("/Profile/SecondaryType", GR_SECONDARY_TYPE));
//			rtrQuery.addAtomicConditions(new AtomicCondition("/Profile/Name", GR_NAME));
//
//			List<GCUBEGenericResource> rtrs = client.execute(rtrQuery, scope);
//			if (rtrs.size()==0) throw new Exception("Generic resource not found");
//
//			GCUBEGenericResource resource = rtrs.get(0);
//			String xml = resource.getBody();
//
//			System.out.println("xml = "+xml);
//
//			List<String> operators = new ArrayList<String>();
//			operators.add("pinco");
//			operators.add("pallino");
//			operators.add("ciccio");
//			operators.add("ceras");
//			operators.add("gianpaolo");
//			operators.add("coro");
//			
//			//////////////
//			System.out.println("Operators found:");
//			for (String op: operators)
//				System.out.println(op);
//			System.out.println();
//			//////////////
//			
//			
//			List<String> prevoiusOperators = getOperatorsFromXml(xml);			
//
//			//////////////
//			System.out.println("Operators in generic resource:");
//			for (String op: prevoiusOperators)
//				System.out.println(op);
//			System.out.println();
//			//////////////
//
//			List<String> newOperators = new ArrayList<String>();
//
//			StringBuilder newXml = new StringBuilder();
//			newXml.append("<operators>");
//			for (String op: operators) {
//				newXml.append("<operator>"+op+"</operator>");
//				if (!prevoiusOperators.contains(op))
//					newOperators.add(op);
//			}
//			newXml.append("</operators>");
//			
//			//////////////
//			System.out.println(newXml.toString());
//			System.out.println();
//
//			if (newOperators.size()==0)
//				System.out.println("No news");
//			else {
//				System.out.println("News:");
//				for (String op: newOperators)
//					System.out.println(op);
//			}
//			//////////////
//			
//			resource.setBody(newXml.toString());
//			ISPublisher publisher = GHNContext.getImplementation(ISPublisher.class);
//			publisher.updateGCUBEResource(resource, scope, null);
//
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//	}

	/**
	 * @param xml
	 * @return
	 * @throws IOException 
	 * @throws SAXException 
	 * @throws ParserConfigurationException 
	 */
	private static List<String> getOperatorsFromXml(String xml) throws SAXException, IOException, ParserConfigurationException {
		List<String> operators = new ArrayList<String>();
		
		DocumentBuilderFactory dbf =
				DocumentBuilderFactory.newInstance();
		DocumentBuilder db = dbf.newDocumentBuilder();
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(xml));

		Document doc = db.parse(is);
		NodeList nodes = doc.getElementsByTagName("operator");

		// iterate the employees
		for (int i = 0; i < nodes.getLength(); i++) {
			Element operator = (Element) nodes.item(i);
			String operatorId = getCharacterDataFromElement(operator);
			operators.add(operatorId);
		}
		
		return operators;
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

}
