package org.gcube.data.speciesplugin.utils;

import static org.gcube.data.spd.client.plugins.AbstractPlugin.classification;
import static org.gcube.data.spd.client.plugins.AbstractPlugin.manager;
import static org.gcube.data.streams.dsl.Streams.pipe;

import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.xml.namespace.QName;

import org.gcube.common.core.scope.GCUBEScopeManager;
import org.gcube.data.spd.client.proxies.Classification;
import org.gcube.data.spd.client.proxies.Manager;
import org.gcube.data.spd.model.products.ResultElement;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.speciesplugin.store.SpeciesStore;
import org.gcube.data.streams.Stream;
import org.gcube.data.trees.data.Tree;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 * @author "Valentina Marioli valentina.marioli@isti.cnr.it"
 */
public class SpeciesService {

	private static Logger logger = LoggerFactory.getLogger(SpeciesService.class);

	public static final QName SPECIES_SERVICE_ID = new QName("SpeciesServiceId");
	protected static final SpeciesTreeGenerator GENERATOR = new SpeciesTreeGenerator();

	protected Manager call;
	protected Classification classificationCall;
	protected SpeciesStore store;
	protected int treeCounter = 0;

	public SpeciesService(SpeciesStore store) throws Exception
	{
		this.store = store;
		logger.trace("getting SPD calls in "+GCUBEScopeManager.DEFAULT.getScope());
		this.call = manager().withTimeout(5, TimeUnit.MINUTES).build();
		this.classificationCall = classification().withTimeout(5, TimeUnit.MINUTES).build();
	}

	/**
	 * Entry point
	 */
	public void createCollection(List<String> scientificNames, List<String> dataSources) throws Exception {
		createCollection(scientificNames, dataSources, true);
	}

	/**
	 * Create collection with trees
	 */
	public void createCollection(List<String> scientificNames, List<String> dataSources, boolean strictMatch) throws Exception {

		logger.trace("retrieving trees with scientificNames: "+scientificNames+" dataSources: "+dataSources+" strictMatch: "+strictMatch+" scope: "+GCUBEScopeManager.DEFAULT.getScope());

		if (scientificNames.size()==0) throw new IllegalArgumentException("No scientific name specified");

		treeCounter = 0;

		String query = createQuery(scientificNames, dataSources);
		logger.trace("SPD query: "+query);

		Stream<ResultElement> stream = call.search(query.toString());

		logger.trace("Filtering retrieved scientific names (strictMatch: "+strictMatch+")");
		int skipped = 0;
		int accepted = 0;

		while(stream.hasNext()){
			TaxonomyItem taxon = (TaxonomyItem) stream.next();
			if (!strictMatch || checkEquals(scientificNames, taxon.getScientificName())){		
				logger.trace("Accepted " + taxon.getId()+" "+taxon.getScientificName());
				accepted++;
				store(taxon);
			} else skipped++;
		}

		logger.trace("Species trees retrieving complete with {} trees", treeCounter);
		logger.trace("Scientific Names {} accepted, {} skipped", accepted, skipped);
		logger.trace("Store size {}", store.cardinality());
	}


	//	/**
	//	 * Creates a Species Discovery query using SPQL 1.0
	//	 */
	//	protected String createQuery(List<String> scientificNames, List<String> dataSource) {
	//
	//		if (scientificNames.size()==0) throw new IllegalArgumentException("No scientific name specified");
	//
	//		//create query
	//		StringBuilder query = new StringBuilder();
	//
	//		Iterator<String> itScNames = scientificNames.iterator();
	//
	//		while(itScNames.hasNext()){
	//			query.append("'");
	//			query.append(itScNames.next());
	//			query.append("'");
	//			if (itScNames.hasNext()) query.append(", ");
	//		}
	//		query.append(" as ScientificName");
	//
	//		if (dataSource.size() > 0){
	//			query.append(" in ");
	//			Iterator<String> itDataSource = dataSource.iterator();
	//			while(itDataSource.hasNext()){        	
	//				query.append(itDataSource.next());
	//				if (itDataSource.hasNext()) query.append(", ");
	//			}	
	//		}
	//		query.append(" return Taxon");
	//
	//		return query.toString();
	//	}


	/**
	 * Creates a Species Discovery query using SPQL 2.0
	 */
	protected String createQuery(List<String> scientificNames, List<String> dataSource) {

		if (scientificNames.size()==0) throw new IllegalArgumentException("No scientific name specified");

		//create query
		StringBuilder query = new StringBuilder();
		query.append("SEARCH BY SN ");
		Iterator<String> itScNames = scientificNames.iterator();

		while(itScNames.hasNext()){
			query.append("'");
			query.append(itScNames.next());
			query.append("'");
			if (itScNames.hasNext()) query.append(", ");
		}

		if (dataSource != null){
			if (dataSource.size() > 0){
				query.append(" IN ");
				Iterator<String> itDataSource = dataSource.iterator();
				while(itDataSource.hasNext()){        	
					query.append(itDataSource.next());
					if (itDataSource.hasNext()) query.append(", ");
				}	
			}
		}
		query.append(" RETURN TAXON");

		logger.info(query.toString());
		return query.toString();
	}

	/**
	 * Check if two scientific names are equal
	 */
	private boolean checkEquals(List<String> scientificNames, String scientificName) {

		for(String name:scientificNames){	    	
			if (name.equalsIgnoreCase(scientificName)){
				return true;
			}
		}
		return false;
	}

	/**
	 * Save each tree in store
	 */
	protected void store(TaxonomyItem taxon) {
		logger.trace("retrieving Taxonomy subtree for {} {}", taxon.getId(), taxon.getScientificName());
		try{
			Stream<TaxonomyItem> taxonomyItems = classificationCall.getTaxonTreeById(taxon.getId());
			Stream<Tree> trees = pipe(taxonomyItems).through(GENERATOR);

			Stream<Tree> outcomes = store.add(trees);

			int i = 0;
			while (outcomes.hasNext()) {
				i++;
				treeCounter++;
				outcomes.next();
				if (i%100==0) logger.trace("Generated {} trees, generation ongoing...",i);
			}
			logger.trace("Generated {} trees from taxon {}", i, taxon.getId()+" "+taxon.getScientificName());
		} catch (Exception e) {
			logger.error("Failed tree generation for taxon "+taxon.getId()+" "+taxon.getScientificName(), e);
		}
	}
}