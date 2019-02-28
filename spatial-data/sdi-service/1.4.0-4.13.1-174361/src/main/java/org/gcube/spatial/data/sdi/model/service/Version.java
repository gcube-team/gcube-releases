package org.gcube.spatial.data.sdi.model.service;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
@NoArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class Version {
	
	@NonNull
	private Short major;
	@NonNull
	private Short minor;
	@NonNull
	private Short build;
	
	public Version(Integer maj,Integer min, Integer build){
		this(maj.shortValue(),min.shortValue(),build.shortValue());
	}
}
