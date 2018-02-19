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
	
	private String remotePersistence="thredds";
	
}
