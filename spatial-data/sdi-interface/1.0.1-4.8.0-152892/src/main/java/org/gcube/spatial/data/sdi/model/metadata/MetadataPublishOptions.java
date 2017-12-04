package org.gcube.spatial.data.sdi.model.metadata;

import java.util.HashSet;
import java.util.Set;

import org.gcube.spatial.data.sdi.model.ServiceConstants;
import org.gcube.spatial.data.sdi.model.ServiceConstants.Metadata;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class MetadataPublishOptions {
	
	private boolean validate=ServiceConstants.Metadata.DEFAULT_VALIDATE;
	private boolean makePublic=ServiceConstants.Metadata.DEFAULT_PUBLIC;
	
	private String geonetworkCategory=ServiceConstants.Metadata.DEFAULT_CATEGORY;
	private String geonetworkStyleSheet=ServiceConstants.Metadata.DEFAULT_STYLESHEET;
	private Set<TemplateInvocation> templateInvocations=new HashSet<>(); 
	
	public MetadataPublishOptions(Set<TemplateInvocation> invocations){
		setTemplateInvocations(invocations);
		
		
	}
	
}
