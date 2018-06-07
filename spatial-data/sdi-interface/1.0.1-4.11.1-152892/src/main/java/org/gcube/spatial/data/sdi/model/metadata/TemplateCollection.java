package org.gcube.spatial.data.sdi.model.metadata;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

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
@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
public class TemplateCollection {

	@XmlElement(name="availableTemplates")
	private Set<TemplateDescriptor> availableTemplates=new HashSet<TemplateDescriptor>();

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(availableTemplates!=null && !availableTemplates.isEmpty())
			for(TemplateDescriptor inv:availableTemplates)
				result=prime*result+inv.hashCode();
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TemplateCollection other = (TemplateCollection) obj;
		
		
		if (availableTemplates == null ) {
			if (other.availableTemplates != null)
				return false;
		} else if (availableTemplates.size()!=other.availableTemplates.size())
			return false;
		else for(TemplateDescriptor inv:availableTemplates)
			if(!other.availableTemplates.contains(inv))return false;
		
		
		
		return true;
	}
	
	
	
}
