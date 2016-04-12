package org.gcube.textextractor.helpers;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.StringEscapeUtils;
import org.gcube.semantic.annotator.FLOD_EntityCollection;
import org.gcube.semantic.annotator.SMARTFISH_EntityCollection;
import org.gcube.semantic.annotator.utils.SMART_ENTITY_TYPES;
import org.gcube.textextractor.entities.Binding;
import org.gcube.textextractor.entities.CE4NameResponse;
import org.gcube.textextractor.entities.ShortenCE4NameResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Charsets;
import com.google.common.base.Joiner;
import com.google.common.base.Predicate;
import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.TreeTraverser;
import com.google.common.io.Files;
import com.google.gson.Gson;
import java.util.LinkedList;
import org.gcube.textextractor.entities.ExtractedEntity;

public class ExtractorHelper {

    private static final Logger logger = LoggerFactory.getLogger(ExtractorHelper.class);

    public static String createRowseFromFields(String documentID, String collectionID, String idxType, String lang, Map<String, String> fields) {

        StringBuilder strBuf = new StringBuilder();
        if (lang == null) {
            lang = "unknown";
        }

        strBuf.append("<ROWSET colID=\"" + collectionID + "\" idxType=\"" + idxType + "\" lang=\"" + lang + "\">\n");
        strBuf.append("\t<ROW>\n");

        for (Map.Entry<String, String> field : fields.entrySet()) {
            String name = field.getKey();
            String value = field.getValue();

            if (name.equals("language") || name.equals("documentID")) {
                continue;
            }

            if (value != null && value.length() > 0) {
                strBuf.append("\t\t<FIELD lang=\"" + lang + "\" name=\"" + name + "\">" + StringEscapeUtils.escapeXml(value) + "</FIELD>\n");
            }

        }

        strBuf.append("\t\t<FIELD name=\"ObjectID\">" + StringEscapeUtils.escapeXml(documentID) + "</FIELD>\n");
        strBuf.append("\t</ROW>\n");
        strBuf.append("</ROWSET>\n");

        return strBuf.toString();
    }

    public static String fileContent(String filename) throws IOException {
        return Files.toString(new File(filename), Charsets.UTF_8);
    }

    public static String removeEmptyLines(String text) {
        return text.replaceAll("\\s+", " ");
    }

    public static List<ExtractedEntity> covertToStringList(String str, String str_fr ) throws Exception {
        ArrayList<ExtractedEntity> result = new ArrayList<ExtractedEntity>();
        List<String> en_names = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        List<String> fr_names = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str_fr);
        
        logger.info("en_names size : " + en_names.size() + " fr_names size : " + fr_names.size());
        if(en_names.isEmpty()){
            for (int i = 0; i < fr_names.size(); i++) {
                result.add(new ExtractedEntity("", fr_names.get(i)));
            }
        }
        else{
         for (int i = 0; i < en_names.size(); i++) {
             String en_name = en_names.get(i);
             try {
                 result.add(new ExtractedEntity(en_name, fr_names.get(i)));
                } catch (Exception e) {
                    result.add(new ExtractedEntity(en_name, ""));
                }
            }
        }
        return result;
    }
    
     public static List<ExtractedEntity> covertToStringList(String str) throws Exception {
        ArrayList<ExtractedEntity> result = new ArrayList<ExtractedEntity>();
        List<String> en_names = Splitter.on(",").trimResults().omitEmptyStrings().splitToList(str);
        for (int i = 0; i < en_names.size(); i++) {
            String en_name = en_names.get(i);
            result.add(new ExtractedEntity(en_name, ""));
        }
        return result;
    }

    public static String covertToString(Collection<String> col) {
        return Joiner.on(", ").skipNulls().join(col);
    }

    public static void main(String[] args) {
        logger.info(covertToString(Lists.newArrayList("a")));
    }

//	
//	
//	public  static String createRowseFromFields(String documentID, String collectionID, String idxType, String lang, Map<String, String> fields){
//		StringBuffer strBuf = new StringBuffer();
//		
//		
//		
//		strBuf.append("<ROWSET colID=\"" + collectionID + "\" idxType=\"" + idxType +"\" lang=\"" + lang + "\">\n");
//		strBuf.append("\t<ROW>\n");
//		
//		for (Map.Entry<String, String> field : fields.entrySet()){
//			String name = field.getKey();
//			String value = field.getValue();
//			
//			if (name.equals("language"))
//				continue;
//			
//			if (value != null && value.length() > 0)
//				strBuf.append("\t\t<FIELD lang=\"" + lang + "\" name=\"" + name + "\">" + StringEscapeUtils.escapeXml(value) +"</FIELD>\n");
//			
//		}
//		
//		strBuf.append("\t\t<FIELD name=\"ObjectID\">" + documentID +"</FIELD>\n");
//		strBuf.append("\t</ROW>\n");
//		strBuf.append("</ROWSET>\n");
//		
//		return strBuf.toString();
//	}
    public static List<String> getFilenames(String path) throws FileNotFoundException {

        List<String> filenames = Lists.newArrayList();

        File pathFile = new File(path);

        TreeTraverser<File> tr = Files.fileTreeTraverser();
        for (File file : tr.children(pathFile)) {
            if (file.isFile()) {
                filenames.add(file.getAbsolutePath());
            }
        }

        return filenames;

    }

    public static String constructURL(String name, String res_type) throws UnsupportedEncodingException {
        String url = "http://www.fao.org/figis/flod/askflod/json/ce4name.jsp";
        //url += "name="+URLDecoder.decode(name) + "&" +
        String params = "name=" + URLEncoder.encode(name, "UTF-8") + "&" + "res_type=" + URLEncoder.encode(res_type, "UTF-8");

        return url + "?" + params;
    }

    public static String constructURL(List<String> names, String res_type) throws UnsupportedEncodingException {
        String url = "http://www.fao.org/figis/flod/askflod/json/ce4name.jsp";
        //url += "name="+URLDecoder.decode(name) + "&" +

        String params = "";
        for (String name : names) {
            params += "name=" + URLEncoder.encode(name, "UTF-8") + "&";
        }
        params += "res_type=" + URLEncoder.encode(res_type, "UTF-8");

        return url + "?" + params;
    }

    public static List<ShortenCE4NameResponse> getJsonResponse(String url) throws Exception {
        logger.info("getting json response for url : " + url);
        String json = getText(url);
        logger.info("json response for url : " + url + " is : " + json);

        return parseJsonRequest(json);
    }

    public static List<ShortenCE4NameResponse> parseJsonRequest(String json) throws Exception {
        //logger.info(json);

        Gson gson = new Gson();

        CE4NameResponse annotationResponse = gson.fromJson(json, CE4NameResponse.class);

        List<ShortenCE4NameResponse> speciesCE4Name = new ArrayList<ShortenCE4NameResponse>();

        for (Binding b : annotationResponse.results.bindings) {
            speciesCE4Name.add(new ShortenCE4NameResponse(b));
        }

        return speciesCE4Name;
    }

    public static String transformList(List<ShortenCE4NameResponse> speciesCE4Name) {
        return transformList(speciesCE4Name, null, null);
    }

    public static String transformList(List<ShortenCE4NameResponse> speciesCE4Name, String lang) {
        return transformList(speciesCE4Name, null, lang);
    }

    public static String transformList(List<ShortenCE4NameResponse> speciesCE4Name, String documentURI, String lang) {

        Set<String> uris = new HashSet<String>();

        for (ShortenCE4NameResponse resp : speciesCE4Name) {
            uris.add(resp.uri);
        }

        List<Map<String, String>> transformedList = new ArrayList<Map<String, String>>();

        for (String uri : uris) {

            Map<String, String> transformed = new HashMap<String, String>();

            if (lang == null || "".equals(lang) || lang.equalsIgnoreCase("en")) {
                transformed.put("label_en", "");
            }
            if (lang == null || "".equals(lang) || lang.equalsIgnoreCase("fr")) {
                transformed.put("label_fr", "");
            }
            if (lang == null || "".equals(lang) || lang.equalsIgnoreCase("la")) {
                transformed.put("label_la", "");
            }
            for (ShortenCE4NameResponse resp : speciesCE4Name) {
                if (resp.uri.equalsIgnoreCase(uri) && (lang == null || "".equals(lang) || lang.equalsIgnoreCase(resp.lang))) {
                    transformed.put("label_" + resp.lang, resp.label);
                }
            }
            transformed.put("uri", uri);
            if (documentURI != null) {
                transformed.put("doc_uri", documentURI);
            }
            transformedList.add(transformed);
        }

        return new Gson().toJson(transformedList);
    }

    public static List<Map<String, String>> transformListToCE4Names(List<ShortenCE4NameResponse> speciesCE4Name) {

        Set<String> uris = new HashSet<String>();

        for (ShortenCE4NameResponse resp : speciesCE4Name) {
            uris.add(resp.uri);
        }

        List<Map<String, String>> transformedList = new ArrayList<Map<String, String>>();

        for (String uri : uris) {

            Map<String, String> transformed = new HashMap<String, String>();

            transformed.put("label_en", "");
            transformed.put("label_fr", "");
            transformed.put("label_la", "");
            for (ShortenCE4NameResponse resp : speciesCE4Name) {
                if (resp.uri.equalsIgnoreCase(uri)) {
                    transformed.put("label_" + resp.lang, resp.label);
                }
            }
            transformed.put("uri", uri);

            transformedList.add(transformed);
        }

        return transformedList;
    }

//	public static String transformElement(ShortenCE4NameResponse speciesCE4Name){
//		
//		
//		
//		List<Map<String, String>> transformedList = new ArrayList<Map<String,String>>();
//		
//			
//		Map<String, String> transformed = new HashMap<String, String>();
//		
//		transformed.put("label_en", "");
//		transformed.put("label_fr", "");
//		transformed.put("label_la", "");
//		transformed.put("label_" + speciesCE4Name.lang, speciesCE4Name.label);
//		transformed.put("uri", speciesCE4Name.uri);
//		
//		transformedList.add(transformed);
//	
//		return new Gson().toJson(transformedList);
//	}
    public static String queryURI(ExtractedEntity e, String res_type) throws Exception {
        if (e.isEmpty()) {
            throw new Exception("empty name given");
        }
        String json = "";
        if (SMART_ENTITY_TYPES.isSMARTFISHtype(res_type)) {
            json = SMARTFISH_EntityCollection.getInstance().getURI(e, res_type);
        } else if (SMART_ENTITY_TYPES.isFLODtype(res_type)) {
            json = FLOD_EntityCollection.getInstance().getURI(e, res_type);
        } else {
            System.out.println("unmatched type :" + res_type);
        }
        return transformList(parseJsonRequest(json));

    }

    public static String queryListURI(List<ExtractedEntity> ees, String res_type) throws Exception {
        if (ees == null || ees.size() == 0) {
            throw new Exception("empty name given");
        }
        ExtractedEntity[] namesArr = new ExtractedEntity[ees.size()];
        namesArr = ees.toArray(namesArr);
        String json = "";
        if (SMART_ENTITY_TYPES.isSMARTFISHtype(res_type)) {
            json = SMARTFISH_EntityCollection.getInstance().getURI(namesArr, res_type);
        } else if (SMART_ENTITY_TYPES.isFLODtype(res_type)) {
            json = FLOD_EntityCollection.getInstance().getURI(namesArr, res_type);
        } else {
            System.out.println("unmatched type :" + res_type);
        }

        return transformList(parseJsonRequest(json));
    }

    public static String queryCountry(ExtractedEntity e) throws Exception {
        return queryURI(e, SMART_ENTITY_TYPES.FLAGSTATE);
    }

    public static String queryCountry(List<ExtractedEntity> e) throws Exception {
        return queryListURI(e, SMART_ENTITY_TYPES.FLAGSTATE);
    }

    public static String queryGear(ExtractedEntity e) throws Exception {
        return queryURI(e, SMART_ENTITY_TYPES.GEAR);
    }

    public static String queryVessel(ExtractedEntity name) throws Exception {
        return queryURI(name, SMART_ENTITY_TYPES.VESSEL);
    }

    public static String querySpecies(ExtractedEntity e) throws Exception {
        return queryURI(e, SMART_ENTITY_TYPES.SPECIES);
    }
    
    public static String queryYear(List<ExtractedEntity> e) throws Exception {
        return queryListURI(e, SMART_ENTITY_TYPES.YEAR);
    }
    
    public static String queryStatistics(List<ExtractedEntity> e) throws Exception {
        return queryListURI(e, SMART_ENTITY_TYPES.STATISTICS);
    }
    
    public static String queryYear(ExtractedEntity e) throws Exception {
        return queryURI(e, SMART_ENTITY_TYPES.YEAR);
    }
   
    public static String querySpecies(List<ExtractedEntity> e) throws Exception {
        return queryListURI(e, SMART_ENTITY_TYPES.SPECIES);
    }
    

    public static String queryAuthority(String e) throws Exception {
        throw new Exception("not implemented");
    }

    public static String querySeasonality(List<ExtractedEntity> months) throws Exception {
        return queryListURI(months, SMART_ENTITY_TYPES.SEASONALITY);
    }

    public static String queryWaterArea(List<ExtractedEntity> waterArea) throws Exception {
        return queryListURI(waterArea, SMART_ENTITY_TYPES.WATER_AREA);
    }

    public static String queryLandArea(List<ExtractedEntity> landArea) throws Exception {
        return queryListURI(landArea, SMART_ENTITY_TYPES.LAND_AREA);
    }

    public static String queryManagement(ExtractedEntity management) throws Exception {
        return queryURI(management, SMART_ENTITY_TYPES.MANAGEMENT);
    }

    public static String queryManagement(List<ExtractedEntity> managements) throws Exception {
        return queryListURI(managements, SMART_ENTITY_TYPES.MANAGEMENT);
    }

    public static String querySector(List<ExtractedEntity> sectors) throws Exception {
        return queryListURI(sectors, SMART_ENTITY_TYPES.SECTOR);
    }

    public static String queryExploitationStatus(ExtractedEntity status) throws Exception {
        return queryURI(status, SMART_ENTITY_TYPES.EXPLOITATION_STATUS);
    }

    public static String queryExploitationStatus(List<ExtractedEntity> statuses) throws Exception {
        return queryListURI(statuses, SMART_ENTITY_TYPES.EXPLOITATION_STATUS);
    }

    public static String queryAccessControl(List<ExtractedEntity> accessControls) throws Exception {
        return queryListURI(accessControls, SMART_ENTITY_TYPES.ACCESS_CONTROL);
    }

    public static String queryFishingControl(List<ExtractedEntity> fishingControls) throws Exception {
        return queryListURI(fishingControls, SMART_ENTITY_TYPES.FISHING_CONTROL);
    }

    public static String queryEnforcementMethod(List<ExtractedEntity> enforcementMethods) throws Exception {
        return queryListURI(enforcementMethods, SMART_ENTITY_TYPES.ENFORCEMENT_METHOD);
    }
    
    public static String queryConservationMeasure(List<ExtractedEntity> conservationMeasure) throws Exception {
        return queryListURI(conservationMeasure, SMART_ENTITY_TYPES.CONSERVATION_MEASURE);
    }

    public static String queryAccessRightApplicant(List<ExtractedEntity> applicants) throws Exception {
        return queryListURI(applicants, SMART_ENTITY_TYPES.LEGAL_ENTITY);
    }

    public static String queryOwnershipOfAccessRight(List<ExtractedEntity> owners) throws Exception {
        return queryListURI(owners, SMART_ENTITY_TYPES.LEGAL_ENTITY);
    }

    public static String queryTechnologyInUse(List<ExtractedEntity> technologies) throws Exception {
        return queryListURI(technologies, SMART_ENTITY_TYPES.TECHNOLOGY);
    }

    public static String queryIncome(List<ExtractedEntity> incomes) throws Exception {
        return queryListURI(incomes, SMART_ENTITY_TYPES.INCOME_SOURCE);
    }

    public static String queryPostHarvestingProcess(List<ExtractedEntity> processes) throws Exception {
        return queryListURI(processes, SMART_ENTITY_TYPES.POST_HARVESTING_PROCESS);
    }

    public static String queryRapresentativeForDecisionMaking(List<ExtractedEntity> representatives) throws Exception {
        return queryListURI(representatives, SMART_ENTITY_TYPES.AUTHORITY);
    }

    public static String queryManagementIndicators(List<ExtractedEntity> indicators) throws Exception {
        return queryListURI(indicators, SMART_ENTITY_TYPES.STATISTICAL_INDICATOR);
    }

    public static String queryFinancingManagement(List<ExtractedEntity> financingMgmt) throws Exception {
        return queryListURI(financingMgmt, SMART_ENTITY_TYPES.AUTHORITY);
    }

    public static String queryMarkets(List<ExtractedEntity> markets) throws Exception {
        return queryListURI(markets, SMART_ENTITY_TYPES.MARKET_PLACE);
    }

    public static String getText(String url) throws Exception {
        URL website = new URL(url);
        URLConnection connection = website.openConnection();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(
                        connection.getInputStream()));

        StringBuilder response = new StringBuilder();
        String inputLine;

        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }

        in.close();

        return response.toString();
    }

    public static abstract class QueryWrapperList {

        abstract public String doCall(List<ExtractedEntity> lst) throws Exception;
    }

    public static abstract class QueryWrapperSimple {

        abstract public String doCall(ExtractedEntity arg) throws Exception;
    }

    public static void enrichListField(Map<String, String> record, Map<String, String> enrichedRecord, Map<String, List<String>> uris, final String fieldName, QueryWrapperList wrapper) {
        logger.info("enriching field : " + fieldName + " with value : " + record.get(fieldName));

        long starttime = System.currentTimeMillis();
        try {
            String jsonURI = null;
            List<ExtractedEntity> stringList = new LinkedList<>();
            logger.info("ExtractedEntity will take : " + record.get(fieldName) + " | " + record.get(fieldName + "_fr"));
            
            if (record.get(fieldName) != null && record.get(fieldName + "_fr") != null && record.get(fieldName).trim().length() > 0) {
                stringList = ExtractorHelper.covertToStringList(record.get(fieldName), record.get(fieldName + "_fr"));
            } else if (record.get(fieldName) != null && record.get(fieldName).trim().length() > 0) {
                stringList = ExtractorHelper.covertToStringList(record.get(fieldName));
            }
            if (stringList.size() > 0) {
                jsonURI = wrapper.doCall(stringList);
                uris.put(fieldName + "_uris", ShortenCE4NameResponse.getURIFromJSON(jsonURI));
                enrichedRecord.put(fieldName + "_uris", jsonURI);
            }
        } catch (Exception e) {
            logger.warn("Error processing " + fieldName + " : " + record.get(fieldName), e);
            //handle exception
        }
        long endtime = System.currentTimeMillis();

        logger.info(
                "query " + fieldName + " : " + (endtime - starttime) / 1000.0 + " sec");
    }

    public static void enrichSimpleField(Map<String, String> record, Map<String, String> enrichedRecord, Map<String, List<String>> uris, final String fieldName, QueryWrapperSimple wrapper) {
        logger.info("enriching field : " + fieldName + " with value : " + record.get(fieldName));

        long starttime = System.currentTimeMillis();
        try {
            String jsonURI = null;
            if (record.get(fieldName) != null && record.get(fieldName).trim().length() > 0) {
            	logger.info("ExtractedEntity will take : " + record.get(fieldName) + " | " + record.get(fieldName + "_fr"));
                jsonURI = wrapper.doCall(new ExtractedEntity(record.get(fieldName), record.get(fieldName + "_fr")));
                uris.put(fieldName + "_uris", ShortenCE4NameResponse.getURIFromJSON(jsonURI));
                enrichedRecord.put(fieldName + "_uris", jsonURI);
            }
        } catch (Exception e) {
            logger.warn("Error processing " + fieldName + " : " + record.get(fieldName), e);
            //handle exception
        }
        long endtime = System.currentTimeMillis();
        logger.info("query " + fieldName + " : " + (endtime - starttime) / 1000.0 + " sec");
    }
//    public static void main(String[] args) throws Exception {
//
////		logger.info(queryCountry("Comoros"));
////		logger.info(queryCountry("Kenya"));
////		logger.info(querySpecies(Arrays.asList("Herrings", "sardines nei")));
////        String json = FLOD_EntityCollection.getInstance().getURI("Trolling Lines / Lignes de traîne", FLOD_CODE_TYPES.GEAR);
////        logger.info(json);
////        List<String> species = covertToStringList("Blacktip sardinella, Flathead grey mullet, Barnes' silverside, Stolephorus anchovies");
////    	logger.info(species);
//    	
//    	List<String> species = Lists.newArrayList("Stolephorus anchovies", "Commerson", "Tooth pony", "Largescaled therapon", "Dash-and-dot goatfish");
//    	 
//    	long start = System.currentTimeMillis();
//		logger.info(querySpecies(species));
//		long end = System.currentTimeMillis();
//		logger.info("duration : " +  (end - start) / 1000.0 + " secs");
//		
//    	start = System.currentTimeMillis();
//		logger.info(querySpecies(species));
//		end = System.currentTimeMillis();
//		logger.info("duration : " +  (end - start) / 1000.0 + " secs");
//
//		
//    	 start = System.currentTimeMillis();
//		logger.info(querySpecies(species));
//		 end = System.currentTimeMillis();
//		logger.info("duration : " +  (end - start) / 1000.0 + " secs");
//
//        //logger.info(queryVessel("Skipboat - Multipurpose Vessel"));
//		//logger.info(queryEnforcementMethod(covertToStringList("Marine Protected Areas officers, National Government Officers")));
//
//		//Enforcement
////		Marine Protected Areas officers
////		National Government Officers
////		logger.info(querySpecies("sardines nei"));
////		logger.info(querySpecies("Giant trevally"));
////		logger.info(querySpecies("Jack"));
//    }

    public static List<String> removeEmptyStringsFromList(List<String> lst) {
        return Lists.newArrayList(Iterables.filter(lst, new Predicate<String>() {

            @Override
            public boolean apply(String input) {
                return (input != null && input.trim().length() > 0);
            }

        }));
    }

}
