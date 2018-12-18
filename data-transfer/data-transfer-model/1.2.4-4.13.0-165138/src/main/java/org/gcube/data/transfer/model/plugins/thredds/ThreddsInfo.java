package org.gcube.data.transfer.model.plugins.thredds;

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
public class ThreddsInfo {

	private String hostname;	
	private String localBasePath;
	private String instanceBaseUrl;
	private ThreddsCatalog catalog;


	private String adminUser;
	private String adminPassword;

	private int version;
	private int minor;
	private int build;
	private int revision;
	private String ghnId;


	public ThreddsCatalog getById(String id) {
		return findById(catalog,id); 
	}


	public ThreddsCatalog getCatalogByFittingLocation(String toMatchLocation) {
		return getByFittingLocation(catalog, toMatchLocation);
	}
	
	public DataSet getDataSetFromLocation(String location) {
		try{
			return getCatalogByFittingLocation(location).getDataSetFromLocation(location);
		}catch(NullPointerException e) {return null;}
	}
	
	public static ThreddsCatalog findById(ThreddsCatalog catalog,String id) {
		if(catalog!=null) {
			if(catalog.getID()!=null&&catalog.getID().equals(id)) return catalog;		
			if(catalog.getSubCatalogs()!=null&&catalog.getSubCatalogs().getLinkedCatalogs()!=null)
				for(ThreddsCatalog cat : catalog.getSubCatalogs().getLinkedCatalogs()) {
					ThreddsCatalog found=findById(cat,id);
					if(found!=null) return found;
				}
		}
		return null;
	}

	public static ThreddsCatalog getByFittingLocation(ThreddsCatalog catalog,String toMatchPath) {
		if(catalog!=null) {
//			if(catalog.getDeclaredDataSetRoot()!=null&&catalog.getDeclaredDataSetRoot().getLocation()!=null&&
//					matchesPath(catalog.getDeclaredDataSetRoot().getLocation(),toMatchPath)) return catalog;
			for(DataSetScan scan : catalog.getDeclaredDataSetScan()) 
				if(scan!=null&&scan.getLocation()!=null&&matchesPath(scan.getLocation(), toMatchPath))
					return catalog;
			if(catalog.getSubCatalogs()!=null&&catalog.getSubCatalogs().getLinkedCatalogs()!=null)
				for(ThreddsCatalog cat:catalog.getSubCatalogs().getLinkedCatalogs()) {	
					ThreddsCatalog found=getByFittingLocation(cat, toMatchPath);
					if(found!=null) return found;
				}
		}
		return null;
	}

	public static boolean matchesPath(String catalogPath,String toMatchPath) {
		if(toMatchPath.endsWith("/"))
		return toMatchPath.startsWith(catalogPath);
		else return (toMatchPath+"/").startsWith(catalogPath);
	}
}
