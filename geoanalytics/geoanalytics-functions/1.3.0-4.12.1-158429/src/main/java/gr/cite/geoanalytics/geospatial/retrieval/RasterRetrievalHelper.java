package gr.cite.geoanalytics.geospatial.retrieval;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.DirectPosition2D;

public class RasterRetrievalHelper {

	public static GridCoverage2D getCoverage(String url) throws Exception {

		HttpClient client = new DefaultHttpClient();

		HttpGet request = new HttpGet(url);

		HttpResponse response = client.execute(request);

		//InputStream added in geotools version 18.0
		AbstractGridCoverage2DReader reader = new gr.cite.geoanalytics.geospatial.retrieval.GeoTiffReader(response.getEntity().getContent());

		GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
//		CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();

		return coverage;
	}
	
	public static void main(String [] args) throws Exception{
		
		String sst = "http://dionysus.di.uoa.gr:3000/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics:adbb5464-fae0-447c-b082-0f13c471f56d&format=geotiff";
//		String sst = "http://dl012.madgik.di.uoa.gr:8080/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics__sst&format=geotiff";
		
		GridCoverage2D gridCoverage = RasterRetrievalHelper.getCoverage(sst);
		float[] val = (float[])gridCoverage.evaluate(new DirectPosition2D(gridCoverage.getCoordinateReferenceSystem2D(), 21.91910, 36.70532) );
		System.out.println(val[0]);
		
//		gridCoverage.evaluate(new DirectPosition2D(coverage.getCoordinateReferenceSystem2D(), x, y)))[0]);
		
	}
	
	
}
