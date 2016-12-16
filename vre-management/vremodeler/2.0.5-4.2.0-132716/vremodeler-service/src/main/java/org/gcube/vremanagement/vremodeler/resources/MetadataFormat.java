package org.gcube.vremanagement.vremodeler.resources;

import java.net.URI;
import java.util.Arrays;
import java.util.List;

import org.apache.axis.components.uuid.UUIDGen;
import org.apache.axis.components.uuid.UUIDGenFactory;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.vremanagement.vremodeler.impl.util.Listable;

public class MetadataFormat implements Listable{

	private static GCUBELog logger= new GCUBELog(MetadataFormat.class);
	
	private static UUIDGen uuidMFGEN= UUIDGenFactory.getUUIDGen();
	
	public final static String ANY_LANGUAGE="any";
	 
	
	private String id;
	private String name;
	private URI schemaURI;
	private String language;
		
	public MetadataFormat(){
		this.id= uuidMFGEN.nextUUID();
	}
	
	public MetadataFormat(String name, URI schemaURI, String language){
		this.id= uuidMFGEN.nextUUID();
		logger.trace("created a metadataFormat Object with "+id);
		this.name=name;
		this.language= language;
		this.schemaURI= schemaURI;
	}
	
	public MetadataFormat(String id,String name, URI schemaURI, String language){
		logger.trace("created a metadataFormat Object with "+id);
		this.id=id;
		this.name=name;
		this.language= language;
		this.schemaURI= schemaURI;
	}
	
	public String getId(){
		return id;
	}
	
	public String getName(){
		return name;
	}
	
	public void setName(String Name){
		name= Name;
	}
	
	public URI getSchemaURI(){
		return schemaURI;
	}
	
	public void setSchemaURI(URI schemaURI){
		this.schemaURI= schemaURI;
	}
	
	public String getLanguage(){
		return language;
	}
	
	public void setLanguage(String lng){
		this.language= lng;
	}
	
	@Override
	public boolean equals(Object o){
		MetadataFormat mf = (MetadataFormat)o;
		boolean languageControl= mf.getLanguage().compareTo(ANY_LANGUAGE)==0 || this.getLanguage().compareTo(ANY_LANGUAGE)==0 || this.getLanguage().compareTo(mf.getLanguage())==0;
		
		return ((this.name.compareTo(mf.getName())==0) &&
				(languageControl) &&
				(this.schemaURI.toString().compareTo(mf.getSchemaURI().toString())==0));
	}
	
	public List<String> getAsStringList(){
		return  Arrays.asList(new String[]{this.id, this.name, this.schemaURI.toString(), this.language});
	}
	
	
	
		
}
