package gr.cite.geoanalytics.environmental.data.retriever.test;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.Assert;
import org.junit.Test;

import gr.cite.geoanalytics.environmental.data.retriever.OxygenRetriever;
import gr.cite.geoanalytics.environmental.data.retriever.model.Oxygen;

public class TestOxygenRetriever {

	private static final Logger logger = LogManager.getLogger(TestOxygenRetriever.class);

	public void assertIntEquals(int source, int target, String message) {
		Assert.assertTrue(" - [" + source + " =/= " + target + " ] ", source == target);
	}

	@Test
	public void testGetByLatLong() {
		double latitude = 37;
		double longitude = 0;

		try {
			OxygenRetriever dataRetriever = new OxygenRetriever();
			Integer[] oxygen = dataRetriever.getByLatLongAsArray(latitude, longitude);

			logger.debug("Oxygen Values = " + Arrays.toString(oxygen));

			Assert.assertArrayEquals("Oxygen test failed", oxygen,
					new Integer[] { 217, 215, 218, 225, 222, 225, 226, 219, 216, 212, 205, 201, 197, 194, 193, 191, 189, 190, 193, 196, 194, 201, 202, 206 });
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testGetByDateLatLong() {
		double latitude = 38.845858;
		double longitude = 23.735962;

		try {
			Oxygen oxygen = new OxygenRetriever().getByDateLatLong("31-12-2016", latitude, longitude);
			logger.debug("Oxygen = " + oxygen.getValueAsInt());
			assertIntEquals(oxygen.getValueAsInt(), 219, "Oxygen values do not match");
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void testLandGetByLatLong() {
		double latitude = 38.939918;
		double longitude = 22.120972;

		try {
			OxygenRetriever oxygenRetriever = new OxygenRetriever();
			Integer[] oxygen = oxygenRetriever.getByLatLongAsArray(latitude, longitude);

			logger.debug("Oxygen = " + Arrays.toString(oxygen));
			Assert.assertArrayEquals("Oxygen test failed", oxygen, null);
		} catch (Exception e) {
			logger.error(e);
		}
	}

	@Test
	public void test() {
		double latitude = 37.9;
		double longitude = 23.7;

		try {
			new OxygenRetriever().getByDateLatLong("1/1/2016", latitude, longitude);
			new OxygenRetriever().getByDateLatLong("29/2/2016", latitude, longitude);
			new OxygenRetriever().getByDateLatLong("31/5/2022", latitude, longitude);
			new OxygenRetriever().getByDateLatLong("30/12/2035", latitude, longitude);
			new OxygenRetriever().getByDateLatLong("16/4/1998", latitude, longitude);
		} catch (Exception e) {
			logger.error(e);
		}
	}
	
	@Test
	public void testOutOfBoundsGetByLatLong() {
		double latitude =160.939918;
		double longitude = 122.120972;
		
		try {
			OxygenRetriever oxygenRetriever = new OxygenRetriever();
			Integer[] oxygen = oxygenRetriever.getByLatLongAsArray(latitude, longitude);

			logger.debug("Oxygen = " + Arrays.toString(oxygen));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
