package org.gcube.data.spd.irmng.capabilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.data.spd.irmng.Utils;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPool;
import org.gcube.data.spd.irmng.dbconnection.ConnectionPoolException;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ClassificationCapabilityImpl extends ClassificationCapability {

	static Logger logger = LoggerFactory.getLogger(ClassificationCapabilityImpl.class);

	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}

	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id)
			throws IdNotValidException, MethodNotSupportedException,
			ExternalRepositoryException {
		logger.trace("getSynonimnsById " + id);

		ResultSet results =  null;
		try{
			results = getSynRSItem(id);
			String idSyn = null;
			if (results!=null){
				while (results.next()){
					idSyn = results.getString(1);
					TaxonomyItem tax = retrieveTaxonById(idSyn);
					if ((tax!=null) && (writer.isAlive()))
						writer.write(tax);
				}
			}
		} catch (SQLException ex) {
			writer.write(new StreamBlockingException("IRMNG", ""));
		}finally{
			try {
				if (results != null) {
					results.close();
				}
			} catch (SQLException ex) {
				logger.error("sql Error", ex);
			}
		}
	}

	/**
	 * get a RSItem by id
	 */
	private ResultSet getSynRSItem(String id) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select distinct(taxonid) from taxon where taxonomicstatus = 'synonym' and acceptednameusageid = ?";
			results =  pool.selectPrestatement(query, id);

		}		
		catch (Throwable e) {
			logger.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		}
		return results;
	}



	/**
	 * get a list of RSItem by id
	 */
	private ResultSet getRSItemChildren(String id) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select taxonid, taxonrank, scientificnameauthorship, taxonomicstatus, modified, acceptednameusageid, nameaccordingto, name from taxon where parentnameusageid = ?";
			results =  pool.selectPrestatement(query, id);
			//			logger.trace(query);
		}
		catch (Throwable e) {
			logger.error("general Error", e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return results;

	}


	/**
	 * Set status in Result Item
	 */
	private TaxonomyStatus setTaxStatus(String status, String syn_parent) {

		TaxonomyStatus tax = null;
		if ( status!= null){
			if (status.equals("accepted"))
				tax = new TaxonomyStatus("accepted", Status.ACCEPTED);
			else if (status.equals("valid"))
				tax = new TaxonomyStatus("valid", Status.VALID);
			else if (status.equals("synonym"))
				tax = new TaxonomyStatus(Status.SYNONYM, syn_parent, "synonym");
			else
				tax = new TaxonomyStatus(status, Status.UNKNOWN);
		} 
		else
			tax = new TaxonomyStatus(status, Status.UNKNOWN);
		//		else
		//			tax = new TaxonomyStatus(Status.UNKNOWN);
		return tax;
	}

	@Override
	public void searchByScientificName(String word,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		logger.trace("searchByScientificName " + word);
		ResultSet rs = null;
		try{
			rs = getRSItemByName(word);
			if (rs!=null){
				while(rs.next()) {
					try{						
						String id = rs.getString(1);
						String rank = rs.getString(2);
						String author = rs.getString(3);
						String status = rs.getString(4);
						String modified = rs.getString(5);
						String acceptednameusageid = rs.getString(6);	
						String nameaccordingto = rs.getString(7);
						String id_parent = rs.getString(8);
						String name = rs.getString(9);

						TaxonomyItem tax = createTaxonomyItem(id, rank,author, status, modified, acceptednameusageid, nameaccordingto, name, id_parent, true);
						if ((tax!=null) && (writer.isAlive()))
							writer.write(tax);	
					}catch (Exception e) {
						writer.write(new StreamBlockingException("IRMNG", ""));
					}

				}
			}
		}catch (SQLException sqlExcept) {        	
			logger.error("sql Error", sqlExcept);
		}finally{	
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
		}
	}


	/**
	 * Create Taxonomy Item by result
	 */
	private ResultSet getRSItemByName(String scientificName) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String name = "%" + scientificName + "%";
			String query = "select taxonid, taxonrank, scientificnameauthorship, taxonomicstatus, modified, acceptednameusageid, nameaccordingto, parentnameusageid, name from taxon where UPPER(name) like UPPER(?)";	
			results =  pool.selectPrestatement(query, name);

		}catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		} catch (SQLException e) {
			logger.error("SQLException",e);
		}finally{
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return results;
	}


	@Override
	public void retrieveTaxonByIds(Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) {
		try{
			while(ids.hasNext()) {
				//				String id = ids.next(); 
				//logger.trace("retrieveTaxonById " + id);
				TaxonomyItem item = retrieveTaxonById(ids.next());
				if (writer.isAlive())
					writer.write(item);
			}
		} catch (Exception e) {
			writer.write(new StreamBlockingException("IRMNG", ""));
		} finally{
			writer.close();	
		}
	}

	/**
	 * Create Taxonomy Item by result (if flag = false, parent=null)
	 * @param name 
	 */
	private TaxonomyItem createTaxonomyItem(String id, String rank,
			String author, String status, String modified,
			String acceptednameusageid, String nameaccordingto, 
			String name, String parentId, boolean flag) throws SQLException {

		TaxonomyItem item = null;

		try{
			item = new TaxonomyItem(id);

			Calendar dateModified = Utils.getCalendar(modified);
			item.setScientificName(name);
			item.setScientificNameAuthorship(author);		
			item.setRank(rank);

			item.setCredits(Utils.createCredits());

			StringBuilder cit = new StringBuilder();

			if (nameaccordingto != null){
				cit.append(nameaccordingto);
				cit.append(". ");
			}
			cit.append(Utils.createCitation());
			item.setCitation(cit.toString());

			item.setStatus(setTaxStatus(status, acceptednameusageid));
			item.setModified(dateModified);

			if (flag){
				//parent null
				if  (parentId != null){   
					try {
						item.setParent(retrieveTaxonById(parentId));
					} catch (IdNotValidException e) {
						logger.error("Id Not Valid",e);
					}	
				}
			}
		
			else
				item.setParent(null);

		}catch (Exception e) {
			logger.error("Exception",e);
		}

		return item;
	}



	@Override
	public TaxonomyItem retrieveTaxonById(String id)
			throws IdNotValidException {
		TaxonomyItem item = null;
		ResultSet rs = null;

		try{
			rs = getRSItemById(id);
			if (rs!=null){
				if (rs.next()){
					try {	
						String rank = rs.getString(1);
						String author = rs.getString(2);
						String status = rs.getString(3);
						String modified = rs.getString(4);
						String acceptednameusageid = rs.getString(5);					
						String nameaccordingto = rs.getString(6);
						String parentId = rs.getString(7);
						String name = rs.getString(8);

						item = createTaxonomyItem(id, rank,author, status, modified, acceptednameusageid, nameaccordingto, name, parentId, true);
					} catch (SQLException e) {
						logger.error("sql Error", e);
					}				
				}	
			}
		} catch (SQLException e) {
			logger.error("sql Error", e);
		}finally{
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
		}
		return item;
	}



	/**
	 * get a RSItem by id
	 */
	private ResultSet getRSItemById(String id) {

		ConnectionPool pool = null;
		Connection con = null;
		ResultSet result = null;
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select taxonrank, scientificnameauthorship, taxonomicstatus, modified, acceptednameusageid, nameaccordingto, parentnameusageid, name from taxon where taxonid = ?";	
			result =  pool.selectPrestatement(query, id);

		}
		catch (SQLException sqlExcept) {
			logger.error("sql Error",sqlExcept);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException",e);
		} finally{

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}			
		}
		return result;		
	}

	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String id_parent)
			throws IdNotValidException, ExternalRepositoryException {
		logger.trace("retrieveTaxonChildsByTaxonId " + id_parent);
		List<TaxonomyItem> list = null;
		ResultSet rs = null;
		try{
			rs = getRSItemChildren(id_parent);
			list = new ArrayList<TaxonomyItem>(); 
			if (rs!=null){
				while(rs.next()) {	

					try {
						String id = rs.getString(1);
						String rank = rs.getString(2);
						String author = rs.getString(3);
						String status = rs.getString(4);
						String modified = rs.getString(5);
						String acceptednameusageid = rs.getString(6);
						String nameaccordingto = rs.getString(7);
						String name = rs.getString(8);

						TaxonomyItem item = createTaxonomyItem(id, rank,author, status, modified, acceptednameusageid, nameaccordingto, name, id_parent, true);

						list.add(item);
					} catch (SQLException ex) {
						logger.error("sql Error", ex);

					}
				}
			}
		} catch (SQLException ex) {
			logger.error("sql Error", ex);
		}finally{
			if (rs!=null)
				try {
					rs.close();
				} catch (SQLException e) {
					logger.error("sql Error", e);
				}
		}
		return list;	
	}


}

