package org.gcube.data.publishing.gCatFeeder.collectors.dm.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserIdentity {

	private String firstName;
	private String lastName;
	private String email;
	private String orcid;

	public String asStringValue() {
		StringBuilder b=new StringBuilder();
		b.append(lastName +",");
		if(firstName!=null)b.append(firstName +",");
		if(email!=null)b.append(email +",");
		if(orcid!=null)b.append(orcid+",");
		return b.substring(0, b.lastIndexOf(","));
	}
	
}
