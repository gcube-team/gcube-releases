package org.gcube.data.spd.remoteplugin;


import static org.gcube.data.streams.dsl.Streams.convert;
import static org.gcube.data.streams.dsl.Streams.publishStringsIn;
import java.net.URI;
import java.rmi.RemoteException;
import java.util.ArrayList;
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
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.stubs.GetSynonymsByIdRequest;
import org.gcube.data.spd.stubs.GetTaxonByIdRequest;
import org.gcube.data.spd.stubs.IdNotValidFault;
import org.gcube.data.spd.stubs.RetrieveTaxaByIdsRequest;
import org.gcube.data.spd.stubs.RetrieveTaxonChildrenByTaxonIdRequest;
import org.gcube.data.spd.stubs.SearchCondition;
import org.gcube.data.spd.stubs.SearchRequest;
import org.gcube.data.streams.Stream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thoughtworks.xstream.XStream;

public class RemoteClassificationCapability extends ClassificationCapability {

	private Set<Conditions> props = new HashSet<Conditions>();
	volatile Logger logger = LoggerFactory.getLogger(RemoteOccurrencesCapability.class);
	private String parentName;
	private List<String> uris;
	
	public RemoteClassificationCapability(Conditions[] properties, String parentName, List<String> uris){
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
			ObjectWriter<TaxonomyItem> writer, Condition... properties) throws ExternalRepositoryException {
		//trasforming properties
		
		SearchCondition[] props;
		if (properties!=null && properties.length>0){
			props = new SearchCondition[properties.length];
			for (int i = 0 ; i<properties.length; i++)
				props[i] = new SearchCondition(properties[i].getOp().name(),  new XStream().toXML(properties[i].getValue()), properties[i].getType().name() );

		}else props = new SearchCondition[0];

		try{
			String locator = RemotePlugin.getRemoteDispatcher(uris).search(new SearchRequest(this.parentName, props, Constants.TAXON_RETURN_TYPE, word));
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((TaxonomyItem) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error bingi result item",e);
				}

		}catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
	}

	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String taxonId)
			throws IdNotValidException, ExternalRepositoryException {
		List<TaxonomyItem> itemsList = new ArrayList<TaxonomyItem>();
		try{
			String locator = RemotePlugin.getRemoteDispatcher(uris).retrieveTaxonChildrenByTaxonId(new RetrieveTaxonChildrenByTaxonIdRequest(taxonId, this.parentName));		
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					itemsList.add((TaxonomyItem) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}

		}catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
		return itemsList;
	}

	

	@Override
	public TaxonomyItem retrieveTaxonById(String id)
			throws IdNotValidException, ExternalRepositoryException {
		try{
			String item = RemotePlugin.getRemoteDispatcher(uris).getTaxonById(new GetTaxonByIdRequest(id, parentName));
			return (TaxonomyItem) Bindings.fromXml(item);
		} catch (IdNotValidFault e) {
			logger.error("id not valid "+id+" for plugin "+parentName);
			throw new IdNotValidException("id not valid "+id+" for plugin "+parentName);
		}  catch (Exception e) {
			logger.error("error retreiveing taxon for plugin "+parentName);
			throw new ExternalRepositoryException("error retreiveing taxon for plugin "+parentName);
		}
		
	}

	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id)
			throws IdNotValidException, MethodNotSupportedException, ExternalRepositoryException {
		try{
			
			String locator = RemotePlugin.getRemoteDispatcher(uris).getSynonymsById(new GetSynonymsByIdRequest(id, this.parentName));		
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((TaxonomyItem) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}

		}catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}
	}

	@Override
	public void retrieveTaxonByIds(Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) throws ExternalRepositoryException {
		try{
			String inputIdsLocator = publishStringsIn(convert(ids)).withDefaults().toString();
			String locator = RemotePlugin.getRemoteDispatcher(uris).retrieveTaxaByIds(new RetrieveTaxaByIdsRequest(inputIdsLocator, this.parentName));		
			Stream<String> items = convert(URI.create(locator)).ofStrings().withDefaults();  
			while(items.hasNext())
				try{
					writer.write((TaxonomyItem) Bindings.fromXml(items.next()));
				}catch (Exception e) {
					logger.error("error binding",e);
				}

		}catch (ServiceException e) {
			throw new ExternalRepositoryException("error contacting external services",e);
		} catch (RemoteException re){
			throw new ExternalRepositoryException("error contacting remote service",re);
		}finally{
			writer.close();
		}
		
	}
	
}
