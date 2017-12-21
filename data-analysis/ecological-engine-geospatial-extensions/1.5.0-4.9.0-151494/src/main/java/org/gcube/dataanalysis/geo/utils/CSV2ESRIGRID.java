package org.gcube.dataanalysis.geo.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Transformations;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;

public class CSV2ESRIGRID {

	public static void main(String [] args) throws Exception{
		String inputFolder = "C:\\Users\\coro\\Desktop\\DatabaseBackup\\HCAFs\\";
//		String inputFile = new File(inputFolder,"hcaf_d - TEST.csv").getAbsolutePath();
		String inputFile = new File(inputFolder,"hcaf_d2050.csv").getAbsolutePath();
		
		
		double resolution = 0.5;
		String latColumn = "centerlat";
		String longColumn = "centerlong";
		String [] paramColumns = {"depthmin","depthmax","depthmean","depthsd","sstanmean","sstansd","sstmnmax",
				"sstmnmin","sstmnrange","sbtanmean","salinitymean","salinitysd","salinitymax","salinitymin","salinitybmean",
				"primprodmean","iceconann","oceanarea","landdist"};
		
		for (String paramColumn:paramColumns){
			String outputFile =new File(inputFolder,"hcaf_2050"+paramColumn+".asc").getAbsolutePath();
			AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
			toESRI(inputFile, outputFile, resolution, latColumn, longColumn, paramColumn);
		}
	}
	
	public static void toESRI(String inputFile, String outputFile, double resolution,String latcolumn,String longcolumn,String paramcolumn) throws Exception{
				
		float BBxLLf = -180;
		float BBxURf= 180;
		float BByLLf = -90;
		float BByURf = 90; 
		
		try {
			long t0 = System.currentTimeMillis();
			
			AnalysisLogger.getLogger().debug("toESRI->Building raster object for "+paramcolumn);
			AnalysisLogger.getLogger().debug("toESRI->Building raster object to "+outputFile);
			
			BufferedReader br = new BufferedReader(new FileReader(new File(inputFile)));
			
			String line = br.readLine();
			String delimiter = ",";
			
			List<String> lineElems=Transformations.parseCVSString(line, delimiter);
			int latColIdx = 0;
			int longColIdx = 0;
			int valColIdx = 0;
			int k=0;
			for (String elem:lineElems){
				if (elem.equalsIgnoreCase(latcolumn))
					latColIdx=k;
				if (elem.equalsIgnoreCase(longcolumn))
					longColIdx=k;
				if (elem.equalsIgnoreCase(paramcolumn))
					valColIdx=k;
				k++;
			}
			
			AnalysisLogger.getLogger().debug("toESRI->Lat column index "+latColIdx);
			AnalysisLogger.getLogger().debug("toESRI->Long column index "+longColIdx);
			AnalysisLogger.getLogger().debug("toESRI->Parameter column index "+valColIdx);
			
			line = br.readLine();
			
			int ncols = (int)Math.round((BBxURf-BBxLLf)/resolution);
			int nrows = (int) Math.round((BByURf-BByLLf)/resolution);
			AnalysisLogger.getLogger().debug("toESRI->Matrix size "+nrows +"X"+ncols);
			
			double [][] data = new double[nrows][ncols];
			AscRaster raster = new AscRaster(data,resolution,-1,-1,BBxLLf,BByLLf);
			
			while (line!=null){
				lineElems=Transformations.parseCVSString(line, delimiter);
				float longitude = Float.parseFloat(lineElems.get(longColIdx));
				float latitude = Float.parseFloat(lineElems.get(latColIdx));
				float value = -9999;
				try{
					value = Float.parseFloat(lineElems.get(valColIdx));}
				catch(Exception e){
//					AnalysisLogger.getLogger().debug("toESRI->Wrong value "+e.getLocalizedMessage());
				}
				
				int lonidx = Math.max(0,raster.longitude2Index(longitude));
				int latidx = Math.max(0,raster.latitude2Index(latitude));
				if (longitude==10.75 && latitude==43.75)
						AnalysisLogger.getLogger().debug("toESRI->"+longitude+","+latitude+","+value+"->"+lonidx+","+latidx);
				raster.setValue(latidx,lonidx,value);
				line = br.readLine();
			}
			
			File outputfile = new File(outputFile);
			AnalysisLogger.getLogger().debug("toESRI->Writing raster file " + outputfile.getAbsolutePath());
			AscRasterWriter writer = new AscRasterWriter();
			writer.writeRaster(outputfile.getAbsolutePath(), raster);
			AnalysisLogger.getLogger().debug("toESRI->Elapsed: Whole operation completed in " + ((double) (System.currentTimeMillis() - t0) / 1000d) + "s");
			
			br.close();
		} catch (Exception e) {
			e.printStackTrace();
			AnalysisLogger.getLogger().debug("toESRI->ERROR!: " + e.getLocalizedMessage());
			throw e;
		} finally {
			
		}
	}
	
	
	
	
}
