/**
 * 
 */
package org.gcube.common.geoserverinterface.test;

import java.io.IOException;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.httpclient.MultiThreadedHttpConnectionManager;
import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.bean.CswLayersResult;
import org.gcube.common.geoserverinterface.bean.LayerCsw;
import org.gcube.common.geoserverinterface.cxml.CXml;
import org.gcube.common.geoserverinterface.cxml.CXmlManager;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;


/**
 * @author ceras
 *
 */
public class TestCsw {
	private static final String geonetworkUrl = "http://geoserver.d4science-ii.research-infrastructures.eu/geonetwork";
	private static final String gnUser = "admin";
	private static final String gnPwd = "admin";
	private static final int MAX_RECORDS = 100000;
	private static int SELECTOR = 1;

	private enum FILTER_TYPE {NO_FILTER, TITLE, ANY_TEXT};

	public static void main(String[] args) {
		if (SELECTOR==1)
			testNew();
		else {
			long startTime = new Date().getTime();
			try {
				String res;

				// SET HMC
				MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
				HttpMethodCall HMC = new HttpMethodCall(connectionManager, geonetworkUrl+"/srv/en", "", "");

				// LOGIN
				login(HMC);

				int maxRecords = 10;
				int startPosition  = 1;
				boolean sortByTitle = true;
				boolean sortAscendent = true;
				FILTER_TYPE filter = FILTER_TYPE.TITLE;
				String textToSearch = "eezall";

				if (maxRecords == 0)
					maxRecords = MAX_RECORDS;
				String cswQuery = "" +
						"<csw:GetRecords xmlns:csw='http://www.opengis.net/cat/csw/2.0.2' service='CSW' version='2.0.2' resultType='results' " +
						"	outputSchema='csw:Record' maxRecords='" + maxRecords + "' startPosition='" + startPosition + "'>\n"+

				"<csw:Query typeNames='csw:Record'>\n"+
				//				"	<csw:ElementSetName>full</csw:ElementSetName>\n"+
				//				"	<csw:ElementName>dct:abstract</csw:ElementName>\n"+
				"	<csw:ElementName>dc:title</csw:ElementName>\n"+
				"	<csw:ElementName>dc:identifier</csw:ElementName>\n"+
				"	<csw:ElementName>dc:URI</csw:ElementName>\n"+
				"	<csw:Constraint version='1.1.0'>\n"+
				"		<Filter xmlns='http://www.opengis.net/ogc' xmlns:gml='http://www.opengis.net/gml'>\n"+
				"			<And>\n"+
				"				<PropertyIsNotEqualTo>\n"+
				"					<PropertyName>title</PropertyName>\n"+
				"					<Literal>GeoServer Web Map Service</Literal>\n"+
				"				</PropertyIsNotEqualTo>\n"+
				"				<Not>\n"+
				"					<PropertyIsLike wildCard='*' singleChar='?' escapeChar='\'>\n"+
				"						<PropertyName>abstract</PropertyName>\n"+
				"						<Literal>*Group*</Literal>\n"+
				"					</PropertyIsLike>\n"+
				"				</Not>\n";

				if (filter==FILTER_TYPE.TITLE || filter==FILTER_TYPE.ANY_TEXT)
					cswQuery += 
					"				<PropertyIsLike wildCard='*' singleChar='?' escapeChar='\'>\n"+
							"					<PropertyName>" + (filter==FILTER_TYPE.TITLE ? "title" : "AnyText") + "</PropertyName>\n"+
							"					<Literal>%" + textToSearch + "%</Literal>\n"+
							"				</PropertyIsLike>\n";

				cswQuery += 
						"			</And>\n"+
								"		</Filter>\n"+
								"	</csw:Constraint>\n";

				if (sortByTitle)
					cswQuery +=
					"	<ogc:SortBy xmlns:ogc='http://www.opengis.net/ogc'>\n"+
							"		<ogc:SortProperty>\n"+
							"			<ogc:PropertyName>title</ogc:PropertyName>\n"+
							"			<ogc:SortOrder>" + (sortAscendent ? "ASC" : "DESC") + "</ogc:SortOrder>\n"+
							"		</ogc:SortProperty>\n"+
							"	</ogc:SortBy>\n";

				cswQuery += 
						"</csw:Query>\n"+
								"</csw:GetRecords>";

				//out(cswQuery);
				res = call(HMC, "csw", cswQuery);

				out(res);

				CXml result = new CXml(res);

				int nTot = Integer.parseInt(result.find("csw:SearchResults").attr("numberOfRecordsReturned"));

				result.find("csw:Record").each(new CXmlManager() {
					public void manage(int i, CXml record) {
						String id = record.child("dc:identifier").text();
						String title = record.child("dc:title").text();
						String name = record.child("dc:URI").attr("name");
						String geoserverUrl = record.child("dc:URI").text();

						System.out.println(i + ") " + id +"\t"+ title +"\t"+ name +"\t"+ geoserverUrl);
					}
				});

				System.out.println("OK");
			} catch (Exception e) {
				e.printStackTrace();
				System.out.println("general error");
			}
			long endTime = new Date().getTime();
			System.out.println("Tempo trascorso :"+(endTime-startTime)/1000);
		}
	}

	/**
	 * @param res
	 * @return 
	 */
	private static List<String> parseUuids(String res) {
		List<String> uuids = new ArrayList<String>();
		try {
			DocumentBuilderFactory dbf =
					DocumentBuilderFactory.newInstance();
			DocumentBuilder db = dbf.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(res));

			Document doc = db.parse(is);
			NodeList nodes = doc.getElementsByTagName("uuid");

			for (int i = 0; i < nodes.getLength(); i++) {
				Element uuid = (Element) nodes.item(i);
				uuids.add(getCharacterDataFromElement(uuid));
			}
			return uuids;
		}
		catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static String getCharacterDataFromElement(Element e) {
		Node child = e.getFirstChild();
		if (child instanceof CharacterData) {
			CharacterData cd = (CharacterData) child;
			return cd.getData();
		}
		return "?";
	}

	/**
	 * @param hMC 
	 * @param string
	 * @param string2
	 * @return
	 */
	private static String call(HttpMethodCall HMC, String req, String xml) {
		try {
			return HMC.CallPost(req, "<?xml version='1.0' encoding='UTF-8'?>" + xml, "text/xml");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @throws Exception 
	 * 
	 */
	private static void login(HttpMethodCall HMC) throws Exception {
		HMC.CallPost("xml.user.login",
				"<?xml version='1.0' encoding=\"UTF-8\"?>" 
						+ "<request>"
						+ "		<username>" + gnUser + "</username>"
						+ "		<password>" + gnPwd + "</password>"
						+ "</request>", "text/xml");
	}

	/**
	 * @param res
	 */
	private static void out(String res) {
		System.out.println("---------------------------------------\n"+res+"\n--------------------------------------");
	}

	private void asd() throws Exception {
		String res;

		// SET HMC
		MultiThreadedHttpConnectionManager connectionManager = new MultiThreadedHttpConnectionManager();
		HttpMethodCall HMC = new HttpMethodCall(connectionManager, geonetworkUrl+"/srv/en", "", "");

		// LOGIN
		login(HMC);

		//		res = call(HMC, "xml.search", "<request><category>interactiveResources</category></request>");
		//		List<String> uuids = parseUuids(res);
		//		int i=0;
		//		for (String uuid : uuids)
		//			if (i++>0) {
		//			res = call(HMC, "xml.metadata.get", "<request><uuid>"+uuid+"</uuid></request>");
		//			//Metadata m = parseMetadata(res);
		//			
		////			System.out.println(res);
		//			
		//			CXml metadata = new CXml(res);
		////			CXml resource = metadata.find("gmd:CI_OnlineResource");
		//			CXml resource = metadata.child("gmd:distributionInfo")
		//					.child("gmd:MD_Distribution")
		//					.child("gmd:transferOptions")
		//					.child("gmd:MD_DigitalTransferOptions")
		//					.child("gmd:onLine")
		//					.child("gmd:CI_OnlineResource");
		//
		//			//System.out.println("	  UUID: "+uuid);
		//			if (resource.isNull())
		//				System.err.println("	 null");
		//			else {
		//				String name = resource.child("gmd:name").child("gco:CharacterString").text();
		//				String geoserverUrl = resource.child("gmd:linkage").child("gmd:URL").text();
		//				String title = resource.child("gmd:description").child("gco:CharacterString").text();
		//				
		//				System.out.println(""+(i-1)+":	" + name + "	-	"+title);
		//				
		////				System.out.println("	  URL: " + geoserverUrl);
		////				System.out.println("	 NAME: " + name);
		////				System.out.println("	TITLE: " + title);
		////				System.out.println();
		//				
		////				System.out.println("UUID: "+uuid+"	NAME:"+name);
		//			}
		//		}

	}
	
	private static void testNew() {
		String res = ""
				+ "<?xml version=\"1.0\" encoding=\"UTF-8\"?>"
				+ "<csw:GetRecordsResponse xmlns:csw='http://www.opengis.net/cat/csw/2.0.2' xmlns:xsi='http://www.w3.org/2001/XMLSchema-instance' xsi:schemaLocation='http://www.opengis.net/cat/csw/2.0.2 http://schemas.opengis.net/csw/2.0.2/CSW-discovery.xsd'>"
				  + "<csw:SearchStatus timestamp='2012-06-21T00:54:06' />"
				  + "<csw:SearchResults numberOfRecordsMatched='6591' numberOfRecordsReturned='6591' elementSet='full' nextRecord='0'>";

				  for (int i=0; i<2000; i++) {  // each 5 records (total is 10000 records)
					  res += ""
							    + "<csw:Record xmlns:dct='http://purl.org/dc/terms/' xmlns:ows='http://www.opengis.net/ows' xmlns:geonet='http://www.fao.org/geonetwork' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
							      + "<dc:identifier>ba83f74e-44c9-43a2-b0b7-160f523f80d0</dc:identifier>"
							      + "<dc:title>TrueMarble.16km.2700x1350</dc:title>"
							      + "<dc:URI protocol='OGC:WMS-1.1.1-http-get-map' name='aquamaps:TrueMarble.16km.2700x1350' description='TrueMarble.16km.2700x1350'>http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&amp;</dc:URI>"
							    + "</csw:Record>"
							    + "<csw:Record xmlns:dct='http://purl.org/dc/terms/' xmlns:ows='http://www.opengis.net/ows' xmlns:geonet='http://www.fao.org/geonetwork' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
							      + "<dc:identifier>97b11ada-2f57-4161-a4ce-7ebfcfc24bf0</dc:identifier>"
							      + "<dc:title>biodiversity</dc:title>"
							      + "<dc:URI protocol='OGC:WMS-1.1.1-http-get-map' name='aquamaps:biodiversity' description='biodiversity'>http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&amp;</dc:URI>"
							    + "</csw:Record>"
							    + "<csw:Record xmlns:dct='http://purl.org/dc/terms/' xmlns:ows='http://www.opengis.net/ows' xmlns:geonet='http://www.fao.org/geonetwork' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
							      + "<dc:identifier>18641c17-0e4c-4cfe-84d0-37ad4b392784</dc:identifier>"
							      + "<dc:title>DepthMean</dc:title>"
							      + "<dc:URI protocol='OGC:WMS-1.1.1-http-get-map' name='aquamaps:depthMean' description='DepthMean'>http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&amp;</dc:URI>"
							    + "</csw:Record>"
							    + "<csw:Record xmlns:dct='http://purl.org/dc/terms/' xmlns:ows='http://www.opengis.net/ows' xmlns:geonet='http://www.fao.org/geonetwork' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
							      + "<dc:identifier>ac7393df-7213-41ca-a7d1-6389e001f7d4</dc:identifier>"
							      + "<dc:title>depthmean_annual</dc:title>"
							      + "<dc:URI protocol='OGC:WMS-1.1.1-http-get-map' name='aquamaps:depthmean_annual' description='depthmean_annual'>http://geoserver2.d4science.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&amp;</dc:URI>"
							    + "</csw:Record>"
							    + "<csw:Record xmlns:dct='http://purl.org/dc/terms/' xmlns:ows='http://www.opengis.net/ows' xmlns:geonet='http://www.fao.org/geonetwork' xmlns:dc='http://purl.org/dc/elements/1.1/'>"
							      + "<dc:identifier>12409123-348b-4c81-a9c4-b711d14e0fd5</dc:identifier>"
							      + "<dc:title>depthmean_annual</dc:title>"
							      + "<dc:URI protocol='OGC:WMS-1.1.1-http-get-map' name='aquamaps:depthmean_annual' description='depthmean_annual'>http://geoserver3.d4science.research-infrastructures.eu:80/geoserver/wms?SERVICE=WMS&amp;</dc:URI>"
							    + "</csw:Record>";
				  }
				  res += "</csw:SearchResults>"
				+ "</csw:GetRecordsResponse>";
		//System.out.println(res);
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser saxParser = factory.newSAXParser();

			final CswLayersResult result = new CswLayersResult();
			
			//			final List<LayerItem> layerItems = new ArrayList<LayerItem>();
			//			final List<GroupItem> groupItems = new ArrayList<GroupItem>();

			DefaultHandler handler = new DefaultHandler() {
				State state = State.START;
				//String uuid, name, title, geoserverUrl;
				//boolean endTitle=false, endIdentifier=false, endUri=false;
				LayerCsw currentLayer;
				StringBuffer buffer;
				
				public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
					switch (state) {
					case START:
						if (qName.equalsIgnoreCase("csw:GetRecordsResponse"))
							state = State.INSIDE_GET_RECORDS_RESPONSE;
						break;
					case INSIDE_GET_RECORDS_RESPONSE:
						if (qName.equalsIgnoreCase("csw:SearchResults")) {
							state = State.INSIDE_SEARCH_RESULTS;
							result.setResultLayersCount(Integer.parseInt(attributes.getValue("numberOfRecordsMatched")));
						}
						break;
					case INSIDE_SEARCH_RESULTS:
						if (qName.equalsIgnoreCase("csw:Record")) {
							state = State.INSIDE_RECORD;
							currentLayer = new LayerCsw();
						}
						break;
					case INSIDE_RECORD:
						if (qName.equalsIgnoreCase("dc:identifier")) {
							state = State.INSIDE_IDENTIFIER;			
							buffer = new StringBuffer();
						}
						else if (qName.equalsIgnoreCase("dc:title")) {
							state = State.INSIDE_TITLE;
							buffer = new StringBuffer();
						}
						else if (qName.equalsIgnoreCase("dc:URI")) {
							state = State.INSIDE_URI;
							currentLayer.setName(attributes.getValue("name"));
							buffer = new StringBuffer();
						}
						break;
					}
				}

				public void endElement(String uri, String localName, String qName) throws SAXException {
					if (qName.equalsIgnoreCase("dc:identifier")) {
						currentLayer.setUuid(buffer.toString());
						state = State.INSIDE_RECORD;
					} else if (qName.equalsIgnoreCase("dc:title")) {
						currentLayer.setTitle(buffer.toString());
						state = State.INSIDE_RECORD;
					} else if (qName.equalsIgnoreCase("dc:URI")) {
						currentLayer.setGeoserverUrl(buffer.toString());
						state = State.INSIDE_RECORD;
					} else
						if (qName.equalsIgnoreCase("csw:Record")) {
							state = State.INSIDE_SEARCH_RESULTS;
							result.addLayer(currentLayer);
						}
				}

				public void characters(char ch[], int start, int length) throws SAXException {
					if (state==State.INSIDE_IDENTIFIER || state==State.INSIDE_TITLE || state==State.INSIDE_URI)
						buffer.append(new String(ch, start, length));
				}

			};

			
			// PARSING SAX
			long start1 = new Date().getTime();			
			saxParser.parse(new InputSource(new StringReader(res)), handler);
			long end1 = new Date().getTime();
			
//			System.out.println("TOT: "+result.getResultLayersCount());
//			for (LayerCsw l : result.getLayers())
//				System.out.println("Uudi:"+l.getUuid()+"\tName: "+l.getName()+"\tTitle: "+l.getTitle()+"\tGUrl: "+l.getGeoserverUrl());

			
			// PARSING DOM
			long start2 = new Date().getTime();			
			
			CXml cxmlResult = new CXml(res);
			
			String nTot = cxmlResult.find("csw:SearchResults").attr("numberOfRecordsMatched");
			result.setResultLayersCount(Integer.parseInt(nTot));

			cxmlResult.find("csw:Record").each(new CXmlManager() {
				public void manage(int i, CXml record) {
					String id = record.child("dc:identifier").text();
					String title = record.child("dc:title").text();
					String name = record.child("dc:URI").attr("name");
					String geoserverUrl = record.child("dc:URI").text();
					
					if (id != null && title !=null && name!=null && geoserverUrl!=null)	result.addLayer(new LayerCsw(id, name, title, null, geoserverUrl));

//					System.out.println(i + ") " + id +"\t"+ title +"\t"+ name +"\t"+ geoserverUrl);
				}
			});
			
			long end2 = new Date().getTime();
			System.out.println("1("+start1+","+end1+"); 2("+start2+","+end2+")");
			System.out.println("SAX TIME: "+((end1-start1)) + "seconds");
			System.out.println("DOM TIME: "+((end2-start2)) + "seconds");

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	enum State {
		START, 
		INSIDE_GET_RECORDS_RESPONSE, 
		INSIDE_SEARCH_RESULTS, 
		INSIDE_RECORD, 
		INSIDE_IDENTIFIER, 
		INSIDE_TITLE, 
		INSIDE_URI
		};

}
