package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayString;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderString extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderString.class);

	public NetCDFValues apply(Variable variable) {

		String[] data = elaborate(variable, false, 0);
		ArrayDataString arrayData = new ArrayDataString(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable, int limit) {

		String[] data = elaborate(variable, true, limit);
		ArrayDataString arrayData = new ArrayDataString(data);
		return new NetCDFValues(arrayData);

	}

	private String[] elaborate(Variable variable, boolean sample, int limit) {
		try {
			ArrayString dataArray = (ArrayString) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			String[] data = new String[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = (dataArray.getObject(i)).toString();

			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
