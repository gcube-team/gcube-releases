package org.gcube.dataanalysis.geo.utils;

import java.util.ArrayList;
import java.util.List;

import org.geotoolkit.referencing.CRS;
import org.geotoolkit.referencing.crs.DefaultGeographicCRS;
import org.opengis.referencing.crs.CoordinateReferenceSystem;
import org.opengis.referencing.cs.CoordinateSystemAxis;
import org.opengis.referencing.operation.MathTransform;
import org.opengis.referencing.operation.TransformException;

import ucar.unidata.geoloc.LatLonPoint;
import ucar.unidata.geoloc.LatLonPointImpl;
import ucar.unidata.geoloc.ProjectionPoint;
import ucar.unidata.geoloc.ProjectionPointImpl;

/**
 * This class wraps the GeoTools/GeoAPI coordinate reference system methods, providing a set of convenience methods such as transformations and validity checks.
 */
public final class CrsHelper {

	public static final String PLATE_CARREE_CRS_CODE = "CRS:84";
	public static final List<String> SUPPORTED_CRS_CODES = new ArrayList<String>();

	private CoordinateReferenceSystem crs;
	private MathTransform crsToLatLon;
	private MathTransform latLonToCrs;
	private boolean isLatLon;

	static {
		// Find the supported CRS codes
		// I think this is the appropriate method to get all the CRS codes
		// that we can support
		for (Object codeObj : CRS.getSupportedCodes("urn:ogc:def")) {
			SUPPORTED_CRS_CODES.add((String) codeObj);
		}
		System.out.println("Supported Codes:"+SUPPORTED_CRS_CODES);
	}

	/** Private constructor to prevent direct instantiation */
	private CrsHelper() {
	}

	public static CrsHelper fromCrsCode(String crsCode) throws Exception {
		// TODO: could cache CrsHelpers with the same code
		CrsHelper crsHelper = new CrsHelper();
		try {
			// The "true" means "force longitude first" axis order
			crsHelper.crs = CRS.decode(crsCode, true);
			// Get transformations to and from lat-lon.
			// The "true" means "lenient", i.e. ignore datum shifts. This
			// is necessary to prevent "Bursa wolf parameters required"
			// errors (Some CRSs, including British National Grid, fail if
			// we are not "lenient".)
			crsHelper.crsToLatLon = CRS.findMathTransform(crsHelper.crs, DefaultGeographicCRS.WGS84, true);
			crsHelper.latLonToCrs = CRS.findMathTransform(DefaultGeographicCRS.WGS84, crsHelper.crs, true);
			crsHelper.isLatLon = crsHelper.crsToLatLon.isIdentity();
			return crsHelper;
		} catch (Exception e) {
			throw new Exception("Error creating CrsHelper from code " + crsCode);
		}
	}

	public CoordinateReferenceSystem getCoordinateReferenceSystem() {
		return this.crs;
	}

	/**
	 * @return true if the given coordinate pair is within the valid range of both the x and y axis of this coordinate reference system.
	 */
	public boolean isPointValidForCrs(ProjectionPoint point) {
		return this.isPointValidForCrs(point.getX(), point.getY());
	}

	/**
	 * @return true if the given coordinate pair is within the valid range of both the x and y axis of this coordinate reference system.
	 */
	public boolean isPointValidForCrs(double x, double y) {
		CoordinateSystemAxis xAxis = this.crs.getCoordinateSystem().getAxis(0);
		CoordinateSystemAxis yAxis = this.crs.getCoordinateSystem().getAxis(1);
		return x >= xAxis.getMinimumValue() && x <= xAxis.getMaximumValue() && y >= yAxis.getMinimumValue() && y <= yAxis.getMaximumValue();
	}

	/**
	 * Transforms the given x-y point in this {@link #getCoordinateReferenceSystem() CRS} to a LatLonPoint.
	 * 
	 * @throws TransformException
	 *             if the required transformation could not be performed
	 */
	public LatLonPoint crsToLatLon(double x, double y) throws TransformException {
		if (this.isLatLon) {
			// We don't need to do the transformation
			return new LatLonPointImpl(y, x);
		}
		// We know x must go first in this array because we selected
		// "force longitude-first" when creating the CRS for this grid
		double[] point = new double[] { x, y };
		// Transform to lat-lon in-place
		this.crsToLatLon.transform(point, 0, point, 0, 1);
		return new LatLonPointImpl(point[1], point[0]);
	}

	/**
	 * Transforms the given x-y point in this {@link #getCoordinateReferenceSystem() CRS} to a LatLonPoint.
	 * 
	 * @throws TransformException
	 *             if the required transformation could not be performed
	 */
	public LatLonPoint crsToLatLon(ProjectionPoint point) throws TransformException {
		return this.crsToLatLon(point.getX(), point.getY());
	}

	/**
	 * Transforms the given LatLonPoint to an x-y point in this {@link #getCoordinateReferenceSystem() CRS}.
	 * 
	 * @throws TransformException
	 *             if the required transformation could not be performed
	 */
	public ProjectionPoint latLonToCrs(LatLonPoint latLonPoint) throws TransformException {
		return this.latLonToCrs(latLonPoint.getLongitude(), latLonPoint.getLatitude());
	}

	/**
	 * Transforms the given longitude-latitude point to an x-y point in this {@link #getCoordinateReferenceSystem() CRS}.
	 * 
	 * @throws TransformException
	 *             if the required transformation could not be performed
	 */
	public ProjectionPoint latLonToCrs(double longitude, double latitude) throws TransformException {
		if (this.isLatLon) {
			// We don't need to do the transformation
			return new ProjectionPointImpl(longitude, latitude);
		}
		// We know x must go first in this array because we selected
		// "force longitude-first" when creating the CRS for this grid
		double[] point = new double[] { longitude, latitude };
		// Transform to lat-lon in-place
		this.latLonToCrs.transform(point, 0, point, 0, 1);
		return new ProjectionPointImpl(point[0], point[1]);
	}

	/**
	 * @return true if this crs is lat-lon
	 */
	public boolean isLatLon() {
		return this.isLatLon;
	}

	public static void main(String[] args) throws Exception{
		CrsHelper helper = fromCrsCode("CRS:84");
//		boolean valid = helper.isPointValidForCrs(180, 0);
//		System.out.println(valid);
		LatLonPoint point = helper.crsToLatLon(190,10);
		double x = point.getLongitude();
		double y = point.getLatitude();
		System.out.println(point+" ("+x+","+y+")");
	}

}
