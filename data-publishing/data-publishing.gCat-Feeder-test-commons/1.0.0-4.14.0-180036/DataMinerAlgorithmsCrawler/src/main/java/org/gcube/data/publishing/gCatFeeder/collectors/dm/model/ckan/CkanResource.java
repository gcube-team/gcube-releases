package org.gcube.data.publishing.gCatFeeder.collectors.dm.model.ckan;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CkanResource {

	private String name;
	private String url;
	private String format;
	private String description;
	
}
