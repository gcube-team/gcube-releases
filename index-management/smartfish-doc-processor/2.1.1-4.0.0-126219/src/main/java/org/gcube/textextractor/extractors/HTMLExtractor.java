package org.gcube.textextractor.extractors;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FilenameUtils;
import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.html.HtmlParser;
import org.apache.tika.sax.BodyContentHandler;
import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.textextractor.entities.ExtractedEntity;
import org.gcube.textextractor.entities.ShortenCE4NameResponse;
import org.gcube.textextractor.helpers.ExtractorHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.ContentHandler;

public class HTMLExtractor extends InformationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(HTMLExtractor.class);

    public static void main(String[] args) throws Exception {

        HTMLExtractor ex = new HTMLExtractor();
        System.out.println(ex.extractFieldsFromFile("/home/alex/Downloads/271.html"));

    }

    private static void makeMeaureField(Map<String, String> fields) {

        String measure = "";
        String measure_fr = "";

        String fc = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL));
        String ac = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL));
        String em = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD));

        String fc_fr = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL + "_fr"));
        String ac_fr = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL + "_fr"));
        String em_fr = fields.get(ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD + "_fr"));

        try {
            fc.isEmpty();
            measure += fc + ",";
        } catch (Exception e) {
        }
        try {
            ac.isEmpty();
            measure += ac + ",";
        } catch (Exception e) {
        }
        try {
            em.isEmpty();
            measure += em;
        } catch (Exception e) {
        }

        try {
            fc_fr.isEmpty();
            measure_fr += fc_fr + ",";
        } catch (Exception e) {
        }
        try {
            ac_fr.isEmpty();
            measure_fr += ac_fr + ",";
        } catch (Exception e) {
        }
        try {
            em_fr.isEmpty();
            measure_fr += em_fr;
        } catch (Exception e) {
        }
        if (!measure.isEmpty()) {
            fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.MEASURE), measure);
        }
        if (!measure_fr.isEmpty()) {
            fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.MEASURE + "_fr"), measure_fr);
        }

    }

    public HTMLExtractor() {
    }

    
    @Override
    public Map<String, String> extractFieldsFromFile(String filename) throws Exception {
        logger.info("Processing file : " + filename);
        long starttime = System.currentTimeMillis();
        try {
            InputStream input = new FileInputStream(filename);
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_TYPE, "text/html; charset=utf-8");
            new HtmlParser().parse(input, handler, metadata,
                    new ParseContext());
            
            String text = ExtractorHelper.removeEmptyLines(handler
                    .toString());
            Map<String, String> info = new HashMap<String, String>();

            info.put("documentID", filename);
            info.put("text", text);
            info.put("title", metadata.get("title"));
            info.put("language", new LanguageIdentifier(text).getLanguage());

            info.put("provenance", "WIOFish");

            // extract custom fields
            long part_start_time = System.currentTimeMillis();
            Map<String, String> fields = customFields(filename);
            long part_end_time = System.currentTimeMillis();
            info.putAll(fields);

            logger.info("~> field extraction time  : " + (part_end_time - part_start_time) / 1000.0 + " secs");

            return info;

        } catch (Exception e) {
            logger.error("error while extracting fields from  : " + filename, e);
            throw e;
        } finally {
            long endtime = System.currentTimeMillis();
            logger.info("time processing file : " + filename + " : " + (endtime - starttime) / 1000.0 + " secs");
        }
    }

    @Override
    public List<Map<String, String>> extractInfo(String path) throws FileNotFoundException {
        List<Map<String, String>> extractedInfo = new ArrayList<Map<String, String>>();

        int cnt = 0;
        List<String> filenames = ExtractorHelper.getFilenames(path);

        for (String filename : filenames) {
            logger.info("Processing file : " + (++cnt) + " " + filename);
            try {
                Map<String, String> info = this.extractFieldsFromFile(filename);

                long part_start_time = System.currentTimeMillis();
                Map<String, String> enriched = enrichRecord(info, filename);
                long part_end_time = System.currentTimeMillis();

                logger.info("~> field enrichment time  : " + (part_end_time - part_start_time) / 1000.0 + " secs");
                extractedInfo.add(enriched);

            } catch (Exception e) {
                logger.error("error while extracting info from : " + filename + " . will skip this file", e);
            }
        }
        return extractedInfo;
    }

    /*
     void extractInfoAndWrite(String outputFilename) throws IOException {

     int cnt = 0;
     FileWriter fw = new FileWriter(new File(outputFilename), true);
     for (String filename : getFilenames()) {
     logger.info("Processing file : " + (++cnt) + " " + filename);
     try {
     InputStream input = new FileInputStream(filename);
     ContentHandler handler = new BodyContentHandler();
     Metadata metadata = new Metadata();
     new HtmlParser().parse(input, handler, metadata,
     new ParseContext());

     String text = ExtractorHelper.removeEmptyLines(handler
     .toString());
     Map<String, String> info = new HashMap<String, String>();

     info.put("documentID", filename);
     info.put("text", text);
     info.put("title", metadata.get("title"));
     info.put("language", new LanguageIdentifier(text).getLanguage());

     info.put("provenance", "WIOFish");

     // extract custom fields
     info.putAll(customFields(filename));

     Map<String, String> enriched = enrichRecord(info, filename);
     //extractedInfo.add(enriched);
                

     String rowset = createCustomRowset(enriched);
     fw.write(rowset);

     } catch (Exception e) {
     e.printStackTrace();
     }
     //            if (cnt % 10 == 0) {
     //                AnnotationBase.getInstance().toFile();
     //            }
     }
     fw.flush();
     fw.close();
     }*/
    public Map<String, String> enrichRecord(Map<String, String> record, String filename) {
        Map<String, String> enrichedRecord = new HashMap<String, String>();
        Map<String, List<String>> uris = new HashMap<String, List<String>>();

        String docName = FilenameUtils.getName(filename);
        docName = docName.substring(docName.lastIndexOf("=") + 1).toLowerCase();
        String docURI = "http://smartfish.collection/wiofish/" + docName.toLowerCase();
        enrichedRecord.putAll(record);

        enrichedRecord.put("documentID", docURI); //override previous value

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), new ExtractorHelper.QueryWrapperSimple() {
            @Override
            public String doCall(ExtractedEntity arg) throws Exception {
                return ExtractorHelper.queryCountry(arg);
            }
        });

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR)/*"gear_used"*/, new ExtractorHelper.QueryWrapperSimple() {
                    @Override
                    public String doCall(ExtractedEntity arg) throws Exception {
                        return ExtractorHelper.queryGear(arg);
                    }
                });

        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL) /*"type_of_vessel"*/, new ExtractorHelper.QueryWrapperSimple() {
                    public String doCall(ExtractedEntity arg) throws Exception {
                        return ExtractorHelper.queryVessel(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT) /*"management"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryManagement(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS) /*"exploitation_status"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryExploitationStatus(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL) /*"access_control"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryAccessControl(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL) /*"fishing_control"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryFishingControl(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD) /*"enforcement_method"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryEnforcementMethod(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.MEASURE) /*"measure"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryConservationMeasure(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.SECTOR) /*"sector"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.querySector(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.TECHNOLOGY_IN_USE), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryTechnologyInUse(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.OWNER_OF_ACCESS_RIGHT) /*"ownership_of_access_right"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryOwnershipOfAccessRight(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.OTHER_INCOME_SOURCE) /*"alternative_income"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryIncome(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.MARKET) /*"markets"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryMarkets(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.SEASONALITY) /*"seasons"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.querySeasonality(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.POST_PROCESSING_METHOD) /*"post_harvesting_processing"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryPostHarvestingProcess(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.DECISION_MAKER)/*"representatives_in_decision_making"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryRapresentativeForDecisionMaking(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT_INDICATOR) /*"management_indicator"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryManagementIndicators(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.FINANCE_MGT_AUTHORITY) /* "financing_management" */, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryFinancingManagement(arg);
                    }
                });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.APPLICANT_FOR_ACCESS_RIGHT) /* "APPLICANT ACCESS RIGHT" */, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryAccessRightApplicant(arg);
                    }
                });

        long starttime, endtime;
//        starttime = System.currentTimeMillis();
//        try {
//            String countryURIJson = null;
//            if (record.get("country") != null) {
//                countryURIJson = ExtractorHelper.queryCountry(record.get("country"));
//                uris.put("country_uris", ShortenCE4NameResponse.getURIFromJSON(countryURIJson));
//                enrichedRecord.put("country_uris", countryURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing country : " + record.get("country"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query country dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//
//            String gearURIJson = null;
//            if (record.get("gear_used") != null) {
//                gearURIJson = ExtractorHelper.queryGear(record.get("gear_used"));
//                uris.put("gear_uris", ShortenCE4NameResponse.getURIFromJSON(gearURIJson));
//                enrichedRecord.put("gear_uris", gearURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing gear : " + record.get("gear_used"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query gear used dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String vesselURIJson = null;
//            if (record.get("type_of_vessel") != null) {
//                vesselURIJson = ExtractorHelper.queryVessel(record.get("type_of_vessel"));
//                uris.put("vessel_uris", ShortenCE4NameResponse.getURIFromJSON(vesselURIJson));
//                enrichedRecord.put("vessel_uris", vesselURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing type_of_vessel : " + record.get("type_of_vessel"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query type of vessel dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String managementURIJson = null;
//            if (record.get("management") != null) {
//                managementURIJson = ExtractorHelper.queryManagement(ExtractorHelper.covertToStringList(record.get("management")));
//                uris.put("management_uris", ShortenCE4NameResponse.getURIFromJSON(managementURIJson));
//                enrichedRecord.put("management_uris", managementURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing management : " + record.get("management"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query management dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String exploitationStatusURIJson = null;
//            if (record.get("exploitation_status") != null) {
//                exploitationStatusURIJson = ExtractorHelper.queryExploitationStatus(ExtractorHelper.covertToStringList(record.get("exploitation_status")));
//                uris.put("exploitation_status_uris", ShortenCE4NameResponse.getURIFromJSON(exploitationStatusURIJson));
//                enrichedRecord.put("exploitation_status_uris", exploitationStatusURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing exploitation_status : " + record.get("exploitation_status"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query exploitation status dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String accessControlURIJson = null;
//            if (record.get("access_control") != null) {
//                accessControlURIJson = ExtractorHelper.queryAccessControl(ExtractorHelper.covertToStringList(record.get("access_control")));
//                uris.put("access_control_uris", ShortenCE4NameResponse.getURIFromJSON(accessControlURIJson));
//                enrichedRecord.put("access_control_uris", accessControlURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing access_controls_used_in_management_eng : " + record.get("access_controls_used_in_management_eng"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query access control dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String fishingControlURIJson = null;
//            if (record.get("fishing_control") != null) {
//                fishingControlURIJson = ExtractorHelper.queryFishingControl(ExtractorHelper.covertToStringList(record.get("fishing_control")));
//                uris.put("fishing_control_uris", ShortenCE4NameResponse.getURIFromJSON(fishingControlURIJson));
//                enrichedRecord.put("fishing_control_uris", fishingControlURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing fishing_controls_used_in_management_eng : " + record.get("fishing_controls_used_in_management_eng"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query fishing control dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String enforcementMethodURIJson = null;
//            if (record.get("enforcement_method") != null) {
//                enforcementMethodURIJson = ExtractorHelper.queryEnforcementMethod(ExtractorHelper.covertToStringList(record.get("enforcement_method")));
//                uris.put("enforcement_method_uris", ShortenCE4NameResponse.getURIFromJSON(enforcementMethodURIJson));
//                enrichedRecord.put("enforcement_method_uris", enforcementMethodURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing enforcement_method : " + record.get("enforcement_method"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query enforcement method dur : " + (endtime - starttime) /1000.0 + " sec");
//
//        starttime = System.currentTimeMillis();
//        try {
//            String sectorsURIJson = null;
//            if (record.get("sector") != null) {
//                sectorsURIJson = ExtractorHelper.querySector(ExtractorHelper.covertToStringList(record.get("sector")));
//                uris.put("sector_uris", ShortenCE4NameResponse.getURIFromJSON(sectorsURIJson));
//                enrichedRecord.put("sector_uris", sectorsURIJson);
//            }
//        } catch (Exception e) {
//           logger.info("Error processing sector : " + record.get("sector"));
//            e.printStackTrace();
//            //handle exception
//        }
//        endtime = System.currentTimeMillis();
//       logger.info("query sector dur : " + (endtime - starttime) /1000.0 + " sec");

        //access_controls_used_in_management_eng
        try {
            String html = null;
            try {
                html = ExtractorHelper.fileContent(filename);
                //System.out.println(html);
            } catch (IOException e) {
                logger.error("error while getting html contents");
                return null;

            }

            List<ExtractedEntity> targetSpecies = new ArrayList<ExtractedEntity>();
            List<ExtractedEntity> bycatchSpecies = new ArrayList<ExtractedEntity>();
            List<ExtractedEntity> discardSpecies = new ArrayList<ExtractedEntity>();
            List<ExtractedEntity> threatenedSpecies = new ArrayList<ExtractedEntity>();

            starttime = System.currentTimeMillis();
            if (record.get("species_english_name") != null && record.get("species_english_name").trim().length() > 0) {
                Pattern speciesRowPattern = Pattern
                        .compile("<tr id='species_row_.*?'>.*?EnglishNameblock.*?>(.*?)</td>(.*?)</tr>");
                Matcher speciedRowMatcher = speciesRowPattern.matcher(html);

                List<ExtractedEntity> allSpecies = ExtractorHelper.covertToStringList(record.get("species_english_name"));

                while (speciedRowMatcher.find()) {
                    String speciesRow = speciedRowMatcher.group(1).trim();
                    String speciesRowRest = speciedRowMatcher.group(2).trim();

                    if (speciesRow.length() == 0) {
                        continue;
                    }
                    //System.out.println("species row " + i + " " + speciesRow + " , " + speciesRowRest);

                    Pattern p2 = Pattern
                            .compile("<td align='center'>.*?</td><td align='center'>.*?</td><td align='center'>(.*?)<br></td>.*?<img src=images/(.*?).gif></td><td width=33% align='center'><img src=images/(.*?).gif></td><td width=33% align='center'><img src=images/(.*?).gif></td>");
                    Matcher m2 = p2.matcher(speciesRowRest);

                    if (m2.find()) {
                        //System.out.println(m2.group(1) + " , " + m2.group(2) + " , " + m2.group(3) + " , " + m2.group(4));

                        for (String species : speciesRow.split("\\s*,\\s*")) {
                            species = species.trim();
                            if (!containsSpecies(allSpecies, species)) {
                                System.out.println("Error : " + species + " not in allSpecies : " + speciesRow);
                                throw new Exception("Error : " + species + " not in allSpecies : " + speciesRow);
                            }

                            String type;
                            if (!m2.group(1).trim().equalsIgnoreCase("Not Applicable")) {
                                type = "threatened";
                                threatenedSpecies.add(new ExtractedEntity(species, ""));
                            }
                            if (m2.group(2).trim().equalsIgnoreCase("tick_blue")) {
                                type = "target";
                                targetSpecies.add(new ExtractedEntity(species, ""));
                            }
                            if (m2.group(3).trim().equalsIgnoreCase("tick_blue")) {
                                type = "by-catch";
                                bycatchSpecies.add(new ExtractedEntity(species, ""));
                            }
                            if (m2.group(4).trim().equalsIgnoreCase("tick_blue")) {
                                type = "discard";
                                discardSpecies.add(new ExtractedEntity(species, ""));
                            }
                        }
                    }

                }

//	           logger.info("threatenedSpecies : " + threatenedSpecies);
//	           logger.info("targetSpecies : " + targetSpecies);
//	           logger.info("bycatchSpecies : " + bycatchSpecies);
//	           logger.info("discardSpecies : " + discardSpecies);
                enrichedRecord.put(ANNOTATIONS.getLocalName(ANNOTATIONS.BYCATCH), Joiner.on(", ").join(bycatchSpecies));
                enrichedRecord.put(ANNOTATIONS.getLocalName(ANNOTATIONS.TARGET), Joiner.on(", ").join(targetSpecies));
                enrichedRecord.put(ANNOTATIONS.getLocalName(ANNOTATIONS.THRETENED), Joiner.on(", ").join(threatenedSpecies));
                enrichedRecord.put(ANNOTATIONS.getLocalName(ANNOTATIONS.DISCARD), Joiner.on(", ").join(discardSpecies));

                endtime = System.currentTimeMillis();
                logger.info("extracting threatened,target,by-catch,discard dur : " + (endtime - starttime) / 1000.0 + " sec");

                /*
                 starttime = System.currentTimeMillis();
                 if (record.get("species_english_name").trim().length() > 0) {
                 for (String species : record.get("species_english_name").split("\\s*,\\s*")) {

                 logger.info("checking : " + species);
                 String pat  = "<tr id='species_row_.*?'>.*?EnglishNameblock.*?>" + species + "*?<td align='center'>.*?</td><td align='center'>.*?</td><td align='center'>(.*?)<br></td>.*?<img src=images/(.*?).gif></td><td width=33% align='center'><img src=images/(.*?).gif></td><td width=33% align='center'><img src=images/(.*?).gif></td>.*?<tr>";
                 System.out.println(pat);
                	
                 Pattern p = Pattern
                 .compile(pat);
                 Matcher m = p.matcher(html);

                 String type = null;
                 if (m.find()) {
                 logger.info(m.group(1).trim());

                 if (!m.group(1).trim().equalsIgnoreCase("Not Applicable")) {
                 type = "threatened  ";
                 threatenedSpecies.add(species);
                 } 
                 if (m.group(2).trim().equalsIgnoreCase("tick_blue")) {
                 type = "target";
                 targetSpecies.add(species);
                 } 
                 if (m.group(3).trim().equalsIgnoreCase("tick_blue")) {
                 type = "by-catch";
                 bycatchSpecies.add(species);
                 } 
                 if (m.group(4).trim().equalsIgnoreCase("tick_blue")) {
                 type = "discard";
                 discardSpecies.add(species);
                 }

                 //System.out.println(m.group(1).trim() + " " + m.group(2).trim() + " " + m.group(3).trim());
                 //System.out.println("type : " + type);
                 }
                 }
                 endtime = System.currentTimeMillis();
                 logger.info("extracting threatened,target,by-catch,discard dur : " + (endtime - starttime) /1000.0 + " sec");
                 */
                starttime = System.currentTimeMillis();
                String speciesURIJson = null;
                speciesURIJson = ExtractorHelper.querySpecies(ExtractorHelper.covertToStringList(record.get("species_english_name")));
                //uris.put("species_uris", ShortenCE4NameResponse.getURIFromJSON(speciesURIJson));
                endtime = System.currentTimeMillis();
                logger.info("query species dur : " + (endtime - starttime) / 1000.0 + " sec");

                starttime = System.currentTimeMillis();
                if (threatenedSpecies.size() > 0) {
                    String threatenedSpeciesURIJson = ExtractorHelper.querySpecies(threatenedSpecies);
                    uris.put(ANNOTATIONS.getLocalName(ANNOTATIONS.THRETENED) + "_uris" /*"threatened_species" */, ShortenCE4NameResponse.getURIFromJSON(threatenedSpeciesURIJson));
                }
                endtime = System.currentTimeMillis();
                logger.info("query threatened species dur : " + (endtime - starttime) / 1000.0 + " sec");

                starttime = System.currentTimeMillis();
                if (targetSpecies.size() > 0) {
                    String targetSpeciesURIJson = ExtractorHelper.querySpecies(targetSpecies);
                    uris.put(ANNOTATIONS.getLocalName(ANNOTATIONS.TARGET) + "_uris" /*"target_species" */, ShortenCE4NameResponse.getURIFromJSON(targetSpeciesURIJson));
                }
                endtime = System.currentTimeMillis();
                logger.info("query target species dur : " + (endtime - starttime) / 1000.0 + " sec");

                starttime = System.currentTimeMillis();
                if (bycatchSpecies.size() > 0) {
                    String bycatchSpeciesURIJson = ExtractorHelper.querySpecies(bycatchSpecies);
                    uris.put(ANNOTATIONS.getLocalName(ANNOTATIONS.BYCATCH) + "_uris"/*"bycatch_species"*/, ShortenCE4NameResponse.getURIFromJSON(bycatchSpeciesURIJson));
                }
                endtime = System.currentTimeMillis();
                logger.info("query by catch species dur : " + (endtime - starttime) / 1000.0 + " sec");

                starttime = System.currentTimeMillis();
                if (discardSpecies.size() > 0) {
                    String discardSpeciesURIJson = ExtractorHelper.querySpecies(discardSpecies);
                    uris.put(ANNOTATIONS.getLocalName(ANNOTATIONS.DISCARD) + "_uris" /* "discard_species" */, ShortenCE4NameResponse.getURIFromJSON(discardSpeciesURIJson));
                }
                endtime = System.currentTimeMillis();
                logger.info("query discard species dur : " + (endtime - starttime) / 1000.0 + " sec");

                enrichedRecord.put(ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES) + "_uris"/* "species" */, speciesURIJson);

            }

        } catch (Exception e) {
            logger.error("Error processing species : " + record.get("species_english_name"), e);
        }
        try {
            //System.out.println(uris);

            //process those uris
            //add info to enrichedRecord
            starttime = System.currentTimeMillis();
            annotate(docURI, uris);
            endtime = System.currentTimeMillis();
            logger.info("annotate dur : " + (endtime - starttime) / 1000.0 + " sec");
        } catch (FileNotFoundException ex) {
            logger.error("file : " + filename + " not found", ex);
        }

        return enrichedRecord;
    }

    static Map<String, String> customFields(String filename) {
        Map<String, String> fields = new HashMap<String, String>();

        String html = null;
        try {
            html = ExtractorHelper.fileContent(filename);
            //System.out.println(html);
        } catch (IOException e) {
            logger.error("error while parsing the fields from : " + filename, e);
            return null;
        }

        Pattern p = null;
        Matcher m = null;

        parseSimpleRow(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY) /*"country"*/, "<b>Reporting Area: </b>(.*?)</font>", html, fields);

        parseSimpleRow("title", "<b>Fishery: </b>(.*?)</font>", html, fields);

        parseSimpleRow("fishery_local_name" /* "fishery_local_name" */, "<tr><td><b>Local name for this Fishery:</b></td><td>(.*?)</td></tr><tr>", html, fields);

        parseSimpleRow(ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL) /*"type_of_vessel"*/, "<tr><td><b>Type of vessel</b></td><td>(.*?)</td></tr>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.APPLICANT_FOR_ACCESS_RIGHT) /* "access_right_applicants" */, "<b>Who can apply for access rights</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.OWNER_OF_ACCESS_RIGHT) /* "ownership_of_access_right" */, "<b>Ownership of access right</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.OTHER_INCOME_SOURCE)/* "alternative_income" */, "<b>Alternative Incomes</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.MARKET) /*"markets"*/, "<b>Markets</b></td></tr><tr><td>(.*?)</td></tr>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.POST_PROCESSING_METHOD) /* "post_harvesting_processing" */, "<b>Post-harvest processing</b></td></tr><tr><td>(.*?)</td></tr>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.DECISION_MAKER) /* "representatives_in_decision_making" */, "<b>Representatives in decision making</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR) /* "gear_used" */, "<tr><td><b>Select gear used in this fishery</b></td><td>(.*?)<br></td></tr>", html, fields);
        //TODO: check if parseTable or parseRow

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS) /*"exploitation_status" */, "<b>FAO Status:</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT_INDICATOR) /*"management_indicator"*/, "<td><b>Management Indicators</b>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.FINANCE_MGT_AUTHORITY) /*"financing_management"*/, "<b>Financing management</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL) /*"access_control"*/, "<b>Access controls used in management</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL) /*"fishing_control"*/, "<b>Fishing controls used in management</b></td><td>(.*?)</td>", html, fields);

        parseTable(ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD) /*"enforcement_method"*/, "<b>Enforcement methods used</b></td><td>(.*?)</td>", html, fields);

        parseTickTableMultipleLangs(ANNOTATIONS.getLocalName(ANNOTATIONS.SECTOR) /*"sector"*/, "<b>Sector</b></td></tr><tr><td valign='top' colspan=2><table bgcolor='#eae8e8' class='table' cellspacing=1 width=100%><tr bgcolor='#ffffff'><td><table width=100% class='table' cellpadding=5><tr><td width=50% valign='top'>(.*?)</td><td valign='top' width=50%>", "<img src=images/tick_blue.gif>(.*?)<br>", html, fields);

        parseTickTableMultipleLangs(ANNOTATIONS.getLocalName(ANNOTATIONS.TECHNOLOGY_IN_USE), "<a name='technology'>Technology Used</a>.*?</table>(.*?)</table>", "<img src=images/tick_blue.gif>(.*?)<br>", html, fields);

        parseTickTableMultipleLangs(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT) /* "management" */, "<b>Management Type</b>(.*?)</tr>", "<img src=images/tick_blue.gif>(.*?)<br>", html, fields);

        makeMeaureField(fields);

        p = Pattern
                .compile("<tr id='dmtbl_row_0'><td>(.*?)</td><td>(.*?)<br></td>");
        m = p.matcher(html);
        if (m.find()) {
            //System.out.println("Select gear used in this fishery : "	+ m.group(1));
            String nameOfBody = m.group(1).trim();
            String bodyType = m.group(2).trim();
            String bodyTypeEng = bodyType.split("/")[0].trim();
            String bodyTypeFr = bodyType.split("/")[1].trim();

            if (bodyTypeEng != null && bodyTypeEng.trim().length() > 0) {
                fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.AUTHORITY) /* "authorities" */, Joiner.on(", ").join(nameOfBody, bodyTypeEng));
            }
        }

        p = Pattern
                .compile("<tr><td><b>Jan</b></td><td><b>Feb</b></td><td><b>Mar</b></td><td><b>Apr</b></td><td><b>May</b></td><td><b>Jun</b></td><td><b>Jul</b></td><td><b>Aug</b></td><td><b>Sep</b></td><td><b>Oct</b></td><td><b>Nov</b></td><td><b>Dec</b></td></tr><tr><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td><td>(.*?)</td></tr><tr>");
        m = p.matcher(html);
        if (m.find()) {
            String months[] = {"January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
            List<String> seasonality = Lists.newArrayList();

            for (int i = 1; i <= m.groupCount(); i++) {
                String img = m.group(1).trim();

                if (img.equalsIgnoreCase("<img src=images/tick_blue.gif>")) {
                    seasonality.add(months[i - 1]);
                }
                //System.out.println(seasonality);
            }
            if (seasonality.size() > 0) {
                fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.SEASONALITY) /*"seasonality" */, Joiner.on(", ").join(seasonality));
            }
        }

        p = Pattern
                .compile("<tr id='species_row_.*?'><td.*?>(.*?)</td>.*?EnglishNameblock.*?>(.*?)</td>");
        m = p.matcher(html);

        List<String> scientificNames = Lists.newArrayList();
        List<String> englishNames = Lists.newArrayList();

        while (m.find()) {
            //logger.info("*" + m.group(0));
            //System.out.println(m.group(1) + " - " + m.group(2));
            if (m.group(1).trim().length() > 0) {
                scientificNames.add(m.group(1).trim());
            }

            if (m.group(2).trim().length() > 0) {
                englishNames.add(m.group(2).trim());
            }

        }

        if (scientificNames.size() > 0) {
            fields.put("species_scientific_name", Joiner.on(", ").join(scientificNames));
        }

        if (englishNames.size() > 0) {
            fields.put("species_english_name", Joiner.on(", ").join(englishNames));
        }

        //System.out.println(fields);
        return fields;
    }

    @Override
    public String convertInfoToRowset(Map<String, String> info) {
        return ExtractorHelper.createRowseFromFields(
                info.get("documentID"), collectionID, idxType,
                info.get("language"), info);
    }

    private void annotate(String filename, Map<String, List<String>> uris) throws FileNotFoundException {
//        System.out.println("annotate uris : " + uris);
        AnnotationBase annotator = AnnotationBase.getInstance();
        Set<Entry<String, List<String>>> entrySet = uris.entrySet();
        for (Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_country(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.VESSEL) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_vessel(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_gear(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.TARGET) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_targetSpecies(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.BYCATCH) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_bycatchSpecies(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.DISCARD) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_discardSpecies(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.THRETENED) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_thretenedSpecies(filename, uri_);
                }
            } //	            if (entry.getKey().equals("authority")) {
            //	                List<String> uris_ = entry.getValue();
            //	                for (String uri_ : uris_) {
            //	                    annotator.WIOFISH_authority(filename,uri_);
            //	                }
            //	            }
            else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_management(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.SECTOR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_sector(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.SEASONALITY) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_seasonality(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_status(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.ACCESS_CONTROL) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_access_control(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.FISHING_CONTROL) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_fishing_control(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.ENFORCEMENT_METHOD) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_enforcement_method(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.APPLICANT_FOR_ACCESS_RIGHT) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_accessRightApplicant(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.OWNER_OF_ACCESS_RIGHT) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_ownershipOfAccessRight(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.OTHER_INCOME_SOURCE) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_alternativeIncomeSource(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.MARKET) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_market(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.POST_PROCESSING_METHOD) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_postHarvestProcessing(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.DECISION_MAKER) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_representativesInDecisionMaking(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT_INDICATOR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_managementIndicator(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.FINANCE_MGT_AUTHORITY) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_financingManagement(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.TECHNOLOGY_IN_USE) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.WIOFISH_technologiesInUse(filename, uri_);
                }
            } else {
                System.out.println("=> annotation uri not found: " + entry.getKey() + " all entries are : " + entrySet);
            }
        }
    }

    ///// parse table helpers
    static void parseSimpleRow(String fieldName, String pattern, String html, Map<String, String> fields) {
        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(html);

        if (m.find()) {
            String value = m.group(1).trim();

            value = value.replace("<br>", "");

            fields.put(fieldName, value);
        }
    }

    static void parseTickTableMultipleLangs(String fieldName, String pattern1, String pattern2, String html, Map<String, String> fields) {

        Pattern p = Pattern.compile(pattern1);

        Matcher m = p.matcher(html);
        if (m.find()) {
            //System.out.println("Select gear used in this fishery : "	+ m.group(1));
            String table = m.group(1).trim();

            Pattern p2 = Pattern.compile(pattern2);
            Matcher m2 = p2.matcher(table);

            List<String> eng = Lists.newArrayList();
            List<String> fr = Lists.newArrayList();

            while (m2.find()) {

                List<String> values = Splitter.on(" / ").trimResults().omitEmptyStrings().splitToList(m2.group(1).trim());

                if (values.size() != 2 && values.size() != 1) {
                    logger.warn("bad data for " + fieldName + " : " + values);
                    break;
                }

                if (values.size() == 1) {
                    String valueEng = values.get(0);

                    if (valueEng.equalsIgnoreCase("None") || valueEng.equalsIgnoreCase("Unknown")) {
                        eng = Lists.newArrayList();
                        break;
                    }

                    eng.add(valueEng.trim());
                } else if (values.size() == 2) {
                    String valueEng = values.get(0);
                    String valueFr = values.get(1);

                    if (valueEng.equalsIgnoreCase("None") || valueEng.equalsIgnoreCase("Unknown")) {
                        eng = Lists.newArrayList();
                        fr = Lists.newArrayList();
                        break;
                    }

                    eng.add(valueEng.trim());
                    fr.add(valueFr.trim());
                }
            }

            if (eng.size() > 0) {
                fields.put(fieldName, Joiner.on(", ").join(eng));
            }
            if (fr.size() > 0) {
                fields.put(fieldName + "_fr", Joiner.on(", ").join(fr));
            }

        }
    }

    static void parseTickTable(String fieldName, String pattern1, String pattern2, String html, Map<String, String> fields) {
        Pattern p = Pattern.compile(pattern1);

        Matcher m = p.matcher(html);
        if (m.find()) {
            //System.out.println("Select gear used in this fishery : "	+ m.group(1));
            String table = m.group(1).trim();

            Pattern p2 = Pattern.compile(pattern2);
            Matcher m2 = p2.matcher(table);

            List<String> eng = Lists.newArrayList();
            while (m2.find()) {

                String value = m2.group(1).trim();

                if (value.equalsIgnoreCase("None") || value.equalsIgnoreCase("Unknown")) {
                    eng = Lists.newArrayList();
                    break;
                }

                eng.add(value);
            }

            if (eng.size() > 0) {
                fields.put(fieldName, Joiner.on(", ").join(eng));
            }

        }
    }

    static void parseTable(String fieldName, String pattern, String html, Map<String, String> fields) {
        Pattern p = Pattern.compile(pattern);

        Matcher m = p.matcher(html);

        if (m.find()) {
            String table = m.group(1);
            //System.out.println(table);

            List<String> rows = Splitter.on("<br>").trimResults().omitEmptyStrings().splitToList(table);

            List<String> engValues = Lists.newArrayList();
            List<String> frValues = Lists.newArrayList();

            for (String row : rows) {
                List<String> values = Splitter.on(" / ").trimResults().omitEmptyStrings().splitToList(row);

                if (values.size() == 2) {
                    String dmEng = values.get(0);
                    String dmFr = values.get(1);
                    if (fieldName.equals(ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS))) {
                        dmEng = dmEng.replace("-", "");
                        dmFr = dmFr.replace("-", "");
                    }
                    engValues.add(dmEng);
                    frValues.add(dmFr);

                    if (dmEng.equalsIgnoreCase("None") || dmEng.equalsIgnoreCase("Unknown")) {
                        engValues = Lists.newArrayList();
                        frValues = Lists.newArrayList();
                    }

                } else {
                    logger.warn("bad data for " + fieldName + " : " + values + " for row : " + row);
                }
            }

            if (engValues.size() > 0) {
                fields.put(fieldName, Joiner.on(", ").join(engValues));
            }
            if (frValues.size() > 0) {
                fields.put(fieldName + "_fr", Joiner.on(", ").join(frValues));
            }

        }

    }

    private boolean containsSpecies(List<ExtractedEntity> allSpecies, String species) {
        for (ExtractedEntity extractedEntity : allSpecies) {
            if (extractedEntity.en_name.equalsIgnoreCase(species)) {
                return true;
            }
        }
        return false;
    }
}
