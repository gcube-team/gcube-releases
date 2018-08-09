package org.gcube.data.transfer.plugins.thredds;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@AllArgsConstructor
public class CatalogDescriptor {

	private String ID;
	private String name;
	private String title;
	private String catalogFile;	
	
}
