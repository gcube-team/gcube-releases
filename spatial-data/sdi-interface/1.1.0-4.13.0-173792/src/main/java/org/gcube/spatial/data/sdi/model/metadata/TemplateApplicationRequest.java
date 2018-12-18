package org.gcube.spatial.data.sdi.model.metadata;

import java.util.HashSet;

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
public class TemplateApplicationRequest {

	@XmlElement(name="invocationSet")
	private HashSet<TemplateInvocation> invocationSet;
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		if(invocationSet!=null && !invocationSet.isEmpty())
			for(TemplateInvocation inv:invocationSet)
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
		TemplateApplicationRequest other = (TemplateApplicationRequest) obj;
		
		
		if (invocationSet == null ) {
			if (other.invocationSet != null)
				return false;
		} else if (invocationSet.size()!=other.invocationSet.size())
			return false;
		else for(TemplateInvocation inv:invocationSet) {
				boolean ok=false;
				for(TemplateInvocation inv2:other.invocationSet)
					if(inv.equals(inv2)) {
						ok=true;
						break;
					}
				if(!ok)return false;
		}
		
	
		return true;
	}
	
	
	
	
}
