package org.gcube.dataanalysis.geo.charts;

import java.awt.Color;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.utils.GifSequenceWriter;
import org.gcube.dataanalysis.ecoengine.utils.TimeAnalyzer;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.AscRaster;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterReader;
import org.gcube.dataanalysis.geo.connectors.asc.AscRasterWriter;

import density.Grid;
import density.LazyGrid;
import density.Sample;
import density.ShrunkGrid;
import density.WorldImageProducer;

public class GeoMapChart {

	public static List<Tuple<Double>> generateRandomWorldPoints() {

		List<Tuple<Double>> xyvalues = new ArrayList<Tuple<Double>>();
		for (int j = 0; j < 8000; j++) {
			double randomx = ((180) * Math.random()) - 180 * Math.random();
			double randomy = ((90) * Math.random()) - 90 * Math.random();
			xyvalues.add(new Tuple<Double>(randomx, randomy));
		}

		return xyvalues;
	}

	public static void createWorldImageWithPoints(String configFolder, List<GeoTemporalPoint> xyvalues, String outImageFileName) throws Exception {
//		URL is = ClassLoader.getSystemResource("raster_res/templatelayerres05.asc");
//		GeoMapChart.class.getClassLoader().getResource("raster_res/templatelayerres05.asc");
		String is = (new File(configFolder,"templatelayerres05.asc")).getAbsolutePath();
		createImage(is, outImageFileName, xyvalues, true, 100d, 10000d, false, false,null);
	}
	
	public static void createWorldImageWithPointsAndFixedTime(String configFolder, List<GeoTemporalPoint> xyvalues, String outImageFileName, Date time) throws Exception {
		String is = (new File(configFolder,"templatelayerres05.asc")).getAbsolutePath();
		createImage(is, outImageFileName, xyvalues, true, 100d, 10000d, false, false,time);
	}
	
	private static List<Date> extractTimefromPoints(List<GeoTemporalPoint> points){
		List<Date> times = new ArrayList<Date>();
		HashMap<Date, String> timesh = new HashMap<Date, String>();  
		
		for (GeoTemporalPoint p : points){
			timesh.put(p.time,"");
		}
		
		for (Date t: timesh.keySet()){
			int i=0;
			for (Date d:times){
				if (d.after(t))
					break;
				i++;
			}
			times.add(i, t);
		}
		
		return times;
	}
	
	private static void createImage(String filepath, String outImageFileName, List<GeoTemporalPoint> xypoints, boolean displaypoints, double min, double max, boolean makelegend, boolean logscale, Date fixedTime) throws Exception {
		Grid g = new ShrunkGrid(new LazyGrid(filepath), 2000);
		double cellsize = g.getDimension().getcellsize();
		double xll = g.getDimension().getxllcorner();
		double yll = g.getDimension().getyllcorner();
		// int nrows = g.getDimension().getnrows();
		// int ncols = g.getDimension().getncols();

		WorldImageProducer d = new WorldImageProducer(g);
		AscRaster raster = new AscRaster(cellsize, -1, -1, xll, yll);

		Date minimumTime = new Date(System.currentTimeMillis());
		Date maximumTime = new Date(0);
		HashMap<Date,String> timeFrames = new HashMap<Date, String>();
		
		if (xypoints != null) {
			Sample[] samps = new Sample[xypoints.size()];
			int i = 0;
			
			 
			for (GeoTemporalPoint t : xypoints) {
				double longitude = t.x;
				double latitude = t.y;
				
				if (minimumTime.after(t.time))
					minimumTime = new Date(t.time.getTime());
				if (maximumTime.before(t.time))
					maximumTime = new Date(t.time.getTime());
				
				if (timeFrames.get(t.time)==null)
					timeFrames.put(t.time, "");
				
				if (fixedTime!=null && t.time.compareTo(fixedTime)!=0)
						continue;
						
				samps[i] = new Sample(1, raster.latitude2Index(latitude), raster.longitude2Index(longitude), latitude, longitude, "sample");
				i++;
			}
			if (displaypoints)
				d.setTestSamples(samps);
		}

		// we leave this piece of code here for future developments
		boolean logScale = logscale;
		if (!logScale) {
			d.setMode(1);
		}

		d.minval = min;
		d.maxval = max;

		d.visible = false;
		d.makeLegend = makelegend;
		d.makeTimeline = fixedTime!=null;
		
		if (d.makeTimeline){
			SimpleDateFormat sdf = new SimpleDateFormat(xypoints.get(0).timePattern, Locale.ENGLISH);
			int timeidx = TimeAnalyzer.getTimeIndexInTimeRange(maximumTime, minimumTime, fixedTime, timeFrames.size());
			d.setTime(timeFrames.size(), timeidx, sdf.format(minimumTime), sdf.format(fixedTime));
		}
		d.makeImage();
		d.writeImage(outImageFileName, 1);

	}

	
	public static HashMap<Double, Double> createWorldWeightedImage(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, Date fixedTime, HashMap<Double, Double> weights) throws Exception {
		String is = (new File(configFolder,"worldcountries_hires.asc")).getAbsolutePath();
		return createWeightedImage(is, xypoints, outImageFileName, tempdir, false,fixedTime,weights);
	}

	public static void createWorldWeightedImageInTime(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, boolean cumulative) throws Exception {
		String is = (new File(configFolder,"worldcountries_hires.asc")).getAbsolutePath();
		createImageInTime(is, configFolder, xypoints, outImageFileName, tempdir, cumulative);
	}
	
	public static void createEEZWeightedImageInTime(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, boolean cumulative) throws Exception {
		String is = (new File(configFolder,"eez.asc")).getAbsolutePath();
		createImageInTime(is, configFolder, xypoints, outImageFileName, tempdir, cumulative);
	}
	
	public static void createFAOAreasWeightedImageInTime(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, boolean cumulative) throws Exception {
		String is = (new File(configFolder,"faoareas.asc")).getAbsolutePath();
		createImageInTime(is, configFolder, xypoints, outImageFileName, tempdir, cumulative);
	}
	
	public static void createImageInTime(String rastersupport, String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, boolean cumulative) throws Exception {
		List<Date> times = extractTimefromPoints(xypoints);
		int i=1;
		List<String> images = new ArrayList<String>();
		HashMap<Double, Double> weights = null;
		for (Date t:times){
			String tempimg = new File(tempdir,i+"_"+UUID.randomUUID()+".png").getAbsolutePath();
			if (!cumulative)
				weights=null;
			
			HashMap<Double, Double> weightstemp = createWeightedImage(rastersupport, xypoints, tempimg, tempdir, false, t,weights);
			if (weightstemp!=null){
				images.add(tempimg);
				weights=weightstemp;
			}
			i++;
		}
		AnalysisLogger.getLogger().debug("GeoMapChart: Writing GIF "+outImageFileName);
		
		GifSequenceWriter.writeGif(outImageFileName, images, 1500);
		
		for (String image:images){
			new File(image).delete();
		}
		
	}
	
	public static void createPointsImageInTime(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir) throws Exception {
		List<Date> times = extractTimefromPoints(xypoints);
		int i=1;
		List<String> images = new ArrayList<String>();
		for (Date t:times){
			String tempimg = new File(tempdir,i+"_"+UUID.randomUUID()+".png").getAbsolutePath();
			createWorldImageWithPointsAndFixedTime(configFolder,xypoints, tempimg, t);
			images.add(tempimg);
			i++;
		}
		AnalysisLogger.getLogger().debug("GeoMapChart: Writing GIF");
		
		GifSequenceWriter.writeGif(outImageFileName, images, 1500);
		
		for (String image:images){
			new File(image).delete();
		}
		
	}
	
	
	public static void createEEZWeightedImage(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir) throws Exception {
		String is = (new File(configFolder,"eez.asc")).getAbsolutePath();
		createWeightedImage(is, xypoints, outImageFileName, tempdir, true,null,null);
	}

	public static void createFAOAreasWeightedImage(String configFolder,List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir) throws Exception {
		String is = (new File(configFolder,"faoareas.asc")).getAbsolutePath();
		createWeightedImage(is, xypoints, outImageFileName, tempdir, false,null,null);
	}

	public static HashMap<Double, Double> createWeightedImage(String rasterSupportFilePath, List<GeoTemporalPoint> xypoints, String outImageFileName, String tempdir, boolean logscale, Date fixedTime, HashMap<Double, Double> previousweights) throws Exception {

		AscRasterReader reader = new AscRasterReader();
		AscRaster raster = reader.readRaster(rasterSupportFilePath);

		HashMap<Double, Double> countryweights = new HashMap<Double, Double>();
		double max = -Double.MAX_VALUE;
		double min = Double.MAX_VALUE;

		for (GeoTemporalPoint triplet : xypoints) {
			double x = triplet.x;
			double y = triplet.y;
			double weigh = triplet.weight;
			Date time = triplet.time;
			
			if (fixedTime!=null && time.compareTo(fixedTime)!=0)
				continue;
			
			int latid = raster.latitude2Index(y);
			int lonid = raster.longitude2Index(x);

			Double value = raster.getValue(latid, lonid);
//			int[] lats = { latid - 1, latid + 1, latid - 2, latid + 2};
//			int[] longs = { lonid - 1, lonid + 1, lonid - 2, lonid + 2};
			int[] lats = { latid - 1, latid + 1};
			int[] longs = { lonid - 1, lonid + 1};

			// search nearby
			if (Double.isNaN(value)) {
				for (int la = 0; la < lats.length; la++) {
					for (int lo = 0; lo < longs.length; lo++) {
						value = raster.getValue(lats[la], longs[lo]);
						if (!Double.isNaN(value))
							break;
					}
					if (!Double.isNaN(value))
						break;
				}
			}
			//check again if the value is NaN
			if (!Double.isNaN(value)) {

				Double currentw = countryweights.get(value);
				if (currentw != null) {
					weigh += currentw;
				}
				else{
					if (previousweights!=null){
						currentw = previousweights.get(value);
						if (currentw != null) 
							weigh += currentw;
					}
				}
				if (weigh > max)
					max = weigh;
				if (weigh < min)
					min = weigh;

				countryweights.put(value, weigh);
			} 
		}
		
		//case in which we did not find any country in this year
		if (countryweights.size()==0)
			return null;
		//add the previous missing weights
		if (previousweights!=null){
			for (Double country:previousweights.keySet()){
				if (countryweights.get(country)==null){
					Double pweigh = previousweights.get(country);
					countryweights.put(country,pweigh);
					if (pweigh > max)
						max = pweigh;
					if (pweigh < min)
						min = pweigh;
				}
			}
		}
		//adjust min and max
		if (min==max){
			if (max>0)
				min = 0;
			else
				max = 0;
		}
		/*
		 * for (Double key:countryweights.keySet()){ Double weigh = countryweights.get(key);
		 * 
		 * if (weigh==0) System.out.println(key+":"+weigh); }
		 */
		AscRasterWriter writer = new AscRasterWriter();

		int ncols = raster.getCols();
		int nrows = raster.getRows();

		for (int i = 0; i < nrows; i++) {
			for (int j = 0; j < ncols; j++) {
				double country = raster.getValue(i, j);
				if (!Double.isNaN(country)) {
					Double weigh = countryweights.get(country);
					if (weigh != null)
						raster.setValue(i, j, weigh);
					else
						raster.setValue(i, j, 0);
				}
			}
		}

		String ascfile = new File(tempdir, "" + UUID.randomUUID() + ".asc").getAbsolutePath();
		writer.writeRaster(ascfile, raster);
		createImage(ascfile, outImageFileName, xypoints, false, min, max, true, logscale,fixedTime);
		System.gc();
		boolean deleted = new File(ascfile).delete();
		AnalysisLogger.getLogger().debug("GeoMapChart: deleted ASC file "+ascfile+" :"+deleted);
		
		return countryweights;
	}

}
