package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.parsing;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
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

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.Coordinates;
import org.gcube.vremanagement.softwaregateway.impl.coordinates.GCubeCoordinates;
import org.gcube.vremanagement.softwaregateway.impl.exceptions.ServiceNotAvaiableFault;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.FileUtilsExtended;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for XML parsing
 * @author Roberto Cirillo (ISTI - CNR)
 *
 */
public class XmlParse {
	
	
	private GCUBELog logger= new GCUBELog(XmlParse.class);
	/**
	 * Extract a URL from a valid Nexus response
	 * @param xml nexus response
	 * @param groupName
	 * @param artifact
	 * @param extension
	 * @param ver
	 * @param pom
	 * @param classifier 
	 * @return
	 * @throws IOException
	 * @throws SAXException
	 */
	public String getURlFromSearch(String xml, String groupName, String artifact, String extension, String ver, boolean pom, String classifier) throws IOException, SAXException{
		String url=null;
		logger.trace("getUrlFromSearch method");
		logger.debug("search: "+groupName+" "+artifact+" "+ ver+" with ext: "+extension+" and classifier "+classifier);
		Element root = extractRootElement(xml.getBytes());
		try {
			List<Node> dataNode=extractChild(root, "data");
			List<Node> artifactList=extractChild(dataNode.get(0), "artifact");
			if((artifactList != null) && (artifactList.size() > 0)){
				logger.debug("artifacts: "+artifactList.size());
				for(int i=0; i < artifactList.size(); i++){
					Node child = artifactList.get(i);
					List<Node> id=extractChild(child, "artifactId");
					String artifactId=id.get(0).getTextContent();
					logger.debug("artifactId: "+artifactId);
					if(artifactId.equalsIgnoreCase(artifact)){
						List<Node> extensionList=extractChild(child, "extension");
						String ext=extensionList.get(0).getTextContent();
	 // if the variable extension is null then i get all the urls founded
	// else i get the first url that matches with the extension variable		
						logger.debug("extension: "+ext);
						if((extension == null) || (ext.equalsIgnoreCase("pom") && pom) || (ext.equalsIgnoreCase(extension))|| (ext.equalsIgnoreCase("war")) || (ext.equalsIgnoreCase("jar")) || (ext.equalsIgnoreCase("gar")) || (classifier!=null)){
							if(ver!=null){
								List<Node> versionList=extractChild(child, "version");
								String version=versionList.get(0).getTextContent();
								logger.debug("version: "+version);
								if(ver.contains(version)){
									if(pom){
										List<Node> pomList=extractChild(child, "pomLink");
										url=pomList.get(0).getTextContent();
										logger.debug("pom link: "+url);
										if(url!= null && !url.isEmpty())
											break;
									}else{
										List<Node> classifierList=extractChild(child, "classifier");
										if(classifierList !=null ){
											String classifierFound=classifierList.get(0).getTextContent();
											logger.debug("classifier found: "+classifierFound+ " classifier searched: "+classifier);
											if((classifierFound==null) && (classifier==null)){
												List<Node> uriList=extractChild(child, "artifactLink");
												url=uriList.get(0).getTextContent();
												if((extension != null))
													break;
											}else if((classifierFound!= null) && (classifier !=null) && (classifier.equalsIgnoreCase(classifierFound))){
												List<Node> uriList=extractChild(child, "artifactLink");
												url=uriList.get(0).getTextContent();
												break;
				// if i found a servicearchive classifier, i get the url but don't break the cycle 									
											}else if(classifierFound.equalsIgnoreCase("servicearchive") && (classifier==null)){
												List<Node> uriList=extractChild(child, "artifactLink");
												url=uriList.get(0).getTextContent();												
											}
										}else{
											logger.debug("classifier"+classifier+" not  found. ");
											if((classifier == null) || (classifier.equalsIgnoreCase("servicearchive") && extension.equalsIgnoreCase("jar"))){
												List<Node> uriList=extractChild(child, "artifactLink");
												url=uriList.get(0).getTextContent();
												if(extension != null)
													break;
											}
										}
									}
								}
							}else{
								logger.debug("version is null ");
								if(pom){
									List<Node> pomList=extractChild(child, "pomLink");
									url=pomList.get(0).getTextContent();
									logger.debug("pom link found: "+url);
									if(url!= null && !url.isEmpty())
										break;
								}else{
									List<Node> uriList=extractChild(child, "artifactLink");
									url=uriList.get(0).getTextContent();
								}
								if(extension != null)
									break;
							}
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		logger.debug(" url found: "+url);
		return url;
	}

	/**
	 * Extracts a root element from a xml document 
	 * @param xml
	 * @return
	 */
	private Element extractRootElement(byte[] xml) {
		logger.trace(" extractRoot method ");
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		Document domDocument = null;
		try {
			builder = factory.newDocumentBuilder();
			domDocument = builder.parse(new ByteArrayInputStream(xml));
		} catch (Exception e) {
			e.printStackTrace();
		}
		Element root = domDocument.getDocumentElement();
		return root;
	}
	
	/**
	 * Extracts a list of child element, matches with the input parameter name, from a parent element  in a xml document 
	 * @param node
	 * @param name
	 * @return
	 */
	private List<Node> extractChild(Node node, String name){
		logger.trace(" extractChild method: "+name);
		List<Node> result=null;
		NodeList nodeList = node.getChildNodes();
		if(nodeList!=null){
			for (int i = 0; i < nodeList.getLength(); i++) {
				Node child = nodeList.item(i);
				String tag=child.getNodeName();
				if(tag.equalsIgnoreCase(name)){
					if(result==null)
						result=new ArrayList<Node>();
					result.add(child);
				}
			}
		}else{
			logger.debug("no packages found ");
		}
		return result;
	}

	/**
	 * 
	 * @param profile
	 * @param coordinates
	 * @return
	 * @throws ServiceNotAvaiableFault
	 */
	public File updateProfile(File profile, Coordinates coordinates) throws ServiceNotAvaiableFault {
		logger.trace("updateProfile method");
		boolean found=false;
		try{
			String profileString=FileUtilsExtended.fileToString(profile.getAbsolutePath());	
			logger.debug("update profile  "+profileString);
			Element root= extractRootElement(profileString.getBytes());
			List<Node>nodes=extractChild(root, "Profile");
			if(nodes!= null && nodes.size()==1){
				List<Node> packages=extractChild(nodes.get(0), "Packages");
				if (packages==null)
					throw new ServiceNotAvaiableFault(" no packages founded in profile ");
				for(Node node : packages){
					List<Node> childs = extractChild(node, "Software");
					updateCoordinates(node, childs, coordinates);
					if(found){
						
					}
				}

			}else{
				logger.error("incorrect profile, ");
				throw new ServiceNotAvaiableFault();
			}
		}catch(Exception e){
			throw new ServiceNotAvaiableFault();
		}
		return profile;
	}
	
	/**
	 * Update a file profile.xml remove from the file the package related to the input parameter coord 
	 * @param file profile.xml
	 * @param coord gCubePackage coordinates
	 * @return
	 */
	public File updateFile(File file, GCubeCoordinates coord){
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();  
		DocumentBuilder builder = null;  
		Document document = null;  
		File profile=file;
		try{  
			builder = factory.newDocumentBuilder(); 
			document = builder.parse( profile );  
			filterElements( document, coord);
			String result=xmlToString(document);
			FileUtilsExtended.stringToFile(result, profile);
		}catch(Exception e){
			e.printStackTrace();
		}
		return profile;
	}
	
	/** 
	 * filter all package elements whose tag name = filter 
	 */  
	public void filterElements( Node parent, GCubeCoordinates coord){  
		NodeList children = parent.getChildNodes();  
		String version="";
		String groupId="";
		String artifactId="";
		for( int i=0; i < children.getLength(); i++ ){  
			Node child = children.item( i );  
			logger.debug("parse: "+child.getNodeName()+" node");
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
						logger.debug("Coordinate. name: "+node.getNodeName()+" value: "+node.getTextContent());
						if(node.getNodeName().equalsIgnoreCase("name")){
							name=node.getTextContent();
						}else if(node.getNodeName().equalsIgnoreCase("Value")){
							value=node.getTextContent();
						}
					}
					if(name.equalsIgnoreCase("groupid")){
						groupId=value;
					}else if(name.equalsIgnoreCase("artifactId")){
						artifactId=value;//
					}else if(name.equalsIgnoreCase("version")){
						version=value;
					}
					logger.debug("gid: "+groupId+" aid: "+artifactId+" v: "+version);
					logger.debug("PACKAGE NAME: "+coord.getPackageName()+" version "+coord.getPackageVersion());
					if(!groupId.isEmpty() && !artifactId.isEmpty() && !version.isEmpty()){
						if((coord.getPackageName().equalsIgnoreCase(artifactId)) && (coord.getPackageVersion().equalsIgnoreCase(version))){
							logger.debug("PACKAGE FOUND");
						}else{
//							System.out.println("REMOVE CHILD: "+child.getNodeName());
							Node granParent=parent.getParentNode();
							Node ancient=granParent.getParentNode();
							logger.debug("ANCIENT IS: "+ancient.getNodeName());
							logger.debug("GRAN PARENT IS: "+granParent.getNodeName());
							ancient.removeChild( granParent);
						}

					}
				} else {  
					filterElements( child, coord);  
				}  
			} 		
		}  
	}  
	   
	
	/**
	 * Trasform a dom structure in a string  
	 * @param node
	 * @return
	 */
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


	/**
	 * Remove child nodes from a parent node if one or more  child nodes matched with the coordinates input parameter 
	 * @param parent
	 * @param nodes
	 * @param coordinates
	 */
	private void updateCoordinates(Node parent, List<Node> nodes,
			Coordinates coordinates) {
		logger.debug(" updateCoordinates method");
		for(Node node : nodes){
			List<Node> childs=extractChild(node, "name");
			if((childs!=null)){
				Node child=childs.get(0);
				if(!child.getTextContent().equals(coordinates.getPackageName())){
					logger.debug("removeChild: "+child.getNodeValue()+" "+child.getLocalName());
					node.removeChild(child);
				}
			}
		}
	}

	
}
