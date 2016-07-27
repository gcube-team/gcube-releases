package org.gcube.textextractor.extractors;

import gr.uoa.di.madgik.commons.server.PortRange;
import gr.uoa.di.madgik.commons.server.TCPConnectionManager;
import gr.uoa.di.madgik.commons.server.TCPConnectionManagerConfig;
import gr.uoa.di.madgik.grs.proxy.local.LocalWriterProxy;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPConnectionHandler;
import gr.uoa.di.madgik.grs.proxy.tcp.TCPStoreConnectionHandler;
import gr.uoa.di.madgik.grs.record.GenericRecord;
import gr.uoa.di.madgik.grs.record.GenericRecordDefinition;
import gr.uoa.di.madgik.grs.record.RecordDefinition;
import gr.uoa.di.madgik.grs.record.field.FieldDefinition;
import gr.uoa.di.madgik.grs.record.field.StringFieldDefinition;
import gr.uoa.di.madgik.grs.utils.Locators;
import gr.uoa.di.madgik.grs.writer.GRS2WriterException;
import gr.uoa.di.madgik.grs.writer.RecordWriter;

import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import org.gcube.rest.index.client.exceptions.IndexException;
import org.gcube.semantic.annotator.AnnotationBase;
import org.gcube.semantic.annotator.SMARTFISH_EntityCollection;
import org.gcube.textextractor.helpers.IndexHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Test {

	private static final Logger logger = LoggerFactory.getLogger(Test.class);
	
    private static String base_folder = System.getProperty("user.home") + "/"; //home
    private static String rootFolder = base_folder+"Smartfish/";
    private static String xmlRootFolder = rootFolder+"rowsets/new/";
    


    public static void main(String[] args) throws IOException {
        String path = "";
        String outputFilename = "";
        long starttime, endtime;
//        
        
        starttime = System.currentTimeMillis();
        path = rootFolder + "firms";
        outputFilename = xmlRootFolder + "smartfish_xml_rowsets.xml";
        XMLExtractor xml = new XMLExtractor();
        xml.extractInfoAndWriteToFile(path, outputFilename);

        AnnotationBase.getInstance().dump(AnnotationBase.FIRMS_annotation_file_name);
//        AnnotationBase.getInstance().toRemoteGraph(AnnotationBase.FIRMS_GRAPH_NODE);
//        SMARTFISH_EntityCollection.getInstance().toRemoteGraph();
        SMARTFISH_EntityCollection.getInstance().toFile();

        endtime = System.currentTimeMillis();
        logger.info("time for XML : " + (endtime - starttime) / 1000.0 + " secs");

        starttime = System.currentTimeMillis();
        path = rootFolder + "wiofish_pages/encoded";
        //path = "/home/alex/Smartfish/wiofish_pages/encoded/";
        
        
        outputFilename = xmlRootFolder + "wiofish_html_rowsets.xml";
        
        HTMLExtractor html = new HTMLExtractor();
        html.extractInfoAndWriteToFile(path, outputFilename);
        AnnotationBase.getInstance().dump(AnnotationBase.WIOFISH_annotation_file_name);
//        AnnotationBase.getInstance().toRemoteGraph(AnnotationBase.WIOFISH_GRAPH_NODE);
        SMARTFISH_EntityCollection.getInstance().toFile();

        endtime = System.currentTimeMillis();
        logger.info("time for HTML : " + (endtime - starttime) / 1000.0 + " secs");
        
        
        
        starttime = System.currentTimeMillis();
        path = "http://statbase.smartfish.d4science.org/statbase/rest/country";
        
        outputFilename = xmlRootFolder + "restservice_rowsets.xml";
        
        RESTServiceExtractor rest = new RESTServiceExtractor();
        rest.extractInfoAndWriteToFile(path, outputFilename);
        AnnotationBase.getInstance().dump(AnnotationBase.STATBASE_annotation_file_name);
//        AnnotationBase.getInstance().toRemoteGraph(AnnotationBase.STATBASE_GRAPH_NODE);
        SMARTFISH_EntityCollection.getInstance().toFile();

        endtime = System.currentTimeMillis();
        logger.info("time for REST : " + (endtime - starttime) / 1000.0 + " secs");

    }

    public static void feed(String[] args) throws IOException, GRS2WriterException, IndexException {

        TCPConnectionManager.Init(new TCPConnectionManagerConfig(
                "jazzman.di.uoa.gr", new ArrayList<PortRange>(), true));
        TCPConnectionManager.RegisterEntry(new TCPConnectionHandler());
        TCPConnectionManager.RegisterEntry(new TCPStoreConnectionHandler());

        RecordDefinition[] defs = null;

        defs = new RecordDefinition[]{new GenericRecordDefinition(
            (new FieldDefinition[]{new StringFieldDefinition("Rowset")}))};
        final RecordWriter<GenericRecord> writer = new RecordWriter<GenericRecord>(new LocalWriterProxy(), defs,
                RecordWriter.DefaultBufferCapacity,
                RecordWriter.DefaultConcurrentPartialCapacity,
                RecordWriter.DefaultMirrorBufferFactor, 1, TimeUnit.DAYS);

        String path = null;
        String outputFilename = null;

        final String scope = "/gcube/devsec";
        IndexHelper.createCluster(scope, 1);

        try {
            Thread.sleep(10 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        new Thread() {
            public void run() {
                String uri = getOutput(writer);
                try {
                    IndexHelper.feedIndex(scope, uri);
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }.start();

        /*path = "/home/alex/Smartfish/autocomplete/flod_entity_label.csv";
         outputFilename = "/home/alex/Smartfish/rowsets/smartfish_all_rowsets.xml";
         CSVAutocompleteExtractor csv_autocomplete = new CSVAutocompleteExtractor(path, outputFilename);
         csv_autocomplete.writeRowsetsToFile();*/
//
        path = "/home/alex/Smartfish/StatBase.1.csv";
        outputFilename = "/home/alex/Smartfish/rowsets/smartfish_all_rowsets.xml";
        CSVExtractor csv = new CSVExtractor();
        //csv.writeRowsetsToFile(outputFilename);
        csv.extractInfoAndWriteToRS(path, writer);
////		
//		
		/*path = "/home/alex/Smartfish/pdf/all/";
         outputFilename = "/home/alex/Smartfish/rowsets/smartfish_all_rowsets.xml";
         PDFExtractor pdf = new PDFExtractor(path, outputFilename);
         pdf.writeRowsetsToFile(outputFilename);*/
//		
//		

        path = "/home/alex/Smartfish/wiofish_pages/encoded";
        outputFilename = "/home/alex/Smartfish/rowsets/smartfish_all_rowsets.xml";
        HTMLExtractor html2 = new HTMLExtractor();
        //html2.writeRowsetsToFile(outputFilename);
        html2.extractInfoAndWriteToRS(path, writer);
//		
        path = "/home/alex/Smartfish/firms/";
        outputFilename = "/home/alex/Smartfish/rowsets/smartfish_all_rowsets.xml";
        XMLExtractor xml = new XMLExtractor();
        //xml.writeRowsetsToFile(outputFilename);
        xml.extractInfoAndWriteToRS(path, writer);

//		AnnotationBase.getInstance().toFile();
//		SMARTFISH_EntityCollection.getInstance().toFile();
        writer.close();

        try {
            Thread.sleep(60 * 1000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static String getOutput(RecordWriter<GenericRecord> writer) {
        try {
            URI TCPLocator = Locators.localToTCP(writer.getLocator());
            return TCPLocator.toString();
        } catch (Exception e) {
            return null;
        }
    }
}
