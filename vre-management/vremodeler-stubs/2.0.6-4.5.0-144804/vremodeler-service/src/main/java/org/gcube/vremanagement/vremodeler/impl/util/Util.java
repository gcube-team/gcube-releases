package org.gcube.vremanagement.vremodeler.impl.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.FunctionalityPersisted;
import org.gcube.vremanagement.vremodeler.impl.peristentobjects.VreFunctionalityRelation;
import org.gcube.vremanagement.vremodeler.stubs.FunctionalityItem;
import org.gcube.vremanagement.vremodeler.stubs.VREDescription;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.SelectArg;

/**
 * 
 * @author Lucio Lelii
 *
 */
public class Util {

	
	private static final GCUBELog logger = new GCUBELog(Util.class);
	
	
	public static List<FunctionalityPersisted> getSelectedFunctionality(Dao<VreFunctionalityRelation, String> vreFunctionalityDao, Dao<FunctionalityPersisted, Integer> functionalityDao, String resourceId) throws Exception{
		//retrieves all functionalities selected in this vre
		QueryBuilder<VreFunctionalityRelation, String> vreFunctionalityQb = vreFunctionalityDao.queryBuilder();
		vreFunctionalityQb.selectColumns(VreFunctionalityRelation.FUNCTIONALITY_ID_FIELD);
		SelectArg userSelectArg = new SelectArg();
		vreFunctionalityQb.where().eq(VreFunctionalityRelation.VRE_ID_FIELD, userSelectArg);
		QueryBuilder<FunctionalityPersisted, Integer> functionalityQb = functionalityDao.queryBuilder();
		functionalityQb.where().in(FunctionalityPersisted.ID_FIELDNAME, vreFunctionalityQb);
		functionalityQb.prepare().setArgumentHolderValue(0, resourceId);
		return functionalityQb.query();
	}
	
			
//	private static <T> List<T> getDistinctObjects(Collection<T> list){
//		List<T> tmpList= new ArrayList<T>();
//		for (T el: list)
//			if (!tmpList.contains(el)) tmpList.add(el);
//		return tmpList;
//	}
	
	
	/**
	 * 	
	 * @return the XML representing the quality string
	 */
	public static String prepareQualityXML(){
		StringBuilder qualityString=new StringBuilder();
		qualityString.append("<ResultSet>");
		qualityString.append("<Quality><DisplayName>Response Time</DisplayName><Description>This quality parameter indicates the importance of response time (i.e., the delay from a function request to the reception of the response) ");
		qualityString.append("in selecting the resource (both in number and type) needed to satisfy the Digital Library definition criteria. E.g., the specification of a 100% response time will result in a maximization of the resources ");
		qualityString.append("allocated as to minimise the response time. </Description><Multiple value=\"true\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">*</Value></AllowedValues></Quality>");
		qualityString.append("<Quality><DisplayName>Robustness</DisplayName><Description>This quality parameter indicates the importance of robustness (i.e., the resilience to faults) in selecting the resource (both in number and type)");
		qualityString.append(" needed to satisfy the Digital Library definition criteria. E.g., the specification of a 100% robustness will result in a maximisation of the resources allocated as to minimise service unavailability.</Description><Multiple ");
		qualityString.append(" value=\"true\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">*</Value></AllowedValues></Quality>");
		qualityString.append("<Quality><DisplayName>Scalability</DisplayName><Description>This quality parameter indicates the importance of scalability (i.e., the capability to increase Digital Library Capacity as much as needed) in selecting the resource ");
		qualityString.append("(both in number and type) needed to satisfy the Digital Library definition criteria. E.g., the specification of a 100% scalability will result in a maximisztion of the resources allocated as to minimise the response time.</Description>");
		qualityString.append("<Multiple value=\"true\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">*</Value></AllowedValues></Quality>");
		qualityString.append("<Quality><DisplayName>Re-use</DisplayName><Description>This quality parameter indicates the willingness to re-use existing resources (running instances, indexes, etc.) across various Digital Libraries. The specified ");
		qualityString.append("percentage represents the degree of re-use of existing assets in concretely implementing the Digital Library.</Description><Multiple value=\"true\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">*</Value></AllowedValues></Quality>");
		qualityString.append("<Quality><DisplayName>Capacity</DisplayName><Description>This quality parameter indicates the importance of capacity (i.e., the amount of resources allocated to the Digital Library) in selecting the resource (both in number and type) ");
		qualityString.append("needed to satisfy the Digital Library definition criteria. E.g., the specification of a 100% capacity will result in a maximization of the amount of resources allocated to the Digital Library.");
		qualityString.append(" </Description><Multiple value=\"true\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">*</Value></AllowedValues></Quality>");
		qualityString.append("<Quality><DisplayName>Security</DisplayName><Description>A true/false quality parameter indicates whether the operations have to be executed in a fully authorised and authenticated environment (true value) or ");
		qualityString.append("not (false value)</Description><Multiple value=\"false\"/><CanBeEmpty value=\"false\"/><AllowedValues><Value default=\"true\" selected=\"false\">Yes</Value><Value default=\"false\" selected=\"false\">No</Value></AllowedValues></Quality>");
		qualityString.append("</ResultSet>");
		return qualityString.toString();
	}
	
	
	/**
	 * 
	 * @param res
	 * @param selectedFunct
	 * @param selectedCS
	 * @return
	 */
	public static List<FunctionalityItem> prepareFunctionalities(ResultSet res, ArrayList<Integer> selectedFunct, ArrayList<String> selectedCS){
		logger.trace("preparing functionality");
		List<FunctionalityItem> functionalityItemList= new ArrayList<FunctionalityItem>();
		try {
			while(res.next()){
				FunctionalityItem functionlityItem= new FunctionalityItem();
				functionlityItem.setId(res.getInt(1));
				functionlityItem.setName(res.getString(2));
				functionlityItem.setSelected(selectedFunct.contains(res.getInt(1)));
				functionlityItem.setDescription(res.getString(3));
				
				logger.trace(" - "+functionlityItem.getName()+" - "+ functionlityItem.isSelected() );
				List<FunctionalityItem> subFunctionalities= new ArrayList<FunctionalityItem>();
				/*
				ResultSet subFunctRes=DBInterface.queryDB("select * from FUNCTIONALITY where father='"+res.getInt(1)+"';");
				while(subFunctRes.next()){
					FunctionalityItem subFunctionalityItem= new FunctionalityItem();
					subFunctionalityItem.setId(subFunctRes.getInt(1));
					subFunctionalityItem.setName(subFunctRes.getString(2));
					subFunctionalityItem.setDescription(subFunctRes.getString(3));
					subFunctionalityItem.setSelected(selectedFunct.contains(subFunctRes.getInt(1)));
					subFunctionalities.add(subFunctionalityItem);
					logger.trace(" - "+subFunctRes.getString(2)+" - "+ subFunctionalityItem.isSelected() );
				}
				*/
				functionlityItem.setChilds(subFunctionalities.toArray(new FunctionalityItem[subFunctionalities.size()]));
				functionalityItemList.add(functionlityItem);
			}
		} catch (SQLException e) {
			logger.error("DB error preparing Functionalities",e);
			
		}
		
		return functionalityItemList;
	}
	
	/**
	 * 
	 * @param res
	 * @return a String representing the message
	 * @throws Exception
	 */
	public static VREDescription prepareVREDescription(ResultSet res) throws Exception{
		VREDescription vreDescription= new VREDescription();
		
		if (res.next()){
			vreDescription.setName(res.getString(2));
			vreDescription.setDescription(res.getString(3));
			vreDescription.setDesigner(res.getString(4));
			vreDescription.setManager(res.getString(5));
			Calendar calendarStart= Calendar.getInstance();
			calendarStart.setTime(res.getDate(6));
			vreDescription.setStartTime(calendarStart);
			Calendar calendarEnd= Calendar.getInstance();
			calendarEnd.setTime(res.getDate(7));
			vreDescription.setEndTime(calendarEnd);
		}else{
			Calendar calendarStart= Calendar.getInstance();
			Calendar calendarEnd= Calendar.getInstance();
			calendarEnd.add(Calendar.YEAR, 1);
			vreDescription.setName(null);
			vreDescription.setDescription(null);
			vreDescription.setDesigner(null);
			vreDescription.setManager(null);
			vreDescription.setStartTime(calendarStart);
			vreDescription.setEndTime(calendarEnd);
		}
		return vreDescription;
	}
	

	/**
	 * transform a Document into string
	 * 
	 * @param doc Document
	 * @return a String
	 */
	public static String docToString(Document doc){
		
		String temp= null;
		try{
			DOMSource domSource = new DOMSource(doc);
			
			TransformerFactory tf = TransformerFactory.newInstance();
			Transformer serializer = tf.newTransformer();
			serializer.setOutputProperty("encoding", "UTF-8");
			serializer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes" );
			StringWriter sw= new StringWriter();
			StreamResult sr= new StreamResult(sw);
			serializer.transform(domSource, sr);
			temp=sr.getWriter().toString();
		}catch(Exception e){
			logger.error("transformation to String Error");
			e.printStackTrace();
		}
		return temp;
	}
	
	/**
	 * 
	 * @param doc the root Document
	 * @param root the element tag name
	 * @param elements elements to add
	 * @return the result element
	 */
	public static Element addElements(Document doc, String root, Element[] elements){
		Element returnEl= doc.createElement(root);
		for (Element el: elements){
			returnEl.appendChild(el);
		}
		return returnEl;
	}
	
	
	/**
	 * 
	 * @param doc the root Document
	 * @param Tag the element tag name
	 * @param text the text to add at the element
	 * @return the result element
	 */
	public static Element createTextElement(Document doc, String tag, String text){
		Element returnEl= doc.createElement(tag);
		if (text!=null) returnEl.appendChild(doc.createTextNode(text));
		return returnEl;
	}

	
	
	public static boolean isVersionGreater(String versionA, String versionB){
		String versionAWithoutSnapshot = versionA.split("-")[0];
		String versionBWithoutSnapshot = versionB.split("-")[0];
		String[] versionAsplitted = versionAWithoutSnapshot.split("\\.");		
		String[] versionBsplitted = versionBWithoutSnapshot.split("\\.");
		
		//checking mayor version
		if (Integer.parseInt(versionAsplitted[0]) > Integer.parseInt(versionBsplitted[0]))
			return true;
		else if (Integer.parseInt(versionAsplitted[0]) < Integer.parseInt(versionBsplitted[0]))
			return false;
		
		//checking minor version
		if (Integer.parseInt(versionAsplitted[1]) > Integer.parseInt(versionBsplitted[1]))
			return true;
		else if (Integer.parseInt(versionAsplitted[1]) < Integer.parseInt(versionBsplitted[1]))
			return false;
		
		//checking revision version
		if (Integer.parseInt(versionAsplitted[2]) > Integer.parseInt(versionBsplitted[2]))
			return true;
		else if (Integer.parseInt(versionAsplitted[2]) < Integer.parseInt(versionBsplitted[2]))
			return false;
		
		return false;
	}
	
	public static boolean isDeploymentStatusFinished(String report) {
		boolean ret = false;
		String xpath = "/ResourceReport/Status";
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new ByteArrayInputStream(report.getBytes()));
			XPath engine = XPathFactory.newInstance().newXPath();
			NodeList nl = (NodeList) engine.evaluate(xpath,document, XPathConstants.NODESET);
			for (int i = 0; i < nl.getLength(); i++) {
				if(i==0){
					ret = true;
				}
				if(nl.item(i).getFirstChild().getNodeValue().compareTo("CLOSED")!=0){
					ret = false;
					break;
				}
			}
		} catch (ParserConfigurationException e) {
			return false;
		} catch (SAXException e) {
			return false;
		} catch (IOException e) {
			return false;
		} catch (XPathExpressionException e) {
			return false;
		}
		return ret;
	}
	
	/**
	 * controls if something is failed deploying 
	 * @param report the VRE deployment report
	 * @return true if Failed, else false
	 */
	public static boolean isSomethingFailed(String report){
		boolean ret = false;
		String deploymentXpath = "/ResourceReport/DeploymentPlanCreation/Status";
		String dependenciesResolutionXpath ="/ResourceReport/Services/Service/DependenciesResolutionStatus";
		String serviceXpath="/ResourceReport/Services/Service/DeploymentActivity/GHN/LastReportReceived/Packages/Package/Status";
		String resourceXpath="/ResourceReport/Resources/Resource[/Type/text()!='GHN']/Status";
		String relatedRIXpath="/ResourceReport/Services/Service/DeploymentActivity/RelatedRunningInstance/Status";
		String sessionXPath="/ResourceReport/SessionExitStatus";
		
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		DocumentBuilder db;
		try {
			db = dbf.newDocumentBuilder();
			Document document = db.parse(new ByteArrayInputStream(report.getBytes()));
			XPath engine = XPathFactory.newInstance().newXPath();
			
			
			NodeList sessionResoulution = (NodeList) engine.evaluate(sessionXPath,document, XPathConstants.NODESET);
			for (int i = 0; i < sessionResoulution.getLength(); i++) {
				if(sessionResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
			
			
			NodeList nDeployment = (NodeList) engine.evaluate(deploymentXpath,document, XPathConstants.NODESET);
			for (int i = 0; i < nDeployment.getLength(); i++) {
				if(nDeployment.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
						
			NodeList nResoulution = (NodeList) engine.evaluate(dependenciesResolutionXpath,document, XPathConstants.NODESET);
			for (int i = 0; i < nResoulution.getLength(); i++) {
				if(nResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
			
			NodeList sResoulution = (NodeList) engine.evaluate(serviceXpath,document, XPathConstants.NODESET);
			for (int i = 0; i < sResoulution.getLength(); i++) {
				if(sResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
			
			NodeList resResoulution = (NodeList) engine.evaluate(resourceXpath,document, XPathConstants.NODESET);
			for (int i = 0; i < resResoulution.getLength(); i++) {
				if(resResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
			
			NodeList relResoulution = (NodeList) engine.evaluate(relatedRIXpath,document, XPathConstants.NODESET);
			for (int i = 0; i < relResoulution.getLength(); i++) {
				if(relResoulution.item(i).getFirstChild().getNodeValue().compareTo("FAILED")==0){
					return true;
				}
			}
			
			
			
		} catch (ParserConfigurationException e) {
			return true;
		} catch (SAXException e) {
			return true;
		} catch (IOException e) {
			return true;
		} catch (XPathExpressionException e) {
			return true;
		}
		return ret;
	}
	
}
