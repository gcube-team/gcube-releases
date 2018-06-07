package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataInt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayInt;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderInt extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderInt.class);

	public NetCDFValues apply(Variable variable) {

		int[] data = elaborate(variable, false, 0);
		ArrayDataInt arrayData = new ArrayDataInt(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		int[] data = elaborate(variable, true, limit);
		ArrayDataInt arrayData = new ArrayDataInt(data);
		return new NetCDFValues(arrayData);

	}

	private int[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayInt dataArray = (ArrayInt) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			int[] data = new int[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getInt(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
