package org.gcube.common.informationsystem.client.eximpl;


import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.axis.message.addressing.AttributedURI;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.apache.axis.utils.XMLUtils;

import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedQueryException;
import org.gcube.common.core.informationsystem.client.ISClient.ISMalformedResultException;
import org.gcube.common.core.contexts.GHNContext;
import org.gcube.common.core.resources.GCUBEResource;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.common.informationsystem.client.eximpl.utils.Couple;

import org.globus.wsrf.ResourceKey;
import org.globus.wsrf.impl.SimpleResourceKey;
import org.globus.wsrf.utils.AddressingUtils;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Text;
import org.xml.sax.InputSource;

/**
 * 
 * 
 * @author andrea
 *
 */
public class ExistClientUtil {


	/**
	 * Message Logger
	 */
	protected static GCUBELog logger = new GCUBELog(ExistClientUtil.class);

	/**
	 * 
	 * @param record
	 * @return
	 * @throws ISMalformedResultException
	 */
	public static String parseRecord(String record) throws ISMalformedResultException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new ISMalformedResultException(e);
		}


		StringReader reader = new StringReader(record);
		InputSource source = new InputSource(reader);
		Document domDoc = null;
		try {
			domDoc = builder.parse(source);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ISMalformedResultException(e);
		}

		Element root = domDoc.getDocumentElement();
		return root.getTextContent().trim();
	}

	/**
	 * 
	 * @param record
	 * @param tagName
	 * @return
	 * @throws ISMalformedResultException
	 */
	public static String parseRecord(String record,String tagName) throws ISMalformedResultException{
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		DocumentBuilder builder = null;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
			throw new ISMalformedResultException(e);
		}


		StringReader reader = new StringReader(record);
		InputSource source = new InputSource(reader);
		Document domDoc = null;
		try {
			domDoc = builder.parse(source);
		} catch (Exception e) {
			e.printStackTrace();
			throw new ISMalformedResultException(e);
		}

		Element root = domDoc.getDocumentElement();
		NodeList resultSetChilds= root.getElementsByTagName(tagName);

		Element resultChild=(Element) resultSetChilds.item(0);

		return resultChild.getTextContent().trim();
	}
	
	/**
	 * 
	 * @param record
	 * @return
	 * @throws ISMalformedResultException
	 */
	public static EndpointReferenceType parseEndpointReferenceTypeRecord (String record) throws ISMalformedResultException{
		
		EndpointReferenceType epr = new EndpointReferenceType();
		
		try {	
			
    		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
    		factory.setNamespaceAware(true);
		
    		DocumentBuilder builder = factory.newDocumentBuilder();
		
    		StringReader reader = new StringReader(record);
    		InputSource source = new InputSource(reader);
    		Document domDoc = builder.parse(source);
		
    		String Address = "";
    		String key ="";
    		String namespaceKey="";
    		String localNameKey="";
		
		    
    		Element root = domDoc.getDocumentElement();      
    		NodeList resultSetChilds = root.getElementsByTagName("RESULT");
   
    	
    			boolean keyExist = false;
    	    	NodeList EPRChild= resultSetChilds.item(0).getChildNodes(); 
    	    	
    	    	for(int i=0; i<EPRChild.getLength(); i++)
    	    	{
    	    		
    	    		
    	    		if (EPRChild.item(i) instanceof Element)
    	    		{   
    	    			Element child=(Element) EPRChild.item(i);
    	    			
    	    			if ( child.getTagName().equals("Source"))
    	    			{
    	    				Address = ((Text)child.getFirstChild()).getNodeValue();
    	    			}else if ( child.getTagName().equals("SourceKey"))
    	    			{
    	    				Text keyElement = (Text)child.getFirstChild();
						
    	    				if (keyElement != null)
    	    				{
    	    					key = keyElement.getNodeValue();
    	    					keyExist=true;
    	    				}
    	    			}else if (child.getTagName().equals("CompleteSourceKey") && keyExist)
    	    			{
    	    									
													
							
							if (child instanceof Element) {
								Element completeKey =(Element)child;
								
								NodeList listChild =completeKey.getChildNodes();
								
								
								for (int x = 0; x < listChild.getLength(); x++) {
									
									if (listChild.item(x) instanceof Element) {
										Element childEl = (Element) listChild.item(x);
										
										if (childEl != null ){
											Text keyElement = (Text)childEl.getFirstChild();
											
											key = (String)keyElement.getNodeValue();
											//logger.debug("key " +key);
											namespaceKey = (String) childEl.getNamespaceURI();
											//logger.debug("ns "+namespaceKey);
											localNameKey =(String) childEl.getLocalName();
											//logger.debug("local " +localNameKey);
											
										}
									}
								}
							}
    	    			}
    	    		}
    	    		
    	    		
    	    	}
    	    
				
				if (keyExist) {
					logger.trace("Trying to create EPR with namespaceKey "+ namespaceKey + " localNameKey " +localNameKey +" and key " + key);
					ResourceKey key_resource = new SimpleResourceKey(new QName(namespaceKey,localNameKey), key);
					epr = AddressingUtils.createEndpointReference(Address, key_resource);
					
				}
				else 
					epr.setAddress(new AttributedURI(Address));
				
    	    
    	
    	}catch(Exception e){
    		logger.error("Error parsing xQuery result");
    		throw new ISMalformedResultException(e);
		}
    	
    	return epr;
	}

	/**
	 * It assumes that the resultSet is <Record><Resource>...</Resource>
	 * @exception ISMalformedResultException
	 */
	public static GCUBEResource parseGCUBEResourceResult(String resultSet,Class<? extends GCUBEResource> resourcetype) throws ISMalformedResultException{
		
		GCUBEResource resource =null;
		StringReader reader = null;
		try {	
			resource =GHNContext.getImplementation(resourcetype);
			
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);

			DocumentBuilder builder = factory.newDocumentBuilder();

			 new StringReader(resultSet);
			InputSource source = new InputSource(reader);
			Document domDoc = builder.parse(source);
			Element root= domDoc.getDocumentElement();
			NodeList childRoot= root.getElementsByTagName("Record");
			Element childElement = (Element)childRoot.item(0);
			reader = new StringReader(XMLUtils.ElementToString(childElement));
			resource.load(reader);
		}catch(Exception e){
			e.printStackTrace();
			logger.error("Error parsing xQuery result");
			throw new ISMalformedResultException(e);
		}	
		
		return resource;
	}
	

	
	public  static String queryAddAuthenticationControl(String query,String filtering) throws ISMalformedQueryException
	{
		if (filtering=="") return query;
		int wherePathIndex=0;
		int returnPathIndex=0;
		int collIndexEnd=0;
		int collIndexStart=0;
		boolean whereFinded=false;
		String forInsertFinal="";
		String whereInsertFinal="";
		String temp="";
		
		String forPropertiesString="\n *VAR* in *COLLECTION*/Data\n";
		String forString=" *VAR* in *COLLECTION*/Scopes \n";
		String authString=" (ends-with(*VAR*/child::*[local-name()='Scope'],'"+filtering+"') or ends-with(*VAR*/child::*[local-name()='Scope'],'"+filtering.substring(0,filtering.lastIndexOf("/"))+"')) ";
		String authStringNormal=" ends-with(*VAR*/child::*[local-name()='Scope'],'"+filtering+"') ";  
		
		String queryFiltered;
		List<Couple> collInsert= new ArrayList<Couple>();
		Pattern wherePattern= Pattern.compile("where");
		//mathcing , $result in $outer/Data 
		Pattern inSubResult= Pattern.compile("(,\\s*([^\\s]*)\\s*in\\s*([^\\s]*)\\s*)(where|order\\sby|return)");
		Pattern returnPattern= Pattern.compile("order\\sby|return");
		

		Pattern collectionPattern= Pattern.compile("[^\\s]*\\s*in\\s*collection\\s*[^\\s,]*");
		Pattern varPattern= Pattern.compile("[^\\s]*");
		Pattern resourcePattern= Pattern.compile("\\scollection\\s*\\([^\\)]*.*/Resource",  Pattern.DOTALL);
		Pattern propertiesPattern=Pattern.compile("\\scollection\\s*\\([^\\)]*.*/Document",  Pattern.DOTALL);
		Matcher varMat;
		Matcher resourceMat;
		Matcher collMat=collectionPattern.matcher(query);
		collMat.reset();
		String forStringTemp="";
		while (collMat.find(collIndexEnd))
		{
			
			try{
				collIndexEnd=collMat.end();
				temp=collMat.group();
				collIndexStart=collMat.start();
			}catch(IllegalStateException e){ 
				logger.warn("error parsing collection statement");
				}
			varMat= varPattern.matcher(temp);
			boolean propBool=false;
			if (temp.contains("/Properties")) 
			{
				resourceMat= propertiesPattern.matcher(temp);
			    propBool=true;
			}
			else resourceMat=resourcePattern.matcher(temp);
			varMat.lookingAt();
			resourceMat.find();
			String tempPath="";
			try{
				tempPath= temp.substring(resourceMat.end());
				if (propBool) 
				{
					String resourceMatString= resourceMat.group();
					forStringTemp=forPropertiesString.replace("*VAR*","$entry"+collInsert.size()+"ValueAuth" ).replace("*COLLECTION*", resourceMatString );
					Couple c= new Couple("let "+varMat.group()+" := "+" $entry"+collInsert.size()+"ValueAuth/.."+tempPath, true);
					collInsert.add(c);
				}
				else
				{
					String resourceMatString= resourceMat.group();
					forStringTemp=forString.replace("*VAR*","$entry"+collInsert.size()+"ValueAuth" ).replace("*COLLECTION*",resourceMatString );
					Couple c= new Couple("let "+varMat.group()+" := "+" $entry"+collInsert.size()+"ValueAuth/.."+tempPath, (resourceMatString.contains("/Profiles/RunningInstance") || resourceMatString.contains("/Profiles/GHN") || resourceMatString.contains("/Profiles/Service")));
					collInsert.add(c);
				}
			}catch(IllegalStateException e){ logger.debug("error parsing statement");}
			query=query.substring(0, collIndexStart)+ forStringTemp +query.substring(collIndexEnd);  
			collMat=collectionPattern.matcher(query);
		}
		
		
		
		if (collInsert.size()==0) return query;
		//concat the let statements
		
		
		for (int i=0; i<collInsert.size(); i++)
		{	
			if (i==collInsert.size()-1){
				if(collInsert.get(i).isStrangeBehaviour())
					whereInsertFinal+=authString.replace("*VAR*", "$entry"+i+"ValueAuth");
				else whereInsertFinal+=authStringNormal.replace("*VAR*", "$entry"+i+"ValueAuth");
			}else{ 
				if(collInsert.get(i).isStrangeBehaviour())
					whereInsertFinal+=authString.replace("*VAR*", "$entry"+i+"ValueAuth")+" and " ;
				else whereInsertFinal+=authStringNormal.replace("*VAR*", "$entry"+i+"ValueAuth")+" and " ;
			}
		   forInsertFinal+="\n"+collInsert.get(i).getToInsert()+" "; 		
		}
		
		Matcher whereMat=wherePattern.matcher(query);
		Matcher inSubResultMat= inSubResult.matcher(query);
		Matcher returnMat=returnPattern.matcher(query);
		whereMat.reset();
		returnMat.reset(); 
		whereFinded=whereMat.find();
		returnMat.find();
		String inSubResultString="";
		try{
			inSubResultMat.find();
			inSubResultString= inSubResultMat.group(1);
		}catch(Exception e){}
		try{ 
			
			wherePathIndex=whereMat.start();
		}catch(IllegalStateException e){ logger.debug("where not found");}
		try{
			returnPathIndex=returnMat.start();
		}catch(IllegalStateException e){ logger.error("error parsing return statement"); throw new ISMalformedQueryException();}
		if (whereFinded) 
			queryFiltered=query.substring(0,wherePathIndex)+"\nwhere "+whereInsertFinal
			+" and ("+query.substring(wherePathIndex+5, returnPathIndex)+" ) \n"+query.substring(returnPathIndex);
		else 
			queryFiltered=query.substring(0,returnPathIndex)+ "\nwhere "+whereInsertFinal+" \n "+query.substring(returnPathIndex);
		
		//logger.trace("queryFiltered to match: " + queryFiltered);
		Pattern letPattern=Pattern.compile("let.*:=", Pattern.DOTALL);
		Matcher letMat=letPattern.matcher(queryFiltered);
		whereMat=wherePattern.matcher(queryFiltered);
		whereMat.reset();
		whereMat.find();
		
		boolean letFinded=letMat.find(collIndexStart);
		int letPathIndex=0;
		try{
			
			wherePathIndex=whereMat.start();
			if (letFinded) letPathIndex=letMat.start();
		}catch(IllegalStateException e){ logger.error("error parsing let statement"); throw new ISMalformedQueryException();} 		
		
		
		if (!letFinded) {		
			queryFiltered=queryFiltered.substring(0,wherePathIndex)+forInsertFinal +queryFiltered.substring(wherePathIndex);
		}
		else {			
			queryFiltered=queryFiltered.substring(0,letPathIndex)+forInsertFinal+queryFiltered.substring(letPathIndex);
		}
		queryFiltered=queryFiltered.replace(inSubResultString, "");
		
		if (inSubResultString.compareTo("")!=0){
			String firstVar=inSubResultMat.group(2);
			String replaceVar=inSubResultMat.group(3);
			queryFiltered= queryFiltered.replace(firstVar, replaceVar);
			//System.out.println(firstVar);
			//System.out.println(replaceVar);
		}
		return queryFiltered;
	}

	
	
	public static String resourcePropertyParser(String record) throws ISMalformedResultException {
		
		String textRes="";
			try{
				DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
				factory.setNamespaceAware(true);
			
				DocumentBuilder builder = factory.newDocumentBuilder();
				
				StringReader reader = new StringReader(record);
				InputSource source = new InputSource(reader);
				Document domDoc = builder.parse(source);
				
				NodeList childs = domDoc.getDocumentElement().getChildNodes();
				for(int i=0; i<childs.getLength(); i++)
				{
					NodeList recordChilds=childs.item(i).getChildNodes();
					for (int k=0; k<recordChilds.getLength(); k++)
					{
						if (recordChilds.item(k) instanceof Element)
						{
							
							NodeList resultSetChilds= recordChilds.item(k).getChildNodes();
							for(int j=0; j<resultSetChilds.getLength(); j++)
							{
								String temp="";
								if (resultSetChilds.item(j) instanceof Element)
								{
									
								  textRes+=XMLUtils.ElementToString((Element) resultSetChilds.item(j)); 
								  
								}
								else if((resultSetChilds.item(j) instanceof Text) && !(Pattern.matches("\\s*", temp=resultSetChilds.item(j).getTextContent())))
									textRes+=temp;
							}
							
						}
						
					}
				}
			
			}catch(Exception e){
				e.printStackTrace();
				logger.error("GetResourcePropertyValue error in parsing the result set\n"+e);
				throw new ISMalformedResultException(e);
				
			}
			return textRes;
		}
		
}


