package org.gcube.data.publishing.gCataFeeder.catalogues.gCat;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;

import javax.ws.rs.WebApplicationException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.data.publishing.gCatFeeder.catalogues.CatalogueController;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.PublishReport;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.CatalogueInteractionException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.ControllerInstantiationFault;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.PublicationException;
import org.gcube.data.publishing.gCatFeeder.catalogues.model.faults.WrongObjectFormatException;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueFormatData;
import org.gcube.data.publishing.gCatFeeder.model.CatalogueInstanceDescriptor;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration;
import org.gcube.data.publishing.gCatFeeder.model.ControllerConfiguration.PublishingPolicy;
import org.gcube.data.publishing.gCatFeeder.model.InternalConversionException;
import org.gcube.gcat.client.Item;
import org.gcube.gcat.client.Profile;
import org.gcube.gcat.client.Resource;
import org.w3c.dom.Document;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class GCatController implements CatalogueController{


	private static ObjectMapper mapper;



	private static DocumentBuilderFactory factory; 
	private static DocumentBuilder builder; 
	private static XPathFactory xPathfactory;
	private static XPath xpath;
	private static XPathExpression expr; 

	static {
		mapper=new ObjectMapper();
		factory= DocumentBuilderFactory.newInstance();
		xPathfactory = XPathFactory.newInstance();
		xpath = xPathfactory.newXPath();




		try {
			builder= factory.newDocumentBuilder();
			expr= xpath.compile("string(//metadataformat/@type)");
		} catch (ParserConfigurationException | XPathExpressionException e) {
			throw new RuntimeException("Unable to initialize Controller");
		}

	}

	private CatalogueInstanceDescriptor desc;

	private String callerToken=null;
	private ControllerConfiguration config;

	public GCatController(CatalogueInstanceDescriptor instance) throws ControllerInstantiationFault {
		try{
			this.desc=instance;

			if(isCustomToken())
				setCustomToken();

			checkInstance();

			if(isCustomToken()) 
				resetToken();


			// setting default configuration
			config=new ControllerConfiguration();
			config.setOnClash(PublishingPolicy.UPDATE);



		}catch(ControllerInstantiationFault e) {
			throw e;
		}
	}

	@Override
	public void configure(ControllerConfiguration config) {
		this.config = config;
	}

	/**
	 * Expected structure 
	 * 
	 * { 
	 * 		"profile" : "<xml_profile>",
	 * 		"item" : "<json_item>",
	 * 	 	"resources" : [
	 * 			"<json_resource>",
	 * 			"<json_resource>",
	 * 				....]
	 * } 
	 * 
	 * NB serialized resources are updated with "package_id" set as the published item id
	 * @throws InternalConversionException 
	 * 
	 * 
	 */
	@Override
	public PublishReport publishItem(CatalogueFormatData arg0) throws WrongObjectFormatException,CatalogueInteractionException,PublicationException, InternalConversionException{
		log.debug("Publishing {} ",arg0);
		String serialized=arg0.toCatalogueFormat();
		try {
			if(isCustomToken())
				setCustomToken();

			log.debug("Parsing data..");
			JsonNode node=mapper.readTree(serialized);
			if(!node.hasNonNull("item")) throw new WrongObjectFormatException("No \"item\" specified in serialized object");
			JsonNode itemNode=node.path("item");
			if(node.hasNonNull("profile")) {
				log.debug("Publishing profile..");
				String xmlProfile=node.path("profile").asText();
				String profileName=getProfileName(xmlProfile);
				createProfile(profileName, xmlProfile);
			}

			log.debug("Publishing Item..");
			String itemResp=createItem(itemNode.toString(),itemNode.path("name").textValue());
			String itemId = getId(itemResp);		
			log.debug("Pubilshed Item with ID {} ",itemId);		

			if(node.hasNonNull("resources")) {
				log.debug("Pubilshing resources for {} ",itemId);
				Iterator<JsonNode> resIterator=node.path("resources").elements();
				while(resIterator.hasNext()) {
					JsonNode resNode=resIterator.next();
					String resourceName=resNode.path("name").textValue();
					createResource(itemId, resourceName, resNode.toString());
				}			
			}

			return new PublishReport(true, itemId);

		} catch(WrongObjectFormatException | PublicationException e) {
			throw e;
		} catch (Throwable e) {
			throw new WrongObjectFormatException("Unable to read format ",e);
		}finally{
			if(isCustomToken())
				resetToken();
		}
	}




	private String createProfile(String profileName,String xmlProfile) throws WrongObjectFormatException, PublicationException{
		Profile profile=null;
		try{
			if(isForcedUrl())
				profile = new Profile(getForcedUrl());
			else profile=new Profile();

			// check if exists 
			boolean exists=false;

			try {
				profile.read(profileName);
				exists=true;
			}catch(Throwable t) {
				exists=false;
			}

			switch(getOnClash()) {
			case SKIP : return null;
			case FAIL : {
				if(exists) throw new PublicationException("Profile "+profileName+" already existing");
				break;
			}
			case UPDATE : {
				if(exists) return profile.update(profileName, xmlProfile);
				break; 
			}
			}			
			return profile.create(profileName, xmlProfile);
		}catch(WebApplicationException e) {
			handleWebException(e);
			return null;
		}catch(MalformedURLException e) {
			throw new CatalogueInteractionException(e);
		}

	}


	private String createResource(String itemId,String resourceName,String toCreate) throws WrongObjectFormatException, PublicationException{
		Resource resource=null;
		try{
			if(isForcedUrl())
				resource = new Resource(getForcedUrl());
			else resource=new Resource();

			// Try to create

			try {
				log.debug("Trying to create resource {} for package {} ",resourceName,itemId);
				return resource.create(itemId, toCreate);
			}catch(WebApplicationException e) {
				// Conflict = resource already existing
				if(e.getResponse().getStatus()==409) {
					switch(getOnClash()) {
					case SKIP : return null;
					case FAIL : throw new PublicationException("Resource "+resourceName+" already existing");
					case UPDATE : {
						// Look for resource id
						log.debug("Looking for resource with name {} under item {} ",resourceName,itemId);
						String resourceList=resource.list(itemId);
						JsonNode listNode=mapper.readTree(resourceList);
						Iterator<JsonNode> iterator=listNode.elements();
						String resourceId=null;
						while(iterator.hasNext()) {
							JsonNode res=iterator.next();
							if(res.path("name").asText().equals(resourceName)) {
								resourceId=res.path("id").asText();
								break;
							}
						}
						log.debug("Found id {} for resource Name {} ",resourceId,resourceName);

						return resource.update(itemId, resourceId, toCreate);
					}
					default : return null;
					}
				}else throw e;
			}


		}catch(WebApplicationException e) {
			handleWebException(e);
			return null;
		}catch(MalformedURLException e) {
			throw new CatalogueInteractionException(e);
		}catch(IOException e) {
			throw new CatalogueInteractionException(e);
		}

	}


	private String createItem(String toCreate,String name) throws WrongObjectFormatException, PublicationException{
		Item item=null;
		try{
			if(isForcedUrl())
				item = new Item(getForcedUrl());
			else item=new Item();
			
			if(existsItem(item,name))
				return item.update(name, toCreate);
			else return item.create(toCreate);
			
		}catch(WebApplicationException e) {
			handleWebException(e);
			return null;
		}catch(MalformedURLException e) {
			throw new CatalogueInteractionException(e);
		}

	}


	private boolean existsItem(Item item,String name) {
		try {
			item.read(name);
			return true;
		}catch(WebApplicationException e) {
			log.debug("Read Item returned error. Assuming it's missing ",e);
			return false;
		}
	}



	// instance utils

	private PublishingPolicy getOnClash() {
		return config.getOnClash();
	}



	private boolean isForcedUrl() {
		return desc.getCustomToken()==null&&desc.getUrl()!=null;
	}

	private URL getForcedUrl() throws MalformedURLException {
		return new URL(desc.getUrl());
	}

	private boolean isCustomToken() {
		return desc.getCustomToken()!=null;
	}

	private void setCustomToken() {
		callerToken=SecurityTokenProvider.instance.get();
		SecurityTokenProvider.instance.set(desc.getCustomToken());
	}

	private void resetToken() {
		SecurityTokenProvider.instance.set(callerToken);
	}

	private void checkInstance() throws ControllerInstantiationFault{
		Item item=null;
		try{
			if(isForcedUrl())
				item=new Item(getForcedUrl());
			else item=new Item();
			item.list(10, 0);
		}catch(Throwable t) {
			String msg=String.format("Unable to contact gCat with configuration %1$s ",desc);
			log.error(msg,t);
			throw new ControllerInstantiationFault(msg,t);
		}
	}

	// Static utils from catalogue-ws

	private static String getProfileName(String xmlProfile) throws WrongObjectFormatException {
		try{
			Document doc = builder.parse(new ByteArrayInputStream(xmlProfile.getBytes()));
			return (String) expr.evaluate(doc, XPathConstants.STRING);
		}catch(Throwable t) {
			throw new WrongObjectFormatException("Unable to parse profile. ",t);
		}
	}



	private static String getId(String publishResponse) {		
		try {			
			return mapper.readTree(publishResponse).path("id").textValue();
		} catch (Throwable t) {			
			t.printStackTrace();
			throw new RuntimeException("FAILED Parsing of "+publishResponse);			
		} 
	}

	private static String getPublishedUrl(String publishResponse) {
		try {			
			Iterator<JsonNode> iterator=mapper.readTree(publishResponse).path("extras").elements();
			while(iterator.hasNext()) {
				JsonNode node=iterator.next();
				if(node.path("key").asText().equals("Item URL"))
					return node.path("value").asText();
			}
			return "N/A";
		} catch (Throwable t) {			
			t.printStackTrace();
			throw new RuntimeException("FAILED Parsing of "+publishResponse);		
		} 
	}

	private static void handleWebException(WebApplicationException e) throws WrongObjectFormatException, PublicationException {
		log.debug("Received Web Exception {} ",e);
		String msg=null;
		try{
			e.getResponse().readEntity(String.class);
		}catch(IllegalStateException e1) {
			//unable to read outbound
			msg="Status : "+e.getResponse().getStatus();
		}
		switch(e.getResponse().getStatus()) {
		case 400 : throw new  WrongObjectFormatException("BAD Request : "+msg,e);
		case 401 : throw new CatalogueInteractionException("Unauthorized : "+msg,e);
		case 404 : throw new CatalogueInteractionException("NOT FOUND : "+msg,e); 
		case 405 : throw new CatalogueInteractionException("Method Not Allowed : "+msg,e);
		case 409 : throw new CatalogueInteractionException("Conflict : "+msg,e);
		case 500 : throw new CatalogueInteractionException("Remote Error : "+msg,e);
		default : throw new PublicationException("Unexpected error code : "+msg,e);
		}
	}



}
