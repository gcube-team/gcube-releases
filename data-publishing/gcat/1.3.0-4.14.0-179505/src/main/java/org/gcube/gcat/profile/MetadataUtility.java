package org.gcube.gcat.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.gcube.datacatalogue.metadatadiscovery.DataCalogueMetadataFormatReader;
import org.gcube.datacatalogue.metadatadiscovery.bean.MetadataProfile;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.MetadataFormat;
import org.gcube.datacatalogue.metadatadiscovery.bean.jaxb.NamespaceCategory;

public class MetadataUtility {
	
	private DataCalogueMetadataFormatReader dataCalogueMetadataFormatReader;
	
	/*
	 * this map contains the Metadata Profiles. The key is the name of the profile.
	 */
	private Map<String,MetadataProfile> metadataProfiles;
	
	public MetadataUtility() throws Exception{
		dataCalogueMetadataFormatReader = new DataCalogueMetadataFormatReader();
	}
	
	public void validateProfile(String xmlProfile) throws Exception {
		dataCalogueMetadataFormatReader.validateProfile(xmlProfile);
	}
	
	public Map<String, MetadataProfile> getMetadataProfiles() throws Exception{
		if(metadataProfiles==null) {
			metadataProfiles = new HashMap<>();
			List<MetadataProfile> list = dataCalogueMetadataFormatReader.getListOfMetadataProfiles();
			for(MetadataProfile profile : list) {
				metadataProfiles.put(profile.getName(), profile);
			}
		}
		return metadataProfiles;
	}
	
	/**
	 * Returns the names of the metadata profiles in a given context
	 * @param context
	 * @return
	 * @throws Exception 
	 */
	public Set<String> getProfilesNames() throws Exception {
		return getMetadataProfiles().keySet();
	}
	
	public MetadataFormat getMetadataFormat(String profileName) throws Exception {
		MetadataProfile profile = getMetadataProfiles().get(profileName);
		if(profile!=null) {
			return dataCalogueMetadataFormatReader.getMetadataFormatForMetadataProfile(profile);
		}
		return null;
	}
	
	
	public List<NamespaceCategory> getNamespaceCategories() throws Exception {
		return dataCalogueMetadataFormatReader.getListOfNamespaceCategories();
		
	}
	
}
