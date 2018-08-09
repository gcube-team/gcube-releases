package org.gcube.portlets.widgets.netcdfbasicwidgets.server.netcdf.behavior;

import org.gcube.portlets.widgets.netcdfbasicwidgets.shared.netcdf.NetCDFValues;

import ucar.nc2.Variable;

/**
 * 
 * @author Giancarlo Panichi
 *
 *
 */
public class NetCDFValueReader {

	public NetCDFValues apply(Variable variable, ValueReader valueReader) {
		return valueReader.apply(variable);

	}

	public NetCDFValues sample(Variable variable,int limit, ValueReader valueReader) {
		return valueReader.sample(variable, limit);

	}

}
