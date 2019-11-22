package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataDouble;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayDouble;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi 
 *
 *
 */
public class ValueReaderDouble extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderDouble.class);

	
	public NetCDFValues apply(Variable variable) {

		double[] data = elaborate(variable, false, 0);
		ArrayDataDouble arrayData = new ArrayDataDouble(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable, int limit) {

		double[] data = elaborate(variable, true, limit);
		ArrayDataDouble arrayData = new ArrayDataDouble(data);
		return new NetCDFValues(arrayData);
	
	}


	private double[] elaborate(Variable variable,boolean sample, int limit) {
		try {
			ArrayDouble dataArray = (ArrayDouble) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if(sample){
				totalLen = totalLen < limit?totalLen:limit;
			}
			
			double[] data = new double[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getFloat(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
