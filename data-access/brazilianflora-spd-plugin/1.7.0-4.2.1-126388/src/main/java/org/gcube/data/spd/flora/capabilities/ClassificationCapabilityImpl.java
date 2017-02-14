package org.gcube.data.spd.flora.capabilities;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.flora.FloraPlugin;
import org.gcube.data.spd.flora.Utils;
import org.gcube.data.spd.flora.dbconnection.ConnectionPool;
import org.gcube.data.spd.flora.dbconnection.ConnectionPoolException;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.exceptions.StreamBlockingException;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;
import org.gcube.data.spd.model.products.TaxonomyStatus.Status;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;


public class ClassificationCapabilityImpl extends ClassificationCapability {

	GCUBELog logger = new GCUBELog(ClassificationCapabilityImpl.class);

	@Override
	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}


	/* (non-Javadoc)
	 * @see org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability#getSynonymnsById(org.gcube.data.spd.plugin.fwk.writers.ObjectWriter, java.lang.String)
	 */
	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id)
			throws IdNotValidException, MethodNotSupportedException,
			ExternalRepositoryException {
		logger.trace("getSynonimnsByIds");
		
		TaxonomyItem rs = null;
		try{
			rs = getSynbyId(id);
			if ((rs!=null) && (writer.isAlive()))
				writer.write(rs);

		}catch (Exception e) {
			writer.write(new StreamBlockingException("BrazilianFlora",id));
		}
	}


	/**
	 * get RSItem by id
	 */
	private TaxonomyItem getSynbyId(String id) {


		TaxonomyItem item = null;
		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			String query ="select scientific_name, rank, status, id_parent, citation, acceptednameusageid, qualifier from "+ FloraPlugin.tableName + " where acceptednameusageid = ?";			
			results =  pool.selectPrestatement(query, id);	
			if(results!=null) {	
				if(results.next()) {	

					String ScName = results.getString(1);
					String rank = results.getString(2);
					String status = results.getString(3);
					String idParent = results.getString(4);
					String author = results.getString(5);
					String acceptednameusageid = results.getString(6);
					String qualifier = results.getString(7);

					item = createTaxonomyItem(id, ScName, rank, status, idParent, 
							author, acceptednameusageid, qualifier, true);

				}
			}
		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e) {
			logger.error("general Error", e);
		}
		finally {
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}	
		return item;
	}


	/**
	 * Create Taxonomy Item
	 */

	private TaxonomyItem createTaxonomyItem(String id, String ScName, String rank, String status, String idParent, 
			String author, String acceptednameusageid, String qualifier, boolean flag) throws SQLException {
		TaxonomyItem item = null;
		try{

			item = new TaxonomyItem(id);
			item.setScientificName(ScName);
			item.setRank(rank);
			item.setScientificNameAuthorship(author);
			item.setStatus(setTaxonomicStatus(status, acceptednameusageid, qualifier));

			item.setCitation(Utils.createCitation());
			item.setCredits(Utils.createCredits());

			if ((flag) && (idParent!=null)){
				try {
					item.setParent(retrieveTaxonById(idParent));
				} catch (IdNotValidException e) {
					logger.error("Id Not Valid", e);
				}    
			}
			else
				item.setParent(null);
		}catch (Exception e) {
			logger.error("ID not valid Exception", e);
		}

		return item;
	}



	/**
	 * Set Taxonomic status using "status", "acceptednameusageid", "qualifier"
	 */
	public static TaxonomyStatus setTaxonomicStatus(String status, String id_syn, String qualifier) {
		TaxonomyStatus t;

		if (status==null)
			t =  new TaxonomyStatus(qualifier, Status.UNKNOWN);
		else if (status.equals("Accepted name"))
			t =  new TaxonomyStatus(qualifier, Status.ACCEPTED);
		else if (status.equals("Synonym"))
			t =  new TaxonomyStatus(Status.SYNONYM, id_syn, qualifier);
		else t =  new TaxonomyStatus(qualifier, Status.UNKNOWN);

		return t;
	}


	/**
	 * Return a ResultSet of Children
	 */
	private ResultSet createResultItemChilds(String id) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			String query = "select id, scientific_name, rank, status, citation, acceptednameusageid, qualifier from "+ FloraPlugin.tableName + " where id_parent = ?";
			results =  pool.selectPrestatement(query, id);
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
			results = createResultSet(name);
			if (results!=null){
				while(results.next()) {	

					String id = results.getString(1);
					String scientificName = results.getString(2);
					String rank = results.getString(3);
					String status = results.getString(4);
					String acceptednameusageid = results.getString(7);
					String qualifier = results.getString(8);
					String parent = results.getString(5);
					String author = results.getString(6);

					TaxonomyItem tax = createTaxonomyItem(id, scientificName, rank, status, parent, author, acceptednameusageid, qualifier, true);
					if (writer.isAlive() && (tax!=null))
						writer.write(tax);
					else
						break;
				}
			}

		}catch (Exception e) {
			writer.write(new StreamBlockingException("BrazilianFlora",""));
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


	/**
	 * Return a ResultSet of scientific names
	 */
	private ResultSet createResultSet(String scientificName) {

		ConnectionPool pool = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();

			String term = "%" + scientificName + "%";

			String query = "select id, scientific_name, rank, status, id_parent, citation, acceptednameusageid, qualifier from "+ FloraPlugin.tableName + " where UPPER(scientific_name) like UPPER(?)";
			results =  pool.selectPrestatement(query, term);
		}
		catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		}finally{

		}
		return results;
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
			writer.write(new StreamBlockingException("BrazilianFlora", ""));					
		}finally{
			writer.close();
		}
	}


	@Override
	public TaxonomyItem retrieveTaxonById(String id)
			throws IdNotValidException {

		ConnectionPool pool = null;
		ResultSet results = null;
		TaxonomyItem tax = null;

		try {
			pool = ConnectionPool.getConnectionPool();

			String query ="select scientific_name, rank, status, id_parent, citation, acceptednameusageid, qualifier from "+ FloraPlugin.tableName + " where id = ?";			

			results =  pool.selectPrestatement(query, id);	

			if(results.next()) {	

				String scientificName = results.getString(1);
				String rank = results.getString(2);
				String status = results.getString(3);
				String parent = results.getString(4);
				String author = results.getString(5);
				String acceptednameusageid = results.getString(6);
				String qualifier = results.getString(7);

				tax = createTaxonomyItem(id, scientificName, rank, status, parent, author, acceptednameusageid, qualifier, true);

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
		List<TaxonomyItem> list = null;
		ResultSet results = null;
		try {
			results = createResultItemChilds(idParent);
			if (results!=null){
				list = new ArrayList<TaxonomyItem>(); 

				while(results.next()) {	

					String id = results.getString(1);
					String scientific_name = results.getString(2);
					String rank = results.getString(3);
					String status = results.getString(4);
					String author = results.getString(5);
					String acceptednameusageid = results.getString(6);
					String qualifier = results.getString(7);

					TaxonomyItem tax = createTaxonomyItem(id, scientific_name, rank, status, idParent, author, acceptednameusageid, qualifier, false);

					if (tax!=null)
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



}
