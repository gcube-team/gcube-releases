package org.gcube.rest.index.common.entities;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.rest.index.common.entities.configuration.CollectionStatus;
import org.gcube.rest.index.common.entities.configuration.DatasourceType;
import org.gcube.rest.index.common.entities.fields.Field;
import org.gcube.rest.index.common.entities.fields.config.FieldConfig;
import org.gcube.rest.index.common.entities.fields.config.StopwordLang;


@XmlRootElement
public class CollectionInfo {

	private String id;
	private String collectionDomain;
	private String title;
	private String description;
	private Date date;
	private CollectionStatus status;
	private ExternalEndpointInfo endpoint; //that's for external, not indexed (e.g. opensearch)
	private DatasourceType datasourceType;
	private Set<Field> collectionFields;
	private Map<String, FieldConfig> collectionFieldsConfigs;
	private Set<StopwordLang> stopwords;
	
	
	@SuppressWarnings("unused")
	private CollectionInfo(){} //do not allow Collections without an id
	
	
	public CollectionInfo(String id, String collectionDomain, DatasourceType type){
		this(id, collectionDomain, type, null, null, null);
	}
	
	
	/**
	 * 
	 * @param id  -- will be stored as lowercase, due to certain limitations
	 * @param collectionDomain
	 * @param datasourceType
	 * @param title
	 * @param description
	 * @param date
	 */
	public CollectionInfo(String id, String collectionDomain, DatasourceType datasourceType, String title, String description, Date date) {
		this.id = id.toLowerCase();
		this.setCollectionDomain(collectionDomain);
		this.title = title;
		this.description = description;
		this.date = (date==null) ? new Date() : date;
		this.datasourceType = datasourceType;
		collectionFields = new HashSet<Field>();
		collectionFieldsConfigs = new HashMap<String, FieldConfig>();
		stopwords = new HashSet<StopwordLang>();
		status = CollectionStatus.OK;
	}
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}


	public Set<Field> getCollectionFields() {
		return collectionFields;
	}

	public void setCollectionFields(Set<Field> collectionFields) {
		this.collectionFields = collectionFields;
	}
	

	public String getCollectionDomain() {
		return collectionDomain;
	}

	public void setCollectionDomain(String collectionDomain) {
		this.collectionDomain = collectionDomain;
	}
	
	
	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
	}
	
	public String getDescription() {
		return description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	public DatasourceType getDatasourceType() {
		return datasourceType;
	}
	
	public void setDatasourceType(DatasourceType datasourceType) {
		this.datasourceType = datasourceType;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public Map<String, FieldConfig> getCollectionFieldsConfigs() {
		return collectionFieldsConfigs;
	}
	
	public void setCollectionFieldsConfigs(Map<String, FieldConfig> collectionFieldsConfigs) {
		this.collectionFieldsConfigs = collectionFieldsConfigs;
	}
	
	public Map<String,String> getCollectionFieldsAliases(){
		Map<String,String> aliases = new HashMap<String,String>();
		if(collectionFieldsConfigs==null) return aliases;
		for(Map.Entry<String, FieldConfig> entry : collectionFieldsConfigs.entrySet())
			aliases.put(entry.getKey(), entry.getValue().getFieldNameAlias());
		return aliases;
	}
	
	public void setCollectionFieldsAliases(Map<String,String> aliases){
		if(collectionFieldsConfigs==null)
			collectionFieldsConfigs = new HashMap<String,FieldConfig>();
		for(Map.Entry<String, String> entry : aliases.entrySet()){
			FieldConfig fc = collectionFieldsConfigs.get(entry.getKey());
			if(fc==null)
				collectionFieldsConfigs.put(entry.getKey(), new FieldConfig(entry.getValue()));
			else
				collectionFieldsConfigs.get(entry.getKey()).setFieldNameAlias(entry.getValue());;
		}
	}
	
	public ExternalEndpointInfo getEndpoint() {
		return endpoint;
	}


	public void setEndpoint(ExternalEndpointInfo endpoint) {
		this.endpoint = endpoint;
	}


	public Set<StopwordLang> getStopwords() {
		return stopwords;
	}


	public void setStopwords(Set<StopwordLang> stopwords) {
		this.stopwords = stopwords;
	}
	
	public CollectionStatus getStatus() {
		return status;
	}

	public void setStatus(CollectionStatus status) {
		this.status = status;
	}

	public boolean isValid(){
		return (id==null || id.isEmpty() || collectionDomain==null || collectionDomain.isEmpty() || datasourceType==null) 
				? false : true; 
	}
	
	@Override
	public String toString(){
		return "[\tid: "+id+"\ncollectionDomain: "+collectionDomain
				+"\nTitle: "+title+"\nDescription: "+description+"\nDate: "+date+"\nDatasourceType: "+datasourceType
				+"\ncollectionFields: "+ collectionFields.toString()
				+"\ncollectionFieldsConfigs"+collectionFieldsConfigs.toString()
				+"\ncollectionStatus: "+ status.toString()
				+"\n]";
	}
	
}
