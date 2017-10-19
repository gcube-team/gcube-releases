package gr.cite.geoanalytics.functions.experiments.tests;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.coverage.grid.io.AbstractGridCoverage2DReader;
import org.geotools.geometry.DirectPosition2D;
import org.opengis.geometry.DirectPosition;
import org.opengis.geometry.Envelope;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

public class GeoTiffTest {

	public static void main(String[] args) throws Exception {

		String url = "http://dl014.madgik.di.uoa.gr:8080/geoserver/wcs?request=GetCoverage&version=2.0.1&coverageid=geoanalytics__20161201000000-GOS-L4_GHRSST-SSTfnd-OISST_HR_NRT-MED-v02.0-fv02.0&format=geotiff";

		HttpClient client = new DefaultHttpClient();

		HttpGet request = new HttpGet(url);

		HttpResponse response = client.execute(request);

		//InputStream added in geotools version 18.0
		AbstractGridCoverage2DReader reader = new gr.cite.geoanalytics.geospatial.retrieval.GeoTiffReader(response.getEntity().getContent());

		GridCoverage2D coverage = (GridCoverage2D) reader.read(null);
		CoordinateReferenceSystem crs = coverage.getCoordinateReferenceSystem2D();
		Envelope env = coverage.getEnvelope();

		double x = 19.125033720139346;
		double y = 31.24996621665651;

		for (int i = 0; i < 100; i++) {
			DirectPosition position = new DirectPosition2D(crs, x, y);

			float[] sample = (float[]) coverage.evaluate(position);

			x += 0.041;
		}
	}
}
