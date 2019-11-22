package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataLong;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayLong;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderLong extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderLong.class);

	public NetCDFValues apply(Variable variable) {

		long[] data = elaborate(variable, false, 0);
		ArrayDataLong arrayData = new ArrayDataLong(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		long[] data = elaborate(variable, true, limit);
		ArrayDataLong arrayData = new ArrayDataLong(data);
		return new NetCDFValues(arrayData);

	}

	private long[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayLong dataArray = (ArrayLong) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			long[] data = new long[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getLong(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
