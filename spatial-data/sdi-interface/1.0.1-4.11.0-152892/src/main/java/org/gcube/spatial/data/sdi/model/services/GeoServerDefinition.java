package org.gcube.spatial.data.sdi.model.services;

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

@XmlRootElement
@XmlAccessorType(XmlAccessType.NONE)
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class GeoServerDefinition extends ServiceDefinition {

	@Override
	public Type getType() {
		return Type.GEOSERVER;
	}
	
	@XmlElement
	private ArrayList<WorkspaceDefinition> workspaces;

	
	public void addWorkspace(WorkspaceDefinition def) {
		if(workspaces==null) workspaces=new ArrayList<>();
		workspaces.add(def);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		
		if(!(workspaces == null || workspaces.isEmpty()))
			for(WorkspaceDefinition workspace:workspaces) 
				result=prime*result+workspace.hashCode();
		
		
		return result;
	}


	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoServerDefinition other = (GeoServerDefinition) obj;
		
		if (workspaces == null || workspaces.isEmpty()) {
			if (!(other.workspaces == null || other.workspaces.isEmpty()))
				return false;
		} else if (workspaces.size()!=other.workspaces.size())
			return false;
		else  
			if(!other.workspaces.containsAll(this.workspaces)) return false;
		
		
		return true;
	}
	
	
	
	
}
