package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataShort extends ArrayData {

	private static final long serialVersionUID = 6129653301969954455L;
	private short[] data;

	public ArrayDataShort() {
		super();
	}

	public ArrayDataShort(short[] data) {
		super();
		this.data = data;
	}

	public short[] getData() {
		return data;
	}

	public void setData(short[] data) {
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
