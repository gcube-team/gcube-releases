package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataInt extends ArrayData {

	private static final long serialVersionUID = 5381922244689509206L;
	private int[] data;

	public ArrayDataInt() {
		super();
	}

	public ArrayDataInt(int[] data) {
		super();
		this.data = data;
	}

	public int[] getData() {
		return data;
	}

	public void setData(int[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataInt [data=" + Arrays.toString(data) + "]";
	}

}
