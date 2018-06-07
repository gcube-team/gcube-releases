package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataFloat extends ArrayData {

	private static final long serialVersionUID = -5059700701483524112L;
	private float[] data;

	public ArrayDataFloat() {
		super();
	}

	public ArrayDataFloat(float[] data) {
		super();
		this.data = data;
	}

	public float[] getData() {
		return data;
	}

	public void setData(float[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataFloat [data=" + Arrays.toString(data) + "]";
	}

}
