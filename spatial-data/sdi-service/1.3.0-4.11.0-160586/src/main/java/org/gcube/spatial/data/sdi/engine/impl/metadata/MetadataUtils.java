package org.gcube.spatial.data.sdi.engine.impl.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;

import org.gcube.common.resources.gcore.utils.XPathHelper;
import org.gcube.portlets.user.uriresolvermanager.UriResolverManager;
import org.gcube.portlets.user.uriresolvermanager.exception.IllegalArgumentException;
import org.gcube.portlets.user.uriresolvermanager.exception.UriResolverMapException;
import org.gcube.spatial.data.geonetwork.utils.ScopeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class MetadataUtils {

	public static Transformer transformer =null;
	public static DocumentBuilder docBuilder =null;


	static HashMap<String,String> namespaces=new HashMap<String,String>();

	static{
		try{
			DocumentBuilderFactory factory=DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			docBuilder =  factory.newDocumentBuilder();


			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			transformer = transformerFactory.newTransformer();


			namespaces.put("gmd", "http://www.isotc211.org/2005/gmd");
			namespaces.put("gco", "http://www.isotc211.org/2005/gco");
			namespaces.put("fra", "http://www.cnig.gouv.fr/2005/fra");
			namespaces.put("xlink", "http://www.w3.org/1999/xlink");
			namespaces.put("gml", "http://www.opengis.net/gml");
			namespaces.put("xsi", "http://www.w3.org/2001/XMLSchema-instance");
			namespaces.put("gmi", "http://www.isotc211.org/2005/gmi");
			namespaces.put("gmx", "http://www.isotc211.org/2005/gmx");




		}catch(Exception e){
			throw new RuntimeException("Unable to init Fixer ",e);
		}
	}
	
	
	public static enum Position{
		sibling_after,sibling_before,first_child,last_child,replace
	}
	
	public static XPathHelper getHelper(Node root){
		XPathHelper toReturn =new XPathHelper(root);
		for(Entry<String,String> entry:namespaces.entrySet())
			toReturn.addNamespace(entry.getKey(), entry.getValue());
		return toReturn;
	}
	

	public static String readFile(String path) throws IOException{
		byte[] encoded = Files.readAllBytes(Paths.get(path));
		return new String(encoded);
	}

	public static String getGisLinkByUUID(String uuid) throws UriResolverMapException, IllegalArgumentException{
		Map<String,String> params=new HashMap();
		params.put("scope", ScopeUtils.getCurrentScope());
		params.put("gis-UUID", uuid);
		UriResolverManager resolver = new UriResolverManager("GIS");
		String toReturn= resolver.getLink(params, false);
		return toReturn;
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
