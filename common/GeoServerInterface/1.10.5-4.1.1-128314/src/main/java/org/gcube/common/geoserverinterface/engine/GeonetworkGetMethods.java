package org.gcube.common.geoserverinterface.engine;

import java.util.ArrayList;
import java.util.List;

import org.gcube.common.geoserverinterface.GeoCaller.FILTER_TYPE;
import org.gcube.common.geoserverinterface.GeonetworkCommonResourceInterface.GeonetworkCategory;
import org.gcube.common.geoserverinterface.HttpMethodCall;
import org.gcube.common.geoserverinterface.bean.CswLayersResult;
import org.gcube.common.geoserverinterface.bean.CswRecord;
import org.gcube.common.geoserverinterface.bean.LayerCsw;
import org.gcube.common.geoserverinterface.cxml.CXml;
import org.gcube.common.geoserverinterface.cxml.CXmlManager;
import org.gcube.common.geoserverinterface.geonetwork.utils.ParserXpath;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class GeonetworkGetMethods {

	private static final Logger logger = LoggerFactory.getLogger(GeonetworkGetMethods.class);
	
	/**
	 * @uml.property name="hMC"
	 * @uml.associationEnd multiplicity="(1 1)"
	 */
	private HttpMethodCall HMC = null;
	private final String APPLICATIONXML = "application/xml";
	private final String XMLHARVESTINGGET = "xml.harvesting.get";
	private final String XMLMETADATAGET = "xml.metadata.get";
	private final String XMLHARVESTINGRUN = "xml.harvesting.run";
	private final String CSW = "csw";	private final String XMLSEARCH = "xml.search";
	public static final String GROUP = "Layer-Group";
	private static final int MAX_RECORDS = 100000;


	public GeonetworkGetMethods(HttpMethodCall HMC) {
		this.HMC = HMC;
	}

	public String getListHarvestings() {
		String res = null;
		try {

			// res = HMC.Call("csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml");

			res = HMC.CallPost(XMLHARVESTINGGET, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><requests/>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String getHarvestingById(String id) {
		String res = null;
		try {
			// res = HMC.Call("csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml");

			res = HMC.CallPost(XMLHARVESTINGGET, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><id>" + id + "</id></request>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String getMetadataByFileIdentifier(String uuid) {
		String res = null;
		try {

			// res = HMC.Call("csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml");

			res = HMC.CallPost(XMLMETADATAGET, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><uuid>" + uuid + "</uuid></request>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String getMetadataById(String id) {
		String res = null;
		try {

			// res = HMC.Call("csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml");

			res = HMC.CallPost(XMLMETADATAGET, "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request><id>" + id + "</id></request>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String updateHarvesting(String id) {
		String res = null;
		try {

			// res = HMC.Call("csw?request=GetCapabilities&service=CSW&acceptVersions=2.0.2&acceptFormats=application/xml");

			res = HMC.CallPost(XMLHARVESTINGRUN, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<request> " + "<id>" + id + "</id>" + "</request>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String searchLayerByTitleIsLike(String title) {

		String res = null;
		try {
			res = HMC.CallPost(CSW, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\" version=\"2.0.2\" resultType=\"results\" outputSchema=\"csw:IsoRecord\">" + "<csw:Query typeNames=\"gmd:MD_Metadata\">" + "<csw:ElementName>/gmd:MD_Metadata/gmd:fileIdentifier</csw:ElementName>" + "<csw:ElementName>/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title</csw:ElementName>" + "<csw:ElementName>/gmd:MD_Metadata/gmd:distributionInfo//gmd:CI_OnlineResource/gmd:linkage/gmd:URL</csw:ElementName>" + "<csw:Constraint version=\"1.1.0\">" + "<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">" + "<PropertyIsLike wildCard=\"%\" singleChar=\"_\" escapeChar=\"\\\">" + "<PropertyName>title</PropertyName>" + "<Literal>" + title + "</Literal>" + "</PropertyIsLike>" + "</Filter>" + "</csw:Constraint>" + "</csw:Query>" + "</csw:GetRecords>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String searchLayerByTitleIsEqualTo(String title) {

		String res = null;
		try {
			res = HMC.CallPost(CSW, "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" service=\"CSW\" version=\"2.0.2\" resultType=\"results\" outputSchema=\"csw:IsoRecord\">" + "<csw:Query typeNames=\"gmd:MD_Metadata\">" + "<csw:ElementName>/gmd:MD_Metadata/gmd:fileIdentifier</csw:ElementName>" + "<csw:ElementName>/gmd:MD_Metadata/gmd:identificationInfo/gmd:MD_DataIdentification/gmd:citation/gmd:CI_Citation/gmd:title</csw:ElementName>" + "<csw:ElementName>/gmd:MD_Metadata/gmd:distributionInfo//gmd:CI_OnlineResource/gmd:linkage/gmd:URL</csw:ElementName>" + "<csw:Constraint version=\"1.1.0\">" + "<Filter xmlns=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\">" + "<PropertyIsEqualTo>" + "<PropertyName>title</PropertyName>" + "<Literal>" + title + "</Literal>" + "</PropertyIsEqualTo>" + "</Filter>" + "</csw:Constraint>" + "</csw:Query>" + "</csw:GetRecords>", APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String searchLayerByAnyText(String anyText, int maxRecords) {

		if (maxRecords < 0)
			maxRecords = 10;

		// anyText = anyText.replace("-", " ").replace("_", " ");

		String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" version=\"2.0.2\" service=\"CSW\" resultType=\"results\" ";

		if (maxRecords == 0)
			query += " >";
		else
			query += "startPosition=\"1\" maxRecords=\"" + maxRecords + "\">";

		query += "<csw:Query xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" typeNames=\"csw:Record\">" + "<csw:ElementSetName>full</csw:ElementSetName>" + "<csw:Constraint version=\"1.1.0\">" + "<ogc:Filter>" + "<ogc:PropertyIsLike wildCard=\"*\" escape=\"\\\" singleChar=\"?\">" + "<ogc:PropertyName>AnyText</ogc:PropertyName>" + "<ogc:Literal>*" + anyText + "*</ogc:Literal>" + "</ogc:PropertyIsLike>" + "</ogc:Filter>" + "</csw:Constraint>" + "</csw:Query>" + "</csw:GetRecords>";
		
		String res = null;
		try {
			res = HMC.CallPost(CSW, query, APPLICATIONXML);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String searchMetadataByTitle(String title, int maxRecords) {

		if (maxRecords < 0)
			maxRecords = 10;

		// anyText = anyText.replace("-", " ").replace("_", " ");

		String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<csw:GetRecords xmlns:csw=\"http://www.opengis.net/cat/csw/2.0.2\" version=\"2.0.2\" service=\"CSW\" resultType=\"results\" ";

		if (maxRecords == 0)
			query += " >";
		else
			query += "startPosition=\"1\" maxRecords=\"" + maxRecords + "\">";

		query += "<csw:Query xmlns:ogc=\"http://www.opengis.net/ogc\" xmlns:gml=\"http://www.opengis.net/gml\" typeNames=\"csw:Record\">" + "<csw:ElementSetName>full</csw:ElementSetName>" + "<csw:Constraint version=\"1.1.0\">" + "<ogc:Filter>" + "<ogc:PropertyIsLike wildCard=\"*\" escape=\"\\\" singleChar=\"?\">" + "<ogc:PropertyName>Title</ogc:PropertyName>" + "<ogc:Literal>*" + title + "*</ogc:Literal>" + "</ogc:PropertyIsLike>" + "</ogc:Filter>" + "</csw:Constraint>" + "</csw:Query>" + "</csw:GetRecords>";

		String res = null;
		try {
			res = HMC.CallPost(CSW, query, APPLICATIONXML);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public ArrayList<CswRecord> getCswRecordsBySearch(String anyText, int maxRecords) {

		String xml = this.searchLayerByAnyText(anyText, maxRecords);
		return getCswRecordFromXmlResponse(xml);

	}

	
	/**
	 * 
	 * @param groupName
	 *            (mandatory)
	 * @return
	 */
	public String getGeoserverUrlForName(String groupName, boolean searchgroup) {

		int maxRecords = 0; // all records
		ArrayList<String> uriList = null;

		String xml = this.searchMetadataByTitle(groupName, maxRecords);

		ArrayList<CswRecord> cswRecordsList = getCswRecordFromXmlResponse(xml);

		for (CswRecord cswRecord : cswRecordsList) {

			if (searchgroup && cswRecord.getAbstractProperty().contains(GROUP)) {
				uriList = cswRecord.getURI();
				break;
			}
			else if (!searchgroup) {
				uriList = cswRecord.getURI();
				break;
			}
				
			
		}

		if (uriList != null) 
			return cleanGeoUrl(uriList.get(0));

		return null;
	}

	public static String cleanGeoUrl(String geourl) {
		int interr = geourl.indexOf("?");
		if (interr > 0)
			geourl = geourl.substring(0, interr);
		
		if (geourl.endsWith("/wms") || geourl.endsWith("/gwc") || geourl.endsWith("/wfs"))
			geourl = geourl.substring(0, geourl.length() - 4);
		
		return geourl;
	}

	public ArrayList<CswRecord> getCswRecordFromXmlResponse(String xml) {

		String tagTitle = "dc:title";
		String tagType = "dc:type";
		String tagSubject = "dc:subject";
		String tagBoundingBox = "ows:BoundingBox";
		String tagURI = "dc:URI";
		String tagAbstract = "dct:abstract";
		String tagCswRecord = "csw:Record";
		String tagIdentifier = "dc:identifier";

		ArrayList<CswRecord> arrayList = new ArrayList<CswRecord>();
		ArrayList<String> xmlValues = new ArrayList<String>();

		// Select resource identifier
		String queryXPath = "//" + tagCswRecord + "/" + tagIdentifier;

		xmlValues = ParserXpath.getTextFromXPathExpression(xml, queryXPath);

		// Create CswRecord Objects, one for any identifier
		for (String id : xmlValues) {
			CswRecord csw = new CswRecord();
			csw.setIdentifier(id);
			arrayList.add(csw);
		}

		// Add proprerty for any CswRecord Objects
		for (int i = 0; i < arrayList.size(); i++) {

			CswRecord csw = arrayList.get(i);

			// Select titles and set value in CswRecord Object
			queryXPath = "//" + tagCswRecord + "[" + tagIdentifier + "/text()[contains(.,'" + csw.getIdentifier() + "')]]/" + tagTitle;
			// System.out.println(queryXPath);
			xmlValues = ParserXpath.getTextFromXPathExpression(xml, queryXPath);
			csw.setTitle(xmlValues.get(0));

			// Select type and set value in CswRecord Object
			queryXPath = "//" + tagCswRecord + "[" + tagIdentifier + "/text()[contains(.,'" + csw.getIdentifier() + "')]]/" + tagType;
			// System.out.println(queryXPath);
			xmlValues = ParserXpath.getTextFromXPathExpression(xml, queryXPath);
			csw.setType(xmlValues.get(0));

			// Select type and set value in CswRecord Object
			queryXPath = "//" + tagCswRecord + "[" + tagIdentifier + "/text()[contains(.,'" + csw.getIdentifier() + "')]]/" + tagAbstract;
			// System.out.println(queryXPath);
			xmlValues = ParserXpath.getTextFromXPathExpression(xml, queryXPath);
			csw.setAbstractProperty(xmlValues.get(0));

			// Select URI and set value in CswRecord Object
			queryXPath = "//" + tagCswRecord + "[" + tagIdentifier + "/text()[contains(.,'" + csw.getIdentifier() + "')]]/" + tagURI;
			// System.out.println(queryXPath);
			xmlValues = ParserXpath.getTextFromXPathExpression(xml, queryXPath);
			ArrayList<String> uriList = new ArrayList<String>();
			for (String url : xmlValues)
				uriList.add(url);

			csw.setURI(uriList);

		}

		return arrayList;
	}

	
	public List<String> searchID(String title, GeonetworkCategory category, float similarity) {
		final List<String> ids = new ArrayList<String>();
		
		String res = null;
		try {
			
			String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request>";

			if (title != null && !title.isEmpty())
				query += "<any>" + title + "</any>";

			if (category != null && category != GeonetworkCategory.ANY)
				query += "<category>" + category.toString().toLowerCase() + "</category>";

				query += "<similarity>"+similarity+"</similarity>";

			query += "</request>";

			res = HMC.CallPost(XMLSEARCH, query, APPLICATIONXML);
			
			CXml cxmlResult = new CXml(res);
			cxmlResult.find("geonet:info").each(new CXmlManager() {
				public void manage(int i, CXml record) {
					String id = record.child("id").text();
					String uuid = record.child("uuid").text();
					logger.debug("ID:"+id);
					logger.debug("UUID:"+uuid);
					ids.add(id);
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}

		return ids;
	}
	
	public String searchService(String title, GeonetworkCategory category, Boolean similarity) {

		String res = null;
		try {

			String query = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><request>";

			if (title != null && !title.isEmpty())
				query += "<title>" + title + "</title>";
//				query += "<any>" + title + "</any>";

			if (category != null && category != GeonetworkCategory.ANY)
				query += "<category>" + category.toString().toLowerCase() + "</category>";

			if (similarity)
				query += "<similarity>1</similarity>";
			else
				query += "<similarity>0.8</similarity>";

			query += "</request>";

			res = HMC.CallPost(XMLSEARCH, query, APPLICATIONXML);

		} catch (Exception e) {
			e.printStackTrace();
		}

		return res;
	}

	public String getIdFromXml() {
		// Document customerDom = XMLParser.parse(xmlText);
		// Element customerElement = customerDom.getDocumentElement();
		// // Must do this if you ever use a raw node list that you expect to be
		// // all elements.
		// XMLParser.removeWhitespace(customerElement);
		//
		// // Customer Name
		// String nameValue = getElementTextValue(customerElement, "name");
		// String title = "<h1>" + nameValue + "</h1>";
		// HTML titleHTML = new HTML(title);
		// xmlParsed.add(titleHTML);

		return null;
	}

	/**
	 * @param referredWorkspace 
	 * @param startPosition
	 * @param maxRecords
	 * @param sortByTiyle
	 * @param sortAscendent
	 * @param filter
	 * @param textToSearch
	 * @return
	 */
	public CswLayersResult getLayersFromCsw(
			String referredWorkspace,
			int startPosition,
			int maxRecords,
			boolean sortByTitle,
			boolean sortAscendent,
			FILTER_TYPE filter,
			String textToSearch) {

		if (maxRecords<=0)
			maxRecords = MAX_RECORDS;
		
		// Query construction		
		String cswQuery = "" +
			"<?xml version=\"1.0\" encoding=\"UTF-8\"?>"+
			"<csw:GetRecords xmlns:csw='http://www.opengis.net/cat/csw/2.0.2' service='CSW' version='2.0.2' resultType='results' " +
			"	outputSchema='csw:Record' maxRecords='" + maxRecords + "' startPosition='" + startPosition + "'>\n"+
			
			"<csw:Query typeNames='csw:Record'>\n"+
//			"	<csw:ElementSetName>full</csw:ElementSetName>\n"+
//			"	<csw:ElementName>dct:abstract</csw:ElementName>\n"+
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
		
		if (referredWorkspace!=null)
			cswQuery += 
			"				<PropertyIsLike wildCard='*' singleChar='?' escapeChar='\'>\n"+
			"					<PropertyName>AnyText</PropertyName>\n"+
			"					<Literal>"+referredWorkspace+":*</Literal>\n"+
			"				</PropertyIsLike>\n";

		if (filter==FILTER_TYPE.TITLE || filter==FILTER_TYPE.ANY_TEXT)
			cswQuery += 
			"				<PropertyIsLike wildCard='*' singleChar='?' escapeChar='\'>\n"+
			"					<PropertyName>" + (filter==FILTER_TYPE.TITLE ? "title" : "AnyText") + "</PropertyName>\n"+
			"					<Literal>*" + textToSearch + "*</Literal>\n"+
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
//		System.out.println("CSWQUERY REQUEST--------\n"+cswQuery);
	
//		System.out.println(cswQuery);
		logger.info("CSWQUERY REQUEST--------\n"+cswQuery);
		

		final CswLayersResult result = new CswLayersResult();
		try {
			// Execute the query
			String res = HMC.CallPost(CSW, cswQuery, APPLICATIONXML);
			
			logger.info("CSWQUERY RESPONSE--------\n"+res);
			//System.out.println("CSWQUERY REQUEST--------\n"+cswQuery);
//			System.out.println("CSWQUERY RESPONSE--------\n"+res);//res.substring(0, 500));
			
			// Parse result
			CXml cxmlResult = new CXml(res);
			
			String nTot = cxmlResult.find("csw:SearchResults").attr("numberOfRecordsMatched");
			result.setResultLayersCount(Integer.parseInt(nTot));
			
			cxmlResult.find("csw:Record").each(new CXmlManager() {
				public void manage(int i, CXml record) {
					String id = record.child("dc:identifier").text();
					String title = record.child("dc:title").text();
					String name = record.child("dc:URI").attr("name");
					String description = record.child("dc:URI").attr("description");
					String geoserverUrl = record.child("dc:URI").text();
					
//					System.out.println("\nLAYER	Id:	"+id+"" + "\n	Name:	"+name+"\n	Title:	"+title+"\n	Url:	"+geoserverUrl+"\n	Descr:	"+description);
					
					if (id != null && title !=null && name!=null && geoserverUrl!=null)	result.addLayer(new LayerCsw(id, name, title, description, geoserverUrl));

//					System.out.println(i + ") " + id +"\t"+ title +"\t"+ name +"\t"+ geoserverUrl);
				}
			});
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	
}
