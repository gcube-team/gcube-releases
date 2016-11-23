package org.gcube.informationsystem.collector.stubs.metadata;

import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord.TYPE;
import org.gcube.informationsystem.collector.stubs.metadata.MetadataRecord;


/**
 * 
 * A metadata writer for IC records
 *
 * @author Manuele Simi (ISTI-CNR)
 *
 */
public final class MetadataWriter {      
    
    private MetadataRecord metadata;
    
    /**
     * Creates a new writer
     * @param type
     * @param source
     * @param timeToLive lifetime in seconds
     * @param groupkey
     * @param key
     * @param entrykey
     * @param publicationMode the mode in which the resource is published
     */
    public MetadataWriter(TYPE type, String source, 
	    Integer timeToLive, String groupkey, String key, String entrykey, String namespace, String publicationMode) {
	this.metadata = new MetadataRecord();
	this.metadata.setType(type);
	this.metadata.setSource(source);
	this.metadata.setEntryKey(entrykey);
	this.metadata.setGroupKey(groupkey);
	this.metadata.setKey(key);
	this.metadata.setTimeToLive(timeToLive);
	this.metadata.setPublicationMode(publicationMode);
	this.metadata.setNamespace(namespace);
    }

    public MetadataRecord getRecord() {
	return this.metadata;
    }

 }
