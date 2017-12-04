package org.gcube.portlets.user.gisviewer.server.util;

import java.util.LinkedHashMap;
import java.util.Map;

import org.apache.commons.lang.math.IntRange;

/**
 * The Class WPSXYResolutionFunction.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it Feb 9, 2016
 */
public class WPSXYResolutionFunction {

	private static final int MIN_RANGE_VALUE = 0;
	private static final int MAX_RANGE_VALUE = 11;
	private Map<IntRange, Float> xyResolution;
	private static WPSXYResolutionFunction INSTANCE;

	/**
	 * Gets the single instance of WPSXYResolutionFunction.
	 *
	 * @return single instance of WPSXYResolutionFunction
	 */
	public static synchronized WPSXYResolutionFunction getInstance() {

		if (INSTANCE == null)
			INSTANCE = new WPSXYResolutionFunction();
		return INSTANCE;
	}

	/**
	 * Instantiates a new WPSXY resolution function.
	 */
	private WPSXYResolutionFunction() {

		xyResolution = new LinkedHashMap<IntRange, Float>(5);
		xyResolution.put(new IntRange(MIN_RANGE_VALUE, 2), new Float(10.0));
		xyResolution.put(new IntRange(3, 5), new Float(5.0));
		xyResolution.put(new IntRange(6, 8), new Float(1.0));
		xyResolution.put(new IntRange(9, MAX_RANGE_VALUE), new Float(0.5));
	}

	/**
	 * Gets the XY resolution of.
	 *
	 * @param value            the value
	 * @return the XY resolution of
	 * @throws Exception the exception
	 */
	public Float getXYResolutionOf(int value)
		throws Exception {

		boolean found = false;
		float resolution = Float.MIN_VALUE;
		for (IntRange range : xyResolution.keySet()) {
			if (range.containsInteger(value)) {
				found = true;
				resolution = xyResolution.get(range);
				break;
			}
		}
		if (!found)
			throw new Exception("Input value: " + value + ", not found in: " +
				MIN_RANGE_VALUE + " - " + MAX_RANGE_VALUE);
		return resolution;
	}
}
