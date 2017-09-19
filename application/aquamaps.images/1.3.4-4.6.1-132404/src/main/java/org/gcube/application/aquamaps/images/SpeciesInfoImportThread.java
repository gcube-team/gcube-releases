package org.gcube.application.aquamaps.images;

import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.dataManagement;
import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.maps;
import static org.gcube.application.aquamaps.aquamapsservice.client.plugins.AbstractPlugin.publisher;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.nio.charset.Charset;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;

import net.sf.csv4j.CSVReaderProcessor;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.DataManagement;
import org.gcube.application.aquamaps.aquamapsservice.client.proxies.Publisher;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.BulkItem;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.enhanced.ItemResources;
import org.gcube.application.aquamaps.aquamapsservice.stubs.datamodel.xstream.AquaMapsXStream;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.model.Field;
import org.gcube.application.aquamaps.aquamapsservice.stubs.fw.types.ResourceType;
import org.gcube.application.aquamaps.images.model.MapItem;
import org.gcube.application.aquamaps.images.model.SpeciesInfo;
import org.gcube.common.scope.api.ScopeProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;

public class SpeciesInfoImportThread extends Thread {

	private static final SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
	
	private static SpeciesInfoImportThread instance;
	
	public static synchronized SpeciesInfoImportThread start(Properties prop){
		if(instance==null) instance= new SpeciesInfoImportThread(prop);
		return instance;		
	}
	
	
	private static final Logger logger = LoggerFactory.getLogger(SpeciesInfoImportThread.class);


	private Properties props;


	private SpeciesInfoImportThread(Properties props) {
		this.props=props;
		this.start();
	}


	@Override
	public void run() {
		while(true){
			Hashtable<String,String> configuration=(Hashtable<String, String>) props.clone();
			List<File> toDelete=new ArrayList<>();
			try{
				logger.debug("Using configuration : "+configuration);
				String scope=configuration.get(Common.SCOPE_PROP);
				ScopeProvider.instance.set(scope);
				
				logger.trace("Fetching info in scope "+scope);
				org.gcube.application.aquamaps.aquamapsservice.client.proxies.Maps maps=maps().build();
				Publisher pub=publisher().build();
				DataManagement dmService=dataManagement().build();
				
				int hspenID=0;
				for(Field f:dmService.getDefaultSources()){
					if(f.name().equals(ResourceType.HSPEN+"")) {
						hspenID=f.getValueAsInteger();
						logger.debug("Using hspen id "+hspenID);
						break;
					}
				}
				
				File toRead=maps.getCSVSpecies(hspenID, null, null,"IMAGES WEBAPP");
				toDelete.add(toRead);
				CSVReaderProcessor processor=new CSVReaderProcessor();

				processor.setDelimiter(',');
				processor.setHasHeader(true);

				SpeciesCSVLineProcessor lineProcessor=new SpeciesCSVLineProcessor();

				Reader reader= new InputStreamReader(new FileInputStream(toRead), Charset.defaultCharset());

				processor.processStream(reader , lineProcessor);
				
				long lastUpdate=Common.get().getLastCompletedUpdate();
				long performedUpdateTime=System.currentTimeMillis();
				logger.trace("Fetched "+lineProcessor.getCount()+" species, getting maps generated after "+format.format(new Date(lastUpdate)));
				
				
				
				File bulkMaps=pub.getBulkUpdates(true, false, null, lastUpdate);
				toDelete.add(bulkMaps);
				AquaMapsXStream stream=AquaMapsXStream.getXMLInstance();
				ObjectInputStream ois=stream.createObjectInputStream(new FileInputStream(bulkMaps));
				Dao<SpeciesInfo,String> speciesDao=Common.get().getSpeciesDao();
				Dao<MapItem,Integer> mapDao=Common.get().getMapDao();
				long bulkCount=0;
				long updateCount=0;
				long insertCount=0;
				try{
				while(true){
					try{
						BulkItem item=(BulkItem) ois.readObject();
						bulkCount++;
						String scientificName=speciesDao.queryForId(item.getSpeciesId()).getScientificName();
						for(Entry<Integer,ItemResources> entry:item.getResources().entrySet()){
							ItemResources res=entry.getValue();
							MapItem mapItem=new MapItem(entry.getKey(), item.getSpeciesId(), scientificName, res.getStandardImgUri(), res.getStandardLayerId());
							boolean inserted=Common.get().insertOrUpdateMapInfoByCoverage(mapItem);
							if(inserted)insertCount++;
							else updateCount++;
						}
					}catch(EOFException e){
						throw e;
					}catch(Exception e){
						logger.warn("Unable to load map info",e);
					}
				}
				}catch(EOFException e){
					// end of object stream
				}finally{
					IOUtils.closeQuietly(ois);
				}
				logger.debug("Parsed "+bulkCount+" bulkItems, inserted "+insertCount+" map entries, updated "+updateCount);
				logger.debug("Current map count : "+mapDao.countOf());
				Common.get().setLastConfiguration(configuration);
				Common.get().setLastCompletedUpdate(performedUpdateTime);
			}catch(Throwable e){				
				logger.error("Unexpected Error ", e);
			}finally{
				for(File f:toDelete)FileUtils.deleteQuietly(f);
				try{
					long minutes=Long.parseLong(configuration.get(Common.FETCH_ROUTINE_INTERVAL_MINUTES));
					logger.trace("Going to execute again in "+minutes+" min");
					sleep(minutes*60*1000);
				}catch(InterruptedException e){

				}catch(Throwable t){
					logger.error("Unexpected exception ",t);
				}
			}
		}
	}

	
	
	
}
