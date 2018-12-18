package org.gcube.portlets.user.speciesdiscovery.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.portlets.user.speciesdiscovery.client.util.TaxonomyGridField;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.DaoSession;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.TaxonomyRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.server.service.SpeciesService;
import org.gcube.portlets.user.speciesdiscovery.server.service.TaxonomyItemConverter;
import org.gcube.portlets.user.speciesdiscovery.server.stream.CloseableIterator;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.SearchServiceException;
import org.gcube.portlets.user.speciesdiscovery.shared.SessionExpired;
import org.gcube.portlets.user.speciesdiscovery.shared.TaxonomyRow;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 6, 2013
 *
 */
public class TaxonomyRowTable extends HttpServlet {

	protected static final String UTF_8 = "UTF-8";
	private static final long serialVersionUID = 9157876104914505028L;
	public static final String TEXT_HTML = "text/html; charset=UTF-8";

	public static final String DOCTYPE = "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 Transitional//EN\">";
	public static final String HTML = "<HTML>";
	public static final String HTMLCLOSE = "</HTML>";
	public static final String HEAD = "<HEAD>";
	public static final String HEADCLOSE = "</HEAD>";
	public static final String TITLE = "<TITLE>";
	public static final String TITLECLOSE = "</TITLE>";
	public static final String BODY = "<BODY>";
	public static final String BODYCLOSE = "</BODY>";

	public static String headWithTitle(String title) {
//		return (DOCTYPE + "\n" + HTML+"\n<link type=\"text/css\" rel=\"stylesheet\" href=\"SpeciesDiscovery.css\">" + HEAD+TITLE + title + TITLECLOSE+HEADCLOSE+"\n");
		return DOCTYPE + "\n" + HTML+ "\n"+HEAD + "\n"+TITLE + title + TITLECLOSE+"\n"+HEADCLOSE+"\n";

	}


	protected Logger logger = Logger.getLogger(TaxonomyRowTable.class);


	protected ASLSession getASLSession(HttpServletRequest req)
	{
		return SessionUtil.getAslSession(req.getSession());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		retrieveTaxonomyRowAsHtmlTable(req, resp);
	}


	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		retrieveTaxonomyRowAsHtmlTable(req, resp);
	}


	/**
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	private void retrieveTaxonomyRowAsHtmlTable(HttpServletRequest req, HttpServletResponse resp) {

		String taxonomyServiceRowID = "";
//		resp.setCharacterEncoding(UTF_8);
		resp.setContentType(TEXT_HTML);

		TaxonomyRow row = null;
		PrintWriter out = null;

		try {
			out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), UTF_8), true);
			out.println(headWithTitle(""));
			out.println(BODY);
			// get parameters
			taxonomyServiceRowID = req.getParameter("oid");

			ASLSession aslSession = getASLSession(req);

			//IS VALID RR ID?
			if(taxonomyServiceRowID==null || taxonomyServiceRowID.isEmpty()){
				out.println(error("Parameter oid not found"));
			}else{

				logger.trace("doGet found oid "+taxonomyServiceRowID);

				try {

					TaxonomyRowPersistence persistence = SessionUtil.getCurrentEJBTaxonomyItem(aslSession);

					if(persistence==null){

						logger.trace("TaxonomyRowPersistence not found in session, creating it");
						persistence = new TaxonomyRowPersistence(DaoSession.getEntityManagerFactory(aslSession));
					}

					CriteriaBuilder queryBuilder = persistence.getCriteriaBuilder();
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(persistence.rootFrom(cq).get(TaxonomyRow.SERVICE_ID_FIELD), taxonomyServiceRowID);
					cq.where(pr1);

					Iterator<TaxonomyRow> iterator = persistence.executeCriteriaQuery(cq).iterator();

					while(iterator.hasNext()){
						row = iterator.next();
						break;
					}

					if(row==null){

						logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" not found in database");

						row = findingTaxonomyInCaches(taxonomyServiceRowID, getASLSession(req));

						if(row!=null){
							logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" found into hash map caches");
							//ROW was found into database or by service
							logger.trace("converting taxonomy row to html table");
							String table = getHTMLTableForTaxonomy(row);

							logger.trace("table for ResultRowPersistence is empty? "+table.isEmpty());
							out.println(table);
							out.close();
							return;
						}else
							logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" doesn't found into hash map caches");


						logger.trace("Tentative recovering taxonomy with id "+taxonomyServiceRowID+" from service");
						SpeciesService service = getSpeciesService(req);
						//StreamExtend<String> streamIds = new StreamExtend<String>(Arrays.asList(taxonomyServiceRowID).iterator());
						CloseableIterator<TaxonomyItem> streamIterator = service.retrieveTaxonomyById(Arrays.asList(taxonomyServiceRowID));
						TaxonomyItemConverter taxonomyItemConverter = new TaxonomyItemConverter(aslSession);

	//						int i = 1;
						while (streamIterator.hasNext()) {
							TaxonomyItem tax = streamIterator.next();
							logger.trace("Taxonomy with id "+taxonomyServiceRowID+" found on service, converting");
							// from TaxonomyItem to LightTaxonomyRow
							row = taxonomyItemConverter.convert(tax);
							break;
						}

						streamIterator.close();

						if(row==null){
							out.println(error("Sorry, taxonomy with "+taxonomyServiceRowID+" doesn't found in service"));
							out.close();
							return;
						}
					}

					//ROW was found into database or by service
					logger.trace("converting taxonomy row to html table");
					String table = getHTMLTableForTaxonomy(row);

					logger.trace("table for ResultRowPersistence is empty? "+table.isEmpty());
					out.println(table);

				} catch (Exception e) {

					if(e instanceof SessionExpired)
						throw new SessionExpired("Sorry, user session is expired, Refresh and Try again");

					logger.error("Error in TaxonomyRowTable servlet ",e);
					throw new Exception("Error in ResultRowTable servlet ", e);
				}
			}
			out.println(BODYCLOSE);
			out.println(HTMLCLOSE);
			out.close(); //CLOSE STREAM

		}catch (Exception e) {
			String error;

			if(e instanceof SessionExpired)
				error = e.getMessage();
			else
				error = "Sorry an error occurred when creating the table for taxonomy row with id: "+taxonomyServiceRowID;

			if(out==null){
				try{
					out = resp.getWriter();
				} catch (IOException e1) {
					logger.error("Error on get Writer into servlet ",e);
				}
			}
			out.println(error);
			out.println(BODYCLOSE);
			out.println(HTMLCLOSE);
			out.close(); //CLOSE STREAM
		}

	}

	protected TaxonomyRow findingTaxonomyInCaches(String taxonomyServiceRowID, ASLSession session){

		HashMap<String, TaxonomyRow> hashChildrenTaxa = SessionUtil.getHashMapChildrenTaxonomyCache(session);


		if(hashChildrenTaxa!=null){
			logger.trace("Finding Taxonomy Row with id "+taxonomyServiceRowID+" into hash map children");
			TaxonomyRow row = hashChildrenTaxa.get(taxonomyServiceRowID);

			if(row!=null){
				logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" found into hash map children");
				return row;
			}
		}

		hashChildrenTaxa = SessionUtil.getHashMapSynonymsTaxonomyCache(session);

		if(hashChildrenTaxa!=null){
			logger.trace("Finding Taxonomy Row with id "+taxonomyServiceRowID+" into hash map synonyms");
			TaxonomyRow row = hashChildrenTaxa.get(taxonomyServiceRowID);

			if(row!=null){
				logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" found into hash map synonyms");
				return row;
			}
		}

		hashChildrenTaxa = SessionUtil.getHashMapTaxonomyByIdsCache(session);

		if(hashChildrenTaxa!=null){
			logger.trace("Finding Taxonomy Row with id "+taxonomyServiceRowID+" into hash map ByIds");
			TaxonomyRow row = hashChildrenTaxa.get(taxonomyServiceRowID);

			if(row!=null){
				logger.trace("Taxonomy Row with id "+taxonomyServiceRowID+" found into hash map ByIds");
				return row;
			}
		}

		return null;
	}



	protected SpeciesService getSpeciesService(HttpServletRequest req) throws SearchServiceException
	{
		try {
			ASLSession session = getASLSession(req);
			return SessionUtil.getService(session);
		} catch (Exception e) {
			e.printStackTrace();
			logger.error("An error occurred when contacting the species service", e);
			//			System.out.println("An error occurred retrieving the service" +e);
			throw new SearchServiceException("contacting the species service.");
		}
	}

	public String error(String message){
		String errorPage = "";
	    errorPage +="<p>Error: "+message+"</p>";
	    return errorPage;
	}


	public String getHTMLTableForTaxonomy(TaxonomyRow row){

		//Init values
		String dataProviderName = "";
		String dataSetCitation= "";
		String matchingAccordionTo= "";
		String rank= "";
//		String matchingCredits= "";

		String statusName = "";
		String dateModified = "";
		String statusRemark = "";

		String scientificNameAuthorship = "";
		String lsid = "";
		String credits = "";

		String propertiesHtml = "";

		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
		if(row.getStatusName()!=null) statusName = row.getStatusName();
		if(row.getDateModified()!=null) dateModified = row.getDateModified();

		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
		if(row.getRank()!=null) rank = row.getRank();
		if(row.getAccordingTo()!=null) matchingAccordionTo = row.getAccordingTo();

		if(row.getStatusRemarks()!=null) statusRemark = row.getStatusRemarks();

		if(row.getScientificNameAuthorship()!=null) scientificNameAuthorship = row.getScientificNameAuthorship();

		if(row.getLsid()!=null) lsid = row.getLsid();

		if(row.getCredits()!=null) credits = row.getCredits();


		//BUILD TABLES PROPERTIES
		if(row.getProperties()!=null){

			List<ItemParameter>  hashProperties = row.getProperties();
			Collections.sort(hashProperties, ItemParameter.COMPARATOR);

			propertiesHtml+="<table class=\"parameters\">";

			for (ItemParameter itemParameter : hashProperties) {

				propertiesHtml+=
					"<tr>" +
					"	<td class=\"title\">"+itemParameter.getKey()+"</td>" +
					"	<td>"+itemParameter.getValue()+"</td>" +
					"</tr>";
			}

			propertiesHtml+="</table>";
		}


		//Create list common name
		String commonNames = "";

		if(row.getCommonNames()!=null){
			for (CommonName comName : row.getCommonNames()) {
				commonNames+= "<b>"+comName.getName()+"</b>" +" ("+comName.getLanguage()+") - ";
			}
		}


		String table = "<table class=\"imagetable\">";

//		if(isNewTab)
			table+=
				"<tr>" +
				"	<td class=\"title\">"+TaxonomyGridField.COMMON_NAMES.getName()+" (Language)</td>" +
				"	<td>"+commonNames+"</td>" +
				"</tr>";


//		table+="<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.STATUSREFNAME.getName()+"</td>" +
//		"	<td>"+statusName+"</td>" +
//		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+TaxonomyGridField.STATUS_REMARKS.getName()+"</td>" +
//		"	<td>"+statusRemark+"</td>" +
//		"</tr>" +


		table +="<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.DATASOURCE.getName()+"</td>" +
		"	<td>"+dataProviderName+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.DATEMODIFIED.getName()+"</td>" +
		"	<td>"+dateModified+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.MATCHING_RANK.getName()+"</td>" +
		"	<td>"+rank+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.CITATION.getName()+"</td>" +
		"	<td>"+dataSetCitation+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.LSID.getName()+"</td>" +
		"	<td>"+lsid+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.SCIENTIFICNAMEAUTHORSHIP.getName()+"</td>" +
		"	<td>"+scientificNameAuthorship+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.CREDITS.getName()+"</td>" +
		"	<td>"+credits+"</td>" +
		"</tr>" +

		"<tr>" +
		"	<td class=\"title\">"+TaxonomyGridField.PROPERTIES.getName()+"</td>" +
		"	<td>"+propertiesHtml+"</td>" +
		"</tr>" +

		"</table>";

		//DEBUG
//		System.out.println("Table: "+table);

		return table;

	}

}