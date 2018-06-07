package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf;

import java.io.Serializable;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayData;


/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFValues implements Serializable {

	private static final long serialVersionUID = -60684556484226449L;
	private ArrayData arrayData;

	public NetCDFValues() {
		super();
	}

	public NetCDFValues(ArrayData arrayData) {
		super();
		this.arrayData = arrayData;
	}

	public ArrayData getArrayData() {
		return arrayData;
	}

	public void setArrayData(ArrayData arrayData) {
		this.arrayData = arrayData;
	}

	@Override
	public String toString() {
		return "NetCDFValues [arrayData=" + arrayData + "]";
	}

}
