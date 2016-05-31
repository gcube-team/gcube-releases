package org.gcube.dataanalysis.geo.algorithms;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalExternalAlgorithm;
import org.gcube.dataanalysis.geo.utils.ThreddsPublisher;

public class RasterDataPublisher extends StandardLocalExternalAlgorithm{
	private static String layerTitleParam = "DatasetTitle";
	private static String layerAbstractParam = "DatasetAbstract";
	private static String layerInnerNameParam = "InnerLayerName";
	private static String FileParam = "RasterFile";
	private static String TopicsParam = "Topics";
	private static String ResolutionParam = "SpatialResolution";
	private static String FileNameInfraParam = "FileNameOnInfra";
	
	@Override
	public String getDescription() {
		return "This algorithm publishes a raster file as a maps or datasets in the e-Infrastructure. NetCDF-CF files are encouraged, as WMS and WCS maps will be produced using this format. For other types of files (GeoTiffs, ASC etc.) only the raw datasets will be published. The resulting map or dataset will be accessible via the VRE GeoExplorer by the VRE participants.";
	}

	@Override
	public void init() throws Exception {
	}

	@Override
	protected void process() throws Exception {
		status = 10;
		String scope = config.getGcubeScope();
		String username = config.getParam("ServiceUserName");
		String fileAbsolutePath = config.getParam(FileParam);
		String fileName = config.getParam(FileNameInfraParam);
		String layerTitle = config.getParam(layerTitleParam);
		String layerName = config.getParam(layerInnerNameParam);
		String abstractField = config.getParam(layerAbstractParam);
		String[] topics = config.getParam(TopicsParam).split(AlgorithmConfiguration.listSeparator);
		
		
		AnalysisLogger.getLogger().debug("scope: "+scope);
		AnalysisLogger.getLogger().debug("username: "+username);
		AnalysisLogger.getLogger().debug("fileAbsolutePath: "+fileAbsolutePath);
		AnalysisLogger.getLogger().debug("layerTitle: "+layerTitle);
		AnalysisLogger.getLogger().debug("layerName: "+layerName);
		AnalysisLogger.getLogger().debug("abstractField: "+abstractField);
		AnalysisLogger.getLogger().debug("topics: "+topics);
		AnalysisLogger.getLogger().debug("filename: "+fileName);
		
		
		if (scope==null || username==null)
			throw new Exception ("Service parameters are not set - please contact the Administrators");
		if (fileAbsolutePath==null || fileAbsolutePath.trim().length()==0)
			throw new Exception ("No file has been provided to the process");
		if (layerTitle==null || layerTitle.trim().length()==0)
			throw new Exception ("Please provide a valid dataset title");
		if (abstractField==null || abstractField.trim().length()==0)
			throw new Exception ("Please provide a valid abstract for the dataset");
		if (topics==null || topics.length==0 || topics[0].length()==0)
			throw new Exception ("Please provide at least a valid topic for the dataset");
		
		double resolution = Double.parseDouble(config.getParam(ResolutionParam));
		AnalysisLogger.getLogger().debug("resolution: "+resolution);
		
	
      if (!(fileName.endsWith(".nc")||fileName.endsWith(".tiff")||fileName.endsWith(".geotiff")||fileName.endsWith(".asc")||fileName.endsWith(".ncml")))
			throw new Exception("Wrong file name: allowed files extensions are .nc, .tiff, .geotiff, .asc, .ncml");
				
		File f = new File(fileAbsolutePath); 
		File newf = new File(f.getParent(),fileName);
		AnalysisLogger.getLogger().debug("renaming: "+fileAbsolutePath+" to "+newf.getAbsolutePath());
		boolean renamed = f.renameTo(newf);
		
		if (!renamed)
			throw new Exception("Impossible to use "+fileName+" as file name");
		
		fileName=fileName.trim();
		layerTitle=layerTitle.trim();
		layerName=layerName.trim();
		abstractField = abstractField.trim();
		
		ArrayList<String> listTopics = new ArrayList<String>();
		listTopics.addAll(Arrays.asList(topics));
		listTopics.add(username);
		listTopics.add("D4Science");
		listTopics.add(scope);
		String [] topicsListArr = new String[listTopics.size()];
		topics = listTopics.toArray(topicsListArr);
		
		boolean result = ThreddsPublisher.publishOnThredds(scope, username, newf.getAbsolutePath(), layerTitle, layerName, abstractField, topics, resolution);
		
		if (result) {
			addOutputString("Created map name", layerTitle);
			addOutputString("Map abstract", abstractField);
			addOutputString("Map Topics", Arrays.toString(topics));
			addOutputString("Inner layer name", layerName);
			addOutputString("File name created in the e-Infrastructure", fileName);
			addOutputString("Map creator", username);
		}
		status = 100;
	}

	@Override
	protected void setInputParameters() {
		try {
			addStringInput(layerTitleParam, "Title of the geospatial dataset to be shown on GeoExplorer", "Generic Raster Layer");
			addStringInput(layerAbstractParam, "Abstract defining the content, the references and usage policies", "Abstract");
			addStringInput(layerInnerNameParam, "Name of the inner layer or band to be published as a Map (ignored for non-NetCDF files)", "band_1");
			addStringInput(FileNameInfraParam, "Name of the file that will be created in the infrastructures", "raster-"+System.currentTimeMillis()+".nc");
			inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, FileParam, "Raster dataset to process"));
			inputs.add(new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, TopicsParam, "Topics to be attached to the published dataset. E.g. Biodiversity, D4Science, Environment, Weather", false));
			addDoubleInput(ResolutionParam, "The resolution of the layer. For NetCDF file this is automatically estimated by data (leave -1)", "-1d");
			inputs.add(new ServiceType(ServiceParameters.USERNAME,"ServiceUserName","The final user Name"));
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	@Override
	public void shutdown() {
		AnalysisLogger.getLogger().debug("RasterFilePublisher - shutdown");
	}	

}

