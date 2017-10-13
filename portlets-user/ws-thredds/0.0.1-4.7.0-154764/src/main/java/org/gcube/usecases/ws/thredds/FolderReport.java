package org.gcube.usecases.ws.thredds;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
public class FolderReport {

	@NonNull
	private FolderConfiguration config;
	private Set<String> transferredFiles=new HashSet<>();
	
	/* map file->uuid*/
	private Map<String,String> generatedMetadata=new HashMap<>();
	private Set<String> publishedMetadata=new HashSet<>();
	
	
	
	
}
