package org.gcube.vremanagement.vremodeler.resources;

import java.util.Arrays;
import java.util.List;

import org.gcube.vremanagement.vremodeler.impl.util.Listable;

public class MFRelationNative implements Listable{

	private String metaCollectionID;
	private String metaFormatID;
		
	
	public MFRelationNative(String collectionId, String metaFormatId){
		this.metaCollectionID= collectionId;
		this.metaFormatID= metaFormatId;
	}
	
	public List<String> getAsStringList(){
		return Arrays.asList(new String[]{this.metaCollectionID, this.metaFormatID});
	}
	
	public String getMetadataCollectionId(){
		return this.metaCollectionID;
	}
	
	public String getMetadataFormatId(){
		return this.metaFormatID;
	}
	
	public boolean equals(Object o){
		MFRelationNative mf= (MFRelationNative) o;
		return (this.metaCollectionID.compareTo(mf.getMetadataCollectionId())==0) && (this.metaFormatID.compareTo(mf.getMetadataFormatId())==0);
	}
}
