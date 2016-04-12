/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.sparql.modify.request.UpdateCreate;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.DCTerms;
import com.hp.hpl.jena.vocabulary.RDF;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.riot.RiotWriter;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.gcube.semantic.annotator.utils.ANNOTATIONS;
import org.gcube.semantic.annotator.utils.COLLECTION;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class AnnotationBase {

    private static AnnotationBase instance;
    public static String FIRMS_annotation_file_name = "annotation_collection_firms.nq";
    public static String STATBASE_annotation_file_name = "annotation_collection_statbase.nq";
    public static String WIOFISH_annotation_file_name = "annotation_collection_wiofish.nq";
    public static Node FIRMS_GRAPH_NODE = NodeFactory.createURI("http://smartfish.graph/firms");
    public static Node STATBASE_GRAPH_NODE = NodeFactory.createURI("http://smartfish.graph/statbase");
    public static Node WIOFISH_GRAPH_NODE = NodeFactory.createURI("http://smartfish.graph/wiofish");

    private final String remoteAnnotationUpdateEndpoint = "http://dl051.madgik.di.uoa.gr:3030/chimaera_annotation_update/update";
    private final DatasetGraphAccessorHTTP annotationStore_accessor;
    private final DatasetGraph annotationStore = DatasetGraphFactory.createMem();
    private final DatasetGraph annotations = DatasetGraphFactory.createMem();
    private static final Model unionModel = ModelFactory.createDefaultModel();

    public static AnnotationBase getInstance() {
        if (instance == null) {
            instance = new AnnotationBase();
        }
        return instance;
    }

    private AnnotationBase() {
        System.out.println("Initializing AnnotationBase");
        annotationStore_accessor = new DatasetGraphAccessorHTTP("http://dl051.madgik.di.uoa.gr:3030/chimaera_annotation_update/data");
//        this.annotations.addGraph(Quad.unionGraph, annotationStore_accessor.httpGet(Quad.unionGraph));
        this.annotations.addGraph(NodeFactory.createURI("http://smartfish.graph/wiofish"), annotationStore_accessor.httpGet(Quad.unionGraph));
        this.annotations.addGraph(NodeFactory.createURI("http://smartfish.graph/statbase"), annotationStore_accessor.httpGet(Quad.unionGraph));
        this.annotations.addGraph(NodeFactory.createURI("http://smartfish.graph/firms"), annotationStore_accessor.httpGet(Quad.unionGraph));
        ExtendedIterator<Triple> find = annotationStore_accessor.httpGet(Quad.unionGraph).find(Node.ANY, Node.ANY, Node.ANY);
        unionModel.add(ModelUtils.triplesToStatements(find, unionModel));
        System.out.println("Finalized initialization AnnotationBase");
    }

    private void addAnnotation(String docURI, String entity_uri, String annotation_property, String collection_name) {

        Node graphNode = NodeFactory.createURI("http://smartfish.graph/" + collection_name.toLowerCase());
        Node collection_node = NodeFactory.createURI("http://smartfish.collection/" + collection_name.toLowerCase());
        Node doc_node = NodeFactory.createURI(docURI);
        Node entity_node = NodeFactory.createURI(entity_uri);
        this.annotationStore.add(graphNode, collection_node, RDF.type.asNode(), NodeFactory.createURI("http://purl.org/ontology/bibo/Collection"));
        this.annotationStore.add(graphNode, doc_node, RDF.type.asNode(), NodeFactory.createURI("http://purl.org/ontology/bibo/Document"));
        this.annotationStore.add(graphNode, doc_node, NodeFactory.createURI(annotation_property), entity_node);
        this.annotationStore.add(graphNode, doc_node, DCTerms.subject.asNode(), entity_node);
        this.annotationStore.add(graphNode, doc_node, NodeFactory.createURI("http://dbpedia.org/property/collection"), collection_node);
//        System.out.println("Annotated : " + annotation_property + " with " + entity_uri + " in " + collection_name);
    }

    public AnnotationBase WIOFISH_targetSpecies(String docID, String speciesURI) {
        String collection = COLLECTION.WIOFISH;
        addAnnotation(docID, speciesURI, ANNOTATIONS.TARGET, collection);
        addAnnotation(docID, speciesURI, ANNOTATIONS.INCATCH, collection);
        addAnnotationSpecies(docID, speciesURI, collection);
        return instance;

    }

    public AnnotationBase WIOFISH_bycatchSpecies(String docID, String speciesURI) {
        String collection = COLLECTION.WIOFISH;
        addAnnotation(docID, speciesURI, ANNOTATIONS.BYCATCH, collection);
        addAnnotation(docID, speciesURI, ANNOTATIONS.INCATCH, collection);
        addAnnotationSpecies(docID, speciesURI, collection);
        return instance;

    }

    public AnnotationBase WIOFISH_discardSpecies(String docID, String speciesURI) {
        String collection = COLLECTION.WIOFISH;
        addAnnotation(docID, speciesURI, ANNOTATIONS.DISCARD, collection);
        addAnnotation(docID, speciesURI, ANNOTATIONS.INCATCH, collection);
        addAnnotationSpecies(docID, speciesURI, collection);
        return instance;

    }

    public AnnotationBase WIOFISH_thretenedSpecies(String docID, String speciesURI) {
        String collection = COLLECTION.WIOFISH;
        addAnnotation(docID, speciesURI, ANNOTATIONS.THRETENED, collection);
        addAnnotation(docID, speciesURI, ANNOTATIONS.INCATCH, collection);
        addAnnotationSpecies(docID, speciesURI, collection);
        return instance;

    }

    public AnnotationBase WIOFISH_gear(String docID, String gearURI) {
        addAnnotationGear(docID, gearURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_status(String docID, String statusURI) {
        addAnnotationStatus(docID, statusURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_sector(String docID, String sectorURI) {
        addAnnotationSector(docID, sectorURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_seasonality(String docID, String seasonalityURI) {
        seasonalityURI = "http://smartfish.d4science.org/time/" + seasonalityURI.toLowerCase();
        addAnnotationSeasonality(docID, seasonalityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_management(String docID, String managemetURI) {
        addAnnotationManagement(docID, managemetURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_access_control(String docID, String access_control_uri) {
        addAnnotationMeasure(docID, access_control_uri, COLLECTION.WIOFISH);
        addAnnotationControl(docID, access_control_uri, COLLECTION.WIOFISH);
        addAnnotationAccessControl(docID, access_control_uri, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_fishing_control(String docID, String fishing_control_uri) {
        addAnnotationMeasure(docID, fishing_control_uri, COLLECTION.WIOFISH);
        addAnnotationControl(docID, fishing_control_uri, COLLECTION.WIOFISH);
        addAnnotationFishingControl(docID, fishing_control_uri, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_enforcement_method(String docID, String enforcement_method_uri) {
        addAnnotationMeasure(docID, enforcement_method_uri, COLLECTION.WIOFISH);
        addAnnotationMethod(docID, enforcement_method_uri, COLLECTION.WIOFISH);
        addAnnotationEnforcementMethod(docID, enforcement_method_uri, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_authority(String docID, String authorityURI) {
        addAnnotationAuthority(docID, authorityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_vessel(String docID, String vesselURI) {
        addAnnotationVessel(docID, vesselURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_country(String docID, String countryURI) {
        addAnnotationCountry(docID, countryURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_financingManagement(String docID, String authorityURI) {
        addAnnotationFinancingManagement(docID, authorityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_managementIndicator(String docID, String indicatorURI) {
        addAnnotationManagementIndicator(docID, indicatorURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_representativesInDecisionMaking(String docID, String authorityURI) {
        addAnnotationRepresentativeInDecisionMaking(docID, authorityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_postHarvestProcessing(String docID, String processingURI) {
        addAnnotationPostHarvestProcessing(docID, processingURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_market(String docID, String marketURI) {
        addAnnotationMarket(docID, marketURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_alternativeIncomeSource(String docID, String incomeSourceURI) {
        addAnnotationAlternativeIncomeSource(docID, incomeSourceURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_ownershipOfAccessRight(String docID, String legalEntityURI) {
        addAnnotationOwnershipOfAccessRight(docID, legalEntityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_accessRightApplicant(String docID, String legalEntityURI) {
        addAnnotationAccessRightApplicant(docID, legalEntityURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase WIOFISH_technologiesInUse(String docID, String technologyURI) {
        addAnnotationTechnologyInUse(docID, technologyURI, COLLECTION.WIOFISH);
        return instance;
    }

    public AnnotationBase FIRMS_gear(String docID, String gearURI) {
        addAnnotationGear(docID, gearURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase FIRMS_species(String docID, String speciesURI) {
        addAnnotationSpecies(docID, speciesURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase FIRMS_country(String docID, String countryURI) {
        addAnnotationCountry(docID, countryURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase FIRMS_management(String docID, String managementURI) {
        addAnnotationManagement(docID, managementURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase FIRMS_status(String docID, String statusURI) {
        addAnnotationStatus(docID, statusURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase FIRMS_year(String docID, String yearURI) {
        addAnnotationYear(docID, yearURI, COLLECTION.FIRMS);
        return instance;
    }

    public AnnotationBase STATBASE_gear(String docID, String gearURI) {
        addAnnotationGear(docID, gearURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_year(String docID, String yearURI) {
        addAnnotationYear(docID, yearURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_species(String docID, String speciesURI) {
        addAnnotationSpecies(docID, speciesURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_country(String docID, String countryURI) {
        addAnnotationCountry(docID, countryURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_vessel(String docID, String vesselURI) {
        addAnnotationVessel(docID, vesselURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_water_area(String docID, String waterAreaURI) {
        addAnnotationWaterArea(docID, waterAreaURI, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_statistics(String docID, String statUri) {
        addAnnotationStatistics(docID, statUri, COLLECTION.STATBASE);
        return instance;
    }

    public AnnotationBase STATBASE_land_area(String docID, String landAreaURI) {
        addAnnotationLandArea(docID, landAreaURI, COLLECTION.STATBASE);
        return instance;
    }

    private void addAnnotationCountry(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.COUNTRY, collection);
    }

    private void addAnnotationGear(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.GEAR, collection);
    }

    private void addAnnotationYear(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.YEAR, collection);
    }

    private void addAnnotationVessel(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.VESSEL, collection);
    }

    private void addAnnotationSpecies(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.SPECIES, collection);
    }

    private void addAnnotationStatus(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.STATUS, collection);
    }

    private void addAnnotationSector(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.SECTOR, collection);
    }

    private void addAnnotationSeasonality(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.SEASONALITY, collection);
    }

    private void addAnnotationManagement(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.MANAGEMENT, collection);
    }

    private void addAnnotationAccessControl(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.ACCESS_CONTROL, collection);
    }

    private void addAnnotationFishingControl(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.FISHING_CONTROL, collection);
    }

    private void addAnnotationEnforcementMethod(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.ENFORCEMENT_METHOD, collection);
    }

    private void addAnnotationAuthority(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.AUTHORITY, collection);
    }

    private void addAnnotationMeasure(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.MEASURE, collection);
    }

    private void addAnnotationControl(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.CONTROL, collection);
    }

    private void addAnnotationMethod(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.METHOD, collection);
    }

    private void addAnnotationFinancingManagement(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.FINANCE_MGT_AUTHORITY, collection);
    }

    private void addAnnotationManagementIndicator(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.MANAGEMENT_INDICATOR, collection);
    }

    private void addAnnotationRepresentativeInDecisionMaking(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.DECISION_MAKER, collection);
    }

    private void addAnnotationPostHarvestProcessing(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.POST_PROCESSING_METHOD, collection);
    }

    private void addAnnotationMarket(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.MARKET, collection);
    }

    private void addAnnotationAlternativeIncomeSource(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.OTHER_INCOME_SOURCE, collection);
    }

    private void addAnnotationOwnershipOfAccessRight(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.OWNER_OF_ACCESS_RIGHT, collection);
    }

    private void addAnnotationAccessRightApplicant(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.APPLICANT_FOR_ACCESS_RIGHT, collection);
    }

    private void addAnnotationTechnologyInUse(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.TECHNOLOGY_IN_USE, collection);
    }

    private void addAnnotationWaterArea(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.WATER_AREA, collection);
    }

    private void addAnnotationStatistics(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.STATISTICS, collection);
    }

    private void addAnnotationLandArea(String docID, String uri, String collection) {
        addAnnotation(docID, uri, ANNOTATIONS.LAND_AREA, collection);
    }

    public void toRemoteGraph(Node graphNode) throws FileNotFoundException {
        Node tempGraph = NodeFactory.createURI(graphNode.toString() + "_temp");
        if (annotationStore_accessor.httpGet(tempGraph) == null) {
            UpdateCreate create = new UpdateCreate(tempGraph);
            UpdateExecutionFactory.createRemote(create, remoteAnnotationUpdateEndpoint).execute();
        }
        UpdateClear clear = new UpdateClear(tempGraph);
        UpdateExecutionFactory.createRemote(clear, remoteAnnotationUpdateEndpoint).execute();
        UpdateDataInsert updateData = new UpdateDataInsert(makeAcc(graphNode));
        UpdateExecutionFactory.createRemote(updateData, remoteAnnotationUpdateEndpoint).execute();
        this.annotationStore.deleteAny(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
    }

    public File dump(String filename) throws FileNotFoundException {
        File f = new File(filename);
        FileOutputStream fos = new FileOutputStream(f);
        RiotWriter.createNQuads().write(fos, this.annotationStore, null, null, this.annotationStore.getContext());
        this.annotationStore.deleteAny(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
        return f;
    }

    private ArrayList<String> documentURIs = new ArrayList<String>();

    public String statPublicationPerAnnotation() {
        List<String> properties = listAnnotationProperties();
        JsonArray ja = new JsonArray();
        for (String p_uri : properties) {
            JsonObject jo = new JsonObject();
            List<String> entities = listEntitiesOfProperty(p_uri);
            jo.addProperty("uri", p_uri);
            jo.addProperty("size", entities.size());
            jo.addProperty("name", StringUtils.capitalize(p_uri.split("#")[1].replace("_", " ")));
            ja.add(jo);
        }
        return ja.toString();
    }

    public String statPublicationPerAnnotation(String[] target, String lang) {
        JsonArray ja = new JsonArray();
        for (int i = 0; i < target.length; i++) {
            String annotation_p = target[i];
            List<String> annotationEntities = listEntitiesOfProperty(annotation_p);
            Set<String> uniqueEntities = new HashSet<String>(annotationEntities);
            for (String annotationEntity : uniqueEntities) {
                int frequency = Collections.frequency(annotationEntities, annotationEntity);
                JsonObject jo = new JsonObject();
                jo.addProperty("uri", annotationEntity);
                jo.addProperty("size", frequency);
                jo.addProperty("name", SMARTFISH_EntityCollection.getInstance().getLabel(annotationEntity, lang));
                ja.add(jo);
            }
        }
        return ja.toString();
    }

    public String statPublicationPerAnnotation(String target, String lang, String[] concept_filter) {
        List<String> properties = listAnnotationProperties();
        JsonArray ja = new JsonArray();
        for (String p_uri : properties) {
            JsonObject jo = new JsonObject();
            List<String> entities = listEntitiesOfProperty(p_uri);
            jo.addProperty("uri", p_uri);
            jo.addProperty("size", entities.size());
            jo.addProperty("name", StringUtils.capitalize(p_uri.split("#")[1].replace("_", " ")));
            ja.add(jo);
        }
        return ja.toString();
    }

    public List<String> listAnnotationProperties() {
        String annotation_NS = "http://www.fao.org/figis/onto/smartfish/annotation.owl#";
        String prefixes = "prefix afn:  <http://jena.hpl.hp.com/ARQ/function#>";
        String query = "";
        String select = " select distinct ?annotation_property";
        String where = " where ";
        String gbp = " ?doc ?annotation_property ?tagging_entity ."
                + "filter(afn:namespace(?annotation_property) = '" + annotation_NS + "') ";
        query = prefixes + select + where + "{" + gbp + "}";
        ResultSet rs = QueryExecutionFactory.create(query, unionModel).execSelect();
        return ResultSetUtils.resultSetToStringList(rs, "annotation_property", "Resource");
    }

    public List<String> listDocuments(String forThisEntityUri) {
        return listDocuments(forThisEntityUri, Node.ANY);
    }

    private List<String> listDocuments(String forThisEntityUri, Node inThisCollection) {
        documentURIs.clear();
        if (inThisCollection == null) {
            inThisCollection = Node.ANY;
        }
        Iterator<Quad> quads_it = annotations.find(inThisCollection, Node.ANY, DCTerms.subject.asNode(), NodeFactory.createURI(forThisEntityUri));
        while (quads_it.hasNext()) {
            Quad quad = quads_it.next();
            documentURIs.add(quad.getSubject().getURI());
        }
        return documentURIs;
    }

    public List<String> listDocumentsInWIOFISH(String forThisEntityUri) {
        return listDocuments(forThisEntityUri, NodeFactory.createURI(COLLECTION.WIOFISH));
    }

    public List<String> listDocumentsInSTATBASE(String forThisEntityUri) {
        return listDocuments(forThisEntityUri, NodeFactory.createURI(COLLECTION.STATBASE));
    }

    public List<String> listDocumentsInFIRMS(String forThisEntityUri) {
        return listDocuments(forThisEntityUri, NodeFactory.createURI(COLLECTION.FIRMS));
    }

    public List<String> listDocumentsWithCoOccuringEntities(String referenceURI, String[] coOccurringEntitiesURIs) {
        ArrayList<String> docURIs = new ArrayList<String>();
        String query = "";
        String select = " select distinct ?doc";
        String where = " where ";
        String gbp = "";
        for (int i = 0; i < coOccurringEntitiesURIs.length; i++) {
            String coOccurEntUri = coOccurringEntitiesURIs[i];
            gbp += ""
                    + "{ ?doc <" + ANNOTATIONS.COUNTRY + "> <" + referenceURI + "> .  ?doc <" + ANNOTATIONS.SPECIES + "> <" + coOccurEntUri + "> } ";
            if (i != coOccurringEntitiesURIs.length - 1) {
                gbp += " UNION ";
            }
        }
        query = select + where + "{" + gbp + "}";
        Query q = QueryFactory.create(query);
        System.out.println(q.serialize());
        ResultSet rs = QueryExecutionFactory.create(query, unionModel).execSelect();

        docURIs.addAll(ResultSetUtils.resultSetToStringList(rs, "doc", "Resource"));

        return docURIs;
    }

    public List<String> listDocuments(String[] forThisEntityUris) {
        ArrayList<String> result = new ArrayList<String>();
        for (int i = 0; i < forThisEntityUris.length; i++) {
            String uri = forThisEntityUris[i];
            List<String> listDocuments = listDocuments(uri);
            addIfnotThere(result, listDocuments);
        }
        return result;

    }

    public List<String> listEntitiesOfProperty(String propertyUri) {
        ArrayList<String> entitiesURIs = new ArrayList<String>();
        Iterator<Quad> quads_it = annotations.find(Node.ANY, Node.ANY, NodeFactory.createURI(propertyUri), Node.ANY);
        while (quads_it.hasNext()) {
            Quad quad = quads_it.next();
            entitiesURIs.add(quad.getObject().getURI());
        }
        return entitiesURIs;
    }

    public List<String> listEntitiesOfDocument(String documentURI, String annotationProperty) {
        ArrayList<String> entitiesURIs = new ArrayList<String>();
        Iterator<Quad> quads_it = annotations.find(Node.ANY, NodeFactory.createURI(documentURI), NodeFactory.createURI(annotationProperty), Node.ANY);
        while (quads_it.hasNext()) {
            Quad quad = quads_it.next();
            entitiesURIs.add(quad.getObject().getURI());
        }
        return entitiesURIs;
    }

    public List<String> listSpeciesInCountry(String countryURI) {
        ArrayList<String> entitiesURIs = new ArrayList<String>();
        String query = "";
        String select = " select distinct ?species";
        String where = " where ";
        String gbp = ""
                + "?doc <" + ANNOTATIONS.COUNTRY + "> <" + countryURI + "> . "
                + "?doc <" + ANNOTATIONS.SPECIES + "> ?species ";
        query = select + where + "{" + gbp + "}";
        ResultSet rs = QueryExecutionFactory.create(query, unionModel).execSelect();

        entitiesURIs.addAll(ResultSetUtils.resultSetToStringList(rs, "species", "Resource"));

        return entitiesURIs;
    }

    public List<String> getMetaLayer(String entityURI) {
        ArrayList<String> entitiesURIs = new ArrayList<String>();
        String query = "";
        String select = " select distinct ?gisEntity";
        String where = " where ";
        String gbp = ""
                + "<" + entityURI + "> <http://www.fao.org/figis/flod/onto/distribution.owl#faodistribution> ?gisEntity . ";
        query = select + where + "{" + gbp + "}";
        ResultSet rs = QueryExecutionFactory.sparqlService(FLOD_EntityCollection.flodsparql, query).execSelect();
        entitiesURIs.addAll(ResultSetUtils.resultSetToStringList(rs, "gisEntity", "Resource"));

        return entitiesURIs;
    }

    public List<String> getMetaLayerSource(String entityURI) {
        ArrayList<String> entitiesURIs = new ArrayList<String>();
        String query = "";
        String select = " select distinct ?gisEntity";
        String where = " where ";
        String gbp = ""
                + "<" + entityURI + "> <http://www.fao.org/figis/flod/onto/distribution.owl#faodistribution> ?gisEntity . ?gisEntity <http://purl.org/dc/terms/source> ?source ";
        query = select + where + "{" + gbp + "}";
        ResultSet rs = QueryExecutionFactory.sparqlService(FLOD_EntityCollection.flodsparql, query).execSelect();
        entitiesURIs.clear();
        entitiesURIs.addAll(ResultSetUtils.resultSetToStringList(rs, "gisEntity", "Resource"));

        return entitiesURIs;
    }

    private void addIfnotThere(ArrayList<String> entitiesURIs, List<String> response) {
        for (String newUri : response) {
            if (!entitiesURIs.contains(newUri)) {
                entitiesURIs.add(newUri);
            }
        }
    }

    public static void main(String[] args) {
        String e_uri = "http://www.fao.org/figis/flod/entities/codedentity/f527d0db-0255-4893-b266-cf8a18b6e9ca";
        String[] species = {"http://www.fao.org/figis/flod/entities/codedentity/778517e8-eda6-458d-8029-dbb02f0e2dd2", "http://www.fao.org/figis/flod/entities/codedentity/b2ef129b-2d86-446a-83b9-e731915394c9"};
        String[] species_list = {"http://www.fao.org/figis/flod/entities/codedentity/7be56ab6-09d2-4b94-9718-ccf97d4ae495"};
        String country = "http://www.fao.org/figis/flod/entities/codedentity/35c59a75-6796-4d3a-8b00-d9520261af0e";
        String doc = "http://smartfish.collection/wiofish/189";
//        System.out.println("List Annotation Properties");
//        System.out.println(AnnotationBase.getInstance().listAnnotationProperties());
        System.out.println("List Documents");
        System.out.println(AnnotationBase.getInstance().listDocuments(country));
//        System.out.println("List Documents");
//        System.out.println(AnnotationBase.getInstance().listDocuments(species_list));
//        System.out.println("List Documents");
//        System.out.println(AnnotationBase.getInstance().listDocuments(e_uri, NodeFactory.createURI("http://smartfish.graph/wiofish")));
//        System.out.println("List document with entities");
//        System.out.println(AnnotationBase.getInstance().listDocumentsWithCoOccuringEntities("http://www.fao.org/figis/flod/entities/codedentity/f527d0db-0255-4893-b266-cf8a18b6e9ca", species));
//        System.out.println("List document with entities");
//        System.out.println(AnnotationBase.getInstance().listDocumentsWithCoOccuringEntities("http://www.fao.org/figis/flod/entities/codedentity/f527d0db-0255-4893-b266-cf8a18b6e9ca", species));
//        System.out.println("List Species in Country");
//        System.out.println(AnnotationBase.getInstance().listSpeciesInCountry(country));
//        System.out.println("List Entities of Document");
//        System.out.println(AnnotationBase.getInstance().listEntitiesOfDocument(doc, ANNOTATIONS.SPECIES));
//        System.out.println("List Entities of Property");
//        System.out.println(AnnotationBase.getInstance().listEntitiesOfProperty(ANNOTATIONS.COUNTRY));
//        try {
//            AnnotationBase.getInstance().toFile(new File("annotations.nq"));
//        } catch (FileNotFoundException ex) {
//            Logger.getLogger(AnnotationBase.class.getName()).log(Level.SEVERE, null, ex);
//        }
    }

    private QuadDataAcc makeAcc(Node gNode) {
//        new QuadDataAcc(Iter.toList(entityCollection_locale.find()))
        Iterator<Quad> find = annotationStore.find();
        QuadDataAcc quadDataAcc = new QuadDataAcc();
        quadDataAcc.setGraph(gNode);
        while (find.hasNext()) {
            Quad quad = find.next();
            quadDataAcc.addQuad(quad);;
        }
        return quadDataAcc;
    }

}
