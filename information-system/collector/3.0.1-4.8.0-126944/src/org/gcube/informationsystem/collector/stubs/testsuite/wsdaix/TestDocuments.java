package org.gcube.informationsystem.collector.stubs.testsuite.wsdaix;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.*;

/**
 * 
 * Loads the XML documents used in tests.
 * 
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class TestDocuments {

    private static Logger mLog = Logger.getLogger(TestDocuments.class);
    
    /**
     * Loads an XML document with the specified filename
     */
    protected static Document loadDocument(String filename) {
        try {
            File file = new File(filename);
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            dbf.setNamespaceAware(true);
            DocumentBuilder db = dbf.newDocumentBuilder();
            return db.parse(file);
            
        } catch ( Exception e ) {
            mLog.error("Unable to load document: " + filename);    
        }
        return null;
    }
    
    
}
