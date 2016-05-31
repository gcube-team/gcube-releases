package org.gcube.dataanalysis.geo.matrixmodel;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public class XYExtractor extends MatrixExtractor{

	public XYExtractor(AlgorithmConfiguration configuration) {
		super(configuration);
	}
	
	public double[][] extractXYGrid(String layerTitle, int timeInstant, double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution) throws Exception {
		return extractXYGridWithFixedTZ(layerTitle, timeInstant, x1, x2, y1, y2, z, xResolution, yResolution, false);
	}

}
