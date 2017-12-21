package org.gcube.dataanalysis.geo.connectors.table;

import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.utils.VectorOperations;

public class Table implements GISDataConnector {

	AlgorithmConfiguration config;
	TableMatrixRepresentation tmr;
	double defaultresolution;
	
	public Table(AlgorithmConfiguration config,double resolution) throws Exception {
		this.config = config;
		tmr = new TableMatrixRepresentation();
		tmr.build5DTuples(config, true);
		this.defaultresolution=resolution;
	}

	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception {

		List<Double> values = new ArrayList<Double>();
		values.add(Math.random());

		List<Tuple<Double>> tuples = tmr.currentcoordinates5d.get((double) time);
		// AnalysisLogger.getLogger().debug("TUPLES "+tuples);
		double resolution = defaultresolution;
		if (coordinates3d.size() > 1)
			resolution = Math.abs(coordinates3d.get(0).getElements().get(0) - coordinates3d.get(1).getElements().get(0));

		double tolerance = Math.sqrt(2d) * resolution / 2d;

		if (tuples.size() == 0) {
			AnalysisLogger.getLogger().debug("Error in getting elements for time " + time);
			throw new Exception("Error in getting elements for time " + time);
		}

		AnalysisLogger.getLogger().debug("Getting elements for time " + time);
		// check z: if there is at least one point inside the z boundary then it is ok
		boolean outsideZ = true;

		for (Tuple<Double> coordinates : coordinates3d) {
			double Zcoord = 0;
			if (coordinates.getElements().size() > 2)
				Zcoord = coordinates.getElements().get(2);

			if ((Zcoord <= tmr.maxZ) && (Zcoord >= tmr.minZ)) {
				outsideZ = false;
				break;
			}
		}

		if (outsideZ) {
			AnalysisLogger.getLogger().debug("Error in getting elements for Z ");
			throw new Exception("Outside the z boundaries [" + tmr.minZ + ";" + tmr.maxZ + "]");
		}
		
		AnalysisLogger.getLogger().debug("Assigning points to grid ");
		List<Double> v = VectorOperations.assignPointsValuesToGrid(coordinates3d, time, tuples, tolerance);

		// AnalysisLogger.getLogger().debug("VALUES "+v);

		return v;

	}

	@Override
	public double getMinZ(String layerURL, String layerName) {
		return tmr.minZ;
	}

	@Override
	public double getMaxZ(String layerURL, String layerName) {
		return tmr.maxZ;
	}

}
