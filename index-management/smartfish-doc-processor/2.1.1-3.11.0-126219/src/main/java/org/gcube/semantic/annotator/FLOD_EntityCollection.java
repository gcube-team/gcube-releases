/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.graph.Triple;
import com.hp.hpl.jena.graph.TripleMatchFilter;
import com.hp.hpl.jena.query.*;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.util.iterator.ExtendedIterator;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang.StringEscapeUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import org.gcube.semantic.annotator.utils.SMART_ENTITY_TYPES;
import org.gcube.textextractor.entities.ExtractedEntity;
import org.gcube.textextractor.helpers.ExtractorHelper;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class FLOD_EntityCollection {

    private static FLOD_EntityCollection instance;
    private static Model m = ModelFactory.createDefaultModel();
    public static final String flodsparql = "http://www.fao.org/figis/flod/endpoint/flod";
    private static final Node flodGnode = NodeFactory.createURI("http://www.fao.org/figis/flod/entities");

    public static FLOD_EntityCollection getInstance() {
        if (instance == null) {
            instance = new FLOD_EntityCollection();
        }
        return instance;
    }
    private static int succesfullQuery = 0;
    private ArrayList<String> flodNameCollection = new ArrayList<String>();

    private FLOD_EntityCollection() {

//            loadList();
        System.out.println("Initializing FLOD EntityCollection");
        DatasetGraphAccessorHTTP kbAccessor = new DatasetGraphAccessorHTTP("http://dl051.madgik.di.uoa.gr:3030/chimaera_kb_update/data");
        Graph flodGraph = kbAccessor.httpGet(flodGnode);
        makeFlodNameCollection(flodGraph);
        m.add(ModelUtils.triplesToStatements(GraphUtil.findAll(flodGraph), m));
        System.out.println("Finalized initialization FLOD EntityCollection");
    }

    private void loadList() throws FileNotFoundException {
        String construct = " construct ";
        String constructPattern = "";
        constructPattern += " ?flagstateCode a <" + SMART_ENTITY_TYPES.FLAGSTATE + "> . ?flagstate <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?flagstateCode . ?flagstate <http://www.w3.org/2000/01/rdf-schema#label> ?flagstateName . ";
        constructPattern += " ?speciesCode a <" + SMART_ENTITY_TYPES.SPECIES + "> . ?species <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?speciesCode . ?species <http://www.w3.org/2000/01/rdf-schema#label> ?speciesName . ";
        constructPattern += " ?vesselCode a <" + SMART_ENTITY_TYPES.VESSEL + "> . ?vessel <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?vesselCode . ?vessel <http://www.w3.org/2000/01/rdf-schema#label> ?vesselName .";
        constructPattern += " ?gearCode a <" + SMART_ENTITY_TYPES.GEAR + "> . ?gear <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?gearCode .  ?gear <http://www.w3.org/2000/01/rdf-schema#label> ?gearName . ";

        String where = " where ";
        String pattern = "";
        pattern += "{ ?speciesCode a <" + SMART_ENTITY_TYPES.SPECIES + "> . ?species <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?speciesCode. ?species <http://www.w3.org/2000/01/rdf-schema#label> ?speciesName}";
        pattern += "union { ?vesselCode a <" + SMART_ENTITY_TYPES.VESSEL + "> .  ?vessel <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?vesselCode . ?vessel <http://www.w3.org/2000/01/rdf-schema#label> ?vesselName}";
        pattern += "union { ?gearCode a <" + SMART_ENTITY_TYPES.GEAR + "> . ?gear <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?gearCode . ?gear <http://www.w3.org/2000/01/rdf-schema#label> ?gearName}";
        pattern += "union { ?flagstateCode a <" + SMART_ENTITY_TYPES.FLAGSTATE + "> . ?flagstate <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#isClassfiedByCode> ?flagstateCode . ?flagstate <http://www.w3.org/2000/01/rdf-schema#label> ?flagstateName }";

        String query = construct + "{ " + constructPattern + " } " + where + "{ " + pattern + " }";
        Query q = QueryFactory.create(query);
        System.out.println(q.serialize());
        m = QueryExecutionFactory.sparqlService(flodsparql, q).execConstruct();
        File f = new File("flod_entity_collection.nt");
        RDFDataMgr.write(new FileOutputStream(f), m, Lang.NT);
    }

    private ResultSet label4uri(String uri, String lang) {
        String filter = "";
        String select = " select distinct ?uri ?label_str ?lang";
        String where = " where ";
        String gbpattern
                = "BIND (IRI('" + uri + "') as ?uri) "
                + " ?uri rdfs:label ?label_lang . "
                + "BIND (str(?label_lang) as ?label_str) "
                + "BIND (lang(?label_lang) as ?lang) ";

        if (!"".equals(lang)) {
            filter += "filter(?lang = '" + lang + "')";
        } else {
            filter += "filter(?lang = 'en' || ?lang = 'fr' || ?lang = 'la')";
        }
        return QueryExecutionFactory.create(prefixes + select + where + "{" + gbpattern + filter + "}", m).execSelect();
    }

    private boolean isOfType(String entityURI, String typeURI) {
        String query
                = "  ASK "
                + " "
                + "{"
                + "<" + entityURI + "> ce_cls:isClassfiedByCode  ?code ."
                + "?code rdf:type ?<" + typeURI + "> ."
                + "}";
        return QueryExecutionFactory.create(prefixes + query, m).execAsk();

    }

    private ResultSetMem ce4name(String name, String resType) {
        name = sanitize(name);
        String where = "{"
                + "?code rdf:type <" + resType + "> ."
                + "?uri ce_cls:isClassfiedByCode  ?code ."
                + "?uri rdfs:label ?label . "
                + "filter(str(?label) = '" + name.toLowerCase() + "') "
                + "?uri rdfs:label ?label_lang . "
                + "BIND (str(?label_lang) as ?label_str) "
                + "BIND (lang(?label_lang) as ?lang) "
                + "filter(?lang = 'en' || ?lang = 'fr' || ?lang = 'la')"
                + "}";
//        String queryAsk = "ASK "+ where;
//        boolean exisist = QueryExecutionFactory.create(prefixes + queryAsk, m).execAsk();
//        if(exisist){
        String querySelect = "  SELECT distinct ?uri ?label_str ?lang WHERE " + where;
        return new ResultSetMem(QueryExecutionFactory.create(prefixes + querySelect, m).execSelect());
//        }
//        else
//            return null;
    }

    private List<Triple> describe(String entityURI, String lang, boolean remote, TripleMatchFilter filter) {
        Iterator<Triple> describe = QueryExecutionFactory.sparqlService(flodsparql, "describe <" + entityURI + ">").execDescribeTriples();
        return filter.filterKeep(describe).toList();

    }

    private JsonObject triplesToJsonObject(Iterator<Triple> triples) {
        JsonObject jo = new JsonObject();
        while (triples.hasNext()) {
            Triple triple = triples.next();
            String sbj = triple.getSubject().getURI();
            String p = triple.getPredicate().getLocalName();
            String obj;
            Node objNode = triple.getObject();
            if (objNode.isLiteral()) {
                p = objNode.getLiteral().language().isEmpty() ? p : p + "_" + objNode.getLiteral().language();
                obj = objNode.getLiteralValue().toString();
            } else {
                obj = objNode.getURI();
            }
            if (!obj.isEmpty()) {
                jo.addProperty(p, obj);
            }
        }
        return jo;
    }

    public String describeEntity(String entityURI, String lang, boolean remote, TripleMatchFilter filter) {
        if (filter == null) {
            filter = new TripleMatchFilter((Triple.ANY));
        }
        List<Triple> triples = describe(entityURI, lang, remote, filter);
        return triplesToJsonObject(triples.iterator()).toString();
    }

    public String describeEntity(List<String> entityURI, String lang, boolean remote, TripleMatchFilter filter) {
        JsonArray ja = new JsonArray();
        if (filter == null) {
            filter = new TripleMatchFilter((Triple.ANY));
        }
        for (String uri : entityURI) {
            ja.add(triplesToJsonObject(describe(uri, lang, remote, filter).iterator()));
        }
        return ja.toString();
    }

    public String infobox(String entityURI, String lang) {
        Node entityNode = NodeFactory.createURI(entityURI);
        TripleMatchFilter filter = new TripleMatchFilter(Triple.create(entityNode, RDFS.label.asNode(), Node.ANY));
        List<Triple> describe = describe(entityURI, lang, true, filter);
        String entityType = isSpecies(entityURI) ? "species" : "country";
        describe.add(Triple.create(entityNode, NodeFactory.createURI("http://temp#entity_type"), NodeFactory.createLiteral(String.valueOf(entityType))));
        int sizeInFIRMS = AnnotationBase.getInstance().listDocumentsInFIRMS(entityURI).size();
        int sizeInStatBase = AnnotationBase.getInstance().listDocumentsInSTATBASE(entityURI).size();
        int sizeWIOFISH = AnnotationBase.getInstance().listDocumentsInWIOFISH(entityURI).size();
        describe.add(Triple.create(entityNode, NodeFactory.createURI("http://temp#wiofish_size"), NodeFactory.createLiteral(String.valueOf(sizeWIOFISH))));
        describe.add(Triple.create(entityNode, NodeFactory.createURI("http://temp#statbase_size"), NodeFactory.createLiteral(String.valueOf(sizeInStatBase))));
        describe.add(Triple.create(entityNode, NodeFactory.createURI("http://temp#firms_size"), NodeFactory.createLiteral(String.valueOf(sizeInFIRMS))));
        List<String> metaLayers = AnnotationBase.getInstance().getMetaLayerSource(entityURI);
        for (String uri : metaLayers) {
            describe.add(Triple.create(entityNode, NodeFactory.createURI("http://temp#matalayer"), NodeFactory.createURI(uri)));
        }
        return triplesToJsonObject(describe.iterator()).toString();
    }

    public String getURI(ExtractedEntity e, String resType) {
        ResultSet rs = getURIrs(e, resType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rs);
        return bos.toString();
    }

    private ResultSet getURIrs(ExtractedEntity e, String resType) {
        String ref_name = e.en_name.isEmpty() ? e.fr_name : e.en_name;
        if (isInFlodCollection(ref_name)) {
            return ce4name(ref_name, resType);
        } else {
            return SMARTFISH_EntityCollection.getInstance().getURIrs(e, resType);
        }

    }

    public String getURI(ExtractedEntity ees[], String resType) {
        ResultSet[] rsList = new ResultSet[ees.length];
        ResultSetMem rsm = new ResultSetMem();
        for (int i = 0; i < ees.length; i++) {
            ExtractedEntity e = ees[i];
            rsList[i] = getURIrs(e, resType);
        }
        if (rsList.length > 0) {
            rsm = (ResultSetMem) ResultSetUtils.union(rsList);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rsm);
        return bos.toString();
    }

    public String getLabel(String uri, String lang) {
        ResultSet rs = label4uri(uri, lang);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rs);
        return bos.toString();
    }

    public boolean isSpecies(String uri) {
        return isOfType(uri, SMART_ENTITY_TYPES.SPECIES);
    }

    public String getLabel(List<String> entitiesURIs, String lang) {
        ResultSet[] rsList = new ResultSet[entitiesURIs.size()];
        ResultSetMem rsm = new ResultSetMem();
        for (int i = 0; i < entitiesURIs.size(); i++) {
            String uri = entitiesURIs.get(i);
            ResultSet rs = label4uri(uri, lang);
            if (uri.contains("smartfish.d4science.org")) {
                String labelResult = SMARTFISH_EntityCollection.getInstance().getLabel(uri, lang);
                rs = ResultSetFactory.fromJSON(new ByteArrayInputStream(labelResult.getBytes()));
            }
            rsList[i] = rs;
        }
        if (rsList.length > 0) {
            rsm = (ResultSetMem) ResultSetUtils.union(rsList);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rsm);
        return bos.toString();
    }

    public static String getValueFromJson(String sparqlJson, String varName) {
        try {
            return ExtractorHelper.parseJsonRequest(sparqlJson).get(0).uri;
        } catch (Exception ex) {
            Logger.getLogger(FLOD_EntityCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";
    }

    static String prefixes = ""
            + "PREFIX rdf:        <http://www.w3.org/1999/02/22-rdf-syntax-ns#> "
            + "PREFIX rdfs:	  <http://www.w3.org/2000/01/rdf-schema#> "
            + "PREFIX owl:        <http://www.w3.org/2002/07/owl#> "
            + "PREFIX xsd:        <http://www.w3.org/2001/XMLSchema#> "
            + "PREFIX csv:        <http://www.fao.org/figis/flod/csv/> "
            + "PREFIX fn:         <http://www.w3.org/2005/xpath-functions#> "
            + "PREFIX afn:        <http://jena.hpl.hp.com/ARQ/function#> "
            + "PREFIX dc:         <http://purl.org/dc/elements/1.1/>"
            + "PREFIX xsd:        <http://www.w3.org/2001/XMLSchema#>"
            + "PREFIX sys:        <http://www.fao.org/figis/flod/onto/codedentitycollection.owl#>"
            + "PREFIX rfb:        <http://www.fao.org/figis/flod/onto/rfb.owl#>"
            + "PREFIX dbp:        <http://dbpedia.org/property/>"
            + "PREFIX dcterms:    <http://purl.org/dc/terms/>"
            + "PREFIX dwc:        <http://rs.tdwg.org/dwc/terms/>"
            + "PREFIX cls:        <http://www.ontologydesignpatterns.org/cp/owl/classification.owl#> "
            + "PREFIX ce_cls:     <http://www.fao.org/figis/flod/onto/codedentityclassification.owl#> ";

    public static void main(String[] args) {
        FLOD_EntityCollection.getInstance();
//        String[] names = {"atlantic bluefin tuna", "atlantic cod"};
//        System.out.println(
//                //                FLOD_EntityCollection.getInstance().getURI("atlantic bluefin tuna", FLOD_CODE_TYPES.SPECIES));
//                //                FLOD_EntityCollection.getInstance().getURI("Trolling Lines / Lignes de traîne", FLOD_CODE_TYPES.GEAR)
//                FLOD_EntityCollection.getInstance().getLabel("http://www.fao.org/figis/flod/entities/codedentity/fb7c317e-c1ff-4df9-8e99-d9c8907fc1b5", "en"));
//        String entityURI = FLOD_EntityCollection.getInstance().getURI("kenya", FLOD_CODE_TYPES.FLAGSTATE);
//        String entityURI = "http://www.fao.org/figis/flod/entities/codedentity/c1688b54-2973-468c-8fda-df39c53d609e";
//        System.out.println(entityURI);
//        String describe = FLOD_EntityCollection.getInstance().infobox(entityURI, "");
//        String describe = FLOD_EntityCollection.getInstance().getURI("Scavengers", "");
//        System.out.println(describe);

    }

    private String sanitize(String name) {
        name = StringUtils.remove(name, "/");
        name = StringEscapeUtils.escapeJavaScript(name.trim());
        return name;
    }

    private boolean isInFlodCollection(String ref_name) {
        boolean found =  Collections.binarySearch(flodNameCollection, ref_name.toLowerCase()) >= 0;
        if (found) {
            System.out.println("Found in flod :" +ref_name);
        }
        return found;
    }

    
    private void makeFlodNameCollection(Graph flodGraph) {
        ExtendedIterator<Triple> find = flodGraph.find(Node.ANY, RDFS.label.asNode(), Node.ANY);
        while (find.hasNext()) {
            Triple triple = find.next();
            flodNameCollection.add(triple.getObject().getLiteralLexicalForm());
        }
        Collections.sort(flodNameCollection);
    }
}
