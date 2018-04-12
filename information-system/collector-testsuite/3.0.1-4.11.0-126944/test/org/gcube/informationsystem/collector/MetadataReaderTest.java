package org.gcube.informationsystem.collector;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.informationsystem.collector.impl.utils.MetadataReader;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


import junit.framework.TestCase;

public class MetadataReaderTest extends TestCase {
    
    MetadataReader reader;

    protected void setUp() throws Exception {
	Document metadata = this.createMetadata("Profile", "MySource", "600", "MyGroup", "MyKey",
		"MyEntry");
	reader = new MetadataReader(metadata);
    }

    protected void tearDown() throws Exception {
	super.tearDown();
    }

    public void testGetType() {
	System.out.println("Type: " + reader.getType());
    }

    public void testGetSource() {
	System.out.println("Source: " + reader.getSource());
    }

    public void testGetTerminationTime() {
    	System.out.println("Termination time: " + reader.getTerminationTime().getTime().toString());
    }

    public void testGetGroupKey() {
	System.out.println("Group Key: " + reader.getGroupKey());
    }

    public void testGetEntryKey() {
	System.out.println("Entry Key: " + reader.getEntryKey());
    }

    public void testGetKey() {
	System.out.println("Key: " + reader.getKey());
    }

    private Document createMetadata(String type, String source, 
	    String tt, String groupkey, String key, String entrykey) 
    	throws SAXException, IOException, ParserConfigurationException {
	
	StringBuilder builder = new StringBuilder();
	builder.append("<Metadata>");
	builder.append("<Type>").append(type).append("</Type>");
	builder.append("<Source>").append(source).append("</Source>");
	builder.append("<TerminationTime>").append(tt).append("</TerminationTime>");
	builder.append("<GroupKey>").append(groupkey).append("</GroupKey>");
	builder.append("<EntryKey>").append(entrykey).append("</EntryKey>");
	builder.append("<Key>").append(key).append("</Key>");
	builder.append("</Metadata>");
	builder.toString();
	
	return  DocumentBuilderFactory
	    .newInstance()
	    .newDocumentBuilder()
	    .parse(new ByteArrayInputStream(builder.toString().getBytes("UTF-8")));

	
    }

}
