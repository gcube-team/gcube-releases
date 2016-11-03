package org.gcube.application.framework.search.library.impl;

import gr.uoa.di.madgik.grs.buffer.GRS2BufferException;
import gr.uoa.di.madgik.grs.buffer.IBuffer.Status;
import gr.uoa.di.madgik.grs.events.BufferEvent;
import gr.uoa.di.madgik.grs.events.KeyValueEvent;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderException;
import gr.uoa.di.madgik.grs.reader.GRS2ReaderInvalidArgumentException;
import gr.uoa.di.madgik.grs.reader.IRecordReader;
import gr.uoa.di.madgik.grs.reader.RandomReader;
import gr.uoa.di.madgik.grs.reader.decorators.RecordReaderDelegate;
import gr.uoa.di.madgik.grs.reader.decorators.keepalive.KeepAliveReader;
import gr.uoa.di.madgik.grs.record.GRS2RecordDefinitionException;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.field.Field;
import gr.uoa.di.madgik.grs.record.field.StringField;
import gr.uoa.di.madgik.rr.ResourceRegistryException;
import gr.uoa.di.madgik.rr.element.query.QueryHelper;

import java.io.UnsupportedEncodingException;
import java.net.IDN;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.gcube.application.framework.contentmanagement.content.impl.DigitalObject;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.util.SessionConstants;
import org.gcube.application.framework.search.library.exception.InitialBridgingNotCompleteException;
import org.gcube.application.framework.search.library.exception.InternalErrorException;
import org.gcube.application.framework.search.library.exception.gRS2AvailableRecordsRetrievalException;
import org.gcube.application.framework.search.library.exception.gRS2BufferException;
import org.gcube.application.framework.search.library.exception.gRS2CreationException;
import org.gcube.application.framework.search.library.exception.gRS2NoRecordReadWithinTimeIntervalException;
import org.gcube.application.framework.search.library.exception.gRS2ReaderException;
import org.gcube.application.framework.search.library.exception.gRS2RecordDefinitionException;
import org.gcube.application.framework.search.library.interfaces.ResultSetConsumerI;
import org.gcube.application.framework.search.library.util.DisableButtons;
import org.gcube.application.framework.search.library.util.SearchConstants;
import org.gcube.application.framework.search.library.util.SearchType;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.lang.StringEscapeUtils;

public class ResultSetConsumer implements ResultSetConsumerI{

	/** The logger. */
	private static final Logger logger = LoggerFactory.getLogger(ResultSetConsumer.class);

	IRecordReader<GenericRecord> reader;
	ListIterator<GenericRecord> iter;
	int pageNo = 1;
	int pageTotal = 0;
	int lastRequestedEnd = 0;
	int lastRequestedStart = 0;
	boolean out_of_end = false;
	boolean readTotal = false;
	ArrayList<String> selectedCollections;
	String rsLocator;
	String searchType;
	String genericSearchType;
	int numOfResultsRead = 0;
	boolean getTotalRead = false;
	HashMap<String, String> fieldsIDsNames = new HashMap<String, String>();
	boolean onlyPresentationFields = false;
	boolean onlyTitleSnippet;
	
	long searchStartTime; //should be provided upon the initiation

	protected static AtomicInteger SMid = new AtomicInteger();

	ResultSetConsumer() {
		reader = null;
		pageNo = 1;
		pageTotal = 0;
		lastRequestedEnd = 0;
		lastRequestedStart = 0;
		out_of_end = false;
		readTotal = false;
		selectedCollections = new ArrayList<String>();
		searchType = "";
		onlyTitleSnippet = false;
	}
/*
	private void printRSCState() throws GRS2ReaderException{
		logger.debug("Current timestamp: "+ System.currentTimeMillis());
		logger.debug("Printing result set consumer state");
		logger.debug("Page number: "+ this.pageNo);
		logger.debug("TotalPages: "+ this.pageTotal);
		logger.debug("Last requested end: "+lastRequestedEnd);
		logger.debug("Last requested start: "+lastRequestedStart);
		logger.debug("out_of_end ? -> "+this.out_of_end);
		logger.debug("readTotal ? -> "+this.readTotal);
		logger.debug("Result Set Locator: "+this.rsLocator);
		logger.debug("SearchType: "+ this.searchType);
		logger.debug("genericSearchType: "+ this.genericSearchType);
		logger.debug("numOfResultsRead: "+this.numOfResultsRead);
		logger.debug("Selected Collections: ");
		for(String collection : selectedCollections)
			logger.debug(collection);
		logger.debug("Printing Result Set Consumer Reader state");
		if (reader!=null){
			logger.debug("Reader's current record: "+reader.currentRecord());
			logger.debug("Reader's inactivity timeout: "+reader.getInactivityTimeout());
			logger.debug("Reader's iterator timeout: "+reader.getIteratorTimeout());
			logger.debug("Reader's capacity (buffer): " +	reader.getCapacity());
			logger.debug("Reader's status.toString(): "+ reader.getStatus().toString());
			logger.debug("Reader has red " +reader.totalRecords() +" so far.");
		}
		else
			logger.debug("Reader is null !!!");
		
	}
*/
	
	//old search , should be abandoned !
	public ResultSetConsumer(String RSLocator, String searchType, boolean onlyTitleSnippet) throws URISyntaxException, gRS2CreationException {
		this.searchType = searchType;
		rsLocator = RSLocator;
		this.onlyTitleSnippet = onlyTitleSnippet;
		
		URI locator = new URI(RSLocator);
		try {
			long startTime = System.currentTimeMillis();
			reader = new RandomReader<GenericRecord>(locator);
			reader = new KeepAliveReader<GenericRecord>(reader,20, TimeUnit.SECONDS, 60, TimeUnit.MINUTES);
			reader.setIteratorTimeout(2);
			reader.setIteratorTimeUnit(TimeUnit.MINUTES);
			iter = (ListIterator<GenericRecord>) reader.iterator();
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			logger.debug("Portal Benchmarking - Time to get the RandomReader from ResultSet: " + diff);
		} catch (GRS2ReaderException e) {
			throw new gRS2CreationException(e);
		} catch (GRS2ReaderInvalidArgumentException e) {
			throw new gRS2CreationException(e);
		}
	}
	
	public void setSearchStartTime(long timeMillis){
		searchStartTime = timeMillis;
	}
	
	public ResultSetConsumer(Stream <GenericRecord> recordsStream, String searchType, boolean onlyTitleSnippet) throws gRS2CreationException  {
		this.searchType = searchType;
		this.onlyTitleSnippet = onlyTitleSnippet;
		try {
			long startTime = System.currentTimeMillis();
			reader = new RandomReader<GenericRecord>(recordsStream.locator());
			reader = new KeepAliveReader<GenericRecord>(reader,5, TimeUnit.SECONDS, 60, TimeUnit.MINUTES);
			reader.setIteratorTimeout(2);
			reader.setIteratorTimeUnit(TimeUnit.MINUTES);
			iter = (ListIterator<GenericRecord>) reader.iterator();
			long endTime = System.currentTimeMillis();
			long diff = endTime - startTime;
			logger.debug("Portal Benchmarking - Time to get the RandomReader from ResultSet: " + diff);
		} catch (GRS2ReaderInvalidArgumentException e) {
			throw new gRS2CreationException(e);
		} catch (GRS2ReaderException e) {
			logger.debug("Could not read the results stream");
		} catch(NullPointerException e){
			throw new gRS2CreationException("Could not create the ResultSetConsumer. Stream locator was null",e);
		}
		
	}
	


	/**
	 * @param session the D4Science session to be used in order to remove the attributes
	 */
	public static void removeSessionVariables(ASLSession session) {
		logger.info("Removing session variables");
		session.removeAttribute(SessionConstants.page_no);
		session.removeAttribute(SessionConstants.page_total);
		session.removeAttribute(SessionConstants.lastRes);
		session.removeAttribute(SessionConstants.isLast);
		session.removeAttribute(SessionConstants.out_of_end);
		session.removeAttribute(SessionConstants.rsClient);
		session.removeAttribute(SessionConstants.theResultObjects);
		session.removeAttribute(SessionConstants.theThumbnails);
		session.removeAttribute(SessionConstants.startingPoint);
		session.removeAttribute(SessionConstants.sourcePortlet); 
		session.removeAttribute(SessionConstants.rsEPR);
		session.removeAttribute(SessionConstants.showRank);
		session.removeAttribute(SessionConstants.searchException);
		session.removeAttribute("QeuryIndexToPresent");
		session.removeAttribute("selectedCriteriaNames");
	}

	/**
	 * only to be used in special cases. use getFirst() instead.
	 * @throws GRS2BufferException 
	 * @throws GRS2RecordDefinitionException 
	 */
	public List<Properties> getFirstRaw(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, GRS2RecordDefinitionException, GRS2BufferException {
		logger.debug("Get First raw results!");
		dis.setBack(true);
		// Read first n results
		List<GenericRecord> records;
		List<Properties> results = new ArrayList<Properties>();
		try {
			records = readRS(n, dis, false);
			results = transformToProperties(session, records);
		} catch (GRS2ReaderException e1) {
			throw new gRS2ReaderException(e1);
		}
		try {
			if (reader.availableRecords() == 0)
				dis.setForward(true);
		} catch (GRS2ReaderException e1) {
			if (reader.getStatus() != Status.Open)
				throw new gRS2AvailableRecordsRetrievalException(e1);
			logger.info("Reader is not open, cannot retrieve the first set of results");
		}
		return results;
	}


	public List<DigitalObject> getFirst(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException {

		logger.debug("Get First results!");
		long startTime = System.currentTimeMillis();
		dis.setBack(true);

		// Read first n results
		List<GenericRecord> results;
		try {
			long startTimeReadRs = System.currentTimeMillis();
			logger.info("Time_Counter -- Started reading " + n + " records (first page) from results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
			results = readRS(n, dis, false);
			logger.info("Time_Counter -- Finished reading " + n + " records (first page) from results stream" + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
			long endTimeReadRs = System.currentTimeMillis();
			long diffReadRs = endTimeReadRs - startTimeReadRs;
			logger.debug("Portal Benchmarking - Reading first Results Time: " + diffReadRs);
		} catch (GRS2ReaderException e1) {
			throw new gRS2ReaderException(e1);
		}
		try {
			if (reader.availableRecords() == 0)
				dis.setForward(true);
		} catch (GRS2ReaderException e1) {
			if (reader.getStatus() != Status.Open)
				throw new gRS2AvailableRecordsRetrievalException(e1);
			logger.info("Reader is not open, cannot retrieve the first set of results");
		}

		List<DigitalObject> resultsList = null;
		
		logger.info("Time_Counter -- Started transforming to HTML the records (of first page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		try {
			resultsList = transformToHTML(session, results);
		} catch (GRS2RecordDefinitionException e) {
			throw new gRS2RecordDefinitionException(e);
		} catch (GRS2BufferException e) {
			logger.error("Exception while consuming the resultset", e);
		}
		logger.info("Time_Counter -- Finished transforming to HTML the records (of first page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		long endTime = System.currentTimeMillis();
		long diff = endTime - startTime;
		logger.debug("Portal Benchmarking - First page available: " + diff);
		if (results != null && results.size() != 0) {
			try {
				BufferEvent ev = reader.receive();
				while (ev != null) {
					KeyValueEvent kvev = (KeyValueEvent) ev;
					if (kvev != null) {
						String key = kvev.getKey();
						if (key.equals("resultsNumber")) {
							numOfResultsRead = Integer.parseInt(kvev.getValue());
							break;
						}
						else if (key.equals("resultsNumberFinal")) {
							numOfResultsRead = Integer.parseInt(kvev.getValue());
							getTotalRead = true;
							break;
						}
					}
					ev = reader.receive();
				}
			} catch (GRS2ReaderException e) {
				logger.error("Exception while consuming the resultset", e);
			}
		} else {
			numOfResultsRead = 0;
			getTotalRead = true;
		}
		return resultsList;

	}
	
	/**
	 * only to be used in special cases. use getNext() instead.
	 * @throws gRS2NoRecordReadWithinTimeIntervalException 
	 * @throws GRS2BufferException 
	 * @throws GRS2RecordDefinitionException 
	 */
	public List<Properties> getNextRaw(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, GRS2RecordDefinitionException, GRS2BufferException {
		logger.debug("Get Next results!");
		dis.setBack(false);
		List<GenericRecord> records;
		List<Properties> results = new ArrayList<Properties>();
		try {
			records = readRS(n, dis, false);
			results = transformToProperties(session, records);			
		} catch (GRS2ReaderException e1) {
			logger.error("Results coming from resultset will be of zero size. Exception:", e1);
		}
		return results;
	}

	public List<DigitalObject> getNext(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException {

		logger.debug("Get Next results!");
		dis.setBack(false);

		// Read first n results
		List<GenericRecord> results;
		logger.info("Time_Counter -- Started reading " + n + " records (next page) from results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		try {
			results = readRS(n, dis, false);
		} catch (GRS2ReaderException e1) {
			throw new gRS2ReaderException(e1);
		}
		logger.info("Time_Counter -- Finished reading " + n + " records (next page) from results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		List<DigitalObject> resultsList = null;
		logger.info("Time_Counter -- Started transforming to HTML the records (of a next page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		try {
			resultsList = transformToHTML(session, results);
		} catch (GRS2RecordDefinitionException e) {
			throw new gRS2RecordDefinitionException(e);
		} catch (GRS2BufferException e) {
			logger.error("Exception:", e);
		}
		logger.info("Time_Counter -- Finished transforming to HTML the records (of a next page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		try {
			BufferEvent ev = reader.receive();
			while (ev != null) {
				KeyValueEvent kvev = (KeyValueEvent) ev;
				if (kvev != null) {
					String key = kvev.getKey();
					if (key.equals("resultsNumber")) {
						numOfResultsRead = Integer.parseInt(kvev.getValue());
						break;
					}
					else if (key.equals("resultsNumberFinal")) {
						numOfResultsRead = Integer.parseInt(kvev.getValue());
						getTotalRead = true;
						break;
					}
				}
				ev = reader.receive();
			}
		} catch (GRS2ReaderException e) {
			logger.error("Exception:", e);
		}
		return resultsList;

	}

	public List<DigitalObject> getPrevious(int n, DisableButtons dis, ASLSession session) throws gRS2NoRecordReadWithinTimeIntervalException, gRS2RecordDefinitionException, gRS2ReaderException, gRS2AvailableRecordsRetrievalException, InitialBridgingNotCompleteException, InternalErrorException {

		logger.debug("Get previous results.");
		List<GenericRecord> results;
		dis.setForward(false);
		
		logger.info("Time_Counter -- Started reading " + n + " records (previous page) from results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		try {
			results = readRS(n, dis, true);
		} catch (GRS2ReaderException e1) {
			throw new gRS2ReaderException(e1);
		}
		logger.info("Time_Counter -- Finished reading " + n + " records (previous page) from results stream " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		
		List<DigitalObject> resultsList = null;
		logger.info("Time_Counter -- Started transforming to HTML the records (of previous page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		try {
			resultsList = transformToHTML(session, results);
		} catch (GRS2RecordDefinitionException e) {
			throw new gRS2RecordDefinitionException(e);
		} catch (GRS2BufferException e) {
			logger.error("Exception:", e);
		}
		logger.info("Time_Counter -- Finished transforming to HTML the records (of previous page) " + (System.currentTimeMillis() - searchStartTime) / 1000.0 + " sec(s)");
		return resultsList;

	}
	
	private List<Properties> transformToProperties(ASLSession session, List<GenericRecord> results) throws GRS2RecordDefinitionException, GRS2BufferException{
		
		List<Properties> resultsList = new ArrayList<Properties>();
		HashMap<String, String> idsNames = new HashMap<String, String>();
		
		
		for (int i = 0; i < results.size(); i++) {
			
			SearchHelper sh = new SearchHelper(session);
			logger.debug("This record has length of: " + results.get(i).getFields().length);
			
			ArrayList<String> presentationIds = (ArrayList<String>)session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.presentableFields);
			logger.debug("Presentation fields (IDs) are: "+ presentationIds);
			Properties nameValues = new Properties();
			
			if (presentationIds != null && presentationIds.size() != 0) {
				for (int j = 0; j < presentationIds.size(); j++) {
					Field fld = results.get(i).getField(presentationIds.get(j));
					StringField stringField = (StringField) fld;
					
					if (fld != null) {		// there might be a case where we have asked for a field that cannot be presented
						String name = idsNames.get(fld.getFieldDefinition().getName());
						logger.debug("name: " +name);
						if (name == null || name.equals("")) {
							try {
								name = QueryHelper.GetFieldNameById(fld.getFieldDefinition().getName());
							} catch (ResourceRegistryException e) {
								logger.error("Error while retrieving field name", e);
							}
							idsNames.put(fld.getFieldDefinition().getName(), name);
						}
						nameValues.put(name, stringField.getPayload());
					}
				}
				//add also the object id
				StringField docURI = (StringField)results.get(i).getField("ObjectID");
				String docID;
				if (docURI != null) 
					docID = new DigitalObject(session, docURI.getPayload()).getObjectId();
				else 
					docID = new DigitalObject(session, "", "").getObjectId();
				nameValues.put("_OBJECT_ID_", docID);
				resultsList.add(nameValues);
			}
			
		}

		//session.removeAttribute("presentationFields");
		logger.info("---------------------------The number of results returned are: " + resultsList.size());

		return resultsList;
		
	}
	
	private List<DigitalObject> transformToHTML(ASLSession session, List<GenericRecord> results) throws GRS2RecordDefinitionException, GRS2BufferException, InitialBridgingNotCompleteException, InternalErrorException {
		List<DigitalObject> resultsList = new ArrayList<DigitalObject>();
		HashMap<String, String> idsNames = new HashMap<String, String>();
		long startTime = System.currentTimeMillis();
		logger.debug("Inside transform to HTML");
		logger.debug("FROM QUERY TYPE: "+searchType);
		
		logger.debug("Results.size(): "+results.size());
		logger.debug("onlyTitleSnippet: "+onlyTitleSnippet);
		
		for (int i = 0; i < results.size(); i++) {
			// Get the fields we need
			StringField docURI = (StringField)results.get(i).getField("ObjectID");;
			Field RankID = results.get(i).getField("rank");
			StringField rankId = (StringField)RankID;
			Field CollID = results.get(i).getField("gDocCollectionID");
			StringField colId = null;
			if (CollID != null)
				colId = (StringField)CollID;

			SearchHelper sh = new SearchHelper(session);

			DigitalObject resRec = null;
			if (colId == null) {
				logger.debug("the collection ID is null");
				if (docURI != null) {
					logger.debug("Creating digital object - the gDocCollection id is NULL: " + docURI.getPayload());
					resRec = new DigitalObject(session, docURI.getPayload());
				} else {
					logger.debug("The docId is null - probably browse distinct");
					resRec = new DigitalObject(session, "", "");
				}
				// set the collection info
//				String cid = resRec.getCollectionID();
//				logger.debug("cid: " + cid);
//				CollectionInfo colI = sh.findCollectionInfo(cid);
//				String colName;
//				if (colI == null)
//					colName = FindFieldsInfo.findCollectionName(cid, session.getScopeName());
//				else
//					colName = colI.getName();
//				resRec.setCollectionName(colName);
			}
			else {
				if (docURI != null) {
					logger.debug("Creating digital object - the gDocCollection id is NOT null: " + docURI.getPayload());
					resRec = new DigitalObject(session, docURI.getPayload(), colId.getPayload());
				} else {
					resRec = new DigitalObject(session, "", colId.getPayload());
				}
				//CollectionInfo colI = sh.findCollectionInfo(colId.getPayload());
				//resRec.setCollectionName(colI.getName());
			}
			
			logger.debug("colId: "+colId);
			
			
			logger.debug("This record has length of: " + results.get(i).getFields().length);
			
			//CollectionInfo colInfo = sh.findCollectionInfo(resRec.getCollectionID());
			//resRec.setCollectionID(colId.getPayload());
			//resRec.setCollectionName(colInfo.getName());

			//if (rankId != null)
			//	resRec.setRank(rankId.getPayload());Record's HTML representation --> 

			// TODO:
			// get all the fields except from the ones that will not be shown, apply the xslt to transform to html
			// set to html representation
			// add to resultsList
			StringBuffer htmlRepresentation = new StringBuffer();
			String queriedByID = sh.getActiveQueryGroup().getQuery(0).getBrowseByField(); //this is the fieldID, not the name !
			
			logger.debug("searchType: "+ searchType);
			logger.debug("queriedByID: "+ queriedByID);
			
			//if it's generic search, it has either the following session attribute false or true. it's always set, never null
			
			logger.debug("Display detailed result? --> "+ session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.sessionDetailedResult));			
			//we change it in order to get detailed results only when it's set to true or if it's browse. Otherwise (null or false), we print only the snippet thing
			if ( (session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.sessionDetailedResult) != null &&
						(Boolean) (session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.sessionDetailedResult)))
						||  searchType.equals(SearchType.BrowseFields) || searchType.equals(SearchType.Browse) 
						) 
			{
				
				ArrayList<String> presentationIds = (ArrayList<String>)session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.presentableFields);
				logger.debug("Presentation fields (IDs) are: "+ presentationIds);
				
				if (presentationIds != null && presentationIds.size() != 0) {
					for (int j = 0; j < presentationIds.size(); j++) {
						Field fld = results.get(i).getField(presentationIds.get(j));
						StringField stringField = (StringField) fld;
						
						logger.debug("stringField: " +stringField);
						
						if (fld != null) {		// there might be a case where we have asked for a field that cannot be presented
							String name = idsNames.get(fld.getFieldDefinition().getName());
							
							logger.debug("name: " +name);
							
							if (name == null || name.equals("")) {
								try {
									name = QueryHelper.GetFieldNameById(fld.getFieldDefinition().getName());
								} catch (ResourceRegistryException e) {
									logger.error("Error while retrieving field name", e);
								}
								idsNames.put(fld.getFieldDefinition().getName(), name);
							}

							if (name.trim().equalsIgnoreCase("S")) {
								/* Only for the Snippet field. */
								name = SearchConstants.SNIPPET;
								stringField.setPayload(handleSnippetValue(stringField.getPayload()));
							}		

							//if it's not a browse field, put {name,value} within html, else put only {value} without any html
							if(!searchType.equals(SearchType.BrowseFields))
								htmlRepresentation.append("<p><b>" + name + ":</b> ");

							if (stringField.getPayload() != null && stringField.getPayload().length() > 370) {
								String smallDescription = stringField.getPayload().substring(0, 369);
								if(presentationIds.get(j).equals(queriedByID))
									htmlRepresentation.append(smallDescription);// + "..." + "</p>";
							}
							else
							{
								/* Add the field to the html representation, only if it has a non-empty payload. */
								if (stringField.getPayload() != null && !stringField.getPayload().equals("")) {
									logger.info("Description was "+ stringField.getPayload());
									htmlRepresentation.append(stringField.getPayload());// + "</p>";
								}
							}
							
							//if it's not a browse field, put {name,value} within html, else put only {value} without any html
							if(!searchType.equals(SearchType.BrowseFields))
								htmlRepresentation.append("</p>");
							
							if (resRec.getTitle() == null || resRec.getTitle().equals("")) {
								if (stringField != null && stringField.getPayload() != null) {
									if (stringField.getPayload().length() > 40)
										resRec.setTitle(name + ": " + stringField.getPayload().substring(0,40));
									else
										resRec.setTitle(name + ": " + stringField.getPayload());
								}
							}	
						}
					}
					logger.debug("Record's HTML representation --> " + htmlRepresentation.toString());
					resRec.setHTMLRepresentation(htmlRepresentation.toString());
					resultsList.add(resRec);
				} else {
					// quick search - get all presentation fields
					Field[] allFields = results.get(i).getFields();
					for (int k = 0; k < allFields.length; k++) {
						try {
							logger.debug("Looking for name of the field: " + allFields[k].getFieldDefinition().getName());
							String name = QueryHelper.GetFieldNameById(allFields[k].getFieldDefinition().getName());
							logger.debug("The name is: " + name);
							if (name != null && !name.equals("ObjectID") && !name.equals("rank") && !name.equals("gDocCollectionID")) {
								StringField stringField = (StringField) allFields[k];
								htmlRepresentation.append("<p><b>" + name +  ":</b> " + stringField.getPayload() + "</p>");
								if (resRec.getTitle() == null || resRec.getTitle().equals("")) {
									if (stringField.getPayload().length() > 40)
										resRec.setTitle(name + ": " + stringField.getPayload().substring(0,40));
									else
										resRec.setTitle(name + ": " + stringField.getPayload());
								}
							} 
						} catch (ResourceRegistryException e) {
							logger.error("Error while retrieving field name", e);
						}
					}
					//resRec.setTitle(htmlRepresentation.substring(0,20));
					resRec.setHTMLRepresentation(htmlRepresentation.toString());
					resultsList.add(resRec);
				}
			}
			else { // simple and generic search case
				
				logger.debug("simple and advanced search case");
				
				Field titleField = results.get(i).getField((String)session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.SESSION_TITLE_ATTR));
				Field snippetField = results.get(i).getField((String)session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.SESSION_SNIPPET_ATTR));				
				
				logger.debug("titleField: "+titleField);
				logger.debug("snippetField: "+snippetField);
				
				
				//this case is when the query is resubmitted from the basket/workspace save list and the session does not contain the id of the 2 fields
				//in this case, show all fields returned
				if((titleField==null)&&(snippetField==null)){ 
					HashMap<String,String> name_value = new HashMap<String,String>(); //store here temporarily the (field name,field value) pairs, to construct the htmlRepresentation at the end
					for(Field f : results.get(i).getFields()){
						String name = idsNames.get(((StringField)f).getFieldDefinition().getName()); 
						if(name == null){
							logger.debug("No pre-stored name for field id: "+((StringField)f).getFieldDefinition().getName()+ " , asking registry.");
							try{
								name = QueryHelper.GetFieldNameById(((StringField)f).getFieldDefinition().getName());
								idsNames.put(((StringField)f).getFieldDefinition().getName(), name);
							}catch (ResourceRegistryException e) {
								logger.error("Error while retrieving field name for id: "+((StringField)f).getFieldDefinition().getName()+" from registry", e);
							}
						}
						if(name == null) // this case is for fields which should not be shown (i.e. system fields)
							continue;
						if("ObjectID".equalsIgnoreCase(name) || "rank".equalsIgnoreCase(name) || "gDocCollectionID".equalsIgnoreCase(name))
							continue; //skip those fields too
						if("title".equalsIgnoreCase(name)){
							resRec.setTitle(((StringField)f).getPayload());
							name_value.put("title", ((StringField)f).getPayload());
						}
						else if("S".equalsIgnoreCase(name))
							name_value.put("S", ((StringField)f).getPayload());
						else
							name_value.put(name, ((StringField)f).getPayload());
					}
					//special treatment for title and snippet...
					String tval = name_value.remove("title");
					if(tval!=null)
						htmlRepresentation.append("<p><h5 style=\"color: darkblue;\">" + tval + "</h5></p>");
					String sval = name_value.remove("S");
					if(sval!=null)
						htmlRepresentation.append("<p style=\"color:#333333;  word-wrap: break-word;\">" + handleSnippetValue(sval) + "</p>");
					//...and normal treatment for the remaining values
					for(String remName : name_value.keySet())
						htmlRepresentation.append("<p style=\"color: darkblue;\"><b>" + remName +  ":</b> " + name_value.get(remName) + "</p>");
					
				}
				else{
					if (titleField != null) {
						StringField stringField = (StringField) titleField;
						String titlePayload = stringField.getPayload();
						
						logger.debug("Title field payload -> " + titlePayload);
						
						resRec.setTitle(titlePayload);
						htmlRepresentation.append("<p><h5 style=\"color: darkblue;\">" + titlePayload + "</h5></p>");
					}
					if (snippetField != null) {
						StringField stringField = (StringField) snippetField;
						String snippetPayload = handleSnippetValue(stringField.getPayload());
						
						logger.debug("Snippet field payload -> " + snippetPayload);
						
						htmlRepresentation.append("<p style=\"color:#333333;  word-wrap: break-word;\">" + snippetPayload + "</p>");
					}
				}
				logger.debug("Record's HTML representation --> " + htmlRepresentation.toString());
				resRec.setHTMLRepresentation(htmlRepresentation.toString());
				resultsList.add(resRec);
			}
		}

		long endTime = System.currentTimeMillis();
		long diff = endTime - startTime;
		logger.debug("Portal Benchmarking - Records Transformation for Presentation (for whole page): " + diff);
		//session.removeAttribute("presentationFields");
		logger.info("---------------------------The number of results returned are: " + resultsList.size());

		return resultsList;
	}

	
	private String handleSnippetValue(String snippetValue) {
		String newValue;
		logger.debug("Handling the snippet value came from search --> " + snippetValue);
		/* Replace all encoded characters and set the new string to be the new payload. */
		newValue = snippetValue.replaceAll("&gt;", ">").replaceAll("&lt;", "<");
		
		String currentPayload = newValue;
		if(currentPayload.length() > 370) {
			/* Determine the position of the last '<' character (if exists) and get the substring
			 * until that character. In this way, we make sure that we don't leave any tag unclosed. */
			int index;
			for(index = 362; index < 365; index++) {
				if(currentPayload.charAt(index) == '<')
					break;
			}

			String newPayload = currentPayload.substring(0, index);

			/* Append the bold close tag, in order to avoid presenting all following fields
			 * in bold. */
			newPayload += "</b>";

			newValue = newPayload;
		}
		return newValue;
	}

	public List<String> getResultsToText (int n, int offset, ASLSession session) throws gRS2ReaderException, gRS2RecordDefinitionException, gRS2BufferException {
		int currentPlace;
		try {
			currentPlace = (int)reader.currentRecord(); //(int)reader.totalRecords() - reader.availableRecords();
		} catch (GRS2ReaderException e) {
			logger.error("Error while getting current place in ResultSet", e);
			throw new gRS2ReaderException(e);
		}
		logger.debug("Current Place of ResultSet: " + currentPlace + " and offset is: " + offset);
		logger.info("Current Place of ResultSet: " + currentPlace + " and offset is: " + offset);
		if (offset < 0) {
			return new ArrayList<String>();
		}
		if (offset > currentPlace) {

			int diff = offset - currentPlace;
			logger.debug("Seeking to: " + diff);
			logger.info("Seeking to: " + diff);
			try {
				reader.seek(diff);
			} catch (GRS2ReaderException e) {
				logger.error("Error while seeking resultSet.", e);
				logger.info("Error while seeking resultSet.");
				throw new gRS2ReaderException(e);
			}
		} else {
			int diff = currentPlace - offset;
			logger.debug("Seeking back: " + diff);
			logger.info("Seeking back: " + diff);
			try {
				if (diff==0) //in order to avoid reader.seek(-0) which leads to exception !
					reader.seek(diff); 
				else 
					reader.seek(-diff);
			} catch (GRS2ReaderException e) {
				logger.error("Error while seeking resultSet.", e);
				logger.info("Error while seeking resultSet.");
				throw new gRS2ReaderException(e);
			}
		}

		List<String> recs = new ArrayList<String>();
		try {
			recs = read(n, session);
		} catch (GRS2RecordDefinitionException e) {
			logger.error("Error while reading resultSet.", e);
			throw new gRS2RecordDefinitionException(e);
		} catch (GRS2ReaderException e) {
			logger.error("Error while reading resultSet.", e);
			throw new gRS2ReaderException(e);
		} catch (GRS2BufferException e) {
			logger.error("Error while reading resultSet.", e);
			throw new gRS2BufferException(e);
		} catch (ResourceRegistryException e) {
			logger.error("Error while reading field names from registry.", e);
			throw new gRS2BufferException(e);
		}
		return recs;
	}


	/**
	 * Moves the resultset by the number of records specified
	 * If negative, moves backwards
	 * 
	 * @param numofresults
	 * @return
	 */
	public boolean advanceReaderBy(int numOfResults){
		try {
			reader.seek(numOfResults);
			return true;
		} catch (GRS2ReaderException e) {
			logger.debug("Could not advance result set to point "+numOfResults + "Details: "+e);
			return false;
		}
	}
	
	
	public void setOnlyPresentables() {
		this.onlyPresentationFields = true;
	}

	private List<String> read(int count, ASLSession session) throws GRS2ReaderException, GRS2RecordDefinitionException, GRS2BufferException, ResourceRegistryException
	{
		if (!onlyPresentationFields) {
			List<String> records = new ArrayList<String>();

			for(int i=0;i<count;i+=1)
			{
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) {

					break;
				}
				
				GenericRecord rec=reader.get(5, TimeUnit.MINUTES);

				if(rec==null) 
					break;

				String recString = "<RSRecord>";
				Field[] fields = rec.getFields();
				for (int j = 0; j < fields.length; j++) {
					StringField stringField = (StringField)fields[j];
					
					//TODO: correct this...
					String key = null;
					try {
						key = stringField.getFieldDefinition().getName();
					} catch (GRS2RecordDefinitionException e) {
						logger.error("Error while trying to get field : " + j + " from record : " + i + ". Total records : " + count + ", total fields/rec : " +  fields.length);
						throw e;
					} 
					
					String stringValue = stringField.getPayload();

//					//-- Convert value to UTF-8
//					String roundTrip = null;
//					try {
//						if (stringValue != null) {
//							byte[] utf8Bytes = stringValue.getBytes("UTF8");
//							roundTrip = new String(utf8Bytes, "UTF8");
//						}
//					} catch (UnsupportedEncodingException e) {
//						logger.error("Exception:", e);
//					}

					
					
					String name = null;//fieldsIDsNames.get(key);
					if (!fieldsIDsNames.containsKey(key)) {
						name = QueryHelper.GetFieldNameById(key);
						fieldsIDsNames.put(key, name);
					}

					else
						name = fieldsIDsNames.get(key);

					
//					if (roundTrip == null)
//						recString += "<field><fieldId>" + key + "</fieldId><fieldValue>" + StringEscapeUtils.escapeXml(stringValue) + "</fieldValue><fieldName>" + StringEscapeUtils.escapeXml(name) + "</fieldName></field>";
//					else
//						recString += "<field><fieldId>" + key + "</fieldId><fieldValue>" + StringEscapeUtils.escapeXml(roundTrip) + "</fieldValue><fieldName>" + StringEscapeUtils.escapeXml(name) + "</fieldName></field>";
					
					
					if(key.equalsIgnoreCase("gDocCollectionID")&&((name==null)||name.equalsIgnoreCase("null")))
						name = key;
					if(key.equalsIgnoreCase("ObjectID")&&((name==null)||name.equalsIgnoreCase("null")))
						name = key;
					
					
					recString += "<field><fieldId>" + key + "</fieldId><fieldValue>" + StringEscapeUtils.escapeXml(stringValue) + "</fieldValue><fieldName>" + StringEscapeUtils.escapeXml(name) + "</fieldName></field>";
				
				}
				//recString = recString.substring(0, recString.length() - 2);
				recString += "</RSRecord>";
				records.add(recString);
			}
			return records;
		} else {
			List<String> records = new ArrayList<String>();

			HashMap<String, String> idsNames = new HashMap<String, String>();

			for(int i=0;i<count;i+=1)
			{
				if(reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0)) {

					break;
				}
				GenericRecord rec=reader.get(5, TimeUnit.MINUTES);
				if(rec==null) 
					break;

				Field DocId = rec.getField("ObjectID");
				StringField docId = (StringField)DocId;
				Field RankID = rec.getField("rank");
				StringField rankId = (StringField)RankID;
				Field CollID = rec.getField("gDocCollectionID");
				StringField colId = null;
				String docIdRecord;
				String colIdRecord = null;


				String recString = "<RSRecord>";
				ArrayList<String> presentationIds = (ArrayList<String>)session.getAttribute(org.gcube.application.framework.search.library.util.SessionConstants.presentableFields);
				if (presentationIds != null && presentationIds.size() != 0) {
					for (int j = 0; j < presentationIds.size(); j++) {
						Field fld = rec.getField(presentationIds.get(j));
						StringField stringField = (StringField) fld;

						if (fld != null) {
							String name = idsNames.get(fld.getFieldDefinition().getName());
							if (name == null || name.equals("")) {
								try {
									name = QueryHelper.GetFieldNameById(fld.getFieldDefinition().getName());
								} catch (ResourceRegistryException e) {
									logger.error("Error while retrieving field name", e);
								}
								idsNames.put(fld.getFieldDefinition().getName(), name);
							}

							recString += "<field><fieldId>" + fld.getFieldDefinition().getName() + "</fieldId><fieldValue>" + StringEscapeUtils.escapeXml(stringField.getPayload()) + "</fieldValue><fieldName>" + StringEscapeUtils.escapeXml(name) + "</fieldName></field>";
						}
					}
				}

				docIdRecord = "<field><fieldId>" + DocId.getFieldDefinition().getName() + "</fieldId><fieldValue>" + StringEscapeUtils.escapeXml(docId.getPayload()) + "</fieldValue><fieldName>" + "ObjectID" + "</fieldName></field>";

				if (CollID  != null) {
					colId = (StringField)CollID;
					colIdRecord = "<field><fieldId>";
					if (CollID.getFieldDefinition() != null && CollID.getFieldDefinition().getName() != null)
						colIdRecord += CollID.getFieldDefinition().getName(); 

					colIdRecord += "</fieldId><fieldValue>";
					if (colId != null && colId.getPayload() != null)
						colIdRecord+= colId.getPayload();
					colIdRecord+= "</fieldValue><fieldName>"+ "gDocCollectionID" + "</fieldName></field>";
				}
				recString += docIdRecord;

				if (colIdRecord != null)
					recString += colIdRecord;

				DigitalObject mDO;
				if (colId == null) {
					logger.debug("the gDocCollection id is: ");
					logger.debug("Creating digital object - the gDocCollection id is NULL: " + docId.getPayload());
					mDO = new DigitalObject(session, docId.getPayload());
				}
				else {
					logger.debug("Creating digital object - the gDocCollection id is NOT null: " + docId.getPayload());
					mDO = new DigitalObject(session, docId.getPayload(), colId.getPayload());
				}

				String mimeRecord = "<field><fieldId>" + "mimeId" + "</fieldId><fieldValue>" + mDO.getMimeType() + "</fieldValue><fieldName>" + "mimeType" + "</fieldName></field>";
				recString+= mimeRecord;


				recString += "</RSRecord>";
				records.add(recString);
			}
			return records;
		}
	}

	public void setWindowSize(int windowSize) throws GRS2ReaderInvalidArgumentException {
		if (this.reader instanceof RecordReaderDelegate) {
			((RecordReaderDelegate<GenericRecord>)this.reader).changeWindowSize(windowSize);
		} else {
			throw new GRS2ReaderInvalidArgumentException("Reader not instance of RecordReaderDelegate");
		}
	}

	private List<GenericRecord> readRS(int count, DisableButtons dis, boolean back) throws GRS2ReaderException, gRS2NoRecordReadWithinTimeIntervalException {
		if (!back) {
			List<GenericRecord> results = new ArrayList<GenericRecord>();
			int i = 0;
			while (iter.hasNext()) {
				if (i < count) {
					GenericRecord rec = iter.next();
					if (rec == null) {
						dis.setForward(true);
						break;
					} else {
						results.add(rec);
						i++;
					}
				} else 
					break;
			}
			if (!iter.hasNext())
				dis.setForward(true);
			return results;
		} else {
			List<GenericRecord> results = new ArrayList<GenericRecord>();
			int i = 0;
			while (iter.hasPrevious()) {
				if (i < count) {
					GenericRecord rec = iter.previous();
					if (rec == null) {
						dis.setForward(true);
						break;
					} else {
						results.add(rec);
						i++;
					}
				} else 
					break;
			}
			if (!iter.hasNext())
				dis.setForward(true);
			return results;
		}
	}

	public ArrayList<DigitalObject> getAllResultIds(ASLSession session) {
		ArrayList<DigitalObject> rsIds = new ArrayList<DigitalObject>();
		try {
			while (true) {
				if (reader.getStatus()==Status.Dispose || (reader.getStatus()==Status.Close && reader.availableRecords()==0))
					break;
				GenericRecord rec = reader.get(180, TimeUnit.SECONDS);
				Field DocId;
				try {
					DocId = rec.getField("ObjectID");
					StringField docId = (StringField)DocId;
					Field CollID = rec.getField("gDocCollectionID");
					StringField colId = null;
					if (CollID != null)
						colId = (StringField)CollID;
					DigitalObject dobj = new DigitalObject(session, docId.getPayload(), colId.getPayload());
					// TODO: get guid and put it as the do title!!
					Field[] flds = rec.getFields();
					for (int i = 0; i < flds.length; i++) {
						try {
							String name = QueryHelper.GetFieldNameById(flds[i].getFieldDefinition().getName());
							if (name != null && name.equals("guid")) {
								StringField strFld = (StringField) flds[i];
								//dobj.setTitle(strFld.getPayload());
								logger.debug("The guid is: " + name);
							} else if (name == null)
								logger.debug("The guid is null");

						} catch (ResourceRegistryException e) {
							// TODO Auto-generated catch block
							logger.error("Exception:", e);
						}
					}
					rsIds.add(dobj);
				}
				catch (GRS2RecordDefinitionException e) {
					logger.error("Exception:", e);
				}
				catch (GRS2BufferException e) {
					logger.error("Exception:", e);
				}
			}
			logger.debug("Returning number of all ids: " + rsIds.size());
		}
		catch (GRS2ReaderException e) {
			// TODO Auto-generated catch block
			logger.error("Exception:", e);
		}
		return rsIds;
	}

	public void setGenericSearchType (String gst) {
		genericSearchType = gst;
	}
	
	public void setOnlyTitleSnippet (boolean onlyTS){
		onlyTitleSnippet = onlyTS;
	}

	public int getNumOfResultsRead () {
		return numOfResultsRead;
	}

	public boolean getTotalRead() {
		return readTotal;
	}



}
