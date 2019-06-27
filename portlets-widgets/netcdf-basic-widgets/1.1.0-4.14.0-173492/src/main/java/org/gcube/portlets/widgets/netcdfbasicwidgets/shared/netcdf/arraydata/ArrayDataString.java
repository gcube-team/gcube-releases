package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataString extends ArrayData {

	private static final long serialVersionUID = -800750497029042816L;

	private String[] data;

	public ArrayDataString() {
		super();
	}

	public ArrayDataString(String[] data) {
		super();
		this.data = data;
	}

	public String[] getData() {
		return data;
	}

	public void setData(String[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataShort [data=" + Arrays.toString(data) + "]";
	}

}
