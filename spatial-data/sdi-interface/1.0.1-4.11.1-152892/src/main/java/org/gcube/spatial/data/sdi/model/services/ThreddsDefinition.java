package org.gcube.spatial.data.sdi.model.services;

import java.util.ArrayList;
import java.util.List;

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
public class ThreddsDefinition extends ServiceDefinition {

	@Override
	public Type getType() {
		return Type.THREDDS;
	}
	
	@XmlElement
	private List<CatalogDefinition> catalogs;
	
	
	public void addCatalog(CatalogDefinition catalog) {
		if(catalogs==null) catalogs=new ArrayList<>();
		catalogs.add(catalog);
	}


	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		if(!(catalogs == null || catalogs.isEmpty()))
			for(CatalogDefinition catalog:catalogs) 
				result=prime*result+catalog.hashCode();
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
		ThreddsDefinition other = (ThreddsDefinition) obj;
		
		if (catalogs == null || catalogs.isEmpty()) {
			if (!(other.catalogs == null || other.catalogs.isEmpty()))
				return false;
		} else if (catalogs.size()!=other.catalogs.size())
			return false;
		else  
			if(!other.catalogs.containsAll(this.catalogs)) return false;
		
		return true;
	}
	
	
}
