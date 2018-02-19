package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.model.service.types.SearchCondition;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemoteOccurrencesCapability extends OccurrencesCapability {

	private Set<Conditions> props = new HashSet<Conditions>();
	private String parentName;
	private Collection<String> uris;	

	volatile Logger logger = LoggerFactory.getLogger(RemoteOccurrencesCapability.class);

	public RemoteOccurrencesCapability(Conditions[] properties,   String parentName, Collection<String> uris) {
		if (properties!=null)
			for (Conditions prop: properties)
				props.add(prop);
		this.parentName = parentName;
		this.uris = uris;
	}

	@Override
	public Set<Conditions> getSupportedProperties() {
		return props;
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<OccurrencePoint> writer, Condition... properties) throws ExternalRepositoryException {
		//trasforming properties
		List<SearchCondition> props = Collections.emptyList();
		if (properties!=null && properties.length>0){
			props = new ArrayList<SearchCondition>(properties.length);
			for (int i = 0 ; i<properties.length; i++)
				props.add(new SearchCondition(properties[i].getType(), properties[i].getOp(),  new XStream().toXML(properties[i].getValue())));
		}

		//TODO : call remote rest service
		String locator = "";// RemotePlugin.getRemoteDispatcher(uris).search(new SearchRequest(this.parentName, props, Constants.OCCURRENCE_RETURN_TYPE, word));
		Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
		while(items.hasNext())
			try{
				writer.write((OccurrencePoint) Bindings.fromXml(items.next()));
			}catch (Exception e) {
				logger.error("error binding",e);
			}


	}

	@Override
	public void getOccurrencesByProductKeys(
			ClosableWriter<OccurrencePoint> writer, Iterator<String> keys) throws ExternalRepositoryException {

		logger.trace("remote getOccurrencesByProductKeys called in "+this.parentName);
		try{
			//TODO : call remote rest service
			String locator = ""; //RemotePlugin.getRemoteDispatcher(uris).getOccurrencesByProductKeys(publishStringsIn(convert(keys)).withDefaults().toString(), this.parentName);
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext()){
				String item = items.next();
				try{
					writer.write((OccurrencePoint) Bindings.fromXml(item));
				}catch (Exception e) {
					logger.error("error binding the item:\n"+item+"\n",e);
				}
			}

		}finally{
			writer.close();
		}
	}

	@Override
	public void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer,
			Iterator<String> ids) throws ExternalRepositoryException{

		logger.trace("remote getOccurrencesByIds called in "+this.parentName);
		try{					
			String locator = ""; //RemotePlugin.getRemoteDispatcher(uris).getOccurrencesByProductKeys(publishStringsIn(convert(ids)).withDefaults().toString(), this.parentName);
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((OccurrencePoint) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}


		}finally{
			writer.close();
		}
	}

}
