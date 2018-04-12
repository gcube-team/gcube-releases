package org.gcube.dataanalysis.geo.insertion;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDFDataExplorer;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.meta.NetCDFMetadata;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.gcube.dataanalysis.geo.utils.ThreddsExplorer;
import org.opengis.metadata.Metadata;

import ucar.nc2.dataset.CoordinateAxis;
import ucar.nc2.dt.GridDatatype;
import ucar.nc2.dt.grid.GridDataset;
import ucar.nc2.units.DateRange;

public class ThreddsFetcher {

	private GeoNetworkInspector featurer;
	public static String NetCDFDateFormat = "time: E MMM dd HH:mm:ss zzz yyyy";
	public static String HumanDateFormat = "MM-dd-yy HH:mm";

	public ThreddsFetcher(String scope) {
		featurer = new GeoNetworkInspector();
		featurer.setScope(scope);
	}

	public void fetch(String threddsCatalogURL) throws Exception {

		List<String> filesURL = ThreddsExplorer.getFiles(threddsCatalogURL);
		for (String filename : filesURL) {
			if (!filename.endsWith(".nc"))
				continue;
//			if (!filename.equalsIgnoreCase("bathymetrycf2_CLIMATOLOGY_METEOROLOGY_ATMOSPHERE_.nc"))
//				continue;
			
			String url = OGCFormatter.getOpenDapURL(threddsCatalogURL, filename);
			if (ThreddsExplorer.isGridDataset(url)) {
				// retrieve information
				GridDataset gds = ucar.nc2.dt.grid.GridDataset.open(url);
				
				List<GridDatatype> gridTypes = gds.getGrids();
				for (GridDatatype gdt : gridTypes) {
					String description = gdt.getDescription();
					if ((description==null) || (description.length()==0))
						description = gdt.getName();
					// get date range
					DateRange dr = gdt.getCoordinateSystem().getDateRange();
//					SimpleDateFormat netcdfDF = new SimpleDateFormat(NetCDFDateFormat, Locale.ENGLISH);
					SimpleDateFormat humanDF = new SimpleDateFormat(HumanDateFormat, Locale.ROOT);
					String hStartDate = null;
					String hEndDate = null;
					String duration = null;
					String resolution = gdt.getTimeDimension()==null?null:""+gdt.getTimeDimension().getLength();
					
					int numberOfDimensions = 2;
					if ((gdt.getZDimension()!=null)&&(gdt.getZDimension().getLength()>1)){
						numberOfDimensions = 3;
						AnalysisLogger.getLogger().debug("Length of Z Dimension: "+gdt.getZDimension().getLength());
					}
					else
						AnalysisLogger.getLogger().debug("Bidimensional Layer ");
					
					if (dr != null) {
						hStartDate = dr.getStart() == null ? null : humanDF.format(dr.getStart().getDate());
						hEndDate = dr.getEnd() == null ? null : humanDF.format(dr.getEnd().getDate());
						duration = dr.getDuration() == null ? null : "" + dr.getDuration();
					}
					
					// control if the file is yet on GN
//					String generatedTitle = generateTitle(filename, description, hStartDate, hEndDate, numberOfDimensions);
					String generatedTitle = generateTitle(gds.getTitle()+": "+gds.getDescription(), description, hStartDate, hEndDate, numberOfDimensions);
					
					CoordinateAxis xAxis = gdt.getCoordinateSystem().getXHorizAxis();
					CoordinateAxis yAxis = gdt.getCoordinateSystem().getYHorizAxis();
					AnalysisLogger.getLogger().debug("Bounds:"+xAxis.getMinValue()+","+yAxis.getMinValue()+","+xAxis.getMaxValue()+","+yAxis.getMaxValue());
					
					Metadata previousmeta = featurer.getGNInfobyUUIDorName(generatedTitle);
					if (previousmeta!=null){
						AnalysisLogger.getLogger().debug("***WARNING: layer yet found on GeoNetwork***");
						continue;
					}
					/*Layers check - for testing only 
					else {
						AnalysisLogger.getLogger().debug("***layer retrieval failed***");
						if (true) System.exit(0);
					}
					*/
					
					// get resolution - take the maximum regular step
					double resolutionX = Math.abs((double) (xAxis.getMaxValue() - xAxis.getMinValue()) / (double) xAxis.getShape()[0]);
					double resolutionY = Math.abs((double) (yAxis.getMaxValue() - yAxis.getMinValue()) / (double) yAxis.getShape()[0]);
					
					//build metadata
					NetCDFMetadata metadataInserter = new NetCDFMetadata();
					metadataInserter.setGeonetworkUrl(featurer.getGeonetworkURLFromScope());
					metadataInserter.setGeonetworkUser(featurer.getGeonetworkUserFromScope());
					metadataInserter.setGeonetworkPwd(featurer.getGeonetworkPasswordFromScope());
					
					// Build standard info:
					metadataInserter.setThreddsCatalogUrl(threddsCatalogURL);
					metadataInserter.setLayerUrl(url);
					metadataInserter.setLayerName(gdt.getName());
					metadataInserter.setSourceFileName(filename);
					// insert ranges and sampling
					metadataInserter.setTitle(generatedTitle);
					metadataInserter.setAbstractField(generateAbstractField(gdt.getName(), filename, description, gdt.getUnitsString().trim(), hStartDate, hEndDate, duration, resolution, numberOfDimensions, gds.getTitle(), gds.getDescription()));
					metadataInserter.setResolution(Math.max(resolutionX, resolutionY));
					// set Bounding box
					double minX = NetCDFDataExplorer.getMinX(gdt.getCoordinateSystem());
					double maxX = NetCDFDataExplorer.getMaxX(gdt.getCoordinateSystem());
					double minY = NetCDFDataExplorer.getMinY(gdt.getCoordinateSystem());
					double maxY = NetCDFDataExplorer.getMaxY(gdt.getCoordinateSystem());
					
					if (gds.getTitle().toUpperCase().contains("WORLD OCEAN ATLAS"))
					{
							AnalysisLogger.getLogger().debug("Managing WoA Layer");
							minX = minX-180;
							maxX = maxX-180;
					}
					
					metadataInserter.setXLeftLow(minX);
					metadataInserter.setYLeftLow(minY);
					metadataInserter.setXRightUpper(maxX);
					metadataInserter.setYRightUpper(maxY);
					
					//set keywords
					metadataInserter.setCustomTopics(filename, description,numberOfDimensions+"D",gds.getTitle(),gds.getDescription(),"unit:"+gdt.getUnitsString().trim());
					//set Temporal Extent
					if (hStartDate!=null){
						metadataInserter.setStartDate(dr.getStart().getDate());
						metadataInserter.setEndDate(dr.getEnd().getDate());
					}
					
					AnalysisLogger.getLogger().debug("title: " + metadataInserter.getTitle());
					AnalysisLogger.getLogger().debug("abstract: " + metadataInserter.getAbstractField());
					
					try{
					 metadataInserter.insertMetaData();
					}catch(Exception e){
						AnalysisLogger.getLogger().debug("Error in inserting file: "+filename);
					}
//					 break;
				}
				
			}
//			break;
		}
	}

	public static String generateTitle(String filename, String description, String startDate, String endDate, int numberOfDimensions) {
		String dateString = "";
		if (startDate != null){
			if (startDate.equals(endDate))
				dateString = " in [" + startDate + "]";
			else	
				dateString = " from [" + startDate + "] to [" + endDate + "]";
		}
		
		description = description + " "+dateString+" (" + numberOfDimensions+ "D) {" + filename + "}";
		
		return description.replaceAll("( )+", " ");
	}

	public static String generateAbstractField(String layername, String filename, String description, String unit, String startDate, String endDate, String duration, String timeInstants, int numberOfDimensions, String netcdftitle, String netcdfdescription) {
		String timeresolutionString = "";
		String durationString = "";
		if ((timeInstants != null) && (timeInstants.length() > 0))
			timeresolutionString = " Number of time instants: " + timeInstants+".";

		if ((duration != null) && (duration.length() > 0))
			durationString = " Time interval lenght: " + duration+".";

		String dateString = "";
		if (startDate != null)
			dateString = " in the time range between [" + startDate + "] and [" + endDate + "].";
		
		String unitString = "";
		if ((unit != null) && (unit.length()>0))
			unitString= " (" + unit + ")"; 
		
		String numberOfDimensionsString = "";
		if (numberOfDimensions>0)
			numberOfDimensionsString = " Number of Dimensions: "+numberOfDimensions+".";
		
		String  netcdfinfo = "";
		if (netcdftitle!=null)
			netcdfinfo = " "+netcdftitle+": "+netcdfdescription+".";
		
		return layername + ": " + description + unitString+dateString + durationString + timeresolutionString + numberOfDimensionsString+netcdfinfo+" Local file in iMarine: " + filename + ".";
	}

	public static void main(String[] args) throws Exception {
		AnalysisLogger.setLogger("./cfg/"+AlgorithmConfiguration.defaultLoggerFile);
//		ThreddsFetcher tf = new ThreddsFetcher("/gcube/devsec");
		ThreddsFetcher tf = new ThreddsFetcher(null);
		tf.fetch("http://thredds.research-infrastructures.eu/thredds/catalog/public/netcdf/catalog.xml");
	}

}
