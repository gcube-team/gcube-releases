package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataShort;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayShort;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderShort extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderShort.class);

	public NetCDFValues apply(Variable variable) {

		short[] data = elaborate(variable, false, 0);
		ArrayDataShort arrayData = new ArrayDataShort(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		short[] data = elaborate(variable, true, limit);
		ArrayDataShort arrayData = new ArrayDataShort(data);
		return new NetCDFValues(arrayData);

	}

	private short[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayShort dataArray = (ArrayShort) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			short[] data = new short[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getShort(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
