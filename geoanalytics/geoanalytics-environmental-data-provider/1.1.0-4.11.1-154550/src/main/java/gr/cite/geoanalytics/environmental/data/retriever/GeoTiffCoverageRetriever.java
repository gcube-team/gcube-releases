package gr.cite.geoanalytics.environmental.data.retriever;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.factory.Hints;
import org.geotools.gce.geotiff.GeoTiffReader;

import gr.cite.geoanalytics.environmental.data.retriever.utils.ResourceUtils;

public class GeoTiffCoverageRetriever {
	
	private static final Logger logger = LogManager.getLogger(GeoTiffCoverageRetriever.class);

	private DateResourceResolver dateResourceResolver;
	private Map<String, GridCoverage2D> geotiffToCoverage;
	
	public GeoTiffCoverageRetriever(DateResourceResolver dateResourceResolver){
		this.dateResourceResolver = dateResourceResolver;
		this.geotiffToCoverage = new HashMap<>();
		
		for(String resource : dateResourceResolver.getAllResourceNames()){
			try {
				this.geotiffToCoverage.put(resource, createGeoTiffCoverageByResource(resource));
			} catch (Exception e) {
				logger.error(e);
			}
		}
	}	
	
	public GridCoverage2D getCoverageByDate(String date){
		String resource = dateResourceResolver.getResourceFromDate(date);
		return geotiffToCoverage.get(resource);
	}
	
	public GridCoverage2D getCoverageByResource(String resource){
		return geotiffToCoverage.get(resource);
	}
	
	private GridCoverage2D createGeoTiffCoverageByResource(String resource) throws Exception {
		return new GeoTiffReader(ResourceUtils.getResource(resource), new Hints()).read(null);
	}
}
