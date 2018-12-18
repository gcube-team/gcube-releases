package org.gcube.dataanalysis.geo.connectors.netcdf;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;

public class NetCDF implements GISDataConnector {

	NetCDFDataExplorer netcdffile;

	public NetCDF(String layerURL, String layerName) {

		if (netcdffile == null)
			netcdffile = new NetCDFDataExplorer(layerURL, layerName);

	}

	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception {

		AnalysisLogger.getLogger().debug("Managing netCDF file");
		if (layerURL == null)
			return null;

		return netcdffile.retrieveDataFromNetCDF(layerURL, layerName, time, coordinates3d);

	}

	@Override
	public double getMinZ(String layerURL, String layerName) {
		return netcdffile.minZ;
	}

	@Override
	public double getMaxZ(String layerURL, String layerName) {

		return netcdffile.maxZ;
	}

}
