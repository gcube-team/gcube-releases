package org.gcube.data.spd.wordssplugin.capabilities;

import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.model.CommonName;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.wordssplugin.Utils;
import org.gcube.data.spd.wordssplugin.WordssPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import aphia.v1_0.wordss.AphiaRecord;
import aphia.v1_0.wordss.Source;
import aphia.v1_0.wordss.Vernacular;

public class ClassificationCapabilityImpl extends ClassificationCapability {

	static Logger logger = LoggerFactory.getLogger(ClassificationCapabilityImpl.class);

	@Override
	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}

	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String id) throws IdNotValidException, ExternalRepositoryException {

		//		logger.trace(id);
		List<TaxonomyItem> list = new ArrayList<TaxonomyItem>(); 	
		try {
			AphiaRecord[] records;
			final int offsetlimit=50;
			int offset =1;
			do{
				records =  WordssPlugin.binding.getAphiaChildrenByID(Integer.parseInt(id), offset);

				if (records!=null){
					//										logger.debug(records.length);
					for (AphiaRecord record : records){
						TaxonomyItem item = createItem(record, false);
						if (item!=null)
							list.add(item);
					}
				}
				offset+=offsetlimit;
			} while (records!=null && records.length==offsetlimit);
		}catch (NumberFormatException e) {
			logger.error("id not valid",e);
			throw new IdNotValidException(e);
		} catch (Throwable e) {
			logger.error("error contacting wordss service", e);
			throw new ExternalRepositoryException(e);
		}

		return list;
	}




	public void retrieveTaxa(ObjectWriter<TaxonomyItem> writer, String word) throws ExternalRepositoryException{

		Set<Integer> hash = new HashSet<Integer>();

		try {
			AphiaRecord[] records = null;

			final int offsetlimit=50;
			int offset =1;
			do{

				records = WordssPlugin.binding.getAphiaRecords(word, true, false, false, offset);

				if (records!=null){
					for (AphiaRecord record : records){

						if (hash.contains(record.getAphiaID())){
							continue;
						}

						hash.add(record.getAphiaID());

						TaxonomyItem item = createItem(record, true);
						if ((item != null) && (writer.isAlive()))
							writer.write(item);

					}
				}


				offset+=offsetlimit;
			} while (records!=null && records.length==offsetlimit);

		} catch (Throwable e) {
			logger.error("error contacting wordss service", e);
			throw new ExternalRepositoryException(e);
		}

	}

	//create TaxonomyItem
	private TaxonomyItem createItem(AphiaRecord record, Boolean flag) throws RemoteException {
		TaxonomyItem item = null;
		String scientificname = record.getScientificname();
		//		logger.trace("scientificname " + scientificname);
		if (scientificname != null ){

			item = new TaxonomyItem(record.getAphiaID()+"");

			item.setScientificName(record.getScientificname());
			item.setScientificNameAuthorship(record.getAuthority());
			item.setLsid(record.getLsid());

			item.setCredits(Utils.createCredits());			
			item.setCitation(record.getCitation());

			item.setRank(record.getRank());		
			List<CommonName> listCommNames = new ArrayList<CommonName> ();

			Vernacular[] vernaculars = WordssPlugin.binding.getAphiaVernacularsByID(record.getAphiaID());
			if (vernaculars!=null){
				for (Vernacular vernacular : vernaculars) {			
					if (vernacular.getLanguage_code()!=null){
						CommonName a = new CommonName(vernacular.getLanguage(),vernacular.getVernacular());
						listCommNames.add(a);		
					}
				}
			}
			item.setCommonNames(listCommNames);		

			try{
				if (record.getStatus().equals("accepted"))
					item.setStatus(new TaxonomyStatus("accepted", Status.ACCEPTED));
				else if (record.getStatus().equals("unaccepted") && WordssPlugin.binding.getAphiaRecordByID(record.getValid_AphiaID()).getStatus().equals("accepted")){
					//							logger.trace(WordssPlugin.binding.getAphiaSynonymsByID(record.getValid_AphiaID()));						
					item.setStatus(new TaxonomyStatus(Status.SYNONYM, record.getValid_AphiaID()+"", "synonym"));}
				else
					item.setStatus(new TaxonomyStatus(record.getStatus(), Status.UNKNOWN));
			}catch (Exception e) {
				item.setStatus(new TaxonomyStatus(record.getStatus(), Status.UNKNOWN));
			}

			if (flag){
				try{
					item.setParent(Utils.retrieveTaxonomy( WordssPlugin.binding.getAphiaClassificationByID(record.getAphiaID()), record.getAphiaID()));
				}catch (Exception e) {
					item.setParent(null);
				}
			}
			else 
				item.setParent(null);

			Source[] sources = null;
			if ((sources= WordssPlugin.binding.getSourcesByAphiaID(record.getAphiaID()))!=null){
				for (Source source : sources){
					//			logger.trace(source.getReference());
					if (source==null)
						continue;
					if (source.getReference()!=null){

						StringBuilder p = new StringBuilder();
						p.append(source.getReference());
						if (source.getLink()!=null){
							p.append(", available online at ");
							p.append(source.getLink());
						}
						if (source.getUrl()!=null){
							p.append(", details: ");
							p.append(source.getUrl());
						}				
						ElementProperty property = new ElementProperty(source.getUse(), p.toString());
						item.addProperty(property);				
					}
				}
			}
		}

		return item;
	}



	@Override
	public void retrieveTaxonByIds(Iterator<String> ids, ClosableWriter<TaxonomyItem> writer) throws ExternalRepositoryException {

		try{
			while(ids.hasNext()) {

				String id = ids.next(); 
				//				logger.trace("Retrive taxon by id " + id);
				TaxonomyItem tax = retrieveTaxonById(id);
				if ((tax != null) && (writer.isAlive()))
					writer.write(tax);
				else
					break;
			}

		} catch (IdNotValidException e) {
			logger.error("IdNotValidn", e);
		} catch (ExternalRepositoryException e) {
			logger.error("ExternalRepositoryException", e);
			throw e;
		}finally{
			writer.close();
		}
	}


	@Override
	public TaxonomyItem retrieveTaxonById(String id) throws IdNotValidException, ExternalRepositoryException {

		TaxonomyItem tax = null;
		//		logger.trace("Retrive taxon by id " + id);
		AphiaRecord record = null;

		try {
			record = WordssPlugin.binding.getAphiaRecordByID(Integer.parseInt(id));
			if (record!=null){				
				tax = createItem(record, true);
			}
		} catch (NumberFormatException e) {
			logger.error("NumberFormatException", e);
			throw new IdNotValidException(e);
		} catch (RemoteException e) {
			logger.error("Remote Error", e);
			throw new ExternalRepositoryException(e);
		}

		return tax;

	}


	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id)
			throws IdNotValidException, MethodNotSupportedException, ExternalRepositoryException {
		try{			

			AphiaRecord[] records = null;
			if ((records = WordssPlugin.binding.getAphiaSynonymsByID(Integer.parseInt(id)))!=null){
				for (AphiaRecord record : records){

					TaxonomyItem tax = createItem(record, true);
					if ((tax != null) && (writer.isAlive()))
						writer.write(tax);
					else
						break;
				}	
			}
		}catch (Exception e) {
			logger.error("General Error", e);
			throw new ExternalRepositoryException(e);
		}
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) throws ExternalRepositoryException {
		retrieveTaxa(writer, word);

	}

}
