/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.gcube.semantic.annotator;

import com.hp.hpl.jena.graph.Graph;
import com.hp.hpl.jena.graph.GraphUtil;
import com.hp.hpl.jena.graph.Node;
import com.hp.hpl.jena.graph.NodeFactory;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFormatter;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.ModelFactory;
import com.hp.hpl.jena.sparql.core.DatasetGraph;
import com.hp.hpl.jena.sparql.core.DatasetGraphFactory;
import com.hp.hpl.jena.sparql.core.Quad;
import com.hp.hpl.jena.sparql.modify.request.QuadDataAcc;
import com.hp.hpl.jena.sparql.modify.request.UpdateClear;
import com.hp.hpl.jena.sparql.modify.request.UpdateCreate;
import com.hp.hpl.jena.sparql.modify.request.UpdateDataInsert;
import com.hp.hpl.jena.sparql.resultset.ResultSetMem;
import com.hp.hpl.jena.sparql.util.Context;
import com.hp.hpl.jena.sparql.util.ModelUtils;
import com.hp.hpl.jena.sparql.util.ResultSetUtils;
import com.hp.hpl.jena.update.UpdateExecutionFactory;
import com.hp.hpl.jena.vocabulary.RDF;
import com.hp.hpl.jena.vocabulary.RDFS;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.jena.riot.RiotWriter;
import org.apache.jena.web.DatasetGraphAccessorHTTP;
import static org.gcube.semantic.annotator.FLOD_EntityCollection.prefixes;
import org.gcube.textextractor.entities.ExtractedEntity;

/**
 *
 * @author Claudio Baldassarre <c.baldassarre@me.com>
 */
public class SMARTFISH_EntityCollection {

    private static SMARTFISH_EntityCollection instance;
    private static DatasetGraph entityCollection_locale = DatasetGraphFactory.createMem();
    private static String NS = "http://smartfish.d4science.org/entities/";
    private final Node chimaeraKBnode = NodeFactory.createURI("http://smartfish.d4science.org/entities");
    private static Model m = ModelFactory.createDefaultModel();
    private final String remoteUpdateEndpoint = "http://dl051.madgik.di.uoa.gr:3030/chimaera_kb_update/update";
    private final DatasetGraphAccessorHTTP kbAccressor;
    private HashMap registry = new HashMap<String, ExtractedEntity>();

    private SMARTFISH_EntityCollection() {
        System.out.println("Initializing SMARTFISH EntityCollection");
        kbAccressor = new DatasetGraphAccessorHTTP("http://dl051.madgik.di.uoa.gr:3030/chimaera_kb_update/data");
//        Graph chimaeraGraph = kbAccressor.httpGet(chimaeraKBnode);
        Graph chimaeraGraph = null;
        if (chimaeraGraph == null) {
            chimaeraGraph = Graph.emptyGraph;
        }
        m.add(ModelUtils.triplesToStatements(GraphUtil.findAll(chimaeraGraph), m));
        System.out.println("Finalized initialization SMARTFISH EntityCollection");
    }

    public static SMARTFISH_EntityCollection getInstance() {
        if (instance == null) {
            instance = new SMARTFISH_EntityCollection();
        }
        return instance;
    }

    private String addInstance(ExtractedEntity e, String typeURI) throws NoSuchAlgorithmException {
        System.out.println("Adding entity to smartfish_kb for : " + e.toString());
        Node graphNode = NodeFactory.createURI("http://smartfish.d4science.org/entities");
        Node typeNode = NodeFactory.createURI(typeURI);
        String entity_uri = SMARTFISH_EntityCollection.NS + typeNode.getLocalName().toLowerCase() + "/" + e.uri_localName;
        Node entity_node = NodeFactory.createURI(entity_uri);
        entityCollection_locale.add(graphNode, entity_node, RDF.type.asNode(), typeNode);
        m.add(m.createResource(entity_uri), RDF.type, m.createResource(typeURI));
        if (e.en_name != null && !e.en_name.isEmpty()) {
            entityCollection_locale.add(graphNode, entity_node, RDFS.label.asNode(), NodeFactory.createLiteral(e.en_name, "en", false));
            m.add(m.createResource(entity_uri), RDFS.label, m.createLiteral(e.en_name, "en"));
        }
        if (e.fr_name != null && !e.fr_name.isEmpty()) {
            entityCollection_locale.add(graphNode, entity_node, RDFS.label.asNode(), NodeFactory.createLiteral(e.fr_name, "fr", false));
            m.add(m.createResource(entity_uri), RDFS.label, m.createLiteral(e.fr_name, "fr"));
        }
        return entity_uri;
    }

    public File toFile() throws UnsupportedEncodingException, FileNotFoundException {

        File f = new File("smartfish_kb.nq");
        FileOutputStream fos = new FileOutputStream(f);
        Writer out = new OutputStreamWriter(fos, "UTF8");
        RiotWriter.createNTriples().write(fos, entityCollection_locale.getGraph(chimaeraKBnode), null, null, Context.emptyContext);
        return f;
    }

    public void toRemoteGraph() throws FileNotFoundException {
        Node tempGraph = NodeFactory.createURI(chimaeraKBnode.toString() + "_temp");
        if (kbAccressor.httpGet(tempGraph) == null) {
            UpdateCreate create = new UpdateCreate(tempGraph, false);
            UpdateExecutionFactory.createRemoteForm(create, remoteUpdateEndpoint).execute();
        }
        UpdateClear clear = new UpdateClear(tempGraph);
        UpdateExecutionFactory.createRemoteForm(clear, remoteUpdateEndpoint).execute();
        UpdateDataInsert insert = new UpdateDataInsert(makeAcc(tempGraph));
        UpdateExecutionFactory.createRemoteForm(insert, remoteUpdateEndpoint).execute();
    }

    public File dump(File f) throws FileNotFoundException {
        FileOutputStream fos = new FileOutputStream(f);
        RiotWriter.createNQuads().write(fos, entityCollection_locale, null, null, entityCollection_locale.getContext());
        entityCollection_locale.deleteAny(Node.ANY, Node.ANY, Node.ANY, Node.ANY);
        return f;
    }

    public String getURI(ExtractedEntity e, String resType) {
        ResultSet rs = getURIrs(e, resType);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rs);
        return bos.toString();
    }

    public ResultSet getURIrs(ExtractedEntity e, String resType) {
        String ref_name = e.en_name.isEmpty() ? e.fr_name : e.en_name;
        addIfNotThere(e, resType);
        ResultSet rs = ce4name(ref_name, resType);
        return rs;
    }

    public String getURI(ExtractedEntity ees[], String resType) {
        ResultSet[] rsList = new ResultSet[ees.length];
        for (int i = 0; i < ees.length; i++) {
            ExtractedEntity e = ees[i];
            if (!e.isEmpty()) {
                rsList[i] = getURIrs(e, resType);
            }
        }
        ResultSet rsm = ResultSetUtils.union(rsList);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rsm);
        return bos.toString();
    }

    private ResultSet ce4name(String name, String resType) {
        String sanitized_name = sanitize(name);
        String BGP = "{"
                //                + "graph <http://smartfish.d4science.org/entities>{"
                + "?uri rdf:type <" + resType + "> ."
                + "?uri rdfs:label ?label . "
                + "filter(str(?label) = '" + sanitized_name + "') "
                + "?uri rdfs:label ?label_lang . "
                + "BIND (str(?label_lang) as ?label_str) "
                + "BIND (lang(?label_lang) as ?lang) "
                + "filter(?lang = 'en' || ?lang = 'fr' )"
                + "}";
//        String queryAsk = "ASK "+ BGP;
//        boolean exisist = QueryExecutionFactory.create(prefixes + queryAsk, m).execAsk();
//        if(exisist){
        String querySelect = " SELECT distinct ?uri ?label_str ?lang WHERE " + BGP;
        return QueryExecutionFactory.create(prefixes + querySelect, m).execSelect();

    }

    public String getLabel(String uri, String lang) {
        ResultSet rs = label4uri(uri, lang);
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rs);
        return bos.toString();
    }

    public String getLabel(List<String> entitiesURIs, String lang) {
        ResultSet[] rsList = new ResultSet[entitiesURIs.size()];
        ResultSetMem rsm = new ResultSetMem();
        for (int i = 0; i < entitiesURIs.size(); i++) {
            String uri = entitiesURIs.get(i);
            ResultSet rs = label4uri(uri, lang);
        }
        if (rsList.length > 0) {
            rsm = (ResultSetMem) ResultSetUtils.union(rsList);
        }
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ResultSetFormatter.outputAsJSON(bos, rsm);
        return bos.toString();
    }

    private ResultSet label4uri(String uri, String lang) {
        String filter = "";
        String select = " select distinct ?uri ?label_str ?lang";
        String where = " where ";
//        String graph = " graph <http://smartfish.d4science.org/entities>";
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
        Query q = QueryFactory.create(prefixes + select + where + "{" + gbpattern + filter + " }");
        return QueryExecutionFactory.create(q, m).execSelect();
    }

    private String sanitize(String name) {
        if (name.contains("/")) {
            name = name.split("/")[0].trim();
        }
        name = name.replace("'", "\\'");
        return name;
    }

    public static void main(String[] args) throws FileNotFoundException {
        SMARTFISH_EntityCollection.getInstance().toRemoteGraph();
//        String uri = SMARTFISH_EntityCollection.getInstance().getURI("No Vessel Used / Aucun navire utilisé", "http://www.fao.org/figis/flod/onto/vessel.owl#VesselCode");
//        ResultSet ce4name = SMARTFISH_EntityCollection.getInstance().ce4name("Vanderbilt's chromis", "http://www.fao.org/figis/flod/onto/linneanspecies.owl#SpeciesCode");
//        System.out.println(ResultSetFormatter.asText(ce4name));
    }

    private ExtractedEntity addIfNotThere(ExtractedEntity e, String resType) {
        try {
            Node typeNode = NodeFactory.createURI(resType);
            String entity_uri = SMARTFISH_EntityCollection.NS + typeNode.getLocalName().toLowerCase() + "/" + e.uri_localName;
            if (!e.isEmpty() && !registry.containsKey(entity_uri)) {
                String eUri = addInstance(e, resType);
                registry.put(eUri, e);
            }
        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(SMARTFISH_EntityCollection.class.getName()).log(Level.SEVERE, null, ex);
        }
        return e;
    }

    private QuadDataAcc makeAcc(Node gNode) {
//        new QuadDataAcc(Iter.toList(entityCollection_locale.find()))
        Iterator<Quad> find = entityCollection_locale.find();
        QuadDataAcc quadDataAcc = new QuadDataAcc();
        quadDataAcc.setGraph(gNode);
        while (find.hasNext()) {
            Quad quad = find.next();
            quadDataAcc.addQuad(quad);;
        }
        return quadDataAcc;
    }

}
