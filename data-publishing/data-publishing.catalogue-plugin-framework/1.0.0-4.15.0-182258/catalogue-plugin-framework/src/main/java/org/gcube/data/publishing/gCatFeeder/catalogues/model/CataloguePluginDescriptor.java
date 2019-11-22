package org.gcube.data.publishing.gCatFeeder.catalogues.model;

import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
@Getter
@Setter
public class CataloguePluginDescriptor {

	@NonNull
	private String id;
	
	
}
