package org.gcube.vomanagement.usermanagement.impl.liferay;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;

import java.util.HashMap;
import java.util.Iterator;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.vomanagement.usermanagement.exception.UserManagementFileNotFoundException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementIOException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;

import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class ParseXML {


	private static Document convertStringToDocument (FileInputStream inputFile) throws IOException {

		InputSource in = null;
		try {
			in = new InputSource (inputFile);
		} catch (Exception e) {
			e.printStackTrace();
		}
		DocumentBuilderFactory dfactory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			doc = dfactory.newDocumentBuilder().parse(in);
		} catch (Exception e) {
			e.printStackTrace();
		}
		inputFile.close();
		return doc;
	}

	private static  String document2String(Document document) throws Exception {
		ByteArrayOutputStream baos;
		Transformer t;

		baos = new ByteArrayOutputStream();
		t = TransformerFactory.newInstance().newTransformer();
		t.transform(new DOMSource(document), new StreamResult(baos));
		return baos.toString();

	} 

	protected static HashMap<String,String> getRoles(String groupType) throws UserManagementFileNotFoundException, UserManagementIOException {
		HashMap<String,String> hMap = new HashMap<String,String>();
		File file = new File (Settings.getInstance().getProperty("sharedDir")+ File.separator +  "roles-config.xml");

		Document dom = null;
		try {
			FileInputStream in = new FileInputStream(file);
			dom = convertStringToDocument(in);
			in.close();
		} catch (FileNotFoundException e) {
			throw new UserManagementFileNotFoundException("Roles config file not found. Check if the config file exists", e);
		} catch (IOException e) {
			throw new UserManagementIOException("Error writing to the roles config file", e);
		}
		Element elem = dom.getDocumentElement();

		NodeList nl = elem.getElementsByTagName(groupType);
		for(int i=0;i<nl.getLength();i++){

			Element el = (Element)nl.item(i);

			NodeList nl2 = el.getElementsByTagName("role");
			for(int j=0;j<nl2.getLength();j++){
				Element el2 = (Element)nl2.item(j);
				NamedNodeMap map = el2.getAttributes();
					hMap.put(map.getNamedItem("name").getNodeValue().toString(), map.getNamedItem("description").getNodeValue().toString());
				}
			
		}
		
		return hMap;
	}
	
	protected static void updateRoles(HashMap<String, String> rolesMap, String groupType) throws UserManagementIOException, UserManagementFileNotFoundException{
		File file = new File (Settings.getInstance().getProperty("sharedDir")+ File.separator +  "roles-config.xml");
		Document dom;
		Iterator<String> keyIter = rolesMap.keySet().iterator();
		try {
			dom = convertStringToDocument(new FileInputStream(file));
		} catch (FileNotFoundException e) {
			throw new UserManagementFileNotFoundException("Roles config file not found. Check if the config file exists", e);
		} catch (IOException e) {
			throw new UserManagementIOException("Error writing to the roles config file", e);
		}
		Element elem = dom.getDocumentElement();
		NodeList nl = elem.getElementsByTagName(groupType);
		for(int i=0;i<nl.getLength();i++){

			Element el = (Element)nl.item(i);
			NodeList nl2 = el.getElementsByTagName("role");
			while(nl2.getLength()>0){
				Element el2 = (Element)nl2.item(nl2.getLength()-1);
				el.removeChild(el2);
			}
		
				while(keyIter.hasNext()){
					Element role = dom.createElement("role");

					Element roleNode = (Element)el.appendChild(role);
					String roleName = keyIter.next();
					roleNode.setAttribute("name", roleName);

					roleNode.setAttribute("description", rolesMap.get(roleName));
				}
			}
		FileWriter out = null;
		try {
			out = new FileWriter(file);
		} catch (IOException e) {
			throw new UserManagementIOException("Error writing to the roles config file", e);
		}
		try {
			out.write(document2String(dom));
			out.flush();
			out.close();
		} catch (IOException e) {
			throw new UserManagementIOException("Error writing to the roles config file", e);
		} catch (Exception e) {
			e.printStackTrace();
		} 
	
	}
}
