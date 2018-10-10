package org.gcube.spatial.data.sdi.model.metadata;

import java.util.ArrayList;
import java.util.Map.Entry;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.gcube.spatial.data.sdi.model.ParameterType;

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
public class TemplateInvocation {
	
	@XmlElement(name="templateID")
	private String toInvokeTemplateID;
	
//	@XmlAnyElement
//	@XmlJavaTypeAdapter(MapAdapter.class)
	@XmlElement(name="parameters")
	private ArrayList<ParameterType> templateParameters;

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(!(templateParameters == null || templateParameters.isEmpty()))
			for(ParameterType param:templateParameters) 
				result=prime*result+param.hashCode();

		
		result = prime * result + ((toInvokeTemplateID == null) ? 0 : toInvokeTemplateID.hashCode());
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
		TemplateInvocation other = (TemplateInvocation) obj;
		
		if (templateParameters == null || templateParameters.isEmpty()) {
			if (!(other.templateParameters == null || other.templateParameters.isEmpty()))
				return false;
		} else if (templateParameters.size()!=other.templateParameters.size())
			return false;
		else  
			if(!other.templateParameters.containsAll(this.templateParameters)) return false;
		
		
		
		if (toInvokeTemplateID == null) {
			if (other.toInvokeTemplateID != null)
				return false;
		} else if (!toInvokeTemplateID.equals(other.toInvokeTemplateID))
			return false;
		return true;
	}
	
	public void addParameter(String name,String value) {
		if(templateParameters==null) templateParameters=new ArrayList<>();
		templateParameters.add(new ParameterType(name,value));
	}
}
