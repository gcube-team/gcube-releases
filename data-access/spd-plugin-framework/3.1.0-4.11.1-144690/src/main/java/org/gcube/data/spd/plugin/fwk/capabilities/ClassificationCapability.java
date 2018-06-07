package org.gcube.data.spd.plugin.fwk.capabilities;

import java.util.Iterator;
import java.util.List;

import org.gcube.data.spd.model.PropertySupport;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.plugin.fwk.Searchable;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;


public abstract class ClassificationCapability implements PropertySupport, Searchable<TaxonomyItem>{

	/**
	 * retrieves all children giving a taxon id
	 * 
	 * @param taxonId the taxon id
	 * @return a list of taxon
	 */
	public abstract List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String taxonId) throws IdNotValidException, ExternalRepositoryException;
	
	
	/**
	 * retrieves taxon by ids
	 * 
	 * @param ids the taxon ids
	 * @return a taxon
	 */
	public abstract void retrieveTaxonByIds(Iterator<String> ids, ClosableWriter<TaxonomyItem> writer) throws ExternalRepositoryException;

	/**
	 * retrieves taxon by id
	 * 
	 * @param taxonId the taxon id
	 * @return a taxon
	 */
	public abstract TaxonomyItem retrieveTaxonById(String id) throws IdNotValidException, ExternalRepositoryException;
	
	/**
	 * 
	 * retrieve a list of synonyms 
	 * 	 
	 * @param writer
	 * @param ids
	 */
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id) throws IdNotValidException, MethodNotSupportedException, ExternalRepositoryException{
		throw new MethodNotSupportedException();
	}
	

	@Override
	public Class<TaxonomyItem> getHandledClass() {
		return TaxonomyItem.class;
	}
		
}
