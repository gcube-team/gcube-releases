package org.gcube.application.aquamaps.aquamapsservice.impl.publishing.gis;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.gis.LayerType;





public class LayerGenerationRequest {


	//************ Layer Generation details
	private String csvFile;
	private String featureLabel;
	private String FeatureDefinition;
	private String mapName;
	private LayerType mapType;
	
	private List<StyleGenerationRequest> toGenerateStyles=new ArrayList<StyleGenerationRequest>();
	private List<String> toAssociateStyles=new ArrayList<String>();
	private int defaultStyle=0;
	private Map<String,Object> meta;	
	//************ Generated Layer references
	

	
	


	public String getCsvFile() {
		return csvFile;
	}




	public LayerGenerationRequest(String csvFile, String featureLabel,
			String featureDefinition, String mapName, LayerType mapType,
			List<StyleGenerationRequest> toGenerateStyles,
			List<String> toAssociateStyles, int defaultStyle,Map<String,Object> meta) {
		super();
		this.csvFile = csvFile;
		this.featureLabel = featureLabel;
		FeatureDefinition = featureDefinition;
		this.mapName = mapName;
		this.mapType = mapType;
		this.toGenerateStyles.addAll(toGenerateStyles);
		this.toAssociateStyles.addAll(toAssociateStyles);
		this.defaultStyle = defaultStyle;
		this.meta=meta;
	}




	public void setCsvFile(String csvFile) {
		this.csvFile = csvFile;
	}




	public String getFeatureLabel() {
		return featureLabel;
	}




	public void setFeatureLabel(String featureLabel) {
		this.featureLabel = featureLabel;
	}




	public String getFeatureDefinition() {
		return FeatureDefinition;
	}




	public void setFeatureDefinition(String featureDefinition) {
		FeatureDefinition = featureDefinition;
	}




	public String getMapName() {
		return mapName;
	}




	public void setMapName(String mapName) {
		this.mapName = mapName;
	}




	public LayerType getMapType() {
		return mapType;
	}

	public void setMapType(LayerType mapType) {
		this.mapType = mapType;
	}



	public List<StyleGenerationRequest> getToGenerateStyles() {
		return toGenerateStyles;
	}




	public void setToGenerateStyles(List<StyleGenerationRequest> toGenerateStyles) {
		this.toGenerateStyles = toGenerateStyles;
	}




	public List<String> getToAssociateStyles() {
		return toAssociateStyles;
	}




	public void setToAssociateStyles(List<String> toAssociateStyles) {
		this.toAssociateStyles = toAssociateStyles;
	}




	




	

	public void setDefaultStyle(int defaultStyle) {
		this.defaultStyle = defaultStyle;
	}



	public int getDefaultStyle() {
		return defaultStyle;
	}





	public Map<String, Object> getMeta() {
		return meta;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime
				* result
				+ ((FeatureDefinition == null) ? 0 : FeatureDefinition
						.hashCode());
		result = prime * result + ((csvFile == null) ? 0 : csvFile.hashCode());
		result = prime * result + defaultStyle;
		result = prime * result
				+ ((featureLabel == null) ? 0 : featureLabel.hashCode());
		result = prime * result + ((mapName == null) ? 0 : mapName.hashCode());
		result = prime * result + ((mapType == null) ? 0 : mapType.hashCode());
		result = prime * result + ((meta == null) ? 0 : meta.hashCode());
		result = prime
				* result
				+ ((toAssociateStyles == null) ? 0 : toAssociateStyles
						.hashCode());
		result = prime
				* result
				+ ((toGenerateStyles == null) ? 0 : toGenerateStyles.hashCode());
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
		LayerGenerationRequest other = (LayerGenerationRequest) obj;
		if (FeatureDefinition == null) {
			if (other.FeatureDefinition != null)
				return false;
		} else if (!FeatureDefinition.equals(other.FeatureDefinition))
			return false;
		if (csvFile == null) {
			if (other.csvFile != null)
				return false;
		} else if (!csvFile.equals(other.csvFile))
			return false;
		if (defaultStyle != other.defaultStyle)
			return false;
		if (featureLabel == null) {
			if (other.featureLabel != null)
				return false;
		} else if (!featureLabel.equals(other.featureLabel))
			return false;
		if (mapName == null) {
			if (other.mapName != null)
				return false;
		} else if (!mapName.equals(other.mapName))
			return false;
		if (mapType != other.mapType)
			return false;
		if (meta == null) {
			if (other.meta != null)
				return false;
		} else if (!meta.equals(other.meta))
			return false;
		if (toAssociateStyles == null) {
			if (other.toAssociateStyles != null)
				return false;
		} else if (!toAssociateStyles.equals(other.toAssociateStyles))
			return false;
		if (toGenerateStyles == null) {
			if (other.toGenerateStyles != null)
				return false;
		} else if (!toGenerateStyles.equals(other.toGenerateStyles))
			return false;
		return true;
	}




	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LayerGenerationRequest [csvFile=");
		builder.append(csvFile);
		builder.append(", featureLabel=");
		builder.append(featureLabel);
		builder.append(", FeatureDefinition=");
		builder.append(FeatureDefinition);
		builder.append(", mapName=");
		builder.append(mapName);
		builder.append(", mapType=");
		builder.append(mapType);
		builder.append(", toGenerateStyles=");
		builder.append(toGenerateStyles);
		builder.append(", toAssociateStyles=");
		builder.append(toAssociateStyles);
		builder.append(", defaultStyle=");
		builder.append(defaultStyle);
		builder.append(", meta=");
		builder.append(meta);
		builder.append("]");
		return builder.toString();
	}


	
	
}
