package org.gcube.informationsystem.collector.stubs.metadata;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

/**
 * 
 * Metadata Record for gCube Profiles and Instance States
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */

public final class MetadataRecord {     
    
    public static enum  TYPE {INSTANCESTATE("InstanceState"),GCUBERESOURCE("Profile");
	String name;
	TYPE(String name) {this.name = name;}
	public String toString() {return this.name;}
    
    };
    
    static GCUBELog logger = new GCUBELog(MetadataRecord.class);
    
    private Calendar terminationTime;
    
    private Integer time;
    
    private String source, key, groupKey, entryKey;

    private TYPE type;

    private String publicationMode = "";

    private String namespace = "";
    
    
    protected MetadataRecord() {}
    
    /**
     * @return the getTerminationTime
     */
    public Calendar getGetTerminationTime() {
        return terminationTime;
    }

    /**
     * @param time this amount of time (in seconds) will be added to the current time to determine the 
     * 				resource's termination time
     */
    public void setTimeToLive(Integer time) {
	terminationTime = new GregorianCalendar();
	terminationTime.setTimeZone(TimeZone.getTimeZone("GMT"));
	//add seconds to obtain the effective termination time
	terminationTime.add(Calendar.SECOND, time);	
        this.time = time;
    }

    /**
     * @return the source
     */
    public String getSource() {
        return source;
    }

    /**
     * @param source the source to set
     */
    public void setSource(String source) {
        this.source = source;
    }

    /**
     * @return the key
     */
    public String getKey() {
        return key;
    }

    /**
     * @param key the key to set
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * @return the groupKey
     */
    public String getGroupKey() {
        return groupKey;
    }

    /**
     * @param groupKey the groupKey to set
     */
    public void setGroupKey(String groupKey) {
        this.groupKey = groupKey;
    }

    /**
     * @return the entryKey
     */
    public String getEntryKey() {
        return entryKey;
    }

    /**
     * @param entryKey the entryKey to set
     */
    public void setEntryKey(String entryKey) {
        this.entryKey = entryKey;
    }

    /**
     * @return the type
     */
    public TYPE getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(TYPE type) {
        this.type = type;
    }

    /**
     * 
     * @return the time to live in seconds
     */
    public Integer getTimeToLive() {	
	return this.time;
    }
    
    /**
     * Gets a {@link Document} representation of the record
     * @return a document object representing the record as XML document
     * @throws SAXException
     * @throws IOException
     * @throws ParserConfigurationException
     */
    public Document getAsDocument() 
	throws SAXException, IOException, ParserConfigurationException {
	DocumentBuilderFactory factory = DocumentBuilderFactory
	    .newInstance();
	factory.setNamespaceAware(false);
	return  factory
	    .newDocumentBuilder()
	    .parse(new ByteArrayInputStream(getAsBuilder().toString().getBytes("UTF-8")));	
    }


    /**
     * {@inheritDoc}
     */
    public String toString() {	
	return getAsBuilder().toString();
    }
    
    /**
     * 
     * @return a {@link StringBuilder} holding the string serialization of the record
     */
    private StringBuilder getAsBuilder() {
	StringBuilder builder = new StringBuilder();
	builder.append("<Metadata>");
	builder.append("<Type>").append(this.getType().toString()).append("</Type>");
	builder.append("<Source>").append(this.getSource()).append("</Source>");
	builder.append("<TimeToLive>").append(this.getTimeToLive()).append("</TimeToLive>");
	builder.append("<GroupKey>").append(this.getGroupKey()).append("</GroupKey>");
	builder.append("<EntryKey>").append(this.getEntryKey()).append("</EntryKey>");
	builder.append("<Namespace>").append(this.getNamespace()).append("</Namespace>");
	builder.append("<Key>").append(this.getKey()).append("</Key>");
	builder.append("<PublicationMode>").append(this.getPublicationMode()).append("</PublicationMode>");
	builder.append("</Metadata>");
	//logger.info("Metadata document " + builder.toString());
	return builder;
    }

    private Object getNamespace() {
	return this.namespace;
    }

    public String getPublicationMode() {
	return this.publicationMode;
    }

    public void setPublicationMode(String publicationMode) {
	this.publicationMode  = publicationMode;
	
    }

    public void setNamespace(String namespace) {
	this.namespace = namespace;
	
    }

}
