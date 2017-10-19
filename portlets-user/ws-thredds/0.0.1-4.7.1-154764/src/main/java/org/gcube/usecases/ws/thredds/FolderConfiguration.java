package org.gcube.usecases.ws.thredds;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@RequiredArgsConstructor
@ToString
public class FolderConfiguration {
	
	public FolderConfiguration(FolderConfiguration other) {
		this.providedMetadata=other.providedMetadata;
		this.includeSubfolders=other.includeSubfolders;
		this.publishingUserToken=other.publishingUserToken;
		this.folderId=other.folderId;
		this.catalogName=other.catalogName;
		this.metadataFolderId=other.metadataFolderId;
	}
	
	
	
	private boolean providedMetadata=false;
	private boolean includeSubfolders=true;
	
	
	@NonNull
	private String publishingUserToken;	
	
	@NonNull
	private String folderId;
	@NonNull
	private String catalogName;
	
	
	private String metadataFolderId=null;
}
