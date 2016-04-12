package org.gcube.index.entities;

import java.io.Serializable;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.gcube.textextractor.entities.ShortenCE4NameResponse;
import org.gcube.textextractor.helpers.ExtractorHelper;

public class SearchResponse implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

    
	public String qterm;
	public String uri;
//	public String doc_uri;
	public List<Title> titles = Arrays.asList(new Title());
	
	//public String country;
	//public List<ShortenCE4NameResponse> country;
	public List<Map<String, String>> country;
	
	//public String vessel_type;
//	public List<ShortenCE4NameResponse> vessel_type;
	public List<Map<String, String>> vessel;
	
	//public String gear_type;
//	public List<ShortenCE4NameResponse> gear_type;
	public List<Map<String, String>> gear;
	
	public List<Map<String, String>> management;
	public List<Map<String, String>> sector;
	
	public List<String> technology;
	//public List<String> species;
	
//	public String species;
//	public List<ShortenCE4NameResponse> species;
	public List<Map<String, String>> species;
	
	//public String prov = "IS of provinence";
	public Map<String, String> prov = new HashMap<String, String>();
	
	public Double score;
	public String seealso = "http://www.fao.org/documents/en/docrep.jsp";
	public String thumb = "http://www.hardwareluxx.de/images/stories/logos/Document_Foundation_logo.jpg";
	public List<Snippet> snippets;


	public List<Map<String, String>> status;


	public List<Map<String, String>> access_control;


	public List<Map<String, String>> enforcement_method;


	public List<Map<String, String>> fishing_control;


	public List<Map<String, String>> other_income_source;


	public List<Map<String, String>> market;
        
        public List<Map<String, String>> year;
        
        public List<Map<String, String>> statistics;


	public List<Map<String, String>> finance_mgmt_authority;


	public List<Map<String, String>> management_indicator;


	public List<Map<String, String>> post_processing_method;


	public List<Map<String, String>> bycatch;


	public List<Map<String, String>> target;


	public List<Map<String, String>> thretened;


	public List<Map<String, String>> discard;


	public List<Map<String, String>> seasonality;


	public List<Map<String, String>> decision_maker;


	public List<Map<String, String>> owner_of_access_right;


	public List<Map<String, String>> applicant_for_access_right;
	
	public static String emptyResponse(String msg) {
            if(!"".equals(msg))
		return "{msg: "+msg+"}";
            else
                return "{}";
	}

	public static String makeEntitiesListResponse(String documentURI,
			String sparqlJson) {
		
		try {
			List<ShortenCE4NameResponse> speciesCE4Names = ExtractorHelper.parseJsonRequest(sparqlJson);
			return ExtractorHelper.transformList(speciesCE4Names, documentURI);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}


	public static String makeEntitiesListResponse(String sparqlJson) {

		try {
			List<ShortenCE4NameResponse> speciesCE4Names = ExtractorHelper.parseJsonRequest(sparqlJson);
			return ExtractorHelper.transformList(speciesCE4Names);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	public static String makeEntitiesListResponseWithLang(String sparqlJson, String lang) {

		try {
			List<ShortenCE4NameResponse> speciesCE4Names = ExtractorHelper.parseJsonRequest(sparqlJson);
			return ExtractorHelper.transformList(speciesCE4Names, lang);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
        
        public static String makeInfoBoxResponse(String sparqlJson) {
            throw new UnsupportedOperationException("Not yet implemented");
    }

}