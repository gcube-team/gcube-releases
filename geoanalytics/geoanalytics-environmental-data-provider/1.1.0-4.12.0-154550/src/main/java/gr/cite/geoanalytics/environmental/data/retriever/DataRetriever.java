package gr.cite.geoanalytics.environmental.data.retriever;

import java.awt.Rectangle;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.geotools.coverage.grid.GridCoverage2D;
import org.geotools.geometry.DirectPosition2D;

import com.vividsolutions.jts.util.Assert;

import gr.cite.geoanalytics.environmental.data.retriever.model.Data;
import gr.cite.geoanalytics.environmental.data.retriever.model.Unit;

public abstract class DataRetriever<T extends Data> {

	private static final Logger logger = LogManager.getLogger(DataRetriever.class);

	private DateResourceResolver dateResourceResolver;
	private GeoTiffCoverageRetriever geotiffCoverageRetriever;

	abstract T castData(Data data);

	public DataRetriever(String dataFolder, String dataFileExtension) {
		dataFolder = !dataFolder.endsWith("/") ? dataFolder + "/" : dataFolder;

		try {
			this.dateResourceResolver = new DateResourceResolver(dataFolder, dataFileExtension);
			this.geotiffCoverageRetriever = new GeoTiffCoverageRetriever(dateResourceResolver);
		} catch (Exception e) {
			logger.error("Failed to initialize " + this.getClass().getSimpleName(), e);
		}
	}

	/**
	 * Retrieve the data of a point on map based on date
	 * 
	 * @param date : must be in format dd-mm-yyyy or dd.mm.yyyy or dd/mm/yyyy
	 * @param latitude: the latitude of the point
	 * @param longitude : the longitude of the point
	 * 
	 * @return Returns a {@link Data}
	 * @see Retrievable#getByLatLong(java.lang.String, double, double)
	 */
	public T getByDateLatLong(String date, double latitude, double longitude) throws Exception {
		try{
			Assert.isTrue(date != null && date.length() > 0, "Date cannot be empty");
			Assert.isTrue(latitude >= -90 && latitude <= 90, "Latitude cannot exceed the range [-90, 90]");
			Assert.isTrue(longitude >= -180 && longitude <= 180, "Longitude cannot exceed the range [-180, 180]");
		} catch(Exception e){
			logger.error(e.getMessage());
			return null;
		}

		date = date.contains("/") ? date.replace("/", "-") : date;
		date = date.contains(".") ? date.replace(".", "-") : date;

		Date formattedDate = null;

		try {
			formattedDate = new SimpleDateFormat("dd-mm-yyyy").parse(date);
		} catch (Exception e) {
			throw new Exception("Date " + date + " must match the dd-mm-yyyy format", e);
		}

		T data = null;

		try {
			if (formattedDate != null) {
				GridCoverage2D coverage = this.geotiffCoverageRetriever.getCoverageByDate(date);
				data = getDataOfCoverage(coverage, date, latitude, longitude);
			}
		} catch (Exception e) {
			throw new Exception("Could not retrieve data for requested date: " + date, e);
		}

		if (data != null) {
//			logger.debug(data);
		}

		return castData(data);
	}

	/**
	 * Retrieve the data values of a point on map based on a year
	 * 
	 * @param latitude : the latitude of the point
	 * @param longitude: the longitude of the point
	 * 
	 * @return Returns a Map<{@link String}, {@link Data}> with Date-Data entries.
	 * @see Retrievable#getByDateLatLong(double, double)
	 */
	public Map<String, T> getByLatLong(double latitude, double longitude) throws Exception {
		Map<String, T> results = new LinkedHashMap<String, T>();

		List<String> dates = this.dateResourceResolver.getAllDates();

		for (String date : dates) {
			try {
				T data = getByDateLatLong(date, latitude, longitude);
				results.put(date, data);
			} catch (Exception e) {
				throw new Exception("Could not retrieve all available data", e);
			}
		}

		return results;
	}

	public Integer[] getByLatLongAsArray(double latitude, double longitude) throws Exception {
		return getByLatLongAsArray(latitude, longitude, null);
	}

	/**
	 * Retrieve the data values of a point on map based on a year
	 * 
	 * @param latitude : the latitude of the point
	 * @param longitude: the longitude of the point
	 * @param unit: the desired S.I unit to format the values
	 * 
	 * @return Returns an int[24] of data values per 15 days of month.
	 * @see Retrievable#getByLatLongAsArray(double, double)
	 */
	public Integer[] getByLatLongAsArray(double latitude, double longitude, Unit unit) throws Exception {
		Map<String, T> results = this.getByLatLong(latitude, longitude);

		Integer[] array = new Integer[24];
		int index = 0;
		
		for (T data : results.values()) {
			if(data == null){
				return null;
			}
			
			Integer value = data.getValueAsInt(unit);
			
			if(value == null){
				return null;
			} else{
				array[index++] = value;
			}
		}
		
		return array;
	}

	private T getDataOfCoverage(GridCoverage2D coverage, String date, double latitude, double longitude) throws Exception {
		double minX = getBoundingBox(coverage).getMinX();
		double maxX = getBoundingBox(coverage).getMaxX();
		double minY = getBoundingBox(coverage).getMinY();
		double maxY = getBoundingBox(coverage).getMaxY();

		if (latitude <= minY || latitude >= maxY) {
			logger.error("Latitude " + latitude + " is out of bounds! Must be enclosed within [" + minY + ", " + maxY + "]");
			return null;
		}

		if (longitude <= minX || longitude >= maxX) {
			logger.error("Longitude " + longitude + " is out of bounds! Must be enclosed within [" + minX + ", " + maxX + "]");
			return null;
		}

		// may be optional
		//coverage = (GridCoverage2D) Operations.DEFAULT.resample(coverage, CRS.decode("EPSG:4326"));

		float value = ((float[]) coverage.evaluate(new DirectPosition2D(coverage.getCoordinateReferenceSystem2D(), longitude, latitude)))[0];

		Data data = new Data();
		data.setDate(date);
		data.setLatitude(latitude);
		data.setLongitude(longitude);
		data.setValue(value);

		return castData(data);
	}


	private Rectangle getBoundingBox(GridCoverage2D coverage) throws Exception {
		return coverage.getEnvelope2D().getBounds();
	}
}
