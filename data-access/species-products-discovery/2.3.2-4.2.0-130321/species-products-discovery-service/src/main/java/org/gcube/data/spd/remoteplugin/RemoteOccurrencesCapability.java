package org.gcube.data.spd.remoteplugin;

import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.publishStringsIn;

import java.net.URI;
import java.rmi.RemoteException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.Constants;
import org.gcube.data.spd.exception.ServiceException;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.binding.Bindings;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.spd.plugin.fwk.capabilities.OccurrencesCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.stubs.GetOccurrencesByProductKeysRequest;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.thoughtworks.xstream.XStream;

public class RemoteOccurrencesCapability extends OccurrencesCapability {

	private Set<Conditions> props = new HashSet<Conditions>();
	private String parentName;
	private List<String> uris;	
	
	volatile Logger logger = LoggerFactory.getLogger(RemoteOccurrencesCapability.class);
	
	public RemoteOccurrencesCapability(Conditions[] properties,   String parentName, List<String> uris) {
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
		SearchCondition[] props;
		if (properties!=null && properties.length>0){
			props = new SearchCondition[properties.length];
			for (int i = 0 ; i<properties.length; i++)
				props[i] = new SearchCondition(properties[i].getOp().name(),  new XStream().toXML(properties[i].getValue()), properties[i].getType().name() );
		}else props = new SearchCondition[0];

		try{
			String locator = RemotePlugin.getRemoteDispatcher(uris).search(new SearchRequest(this.parentName, props, Constants.OCCURRENCE_RETURN_TYPE, word));
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((OccurrencePoint) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}

		} catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
	}

	@Override
	public void getOccurrencesByProductKeys(
			ClosableWriter<OccurrencePoint> writer, Iterator<String> keys) throws ExternalRepositoryException {
		
		logger.trace("remote getOccurrencesByProductKeys called in "+this.parentName);
		try{					
			String locator = RemotePlugin.getRemoteDispatcher(uris).getOccurrencesByProductKeys(new GetOccurrencesByProductKeysRequest(this.parentName, publishStringsIn(convert(keys)).withDefaults().toString()));
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext()){
				String item = items.next();
				try{
					writer.write((OccurrencePoint) Bindings.fromXml(item));
				}catch (Exception e) {
					logger.error("error binding the item:\n"+item+"\n",e);
				}
			}

		} catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}finally{
			writer.close();
		}
	}

	@Override
	public void getOccurrencesByIds(ClosableWriter<OccurrencePoint> writer,
			Iterator<String> ids) throws ExternalRepositoryException{
			
		logger.trace("remote getOccurrencesByIds called in "+this.parentName);
		try{					
			String locator = RemotePlugin.getRemoteDispatcher(uris).getOccurrencesByProductKeys(new GetOccurrencesByProductKeysRequest(this.parentName, publishStringsIn(convert(ids)).withDefaults().toString()));
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((OccurrencePoint) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}

		} catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}finally{
			writer.close();
		}
	}

}
