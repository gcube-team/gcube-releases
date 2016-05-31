package org.gcube.textextractor.extractors;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.XMLConstants;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.tika.language.LanguageIdentifier;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.xml.XMLParser;
import org.apache.tika.sax.BodyContentHandler;
import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.textextractor.entities.ExtractedEntity;
import org.gcube.textextractor.entities.ShortenCE4NameResponse;
import org.gcube.textextractor.helpers.ExtractorHelper;
import org.gcube.textextractor.helpers.XPathHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;

public class XMLExtractor extends InformationExtractor {

    private static final Logger logger = LoggerFactory.getLogger(XMLExtractor.class);

    public XMLExtractor() {
    }

    @Override
    public Map<String, String> extractFieldsFromFile(String filename) throws Exception {
        logger.info("Processing file : " + filename);
        long starttime = System.currentTimeMillis();
        try (InputStream input = new FileInputStream(filename)) {
            ContentHandler handler = new BodyContentHandler();
            Metadata metadata = new Metadata();
            metadata.set(Metadata.CONTENT_TYPE, "application/xml; charset=utf-8");
            new XMLParser().parse(input, handler, metadata,
                    new ParseContext());

            String text = ExtractorHelper.removeEmptyLines(handler
                    .toString());
            Map<String, String> info = new HashMap<String, String>();

            info.put("documentID", filename);
            info.put("text", text);
            //info.put("title", metadata.get("title"));
            info.put("language", new LanguageIdentifier(text).getLanguage());

            info.put("provenance", "FIRMS");
            // extract custom fields

            long part_start_time = System.currentTimeMillis();
            info.putAll(customFields(filename));
            long part_end_time = System.currentTimeMillis();

            logger.info("~> field extraction time  : " + (part_end_time - part_start_time) / 1000.0 + " secs");

            return info;
        } catch (Exception e) {
            e.printStackTrace();
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

    @Override
    public Map<String, String> enrichRecord(Map<String, String> record, String filename) {
        Map<String, String> enrichedRecord = new HashMap<String, String>();
        Map<String, List<String>> uris = new HashMap<String, List<String>>();
        String docName = null;

        //insert here a way to retrieve the last part of the filename and obtain e.g. 13519 from http://firms.fao.org/firms/resource/13519/fr
        String[] filenameParts = filename.split("_");
        if (filenameParts != null && filenameParts.length > 1) {
            docName = filenameParts[filenameParts.length - 2];
        } else {
            docName = filename;
        }

        String docURI = "http://smartfish.collection/firms/" + docName.toLowerCase();

        //logger.info("docURI : " + docURI);
        enrichedRecord.putAll(record);

        //replace the documentID
        enrichedRecord.put("documentID", docURI);

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), new ExtractorHelper.QueryWrapperList() {
            @Override
            public String doCall(List<ExtractedEntity> arg) throws Exception {
                return ExtractorHelper.queryCountry(arg);
            }
        });
        
        ExtractorHelper.enrichSimpleField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR), new ExtractorHelper.QueryWrapperSimple() {
            @Override
            public String doCall(ExtractedEntity arg) throws Exception {
                return ExtractorHelper.queryYear(arg);
            }
        });

        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS) /*"exploitation_status"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryExploitationStatus(arg);
                    }
                });

        try {
            String speciesURIJson = null;
            if (record.get("species_english_name") != null && record.get("species_english_name").trim().length() > 0) {
                List<String> asList = Arrays.asList(record.get("species_english_name").split("\\s*,\\s*"));
                ArrayList<ExtractedEntity> eList = new ArrayList<ExtractedEntity>();
                for (String name : asList) {
                    eList.add(new ExtractedEntity(name, ""));
                }
                speciesURIJson = ExtractorHelper.querySpecies(eList);
                uris.put("species_uris", ShortenCE4NameResponse.getURIFromJSON(speciesURIJson));
                enrichedRecord.put("species_uris", speciesURIJson);
            }
        } catch (Exception e) {
            logger.warn("Error processing species : " + record.get("species_english_name"), e);
        }

//		try {
//			String sectorURIJson = null; 
//			if (record.get("sector").trim().length() > 0){
//				sectorURIJson = ExtractorHelper.queryExploitationStatus(record.get("sector"));
//				uris.put("sector_uris", ShortenCE4NameResponse.getURIFromJSON(sectorURIJson));
//				enrichedRecord.put("sector_uris", sectorURIJson);
//			}
//		} catch (Exception e){
//			logger.info("Error processing sector : " + record.get("sector"));
//			e.printStackTrace();
//		}
        ExtractorHelper.enrichListField(record, enrichedRecord, uris, ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT) /*"management"*/, new ExtractorHelper.QueryWrapperList() {
                    @Override
                    public String doCall(List<ExtractedEntity> arg) throws Exception {
                        return ExtractorHelper.queryManagement(arg);
                    }
                });

        try {
            String mng = record.get("management");
            if (mng != null) {
                if (mng.equalsIgnoreCase("true")) {
                    mng = "Management Unit";
                }

                String managementURIJson = ExtractorHelper.queryManagement(new ExtractedEntity(mng, ""));
                uris.put("management_uris", ShortenCE4NameResponse.getURIFromJSON(managementURIJson));
                enrichedRecord.put("management_uris", managementURIJson);
            }
        } catch (Exception e) {
            logger.warn("Error processing management : " + record.get("management"), e);
        }

                //include uris for the following keys:
        //management
        //sector
        try {
            //logger.info(uris);
            //process those uris

            //add info to enrichedRecord
            annotate(docURI, uris);
        } catch (FileNotFoundException ex) {
            logger.error("file : " + filename + " not found", ex);
        }

        return enrichedRecord;
    }

    private Map<String, String> customFields(String filename) throws SAXException, IOException, ParserConfigurationException, XPathExpressionException {
        Map<String, String> fields = new HashMap<String, String>();

        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        factory.setNamespaceAware(true);
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document doc = builder.parse(new File(filename));
        doc.getDocumentElement().normalize();

        XPathFactory xPathfactory = XPathFactory.newInstance();
        XPath xpath = xPathfactory.newXPath();
        xpath.setNamespaceContext(new FigisNamespaceContext(null));

        String xpathStr = null;

        //Document title
        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:AqResIdent/dc:Title/text()";
        fields.put("title", XPathHelper.getValueXPath(xpath, doc, xpathStr));

        //Country in English
        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:WaterAreaOverview/fi:WaterAreaRef/dc:Title[@xml:lang='en']/text()";
        fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY), XPathHelper.getMultiValuesXPath(xpath, doc, xpathStr));

        //Species names english
        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:AqResIdent/fi:SpeciesList/fi:SpeciesRef/dc:Title[@xml:lang=\"en\"]/text()";
        fields.put("species_english_name", XPathHelper.getMultiValuesXPath(xpath, doc, xpathStr));

        //Species scientific names
        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:AqResIdent/fi:SpeciesList/fi:SpeciesRef/fi:ForeignID[@CodeSystem='scientific_name']";
        fields.put("species_scientific_name", XPathHelper.getMultiValuesXPath(xpath, doc, xpathStr, "Code"));

        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:Management[@ManagementUnit='true']";
        if (XPathHelper.checkNodeExists(xpath, doc, xpathStr)) {
            fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT), "true");
        }
        
        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:AqResIdent[@Factsheet='true']/fi:ReportingYear/text()";
        if (XPathHelper.checkNodeExists(xpath, doc, xpathStr)) {
            fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR), XPathHelper.getStringAttribute(xpath, doc, xpathStr));
        }

        xpathStr = "/fi:FIGISDoc/fi:AqRes/fi:AqResStateTrend/fi:ExploitState/@Value";
        fields.put(ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS), XPathHelper.getStringAttribute(xpath, doc, xpathStr));
        logger.info("extracted fields : " + fields);

        return fields;
    }

    @Override
    public String convertInfoToRowset(Map<String, String> info) {
        return ExtractorHelper.createRowseFromFields(
                info.get("documentID"), collectionID, idxType,
                info.get("language"), info);
    }

    private void annotate(String filename, Map<String, List<String>> uris) throws FileNotFoundException {
        AnnotationBase annotator = AnnotationBase.getInstance();
        Set<Map.Entry<String, List<String>>> entrySet = uris.entrySet();
        for (Map.Entry<String, List<String>> entry : entrySet) {
            if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.COUNTRY) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_country(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.SPECIES) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_species(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.GEAR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_gear(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.MANAGEMENT) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_management(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.YEAR) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_year(filename, uri_);
                }
            } else if (entry.getKey().equals(ANNOTATIONS.getLocalName(ANNOTATIONS.STATUS) + "_uris")) {
                List<String> uris_ = entry.getValue();
                for (String uri_ : uris_) {
                    annotator.FIRMS_status(filename, uri_);
                }
            }
        }
    }

    static public class FigisNamespaceContext implements NamespaceContext {

        final private Map<String, String> prefixMap;

        FigisNamespaceContext(Map<String, String> prefixMap) {
            if (prefixMap != null) {
                this.prefixMap = Collections.unmodifiableMap(new HashMap<String, String>(prefixMap));
            } else {
                this.prefixMap = Collections.emptyMap();
            }
        }

        public String getPrefix(String namespaceURI) {
            return null;
        }

        public Iterator<?> getPrefixes(String namespaceURI) {
            return null;
        }

        public String getNamespaceURI(String prefix) {
            if (prefix == null) {
                throw new NullPointerException("Invalid Namespace Prefix");
            } else if ("fi".equalsIgnoreCase(prefix)) {
                return "http://www.fao.org/fi/figis/devcon/";
            } else if ("argls".equals(prefix)) {
                return "http://www.naa.gov.au/recordkeeping/gov_online/agls/1.1";
            } else if ("ags".equals(prefix)) {
                return "http://www.purl.org/agmes/1.1/";
            } else if ("aida".equals(prefix)) {
                return "http://www.idmlinitiative.org/resources/dtds/AIDA22.xsd";
            } else if ("dc".equals(prefix)) {
                return "http://purl.org/dc/elements/1.1/";
            } else if ("dcterms".equals(prefix)) {
                return "http://purl.org/dc/terms/";
            } else if ("xsi".equals(prefix)) {
                return "http://www.w3.org/2001/XMLSchema-instance";
            } else if ("xml".equals(prefix)) {
                return XMLConstants.XML_NS_URI;
            } else {
                return XMLConstants.NULL_NS_URI;
            }
        }
    }
}
