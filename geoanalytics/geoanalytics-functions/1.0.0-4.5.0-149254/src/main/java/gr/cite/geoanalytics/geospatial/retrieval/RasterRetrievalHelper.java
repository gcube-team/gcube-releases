package gr.cite.geoanalytics.geospatial.retrieval;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;

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
}
