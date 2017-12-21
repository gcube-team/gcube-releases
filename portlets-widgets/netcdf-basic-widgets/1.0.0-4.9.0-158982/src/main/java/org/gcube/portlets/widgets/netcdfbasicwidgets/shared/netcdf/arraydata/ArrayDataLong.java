package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ArrayDataLong extends ArrayData {

	private static final long serialVersionUID = 5381922244689509206L;
	private long[] data;

	public ArrayDataLong() {
		super();
	}

	public ArrayDataLong(long[] data) {
		super();
		this.data = data;
	}

	public long[] getData() {
		return data;
	}

	public void setData(long[] data) {
		this.data = data;
	}

	public String asString() {
		return Arrays.toString(data);
	}

	@Override
	public String toString() {
		return "ArrayDataLong [data=" + Arrays.toString(data) + "]";
	}

}
