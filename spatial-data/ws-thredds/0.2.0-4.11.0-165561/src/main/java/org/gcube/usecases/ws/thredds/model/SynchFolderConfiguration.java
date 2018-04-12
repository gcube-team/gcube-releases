package org.gcube.usecases.ws.thredds.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@RequiredArgsConstructor
public class SynchFolderConfiguration {
	
	@NonNull
	private String remotePath;
	@NonNull
	private String filter;
	@NonNull
	private String targetToken;
	
	
	@NonNull
	private String toCreateCatalogName;
	
	private String remotePersistence="thredds";
	
	@NonNull
	private Boolean validateMetadata=true;
	
	@NonNull
	private String rootFolderId;
	
	public boolean matchesFilter(String name) {
		return name.endsWith(".nc")||name.endsWith(".ncml")||name.endsWith(".asc");	
	}
}
