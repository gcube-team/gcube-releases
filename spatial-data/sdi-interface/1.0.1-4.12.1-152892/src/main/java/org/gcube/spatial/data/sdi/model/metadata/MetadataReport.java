package org.gcube.spatial.data.sdi.model.metadata;

import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@EqualsAndHashCode
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class MetadataReport {

	@XmlElement(name="publishedUUID")
	private String publishedUUID;
	
	@XmlElement(name="publishedID")
	private Long publishedID;
	
	@XmlElement(name="appliedTemplates")
	private Set<String> appliedTemplates;

}
