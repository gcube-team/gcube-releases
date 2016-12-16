package org.gcube.common;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.xml.namespace.QName;

import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMElement;
import org.gcube.common.data.Header;
import org.gcube.common.data.Metadata;
import org.gcube.common.data.Record;
import org.gcube.common.data.RecordIterator;
import org.gcube.common.repository.Identify;
import org.gcube.common.repository.MetadataFormat;
import org.gcube.common.repository.ResumptionToken;
import org.gcube.common.repository.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author David Uvalle, david.uvalle@gmail.com
 * @version 0.1
 * 
 */
public class Harvester {

	private static Logger log = LoggerFactory.getLogger(Harvester.class);

	private String baseUrl;
	private String identifiersResumptionToken ="";
	private String setResumptionToken = "";
	private boolean hasIdentifierResumptionToken = false;
	private boolean hasSetResumptionToken = false;

	/**
	 * Constructs the harvester using a repository URL.
	 * @param baseUrl A repository valid URL.
	 */
	public Harvester(String baseUrl) throws Exception {
		if(baseUrl==null || baseUrl.isEmpty())
			throw new Exception("baseUrl cannot be null");
		this.baseUrl = baseUrl;
	}


	public String getBaseUrl(){
		return baseUrl;
	}
	/**
	 * Returns an {@link Identify} object containing information about the OAI respository.
	 * @return	A {@link Identify} object.
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public Identify identify() throws Exception {
		// TODO: Add compression and description handling.
		OMElement documentElement = null;
		try {
			documentElement = Utils.getReaderFromHttpGet(baseUrl, "Identify");
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}		
		Iterator<OMElement> it = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","Identify"));
		it.hasNext();
		Iterator<OMElement> identifyElements = it.next().getChildElements();
		Identify identify = new Identify();
		while(identifyElements.hasNext()) {
			OMElement eleme = identifyElements.next();

			if(eleme.getLocalName().equals("repositoryName")) {
				identify.setRepositoryName(eleme.getText());
			}
			else if(eleme.getLocalName().equals("protocolVersion")) {
				identify.setProtocolVersion(eleme.getText());
			}
			else if(eleme.getLocalName().equals("baseURL")) {
				identify.setBaseUrl(eleme.getText());
			}
			else if(eleme.getLocalName().equals("earliestDatestamp")) {
				identify.setEarliestDateStamp(eleme.getText());
			}
			else if(eleme.getLocalName().equals("deletedRecord")) {
				identify.setDeletedRecord(eleme.getText());
			}
			else if(eleme.getLocalName().equals("granularity")) {
				identify.setGranularity(eleme.getText());
			}
			else if(eleme.getLocalName().equals("adminEmail")) {
				identify.addAdminEmail(eleme.getText());
			}
		}
		return identify;
	}

	/**
	 * Returns a List of {@link Header} type, using selective harvesting.
	 * @param from A date.
	 * @param until A date.
	 * @param set A set name supported by the respository.
	 * @return {@link List}
	 * @throws Exception
	 */
	public List<Header> listIdentifiers(String from,String until,String set, String metadataPrefix) throws Exception {
		List<Header> listIdentifiers = new ArrayList<Header>();
		List<Header> tmpListIdentifiers = null;
		listIdentifiers = listIdentifiers("",from,until,set,metadataPrefix);
		if(hasIdentifierResumptionToken) {
			while(!this.identifiersResumptionToken.isEmpty()) 
			{
				tmpListIdentifiers = listIdentifiers(this.identifiersResumptionToken,null,null,null,metadataPrefix);
				for(Header h:tmpListIdentifiers) 
				{
					listIdentifiers.add(h);
				}
			}
		}
		return listIdentifiers;
	}

	/**
	 * Returns a List of {@link Header} type.
	 * @return A {@link List} of headers.
	 */
	public List<Header> listIdentifiers(String metadataPrefix) throws Exception {
		List<Header> listIdentifiers = new ArrayList<Header>();
		List<Header> tmpListIdentifiers = null;
		listIdentifiers = listIdentifiers("",null,null,null,metadataPrefix);

		if(hasIdentifierResumptionToken) {
			while(!this.identifiersResumptionToken.isEmpty()) 
			{
				tmpListIdentifiers = listIdentifiers(this.identifiersResumptionToken,null,null,null,metadataPrefix);

				for(Header h:tmpListIdentifiers) 
				{
					listIdentifiers.add(h);
				}
			}
		}
		return listIdentifiers;
	}

	/**
	 * listIdentifiers() auxiliar method.
	 * @param resumptionToken
	 * @return
	 */
	@SuppressWarnings("unchecked")
	private List<Header> listIdentifiers(String resumptionToken,String from,String until,String set, String metadataPrefix) throws Exception {
		// TODO: handle status attribute
		OMElement documentElement = null;
		List<Header> recordHeaderList = new ArrayList<Header>();

		if (metadataPrefix == null)
			metadataPrefix = "oai_dc";

		try {
			if(identifiersResumptionToken.isEmpty())
			{

				if(from == null && until == null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix);
				else if(from!=null && until == null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&from="+from);
				else if(from==null && until != null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&until="+from);
				else if(from==null && until == null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&set="+set);
				else if(from!=null && until != null && set == null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&from="+from+"&until="+until);
				else if(from!=null && until != null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&from="+from+"&until="+until+"&set="+set);
				else if(from==null && until != null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&until="+until+"&set="+set);
				else if(from!=null && until == null && set != null)
					documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix="+metadataPrefix+"&from="+from+"&set="+set);

			}
			else
			{
				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&resumptionToken="+resumptionToken);	
			}
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}

		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			Utils.sendException(getRecordError.next());
			return null;
		}


		Iterator<OMElement> getIdentifiers = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListIdentifiers"));
		getIdentifiers.hasNext();

		Iterator<OMElement> getHeaders = getIdentifiers.next().getChildElements();
		while(getHeaders.hasNext()) {
			OMElement head = getHeaders.next();
			Iterator<OMElement> getHeaderContent = head.getChildElements();
			Header header = new Header();
			if(head.getLocalName().equals("header")) {
				while(getHeaderContent.hasNext()) {
					OMElement headerContent = getHeaderContent.next();
					if(headerContent.getLocalName().equals("identifier")) {
						header.setIdentifier(headerContent.getText());
					}
					else if(headerContent.getLocalName().equals("datestamp")) {
						header.setDatestamp(headerContent.getText());
					}
					else if(headerContent.getLocalName().equals("setSpec")) {
						header.addSpec(headerContent.getText());
					}

				}
				this.identifiersResumptionToken = "";
			}
			else if(head.getLocalName().equals("resumptionToken")) {
				hasIdentifierResumptionToken = true;
				this.identifiersResumptionToken = head.getText();
				continue;
			}
			recordHeaderList.add(header);
		}
		return recordHeaderList;
	}

	/**
	 * Returns a List of {@link MetadataFormat}s supported by the repository.
	 * @return A {@link List} of MetadataFormats. 
	 * @throws Exception
	 */
	public List<MetadataFormat> listMetadataFormats() throws Exception {
		return listMetadataFormats("");
	}

	/**
	 * Returns a List of {@link MetadataFormat}s from a given identifier.
	 * @param identifier valid OAI document identifier.
	 * @return {@link List}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	public List<MetadataFormat> listMetadataFormats(String identifier) throws Exception {
		//TODO: descriptions needs to be handled too.

		OMElement documentElement = null;
		try {
			if(!identifier.isEmpty()) {
				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListMetadataFormats&identifier="+identifier);
			} 
			else {
				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListMetadataFormats");
			}
		}
		catch(Exception e) {	
			throw new Exception(e.getMessage());
		}

		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			Utils.sendException(getRecordError.next());
			return null;
		}

		List<MetadataFormat> metadataFormatList = new ArrayList<MetadataFormat>();

		Iterator<OMElement> listMetadataFormats = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListMetadataFormats"));		
		listMetadataFormats.hasNext();
		Iterator<OMElement> metadataFormats = listMetadataFormats.next().getChildElements();
		while(metadataFormats.hasNext()) {
			Iterator<OMElement> metaContent = metadataFormats.next().getChildElements();
			MetadataFormat metadataFormat = new MetadataFormat();
			while(metaContent.hasNext()) {
				OMElement contentElement = metaContent.next();
				if(contentElement.getLocalName().equals("metadataPrefix")) {
					metadataFormat.setMetadataPrefix(contentElement.getText());
				}
				else if(contentElement.getLocalName().equals("schema")) {
					metadataFormat.setSchema(contentElement.getText());
				}
				else if(contentElement.getLocalName().equals("metadataNamespace")) {
					metadataFormat.setMetadataNamespace(contentElement.getText());
				}
			}
			metadataFormatList.add(metadataFormat);
		}
		return metadataFormatList;
	}

	/**
	 * Returns a List of {@link Set} type.
	 * @return {@link List}
	 * @throws Exception
	 */
	public List<Set> listSets() throws Exception {
		List<Set> listSets = new ArrayList<Set>();
		List<Set> tmpSetList = null;
		listSets = listSets("");
		if(hasSetResumptionToken) {
			while(!this.setResumptionToken.isEmpty()) 
			{
				tmpSetList = listSets(this.setResumptionToken);
				for(Set s:tmpSetList) 
				{
					listSets.add(s);
				}
			}
		}
		return listSets;
	}

	/**
	 * Returns a List of {@link Set}s supported by the repository.
	 * @return {@link List}
	 * @throws Exception
	 */
	@SuppressWarnings("unchecked")
	private List<Set> listSets(String resumptionToken) throws Exception {
		// TODO: handle descriptions when getting set lists

		OMElement documentElement = null;
		List<Set> setList = new ArrayList<Set>();
		try {

			if(setResumptionToken.isEmpty())
			{
				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListSets");
			}
			else {
				documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListSets&resumptionToken="+resumptionToken);
			}
		}
		catch(Exception e) {
			throw new Exception("error!"+e.getMessage());
		}


		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			Utils.sendException(getRecordError.next());
			return null;
		}


		Iterator<OMElement> listSets = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListSets"));
		listSets.hasNext();
		Iterator<OMElement> sets = listSets.next().getChildElements();
		while(sets.hasNext()) {
			OMElement setEl = sets.next();
			Iterator<OMElement> setContent = setEl.getChildElements();
			Set set = new Set();

			if(setEl.getLocalName().equals("set")) {

				while(setContent.hasNext()) {
					OMElement contentElement = setContent.next();
					if(contentElement.getLocalName().equals("setSpec")) {
						set.setSetSpec(contentElement.getText());
					}
					else if(contentElement.getLocalName().equals("setName")) {
						set.setSetName(contentElement.getText());
					}
				}
				this.setResumptionToken = "";
			}
			else if(setEl.getLocalName().equals("resumptionToken")) {
				hasSetResumptionToken = true;
				this.setResumptionToken = setEl.getText();
				continue;
			}
			setList.add(set);
		}
		return setList;
	}

	/**
	 * 	Returns a Record object harvested by his identifier 
	 * 	using 'oai_dc' as metadataPrefix 
	 * @param identifier A OAI document identifier.
	 * @return	Record 
	 */
	@SuppressWarnings("unchecked")
	public Record getRecord(String identifier, String metadataPrefix) throws Exception {
		OMElement documentElement = null;

		if (metadataPrefix == null)
			metadataPrefix= "oai_dc";

		try {
			documentElement = Utils.getReaderFromHttpGet(baseUrl, "GetRecord&identifier="+identifier+"&metadataPrefix="+ metadataPrefix);	
		}
		catch(Exception e) {
			throw new Exception(e.getMessage());
		}



		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			Utils.sendException(getRecordError.next());
			return null;
		}

		Record nrecord = null;

		Iterator<OMElement> getRecord = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","GetRecord"));
		if (getRecord.hasNext()){
			Iterator<OMElement> record = getRecord.next().getChildElements();
			if (record.hasNext()){

				OMElement recordElement = record.next();
				nrecord = Utils.getRecord(recordElement);
				nrecord.setMetadataPrefix(metadataPrefix);
			}
		}

		return nrecord;
	}



	/**
	 * Selective harvest of {@link Record}s from a repository 
	 * @param from A date.
	 * @param until A date.
	 * @param set A set supported by the repository.
	 * @return {@link RecordIterator}
	 * @throws Exception
	 * 
	 */
	public RecordIterator listRecords(String from,String until,String set, String metadataPrefix) throws Exception {

		//		System.out.println("listRecords");
		RecordIterator rec = null;
		try{
			rec = new RecordIterator("", from, until, set, metadataPrefix, baseUrl);
		}catch (Exception e) {
			log.error("Error listing records from " + baseUrl , e);
		}
		return rec;

	}


	/**
	 * Harvest all the {@link Record}s from a repository
	 * @return {@link RecordIterator}
	 * @throws FileNotFoundException
	 * @throws Exception
	 * 
	 */
	public RecordIterator listRecords(String metadataPrefix) throws FileNotFoundException, Exception {
		//		System.out.println("listRecords");
		RecordIterator rec = null;
		try{
			rec = new RecordIterator("",null,null,null, metadataPrefix, baseUrl);
		}catch (Exception e) {
			log.error("Error listing records from " + baseUrl, e);
		}
		return rec;
	}


	/**
	 * Harvest all the {@link Record}s from a repository
	 * @return {@link RecordIterator}
	 * @throws FileNotFoundException
	 * @throws Exception
	 * 
	 */
	public RecordIterator listRecords(String metadataPrefix, String resumptionToken) throws FileNotFoundException, Exception {
		//		System.out.println("listRecords");
		RecordIterator rec = null;
		try{
			rec = new RecordIterator(resumptionToken,null,null,null, metadataPrefix, baseUrl);
		}catch (Exception e) {
			log.error("Error listing records from " + baseUrl, e);
		}
		return rec;
	}

	@SuppressWarnings("unchecked")
	public ResumptionToken getResumptionToken() throws Exception{
		ResumptionToken resumptionToken = new ResumptionToken();
		OMElement documentElement = null;
		try {
			documentElement = Utils.getReaderFromHttpGet(baseUrl, "ListIdentifiers&metadataPrefix=oai_dc");
		}
		catch(Exception e) {
			throw new Exception("error!"+e.getMessage());
		}

		Iterator<OMElement> getRecordError = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","error"));
		if(getRecordError.hasNext()) {
			Utils.sendException(getRecordError.next());

		}


		Iterator<OMElement> listSets = documentElement.getChildrenWithName(new QName("http://www.openarchives.org/OAI/2.0/","ListIdentifiers"));
		listSets.hasNext();
		Iterator<OMElement> resup = listSets.next().getChildElements();
		while(resup.hasNext()) {
			OMElement resuptionElement = resup.next();

			if(resuptionElement.getLocalName().equals("resumptionToken")) {
				Iterator<OMAttribute> resumptionContent = resuptionElement.getAllAttributes();
				while(resumptionContent.hasNext()) {
					OMAttribute contentElement = resumptionContent.next();
					if(contentElement.getLocalName().equals("completeListSize")) {
						resumptionToken.setCompleteListSize(contentElement.getAttributeValue());
					}
					else if(contentElement.getLocalName().equals("cursor")) {
						resumptionToken.setCursor(contentElement.getAttributeValue());
					}
					else if(contentElement.getLocalName().equals("expirationDate")) {
						resumptionToken.setExpirationDate(contentElement.getAttributeValue());
					}
				}
			}

		}
		return resumptionToken;
	}





}
