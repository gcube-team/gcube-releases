package org.gcube.dataanalysis.geo.matrixmodel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.AscDataExplorer;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;

public class ASCConverter {

	XYExtractor extractor;
	AlgorithmConfiguration config;
	public ASCConverter(AlgorithmConfiguration config) {
		extractor = new XYExtractor(config);
		this.config=config;
	}

	public String convertToASC(String layerTitle, String layerName, int timeInstant, double z, double xResolution, double yResolution) throws Exception 
		{
			return convertToASC(layerTitle, layerName, timeInstant, -180, 180, -90, 90, z, xResolution, yResolution);
		}
	
			
	public String convertToASC(String outFilePath, double[][] values, double x1, double y1, double xResolution, double yResolution ) throws Exception {
		try {
			
			AscRaster raster = null;
			if (xResolution == yResolution)
				raster = new AscRaster(values, xResolution, -1, -1, x1, y1);
			else
				raster = new AscRaster(values, -1, xResolution, yResolution, x1,y1);
			
			
			String outputFile =new File(outFilePath).getAbsolutePath();
			AscRasterWriter writer = new AscRasterWriter();
			writer.writeRasterInvertYAxis(outputFile, raster);
		
			return outputFile;
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("Error in converting to ASC : " + e.getLocalizedMessage());
			AnalysisLogger.getLogger().debug(e);
			throw e;
		}
	}
	
	
	public String convertToASC(String layerTitle, String outFilePath, int timeInstant, double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution) throws Exception {
			
			double[][] values = extractor.extractXYGrid(layerTitle, timeInstant, x1, x2, y1, y2, z, xResolution, yResolution);
			return convertToASC(outFilePath, values,  x1, y1, xResolution,yResolution);
	}
	
	public static void main(String[] args) throws Exception{
		
		AlgorithmConfiguration config = new AlgorithmConfiguration();
		
		config.setAgent("XYEXTRACTOR");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");
		
		config.setGcubeScope("/gcube/devsec/devVRE");
		
		String layername = "dfd1bad2-ab00-42ac-8bb2-46a17162f509";
		float z = 0;
		int time = 0;
		float xres = 1f;
		float yres = 1f;
		float xll = -11.080947f;
		float yll = 31.695501f;
		float xur = 23.152451f;
		float yur = 51.265385f;
		
		AnalysisLogger.setLogger(config.getConfigPath() + AlgorithmConfiguration.defaultLoggerFile);
		ASCConverter converter = new ASCConverter(config);
		String converted = converter.convertToASC(layername, "./test.asc", time, xll,xur,yll,yur,z, xres, yres);
		
		AnalysisLogger.getLogger().debug("ASC : "+converted);
		
		
	}

}
