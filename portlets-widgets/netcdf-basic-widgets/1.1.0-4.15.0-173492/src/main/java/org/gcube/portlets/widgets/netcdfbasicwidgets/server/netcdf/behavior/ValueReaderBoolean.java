package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataBoolean;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayBoolean;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderBoolean extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderBoolean.class);

	public NetCDFValues apply(Variable variable) {

		boolean[] data = elaborate(variable, false, 0);
		ArrayDataBoolean arrayData = new ArrayDataBoolean(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		boolean[] data = elaborate(variable, true, limit);
		ArrayDataBoolean arrayData = new ArrayDataBoolean(data);
		return new NetCDFValues(arrayData);

	}

	private boolean[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayBoolean dataArray = (ArrayBoolean) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			boolean[] data = new boolean[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getBoolean(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
