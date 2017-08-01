package org.gcube.data.spd.gbifplugin.capabilities;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.gcube.data.spd.gbifplugin.Constants;
import org.gcube.data.spd.gbifplugin.search.OccurrenceSearch;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.exceptions.StreamNonBlockingException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrencesCapabilityImpl extends OccurrencesCapability{

	
	private static Logger logger = LoggerFactory.getLogger(OccurrencesCapabilityImpl.class);
	
	private String baseUrl;
	
	
	
	public OccurrencesCapabilityImpl(String baseUrl) {
		super();
		this.baseUrl = baseUrl;
	}



	@SuppressWarnings("serial")
	@Override
	public Set<Conditions> getSupportedProperties() {
		return new HashSet<Conditions>(){{
			add(Conditions.DATE);
			add(Conditions.COORDINATE);
			
		}};
	}

	
	
	@Override
	public void searchByScientificName(String word,
			ObjectWriter<OccurrencePoint> writer, Condition... properties) {
		try{
			new OccurrenceSearch(baseUrl).search(writer, word, Constants.QUERY_LIMIT, properties);
		} catch (Exception e) {
			logger.debug("search occurrences by ScientificName failed",e);
		}
	}

	@Override
	public void getOccurrencesByProductKeys(
			ClosableWriter<OccurrencePoint> writer, Iterator<String> keys) {
		OccurrenceSearch occSearch = null;
		try{
			occSearch = new OccurrenceSearch(baseUrl);
		}catch (Exception e) {
			logger.error("error contacting gbif server");
			writer.write(new StreamBlockingException(Constants.REPOSITORY_NAME));
			return;
		}
		while (keys.hasNext()){
			String key = keys.next();
			try{
				occSearch.searchByKey(writer, key, Constants.QUERY_LIMIT);
			}catch (Exception e) {
				logger.warn("error retrieving key "+key, e);
				writer.write(new StreamNonBlockingException(Constants.REPOSITORY_NAME,key));
			}
		}
		writer.close();
	}



	@Override
	public void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer,
			Iterator<String> ids) {
		OccurrenceSearch occSearch = null;
		try{
			occSearch = new OccurrenceSearch(baseUrl);
		}catch (Exception e) {
			logger.error("error contacting gbif server");
			writer.write(new StreamBlockingException(Constants.REPOSITORY_NAME));
			return;
		}
		while (ids.hasNext()){
			String id = ids.next();
			try{
				if (!writer.isAlive()){
					logger.trace("the writer is closed");
					return;
				}else writer.write(occSearch.searchById(id));
			}catch (Exception e) {
				logger.warn("error retrieving id "+id,e);
				writer.write(new StreamNonBlockingException(Constants.REPOSITORY_NAME,id));
			}
		}
		writer.close();
	}

	
}
