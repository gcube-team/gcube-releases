package org.gcube.accounting.accounting.summary.access.model;

import java.io.Serializable;
import java.util.LinkedList;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@RequiredArgsConstructor
@NoArgsConstructor
public class ScopeDescriptor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 8470652008117771209L;
	
	@NonNull
	private String name;
	
	@NonNull
	private String id;
	
	private LinkedList<ScopeDescriptor> children=new LinkedList<>();

	
	public boolean hasChildren() {
		return children!=null&&!children.isEmpty();
	}
	
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
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
		ScopeDescriptor other = (ScopeDescriptor) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
	
	
	
}
