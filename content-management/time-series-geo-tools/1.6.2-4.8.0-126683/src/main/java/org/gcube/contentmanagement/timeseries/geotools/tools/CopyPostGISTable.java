package org.gcube.contentmanagement.timeseries.geotools.tools;

import java.util.List;

import org.gcube.contentmanagement.lexicalmatcher.analysis.core.EngineConfiguration;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.timeseries.geotools.databases.ConnectionsManager;
import org.gcube.contentmanagement.timeseries.geotools.engine.TSGeoToolsConfiguration;

public class CopyPostGISTable {

	
	private static String createTable = "CREATE TABLE depthmean(  depth_fid integer,  csquarecode character varying,  the_geom geometry,  \"DepthMean\" real)WITH (  OIDS=FALSE);ALTER TABLE depthmean OWNER TO postgres;";
	private static String dropTable = "drop table depthmean;";
	private static String getAll = "SELECT depth_fid,csquarecode,ST_AsText(the_geom),\"DepthMean\" from depthmean";
	
	private static String insertAll = "INSERT INTO depthmean values %1$s;";
	private static String configPath = "./cfg/";
	
	public static void copyDepthMean() throws Exception{
		TSGeoToolsConfiguration configuration = new TSGeoToolsConfiguration();
		
		//origin
		configuration.setConfigPath("./cfg/");
		configuration.setGeoServerDatabase("jdbc:postgresql://geoserver.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setGeoServerUserName("postgres");
		configuration.setGeoServerPassword("d4science2");
		//destination
		configuration.setAquamapsDatabase("jdbc:postgresql://geoserver-dev.d4science-ii.research-infrastructures.eu/aquamapsdb");
		configuration.setAquamapsUserName("postgres");
		configuration.setAquamapsPassword("d4science2");
		
		AnalysisLogger.setLogger(configPath + "ALog.properties");
		AnalysisLogger.getLogger().debug("LayersIntersection-> initializing connections");

		ConnectionsManager connectionsManager = new ConnectionsManager(configPath);
		
		
		EngineConfiguration geocfg = new EngineConfiguration();
		geocfg.setConfigPath(configPath);
		geocfg.setDatabaseUserName(configuration.getGeoServerUserName());
		geocfg.setDatabasePassword(configuration.getGeoServerPassword());
		geocfg.setDatabaseURL(configuration.getGeoServerDatabase());
		
		EngineConfiguration destGeocfg = new EngineConfiguration();
		destGeocfg.setConfigPath(configPath);
		destGeocfg.setDatabaseUserName(configuration.getAquamapsUserName());
		destGeocfg.setDatabasePassword(configuration.getAquamapsPassword());
		destGeocfg.setDatabaseURL(configuration.getAquamapsDatabase());
		
		connectionsManager.initGeoserverConnection(geocfg);
		connectionsManager.initAquamapsConnection(destGeocfg);
		
		//create table
		AnalysisLogger.getLogger().warn("dropping ... ");
		try{
		connectionsManager.AquamapsUpdate(dropTable);
		}catch(Exception e){AnalysisLogger.getLogger().warn("..not dropped");}
		AnalysisLogger.getLogger().warn("creating ... ");
		connectionsManager.AquamapsUpdate(createTable);
		long t1 = System.currentTimeMillis();
		AnalysisLogger.getLogger().warn("collecting ... "+getAll);
		//select elements from origin
		List<Object> elements = connectionsManager.GeoserverQuery(getAll);
		long t2 = System.currentTimeMillis();
		AnalysisLogger.getLogger().warn("collected in  ... "+(t2-t1));
		int m = elements.size();
		StringBuffer sb = new StringBuffer();
		for (int i=0;i<m;i++){
			Object[] row = (Object[]) elements.get(i);
			sb.append("(");
			int n = row.length;
			for (int j=0;j<n;j++){
				if (j!=n-2)
					sb.append("'"+row[j]+"'");
				else 
					sb.append("ST_SetSRID('"+row[j]+"',4326)");

				if (j<n-1)
					sb.append(",");
			}
			sb.append(")");
			
			AnalysisLogger.getLogger().warn("analized : "+(i+1) +" of "+m);
			
			if (i%100==0){
				String insertionQuery = String.format(insertAll, sb.toString());
				AnalysisLogger.getLogger().warn("inserting ... "+ insertionQuery);
				connectionsManager.AquamapsUpdate(insertionQuery);
				sb = null;
				sb = new StringBuffer();
			}
			
			else if (i<m-1)
					sb.append(",");
			
		}
		
		String insertionQuery = String.format(insertAll, sb.toString());
		AnalysisLogger.getLogger().warn("inserting ... "+ insertionQuery);
		
		connectionsManager.AquamapsUpdate(insertionQuery);
		
		AnalysisLogger.getLogger().warn("...done!");
	}

	
	public static void main(String[] args) throws Exception{
			
			CopyPostGISTable cpt = new CopyPostGISTable();
			cpt.copyDepthMean();
	}
}
