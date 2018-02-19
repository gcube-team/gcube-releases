package org.gcube.common.geoserverinterface.geonetwork.csw;

import java.io.*;

import org.gcube.common.geoserverinterface.bean.MetadataInfo;
import org.gcube.common.geoserverinterface.geonetwork.utils.DateTimeIso8601;
import org.w3c.dom.*;
import javax.xml.parsers.*;
import javax.xml.transform.*;
import javax.xml.transform.dom.*;
import javax.xml.transform.stream.*;

public class MetadataISO19139 extends AbstractXmlISO19139 {
	
	private DocumentBuilderFactory dbfac;
    private DocumentBuilder docBuilder;
    private Document doc;
   
    //XML ISO19139 INFO
    private NamespaceCswResolver nms;
    private String title;
    private String fileIdentifier;
    private String name;
    private String description;
    private String url;
    private String abst;
    private String categoryCode; //TODO change in enum
	private String westBoundLongitude;
	private String eastBoundLongitude;
	private String southBoundLongitude;
	private String northBoundLongitude;
    private String language;
    
    private Element gmdMD_Metadata;
    private final String xmlns = "xmlns:";
    private final String xsi = "xsi:";

    private String standardName = "ISO 19115:2003/19139";
    private String standardVersion = "1.0";
    private String nilReason = "nilReason";
    private String valueNilReason = "missing";

	private String protocol = "OGC:WMS-1.1.1-http-get-map";

    
	public MetadataISO19139(String fileIdentifier, String title, String name, String description, String url) {

		this.fileIdentifier = fileIdentifier;
		this.title = title;
		this.name = name;
		this.description = description;
		this.url = url;
		this.nms = new NamespaceCswResolver();

	}

	public MetadataISO19139(MetadataInfo metadataInfo) {
		this.fileIdentifier = metadataInfo.getFileIdentifier();
		this.title = metadataInfo.getTitle();
		this.name = metadataInfo.getName();
		this.description = metadataInfo.getDescription();
		this.url = metadataInfo.getUrl();
		this.westBoundLongitude = metadataInfo.getWestBoundLongitude();
		this.eastBoundLongitude = metadataInfo.getEastBoundLongitude();
		this.southBoundLongitude = metadataInfo.getSouthBoundLongitude();
		this.northBoundLongitude = metadataInfo.getNorthBoundLongitude();
		this.abst = metadataInfo.getAbst();
		this.language = metadataInfo.getLanguage();
		this.categoryCode = metadataInfo.getCategoryCode();
		this.nms = new NamespaceCswResolver();
	}

	private void instanceDOM() {
		// Creating an empty XML Document
		// We need a Document
		this.dbfac = DocumentBuilderFactory.newInstance();
		try {
			this.docBuilder = dbfac.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.doc = docBuilder.newDocument();

	}
    
    private void createISO19139(){
    	
    	this.instanceDOM();
    
    	this.gmdMD_Metadata = this.createISOHeader();
    	
    	 //append gmd:identificationInfo to gmd:MD_Metadata
    	this.gmdMD_Metadata.appendChild(this.createIdentificationInfo());
        //append gmd:distributionInfo to gmd:MD_Metadata
    	this.gmdMD_Metadata.appendChild(this.createDistributionInfo());
        //append gmd:dataQualityInfo to gmd:MD_Metadata
    	this.gmdMD_Metadata.appendChild(this.createDataQualityInfo());
    }
    
	public String getISO19139() {

		this.createISO19139();
		String xmlString = new String();
		
		// Output the XML set up a transformer
		TransformerFactory transfac = TransformerFactory.newInstance();
		Transformer trans;
		try {
			trans = transfac.newTransformer();
			trans.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			trans.setOutputProperty(OutputKeys.INDENT, "yes");
			// create string from xml tree
			StringWriter sw = new StringWriter();
			StreamResult result = new StreamResult(sw);
			DOMSource source = new DOMSource(this.doc);
			trans.transform(source, result);
			xmlString = sw.toString();
			// print xml
//			System.out.println("Here's the gmd Metadata:\n\n" + xmlString);

		} catch (TransformerConfigurationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TransformerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return xmlString;
	}
    
    
    private Element createISOHeader(){
    	
    	 //Creating the XML tree
        //create the root element and add it to the document
    	
    	//create gmd:MD_Metadata and attributes
        Element gmdMD_Metadata = doc.createElement(this.nms.getPrefixGMD() +":"+ this.getTagMD_Metedata());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixGMD(), this.nms.getNamespaceGMD());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixGTS(), this.nms.getNamespaceGTS());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixXSI(), this.nms.getNamespaceXSI());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixGML(), this.nms.getNamespaceGML());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixGCO(), this.nms.getNamespaceGCO());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixXLINK(), this.nms.getNamespaceXLINK());
        gmdMD_Metadata.setAttribute(xmlns+this.nms.getPrefixGEONET(), this.nms.getNamespaceGEONET());
        
        gmdMD_Metadata.setAttribute(xsi+"schemaLocation", "http://www.isotc211.org/2005/gmd http://www.isotc211.org/2005/gmd/gmd.xsd");
        
        //add gmd:MD_Metadata to document
        doc.appendChild(gmdMD_Metadata);

        //create gmd:fileIdentifier element and attributes
        Element gmdFileIdentifier  = doc.createElement(this.nms.getPrefixGMD() +":"+ this.getTagFileIdentifier());
        
        gmdFileIdentifier.setAttribute(xmlns+this.nms.getPrefixGMX(), this.nms.getNamespaceGMX());
        gmdFileIdentifier.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        
        //create gmd:gcoCharacterString
        Element gcoCharacterString = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text = doc.createTextNode(this.fileIdentifier);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString.appendChild(text);
        //append gmd:gcoCharacterString to gmdFileIdentifier
        gmdFileIdentifier.appendChild(gcoCharacterString);
        //append gmd:fileIdentifier to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdFileIdentifier);
        
        Element gmdLanguage = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLanguage());
        //create gmd:gcoCharacterString
        Element gcoCharacterString2 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text2 = doc.createTextNode(language);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString2.appendChild(text2);
        //append gmd:gcoCharacterString to gmd:language
        gmdLanguage.appendChild(gcoCharacterString2);
        //append gmd:language to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdLanguage);
        
        //create element gmd:characterSet and your attributes
        Element gmdCharacterSet = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCharacterSet());
        Element gmdMD_CharacterSetCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_CharacterSetCode());
        gmdMD_CharacterSetCode.setAttribute(this.getAttrCodeList(), this.getValueAttrMD_CharacterSetCodeCodeList());
        gmdMD_CharacterSetCode.setAttribute(this.getAttrCodeListValue(), this.getValueAttrMD_CharacterSetCodeCodeListValue());
        //add gmd:MD_CharacterSetCode to gmd:characterSet
        gmdCharacterSet.appendChild(gmdMD_CharacterSetCode);
        //append gmd:characterSet to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdCharacterSet);
        
        //"Create this 
        //<gmd:hierarchyLevel><gmd:MD_ScopeCode codeList=\"./resources/codeList.xml#MD_ScopeCode\" codeListValue=\"dataset\"/></gmd:hierarchyLevel>"
        Element gmdHierarchyLevel = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagHierarchyLevel());
        Element gmdMD_ScopeCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_ScopeCode());
        gmdMD_ScopeCode.setAttribute(this.getAttrCodeList(), this.getValueAttrMD_ScopeCodeCodeList());
        gmdMD_ScopeCode.setAttribute(this.getAttrCodeListValue(), this.getValueAttrMD_ScopeCodeCodeListValue());
        //add gmd:MD_CharacterSetCode to gmd:characterSet
        gmdHierarchyLevel.appendChild(gmdMD_ScopeCode);
        //append gmd:hierarchyLevel to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdHierarchyLevel);
        
        //"Create this 
        // "<gmd:dateStamp><gco:DateTime>"+DateTimeIso8601.formatDateTime()</gco:DateTime></gmd:dateStamp>"
        Element gmdDateStamp = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagdDateStamp());
        Element gcoDateTime = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDateTime());
        Text text3 = doc.createTextNode(DateTimeIso8601.formatDateTime());
        //append text to gco:DateTime 
        gcoDateTime.appendChild(text3);
        //append gco:DateTime to gmd:dateStamp
        gmdDateStamp.appendChild(gcoDateTime);
     
        //append gmd:dateStamp to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdDateStamp);
        
        //"Create this 
		//"<gmd:metadataStandardName><gco:CharacterString>ISO 19115:2003/19139</gco:CharacterString></gmd:metadataStandardName>"
        Element gmdMetadataStandardName = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMetadataStandardName());
        Element gcoCharacterString3 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text4 = doc.createTextNode(this.standardName);
        //append text to gco:CharacterString
        gcoCharacterString3.appendChild(text4);
        //append gco:CharacterString to metadataStandardName
        gmdMetadataStandardName.appendChild(gcoCharacterString3);
     
        //append gmd:metadataStandardName to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdMetadataStandardName);
        
        //Create this
        //"<gmd:metadataStandardVersion><gco:CharacterString>1.0</gco:CharacterString></gmd:metadataStandardVersion>"
        Element gmdMetadataStandardVersion = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMetadataStandardVersion());
        Element gcoCharacterString4 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text5 = doc.createTextNode(this.standardVersion);
        //append text to gco:CharacterString
        gcoCharacterString4.appendChild(text5);
        //append gco:CharacterString to gmd:metadataStandardVersion
        gmdMetadataStandardVersion.appendChild(gcoCharacterString4);
     
        //append gmd:metadataStandardName to gmd:MD_Metadata
        gmdMD_Metadata.appendChild(gmdMetadataStandardVersion);
        
        return gmdMD_Metadata;
    	
    }
    
    private Element createIdentificationInfo(){
    	
        //Create this
//    	 <gmd:identificationInfo>
//         <gmd:MD_DataIdentification>
//            <gmd:citation xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gmd:CI_Citation>
//                  <gmd:title>
//                     <gco:CharacterString>Manhattan (NY) roads</gco:CharacterString>
//                  </gmd:title>
//                  <gmd:date>
//                     <gmd:CI_Date>
//                        <gmd:date>
//                           <gco:DateTime>2011-12-21T12:51:07</gco:DateTime>
//                        </gmd:date>
//                        <gmd:dateType>
//                           <gmd:CI_DateTypeCode codeList="./resources/codeList.xml#CI_DateTypeCode" codeListValue="revision"/>
//                        </gmd:dateType>
//                     </gmd:CI_Date>
//                  </gmd:date>
//               </gmd:CI_Citation>
//            </gmd:citation>
//            <gmd:abstract xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gco:CharacterString>Highly simplified road layout of Manhattan in New York..</gco:CharacterString>
//            </gmd:abstract>
//            <gmd:status xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gmd:MD_ProgressCode codeList="./resources/codeList.xml#MD_ProgressCode" codeListValue="completed"/>
//            </gmd:status>
//            <gmd:language xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv" gco:nilReason="missing">
//               <gco:CharacterString/>
//            </gmd:language>
//            <gmd:characterSet xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gmd:MD_CharacterSetCode codeList="http://www.isotc211.org/2005/resources/codeList.xml#MD_CharacterSetCode" codeListValue=""/>
//            </gmd:characterSet>
//            <gmd:topicCategory xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gmd:MD_TopicCategoryCode>geoscientificInformation</gmd:MD_TopicCategoryCode>
//            </gmd:topicCategory>
//            <gmd:extent xmlns:wms="http://www.opengis.net/wms" xmlns:srv="http://www.isotc211.org/2005/srv">
//               <gmd:EX_Extent>
//                  <gmd:geographicElement>
//                     <gmd:EX_GeographicBoundingBox>
//                        <gmd:westBoundLongitude>
//                           <gco:Decimal>-74.02722</gco:Decimal>
//                        </gmd:westBoundLongitude>
//                        <gmd:eastBoundLongitude>
//                           <gco:Decimal>-73.907005</gco:Decimal>
//                        </gmd:eastBoundLongitude>
//                        <gmd:southBoundLatitude>
//                           <gco:Decimal>40.684221</gco:Decimal>
//                        </gmd:southBoundLatitude>
//                        <gmd:northBoundLatitude>
//                           <gco:Decimal>40.878178</gco:Decimal>
//                        </gmd:northBoundLatitude>
//                     </gmd:EX_GeographicBoundingBox>
//                  </gmd:geographicElement>
//               </gmd:EX_Extent>
//            </gmd:extent>
//         </gmd:MD_DataIdentification>
//     </gmd:identificationInfo>
    	
        Element gmdIdentificationInfo = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagIdentificationInfo());
        Element gmdMD_DataIdentification = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_DataIdentification());
        Element gmdCitation = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCitation());
        
        gmdCitation.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        gmdCitation.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        
        Element gmdCI_Citation = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCI_Citation());
        Element gmdTitle = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagTitle());
        
        Element gcoCharacterString = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text = doc.createTextNode(this.title);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString.appendChild(text);
        //append gmd:gcoCharacterString to gmd:title
        gmdTitle.appendChild(gcoCharacterString);
       
        //append gmd:title to gmd:CI_Citation
        gmdCI_Citation.appendChild(gmdTitle);

        
        Element gmdDate = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDate());
        Element gmdCI_Date = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCI_Date());
        Element gmdDate2 = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDate());
        
        Element gmdDateTime = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDateTime());
  
        Text text2 = doc.createTextNode(DateTimeIso8601.formatDateTime());
        //append text to gco:CharacterString
        gmdDateTime.appendChild(text2);
        //append gmd:DateTime to gmd:date
        gmdDate2.appendChild(gmdDateTime);
        
        //append  gmd:date to gmd:CI_Date
        gmdCI_Date.appendChild(gmdDate2);
        
        
        Element gmdDateType = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDateType());
        Element gmdCI_DateTypeCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCI_DateTypeCode());
        gmdCI_DateTypeCode.setAttribute(this.getAttrCodeList(), this.getValueAttrCI_DateTypeCodeCodeList());
        gmdCI_DateTypeCode.setAttribute(this.getAttrCodeListValue(), this.getValueAttrCI_DateTypeCodeCodeListValue());
        
        gmdDateType.appendChild(gmdCI_DateTypeCode);
       
        //append  gmd:date to gmd:CI_Date
        gmdCI_Date.appendChild(gmdDateType);
       
        //append gmd:CI_Date to gmd:date
        gmdDate.appendChild(gmdCI_Date);
    	
        //append gmd:date to gmd:CI_Citation
        gmdCI_Citation.appendChild(gmdDate);
        
        //append gmd:CI_Citation to gmd:Citation
        gmdCitation.appendChild(gmdCI_Citation);
        
        gmdMD_DataIdentification.appendChild(gmdCitation); //new
        
        Element gmdAbstract = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagAbstract());
        gmdAbstract.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        gmdAbstract.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        
        Element gcoCharacterString2 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text3 = doc.createTextNode(this.abst);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString2.appendChild(text3);
        //append gco:CharacterString2 to gmd:abstract
        gmdAbstract.appendChild(gcoCharacterString2);
        
        gmdMD_DataIdentification.appendChild(gmdAbstract); //new
        
        Element gmdStatus = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagStatus());
        gmdStatus.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        gmdStatus.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        
        Element gmdMD_ProgressCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_ProgressCode());
        gmdMD_ProgressCode.setAttribute(this.getAttrCodeList(), this.getValueAttrMD_ProgressCodeCodeList());
        gmdMD_ProgressCode.setAttribute(this.getAttrCodeListValue(), this.getValueAttrMD_ProgressCodeCodeListValue());
        
        //append gmd:MD_ProgressCode to gmd:status
        gmdStatus.appendChild(gmdMD_ProgressCode);
        
        gmdMD_DataIdentification.appendChild(gmdStatus); //new
        
        Element gmdLanguage = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLanguage());
        gmdLanguage.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        gmdLanguage.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        gmdLanguage.setAttribute(this.nms.getPrefixGCO()+":"+this.nilReason, this.valueNilReason);
        Element gcoCharacterString3 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());

        //append gco:CharacterString3 to gmd:language
        gmdLanguage.appendChild(gcoCharacterString3);
        
        gmdMD_DataIdentification.appendChild(gmdLanguage); //new
        
        Element gmdCharacterSet= doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCharacterSet());
        gmdCharacterSet.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        gmdCharacterSet.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        Element gmdMD_CharacterSetCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_CharacterSetCode());
        gmdMD_CharacterSetCode.setAttribute(this.getAttrCodeList(), this.getValueAttrMD_CharacterSetCodeCodeListLanguage());
        gmdMD_CharacterSetCode.setAttribute(this.getAttrCodeListValue(), "");
        //add gmd:MD_CharacterSetCode to gmd:characterSet
        gmdCharacterSet.appendChild(gmdMD_CharacterSetCode);
        
        gmdMD_DataIdentification.appendChild(gmdCharacterSet); //new
        
        
        Element gmdTopicCategory= doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagTopicCategory());
        gmdTopicCategory.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        gmdTopicCategory.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        Element gmdMD_TopicCategoryCode= doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_TopicCategoryCode());
        //append gmd:characterSet to gmd:MD_Metadata
        Text text4 = doc.createTextNode(this.categoryCode);
        //append text to gmd:MD_TopicCategoryCode 
        gmdMD_TopicCategoryCode.appendChild(text4);
        //append gmd:MD_TopicCategoryCode to gmd:topicCategory
        gmdTopicCategory.appendChild(gmdMD_TopicCategoryCode);
        
        gmdMD_DataIdentification.appendChild(gmdTopicCategory); //new
        
        Element gmdExtent = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagExtent());
        gmdExtent.setAttribute(xmlns+this.nms.getPrefixSRV(), this.nms.getNamespaceSRV());
        gmdExtent.setAttribute(xmlns+this.nms.getPrefixWMS(), this.nms.getNamespaceWMS());
        Element gmdEX_Extent = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagEX_Extent());
        Element gmdGeographicElement = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagGeographicElement());
        Element gmdEX_GeographicBoundingBox = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagEX_GeographicBoundingBox());
        
        Element gmdWestBoundLongitude = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagWestBoundLongitude());
        Element gcoDecimal = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDecimal());
//        Element gmdXmin = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagXmin());
        Text text5 = doc.createTextNode(this.westBoundLongitude);
        //append text to gco:Decimal
        gcoDecimal.appendChild(text5);
        //append gco:Decimal to gmd:westBoundLongitude
        gmdWestBoundLongitude.appendChild(gcoDecimal);
        
        gmdEX_GeographicBoundingBox.appendChild(gmdWestBoundLongitude);
        
        Element gmdEastBoundLongitude = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagEastBoundLongitude());
        Element gcoDecimal2 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDecimal());
//        Element gmdXmin = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagXmin());
        Text text6 = doc.createTextNode(this.eastBoundLongitude);
        //append text to gco:Decimal
        gcoDecimal2.appendChild(text6);
        //append gco:Decimal to gmd:eastBoundLongitude
        gmdEastBoundLongitude.appendChild(gcoDecimal2);
        
        gmdEX_GeographicBoundingBox.appendChild(gmdEastBoundLongitude);
        
        Element gmdSouthBoundLatitude = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagSouthBoundLatitude());
        Element gcoDecimal3 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDecimal());
//        Element gmdXmin = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagXmin());
        Text text7 = doc.createTextNode(this.southBoundLongitude);
        //append text to gco:Decimal
        gcoDecimal3.appendChild(text7);
        //append gco:Decimal to gmd:southBoundLongitude
        gmdSouthBoundLatitude.appendChild(gcoDecimal3);
        
        gmdEX_GeographicBoundingBox.appendChild(gmdSouthBoundLatitude);
        
        Element gmdNorthBoundLatitude = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagNorthBoundLatitude());
        Element gcoDecimal4 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagDecimal());
//        Element gmdXmin = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagXmin());
        Text text8 = doc.createTextNode(this.northBoundLongitude);
        //append text to gco:Decimal
        gcoDecimal4.appendChild(text8);
        //append gco:Decimal to gmd:southBoundLongitude
        gmdNorthBoundLatitude.appendChild(gcoDecimal4);
        
        gmdEX_GeographicBoundingBox.appendChild(gmdNorthBoundLatitude);
        
        //append gmd:EX_GeographicBoundingBox to gmd:GeographicElement
        gmdGeographicElement.appendChild(gmdEX_GeographicBoundingBox);
        
        gmdEX_Extent.appendChild(gmdGeographicElement);
        
        gmdExtent.appendChild(gmdEX_Extent);

        gmdMD_DataIdentification.appendChild(gmdExtent);

        gmdIdentificationInfo.appendChild(gmdMD_DataIdentification);
        
        return gmdIdentificationInfo;
    }
    
    private Element createDistributionInfo(){
    	
        Element gmdDistributionInfo = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDistributionInfo());
        Element gmdMD_Distribution = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_Distribution());
        Element gmdDistributionFormat = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDistributionFormat());
        Element gmdMD_Format = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_Format());
        
        Element gmdName = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagName());
        gmdName.setAttribute(this.nms.getPrefixGCO()+":"+this.nilReason, this.valueNilReason);
        Element gcoCharacterString = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        gmdName.appendChild(gcoCharacterString);
        
        gmdMD_Format.appendChild(gmdName);
        
        Element gmdVersion = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagVersion());
        gmdVersion.setAttribute(this.nms.getPrefixGCO()+":"+this.nilReason, this.valueNilReason);
        Element gcoCharacterString2 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        gmdVersion.appendChild(gcoCharacterString2);
        
        gmdMD_Format.appendChild(gmdVersion);
        
        gmdDistributionFormat.appendChild(gmdMD_Format);
        gmdMD_Distribution.appendChild(gmdDistributionFormat);
        
        Element gmdTransferOptions = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagTransferOptions());
        Element gmdMD_DigitalTransferOptions = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_DigitalTransferOptions());
        Element gmdOnLine = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagOnLine());
        Element gmdCI_OnlineResource = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagCI_OnlineResource());

        Element gmdLinkage = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLinkage());
        Element gmdURL = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagURL());
        Text text = doc.createTextNode(this.url);
        //append text to gmd:URL
        gmdURL.appendChild(text);
        //append gmd:URL to gmd:linkage
        gmdLinkage.appendChild(gmdURL);
        
        gmdCI_OnlineResource.appendChild(gmdLinkage);
        
        Element gmdProtocol = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagProtocol());
        Element gcoCharacterString3 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text2 = doc.createTextNode(this.protocol);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString3.appendChild(text2);
        //append gmd:gcoCharacterString3 to gmd:protocol
        gmdProtocol.appendChild(gcoCharacterString3);
        
        gmdCI_OnlineResource.appendChild(gmdProtocol);
        
        Element gmdName2 = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagName());
        Element gcoCharacterString4 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text3 = doc.createTextNode(this.name);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString4.appendChild(text3);
        //append gmd:gcoCharacterString4 to gmd:name
        gmdName2.appendChild(gcoCharacterString4);
        
        gmdCI_OnlineResource.appendChild(gmdName2);
       
        Element gmdDescription = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDescription());
        Element gcoCharacterString5 = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        Text text4 = doc.createTextNode(this.description);
        //append text to gmd:gcoCharacterString 
        gcoCharacterString5.appendChild(text4);
        //append gmd:gcoCharacterString5 to gmd:description
        gmdDescription.appendChild(gcoCharacterString5);
        
        gmdCI_OnlineResource.appendChild(gmdDescription);
        
        gmdOnLine.appendChild(gmdCI_OnlineResource);
        
        gmdCI_OnlineResource.appendChild(gmdDescription);
        
        gmdOnLine.appendChild(gmdCI_OnlineResource);
        
        gmdMD_DigitalTransferOptions.appendChild(gmdOnLine);
        gmdTransferOptions.appendChild(gmdMD_DigitalTransferOptions);
        gmdMD_Distribution.appendChild(gmdTransferOptions);
        gmdDistributionInfo.appendChild(gmdMD_Distribution);
        
        return gmdDistributionInfo;
    }
    
    private Element createDataQualityInfo(){
    	
        Element gmdDataQualityInfo = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDataQualityInfo());
        Element gmdDQ_DataQuality = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDQ_DataQuality());
        Element gmdScope = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagScope());
        Element gmdDQ_Scope = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagDQ_Scope());
    	
        Element gmdLevel = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLevel());
        Element gmdMD_ScopeCode = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagMD_ScopeCode());
        gmdMD_ScopeCode.setAttribute(this.getAttrCodeList(), this.getValueAttrMD_ScopeCodeCodeList());
        gmdMD_ScopeCode.setAttribute(this.getAttrCodeListValue(), this.getValueAttrMD_ScopeCodeCodeListValue());
        gmdLevel.appendChild(gmdMD_ScopeCode);
        
        gmdDQ_Scope.appendChild(gmdLevel);
        gmdScope.appendChild(gmdDQ_Scope);
        gmdDQ_DataQuality.appendChild(gmdScope);
       
        Element gmdLineage = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLineage());
        Element gmdLI_Lineage = doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagLI_Lineage());
        Element gmdStatement= doc.createElement(this.nms.getPrefixGMD()+":"+this.getTagStatement());
        gmdStatement.setAttribute(this.nms.getPrefixGCO()+":"+this.nilReason, this.valueNilReason);
        Element gcoCharacterString = doc.createElement(this.nms.getPrefixGCO()+":"+this.getTagCharacterString());
        
        gmdStatement.appendChild(gcoCharacterString);
        gmdLI_Lineage.appendChild(gmdStatement);
        gmdLineage.appendChild(gmdLI_Lineage);
        gmdDQ_DataQuality.appendChild(gmdLineage);
        
        gmdDataQualityInfo.appendChild(gmdDQ_DataQuality);
        
        return gmdDataQualityInfo;
    }
    
//    public static void main(String args[]){
//    	
//    	MetadataISO19139 m = new MetadataISO19139(null, "title", "name", "description", "url");
//    	m.getISO19139();
//    }
}
