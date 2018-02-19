package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataBoolean extends ArrayData {
	
	private static final long serialVersionUID = 376775487416054751L;
	private boolean[] data;

	public ArrayDataBoolean() {
		super();
	}

	public ArrayDataBoolean(boolean[] data) {
		super();
		this.data = data;
	}

	public boolean[] getData() {
		return data;
	}

	public void setData(boolean[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataBoolean [data=" + Arrays.toString(data) + "]";
	}

}
