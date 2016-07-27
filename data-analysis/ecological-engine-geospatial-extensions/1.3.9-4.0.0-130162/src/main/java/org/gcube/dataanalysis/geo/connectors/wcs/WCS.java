package org.gcube.dataanalysis.geo.connectors.wcs;

import java.io.File;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.data.transfer.common.TransferUtil;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.ASC;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.meta.OGCFormatter;
import org.gcube.dataanalysis.geo.utils.GdalConverter;
import org.gcube.dataanalysis.geo.utils.GeoTiffMetadata;
import org.gcube.dataanalysis.geo.utils.VectorOperations;

public class WCS implements GISDataConnector {
	// WCS examples
	// String wcs = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs?service=WCS&version=1.0.0&request=GetCoverage&COVERAGE=aquamaps:WorldClimBio2&CRS=EPSG:4326&BBOX=-60,-30,60,30,0,0&WIDTH=640&HEIGHT=480&DEPTH=1&FORMAT=geotiff";
	// String wcs = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs?service=WCS&version=1.0.0&request=GetCoverage&COVERAGE=aquamaps:WorldClimBio2&CRS=EPSG:4326&BBOX=-60,-30,60,30&WIDTH=640&HEIGHT=480&FORMAT=geotiff&CIAO=1&DEPTH=2&TIME=3&RESX=2&RESY=3";
	// String wcs = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs?service=WCS&version=1.0.0&request=GetCoverage&COVERAGE=aquamaps:WorldClimBio2&CRS=EPSG:4326&BBOX=-60,-30,60,30&WIDTH=640&HEIGHT=480&FORMAT=geotiff";
	// String wcs = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs/wcs?service=wcs&version=1.0.0&request=GetCoverage&coverage=aquamaps:WorldClimBio2&CRS=EPSG:4326&bbox=-180,0,180,90&width=1&height=1&format=geotiff&RESPONSE_CRS=EPSG:4326";

	public String baseURL;
	public String coverage;
	public String crs = "EPSG:4326";
	public String boundingBox = "-180,-90,180,90";
	public String width = "640";
	public String height = "480";
	public String depth = null;
	public String format = "geotiff";
	public String time = null;
	public String responseCRS = "EPSG:4326";
	public String resx;
	public String resy;
	public String resz;

	public double zScale = 0;
	public String persistenceDir;

	public HashMap<String, String> parameters = new HashMap<String, String>();

	public WCS(AlgorithmConfiguration config, String wcsURL) throws Exception {
		parseWCSURL(wcsURL);
		persistenceDir = config.getPersistencePath();
		retrieveZScale();
	}

	public void retrieveZScale() throws Exception {
		String uuid = null;
		try{
		String url = OGCFormatter.getWcsUrl(baseURL, coverage, crs, responseCRS, "-180,0,180,90", "1", "1", depth, format, resx, resy, resz, time, parameters);
		AnalysisLogger.getLogger().debug("Retrieving Z parameters: " + url);
		int urlCheck = HttpRequest.checkUrl(url, null, null);
		AnalysisLogger.getLogger().debug("Checking url: " + urlCheck);
		uuid = new File(persistenceDir, "geotiff" + UUID.randomUUID().toString().replace("-", "") + ".tiff").getAbsolutePath();
//		HttpRequest.downloadFile(url, uuid);
		TransferUtil downloadutil = new TransferUtil();
		downloadutil.setConnectiontimeout(120000);
		downloadutil.setTransferTimeout(120000);
		downloadutil.performTransfer(new URI(url), uuid);
		GeoTiffMetadata meta = new GeoTiffMetadata();
		meta.readAndDisplayMetadata(uuid);
		zScale = meta.zScale;
		AnalysisLogger.getLogger().debug("Retrieved Z Scale: " + zScale);
		}catch (Exception e){
			throw e;
		}
		finally{
			if (uuid!=null)
				AnalysisLogger.getLogger().debug("Deleting point-tiff file :"+uuid+" "+new File(uuid).delete());
		}
	}

	public void parseWCSURL(String wcsURL) {
		int questionIDX = wcsURL.indexOf("?");
		if (questionIDX < 0) {
			baseURL = wcsURL;
			return;
		}

		baseURL = wcsURL.substring(0, questionIDX);

		String toParse = wcsURL.substring(questionIDX + 1);
		String[] elements = toParse.split("&");
		for (String element : elements) {
			int eqIdx = element.indexOf("=");
			if (eqIdx < 0)
				continue;
			String paramName = element.substring(0, eqIdx);
			String paramValue = element.substring(eqIdx + 1);
			associateParameter(paramName, paramValue);
		}

		AnalysisLogger.getLogger().debug("WCS Parsing finished");
	}

	private void associateParameter(String parameter, String value) {

		if (parameter.equalsIgnoreCase("COVERAGE"))
			coverage = value;
		else if (parameter.equalsIgnoreCase("CRS"))
			crs = value;
		else if (parameter.equalsIgnoreCase("BBOX"))
			boundingBox = value;
		else if (parameter.equalsIgnoreCase("WIDTH"))
			width = value;
		else if (parameter.equalsIgnoreCase("HEIGHT"))
			height = value;
		else if (parameter.equalsIgnoreCase("DEPTH"))
			depth = value;
		else if (parameter.equalsIgnoreCase("FORMAT"))
			format = value;
		else if (parameter.equalsIgnoreCase("RESX"))
			resx = value;
		else if (parameter.equalsIgnoreCase("RESY"))
			resy = value;
		else if (parameter.equalsIgnoreCase("RESZ"))
			resz = value;
		else if (parameter.equalsIgnoreCase("TIME"))
			time = value;
		else if (parameter.equalsIgnoreCase("RESPONSE_CRS"))
			responseCRS = value;
		else {
			if (!parameter.equalsIgnoreCase("service") && !parameter.equalsIgnoreCase("version") && !parameter.equalsIgnoreCase("request"))
				parameters.put(parameter, value);
		}
	}

	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception {

		if (time > 0)
			throw new Exception("Time is currently not supported in WCS");

		String uuid = null;
		String ascFile = null;
		try {
			String resolutionx = resx;
			String resolutiony = resy;

			if (coordinates3d.size() > 1) {
				double x1 = coordinates3d.get(0).getElements().get(0);
				double x2 = coordinates3d.get(1).getElements().get(0);
				resolutionx = "" + Math.abs(x2 - x1);
				double y1 = coordinates3d.get(0).getElements().get(0);
				double y2 = coordinates3d.get(1).getElements().get(0);
				resolutiony = "" + Math.abs(y2 - y1);
			}
			
			AnalysisLogger.getLogger().debug("Resolution parameters: resx: " +resolutionx+" resy: "+resolutiony );
			
			String url = OGCFormatter.getWcsUrl(baseURL, coverage, crs, responseCRS, "" + BBxL + "," + BByL + "," + BBxR + "," + BByR, null, null, depth, format, resolutionx, resolutiony, resz, "" + time, parameters);
			AnalysisLogger.getLogger().debug("Retrieving Z parameters: " + url);
			int urlCheck = HttpRequest.checkUrl(url, null, null);
			AnalysisLogger.getLogger().debug("Checking url: " + urlCheck);
			String randomFile = new File(persistenceDir, "geotiff" + UUID.randomUUID().toString().replace("-", "")).getAbsolutePath();
			uuid = randomFile + ".tiff";
//			HttpRequest.downloadFile(url, uuid);
			TransferUtil downloadutil = new TransferUtil();
			downloadutil.setConnectiontimeout(120000);
			downloadutil.setTransferTimeout(120000);
			downloadutil.performTransfer(new URI(url), uuid);
			AnalysisLogger.getLogger().debug("Converting to ASCII file: " + uuid);
			ascFile = GdalConverter.convertToASC(uuid,255);
			AnalysisLogger.getLogger().debug("Conversion to ASCII complete: " + ascFile);
			ASC asc = new ASC();
			List<Double> points = asc.getFeaturesInTimeInstantAndArea(ascFile, layerName, time, coordinates3d, BBxL, BBxR, BByL, BByR);
			AnalysisLogger.getLogger().debug("Retrieved: " + points.size() + " points");
			return points;
		} catch (Exception e) {
			throw e;
		} finally {
			if (uuid != null){
				AnalysisLogger.getLogger().debug("Deleting tiff:"+new File(uuid).delete());
				AnalysisLogger.getLogger().debug("Deleting tiff aux file:"+new File(uuid.replace(".tiff", ".prj")).delete());
			}
			if (ascFile!= null){
				AnalysisLogger.getLogger().debug("Deleting asc:"+new File(ascFile).delete());
				AnalysisLogger.getLogger().debug("Deleting asc aux file:"+new File(ascFile+".aux.xml").delete());
				
			}
		}
	}

	@Override
	public double getMinZ(String layerURL, String layerName) {
		// TODO: understand z management on the basis of at least one example
		return 0;
	}

	@Override
	public double getMaxZ(String layerURL, String layerName) {
		// TODO: understand z management on the basis of at least one example
		return 0;
	}

	public static void main(String[] args) throws Exception {

		String wcsUrl = "http://geoserver-dev.d4science-ii.research-infrastructures.eu/geoserver/wcs/wcs?service=wcs&version=1.0.0&request=GetCoverage&coverage=aquamaps:WorldClimBio2&CRS=EPSG:4326&bbox=-180,0,180,90&width=1&height=1&format=geotiff&RESPONSE_CRS=EPSG:4326";

		AlgorithmConfiguration config = new AlgorithmConfiguration();
		config.setGcubeScope("/d4science.research-infrastructures.eu/gCubeApps/BiodiversityLab");
		config.setConfigPath("./cfg/");
		config.setPersistencePath("./");

		WCS wcs = new WCS(config, wcsUrl);
		
		List<Tuple<Double>> triplets = VectorOperations.generateCoordinateTripletsInBoundingBox(-30d, 30d, -30d, 30d, 0, 0.5, 0.5);

		List<Double> points = wcs.getFeaturesInTimeInstantAndArea(wcsUrl, "", 0, triplets, -180d, 180d, -90d, 90d);
		AnalysisLogger.getLogger().debug("points:" + points);
	}

}
