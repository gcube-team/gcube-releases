package org.gcube.application.aquamaps.images;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.gcube.application.aquamaps.images.exceptions.ImageNotFoundException;
import org.gcube.application.aquamaps.images.model.MapItem;
import org.gcube.application.aquamaps.images.model.ProductType;
import org.gcube.application.aquamaps.images.model.SpeciesInfo;
import org.gcube.application.aquamaps.images.model.Statistics;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.spatial.data.geonetwork.GeoNetwork;
import org.gcube.spatial.data.geonetwork.GeoNetworkReader;
import org.gcube.spatial.data.geonetwork.LoginLevel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.jdbc.JdbcPooledConnectionSource;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.table.TableUtils;

public class Common {



	

	public static final String IMAGE_JPEG="image/jpeg";

	private static final String IMAGE_BASE_URL="http://www.fishbase.org/images/thumbnails/jpg/";

	private static final Logger logger = LoggerFactory.getLogger(Common.class);

	private static final String configFile="config.properties";

	public static final String SCOPE_PROP="SCOPE";
	public static final String SUITABLE_PROP="SUITABLE_ID";
	public static final String SUITABLE_2050_PROP="SUITABLE_2050_ID";
	public static final String NATIVE_PROP="NATIVE_ID";
	public static final String NATIVE_2050_PROP="NATIVE_2050_ID";
	public static final String HELP_FILE="HELP_FILE";
	public static final String IMAGE_NOT_FOUND_FILE="IMAGE_NOT_FOUND_FILE";

	public static final String FETCH_ROUTINE_INTERVAL_MINUTES="FETCH_ROUTINE_INTERVAL_MINUTES";

	private static Common instance;

	public synchronized static Common get(){
		if(instance==null)instance=new Common();
		return instance;
	}



	private Properties props=new Properties();

	private Statistics statistics=new Statistics();

	private Dao<MapItem,Integer> mapDao;

	private Dao<SpeciesInfo,String> speciesDao;
	
	private Long lastCompletedUpdate=0l;

	private Map<String,String> lastConfiguration=null;

	
	private GeoNetworkReader gnReader=null;
	
	private Common() {
		try {
			Thread t=new Thread(){
				@Override
				public void run() {
					while(true)
						try {
							props.load(Common.class.getResourceAsStream(configFile));
							Thread.sleep(5*60*1000);
						} catch (IOException e) {
							logger.error("Unable to read configuration",e);
						}catch (InterruptedException e) {
						} 
				}
			};
			t.start();
			JdbcConnectionSource connectionSource=new JdbcPooledConnectionSource("jdbc:h2:mem:occurrence;DB_CLOSE_DELAY=-1");
			mapDao=DaoManager.createDao(connectionSource, MapItem.class);
			speciesDao=DaoManager.createDao(connectionSource, SpeciesInfo.class);
			TableUtils.createTableIfNotExists(connectionSource, SpeciesInfo.class);
			TableUtils.createTableIfNotExists(connectionSource, MapItem.class);			
			SpeciesInfoImportThread.start(props);			
		} catch (SQLException e) {
			logger.error("Unable to create db",e);
		} catch (Exception e){
			logger.error("Unexpected error",e);
		}
	}




	public org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps getAMInterface() throws Exception{
		ScopeProvider.instance.set(props.getProperty(SCOPE_PROP));
		return maps().build();
	}

	private synchronized GeoNetworkReader getGeoNetworkReader()throws Exception{
		if(gnReader==null){
			ScopeProvider.instance.set(props.getProperty(SCOPE_PROP));
			gnReader=GeoNetwork.get();
			gnReader.login(LoginLevel.DEFAULT);
		}
		return gnReader;
	}
	
	
	public MapItem getMap(Integer hspecId,String scientificName)throws SQLException, ImageNotFoundException{
		QueryBuilder<MapItem, Integer> queryBuilder=mapDao.queryBuilder();
		List<MapItem> found=mapDao.query(queryBuilder.where().eq(MapItem.HSPEC_ID, hspecId).and().eq(MapItem.SCIENTIFIC_NAME, scientificName).prepare());
		if(found.size()==0) throw new ImageNotFoundException();
		else return found.get(0);
	}
	
	public List<MapItem> getMapsBySpecies(String scientificName) throws SQLException{
		QueryBuilder<MapItem, Integer> queryBuilder=mapDao.queryBuilder();
		return mapDao.query(queryBuilder.where().eq(MapItem.SCIENTIFIC_NAME, scientificName).prepare());	
	}
	
	public SpeciesInfo getSpeciesInfo(String scientificName) throws SQLException{
		QueryBuilder<SpeciesInfo,String> queryBuilder=speciesDao.queryBuilder();
		queryBuilder.where().eq(SpeciesInfo.SCIENTIFIC_NAME, scientificName);
		return speciesDao.queryForFirst(queryBuilder.prepare());
	}
	
	
		
	public InputStream getProduct(ProductType prod,Integer hspecId,String scientificName)throws SQLException, ImageNotFoundException, MalformedURLException, IOException{
		MapItem item=getMap(hspecId, scientificName);
		switch(prod){
			case IMAGE : 
				logger.debug("Url is "+item.getStaticImageUri());
				return new URL(item.getStaticImageUri()).openStream();
			case PIC : return getSpeciesPicture(scientificName);
			case GIS : 
				String metaId=item.getGeoId();
				logger.debug("GIS ID IS "+metaId);
				if (metaId==null)throw new ImageNotFoundException();
				try{
					String rawMeta=getGeoNetworkReader().getByIdAsRawString(metaId);
					return new ByteArrayInputStream(rawMeta.getBytes("UTF-8"));
				}catch(Exception e){
					e.printStackTrace();
					logger.debug("Unable to read meta with ID "+metaId);
					throw new ImageNotFoundException();
				}
			case OBJ : throw new ImageNotFoundException();
			default : throw new ImageNotFoundException();
		}
	}
	
	

//	public InputStream getSuitableMap(String scientificName) throws SQLException, ImageNotFoundException, MalformedURLException, IOException{
//		if(!dao.idExists(scientificName))throw new ImageNotFoundException();
//		SpeciesInfo info=dao.queryForId(scientificName);
//		if(info==null||info.getSuitableURI()==null)	throw new ImageNotFoundException();
//		return new URL(info.getSuitableURI()).openStream();
//	}
//
//	public InputStream getSuitable2050Map(String scientificName)throws SQLException, ImageNotFoundException, MalformedURLException, IOException{
//		if(!dao.idExists(scientificName))throw new ImageNotFoundException();
//		SpeciesInfo info=dao.queryForId(scientificName);
//		if(info==null||info.getSuitable2050URI()==null)	throw new ImageNotFoundException();
//		return new URL(info.getSuitable2050URI()).openStream();
//	}
//
//	public InputStream getNativeMap(String scientificName)throws SQLException, ImageNotFoundException, MalformedURLException, IOException{
//		if(!dao.idExists(scientificName))throw new ImageNotFoundException();
//		SpeciesInfo info=dao.queryForId(scientificName);
//		if(info==null||info.getNativeURI()==null)	throw new ImageNotFoundException();
//		return new URL(info.getNativeURI()).openStream();
//	}
//
//	public InputStream getNative2050Map(String scientificName)throws SQLException, ImageNotFoundException, MalformedURLException, IOException{
//		if(!dao.idExists(scientificName))throw new ImageNotFoundException();
//		SpeciesInfo info=dao.queryForId(scientificName);
//		if(info==null||info.getNative2050URI()==null)	throw new ImageNotFoundException();
//		return new URL(info.getNative2050URI()).openStream();
//	}
//
//
	public InputStream getSpeciesPicture(String scientificName)throws SQLException, ImageNotFoundException, MalformedURLException, IOException{		
		return new URL(IMAGE_BASE_URL+"tn_"+getSpeciesInfo(scientificName).getPic()).openStream();
	}

	
	public Statistics getStatistics() {	 
		return statistics;
	}

	
	
	public long getCountByHspecId(Integer hspecId) throws SQLException{
		QueryBuilder<MapItem,Integer> builder=mapDao.queryBuilder();
		return mapDao.countOf(builder.setCountOf(true).where().eq(MapItem.HSPEC_ID, hspecId).prepare());
	}
	
	public long getSpeciesCount() throws SQLException{		
		return speciesDao.countOf();
	}
	
	public long getSpeciesPicCount() throws SQLException{
		QueryBuilder<SpeciesInfo,String> builder=speciesDao.queryBuilder();
		return speciesDao.countOf(builder.setCountOf(true).distinct().selectColumns(SpeciesInfo.SPECIES_PIC).where().isNotNull(SpeciesInfo.SPECIES_PIC).prepare());		
	}
	
	public InputStream getImageNotFound(){
		return Common.class.getResourceAsStream(props.getProperty(IMAGE_NOT_FOUND_FILE));
	}

	public InputStream getHelpStream(){
		return Common.class.getResourceAsStream(props.getProperty(HELP_FILE));
	}

	public Dao<MapItem, Integer> getMapDao() {
		return mapDao;
	}

	public Dao<SpeciesInfo, String> getSpeciesDao() {
		return speciesDao;
	}
	
	public String getProperty(String property){
		return props.getProperty(property);
	}

	public Long getLastCompletedUpdate() {
		return lastCompletedUpdate;
	}

	
	public void setLastCompletedUpdate(Long lastCompletedUpdate) {
		this.lastCompletedUpdate = lastCompletedUpdate;
	}
	
	public void setLastConfiguration(Map<String, String> lastConfiguration) {
		logger.debug("Settting last usedConfiguration "+lastConfiguration);
		this.lastConfiguration = lastConfiguration;
		this.lastCompletedUpdate=System.currentTimeMillis();
		try{
			long speciesCount=getSpeciesCount();
			long pictureCount=getSpeciesPicCount();			
			long nativeCount=getCountByHspecId(Integer.parseInt(lastConfiguration.get(NATIVE_PROP)));
			long native2050Count=getCountByHspecId(Integer.parseInt(lastConfiguration.get(NATIVE_2050_PROP)));
			long suitableCount=getCountByHspecId(Integer.parseInt(lastConfiguration.get(SUITABLE_PROP)));
			long suitable2050Count=getCountByHspecId(Integer.parseInt(lastConfiguration.get(SUITABLE_2050_PROP)));
			this.statistics=new Statistics(speciesCount, pictureCount, nativeCount, native2050Count, suitableCount, suitable2050Count, lastCompletedUpdate);
		}catch(SQLException e){
			logger.warn("Unable to evaluate statistics", e);
		}
	}
	public synchronized Map<String, String> getLastConfiguration() {
		if(lastConfiguration==null||lastConfiguration.isEmpty()){
			lastConfiguration=new HashMap<String, String>();
			lastConfiguration.put(NATIVE_PROP, getProperty(NATIVE_PROP));
			lastConfiguration.put(NATIVE_2050_PROP, getProperty(NATIVE_2050_PROP));
			lastConfiguration.put(SUITABLE_2050_PROP, getProperty(SUITABLE_2050_PROP));
			lastConfiguration.put(SUITABLE_PROP, getProperty(SUITABLE_PROP));
			lastConfiguration.put(FETCH_ROUTINE_INTERVAL_MINUTES, getProperty(FETCH_ROUTINE_INTERVAL_MINUTES));
			lastConfiguration.put(SCOPE_PROP, getProperty(SCOPE_PROP));
			logger.debug("Last configuration is empty, using current... "+lastConfiguration);
		}
		return lastConfiguration;
	}
	
	/**
	 * 
	 * 
	 * @param item
	 * @return false if update
	 * @throws SQLException
	 */
	public synchronized boolean insertOrUpdateMapInfoByCoverage(MapItem item) throws SQLException{
		try{
			MapItem found=getMap(item.getHspecId(), item.getScientificName());
			found.setSpeciesId(item.getSpeciesId());
			if(item.getStaticImageUri()!=null)found.setStaticImageUri(item.getStaticImageUri());
			if(item.getGeoId()!=null)found.setGeoId(item.getGeoId());
			mapDao.update(found);
			return false;
		}catch(ImageNotFoundException e){
			// need to update
			mapDao.create(item);
			return true;
		}
	}
}
