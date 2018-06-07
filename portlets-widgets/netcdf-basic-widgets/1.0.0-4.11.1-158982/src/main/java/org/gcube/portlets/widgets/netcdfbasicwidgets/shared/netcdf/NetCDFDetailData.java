package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFDetailData implements Serializable {

	private static final long serialVersionUID = -4421993747362744743L;

	// private String info;
	// private String title;
	private String typeId;
	private String typeDescription;
	private String typeVersion;
	private ArrayList<AttributeData> globalAttributeDataList;

	public NetCDFDetailData() {
		super();
	}

	public NetCDFDetailData(String typeId, String typeDescription, String typeVersion,
			ArrayList<AttributeData> globalAttributeDataList) {
		super();
		this.typeId = typeId;
		this.typeDescription = typeDescription;
		this.typeVersion = typeVersion;
		this.globalAttributeDataList = globalAttributeDataList;
	}

	public String getTypeId() {
		return typeId;
	}

	public void setTypeId(String typeId) {
		this.typeId = typeId;
	}

	public String getTypeDescription() {
		return typeDescription;
	}

	public void setTypeDescription(String typeDescription) {
		this.typeDescription = typeDescription;
	}

	public String getTypeVersion() {
		return typeVersion;
	}

	public void setTypeVersion(String typeVersion) {
		this.typeVersion = typeVersion;
	}

	public ArrayList<AttributeData> getGlobalAttributeDataList() {
		return globalAttributeDataList;
	}

	public void setGlobalAttributeDataList(ArrayList<AttributeData> globalAttributeDataList) {
		this.globalAttributeDataList = globalAttributeDataList;
	}

	@Override
	public String toString() {
		return "NetCDFDetailData [typeId=" + typeId + ", typeDescription=" + typeDescription + ", typeVersion="
				+ typeVersion + ", globalAttributeDataList=" + globalAttributeDataList + "]";
	}

}
