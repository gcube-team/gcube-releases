package org.gcube.gcat.workspace;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.gcube.common.storagehub.model.Metadata;
import org.gcube.storagehub.MetadataMatcher;

public class CatalogueMetadata implements MetadataMatcher {

	public static final String ORIGINAL_URL = "OriginalURL";
	public static final String ORIGINAL_NAME = "OriginalName";
	
	public static final String CATALOGUE_ITEM_ID = "CatalogueItemID";
	public static final String CATALOGUE_RESOURCE_ID = "CatalogueResourceID";
	public static final String CATALOGUE_RESOURCE_REVISION_ID = "CatalogueResourceRevisionID";
	
	protected String itemID;
	
	public CatalogueMetadata(String itemID) {
		this.itemID = itemID;
	}
	
	@Override
	public boolean check(Metadata metadata) {
		Map<String,Object> map = metadata.getMap();
		if(map.get(CATALOGUE_ITEM_ID).toString().compareTo(itemID) == 0) {
			return true;
		}
		return false;
	}
	
	public Metadata getMetadata(URL url, String originalName, String resourceID) {
		Map<String,Object> map = new HashMap<>();
		map.put(ORIGINAL_URL, url.toString());
		map.put(ORIGINAL_NAME, originalName);
		map.put(CATALOGUE_ITEM_ID, itemID);
		map.put(CATALOGUE_RESOURCE_ID, resourceID);
		Metadata metadata = new Metadata(map);
		return metadata;
	}
	
}
