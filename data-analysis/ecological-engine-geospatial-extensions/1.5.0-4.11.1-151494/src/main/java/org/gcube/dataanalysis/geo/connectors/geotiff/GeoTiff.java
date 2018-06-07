package org.gcube.dataanalysis.geo.connectors.geotiff;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.UUID;

import org.gcube.contentmanagement.graphtools.utils.HttpRequest;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.utils.Tuple;
import org.gcube.dataanalysis.geo.connectors.asc.ASC;
import org.gcube.dataanalysis.geo.interfaces.GISDataConnector;
import org.gcube.dataanalysis.geo.utils.GdalConverter;
import org.gcube.dataanalysis.geo.utils.transfer.TransferUtil;

public class GeoTiff implements GISDataConnector {

	public double zScale = 0;
	public String persistenceDir;
	public String geoTiffUrl;
	
	public GeoTiff(AlgorithmConfiguration config) throws Exception {
		persistenceDir = config.getPersistencePath();
	}
	
	@Override
	public List<Double> getFeaturesInTimeInstantAndArea(String layerURL, String layerName, int time, List<Tuple<Double>> coordinates3d, double BBxL, double BBxR, double BByL, double BByR) throws Exception {
		

		if (time > 0)
			throw new Exception("Time is currently not supported in WCS");

		String uuid = null;
		String ascFile = null;
		try {

			int urlCheck = HttpRequest.checkUrl(layerURL, null, null);
			AnalysisLogger.getLogger().debug("Checking url: " + urlCheck);
			String randomFile = new File(persistenceDir, "geotiff" + UUID.randomUUID().toString().replace("-", "")).getAbsolutePath();
			uuid = randomFile + ".tiff";
//			HttpRequest.downloadFile(layerURL, uuid);
			TransferUtil downloadutil = new TransferUtil();
			downloadutil.setConnectiontimeout(120000);
			downloadutil.setTransferTimeout(120000);
			downloadutil.performTransfer(new URI(layerURL), uuid);
			
			AnalysisLogger.getLogger().debug("Converting to ASCII file: " + uuid);
			ascFile = GdalConverter.convertToASC(uuid,0);
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
}
