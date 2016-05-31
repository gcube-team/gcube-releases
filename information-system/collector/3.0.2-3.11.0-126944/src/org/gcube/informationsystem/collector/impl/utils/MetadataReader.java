package org.gcube.informationsystem.collector.impl.utils;

import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;

import org.w3c.dom.Document;

/**
 * 
 * Reader for resource's metadata
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public class MetadataReader {

    private Document metadata;
    
    public MetadataReader(Document metadata) {
	this.metadata = metadata;
    }    
    
    public String getType() {
	try {
		return this.metadata.getElementsByTagName("Type").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }

    public String getSource() {
	try {
		return this.metadata.getElementsByTagName("Source").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }

    public Calendar getTerminationTime() {
	try {
        	//value is in seconds
        	String value = this.metadata.getElementsByTagName("TimeToLive").item(0).getTextContent();
        	Calendar now = new GregorianCalendar();
        	now.setTimeZone(TimeZone.getTimeZone("GMT"));
        	//add seconds to obtain the effective termination time
        	now.add(Calendar.SECOND, Integer.valueOf(value));
        	return now;
	}catch (Exception e) {return null;}
    }

    public String getGroupKey() {
	try {
		return this.metadata.getElementsByTagName("GroupKey").item(0).getTextContent();
	}catch (Exception e) {return "";}
    }

    public String getEntryKey() {
	try {
		return this.metadata.getElementsByTagName("EntryKey").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }

    public String getKey() {
	try {
		return this.metadata.getElementsByTagName("Key").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }

    public String getPublicationMode() {
	try {
		return this.metadata.getElementsByTagName("PublicationMode").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }

    public String getNamespace() {
	try  {
	    return this.metadata.getElementsByTagName("Namespace").item(0).getTextContent();
    	}catch (Exception e) {return "";}
    }
}
