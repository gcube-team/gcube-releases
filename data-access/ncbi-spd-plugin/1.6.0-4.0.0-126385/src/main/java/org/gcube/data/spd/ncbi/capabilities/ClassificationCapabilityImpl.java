package org.gcube.data.spd.ncbi.capabilities;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Conditions;
import org.gcube.data.spd.model.exceptions.ExternalRepositoryException;
import org.gcube.data.spd.model.exceptions.IdNotValidException;
import org.gcube.data.spd.model.exceptions.MethodNotSupportedException;
import org.gcube.data.spd.model.util.ElementProperty;
import org.gcube.data.spd.ncbi.Utils;
import org.gcube.data.spd.ncbi.connection.ConnectionPool;
import org.gcube.data.spd.plugin.fwk.capabilities.ClassificationCapability;
import org.gcube.data.spd.plugin.fwk.writers.ClosableWriter;
import org.gcube.data.spd.plugin.fwk.writers.ObjectWriter;
import org.gcube.data.spd.model.products.TaxonomyItem;
import org.gcube.data.spd.model.products.TaxonomyStatus;


public class ClassificationCapabilityImpl extends ClassificationCapability {

	GCUBELog logger = new GCUBELog(ClassificationCapabilityImpl.class);

	@Override
	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}


	/*
	 * 
	 */
	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String idPost)
			throws IdNotValidException, MethodNotSupportedException,
			ExternalRepositoryException {
		logger.trace("getSynonimnsByIds in NCBI...");

		String id = null;
		try{
			id = Utils.getOriginalId(idPost);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = idPost;
			//			logger.error("id is already a original one", e);
		}		
		ResultSet rs = null;
		try {			
			rs = getRSSynonyms(id);			
			if (rs != null){
				int count = 0;
				while(rs.next()) {						
					count++;
					String tax_id = rs.getString(1) + "-" + count;
					String ScName = rs.getString(2);
					String rank = rs.getString(3);
					String idParent = rs.getString(4);
					String status = rs.getString(5);
					TaxonomyItem item = null;

					try {
						item = createTaxonomyItem(tax_id,ScName,rank,idParent,status, true);			
					}catch (Exception e) {
						logger.error("general Error", e);
					}
					if (item!=null)
						writer.write(item);
				}
			}
		}			
		catch (SQLException sqlExcept) {
			logger.error("sql Error", sqlExcept);
		}catch (Throwable e) {
			logger.error("general Error", e);
		} finally {
			if (rs!=null){
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
			}		
		}

		logger.trace("getSynonimnsByIds finished in NCBI");
	}




	private ResultSet getRSSynonyms(String id) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			logger.trace("select b.name_txt, a.rank, a.parent_tax_id from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and a.tax_id = " + id  );
			
			String query = ("select a.tax_id, b.name_txt, a.rank, a.parent_tax_id, b.name_class from nodes as a NATURAL JOIN names as b where b.name_class = 'synonym' and CAST(a.tax_id as TEXT) like ?");			
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(id);
			rs = pool.selectPrestatement(query, terms, stmt);
		}			
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}

		}
		return rs;	
	}


	private TaxonomyItem createTaxonomyItem(String id, String ScName, String rank, String idParent, String status, Boolean flag) throws SQLException {
		TaxonomyItem item = new TaxonomyItem(id);
		item.setScientificName(ScName);
		item.setCommonNames(Utils.getCommonNames(id));
		item.setStatus(setTaxonomicStatus(status));

		item.setCredits(Utils.createCredits());
		item.setCitation(Utils.createCitation());

		item.setScientificNameAuthorship(Utils.setAuthorship(id, ScName));
		
		List<String> citation = null;
		try {
			citation = Utils.getCitation(id);

			for (String c: citation){
				ElementProperty property = new ElementProperty("Comments and References", c);
				item.addProperty(property);
			}
		}
		finally{
			if (citation!=null)
				citation.clear();
		}
		if (!rank.equals("no rank"))				
			item.setRank(rank);

		//		logger.trace("parent " +idParent);
		if ((flag) && ((idParent!=null) && !(idParent.equals("1")) && !(rank.equals("kingdom")))){
			try {
//				logger.trace("parent " + idParent);
				item.setParent(retrieveTaxonById(idParent));
			} catch (IdNotValidException e) {
				logger.trace("Id not valid", e);
			}
		}
		else
			item.setParent(null);
		return item;	     
	}




	private ResultSet retrieveRSChilds(String id) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			Statement statement = con.createStatement();

			String query = ("select a.tax_id, b.name_txt, a.rank, a.parent_tax_id, b.name_class from nodes as a NATURAL JOIN names as b where CAST(a.parent_tax_id as TEXT) like ?");			
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(id);

			rs = pool.selectPrestatement(query, terms, stmt);
		}
		catch (Throwable e){
			logger.error("general Error", e);
		} finally {		
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}		
		}
		return rs;
	}


//	@Override
//	public void searchByCommonName(String name,
//			ObjectWriter<TaxonomyItem> writer, ... arg2) {
//		logger.trace("Retrive Taxa By Common Name " + name);
//
//		getDistinctIds(name, "common name");
//
//		ResultSet rs = null;
//		try{	
//			rs = getDistinctIds(name, "common name");
//			if (rs != null){
//				int count = 0;
//				while(rs.next()) {	 
//					count++;
//					String id = rs.getString(1) + "-" + count;
//					TaxonomyItem item = retrieveTaxonById(id);
//					if ((item!=null) && (writer.isAlive()))
//						writer.write(item);
//					else
//						break;			
//				}
//
//			}
//		}
//		catch (SQLException sqlExcept) {        	
//			logger.error("sql Error", sqlExcept);
//		}
//		catch (Throwable e) {
//			logger.error("general Error", e);
//		} finally {
//
//			if (rs!=null){
//				try {
//					rs.close();
//				} catch (SQLException e) {
//					logger.error("sql Error", e);
//				}
//			}		
//			
//		}
//	}



	private ResultSet getDistinctIds(String name, String type) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			String query = ("select distinct(tax_id) from names where name_class = (?) and UPPER(name_txt) like UPPER(?)");
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(type);
			terms.add("%" + name + "%");
			rs = pool.selectPrestatement(query, terms, stmt);
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		}
		return rs;		
	}


	@Override
	public void searchByScientificName(String word,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		ResultSet listIds = null;
		try{
			logger.trace("Retrive Taxa By Scientific Name " + word);
			listIds = getDistinctIds(word, "scientific name");

			if (listIds!=null){
				String id = null;

				while(listIds.next()) {	

					id = listIds.getString(1);
					//					logger.trace(id);
					TaxonomyItem item = null;
					try{
						item = retrieveTaxonById(id);
					}catch (Exception e) {
						logger.error("Error", e);
					}
					if ((item!=null) && (writer.isAlive()))
						writer.write(item);
					else
						break;
				}
			}
		} catch (SQLException e) {
			logger.error("sql Error", e);
		}
		finally {
			if (listIds!=null)
				try {
					listIds.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
		}
	}


	@Override
	public void retrieveTaxonByIds(
			Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) {

		try {
			while(ids.hasNext()) {
				String id = null;
				try{
					id = Utils.getOriginalId(ids.next()); 
				}
				catch (ArrayIndexOutOfBoundsException e){
					id = ids.next();
					//					logger.error("id is already a original one", e);
				}

				//				logger.trace("Retrive taxon by id " + id);
				TaxonomyItem item = retrieveTaxonById(id);
				if ((item!=null) && (writer.isAlive()))
					writer.write(item);
				else
					break;
			}
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {
			writer.close();	

		}

	}

	private TaxonomyStatus setTaxonomicStatus(String status) {
		//		logger.trace(status);
		TaxonomyStatus tax;
		if (status!=null){
			if (status.equals("scientific name"))
				tax= new TaxonomyStatus(status, TaxonomyStatus.Status.ACCEPTED);
			else
				tax= new TaxonomyStatus(status, TaxonomyStatus.Status.UNKNOWN);
		}
		else
			tax= new TaxonomyStatus(status, TaxonomyStatus.Status.UNKNOWN);
		return tax;
	}


	@Override
	public TaxonomyItem retrieveTaxonById(String idPost)
			throws IdNotValidException {
		String id = null;
		try{
			id = Utils.getOriginalId(idPost);
		}
		catch (ArrayIndexOutOfBoundsException e){
			id = idPost;
			//			logger.error("id is already a original one", e);
		}

		TaxonomyItem item = null;
		ResultSet rs = null;
		try{
			rs = createRSbyID(id);
			if (rs!=null){
				if(rs.next()) {
					String tax_id = rs.getString(1);
					String name_txt = rs.getString(2);
					String rank = rs.getString(3);
//					logger.trace("name: " + name_txt + "- rank: " + rank);
					
					String parent_tax_id = rs.getString(4);
					//				logger.trace("parent: " + parent_tax_id);
					String name_class = rs.getString(5);		

					item = createTaxonomyItem(tax_id, name_txt, rank, parent_tax_id, name_class, true);
				}		

			}
		}
		catch (SQLException sqlExcept) {        	
			logger.error("sql Error", sqlExcept);
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
		}
		return item;
	}



	private ResultSet createRSbyID(String id) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet rs = null;
		PreparedStatement stmt = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			//			Statement statement = con.createStatement();

			String query = ("select a.tax_id, b.name_txt, a.rank, a.parent_tax_id, b.name_class from nodes as a NATURAL JOIN names as b where b.name_class = 'scientific name' and CAST(a.tax_id as TEXT) like ?" );
			ArrayList<String> terms = new ArrayList<String>();
			terms.add(id);

			rs = pool.selectPrestatement(query, terms, stmt);

		}
		catch (Throwable e) {
			logger.error("general Error", e);
		} finally {

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return rs;
	}


	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String idPost)
			throws IdNotValidException, ExternalRepositoryException {

				String id = null;
				try{
					id = Utils.getOriginalId(idPost);
				}
				catch (ArrayIndexOutOfBoundsException e){
					id = idPost;
					//			logger.error("id is already a original one", e);
				}
				logger.trace("retrieveTaxonChildsByTaxonId " + id);
				List<TaxonomyItem> list = new ArrayList<TaxonomyItem>(); 		
				ResultSet rs = null;
				try {
					rs = retrieveRSChilds(id);
					if (rs != null){
						int count = 0;
						while(rs.next()){
							count++;
							String tax_id = rs.getString(1) + "-" + count;
							String ScName = rs.getString(2);
							String rank = rs.getString(3);
							String idParent = rs.getString(4);
							String status = rs.getString(5);
							TaxonomyItem tax = createTaxonomyItem(tax_id,ScName,rank,idParent,status, false);
							list.add(tax); 
						}			
					}
				}
				catch (SQLException sqlExcept){        	
					logger.error("sql Error", sqlExcept);
				}
				catch (Throwable e){
					logger.error("general Error", e);
				} finally {
					if (rs!=null){
						try {
							rs.close();
						} catch (SQLException e) {
							logger.error("sql Error", e);
						}
					}		
				}
				return list;
			}




}
