package org.gcube.vremanagement.softwaregateway.testsuite;

import java.io.File;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.BadCoordinatesException;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing.XmlParse;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.util.FileUtilsExtended;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class UpdateProfileTest {

	String path=null;
	GCubeCoordinates coord=null;
//	String pathNew=null;
	@Before
	public void initialize() throws BadCoordinatesException{
		path="settings/profileTest.xml";
//		pathNew="settings/profileModified.xml";
		coord=new GCubeCoordinates("Class", "Name", "1.00.00", "publisher", "1.5");
	}
	
	@Test
	public void updateFile(){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;  
		Document document = null;  
		File profile=new File(path);
		try{  
			builder = factory.newDocumentBuilder(); 
			document = builder.parse( profile );  
			filterElements( document, coord);
//			profile=doc2file(document,path);
			String result=xmlToString(document);
			FileUtilsExtended.stringToFile(result, new File("settings/profileNext.xml"));
//			profile=printNodeElements(document., file);
		}catch(Exception e){
			e.printStackTrace();
		}
//		return profile;
	}
	
	/* 
	 * filter all package elements whose tag name = filter 
	 */  
	public void filterElements( Node parent, GCubeCoordinates coord){  
		NodeList children = parent.getChildNodes();  
		String version="";
		String groupId="";
		String artifactId="";
		for( int i=0; i < children.getLength(); i++ ){  
			Node child = children.item( i );  
			System.out.println("parse: "+child.getNodeName()+" node");
			// only interested in elements  
			if( child.getNodeType() == Node.ELEMENT_NODE ){  
				// remove elements whose tag name  = filter  
				// otherwise check its children for filtering with a recursive call  
				if( child.getNodeName().equals("Coordinate") ){ 
					NodeList coordinateNodes=child.getChildNodes();
					String name="";
					String value="";
					for( int j=0; j<coordinateNodes.getLength(); j++){
						Node node=coordinateNodes.item(j);
						System.out.println("Coordinate. name: "+node.getNodeName()+" value: "+node.getTextContent());
						if(node.getNodeName().equalsIgnoreCase("name")){
							name=node.getTextContent();
						}else if(node.getNodeName().equalsIgnoreCase("Value")){
							value=node.getTextContent();
						}
					}
//					System.out.println(" extracted Name: "+name+"   value; "+version);
					if(name.equalsIgnoreCase("groupid")){
						groupId=value;
					}else if(name.equalsIgnoreCase("artifactId")){
						artifactId=value;//
					}else if(name.equalsIgnoreCase("version")){
						version=value;
					}
					System.out.println("gid: "+groupId+" aid: "+artifactId+" v: "+version);
					System.out.println("PACKAGE NAME: "+coord.getPackageName()+" version "+coord.getPackageVersion());
					if(!groupId.isEmpty() && !artifactId.isEmpty() && !version.isEmpty()){
						if((coord.getPackageName().equalsIgnoreCase(artifactId)) && (coord.getPackageVersion().equalsIgnoreCase(version))){
							System.out.println("PACKAGE FOUND");
						}else{
//							System.out.println("REMOVE CHILD: "+child.getNodeName());
							Node granParent=parent.getParentNode();
							Node ancient=granParent.getParentNode();
							System.out.println("ANCIENT IS: "+ancient.getNodeName());
							System.out.println("GRAN PARENT IS: "+granParent.getNodeName());
							ancient.removeChild( granParent);
						}

					}
				} else {  
					filterElements( child, coord);  
				}  
			} 		
		}  
	}  
	   
	
	
//	 public  File doc2file(Node node, String path) {
//		 File out=null;
//		 try {
//	        	out = new File(path);
//	            Source source = new DOMSource(node);
//	            Result result = new StreamResult(out);
//	            TransformerFactory factory = TransformerFactory.newInstance();
//	            Transformer transformer = factory.newTransformer();
//	            transformer.transform(source, result);
//	        } catch (TransformerConfigurationException e) {
//	            e.printStackTrace();
//	        } catch (TransformerException e) {
//	            e.printStackTrace();
//	        }
//	        return out;
//	    }
	 
	 public String xmlToString(Node node) {
	        try {
	            Source source = new DOMSource(node);
	            StringWriter stringWriter = new StringWriter();
	            Result result = new StreamResult(stringWriter);
	            TransformerFactory factory = TransformerFactory.newInstance();
	            Transformer transformer = factory.newTransformer();
	            transformer.transform(source, result);
	            return stringWriter.getBuffer().toString();
	        } catch (TransformerConfigurationException e) {
	            e.printStackTrace();
	        } catch (TransformerException e) {
	            e.printStackTrace();
	        }
	        return null;
	    }
	
}
