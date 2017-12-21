package org.gcube.data.transfer.plugins.thredds;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import lombok.extern.slf4j.Slf4j;
@Slf4j
public class CommonXML {

	
	
	static Transformer transformer =null;
	static DocumentBuilder docBuilder =null;


	static HashMap<String,String> namespaces=new HashMap<String,String>();

	
	
	
	static{
		try{
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			docBuilder =  factory.newDocumentBuilder();


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();


			namespaces.put("xmlns", "http://www.unidata.ucar.edu/namespaces/thredds/InvCatalog/v1.0");
			namespaces.put("xlink", "http://www.w3.org/1999/xlink");
			namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			//			namespaces.put("gml", "http://www.opengis.net/gml");
			//			namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			//			namespaces.put("gmi", "http://www.isotc211.org/2005/gmi");
			//			namespaces.put("gmx", "http://www.isotc211.org/2005/gmx");




		}catch(Exception e){
			throw new RuntimeException("Unable to init Fixer ",e);
		}
	}
	
	
	public static Document getDocument(File xmlFile) throws SAXException, IOException {
		log.debug("Parsing {} ",xmlFile.getAbsolutePath());		
		InputSource inputSource = new InputSource(new FileInputStream(xmlFile));
		//Get document owner		
		Element documentNode = docBuilder.parse(inputSource).getDocumentElement();
		return documentNode.getOwnerDocument();
	}
	
	public static XPathHelper getHelper(Node root){
		XPathHelper toReturn =new XPathHelper(root);
		for(Entry<String,String> entry:namespaces.entrySet())
			toReturn.addNamespace(entry.getKey(), entry.getValue());
		return toReturn;
	}
	
	public static void writeOut(Document document, File destination) throws IOException, TransformerException {
		DOMSource source = new DOMSource(document);
		StreamResult result = new StreamResult(new FileWriter(destination));
		transformer.transform(source, result);
		result.getWriter().flush();
		result.getWriter().close();
	}
	
	
	public static enum Position{
		sibling_after,sibling_before,first_child,last_child,replace
	}

	public static void addContent(String path, Document doc, String toAddContent, XPathHelper documentHelper,Position position) throws SAXException, IOException{
		NodeList nodelist=documentHelper.evaluateForNodes(path);
		if(nodelist==null||nodelist.getLength()==0) throw new RuntimeException("Path "+path+" not found in document");
		//		if(nodelist.getLength()>1) throw new RuntimeException("Invalid Path "+path+"."+nodelist.getLength()+" entries found");
		Node targetNode=nodelist.item(0);

		Document online=docBuilder.parse(new ByteArrayInputStream(toAddContent.getBytes()));
		Node toAdd=doc.importNode(online.getDocumentElement(), true);
		switch(position){
		case first_child: {
			targetNode.insertBefore(toAdd, targetNode.getFirstChild());
			break;
		}
		case last_child:{targetNode.appendChild(toAdd);
		break;}
		case replace : {
			Node parent=targetNode.getParentNode();
			parent.replaceChild(toAdd, targetNode);
			break;
		}
		case sibling_after :{
			Node currentlyNext=targetNode.getNextSibling();
			Node parent=targetNode.getParentNode();
			if(currentlyNext!=null)parent.insertBefore(toAdd, currentlyNext);
			else parent.appendChild(toAdd);
			break;
		}
		case sibling_before :{
			Node parent=targetNode.getParentNode();
			parent.insertBefore(toAdd, targetNode);
			break;
		}
		}

	}
}
