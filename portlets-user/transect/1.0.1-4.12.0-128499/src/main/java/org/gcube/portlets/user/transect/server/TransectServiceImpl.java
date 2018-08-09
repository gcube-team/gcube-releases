package org.gcube.portlets.user.transect.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import org.apache.commons.io.FileUtils;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.contentmanagement.lexicalmatcher.analysis.core.LexicalEngineConfiguration;
import org.gcube.portlets.user.timeseries.charts.support.types.GraphGroups;
import org.gcube.portlets.user.transect.client.TransectService;
import org.gcube.portlets.user.transect.server.readers.RuntimeResourceReader;
import org.gcube.portlets.user.transect.server.readers.entity.RuntimeProperty;
import org.gcube.portlets.user.transect.server.readers.entity.ServiceAccessPoint;
import org.gcube.portlets.user.transectgenerator.core.AquamapsProcessor;
import org.slf4j.Logger;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;


/**
 * The Class TransectServiceImpl.
 * 
 * @author Daniele Strollo (ISTI-CNR)
 * @author updated by Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 28, 2015
 */
@SuppressWarnings("serial")
public class TransectServiceImpl extends RemoteServiceServlet implements
		TransectService {

	private static final long serialVersionUID = 2417983362076766751L;
	
	private static final int GROUP_NUM = 1;

	public static final Logger logger = org.slf4j.LoggerFactory.getLogger(TransectServiceImpl.class);
	/**
	 * Runtime Reosource: Geoserver DB
	 */
	public static final String TransectGeoDatabase = "TransectGeoDatabase";
	/**
	 * Runtime Reosource: Aquamaps DB
	 */
	public static final String TransectAuxiliaryDatabase = "TransectAuxiliaryDatabase";
	
	private static boolean isInitialized = false;

	/**
	 * Runtime Resource Property
	 */
	public static final String DIALECT = "dialect";
	
	/**
	 * Runtime Resource Property
	 */
	public static final String DRIVER = "driver";

	/* (non-Javadoc)
	 * @see javax.servlet.http.HttpServlet#service(javax.servlet.ServletRequest, javax.servlet.ServletResponse)
	 */
	@Override
	public void service(ServletRequest arg0, ServletResponse arg1) throws ServletException, IOException {
		super.service(arg0, arg1);
	}

	/**
	 * Fetch string from file.
	 *
	 * @param propertyName the property name
	 * @param propFile the prop file
	 * @return the string
	 */
	private static String fetchStringFromFile(final String propertyName, final Properties propFile) {
		return propFile.get(propertyName).toString();
	}

	/**
	 * Fetch array from prop file.
	 *
	 * @param propertyName the property name
	 * @param propFile the prop file
	 * @return the string[]
	 */
	private static String[] fetchArrayFromPropFile(final String propertyName, final Properties propFile) {
		return propFile.getProperty(propertyName).split(",");
	}

	/**
	 * Populate data.
	 * @param gcube scope
	 * @param groupNum the group num
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param SRID the srid
	 * @param maxelements the maxelements
	 * @param minumumGap the minumum gap
	 * @param biodiversityfield the biodiversityfield
	 * @param tablename the tablename
	 * @return the graph groups
	 * @throws Exception the exception
	 */
	public final GraphGroups populateData(String scope, final int groupNum, String x1,
			String y1, String x2, String y2, String SRID, String maxelements,
			String minumumGap, String biodiversityfield, String tablename)
			throws Exception {
		logger.info("PopulateData start...");
		Properties props = new Properties();
		String cfgFolder = "cfg";
		String tablePropertiesPath =  cfgFolder + File.separator +"table.properties";
		
		String realPath = this.getServletContext().getRealPath("/");
		logger.info("Trying to create full path, this.getServletContext().getRealPath(): "+realPath);
		
		InputStream resourceAsStream = null;
		if(realPath!=null){
			tablePropertiesPath = realPath+File.separator+tablePropertiesPath;
			File file = new File(tablePropertiesPath);
			resourceAsStream = FileUtils.openInputStream(file);
		}else{
			ServletContext context = this.getServletContext();
			resourceAsStream = context.getResourceAsStream(tablePropertiesPath);
		}
		
		logger.info("Trying to open inputstream for path: "+tablePropertiesPath);
		props.load(resourceAsStream);

		String _x1 = (x1 != null ? x1 : fetchStringFromFile("x1", props));
		String _y1 = (y1 != null ? y1 : fetchStringFromFile("y1", props));
		String _x2 = (x2 != null ? x2 : fetchStringFromFile("x2", props));
		String _y2 = (y2 != null ? y2 : fetchStringFromFile("y2", props));
		String _SRID = (SRID != null ? SRID
				: fetchStringFromFile("SRID", props));
		int _maxelements = (maxelements != null ? Integer.parseInt(maxelements)
				: Integer.parseInt(fetchStringFromFile("maxelements", props)));
		int _minumumGap = (minumumGap != null ? Integer.parseInt(minumumGap)
				: Integer.parseInt(fetchStringFromFile("minumumGap", props)));
		String _biodiversityfield = (biodiversityfield != null ? biodiversityfield
				: fetchStringFromFile("biodiversityfield", props));
		String _tablename = (tablename != null ? tablename
				: fetchStringFromFile("tablename", props));
		
		AquamapsProcessor STG = null;
		try{
			LexicalEngineConfiguration aquamapsconfig = getDatabaseConfig(scope, TransectAuxiliaryDatabase);
			LexicalEngineConfiguration geoserverconfig = getDatabaseConfig(scope, TransectGeoDatabase);
			
			STG = new AquamapsProcessor();
			
			String cfg = realPath!=null?realPath+File.separator+cfgFolder+File.separator:cfgFolder+File.separator;
			
//			String cfg = this.getServletContext().getRealPath("")+ File.separator + "cfg/";
			
			logger.info("AquamapsProcessor init connections..., cfg path: "+cfg);
			STG.init(cfg, aquamapsconfig, geoserverconfig);
			
			logger.info("PopulateData calculating transect...");
			return STG.calculateTransect(_x1, _y1, _x2, _y2, _SRID, _tablename,_biodiversityfield, _maxelements, _minumumGap);
		}catch (Exception e) {
			logger.error("Exception: ",e);
			throw new Exception("Sorry an error occurred with connection to the Transect DB, try again later");
		}finally{
			if(STG!=null){
				STG.shutdown();
				logger.info("AquamapsProcessor shutdown connections!");
			}
		}

	}
	
	public LexicalEngineConfiguration getDatabaseConfig(String scope, String runtimeResourceName) throws Exception{
		logger.info("Trying read runtime name: "+runtimeResourceName +", in scope: "+scope);
		
		RuntimeResourceReader rr = new RuntimeResourceReader(scope, runtimeResourceName);
		ServiceAccessPoint accessPoint = rr.getServiceAccessPoints().get(0);
		
		if(accessPoint==null)
			throw new Exception("Access Point not found for "+runtimeResourceName +" in scope: "+scope);
		
		logger.info("Access Point found for "+runtimeResourceName +" in scope: "+scope);
		
		//READ DIALECT
		RuntimeProperty rdialect = accessPoint.getRuntimeProperties().get(DIALECT);
		
		if(rdialect==null || rdialect.getValue()==null || rdialect.getValue().isEmpty())
			throw new Exception("property "+DIALECT+" not found or is empty");
		
		//READ DRIVER
		RuntimeProperty rdriver = accessPoint.getRuntimeProperties().get(DRIVER);
		
		if(rdriver==null || rdriver.getValue()==null || rdriver.getValue().isEmpty())
			throw new Exception("property "+DRIVER+" not found or is empty");
		
		logger.info("Returning "+runtimeResourceName+" configs: "
				+ "\nusername: "+accessPoint.getUsername()+""
				+ "\npwd: "+accessPoint.getPwd()+""
				+ "\nservice URL: "+accessPoint.getServiceUrl()+""
				+ "\n"+DIALECT+": "+rdialect.getValue()+""
				+ "\n"+DRIVER+": "+rdriver.getValue());
		
		LexicalEngineConfiguration lecongifs = new LexicalEngineConfiguration();
		lecongifs.setDatabaseUserName(accessPoint.getUsername());
		lecongifs.setDatabasePassword(accessPoint.getPwd());
		lecongifs.setDatabaseDialect(rdialect.getValue());
		lecongifs.setDatabaseDriver(rdriver.getValue());
		lecongifs.setDatabaseURL(accessPoint.getServiceUrl());
		
		return lecongifs;
	}

	/**
	 * *************************************************
	 * WORKING PART
	 * *************************************************.
	 * @param scope the gcube scope from whone to read the runtime resource @TransectGeoDatabase @TransectAuxiliaryDatabase
	 * @param x1 the x1
	 * @param y1 the y1
	 * @param x2 the x2
	 * @param y2 the y2
	 * @param SRID the srid
	 * @param maxelements the maxelements
	 * @param minumumGap the minumum gap
	 * @param biodiversityfield the biodiversityfield
	 * @param tablename the tablename
	 * @return the chart data
	 */
	@Override
	public final GraphGroups getChartData(String scope, String x1, String y1, String x2,
			String y2, String SRID, String maxelements, String minumumGap,
			String biodiversityfield, String tablename) throws Exception{
		try {

			String scopeProvider = ScopeProvider.instance.get();
			logger.info("Scope provider: "+scopeProvider);
			logger.info("Parameter scope: "+scope);
			
			String scopeToUse = (scopeProvider==null || scopeProvider.isEmpty())?scope:scopeProvider;
			
			if(scopeToUse==null || scopeToUse.isEmpty())
				throw new ScopeNotFound("Parameter 'scope' not found, set 'scope=value' in query string");
			
			logger.info("Using scope to read RR: "+scopeToUse);
			return populateData(scopeToUse, 1, x1, y1, x2, y2, SRID, maxelements,
					minumumGap, biodiversityfield, tablename);
		} catch (ScopeNotFound e){
			logger.warn("Scope not found: "+e.getMessage());
			throw new Exception(e.getMessage());
		} catch (Exception e) {
			logger.error("Exception on getChartData: ",e);
			throw new Exception(e.getMessage());
		}
	}
	
}
