package org.gcube.data.analysis.tabulardata.operation.view.maps;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name="PublishingParameters")
public class GeoPublishingParameters {

	private String workspace="aquamaps";
	private String datastore="aquamapsdb";
	private List<String> keywords=new ArrayList<>(Arrays.asList("TDM","Tabular Data Management"));
	private String crs="GEOGCS[\"WGS 84\", DATUM[\"World Geodetic System 1984\", SPHEROID[\"WGS 84\", 6378137.0, 298.257223563, AUTHORITY[\"EPSG\",\"7030\"]],"+ 
			"AUTHORITY[\"EPSG\",\"6326\"]], PRIMEM[\"Greenwich\", 0.0, AUTHORITY[\"EPSG\",\"8901\"]],  UNIT[\"degree\", 0.017453292519943295],"+ 
			"AXIS[\"Geodetic longitude\", EAST],  AXIS[\"Geodetic latitude\", NORTH],  AUTHORITY[\"EPSG\",\"4326\"]]";
	private String geometryFieldName="the_geom";
	private String gNCategory="datasets";
	private String gNStyleSheet="_none_";
	
	
	// IS Querying
	private String tdmDataStoreFlag="tdmDataStore";
	private String gisDBCategory="Database";
	private String gisDBPlatformName="postgis";
	private String accessPointName="jdbc";
	private String urlPrefix="jdbc:postgresql:";
	
	public GeoPublishingParameters() {
		// TODO Auto-generated constructor stub
	}
	
	

	public GeoPublishingParameters(String workspace, String datastore,
			List<String> keywords, String crs, String geometryFieldName,
			String gNCategory, String gNStyleSheet, String tdmDataStoreFlag,
			String gisDBCategory, String gisDBPlatformName,
			String accessPointName, String urlPrefix) {
		super();
		this.workspace = workspace;
		this.datastore = datastore;
		this.keywords = keywords;
		this.crs = crs;
		this.geometryFieldName = geometryFieldName;
		this.gNCategory = gNCategory;
		this.gNStyleSheet = gNStyleSheet;
		this.tdmDataStoreFlag = tdmDataStoreFlag;
		this.gisDBCategory = gisDBCategory;
		this.gisDBPlatformName = gisDBPlatformName;
		this.accessPointName = accessPointName;
		this.urlPrefix = urlPrefix;
	}



	/**
	 * @return the workspace
	 */
	public String getWorkspace() {
		return workspace;
	}

	/**
	 * @param workspace the workspace to set
	 */
	public void setWorkspace(String workspace) {
		this.workspace = workspace;
	}

	/**
	 * @return the datastore
	 */
	public String getDatastore() {
		return datastore;
	}

	/**
	 * @param datastore the datastore to set
	 */
	public void setDatastore(String datastore) {
		this.datastore = datastore;
	}

	/**
	 * @return the keywords
	 */
	public List<String> getKeywords() {
		return keywords;
	}

	/**
	 * @param keywords the keywords to set
	 */
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}

	/**
	 * @return the crs
	 */
	public String getCrs() {
		return crs;
	}

	/**
	 * @param crs the crs to set
	 */
	public void setCrs(String crs) {
		this.crs = crs;
	}

	/**
	 * @return the geometryFieldName
	 */
	public String getGeometryFieldName() {
		return geometryFieldName;
	}

	/**
	 * @param geometryFieldName the geometryFieldName to set
	 */
	public void setGeometryFieldName(String geometryFieldName) {
		this.geometryFieldName = geometryFieldName;
	}

	/**
	 * @return the gNCategory
	 */
	public String getgNCategory() {
		return gNCategory;
	}

	/**
	 * @param gNCategory the gNCategory to set
	 */
	public void setgNCategory(String gNCategory) {
		this.gNCategory = gNCategory;
	}

	/**
	 * @return the gNStyleSheet
	 */
	public String getgNStyleSheet() {
		return gNStyleSheet;
	}

	/**
	 * @param gNStyleSheet the gNStyleSheet to set
	 */
	public void setgNStyleSheet(String gNStyleSheet) {
		this.gNStyleSheet = gNStyleSheet;
	}

	/**
	 * @return the tdmDataStoreFlag
	 */
	public String getTdmDataStoreFlag() {
		return tdmDataStoreFlag;
	}

	/**
	 * @param tdmDataStoreFlag the tdmDataStoreFlag to set
	 */
	public void setTdmDataStoreFlag(String tdmDataStoreFlag) {
		this.tdmDataStoreFlag = tdmDataStoreFlag;
	}

	/**
	 * @return the gisDBCategory
	 */
	public String getGisDBCategory() {
		return gisDBCategory;
	}

	/**
	 * @param gisDBCategory the gisDBCategory to set
	 */
	public void setGisDBCategory(String gisDBCategory) {
		this.gisDBCategory = gisDBCategory;
	}

	/**
	 * @return the gisDBPlatformName
	 */
	public String getGisDBPlatformName() {
		return gisDBPlatformName;
	}

	/**
	 * @param gisDBPlatformName the gisDBPlatformName to set
	 */
	public void setGisDBPlatformName(String gisDBPlatformName) {
		this.gisDBPlatformName = gisDBPlatformName;
	}

	/**
	 * @return the accessPointName
	 */
	public String getAccessPointName() {
		return accessPointName;
	}

	/**
	 * @param accessPointName the accessPointName to set
	 */
	public void setAccessPointName(String accessPointName) {
		this.accessPointName = accessPointName;
	}

	/**
	 * @return the urlPrefix
	 */
	public String getUrlPrefix() {
		return urlPrefix;
	}

	/**
	 * @param urlPrefix the urlPrefix to set
	 */
	public void setUrlPrefix(String urlPrefix) {
		this.urlPrefix = urlPrefix;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((accessPointName == null) ? 0 : accessPointName.hashCode());
		result = prime * result + ((crs == null) ? 0 : crs.hashCode());
		result = prime * result
				+ ((datastore == null) ? 0 : datastore.hashCode());
		result = prime * result
				+ ((gNCategory == null) ? 0 : gNCategory.hashCode());
		result = prime * result
				+ ((gNStyleSheet == null) ? 0 : gNStyleSheet.hashCode());
		result = prime
				* result
				+ ((geometryFieldName == null) ? 0 : geometryFieldName
						.hashCode());
		result = prime * result
				+ ((gisDBCategory == null) ? 0 : gisDBCategory.hashCode());
		result = prime
				* result
				+ ((gisDBPlatformName == null) ? 0 : gisDBPlatformName
						.hashCode());
		result = prime * result
				+ ((keywords == null) ? 0 : keywords.hashCode());
		result = prime
				* result
				+ ((tdmDataStoreFlag == null) ? 0 : tdmDataStoreFlag.hashCode());
		result = prime * result
				+ ((urlPrefix == null) ? 0 : urlPrefix.hashCode());
		result = prime * result
				+ ((workspace == null) ? 0 : workspace.hashCode());
		return result;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		GeoPublishingParameters other = (GeoPublishingParameters) obj;
		if (accessPointName == null) {
			if (other.accessPointName != null)
				return false;
		} else if (!accessPointName.equals(other.accessPointName))
			return false;
		if (crs == null) {
			if (other.crs != null)
				return false;
		} else if (!crs.equals(other.crs))
			return false;
		if (datastore == null) {
			if (other.datastore != null)
				return false;
		} else if (!datastore.equals(other.datastore))
			return false;
		if (gNCategory == null) {
			if (other.gNCategory != null)
				return false;
		} else if (!gNCategory.equals(other.gNCategory))
			return false;
		if (gNStyleSheet == null) {
			if (other.gNStyleSheet != null)
				return false;
		} else if (!gNStyleSheet.equals(other.gNStyleSheet))
			return false;
		if (geometryFieldName == null) {
			if (other.geometryFieldName != null)
				return false;
		} else if (!geometryFieldName.equals(other.geometryFieldName))
			return false;
		if (gisDBCategory == null) {
			if (other.gisDBCategory != null)
				return false;
		} else if (!gisDBCategory.equals(other.gisDBCategory))
			return false;
		if (gisDBPlatformName == null) {
			if (other.gisDBPlatformName != null)
				return false;
		} else if (!gisDBPlatformName.equals(other.gisDBPlatformName))
			return false;
		if (keywords == null) {
			if (other.keywords != null)
				return false;
		} else if (!keywords.equals(other.keywords))
			return false;
		if (tdmDataStoreFlag == null) {
			if (other.tdmDataStoreFlag != null)
				return false;
		} else if (!tdmDataStoreFlag.equals(other.tdmDataStoreFlag))
			return false;
		if (urlPrefix == null) {
			if (other.urlPrefix != null)
				return false;
		} else if (!urlPrefix.equals(other.urlPrefix))
			return false;
		if (workspace == null) {
			if (other.workspace != null)
				return false;
		} else if (!workspace.equals(other.workspace))
			return false;
		return true;
	}



	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("GeoPublishingParameters [workspace=");
		builder.append(workspace);
		builder.append(", datastore=");
		builder.append(datastore);
		builder.append(", keywords=");
		builder.append(keywords);
		builder.append(", crs=");
		builder.append(crs);
		builder.append(", geometryFieldName=");
		builder.append(geometryFieldName);
		builder.append(", gNCategory=");
		builder.append(gNCategory);
		builder.append(", gNStyleSheet=");
		builder.append(gNStyleSheet);
		builder.append(", tdmDataStoreFlag=");
		builder.append(tdmDataStoreFlag);
		builder.append(", gisDBCategory=");
		builder.append(gisDBCategory);
		builder.append(", gisDBPlatformName=");
		builder.append(gisDBPlatformName);
		builder.append(", accessPointName=");
		builder.append(accessPointName);
		builder.append(", urlPrefix=");
		builder.append(urlPrefix);
		builder.append("]");
		return builder.toString();
	}

	

	
	
}

