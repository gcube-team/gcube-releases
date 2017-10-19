package org.gcube.data.spd.asfis.capabilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.asfis.AsfisPlugin;
import org.gcube.data.spd.asfis.Utils;
import org.gcube.data.spd.asfis.dbconnection.ConnectionPool;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ClassificationCapabilityImpl extends ClassificationCapability {

	static Logger logger = LoggerFactory.getLogger(ClassificationCapabilityImpl.class);

	@Override
	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}

	@Override
	public void retrieveTaxonByIds(Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) {
		try{
			while(ids.hasNext()) {
				String id = ids.next(); 
				TaxonomyItem item = null;
				try {
					item = retrieveTaxonById(id);
					if (item!=null){
						if (writer.isAlive() && (item!=null))
							writer.write(item);
						else
							break;
					}
				} catch (IdNotValidException e) {
					logger.error("Id Not Valid", e);
				}
			}
		}catch (Exception e) {
			writer.write(new StreamBlockingException("ASFIS", ""));					
		}finally{
			writer.close();
		}
	}


	@Override
	public TaxonomyItem retrieveTaxonById(String id)
			throws IdNotValidException {
		logger.trace("retrieveTaxonById " + id);
		ResultSet results = null;
		TaxonomyItem tax = null;

		try {

			results = Utils.createCompleteResultSetByID(id);
			if (results!=null){
				if(results.next()) {	

					String scientific_name = results.getString(1);
					String author = results.getString(2);
					//common names
					String englishName = results.getString(3);
					String frenchName = results.getString(4);
					String spanishName = results.getString(5);
					String arabic_name = results.getString(11);
					String chinese_name = results.getString(12);
					String russian_name = results.getString(13);

					//parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank
					String parent_id = results.getString(6);
					String taxocode = results.getString(7);
					String isscaap = results.getString(8);
					String threeA_CODE = results.getString(9);
					String rank = results.getString(10);

					tax = createTaxonomyItem(id, scientific_name, author, englishName, frenchName, spanishName, parent_id, taxocode, isscaap, threeA_CODE, rank, arabic_name, chinese_name, russian_name, true);

				}				
			}
		}
		catch (Throwable e) {
			logger.error("Id not valid exception", e);

		}finally{
			if (results!=null){
				try {
					results.close();
				} catch (SQLException e) {
					logger.error("general Error", e);
				}	
			}
		}
		return tax;

	}


	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String idParent)
			throws IdNotValidException, ExternalRepositoryException {
		logger.trace("retrieveTaxonChildrenByTaxonId " + idParent);
		//		logger.info("retrieveTaxonChildrenByTaxonId " + idParent);
		List<TaxonomyItem> list = null;
		ResultSet results = null;

		try {

			results = createResultItemChilds(idParent);

			list = new ArrayList<TaxonomyItem>(); 
			while(results.next())  {
				TaxonomyItem tax = null;

				String id = results.getString(1);
				String scientific_name = results.getString(2);
				String author = results.getString(3);

				//common names
				String englishName = results.getString(4);
				String frenchName = results.getString(5);
				String spanishName = results.getString(6);
				String arabic_name = results.getString(11);
				String chinese_name = results.getString(12);
				String russian_name = results.getString(13);

				//parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank
				String taxocode = results.getString(7);
				String isscaap = results.getString(8);
				String threeA_CODE = results.getString(9);
				String rank = results.getString(10);


				tax = createTaxonomyItem(id, scientific_name, author, englishName, frenchName, spanishName, idParent, taxocode, isscaap, threeA_CODE, rank, arabic_name, chinese_name, russian_name, false);
				if (tax!=null){
					tax.setParent(null);	
					list.add(tax);

				}
			}

		} catch (SQLException e) {
			logger.error("sql Error", e);
		}finally{
			if (results!=null){
				try {
					results.close();
				} catch (SQLException e) {
					logger.error("general Error", e);
				}
			}
		}
		return list;	
	}



	/**
	 * Create Taxonomy Item
	 * @param spanishName 
	 * @param frenchName 
	 * @param englishName 
	 * @param author 
	 * @param scientific_name 
	 * @param id 
	 * @param rank 
	 * @param threeA_CODE 
	 * @param isscaap 
	 * @param taxocode 
	 * @param parent_id 
	 * @param russian_name 
	 * @param chinese_name 
	 * @param arabic_name 
	 */

	private TaxonomyItem createTaxonomyItem(String id, String scientific_name, String author, String englishName, String frenchName, String spanishName, String parent_id, String taxocode, String isscaap, String threeA_CODE, String rank, String arabic_name, String chinese_name, String russian_name, Boolean follow) throws SQLException {
		TaxonomyItem item = null;
		try{

			item = new TaxonomyItem(id);
			item.setScientificName(scientific_name);
			item.setRank(rank);
			item.setScientificNameAuthorship(author);
			item.setStatus(new TaxonomyStatus(AsfisPlugin.STATUS, Status.VALID));

			//set common names
			item.setCommonNames(Utils.setCommonNames(englishName, frenchName, spanishName, chinese_name, arabic_name, russian_name));

			item.setCitation(Utils.createCitation());
			item.setCredits(Utils.createCredits());

			if ((follow) & (!id.equals(parent_id))){
				item.setParent(retrieveTaxonById(parent_id));	
			}				
			else
				item.setParent(null);

			//add property: ISSCAAP, 3A-CODE, STATS-DATA
			if (isscaap!=null){
				ElementProperty propertyIsscaap = new ElementProperty("ISSCAAP", isscaap);
				item.addProperty(propertyIsscaap);	
			}
			if (threeA_CODE!=null){
				ElementProperty property3 = new ElementProperty("3A-CODE", threeA_CODE);
				item.addProperty(property3);
			}
			if (taxocode!=null){
				ElementProperty propertyTaxocode = new ElementProperty("TAXOCODE", taxocode);
				item.addProperty(propertyTaxocode);
			}


		}catch (Exception e) {
			logger.error("ID not valid Exception", e);
		}

		return item;
	}


	/**
	 * Return a ResultSet of Children
	 */
	private ResultSet createResultItemChilds(String id) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			String query = "select id, Scientific_name, Author, English_name, French_name, Spanish_name, TAXOCODE, ISSCAAP, threeA_CODE, rank, Arabic_name, Chinese_name, Russian_name from "+ AsfisPlugin.table + " where parent_id != id and parent_id = " + id;
			results =  pool.selectPrestatement(query, null);
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		}
		return results;

	}

	@Override
	public void searchByScientificName(String name,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		logger.trace("Retrive taxa by name " + name);

		ResultSet results = null;
		try {
			results = Utils.createResultSetByName(name);

			if (results!=null){
				while(results.next()) {	

					String id = results.getString(1);
					String scientific_name = results.getString(2);
					String author = results.getString(3);

					//common names
					String englishName = results.getString(4);
					String frenchName = results.getString(5);
					String spanishName = results.getString(6);
					String arabic_name = results.getString(12);
					String chinese_name = results.getString(13);
					String russian_name = results.getString(14);

					//parent_id, TAXOCODE, ISSCAAP, threeA_CODE, rank
					String parent_id = results.getString(7);
					String taxocode = results.getString(8);
					String isscaap = results.getString(9);
					String threeA_CODE = results.getString(10);
					String rank = results.getString(11);

					TaxonomyItem tax = createTaxonomyItem(id, scientific_name, author, englishName, frenchName, spanishName, parent_id, taxocode, isscaap, threeA_CODE, rank, arabic_name, chinese_name, russian_name, true);
					if (writer.isAlive() && (tax!=null))
						writer.write(tax);
					else
						break;
				}
			}

		}catch (Exception e) {
			writer.write(new StreamBlockingException("ASFIS",""));
		}finally{

			if (results!=null){
				try {
					results.close();
				} catch (SQLException e) {
					logger.error("general Error", e);
				}	
			}
		}

	}

}
