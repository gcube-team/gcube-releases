package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFData implements Serializable {

	private static final long serialVersionUID = -5329486981700757821L;

	private NetCDFId netCDFId;
	private NetCDFDetailData detail;
	private ArrayList<DimensionData> dimensions;
	private ArrayList<VariableData> variables;

	public NetCDFData() {
		super();
	}

	public NetCDFData(NetCDFId netCDFId, NetCDFDetailData detail, ArrayList<DimensionData> dimensions,
			ArrayList<VariableData> variables) {
		super();
		this.netCDFId = netCDFId;
		this.detail = detail;
		this.dimensions = dimensions;
		this.variables = variables;
	}

	public NetCDFId getNetCDFId() {
		return netCDFId;
	}

	public void setNetCDFId(NetCDFId netCDFId) {
		this.netCDFId = netCDFId;
	}

	public NetCDFDetailData getDetail() {
		return detail;
	}

	public void setDetail(NetCDFDetailData detail) {
		this.detail = detail;
	}

	public ArrayList<VariableData> getVariables() {
		return variables;
	}

	public void setVariables(ArrayList<VariableData> variables) {
		this.variables = variables;
	}

	public ArrayList<DimensionData> getDimensions() {
		return dimensions;
	}

	public void setDimensions(ArrayList<DimensionData> dimensions) {
		this.dimensions = dimensions;
	}

	@Override
	public String toString() {
		return "NetCDFData [netCDFId=" + netCDFId + ", detail=" + detail + ", dimensions=" + dimensions + ", variables="
				+ variables + "]";
	}

}
