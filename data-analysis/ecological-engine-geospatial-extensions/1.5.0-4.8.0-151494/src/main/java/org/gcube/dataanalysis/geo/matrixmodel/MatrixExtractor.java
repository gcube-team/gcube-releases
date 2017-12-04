package org.gcube.dataanalysis.geo.matrixmodel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.ASC;
import org.gcube.dataanalysis.geo.connectors.asc.AscDataExplorer;
import org.gcube.dataanalysis.geo.connectors.geotiff.GeoTiff;
import org.gcube.dataanalysis.geo.connectors.netcdf.NetCDF;

import org.gcube.dataanalysis.geo.connectors.table.Table;
import org.gcube.dataanalysis.geo.connectors.table.TableMatrixRepresentation;
import org.gcube.dataanalysis.geo.connectors.wcs.WCS;
import org.gcube.dataanalysis.geo.connectors.wfs.WFS;
import org.gcube.dataanalysis.geo.infrastructure.GeoNetworkInspector;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.meta.GenericLayerMetadata;
import org.gcube.dataanalysis.geo.utils.VectorOperations;
import org.opengis.metadata.Metadata;
import org.opengis.metadata.identification.Identification;

public class MatrixExtractor {

	private GeoNetworkInspector gnInspector;
	private AlgorithmConfiguration configuration;
	public static int maxSignalLength = 100000;
	public static int maxzLength = 100000;
	boolean log = true;
	protected double currentResolution = 0.5;
	public List<Double> currentTimeValues;
	public List<Tuple<Double>> currentTuples;

	public MatrixExtractor(AlgorithmConfiguration configuration) {
		AnalysisLogger.getLogger().debug("Matrix Extractor: setting GeoNetwork search scope to " + configuration.getGcubeScope());
		gnInspector = new GeoNetworkInspector();
		gnInspector.setScope(configuration.getGcubeScope());
		this.configuration = configuration;
	}

	public GeoNetworkInspector getFeaturer() {
		return gnInspector;
	}

	public boolean isTable() {
		if (configuration.getParam(TableMatrixRepresentation.tableNameParameter) != null)
			return true;
		else
			return false;
	}

	protected List<Double> getRawValuesInTimeInstantAndBoundingBox(String layerTitle, int time, List<Tuple<Double>> coordinates3d, double xL, double xR, double yL, double yR, double resolution) throws Exception {
		return getRawValuesInTimeInstantAndBoundingBox(layerTitle, time, coordinates3d, xL, xR, yL, yR, resolution, false);
	}

	public GISDataConnector currentconnector;
	public String layerName;
	public String layerURL;

	public GISDataConnector getConnector(String layerTitle, double resolution) throws Exception {
		// get the layer
		Metadata meta = null;
		GISDataConnector connector = null;
		if (currentconnector != null)
			connector = currentconnector;
		else {
			if (isTable()) {
				AnalysisLogger.getLogger().debug("Extracting grid from table " + configuration.getParam(TableMatrixRepresentation.tableNameParameter));
				connector = new Table(configuration, resolution);
				currentconnector = connector;
			} else {
				try {
					meta = gnInspector.getGNInfobyUUIDorName(layerTitle);
				} catch (Exception e) {

				}
				// if the layer is not on GeoNetwork
				if (meta == null) {
					AnalysisLogger.getLogger().debug("Forcing setting of the meta");
					String[] urls = { layerTitle };
					String[] protocols = { "HTTP" };
					meta = new GenericLayerMetadata().createBasicMeta(urls, protocols);
				}
				layerName = gnInspector.getLayerName(meta);
				if (layerName == null)
					layerName = layerTitle;
				layerURL = "";
				if (gnInspector.isNetCDFFile(meta)) {
					Identification id = meta.getIdentificationInfo().iterator().next();
					String title = id.getCitation().getTitle().toString();
					AnalysisLogger.getLogger().debug("found a netCDF file with title " + title + " and layer name " + layerName);
					layerURL = gnInspector.getOpenDapLink(meta);
					connector = new NetCDF(layerURL, layerName);
				} else if (gnInspector.isAscFile(meta)) {
					AnalysisLogger.getLogger().debug("managing ASC File");
					layerURL = gnInspector.getHttpLink(meta);
					connector = new ASC();
				} else if (gnInspector.isWFS(meta)) {
					AnalysisLogger.getLogger().debug("found a Geo Layer with reference " + layerURL + " and layer name " + layerName);
					// layerURL = gnInspector.getGeoserverLink(meta);
					layerURL = gnInspector.getWFSLink(meta);
					connector = new WFS();
				} else if (gnInspector.isWCS(meta)) {
					AnalysisLogger.getLogger().debug("found a WCS Layer with reference " + layerURL + " and layer name " + layerName);
					layerURL = gnInspector.getWCSLink(meta);
					connector = new WCS(configuration, layerURL);
				} else if (gnInspector.isGeoTiff(meta)) {
					layerURL = gnInspector.getGeoTiffLink(meta);
					AnalysisLogger.getLogger().debug("found a GeoTiff with reference " + layerURL + " and layer name " + layerName);
					connector = new GeoTiff(configuration);
				} else {
					// treat as geotiff
					layerURL = layerTitle;
					AnalysisLogger.getLogger().debug("guessing a GeoTiff with reference " + layerURL + " and layer name " + layerName);
					connector = new GeoTiff(configuration);
				}
			}
		}
		currentconnector = connector;
		return currentconnector;
	}

	// 4D Extraction
	/**
	 * Extract raw values in a time instant according to a set of grid points and a bounding box
	 */
	public GISDataConnector connector;

	public List<Double> getRawValuesInTimeInstantAndBoundingBox(String layerTitle, int time, List<Tuple<Double>> coordinates3d, double xL, double xR, double yL, double yR, double resolution, boolean saveLayer) throws Exception {
		connector = getConnector(layerTitle, resolution);
		// execute connector
		if (connector != null)
			return connector.getFeaturesInTimeInstantAndArea(layerURL, layerName, time, coordinates3d, xL, xR, yL, yR);
		else
			throw new Exception("ERROR: Connector not found for layer " + layerTitle + " - Cannot Rasterize!");

	}

	public GISDataConnector getCurrentConnector() {
		return connector;
	}

	public double zmin;
	public double zmax;

	public double correctZ(double zValue, String layerURL, double resolution) throws Exception {
		GISDataConnector connector = getConnector(layerURL, resolution);
		zmin = connector.getMinZ(layerURL, layerName);
		zmax = connector.getMaxZ(layerURL, layerName);
		if (zValue < zmin)
			zValue = zmin;
		else if (zValue > zmax)
			zValue = zmax;

		return zValue;
	}

	/**
	 * Extract a grid of XY points with fixed time and z
	 * 
	 * @param layerTitle
	 * @param timeInstant
	 * @param x1
	 * @param x2
	 * @param y1
	 * @param y2
	 * @param z
	 * @param xResolution
	 * @param yResolution
	 * @param cachelayer
	 * @return
	 * @throws Exception
	 */
	public double[][] extractXYGridWithFixedTZ(String layerTitle, int timeInstant, double x1, double x2, double y1, double y2, double z, double xResolution, double yResolution, boolean cachelayer) throws Exception {

		currentResolution = (double) (xResolution + yResolution) / 2d;

		boolean faolayer = false;
		if (layerTitle == null)
			layerTitle = "";
		if (layerTitle.toLowerCase().contains("MatrixExtractor->FAO aquatic species distribution map")) {
			AnalysisLogger.getLogger().debug("MatrixExtractor->FAO DISTRIBUTION LAYER ... TO APPY PATCH!");
			faolayer = true;
		}
		if ((x2 < x1) || (y2 < y1)) {
			AnalysisLogger.getLogger().debug("MatrixExtractor->ERROR: BAD BOUNDING BOX!!!");
			return new double[0][0];
		}

		// adjust the BB in the case of one single point
		if (x2 == x1) {
			x2 = x2 + (xResolution / 2d);
			x1 = x1 - (xResolution / 2d);
		}

		if (y2 == y1) {
			y2 = y2 + (yResolution / 2d);
			y1 = y1 - (yResolution / 2d);
		}

		if (log)
			AnalysisLogger.getLogger().debug("Bounding box: (" + x1 + "," + x2 + ";" + y1 + "," + y2 + ")");

		List<Tuple<Double>> tuples = VectorOperations.generateCoordinateTripletsInBoundingBox(x1, x2, y1, y2, z, xResolution, yResolution);

		if (log) {
			AnalysisLogger.getLogger().debug("MatrixExtractor->Building the points grid according to YRes:" + yResolution + " and XRes:" + xResolution);
			AnalysisLogger.getLogger().debug("MatrixExtractor->Assigning "+tuples.size()+" values to the grid");
		}

		// long t0=System.currentTimeMillis();
		currentTimeValues = getRawValuesInTimeInstantAndBoundingBox(layerTitle, timeInstant, tuples, x1, x2, y1, y2, currentResolution, cachelayer);
		currentTuples = tuples;
		// AnalysisLogger.getLogger().debug("Elapsed:"+(System.currentTimeMillis()-t0));
		double[][] slice = VectorOperations.vectorToMatix(currentTimeValues, x1, x2, y1, y2, xResolution, yResolution);

		if (log) {
			AnalysisLogger.getLogger().debug("Taken " + currentTimeValues.size() + " values");
			AnalysisLogger.getLogger().debug("MatrixExtractor->Reassigned:" + (slice.length * slice[0].length));
		}
		// applyNearestNeighbor();

		if (log)
			AnalysisLogger.getLogger().debug("Features map: rows " + slice.length + ", cols " + slice[0].length);
		return slice;
	}

}
