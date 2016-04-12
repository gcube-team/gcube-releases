    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.gcube.index.Index;
import org.gcube.index.entities.SearchResponse;
import org.gcube.index.exceptions.BadRequestException;
import org.gcube.index.exceptions.InternalServerErrorException;
import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.FLOD_EntityCollection;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.semantic.annotator.utils.SMART_ENTITY_TYPES;
import org.gcube.textextractor.entities.ExtractedEntity;

/**
 *
 * @author Claudio Baldassarre <claudio.baldassarre@fao.org |
 * c.baldassarre@me.com>
 */
public class DataServer {

    private static DataServer instance;
    private ArrayList<String> entitiesURIs = new ArrayList<String>();

    public static DataServer getInstance() {
        if (instance == null) {
            instance = new DataServer();
        }
        return instance;
    }

    private DataServer() {
        AnnotationBase.getInstance();
        FLOD_EntityCollection.getInstance();
    }

    public String find(String term, String op, String target[], String page, String[] concept_filter) {
        if (op.equals("match")) {
            return match(term);
        } else if (op.equals("search")) {
            return search(term, page, concept_filter);
        } else if (op.equals("bysbj")) {
            return listDocumentsOfAnnotatedEntity(target, page);
        } else if (op.equals("scope")) {
            return searchInScope(term, target, page, concept_filter);
        }
        return SearchResponse.emptyResponse("bad operation name");
    }

    public String entity(String op, String[] target, String lang) {
        if (op.equals("list")) {
            return listEntitiesInAnnotationBase(target, lang);
        } else if (op.equals("species_in_country")) {
            return listSpeciesInCountry(target, lang);
        }
//        else if (op.equals("mine")) {
//            return listEntitiesInQuery(term);
//        }
        return SearchResponse.emptyResponse("bad operation name");
    }

    public String document(String op, String doc, String[] target, String lang) {
        if (op.equals("annotation")) {
            return listEntitiesAnnotatingDocument(doc, target, lang);
        }
        return SearchResponse.emptyResponse("bad operation name");
    }

    public String gis(String op, String target[], String lang) {
        if (op.equals("metalayer_of_entity")) {
            return gisMetaLayerOfEntity(target);
        } else if (op.equals("metalayer_of_doc")) {
            return gisMetaLayerOfEntitiesInDocument(target[0]);
        }
        return SearchResponse.emptyResponse("bad operation name");
    }

    public String infobox(String op, String target, String lang) {
        if (op.equals("describe")) {
            return listFactsAboutEntity(target, lang);
        }
        return SearchResponse.emptyResponse("bad operation name");
    }

    public String statistics(String op, String[] target, String lang) {
        if (op.equals("repository")) {
            return listPublicationPerAnnotation();
        }
        if (op.equals("tagcloud")) {
            return listPublicationPerAnnotation(target, lang);
        }
        return SearchResponse.emptyResponse("bad operation name");
    }
    

    private String listPublicationPerAnnotation() {
        return AnnotationBase.getInstance().statPublicationPerAnnotation();
    }
    
    private String listPublicationPerAnnotation(String[] target, String lang) {
        return AnnotationBase.getInstance().statPublicationPerAnnotation(target, lang);
    }

    public String exp(String[] country, String[] species) {
        return listDocumentsWithCoOccurringEntities(country, species);

    }

    private String search(String term, String page, String[] concept_filter) {
        int p = "".equals(page) ? 0 : Integer.valueOf(page);
        return searchIndexedDocument(term, p, Collections.EMPTY_LIST, Arrays.asList(concept_filter));
    }

    private String searchInScope(String term, String[] target, String page, String[] concept_filter) {
        int p = "".equals(page) ? 0 : Integer.valueOf(page);
        return searchIndexedDocument(term, p, Arrays.asList(target), Arrays.asList(concept_filter));
    }

    private String match(String stringMatch) {
        return Index.getInstance().autocompleteTitle(stringMatch, null, 10, 0, true).getEntity().toString();
    }

    private String getDocsByURI(List<String> docURIs) {
        try {
            return Index.getInstance().getDocsAPI(docURIs, true, docURIs.size(), 0);
        } catch (InternalServerErrorException e) {
        }
        return null;
    }

    private String searchIndexedDocument(String searchTerm, int page, List<String> searchInTheseFields, List<String> concept_filter) {
        int count = page != 0 ? 10 : 1000;
        int from = page != 0 ? (page - 1) * count : 0;
        try {
            return Index.getInstance().queryAPI(searchTerm, count, from, true, true, searchInTheseFields, concept_filter);
        } catch (BadRequestException e) {
        } catch (InternalServerErrorException e) {
        }
        return null;
    }

    private String listDocumentsOfAnnotatedEntity(String[] forFlodEntityUris, String page) {
        int start = !"".equals(page) ? (Integer.valueOf(page) - 1) * 10 : 0;
        List<String> docURIs = AnnotationBase.getInstance().listDocuments(forFlodEntityUris);
        if (!"".equals(page)) {
            docURIs = docURIs.subList(start, Math.min(start + 9, docURIs.size()));
        }
        return getDocsByURI(docURIs);
    }

    public String listDocumentsWithCoOccurringEntities(String[] countryURIs, String[] speciesURIs) {
        ArrayList<String> docURIs = new ArrayList<String>();
        for (int i = 0; i < countryURIs.length; i++) {
            String countryURI = countryURIs[i];
            List<String> docs = AnnotationBase.getInstance().listDocumentsWithCoOccuringEntities(countryURI, speciesURIs);
            addIfnotThere(docURIs, docs);
        }
        return getDocsByURI(docURIs);
    }

    private String listEntitiesAnnotatingDocument(String documentURI, String[] propertyLocalName, String lang) {
        entitiesURIs.clear();
        for (int i = 0; i < propertyLocalName.length; i++) {
            List<String> response = AnnotationBase.getInstance().listEntitiesOfDocument(documentURI, ANNOTATIONS.PROPERT_NS + propertyLocalName[i]);
            addIfnotThere(entitiesURIs, response);
        }
        String sparqlJson = FLOD_EntityCollection.getInstance().getLabel(entitiesURIs, lang);
        return SearchResponse.makeEntitiesListResponseWithLang(sparqlJson, lang);
    }

    private String listEntitiesInAnnotationBase(String[] propertyLocalName, String lang) {
        entitiesURIs.clear();
        for (int i = 0; i < propertyLocalName.length; i++) {
            List<String> response = AnnotationBase.getInstance().listEntitiesOfProperty(ANNOTATIONS.PROPERT_NS + propertyLocalName[i]);
            addIfnotThere(entitiesURIs, response);
        }
        String sparqlJson = FLOD_EntityCollection.getInstance().getLabel(entitiesURIs, lang);
        return SearchResponse.makeEntitiesListResponseWithLang(sparqlJson, lang);
    }

    private String listFactsAboutEntity(String target, String lang) {
        if (!target.startsWith("http")) {
            try {
                target = findEntityInQuery(target);
            } catch (Exception ex) {    
            }
        }
        return FLOD_EntityCollection.getInstance().infobox(target, lang);

    }

    private String findEntityInQuery(String target) throws Exception {
            String sparqlJson = FLOD_EntityCollection.getInstance().getURI(new ExtractedEntity(target, ""), SMART_ENTITY_TYPES.FLAGSTATE);
            String entityURI = FLOD_EntityCollection.getValueFromJson(sparqlJson, "uri");
            if (entityURI.isEmpty()) {
                sparqlJson = FLOD_EntityCollection.getInstance().getURI(new ExtractedEntity(target, ""), SMART_ENTITY_TYPES.SPECIES);
                entityURI = FLOD_EntityCollection.getValueFromJson(sparqlJson, "uri");
            }
            return entityURI;
    }

    private String listSpeciesInCountry(String[] countryURIs, String lang) {
        entitiesURIs.clear();
        for (int i = 0; i < countryURIs.length; i++) {
            List<String> response = AnnotationBase.getInstance().listSpeciesInCountry(countryURIs[i]);
            addIfnotThere(entitiesURIs, response);
        }
        String sparqlJson = FLOD_EntityCollection.getInstance().getLabel(entitiesURIs, lang);
        return SearchResponse.makeEntitiesListResponseWithLang(sparqlJson, lang);
    }

    private String gisMetaLayerOfEntity(String[] gisEntityURIs) {
        entitiesURIs.clear();
        for (int i = 0; i < gisEntityURIs.length; i++) {
            List<String> response = AnnotationBase.getInstance().getMetaLayer(gisEntityURIs[i]);
            addIfnotThere(entitiesURIs, response);
        }
        return FLOD_EntityCollection.getInstance().describeEntity(entitiesURIs, "", true, null);
//        return SearchResponse.makeDescribeResponse(sparqlJson);
    }

    private String gisMetaLayerOfEntitiesInDocument(String docURI) {
        List<String> species = AnnotationBase.getInstance().listEntitiesOfDocument(docURI, ANNOTATIONS.SPECIES);
        List<String> countries = AnnotationBase.getInstance().listEntitiesOfDocument(docURI, ANNOTATIONS.COUNTRY);
        species.addAll(countries);
        return gisMetaLayerOfEntity(species.toArray(new String[species.size()]));
    }
    
    private void addIfnotThere(ArrayList<String> toThisList, List<String> elementsFromThisList) {
        for (String newUri : elementsFromThisList) {
            if (!toThisList.contains(newUri)) {
                toThisList.add(newUri);
            }
        }
    }

    public static void main(String[] args) {
        String[] doc = {"http://smartfish.collection/wiofish/189"};
        String[] annotProperty = {"year"};
        String[] annotProperty2 = {"market"};
        String[] concept_filter = {"management","country"};
        String[] concept_filter_country = {"country"};
        String[] concept_filter_empty = {};
        String[] countries = {"http://www.fao.org/figis/flod/entities/codedentity/f527d0db-0255-4893-b266-cf8a18b6e9ca"};
        String[] species = {"http://www.fao.org/figis/flod/entities/codedentity/86021df0-f907-478b-a677-98c079466b8f"};
        String[] annotatingEntities = {"http://www.fao.org/figis/flod/entities/codedentity/f527d0db-0255-4893-b266-cf8a18b6e9ca"};

        System.out.println(DataServer.getInstance().find("comoros", "search", null, "", concept_filter_empty));
//        System.out.println(DataServer.getInstance().find("Tuna", "search", null, "", concept_filter));
//        System.out.println(DataServer.getInstance().find("tuna", "search", null, "", concept_filter_empty ));
//            System.out.println(DataServer.getInstance().find("comoros", "match", null, null));
//        System.out.println(DataServer.getInstance().find(null, "bysbj", annotatingEntities, ""));
//        System.out.println(DataServer.getInstance().find(null, "bysbj", annotatingEntities, ""));
//        System.out.println(DataServer.getInstance().exp(countries, annotatingEntities));
//        System.out.println(DataServer.getInstance().entity("list", "sector", "en"));
//        System.out.println(DataServer.getInstance().document("annotation", "http://smartfish.collection/wiofish/458", localName, "en"));
//        System.out.println(DataServer.getInstance().infobox("describe", "http://www.fao.org/figis/flod/entities/codedentity/86021df0-f907-478b-a677-98c079466b8f", "en"));
//        System.out.println(DataServer.getInstance().findEntityInQuery("kenya"));
//        System.out.println(DataServer.getInstance().infobox("describe", "kenya", "en"));
//        System.out.println(DataServer.getInstance().infobox("describe", "http://www.fao.org/figis/flod/entites/codedentity/b2ef129b-2d86-446a-83b9-e731915394c9", "en"));
//        System.out.println(DataServer.getInstance().listDocumentsOfAnnotatedEntity(countries, null));

//        System.out.println(DataServer.getInstance().listEntitiesInAnnotationBase(annotProperty, "en"));
//        System.out.println(DataServer.getInstance().listEntitiesInAnnotationBase(annotProperty2, "en"));
//        System.out.println(DataServer.getInstance().entity("species_in_country", countries, ""));
//            System.out.println(DataServer.getInstance().gis("metalayer_of_entity", species, "en"));
//            System.out.println(DataServer.getInstance().gis("metalayer_of_doc", doc, "en"));
//        System.out.println(DataServer.getInstance().statistics("repository", "", ""));
    }
}
