package org.gcube.spatial.data.geonetwork.iso.tpl;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.ToString;

@Data
@AllArgsConstructor
@ToString
public class Company {

	private String name;
	private String organization;
	private String email;
	private String site;
	
}
