package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataChar extends ArrayData {

	private static final long serialVersionUID = 2958263201966665385L;
	private char[] data;

	public ArrayDataChar() {
		super();
	}

	public ArrayDataChar(char[] data) {
		super();
		this.data = data;
	}

	public char[] getData() {
		return data;
	}

	public void setData(char[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataChar [data=" + Arrays.toString(data) + "]";
	}

}
