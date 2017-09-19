package org.gcube.dataanalysis.geo.matrixmodel;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;

public class ZExtractor extends MatrixExtractor{

	public ZExtractor(AlgorithmConfiguration configuration) {
		super(configuration);
	}
	
	public double zmin;
	public double zmax;
	
	public double[] extractZ(String layerTitle, double x, double y, int timeIndex, double resolution) throws Exception {
		
		double[] signal = new double[maxzLength];
		if (layerTitle==null)
			layerTitle="";
		
		GISDataConnector connector = getConnector(layerTitle,resolution);
		
		zmin = connector.getMinZ(layerURL, layerName);
		zmax = connector.getMaxZ(layerURL, layerName);
		
		AnalysisLogger.getLogger().debug("ZExtractor: minimum Z "+zmin+" maximum Z:"+zmax+" step: "+resolution);
		
		int zcounter=0;
		
		if (resolution==0)
			resolution=1;
		
		for (double z=zmin;z<=zmax;z=z+resolution){
			try {
				if (z%100==0)
					AnalysisLogger.getLogger().debug("Matrix Extractor-> Extracting Z value " + z);
				
				double[][] values = extractXYGridWithFixedTZ(layerTitle, timeIndex, x, x, y, y, z, resolution, resolution, true);
				signal[zcounter]=values[0][0];
				zcounter++;
			} catch (Exception e) {
				AnalysisLogger.getLogger().debug("Matrix Extractor-> No More Time Intervals!");
				break;
			}
		}

		AnalysisLogger.getLogger().debug("Matrix Extractor-> Signal Length:"+zcounter);
		double[] dsignal = new double[zcounter];
		for (int i=0;i<zcounter;i++){
			dsignal[i] = signal[i];
			i++;
		}
		
		return dsignal;
		
	}

}
