package org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata;

import java.util.Arrays;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class ArrayDataDouble extends ArrayData {

	private static final long serialVersionUID = 8662144557829864644L;
	private double[] data;
	
	public ArrayDataDouble(){
		super();
	}
	
	public ArrayDataDouble(double[] data) {
		super();
		this.data = data;
	}


	public double[] getData() {
		return data;
	}


	public void setData(double[] data) {
		this.data = data;
	}

    public String asString(){
    	return Arrays.toString(data);
    }

	@Override
	public String toString() {
		return "ArrayDataDouble [data=" + Arrays.toString(data) + "]";
	}
	
}
