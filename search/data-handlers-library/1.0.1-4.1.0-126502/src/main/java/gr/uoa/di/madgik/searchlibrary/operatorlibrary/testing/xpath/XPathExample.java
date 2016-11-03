package gr.uoa.di.madgik.searchlibrary.operatorlibrary.testing.xpath;
//package xpath;
//
//import java.io.IOException;
//import java.io.StringReader;
//
//import org.w3c.dom.*;
//import org.xml.sax.InputSource;
//import org.xml.sax.SAXException;
//import javax.xml.parsers.*;
//import javax.xml.xpath.*;
//
//public class XPathExample {
//	static String xml = "";
//
//	public static void main(String[] args) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
//		xml = "<?xml version=\"1.0\" ?><t:root xmlns:t=\"http://gcube-system.org/namespaces/data/trees\" SpeciesServiceId=\"ITIS:687575\" storeId=\"1\" t:id=\"ITIS:687575\" t:source=\"fa90189b-0f83-4248-93e4-b871a6b198c3\"><provenance t:id=\"25\"><wasGeneratedBy t:id=\"27\">This information object has been generated via the Species Product Discovery service on 03-28-2013 by interfacing with ITIS, the Integrated Taxonomic Information System (http://www.itis.gov/)</wasGeneratedBy><wasDerivedFrom t:id=\"26\">Accessed through: the Integrated Taxonomic Information System (ITIS) at http://www.itis.gov on 03-28-2013</wasDerivedFrom></provenance><DwC t:id=\"6\"><taxonRemarks t:id=\"10\">valid</taxonRemarks><scientificName t:id=\"15\">Parachela</scientificName><class t:id=\"20\">Actinopterygii</class><scientificNameAuthorship t:id=\"7\">Steindachner, 1881</scientificNameAuthorship><nameAccordingTo t:id=\"16\">Accessed through: the Integrated Taxonomic Information System (ITIS) at http://www.itis.gov on 03-28-2013</nameAccordingTo><taxonomicStatus t:id=\"9\">VALID</taxonomicStatus><genus t:id=\"23\">Parachela</genus><modified t:id=\"13\"></modified><acceptedNameUsageID t:id=\"8\"></acceptedNameUsageID><phylum t:id=\"19\">Chordata</phylum><kingdom t:id=\"18\">Animalia</kingdom><vernacularNames t:id=\"24\"></vernacularNames><order t:id=\"21\">Cypriniformes</order><parentNameUsageID t:id=\"17\">ITIS:163342</parentNameUsageID><family t:id=\"22\">Cyprinidae</family><bibliographicCitation t:id=\"11\">This information object has been generated via the Species Product Discovery service on 03-28-2013 by interfacing with ITIS, the Integrated Taxonomic Information System (http://www.itis.gov/)</bibliographicCitation><scientificNameID t:id=\"12\">urn:lsid:itis.gov:itis_tsn:687575</scientificNameID><taxonRank t:id=\"14\">Genus</taxonRank></DwC><Properties t:id=\"2\"><property t:id=\"3\"><value t:id=\"5\">Catalog of Fishes, 08-Apr-2005, website (version 05-Apr-05). Acquired: 2005-04-08.</value><key t:id=\"4\">Source</key></property></Properties></t:root>";
////		xml = "<root xmlns:foo=\"http://www.foo.org/\" xmlns:bar=\"http://www.bar.org\"><actors><actor id=\"1\">Christian Bale</actor><actor id=\"2\">Liam Neeson</actor><actor id=\"3\">Michael Caine</actor></actors><foo:singers><foo:singer id=\"4\">Tom Waits</foo:singer><foo:singer id=\"5\">B.B. King</foo:singer><foo:singer id=\"6\">Ray Charles</foo:singer></foo:singers></root>";
//
//		InputSource is = new InputSource(new StringReader(xml));
//
//		DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
//		domFactory.setNamespaceAware(true); // never forget this!
//		DocumentBuilder builder = domFactory.newDocumentBuilder();
//		Document doc = builder.parse(is);
//
//		XPathFactory factory = XPathFactory.newInstance();
//		XPath xpath = factory.newXPath();
//		XPathExpression expr = xpath.compile("/*/*/*");
//
//		Object o = expr.evaluate(doc, XPathConstants.NODESET);
//
//		String result = null;
//		if (o instanceof String) {
//			result = (String) o;
//		} else if (o instanceof Boolean) {
//			result = o.toString();
//		} else if (o instanceof Double) {
//			result = o.toString();
//		} else if (o instanceof NodeList) {
//			NodeList nodes = (NodeList) o;
//			StringBuilder res = new StringBuilder();
//			for (int i = 0; i < nodes.getLength(); i++) {
//				res.append("<");
//				res.append(nodes.item(i).getNodeName());
//				res.append(">");
//				res.append(nodes.item(i).getTextContent());
//				res.append("</");
//				res.append(nodes.item(i).getNodeName());
//				res.append(">");
//			}
//			result = res.toString();
//		} else
//			throw new XPathFunctionException("Undefined xpath result: " + o.getClass().getName());
//		
//		System.out.println(o.getClass() + ":\n" + result);
//	}
//
//}
