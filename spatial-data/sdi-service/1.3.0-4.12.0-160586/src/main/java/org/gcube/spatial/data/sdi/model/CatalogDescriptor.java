package org.gcube.spatial.data.sdi.model;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@RequiredArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class CatalogDescriptor {

	@NonNull
	private String catalogURL;
	
	
}
