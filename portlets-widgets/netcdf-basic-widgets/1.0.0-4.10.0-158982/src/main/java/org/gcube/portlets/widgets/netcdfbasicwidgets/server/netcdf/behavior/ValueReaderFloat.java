package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataFloat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayFloat;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderFloat extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderFloat.class);

	public NetCDFValues apply(Variable variable) {

		float[] data = elaborate(variable, false, 0);
		ArrayDataFloat arrayData = new ArrayDataFloat(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		float[] data = elaborate(variable, true, limit);
		ArrayDataFloat arrayData = new ArrayDataFloat(data);
		return new NetCDFValues(arrayData);

	}

	private float[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayFloat dataArray = (ArrayFloat) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			float[] data = new float[totalLen];

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
