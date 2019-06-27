package org.gcube.data.publishing.gCatFeeder.catalogues.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@RequiredArgsConstructor
@Setter
public class PublishReport {
	
	
	@NonNull
	private Boolean successful;
	
	@NonNull
	private String publishedId;
	
}
