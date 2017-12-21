package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataChar;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayChar;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderChar extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderChar.class);

	public NetCDFValues apply(Variable variable) {

		char[] data = elaborate(variable, false, 0);
		ArrayDataChar arrayData = new ArrayDataChar(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		char[] data = elaborate(variable, true, limit);
		ArrayDataChar arrayData = new ArrayDataChar(data);
		return new NetCDFValues(arrayData);

	}

	private  char[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayChar dataArray = (ArrayChar) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			char[] data = new char[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getChar(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
