package org.gcube.portlets.user.speciesdiscovery.server;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.Collections;
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
import org.gcube.portlets.user.speciesdiscovery.client.util.SpeciesGridFields;
import org.gcube.portlets.user.speciesdiscovery.server.asl.SessionUtil;
import org.gcube.portlets.user.speciesdiscovery.server.persistence.dao.ResultRowPersistence;
import org.gcube.portlets.user.speciesdiscovery.shared.CommonName;
import org.gcube.portlets.user.speciesdiscovery.shared.ItemParameter;
import org.gcube.portlets.user.speciesdiscovery.shared.ResultRow;
import org.gcube.portlets.user.speciesdiscovery.shared.Taxon;
import org.gcube.portlets.user.speciesdiscovery.shared.util.NormalizeString;


/**
 * 
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * @Sep 6, 2013
 *
 */
public class ResultRowTable extends HttpServlet {

	/**
	 * 
	 */
	public static final String TEXT_HTML = "text/html; charset=UTF-8";
	protected static final String UTF_8 = "UTF-8";

	/**
	 * 
	 */
	private static final long serialVersionUID = -9006347088111602996L;
	
	protected Logger logger = Logger.getLogger(ResultRowTable.class);

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
		return (DOCTYPE + "\n" + HTML+ "\n"+HEAD + "\n"+TITLE + title + TITLECLOSE+"\n"+HEADCLOSE+"\n");
		
	}

	protected ASLSession getASLSession(HttpServletRequest req)
	{
		return SessionUtil.getAslSession(req.getSession());
	}

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#doGet(javax.servlet.http.HttpServletRequest, javax.servlet.http.HttpServletResponse)
	 */
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {

		String resultServiceRowID = "";
		resp.setContentType(TEXT_HTML);
//		PrintWriter out = resp.getWriter();
		PrintWriter out = new PrintWriter(new OutputStreamWriter(resp.getOutputStream(), UTF_8), true);
		
//		resp.setCharacterEncoding(UTF_8);

	
		out.println(headWithTitle(""));
		out.println(BODY);
		try {
			// get parameters
			resultServiceRowID = req.getParameter("oid");
			
			//IS VALID RR ID?
			if(resultServiceRowID==null || resultServiceRowID.isEmpty()){
				out.println(error("Parameter oid not found"));
			}else{		

				logger.trace("found oid "+resultServiceRowID);
			
				try {

					ResultRowPersistence persistence = SessionUtil.getCurrentEJBResultRow(getASLSession(req));
	
					if(persistence==null){
						logger.trace("ResultRowPersistence not found in database");
						out.println(error("Row id not found in database"));
						out.close();
						return;
					}

					CriteriaBuilder queryBuilder = persistence.getCriteriaBuilder();
					CriteriaQuery<Object> cq = queryBuilder.createQuery();
					Predicate pr1 =  queryBuilder.equal(persistence.rootFrom(cq).get(ResultRow.SERVICE_ID_FIELD), resultServiceRowID);
					cq.where(pr1);
					
					Iterator<ResultRow> iterator = persistence.executeCriteriaQuery(cq).iterator();
					
					ResultRow row = null;

					while(iterator.hasNext()){
						row = iterator.next();
						break;
					}

					if(row==null){
						logger.trace("Service Row id not found in database");
						out.println(error("Service Row id not found in database"));
						out.close();
						return;
					}
					
					String table = getTableForResultRow(row, getClassification(row.getParents()));

					logger.trace("table for ResultRowPersistence is empty "+table.isEmpty());
					out.println(table);
					
					
				} catch (Exception e) {
					
					logger.error("Error in ResultRowTable servlet ",e);
					throw new Exception("Error in ResultRowTable servlet ", e);
				}
			}
			out.println(BODYCLOSE);
			out.println(HTMLCLOSE);
			out.close(); //CLOSE STREAM
			
		}catch (Exception e) {
			String error = "Sorry an error occurred when creating the table for result row with id: "+resultServiceRowID;
			
			if(out!=null)
				out.println(error);
//			else
//				throw new ServletException(error);	
			
//			logger.error(error, e);
			out.println(BODYCLOSE);
			out.println(HTMLCLOSE);
			out.close(); //CLOSE STREAM

		}
	}
	
	public String error(String message){
		String errorPage = "";
	    errorPage +=("<p>Error: "+message+"</p>");
	    return errorPage;
	}
	
	//TODO modified
	public static String getClassification(List<Taxon> listTaxon)
	{
		StringBuilder sb = new StringBuilder();
		
		for (int i = listTaxon.size()-1; i >= 0; i--) {
			Taxon taxon = listTaxon.get(i);

			sb.append("<b>");
			
			if(!NormalizeString.isUndefined(taxon.getRank()))
				sb.append(taxon.getRank());
			else
				sb.append("Rank not found");
	
			sb.append("</b>: ");
			
			if(!NormalizeString.isUndefined(taxon.getName()))
				sb.append(taxon.getName());
			else
				sb.append("Name not found");
			
			if(i!=0)
				sb.append(" -> ");
		}
		return sb.toString();
	}
	
	public String getTableForResultRow(ResultRow row, String classification){
		
		//Init values
		String dataSourceName = "";
		String dataProviderName = "";
		String dataSetName = "";
		String dataSetCitation= "";
		String matchingTaxonName= "";
		String matchingAccordionTo= "";
		String matchingRank= "";
		String matchingCredits= "";
		int occurencesCount = 0;
		
		String scientificNameAuthorship = "";
		String lsid = "";
		String credits = "";
		
		String propertiesHtml = "";
		
		
		//Validate values
		if(row.getParents()!=null){
			
			if(row.getParents().get(0).getName()!=null) matchingTaxonName = row.getParents().get(0).getName();
			
			if(row.getParents().get(0).getAccordingTo()!=null) matchingAccordionTo = row.getParents().get(0).getAccordingTo();
			
			if(row.getParents().get(0).getRank()!=null) 	matchingRank = row.getParents().get(0).getRank();
		}
	
		if(row.getDataSourceName()!=null) dataSourceName = row.getDataSourceName();
		if(row.getDataProviderName()!=null) dataProviderName = row.getDataProviderName();
		if(row.getDataSetName()!=null) dataSetName = row.getDataSetName();
		if(row.getDataSetCitation()!=null) dataSetCitation = row.getDataSetCitation();
		if(row.getMatchingCredits()!=null) matchingCredits = row.getMatchingCredits();
		if(row.getOccurencesCount()!=0) occurencesCount = row.getOccurencesCount();
		
	
		String commonNames = "";
		if(row.getCommonNames()!=null){
			for (CommonName comName : row.getCommonNames()) {
				commonNames+= "<b>"+comName.getName()+"</b>" +" ("+comName.getLanguage()+") - ";
			}
		}
		
		
		if(row.getScientificNameAuthorship()!=null) scientificNameAuthorship = row.getScientificNameAuthorship();
		
		if(row.getLsid()!=null) lsid = row.getLsid();
		
		if(row.getCredits()!=null) credits = row.getCredits();
		
	
		if(row.getProperties()!=null){
			
			List<ItemParameter> listProperties = row.getProperties();
			Collections.sort(listProperties, ItemParameter.COMPARATOR);
			
			propertiesHtml+="<table class=\"parameters\">";
			
			for (ItemParameter itemParameter : listProperties) {
				
				propertiesHtml+=
					"<tr>" +
					"	<td class=\"title\">"+itemParameter.getKey()+"</td>" +
					"	<td>"+itemParameter.getValue()+"</td>" +
					"</tr>";
			}
			
			propertiesHtml+="</table>";
		}

		String table = "<table class=\"imagetable\">" +
		"<tr>" +
		"	<th>"+matchingTaxonName+"</th>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.CLASSIFICATION_STRING.getName()+"</td>" +
		"	<td>"+classification+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.COMMON_NAMES.getName()+" (Language)</td>" +
		"	<td>"+commonNames+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASOURCE.getName()+"</td>" +
		"	<td>"+dataSourceName+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATAPROVIDER.getName()+"</td>" +
		"	<td>"+dataProviderName+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASET.getName()+"</td>" +
		"	<td>"+dataSetName+"</td>" +
		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_AUTHOR.getName()+"</td>" +
//		"	<td>"+matchingAccordionTo+"</td>" +
//		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_RANK.getName()+"</td>" +
		"	<td>"+matchingRank+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.DATASET_CITATION.getName()+"</td>" +
		"	<td>"+dataSetCitation+"</td>" +
		"</tr>" +
//		"<tr>" +
//		"	<td class=\"title\">"+SpeciesGridFields.MATCHING_CREDITS.getName()+"</td>" +
//		"	<td>"+matchingCredits+"</td>" +
//		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.PRODUCT_OCCURRENCES.getName()+"</td>" +
		"	<td>"+occurencesCount+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.LSID.getName()+"</td>" +
		"	<td>"+lsid+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.SCIENTIFICNAMEAUTHORSHIP.getName()+"</td>" +
		"	<td>"+scientificNameAuthorship+"</td>" +
		"</tr>" +
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.CREDITS.getName()+"</td>" +
		"	<td>"+credits+"</td>" +
		"</tr>" +
		
		"<tr>" +
		"	<td class=\"title\">"+SpeciesGridFields.PROPERTIES.getName()+"</td>" +
		"	<td>"+propertiesHtml+"</td>" +
		"</tr>" +

		"</table>";
		return table;
		
	}

}