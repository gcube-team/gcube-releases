package org.gcube.spatial.data.sdi.model.metadata;

import java.util.ArrayList;

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
public class TemplateDescriptor {

	@XmlElement(name="templateID")
	private String id;	
	@XmlElement(name="name")
	private String name;
	@XmlElement(name="description")
	private String description;
	@XmlElement(name="sourceURL")
	private String sourceURL;

//	@XmlAnyElement
//	@XmlJavaTypeAdapter(MapAdapter.class)
	
	@XmlElement(name="parameters")
	private ArrayList<ParameterType> expectedParameters;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		
		if(!(expectedParameters == null || expectedParameters.isEmpty()))
			for(ParameterType param:expectedParameters) 
				result=prime*result+param.hashCode();
				
			
		
		
		result = prime * result + ((description == null) ? 0 : description.hashCode());
		
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + ((sourceURL == null) ? 0 : sourceURL.hashCode());
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
		TemplateDescriptor other = (TemplateDescriptor) obj;
		if (description == null) {
			if (other.description != null)
				return false;
		} else if (!description.equals(other.description))
			return false;
		
		
		
		if (expectedParameters == null || expectedParameters.isEmpty()) {
			if (!(other.expectedParameters == null || other.expectedParameters.isEmpty()))
				return false;
		} else if (expectedParameters.size()!=other.expectedParameters.size())
			return false;
		else  
			if(!other.expectedParameters.containsAll(this.expectedParameters)) return false;
		
			
		
		
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		if (name == null) {
			if (other.name != null)
				return false;
		} else if (!name.equals(other.name))
			return false;
		if (sourceURL == null) {
			if (other.sourceURL != null)
				return false;
		} else if (!sourceURL.equals(other.sourceURL))
			return false;
		return true;
	}
	
	public void addParameter(String name,String value) {
		if(expectedParameters==null) expectedParameters=new ArrayList<>();
		expectedParameters.add(new ParameterType(name,value));
	}
	
}
