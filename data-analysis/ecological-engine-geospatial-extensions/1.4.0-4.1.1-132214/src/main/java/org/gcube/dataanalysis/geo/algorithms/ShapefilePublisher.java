package org.gcube.dataanalysis.geo.algorithms;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.UUID;

import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveType;
import org.gcube.dataanalysis.ecoengine.datatypes.PrimitiveTypesList;
import org.gcube.dataanalysis.ecoengine.datatypes.ServiceType;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.PrimitiveTypes;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.ServiceParameters;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.ZipTools;
import org.gcube.dataanalysis.geo.meta.PolyMapMetadata;
import org.gcube.dataanalysis.geo.utils.GdalConverter;
import org.gcube.dataanalysis.geo.utils.GeospatialDataPublicationLevel;
import org.gcube.spatial.data.gis.model.report.PublishResponse;

public class ShapefilePublisher extends StandardLocalInfraAlgorithm{

	private static String layerTitleParam = "MapTitle";
	private static String layerAbstractParam = "MapAbstract";
	private static String FileParam = "ShapeFileZip";
	private static String ShapeFileParam = "ShapeFileName";
	private static String TopicsParam = "Topics";
	private static String DBUserParam = "DBUser";
	private static String DBPasswordParam = "DBPassword";
	private static String DBUrlParameter = "DBUrl";
	static String publicationLevel= "PublicationLevel";
	
	@Override
	public void init() throws Exception {
		log("ShapefilePublisher->initialised");
	}

	@Override
	public String getDescription() {
		String description = "An algorithm to publish shapefiles under WMS and WFS standards in the e-Infrastructure. The produced WMS, WFS links are reported as output of this process. The map will be available in the VRE for consultation.";
		return description;
	}

	//static String shapeImporting = "shp2pgsql -g the_geom -d shapefile2.shp | PGPASSWORD=d4science2 psql -h geoserver-test.d4science-ii.research-infrastructures.eu -p 5432 -U postgres timeseriesgisdb";
	//static String shapeImporting = "shp2pgsql -g the_geom -d %1$s | PGPASSWORD=%2$s psql -h %3$s -p 5432 -U %4$s %5$s";
	static String shapeImporting = "shp2pgsql -s 4326 -g the_geom -d %1$s public.%6$s | PGPASSWORD=%2$s psql -h %3$s -p 5432 -U %4$s %5$s";
	
	 
	
	@Override
	protected void process() throws Exception {
		status = 10;
		//collect information
		String databaseJdbc = getInputParameter(DBUrlParameter);
		String databaseUser = getInputParameter(DBUserParam);
		String databasePwd = getInputParameter(DBPasswordParam);
		 
		//get the shapefile and extract the zip file
		String zipFile = getInputParameter(FileParam);
		String shapeFileName = getInputParameter(ShapeFileParam);
		String layerName = getInputParameter(layerTitleParam);
		String layerAbstract = getInputParameter(layerAbstractParam);
		String topics = getInputParameter(TopicsParam);
		String username = getInputParameter("ServiceUserName");
		String publicationLevelValue = getInputParameter(publicationLevel);
		
		log("ShapefilePublisher->"+databasePwd);
		log("ShapefilePublisher->Parameters:");
		log("ShapefilePublisher->zipFile:"+zipFile);
		log("ShapefilePublisher->shapeFileName:"+shapeFileName);
		log("ShapefilePublisher->layerName:"+layerName);
		log("ShapefilePublisher->layerAbstract:"+layerAbstract);
		log("ShapefilePublisher->topics:"+topics);
		log("ShapefilePublisher->databaseJdbc:"+databaseJdbc);
		log("ShapefilePublisher->databaseUser:"+databaseUser);
		log("ShapefilePublisher->service user name:"+username);
		
		if (topics == null || topics.trim().length()==0)
			throw new Exception("Error topics missing!");
		
		
		
		File tempFolder = new File(config.getConfigPath(),""+UUID.randomUUID());
		log("ShapefilePublisher->Creating temp folder "+tempFolder);
		
		boolean created = tempFolder.mkdir();
		log("ShapefilePublisher->Temp Folder creation check "+created);
		
		log("ShapefilePublisher->unzipping file :"+zipFile+" in folder "+tempFolder.getAbsolutePath());
		
		ZipTools.unZip(zipFile, tempFolder.getAbsolutePath());
		// parse  a string like this:
		// jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu:5432/timeseriesgisdb
		String databaseName = databaseJdbc.substring(databaseJdbc.lastIndexOf("/")+1);
		String databaseAddress = databaseJdbc.substring(databaseJdbc.indexOf("//")+2,databaseJdbc.lastIndexOf(":"));
			
		log("ShapefilePublisher->Parsed database Info:"+databaseName+" ["+databaseAddress+"]");
		
		String shapefile = new File (tempFolder,shapeFileName).getAbsolutePath();
		log("ShapefilePublisher->Shape file to search for:"+shapefile);
		
		String gisTableName = shapeFileName;
		String gisRandomTableName = ("shp_"+UUID.randomUUID()).replace("-", "");
		try{
			gisTableName = shapeFileName.substring(0,shapeFileName.lastIndexOf("."));
		}catch(Exception  e){
			throw new Exception("Error - Wrong file name "+shapeFileName);
		}
		
		log("ShapefilePublisher->Original table name :"+gisTableName);
		
		log("ShapefilePublisher->Table name to produce :"+gisRandomTableName);
		
		
		String shapeImportCommand = String.format(shapeImporting, shapefile, databasePwd, databaseAddress, databaseUser, databaseName,gisRandomTableName);
		
		log("ShapefilePublisher->Shape file command:"+shapeImportCommand);
		
		//run the importer
		status = 50;
		List<String> commandsExecuted = GdalConverter.command(shapeImportCommand, tempFolder.getAbsolutePath());
		String commandExecuted = commandsExecuted.toString();
		
		log("ShapefilePublisher->Command executed output:"+commandExecuted);
		
		//check for the import to be success
		if (!commandExecuted.contains("COMMIT")){
			throw new Exception("An error occurred when importing the file "+commandExecuted);
		}
		
		log("ShapefilePublisher->Publishing the table "+gisRandomTableName);
		
		String scope = config.getGcubeScope();
		
		
		double resolution = 0;
		String datastore = "timeseriesws";
		String defaultStyle = "polygon";
		String workspace = "aquamaps";
		String purpose = "To Publish Geometric Layers for user-provided Vector Maps";
		String credits = "Generated via the DataMiner Service";
		String keywords = topics.replace(AlgorithmConfiguration.listSeparator, ",");
		
		boolean isprivate = false;
		if (GeospatialDataPublicationLevel.valueOf(publicationLevelValue)==GeospatialDataPublicationLevel.PRIVATE)
			isprivate=true;
		PublishResponse response = null;
		try{
			response = PolyMapMetadata.publishTable(scope, gisRandomTableName, resolution, username, layerName, defaultStyle, workspace, datastore, purpose, credits, keywords, isprivate);
		}catch(Exception e){
			e.printStackTrace();
			log ("ShapefilePublisher->Error during table publication: "+e.getLocalizedMessage());
			throw new Exception("Error during the publication of the shapefile on the SDI");
		}
		
		log("ShapefilePublisher->Finished publishing the table");
		
		if (response == null) {
			log("ShapefilePublisher->Error in generating map");
			throw new Exception("Impossible to publish on GeoNetwork or GeoServer this table: " + gisRandomTableName+" possibly it is already present!");
		} else {
			//writing output
			addOutputString("GIS map title", layerName);
			addOutputString("GIS map UUID", "" + response.getPublishedMetadata().getFileIdentifier());
			addOutputString("GIS Table ", gisRandomTableName);
			addOutputString("Generated by ", username);
			addOutputString("Resolution", "" + resolution);
			addOutputString("Style", "" + defaultStyle);
			addOutputString("Keyword", "" + topics);
		}
		
		log("ShapefilePublisher->Output produced:"+outputParameters);
		
		log("ShapefilePublisher->All done!");
		status = 100;
	}

	@Override
	protected void setInputParameters() {
		try {
			PrimitiveType e = new PrimitiveType(Enum.class.getName(), GeospatialDataPublicationLevel.values(), PrimitiveTypes.ENUMERATED, publicationLevel, "The visibility level of the produced map",""+GeospatialDataPublicationLevel.PRIVATE);
			inputs.add(e);
			addStringInput(layerTitleParam, "Title of the geospatial dataset to be shown on GeoExplorer", "Generic Vector Layer");
			addStringInput(ShapeFileParam, "Name of the shape file inside the zip", "shapefile.shp");
			addStringInput(layerAbstractParam, "Abstract defining the content, the references and usage policies", "Abstract");
			inputs.add(new PrimitiveType(File.class.getName(), null, PrimitiveTypes.FILE, FileParam, "Shapefile zip file to process"));
			inputs.add(new PrimitiveTypesList(String.class.getName(), PrimitiveTypes.STRING, TopicsParam, "Topics to be attached to the published dataset. E.g. Biodiversity, D4Science, Environment, Weather", false));
			inputs.add(new ServiceType(ServiceParameters.USERNAME,"ServiceUserName","The final user Name"));
			
			addRemoteDatabaseInput("UsersGisTablesDB", DBUrlParameter,DBUserParam,DBPasswordParam, "driver", "dialect");
			
		} catch (Throwable e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public void shutdown() {

		}

}
