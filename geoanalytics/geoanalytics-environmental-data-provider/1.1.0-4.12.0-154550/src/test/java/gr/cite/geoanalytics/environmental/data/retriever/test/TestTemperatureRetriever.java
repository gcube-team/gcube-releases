package gr.cite.geoanalytics.environmental.data.retriever.test;

import static gr.cite.geoanalytics.environmental.data.retriever.model.Unit.CELCIUS;
import static gr.cite.geoanalytics.environmental.data.retriever.model.Unit.KELVIN;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import gr.cite.geoanalytics.environmental.data.retriever.TemperatureRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.model.Temperature;

public class TestTemperatureRetriever {

	private static final Logger logger = LogManager.getLogger(TestTemperatureRetriever.class);
	private static TemperatureRetriever temperatureRetriever = new TemperatureRetriever();

	public void assertIntEquals(int source, int target, String message) {
		Assert.assertTrue(" - [" + source + " =/= " + target + " ] ", source == target);
	}

	@Test
	public void testGetByLatLong() {
		double latitude = 37;
		double longitude = 0;

		try {
			Integer[] celcius = temperatureRetriever.getByLatLongAsArray(latitude, longitude, CELCIUS);
			Integer[] kelvin = temperatureRetriever.getByLatLongAsArray(latitude, longitude, KELVIN);

			logger.debug("CELCIUS = " + Arrays.toString(celcius));
			logger.debug("KELVIN  = " + Arrays.toString(kelvin));

			Assert.assertArrayEquals("Kelvin  test failed", kelvin,
					new Integer[] { 289, 289, 289, 288, 288, 289, 289, 291, 291, 293, 296, 295, 297, 298, 297, 298, 297, 297, 297, 295, 293, 291, 291, 289 });
			Assert.assertArrayEquals("Celcius test failed", celcius,
					new Integer[] { 16, 16, 15, 15, 15, 16, 16, 18, 18, 20, 23, 22, 24, 25, 23, 25, 24, 24, 23, 22, 20, 18, 17, 16 });
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetByDateLatLong() {
		double latitude = 36;
		double longitude = 11;

		try {
			Temperature temperature = temperatureRetriever.getByDateLatLong("1-01-2016", latitude, longitude);
			logger.debug("Temperature Celsius = " + temperature.getValueAsInt(CELCIUS));
			logger.debug("Temperature Kelvin  = " + temperature.getValueAsInt(KELVIN));

			assertIntEquals(temperature.getValueAsInt(CELCIUS), 18, "Temperature Celsius values do not match");
			assertIntEquals(temperature.getValueAsInt(KELVIN), 291, "Temperature Kelvin  values do not match");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testLandGetByLatLong() {
		double latitude = 38.939918;
		double longitude = 22.120972;

		try {
			Integer[] celcius = temperatureRetriever.getByLatLongAsArray(latitude, longitude, CELCIUS);
			Integer[] kelvin = temperatureRetriever.getByLatLongAsArray(latitude, longitude, KELVIN);

			logger.debug("CELCIUS = " + Arrays.toString(celcius));
			logger.debug("KELVIN  = " + Arrays.toString(kelvin));

			Assert.assertArrayEquals("Kelvin  test failed", kelvin, null);
			Assert.assertArrayEquals("Celcius test failed", celcius, null);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Test
	public void testOutOfBoundsGetByLatLong() {
		double latitude =160.939918;
		double longitude = 122.120972;
		
		try {
			Integer[] celcius = temperatureRetriever.getByLatLongAsArray(latitude, longitude, CELCIUS);
			Integer[] kelvin = temperatureRetriever.getByLatLongAsArray(latitude, longitude, KELVIN);
			
			logger.debug("CELCIUS = " + Arrays.toString(celcius));
			logger.debug("KELVIN  = " + Arrays.toString(kelvin));
			
			Assert.assertArrayEquals("Kelvin  test failed", kelvin, null);
			Assert.assertArrayEquals("Celcius test failed", celcius, null);
		} catch (Exception e) {
			logger.error(e);
		}
	}
}
