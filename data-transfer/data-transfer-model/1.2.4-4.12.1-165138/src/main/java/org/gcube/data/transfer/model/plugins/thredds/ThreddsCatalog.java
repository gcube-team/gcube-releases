package org.gcube.data.transfer.model.plugins.thredds;

import java.util.HashSet;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@NoArgsConstructor
@AllArgsConstructor
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ThreddsCatalog{
	private String ID;
	private String catalogFile;
	private String title;
	private String name;
	
	private DataSetRoot declaredDataSetRoot;
	private HashSet<DataSetScan> declaredDataSetScan;
	private CatalogCollection subCatalogs;

	
	public DataSet getDataSetFromLocation(String location) {
		for(DataSetScan scan : declaredDataSetScan) 
			if(scan!=null&&scan.getLocation()!=null&&ThreddsInfo.matchesPath(scan.getLocation(), location))
				return scan;
		if(declaredDataSetRoot!=null&&declaredDataSetRoot.getLocation()!=null&&
				ThreddsInfo.matchesPath(declaredDataSetRoot.getLocation(),location)) return declaredDataSetRoot;
		return null;
	}	
	
}