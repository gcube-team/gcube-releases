package org.gcube.dataanalysis.geo.matrixmodel;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;

public class TimeSeriesExtractor extends MatrixExtractor{

	public TimeSeriesExtractor(AlgorithmConfiguration configuration) {
		super(configuration);
	}
	
	//Time Analysis
		/**
		 * Extracts an observations signal for a certain point in space 
		 */
		
		public double[] extractT(String layerTitle, double x, double y, double z, double resolution) throws Exception {
			double[] signal = new double[maxSignalLength];
			int t = 0;
			log=false;
			while (true) {
				try {
					if (t%100==0)
						AnalysisLogger.getLogger().debug("Matrix Extractor-> Extracting Time Instant " + t);
					double[][] values = extractXYGridWithFixedTZ(layerTitle, t, x, x, y, y, z, resolution, resolution, true);
					
					signal[t]=values[0][0];
					
					t++;
					if (t==maxSignalLength)
						break;
				} catch (Exception e) {
					AnalysisLogger.getLogger().debug("Matrix Extractor-> No More Time Intervals! "+e.getMessage());
					break;
				}
			}

			AnalysisLogger.getLogger().debug("Matrix Extractor-> Signal Length:"+t);
			double[] dsignal = new double[t];
			for (int i=0;i<t;i++){
				dsignal[i] = signal[i];
				i++;
			}
			log = true;
			return dsignal;
		}

		/**
		 * Extract observations in time with 0 resolution
		 * @param layerTitle
		 * @return
		 * @throws Exception
		 */
		public double[] extractT(String layerTitle) throws Exception {
			return extractT(layerTitle,0,0,0,0);
		}
		
		
		//END Time Analysis
		

}
