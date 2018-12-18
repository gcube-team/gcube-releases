package org.gcube.dataanalysis.geo.algorithms;

import it.cnr.aquamaps.CSquare;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.dataanalysis.ecoengine.configuration.AlgorithmConfiguration;
import org.gcube.dataanalysis.ecoengine.interfaces.StandardLocalInfraAlgorithm;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseFactory;
import org.gcube.dataanalysis.ecoengine.utils.DatabaseUtils;
import org.gcube.dataanalysis.geo.meta.PolyMapMetadata;
import org.gcube.dataanalysis.geo.utils.GeospatialDataPublicationLevel;
import org.gcube.spatial.data.gis.model.report.PublishResponse;
import org.gcube.spatial.data.gis.model.report.Report.OperationState;
import org.hibernate.SessionFactory;

import scala.collection.Iterator;

public abstract class MapsCreator extends StandardLocalInfraAlgorithm {

	static String databaseParameterName = "TimeSeriesDataStore";
	static String dbuserParameterName = "user";
	static String dbpasswordParameterName = "password";
	static String dburlParameterName = "STOREURL";

	static String inputTableParameter = "InputTable";
	static String outputTableParameter = "OutputTable";
	static String xParameter = "xDimension";
	static String yParameter = "yDimension";
	static String csquareParameter = "csquaresDimension";
	
	static String probabilityParameter = "Probability";
	static String infoParameter = "Info";
	static String resolutionParameter = "Resolution";
	static String layerNameParameter = "MapName";
	static String publicationLevel= "PublicationLevel";
	static int maxNPoints = 259000;
	SessionFactory gisdbconnection = null;
	SessionFactory smdbconnection = null;
	
	private static String createProbTable = "create table  %1$s (geomid serial, x real, y real, probability real);";
	private static String columnsProbNames = " geomid, x , y, probability, the_geom";
	
	private static String createInfoTable = "create table  %1$s (geomid serial, x real, y real, info character varying);";
	private static String columnsInfoNames = " geomid, x , y, info, the_geom";
	
	private static String addGeometryColumn = "Select AddGeometryColumn('%1$s','the_geom',4326,'POLYGON',2);";
	private static String addPointsColumn = "Select AddGeometryColumn('%1$s','the_geom',4326,'POINT',2);";	
	static String makeSquare = "ST_GeomFromText('POLYGON((%1$s ,%2$s, %3$s, %4$s, %1$s))',4326)";
	static String makePoint = "ST_GeomFromText('POINT(%1$s %2$s)',4326)";
//	static String makePoint = "ST_SetSRID(ST_MakePoint((%1$s,%2$s),4326)";
	
	//changeable parameters for application purposes
	String datastore = "";
	String defaultStyle = "";
	String workspace = "";
	String username = "";
	String purpose = "";
	String credits = "";
	String keyword = "";
	
			@Override
	public String getDescription() {
		return "A transducer algorithm to produce a GIS map from a probability distribution or from a set of points. A maximum of " + maxNPoints + " is allowed";
	}

	@Override
	public abstract void init() throws Exception;

	@Override
	protected void process() throws Exception {
		try {
			status = 0;
			log("Beginning process");
			log("Set scope from outside:"+config.getGcubeScope());
			String scope = config.getGcubeScope();
			if (scope == null)
				scope = ScopeProvider.instance.get();
			log("Using scope:"+scope);
			
			String publicationLevelValue = getInputParameter(publicationLevel);
			log("Publication Level:"+publicationLevelValue);
			boolean isprivate = false;
			if (GeospatialDataPublicationLevel.valueOf(publicationLevelValue)==GeospatialDataPublicationLevel.PRIVATE)
				isprivate=true;
			
			//initialize Gis DB parameters 
			String databaseJdbc = getInputParameter(dburlParameterName);
			String databaseUser = getInputParameter(dbuserParameterName);
			String databasePwd = getInputParameter(dbpasswordParameterName);
			log("GIS Database Parameters to use: " + databaseJdbc + " , " + databaseUser);
			//getting resolution
			String res$ = config.getParam(resolutionParameter);
			double resolution = res$!=null?Double.parseDouble(res$):0;
			//connection to the GIS DB
			log("Connecting to gisDB...");
			AlgorithmConfiguration gisconfig = new AlgorithmConfiguration();
			gisconfig.setParam("DatabaseDriver", "org.postgresql.Driver");
			/*
			gisconfig.setParam("DatabaseURL", "jdbc:postgresql://geoserver-test.d4science-ii.research-infrastructures.eu/timeseriesgisdb");
			gisconfig.setParam("DatabaseUserName", databaseUser);
			gisconfig.setParam("DatabasePassword", databasePwd);
			*/
			gisconfig.setParam("DatabaseURL", databaseJdbc);
			gisconfig.setParam("DatabaseUserName", databaseUser);
			gisconfig.setParam("DatabasePassword", databasePwd);
			gisconfig.setConfigPath(config.getConfigPath());
			gisdbconnection = DatabaseUtils.initDBSession(gisconfig);
			log("Initialized gisDBConnection!");
			status = 10;
			// connect to the SM DB
			smdbconnection = DatabaseUtils.initDBSession(config);
			log("Initialized SMDBConnection!");
			
			//select info to attach
			String infoPar = config.getParam(probabilityParameter);
			if (infoPar == null)
				infoPar = config.getParam(infoParameter);
			// points retrieval
			List<Object> points = null;
			log("Retrieving points..");
			if (config.getParam(xParameter) != null && config.getParam(yParameter) != null) {
				log("..from coordinates");
				// select the points from the SM DB up to a maximum of 190000 points
				String q = "select " + config.getParam(xParameter) + "," + config.getParam(yParameter) + "," +infoPar + " from " + config.getParam(inputTableParameter) + " limit " + maxNPoints;
				points = DatabaseFactory.executeSQLQuery(q, smdbconnection);
			}
			//points from csquares
			else if (config.getParam(csquareParameter)!=null){
				log("..from csquares");
				String queryCsquare = "select " + config.getParam(csquareParameter) + "," + infoPar +" from " + config.getParam(inputTableParameter) + " limit " + maxNPoints;
				List<Object> csquares= DatabaseFactory.executeSQLQuery(queryCsquare, smdbconnection);
				points = new ArrayList<Object>();
				//build points from csquares
				for (Object csquare:csquares){
					Object[] csquareandprob = (Object[]) csquare;
					CSquare c = it.cnr.aquamaps.CSquare.apply(""+csquareandprob[0]);
					if (resolution==0)
						resolution=c.size();
					// x,y
					Iterator<Object> iterator = c.center().valuesIterator();
					String x = ""+iterator.next();
					String y = ""+iterator.next();
					String prob = ""+csquareandprob[1];
					Object[] pair = {x,y,prob};
					points.add(pair);
				}
				log("Points built from csquares!");
			}
			//GIS Table creation	
			
			String gisTableName = "stat" + UUID.randomUUID().toString().replace("-", "");
			log("Creating GIS table "+gisTableName);
			status = 30;
			String createTable$ = String.format(createProbTable, gisTableName);
			String columnNames$ = columnsProbNames;
			if (config.getParam(probabilityParameter)==null){
				createTable$ = String.format(createInfoTable, gisTableName);
				columnNames$=columnsInfoNames;
			}
			log(createTable$);
			//drop previous table
			try {
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(gisTableName), gisdbconnection);
			} catch (Exception e) {
				log("Impossible to drop table:" + e.getLocalizedMessage());
			}
			//table creation
			DatabaseFactory.executeSQLUpdate(createTable$, gisdbconnection);
			
			if (resolution>0)
				DatabaseFactory.executeSQLQuery(String.format(addGeometryColumn, gisTableName), gisdbconnection);
			else
				DatabaseFactory.executeSQLQuery(String.format(addPointsColumn, gisTableName), gisdbconnection);
			log("Fulfilling elements");
			log("Resolution:" + resolution);
			//points fulfilling
			List<String[]> values = new ArrayList<String[]>();
			int i = 0;
			for (Object point : points) {
				Object[] elements = (Object[]) point;
				double x = Double.parseDouble("" + elements[0]);
				double y = Double.parseDouble("" + elements[1]);
				String probS = "" + elements[2];
				double x1 = x - resolution;
				double x2 = x + resolution;
  
				double y1 = (y) - resolution;
				double y2 = (y) + resolution;

				String geom = "";
				
				if (resolution == 0)
					geom = String.format(makePoint, x,y);
				else
					geom = String.format(makeSquare, "" + x1 + " " + y1, x1 + " " + y2, x2 + " " + y2, x2 + " " + y1);
//				System.out.println(square);
				String[] selements = { "" + i, "" + x, "" + y, probS,geom };
				values.add(selements);
				i++;
			}
			status = 50;
			log("Writing chunks");
			// write chunks into the DB
			insertGeoChunksIntoTable(gisTableName, columnNames$, values, 5000, gisdbconnection);
			log("Publishing Table");
			String usernameP = config.getParam("ServiceUserName");
			if (usernameP != null)
				username = usernameP;
		
			String layerName = config.getParam(layerNameParameter);
			PublishResponse response = PolyMapMetadata.publishTable(scope, gisTableName, resolution, username, layerName, defaultStyle, workspace, datastore, purpose, credits, keyword,isprivate);
			status = 80;
			//analyzing response
			if (response.getMetaOperationResult() != OperationState.COMPLETE && response.getDataOperationResult()!= OperationState.COMPLETE) {
				log("Error in generating map - dropping gis table - error on data are "+response.getDataOperationMessages()+" erorre on metadata are "+response.getMetaOperationMessages());
				try {
					DatabaseFactory.executeSQLUpdate(DatabaseUtils.dropTableStatement(gisTableName), gisdbconnection);
					log("gis table dropped");
				} catch (Exception e) {
					log("Impossible to drop table:" + e.getLocalizedMessage());
				}
				throw new Exception("Impossible to publish on GeoNetwork or GeoServer table: " + gisTableName+" (error on data are "+response.getDataOperationMessages()+" erorre on metadata are "+response.getMetaOperationMessages()+" )");
			} else {
				//writing output
				addOutputString("GIS map title", layerName);
				addOutputString("GIS map UUID", "" + response.getPublishedMetadata().getFileIdentifier());
				addOutputString("Associated Geospatial Table", gisTableName);
				addOutputString("Generated by ", username);
				addOutputString("Resolution", "" + resolution);
				addOutputString("Style", "" + defaultStyle);
				addOutputString("Keyword", "" + keyword);
			}
			log("Output:"+outputParameters);
			log("All Done!");
			status = 100;
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		} finally {
			if (smdbconnection != null)
				DatabaseUtils.closeDBConnection(smdbconnection);
			if (gisdbconnection != null)
				DatabaseUtils.closeDBConnection(gisdbconnection);
		}

	}

	public static void insertGeoChunksIntoTable(String table, String columnsNames, List<String[]> values, int chunkSize,SessionFactory dbconnection) throws Exception{
		int valuesize = values.size();
		StringBuffer sb = new StringBuffer();
		int stopIndex =0; 
		for (int i=0;i<valuesize;i++){
			String[] row = values.get(i);
			sb.append("(");
			for (int j=0;j<row.length-1;j++){
				String preprow = row[j].replaceAll("^'", "").replaceAll("'$", "");
				preprow=preprow.replace("'", ""+(char)96);
				sb.append("'"+preprow+"'");
				sb.append(",");
			}
			//append geometry
			sb.append(row[row.length-1]);
			sb.append(")");
			if (stopIndex>0 && stopIndex%chunkSize==0){
				DatabaseFactory.executeSQLUpdate(DatabaseUtils.insertFromBuffer(table, columnsNames, sb), dbconnection);
				stopIndex=chunkSize;
				sb = new StringBuffer();
			}
			else if (i<valuesize-1)
				sb.append(",");
		}
		
		if (stopIndex<valuesize-1){
			if (sb.length()>0){
				try{
					DatabaseFactory.executeSQLUpdate(DatabaseUtils.insertFromBuffer(table, columnsNames, sb), dbconnection);
				}catch(Exception e){
					System.out.println("Query:"+sb);
					throw e;
				}
				
			}
		}
			
	}
	
	@Override
	public void shutdown() {
		log("shutdown invoked!");
	}

	@Override
	protected abstract void setInputParameters();
	

	
}
