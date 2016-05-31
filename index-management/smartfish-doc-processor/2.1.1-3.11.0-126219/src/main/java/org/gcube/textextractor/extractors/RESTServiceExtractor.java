package org.gcube.textextractor.extractors;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.textextractor.entities.RestExtractorEntities.*;
import org.gcube.textextractor.helpers.ExtractorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import java.util.HashMap;
import org.gcube.textextractor.entities.ExtractedEntity;

public class RESTServiceExtractor extends InformationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(RESTServiceExtractor.class);

    private static Gson gson = new Gson();
    private static List<CountryObj> countries;

    @Override
    public String convertInfoToRowset(Map<String, String> info) {
        String documentID = info.get("documentID");
        info.remove("documentID");

        return ExtractorHelper.createRowseFromFields(documentID, collectionID, idxType, info.get("language"), info);
    }

    @Override
    public Map<String, String> extractFieldsFromFile(String filename)
            throws Exception {

        String docID = filename;
        Map<String, String> document = Maps.newHashMap();
        document.put("country", getCountryName(filename));
        document.put("provenance", "Statbase");
        document.put("title", getCountryName(filename));
        try {
            List<IntermediateCriterionObj> intermediateCriteria = getCriteria(filename);
            includeCriteriaInDocument(intermediateCriteria, document, docID);
            List<IntermediateStatisticObj> intermediateStatistics = getStatistics(filename);
            includeStatisticsInDocument(intermediateStatistics, document);
        } catch (Exception e) {
            logger.error("error while getting the criteria for : " + filename, e);
            return document;
        }

        return document;
    }

    @Override
    public Map<String, String> enrichRecord(Map<String, String> record,
            String filename) {

        Map<String, String> enrichedRecord = Maps.newHashMap();
        Map<String, List<String>> uris = Maps.newHashMap();

        String docURI = filename;
        enrichedRecord.put("documentID", docURI);
        enrichedRecord.putAll(record);

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.querySpecies(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.STATISTICS), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryStatistics(arg);
            }
        });

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR), new ExtractorHelper.QueryWrapperSimple() {
            @Override
            public String doCall(ExtractedEntity arg) throws Exception {
                return ExtractorHelper.queryGear(arg);
            }
        });

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), new ExtractorHelper.QueryWrapperSimple() {
            @Override
            public String doCall(ExtractedEntity arg) throws Exception {
                return ExtractorHelper.queryCountry(arg);
            }
        });

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL), new ExtractorHelper.QueryWrapperSimple() {
            @Override
            public String doCall(ExtractedEntity arg) throws Exception {
                return ExtractorHelper.queryVessel(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryYear(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.WATER_AREA), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryWaterArea(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.LAND_AREA), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryLandArea(arg);
            }
        });

        try {
            annotate(docURI, uris);
        } catch (Exception ex) {
            logger.error("file : " + filename + " not found", ex);
        }

        return enrichedRecord;
    }

    static List<String> getDocumentUrls(String path) throws Exception {
        List<String> documentUrls = Lists.newArrayList();

        countries = getCountries(path);

        for (CountryObj country : countries) {
            if (Integer.parseInt(country.id) < 11 || Integer.parseInt(country.id) > 15) {
                try {
                    String tableUrl = createUrlForTablesQueries(path, country.id);
                    logger.debug("getting tables for url : " + tableUrl);
                    List<String> tablesIds = getTablesIds(tableUrl);
                    for (String tableId : tablesIds) {
                        logger.debug("tables : " + tablesIds);
                        String tableDataUrl = createUrlForTableDataQueries(path, country.id, tableId);
                        logger.debug("tableID : " + tableDataUrl);

                        documentUrls.add(tableDataUrl);
                    }
                } catch (Exception e) {

                }
            }
        }

        return documentUrls;
    }

    @Override
    public List<Map<String, String>> extractInfo(String path)
            throws FileNotFoundException {

        int cnt = 0;

        List<String> documentUrls;
        try {
            documentUrls = getDocumentUrls(path);
        } catch (Exception e) {
            return Lists.newArrayList();
        }

        List<Map<String, String>> extractedInfo = Lists.newArrayList();;

        for (String documentUrl : documentUrls) {
            logger.info("Processing documentUrl : " + (++cnt) + " " + documentUrl);
            try {
                Map<String, String> info = this.extractFieldsFromFile(documentUrl);

                long part_start_time = System.currentTimeMillis();
                Map<String, String> enriched = enrichRecord(info, documentUrl);
                long part_end_time = System.currentTimeMillis();

                logger.info("~> field enrichment time  : " + (part_end_time - part_start_time) / 1000.0 + " secs");
                extractedInfo.add(enriched);
            } catch (Exception e) {
                logger.error("error while extracting info from : " + documentUrl + " . will skip this documentUrl", e);
            }
        }

        return extractedInfo;
    }

    static List<CountryObj> getCountries(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse;
        httpResponse = client.execute(request);
        try (InputStream responseStream = httpResponse.getEntity().getContent()) {

            Map<String, List<CountryObj>> resp = gson.fromJson(new InputStreamReader(responseStream),
                    new TypeToken<Map<String, List<CountryObj>>>() {
                    }.getType());

            List<CountryObj> countries = resp.get("countries");

            
            return countries;
        } catch (Exception e) {
            throw e;
        }
    }

    static List<IntermediateCriterionObj> getCriteria(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse;
        httpResponse = client.execute(request);
        try (InputStream responseStream = httpResponse.getEntity().getContent()) {

            TableDataObj resp = gson.fromJson(new InputStreamReader(responseStream),
                    new TypeToken<TableDataObj>() {
                    }.getType());

            List<IntermediateCriterionObj> retrievedObjs = resp.criteria;

            return retrievedObjs;
        } catch (Exception e) {
            throw e;
        }
    }

    static CriterionObj getCriterionValues(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse;
        httpResponse = client.execute(request);
        try (InputStream responseStream = httpResponse.getEntity().getContent()) {

            CriterionObj resp = gson.fromJson(new InputStreamReader(responseStream),
                    new TypeToken<CriterionObj>() {
                    }.getType());

            return resp;
        } catch (Exception e) {
            throw e;
        }
    }

    static List<String> getCountriesIds(String url) throws Exception {
        List<CountryObj> countries = getCountries(url);

        List<String> ids = Lists.newArrayList();
        for (CountryObj country : countries) {
            ids.add(String.valueOf(country.id));
        }

        return ids;
    }

    static String createUrlForTablesQueries(String baseUrl, String id) {
        return baseUrl + "/" + id;
    }

    static List<String> createUrlsForTablesQueries(String baseUrl, List<String> ids) {
        List<String> urls = Lists.newArrayList();

        for (String id : ids) {
            urls.add(baseUrl + "/" + id);
        }

        return urls;
    }

    static String createUrlForCriteriaQueries(String baseUrl, String countryID, String tableID, String criterionId) {
        return baseUrl + "/" + countryID + "/tables/" + tableID + "/criteria/" + criterionId;
    }

    static String createUrlForTableDataQueries(String baseUrl, String countryID, String tableID) {
        return baseUrl + "/" + countryID + "/tables/" + tableID;
    }

    static List<String> createUrlsForTableDataQueries(String baseUrl, String countryID, List<String> ids) {
        List<String> urls = Lists.newArrayList();

        for (String id : ids) {
            urls.add(baseUrl + "/" + countryID + "/tables/" + id);
        }

        return urls;
    }

    static List<String> getTablesIds(String url) throws Exception {

        List<String> listings = new ArrayList<String>();

        HttpClient client = new DefaultHttpClient();
        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse;
        httpResponse = client.execute(request);
        try (InputStream responseStream = httpResponse.getEntity().getContent()) {

            Map<String, Object> resp = gson.fromJson(new InputStreamReader(responseStream),
                    new TypeToken<Map<String, Object>>() {
                    }.getType());

            @SuppressWarnings("unchecked")
            List<Map<String, Object>> listingObjs = (List<Map<String, Object>>) resp.get("listing");

            for (Map<String, Object> listing : listingObjs) {
                Double idStr = (double) listing.get("id");
                Integer id = idStr.intValue();

                listings.add(String.valueOf(id));
            }

            return listings;
        } catch (Exception e) {
            throw e;
        }

    }

    public static void main(String[] args) throws Exception {
        RESTServiceExtractor ex = new RESTServiceExtractor();
        Map<String, ValueObj> listAllCountriesOnce = ex.listAllCountriesOnce();
        Set<Map.Entry<String, ValueObj>> entrySet = listAllCountriesOnce.entrySet();
        for (Map.Entry<String, ValueObj> entry : entrySet) {
            System.out.println("country id : " + entry.getValue().id + " country en : " + entry.getValue().enLabel + " country fr : " + entry.getValue().frLabel);
        }
//		System.out.println(ex.extractFieldsFromFile("http://statbase.pirogprod.info:8080/statbase/rest/country/3/tables/324"));
//		String path = "http://statbase.pirogprod.info:8080/statbase/rest/country";
//		
//		List<String> countriesIds = getCountriesIds(path);
//		
//		//List<String> urls = createUrlsForTablesQueries(path, countriesIds);
//		
//		for (String countryId : countriesIds){
//			try {
//				String tableUrl = createUrlForTablesQueries(path, countryId);
//				System.out.println("getting tables for url : " + tableUrl);
//				List<String> tablesIds = getTablesIds(tableUrl);
//				for (String tableId : tablesIds){
//					System.out.println("tables : " + tablesIds);
//					String tableDataUrl = createUrlForTableDataQueries(path, countryId, tableId);
//					System.out.println("tableID : " + tableDataUrl);
//
//					
//					String docID = tableDataUrl;
//					
//					List<IntermediateCriterionObj> intermediateCriteria  = getCriteria(tableDataUrl);
//					for (IntermediateCriterionObj intermediateCriterion : intermediateCriteria){
//						
//						Map<String, String> document = Maps.newHashMap();
//						
//						String fieldNamePrefix = kebabCaseToSnakeCase(intermediateCriterion.criterionName);
//						
//						String criterionUrl = createUrlForCriteriaQueries(path, countryId, tableId, String.valueOf(intermediateCriterion.criterionId));
//						
//						CriterionObj criterion = getCriterionValues(criterionUrl);
//
//						
//						for (ValueObj value : criterion.values){
//							
//							String frLabelName = fieldNamePrefix + "_" + "frLabel";
//							String frLabelValue = value.frLabel;
//							
//							String enLabelName = fieldNamePrefix + "_" + "enLabel";
//							String enLabelValue = value.enLabel;
//							
//							String enDescriptionName = fieldNamePrefix + "_" + "enDescription";
//							String enDescriptionValue = value.enDescription;
//							
//							String frDescriptionName = fieldNamePrefix + "_" + "frDescription";
//							String frDescriptionValue = value.frDescription;
//
//							
//							document.put("documentID", docID);
//							
//							document.put(enLabelName, enLabelValue);
//							document.put(frLabelName, frLabelValue);
//							
//							document.put(enDescriptionName, enDescriptionValue);
//							document.put(frDescriptionName, frDescriptionValue);
//							
//						}
//
//
//						
//						System.out.println(criterion.values);
//						
//						System.out.println("doc : " + document);
//					}
//
//					
//				}
//
//
//				
//			} catch (Exception e) {
//				e.printStackTrace();
//			}
//		}
    }

    private void annotate(String filename, Map<String, List<String>> uris) throws FileNotFoundException {
        AnnotationBase annotator = AnnotationBase.getInstance();
        Set<Map.Entry<String, List<String>>> entrySet = uris.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_country(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_species(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_gear(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_vessel(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_year(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.WATER_AREA) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_water_area(filename, uri_);
                }
            }

            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.STATISTICS) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_statistics(filename, uri_);
                }
            }
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.LAND_AREA) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.STATBASE_land_area(filename, uri_);
                }
            }
        }
    }

    static String kebabCaseToCamelCase(String str) {
        str = str.replace(" ", "-");

        List<String> w = Splitter.on("-").splitToList(str.toLowerCase());
        StringBuilder camelCase = new StringBuilder();

        camelCase.append(w.get(0));
        for (int i = 1; i < w.size(); ++i) {
            camelCase.append(capitalizeFirst(w.get(i)));
        }

        return camelCase.toString();
    }

    static String kebabCaseToSnakeCase(String str) {
        str = str.toLowerCase().replace(" ", "-");

        return str;
    }

    static String capitalizeFirst(String input) {
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    private static Map<String, ValueObj> listAllCountriesOnce() throws Exception {
        HashMap<String, ValueObj> hashMap = new HashMap<String, ValueObj>();
        String path = "http://statbase.pirogprod.info:8080/statbase/rest/country";
        List<String> countriesIds = getCountriesIds(path);

        for (String countryId : countriesIds) {

            String tableUrl = createUrlForTablesQueries(path, countryId);
            logger.debug("getting tables for url : " + tableUrl);
            List<String> tablesIds = getTablesIds(tableUrl);
            for (String tableId : tablesIds) {

                logger.debug("tables : " + tablesIds);
                String tableDataUrl = createUrlForTableDataQueries(path, countryId, tableId);
                logger.debug("tableID : " + tableDataUrl);

                String countryCriteriaInTable = createUrlForCriteriaQueries(path, countryId, tableId, "country");
                try {
                    CriterionObj criterionValues = getCriterionValues(countryCriteriaInTable);

                    List<ValueObj> countries = criterionValues.values;
                    for (ValueObj country : countries) {
                        if (!hashMap.containsKey(country.id.toString())) {
                            hashMap.put(country.id.toString(), country);
                        }
                    }
//                                            }
                } catch (Exception exception) {
                    System.out.println("does not contain country : " + countryCriteriaInTable);
                }

            }
        }
        return hashMap;
    }

    private String getCountryName(String filename) {
        String id = filename.replace("http://statbase.smartfish.d4science.org/statbase/rest/country/", "");
        id = id.split("/")[0];
        for (CountryObj countryObj : countries) {
            if (countryObj.id.equals(id)) {
                return countryObj.label;
            }
        }
        return "";
    }

    private void includeCriteriaInDocument(List<IntermediateCriterionObj> intermediateCriteria, Map<String, String> document, String docID) {
        for (IntermediateCriterionObj intermediateCriterion : intermediateCriteria) {

            String fieldNamePrefix = null;

            logger.debug("-----> " + intermediateCriterion.criterionName);

            if (intermediateCriterion.criterionName.equalsIgnoreCase("SPECIES")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("especes")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("espece")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES);
            } else if (intermediateCriterion.criterionName.equalsIgnoreCase("GEARS")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("GEAR")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("GEAR 1")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("GEARS 1")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("FISHING GEARS")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR);
            } else if (intermediateCriterion.criterionName.equalsIgnoreCase("TIME")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("YEAR")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR);
            } else if (intermediateCriterion.criterionName.equalsIgnoreCase("BOAT")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("BOATS")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("VESSEL")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("VESSELS")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL);
            } else if (intermediateCriterion.criterionName.equalsIgnoreCase("MAR-ZONE")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("MAR-ZONE 1")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("MAR-ZONE 2")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.WATER_AREA);
            } else if (intermediateCriterion.criterionName.equalsIgnoreCase("TER-ZONE")
                    || intermediateCriterion.criterionName.equalsIgnoreCase("TER-ZONE 1")) {
                fieldNamePrefix = ANNOTATIONS.getLocalName(ANNOTATIONS.LAND_AREA);
            }
//				
            if (fieldNamePrefix == null) {
                logger.warn("####### no field matched for : " + intermediateCriterion.criterionName);
                continue;
            }
            logger.info("####### field matched for : " + fieldNamePrefix);
            //String fieldNamePrefix = kebabCaseToSnakeCase(intermediateCriterion.criterionName);

            String criterionUrl = docID + "/criteria/" + String.valueOf(intermediateCriterion.criterionId);
            document.put("documentID", docID);

            try {
                CriterionObj criterion = getCriterionValues(criterionUrl);

                List<String> engValuesList = Lists.newArrayList();
                List<String> frValuesList = Lists.newArrayList();

                for (ValueObj value : criterion.values) {

                    String frLabelName = fieldNamePrefix + "_" + "_fr";
                    String frLabelValue = value.frLabel;//  == null ? "" : value.frLabel;

                    String enLabelName = fieldNamePrefix;// + "_" + "enLabel";
                    String enLabelValue = value.enLabel;// == null ? "" : value.enLabel;

                    String enDescriptionName = fieldNamePrefix + "_" + "enDescription";
                    String enDescriptionValue = value.enDescription;// == null ? "" : value.enDescription;

                    String frDescriptionName = fieldNamePrefix + "_" + "frDescription";
                    String frDescriptionValue = value.frDescription;//  == null ? "" : value.frDescription;

//						document.put(enLabelName, enLabelValue);
//						document.put(frLabelName, frLabelValue);
//						
//						document.put(enDescriptionName, enDescriptionValue);
//						document.put(frDescriptionName, frDescriptionValue);
                    List<String> engValues = ExtractorHelper.removeEmptyStringsFromList(Lists.newArrayList(enLabelValue, enDescriptionValue));

                    List<String> frValues = ExtractorHelper.removeEmptyStringsFromList(Lists.newArrayList(frLabelValue, frDescriptionValue));

                    engValuesList.addAll(engValues);
                    frValuesList.addAll(frValues);

                }

                String engValuesStr = Joiner.on(", ").skipNulls().join(engValuesList);

                logger.debug("will insert eng values : |" + engValuesStr + "|" + " produced by : |" + engValuesList + "|");

                //logger.info("will add value to : " + fieldNamePrefix);
                if (engValuesStr != null && engValuesStr.trim().length() > 0) {
                    document.put(fieldNamePrefix, engValuesStr);
                }

                String frValuesStr = Joiner.on(", ").skipNulls().join(frValuesList);
                if (frValuesStr != null && frValuesStr.trim().length() > 0) {
                    document.put(fieldNamePrefix + "_fr", frValuesStr);
                }

                logger.info("criterion values : " + criterion.values);
                logger.info("doc : " + document);
            } catch (Exception e1) {
                logger.error("error while getting criteriaValues for : " + criterionUrl, e1);
            }
        }
    }

    private void includeStatisticsInDocument(List<IntermediateStatisticObj> intermediateStatistics, Map<String, String> document) {
        ArrayList<String> stat_en = new ArrayList<String>();
        ArrayList<String> stat_fr = new ArrayList<String>();
        for (IntermediateStatisticObj intermediateStatisticObj : intermediateStatistics) {
            String enLabel = intermediateStatisticObj.name;
            String frLabel = intermediateStatisticObj.name;
            if (enLabel.equalsIgnoreCase("Artisanal catch")
                    || enLabel.equalsIgnoreCase("capture")
                    || enLabel.equalsIgnoreCase("Captures")
                    || enLabel.equalsIgnoreCase("Catch")
                    || enLabel.equalsIgnoreCase("Catches")) {
                enLabel = "Catches";
                frLabel = "Captures";
            } else if (enLabel.equalsIgnoreCase("EFF-FISH-TIME")
                    || enLabel.equalsIgnoreCase("EFF-OP")
                    || enLabel.equalsIgnoreCase("EFF-VESS")
                    || enLabel.equalsIgnoreCase("EFFORT")) {
                enLabel = "Effort";
                frLabel = "Effort";
            } else if (enLabel.equalsIgnoreCase("Number of vessels")
                    || enLabel.equalsIgnoreCase("Number-Boats")
                    || enLabel.equalsIgnoreCase("NOMBRE DE NAVIRE")
                    || enLabel.equalsIgnoreCase("NB NAVIRES")
                    || enLabel.equalsIgnoreCase("NB NAVIRE")) {
                enLabel = "Number of vessels";
                frLabel = "Nombre de navires";
            } else if (enLabel.equalsIgnoreCase("Flottille")) {
                enLabel = "Fleet";
                frLabel = "Flottille";
            } else if (enLabel.equalsIgnoreCase("Marine Species")) {
                enLabel = "Marine Species";
                frLabel = "Espèces marines";
            } else if (enLabel.equalsIgnoreCase("Days at sea")
                    || enLabel.equalsIgnoreCase("NB JMER ")
                    || enLabel.equalsIgnoreCase("NB JPECH")
                    || enLabel.equalsIgnoreCase("NB JOUR DE PECHE")) {
                enLabel = "Days at sea";
                frLabel = "Number of Jour en mer";
            } else {
                continue;
            }
            stat_en.add(enLabel);
            stat_fr.add(frLabel);
        }
        if (stat_en.size() > 0) {
            document.put(ANNOTATIONS.getLocalName(ANNOTATIONS.STATISTICS), Joiner.on(",").skipNulls().join(stat_en));
            document.put(ANNOTATIONS.getLocalName(ANNOTATIONS.STATISTICS) + "_fr", Joiner.on(",").skipNulls().join(stat_fr));
        }
    }

    private List<IntermediateStatisticObj> getStatistics(String url) throws Exception {
        HttpClient client = new DefaultHttpClient();

        HttpGet request = new HttpGet(url);

        HttpResponse httpResponse;
        httpResponse = client.execute(request);
        try (InputStream responseStream = httpResponse.getEntity().getContent()) {

            TableDataObj resp = gson.fromJson(new InputStreamReader(responseStream),
                    new TypeToken<TableDataObj>() {
                    }.getType());

            List<IntermediateStatisticObj> retrievedObjs = resp.statistics;

            return retrievedObjs;
        } catch (Exception e) {
            throw e;
        }
    }
}
