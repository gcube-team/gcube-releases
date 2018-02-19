package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import java.io.IOException;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;
import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.arraydata.ArrayDataByte;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ucar.ma2.ArrayByte;
import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class ValueReaderByte extends ValueReader {

	private static Logger logger = LoggerFactory.getLogger(ValueReaderByte.class);

	public NetCDFValues apply(Variable variable) {

		byte[] data = elaborate(variable, false, 0);
		ArrayDataByte arrayData = new ArrayDataByte(data);
		return new NetCDFValues(arrayData);

	}

	public NetCDFValues sample(Variable variable,  int limit) {

		byte[] data = elaborate(variable, true, limit);
		ArrayDataByte arrayData = new ArrayDataByte(data);
		return new NetCDFValues(arrayData);

	}

	private  byte[] elaborate(Variable variable, boolean sample,  int limit) {
		try {
			ArrayByte dataArray = (ArrayByte) variable.read();

			int[] shape = dataArray.getShape();
			int totalLen = 1;

			for (int len : shape) {
				totalLen *= len;
			}

			if (sample) {
				totalLen = totalLen < limit ? totalLen : limit;
			}

			byte[] data = new byte[totalLen];

			for (int i = 0; i < totalLen; i++) {
				data[i] = dataArray.getByte(i);
			}

			return data;

		} catch (IOException e) {
			logger.error("Error reading data: " + e.getLocalizedMessage(), e);
			throw new RuntimeException("Error reading data: " + e.getLocalizedMessage(), e);
		}

	}

}
