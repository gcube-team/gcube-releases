package org.gcube.gcat.profile;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ws.rs.InternalServerErrorException;

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
	
	private static DataCalogueMetadataFormatReader getDataCalogueMetadataFormatReaderInstance() throws Exception {
		/*
		Cache<String,DataCalogueMetadataFormatReader> readerCache = CachesManager.getReaderCache();
		String context = ScopeProvider.instance.get();
		DataCalogueMetadataFormatReader reader
		if(readerCache.containsKey(context)) {
			reader = (DataCalogueMetadataFormatReader) readerCache.get(context);
		} else {
			reader = new DataCalogueMetadataFormatReader();
			readerCache.put(context, reader);
		}
		*/
		return new DataCalogueMetadataFormatReader();
	}
	
	public static void clearCache() {
		/*
		Cache<String,DataCalogueMetadataFormatReader> readerCache = CachesManager.getReaderCache();
		readerCache.clear();
		*/
	}
	
	private MetadataUtility() throws Exception{
		dataCalogueMetadataFormatReader = getDataCalogueMetadataFormatReaderInstance();
	}
	
	private static final InheritableThreadLocal<MetadataUtility> metadataUtility = new InheritableThreadLocal<MetadataUtility>() {
		
		@Override
		protected MetadataUtility initialValue() {
			try {
				return new MetadataUtility();
			} catch(Exception e) {
				throw new InternalServerErrorException("Unable to instantiate MetadataUtility.");
			}
		}
		
	};
		
	public static MetadataUtility getInstance() {
		return metadataUtility.get();
	}
	
	
	public DataCalogueMetadataFormatReader getDataCalogueMetadataFormatReader() {
		return dataCalogueMetadataFormatReader;
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
