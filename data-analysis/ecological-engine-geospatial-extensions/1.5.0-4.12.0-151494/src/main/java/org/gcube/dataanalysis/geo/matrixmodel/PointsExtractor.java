package org.gcube.dataanalysis.geo.matrixmodel;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public class PointsExtractor extends MatrixExtractor{

		
	public PointsExtractor(AlgorithmConfiguration configuration) {
		super(configuration);
	}

		//XYZT Analysis
		public double extractXYZT(String layerTitle, double x, double y, double z, int timeIndex, double resolution) throws Exception {
			AnalysisLogger.getLogger().debug("Matrix Extractor-> Extracting Time Instant " + timeIndex);
			double[][] values = extractXYGridWithFixedTZ(layerTitle, timeIndex, x, x, y, y, z, resolution, resolution, true);
			int ver = values.length;
			int hor = values.length;
			//take central value
			double value=values[ver/2][hor/2];
			return value;
		}
		
}
