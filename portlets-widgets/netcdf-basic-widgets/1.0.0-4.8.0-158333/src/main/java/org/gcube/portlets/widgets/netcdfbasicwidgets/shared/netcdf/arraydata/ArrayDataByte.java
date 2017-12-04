package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataByte extends ArrayData {

	private static final long serialVersionUID = -6974556500776290487L;
	private byte[] data;

	public ArrayDataByte() {
		super();
	}

	public ArrayDataByte(byte[] data) {
		super();
		this.data = data;
	}

	public byte[] getData() {
		return data;
	}

	public void setData(byte[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataByte [data=" + Arrays.toString(data) + "]";
	}

}
