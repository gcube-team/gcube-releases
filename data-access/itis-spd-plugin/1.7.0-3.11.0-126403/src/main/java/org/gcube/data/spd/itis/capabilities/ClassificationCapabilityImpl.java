package org.gcube.data.spd.itis.capabilities;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.data.spd.itis.Utils;
import org.gcube.data.spd.itis.dbconnection.ConnectionPool;
import org.gcube.data.spd.itis.dbconnection.ConnectionPoolException;

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

public class ClassificationCapabilityImpl extends ClassificationCapability{

	DateFormat format = new SimpleDateFormat("yyyy-MM-dd");

	GCUBELog logger = new GCUBELog(ClassificationCapabilityImpl.class);

	public Set<Conditions> getSupportedProperties() {
		return Collections.emptySet();
	}


	@Override
	public void getSynonymnsById(ObjectWriter<TaxonomyItem> writer, String id) {
		logger.trace("getSynonymnsById");
		ResultSet rs = null;
		ResultSet result = null;

		ConnectionPool pool = null;
		Connection con = null;

		//			get a list within Synonym ids
		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select b.tsn from synonym_links as a NATURAL join longnames as b where a.tsn_accepted = ?";
			result =  pool.selectPrestatement(query, id);	


			if (result!=null){

				String tsn = null;
				while(result.next()){	
					tsn = result.getString(1);		
					TaxonomyItem tax = retrieveTaxonById(tsn);			
					if ((tax != null) && (writer.isAlive()))
						writer.write(tax);
					else
						break;
				}
			}
		} catch (Exception e) {
			logger.error("General Error", e);
		} finally {
			try {
				if (result != null) 
					result.close();

				if (rs != null)
					rs.close();

				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			} catch (SQLException e) {
				logger.error("SQL Error", e);
			}
		}
	}




	/**
	 * create full name
	 */
	private String getFullName(String unit_name1, String unit_name2, String unit_name3, String unit_name4) {
		StringBuilder sName = new StringBuilder();
		if (unit_name1!=null) {		
			sName.append(unit_name1);		
			if (unit_name2!=null) {     	
				sName.append(" ");
				sName = sName.append(unit_name2);
				if (unit_name3!=null) {
					sName.append(" ");
					sName = sName.append(unit_name3);
					if (unit_name4!=null) {
						sName.append(" ");
						sName = sName.append(unit_name4);
					}       					
				}
			}        			
		}	
		return sName.toString();
	}


	//
	//	/**
	//	 * Get a list of vernacular ids by a commonName
	//	 */
	//	private ResultSet getVernacularIdsList(String commonName) {
	//
	//		ConnectionPool pool = null;
	//		Connection con = null;
	//		ResultSet results = null;
	//		try {
	//			pool = ConnectionPool.getConnectionPool();
	//			con = pool.getConnection();
	//
	//			String query = "select distinct(a.tsn) from vernaculars as a NATURAL join taxonomic_units as b where UPPER(vernacular_name) like UPPER(?)";
	//			results =  pool.selectPrestatement(query, "%" + commonName + "%");	
	//
	//		} catch (ConnectionPoolException e) {
	//			logger.error("ConnectionPoolException", e);			
	//		} catch (SQLException e) {
	//			logger.error("SQL Error", e);
	//		} finally {		
	//
	//			if ((pool!=null) && (con!=null)){
	//				pool.releaseConnection(con);
	//			}
	//		}
	//		return results;
	//
	//	}



	@Override
	public void searchByScientificName(String scientificName,
			ObjectWriter<TaxonomyItem> writer, Condition... properties) {
		ConnectionPool pool = null;
		Connection con = null;
		ResultSet results = null;

		try {
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select a.tsn, a.completename, b.taxon_author_id, b.rank_id, b.update_date, b.name_usage, b.unaccept_reason, b.parent_tsn from longnames as a NATURAL join taxonomic_units as b where UPPER(a.completename) like UPPER(?)";
			try{
			results =  pool.selectPrestatement(query, "%" + scientificName + "%");	
			}catch (Exception e) {
				
			}

			if (results!=null){
				while(results.next()){	

					try{
						TaxonomyItem taxItem = createTaxItem(results.getString(1), results.getString(2), 
								results.getString(3), results.getString(4), results.getString(5), 
								results.getString(6), results.getString(7), results.getString(8), 
								null, null, null, null, null, true);					

						if (writer.isAlive() && (taxItem != null))
							writer.write(taxItem);
						else
							break;

					}catch (Exception e) {
						logger.error("Exception", e);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
			writer.write(new StreamBlockingException("ITIS", ""));			
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
			writer.write(new StreamBlockingException("ITIS", ""));			
		}finally{

			try {
				if (results != null) 
					results.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}	
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}

	}



	@Override
	public void retrieveTaxonByIds(Iterator<String> ids,
			ClosableWriter<TaxonomyItem> writer) {
		try{
			if (ids!=null){
				logger.trace("ids!=null");
				TaxonomyItem tax = null;
				while(ids.hasNext()) {
					String id = ids.next(); 
					tax = retrieveTaxonById(id);
					if ((tax != null) && (writer.isAlive()))
						writer.write(tax);
					else
						break;
				}
			}
		}finally{
			writer.close();	
		}

	}


	/**
	 * Create a Taxonomy Item by ResultSet object
	 * @throws ConnectionPoolException 
	 */
	private TaxonomyItem createTaxItem(String tsn, String completename, String author, 
			String rank_id, String update_date, String status, String remarks, 
			String parent_id, String citation_id, String unit_name1, String unit_name2, 
			String unit_name3, String unit_name4, Boolean flag) throws SQLException, ParseException, ConnectionPoolException {
		TaxonomyItem tax = null;
		String rank = Utils.getInfoFromId(rank_id, "rank");

		String citation = Utils.getCitationItis();
		String credits = Utils.getCreditsItis();

		//			logger.trace(" rank " + rank);

		tax = new TaxonomyItem(tsn + "");
		if (completename!=null){
			tax.setScientificName(completename);
		}else{
			//				logger.trace((item.getUnit_name1()+ " " + item.getUnit_name2()+ " " +  item.getUnit_name3()+ " " +  item.getUnit_name4()));
			String sName = getFullName(unit_name1, unit_name2, unit_name3, unit_name4);				
			tax.setScientificName(sName);	
		}

		tax = setSources(tax, tsn);

		tax.setCredits(credits);
		tax.setCitation(citation);;

		tax.setLsid("urn:lsid:itis.gov:itis_tsn:"+ tsn);

		tax.setRank(rank);

		if ((parent_id!=null) && flag && (!parent_id.equals("0"))){
			tax.setParent(retrieveTaxonById(parent_id));	
		}

		tax.setCommonNames(Utils.getCommonNameFromId(tsn));

		if (author!=null)
			tax.setScientificNameAuthorship(Utils.getInfoFromId(author, "author"));

		//			tax.setModified(Utils.getCalendar(modified));
		tax.setStatus(setTaxonomyStatus(status, remarks, tsn));

		List<String> comments = null;
		try{
			comments = retrieveComments(tsn);
			for(String comment: comments){
				//				logger.trace("Comment: " + comment);
				ElementProperty property = new ElementProperty("Comments",comment);
				tax.addProperty(property);
			}
		}
		finally{
			if (comments!=null)
				comments.clear();
		}

		return tax;
	}


	/**
	 * get a list of comments
	 * @throws ConnectionPoolException 
	 */
	private List<String> retrieveComments(String tsn) throws ParseException, ConnectionPoolException {
		ResultSet rs = null;
		ConnectionPool pool = null;
		Connection con = null;
		List<String> comments = new ArrayList<String>();
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select comment_time_stamp, commentator, comment_detail from tu_comments_links as a JOIN comments as b where a.comment_id= b.comment_id and a.tsn = ?";
			rs =  pool.selectPrestatement(query, tsn);	

			if (rs!=null){		
				StringBuilder comment = new StringBuilder();
				String time_stamp = null;
				String commentator = null;
				String comment_detail = null;

				while(rs.next()) {	

					time_stamp = rs.getString(1);
					commentator = rs.getString(2);
					comment_detail = rs.getString(3);
					//get version
					comment.append("Comment detail: ");
					comment.append(comment_detail);
					if (commentator!=null){
						comment.append(" - Commentator: ");
						comment.append(commentator);
					}
					if (time_stamp!= null){
						comment.append(" - Date: ");

						Date date = (Date)format.parse(time_stamp); 
						//						 Calendar cal=Calendar.getInstance();
						//						 cal.setTime(date);
						comment.append(format.format(date));
					}

					comments.add(comment.toString());	
					comment.delete(0, comment.length());
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}finally{
			try {
				if (rs != null) {
					rs.close();
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return comments;
	}



	/**
	 * set sources
	 * @throws ConnectionPoolException 
	 */
	private TaxonomyItem setSources(TaxonomyItem tax, String id) throws ConnectionPoolException {

		ResultSet rs = null;
		ConnectionPool pool = null;
		Connection con = null;
		try{

			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select a.source, a.version, a.source_type, a.acquisition_date from other_sources as a join reference_links as b on b.doc_id_prefix = a.source_id_prefix and b.documentation_id = a.source_id and b.tsn = ?";
			rs =  pool.selectPrestatement(query, id);	


			if (rs!=null){		
				String  source = null;
				String  version = null;
				String  source_type = null;
				String acquisition_date = null;
				while(rs.next()) {		
					//get version
					source = rs.getString(1);				
					version = rs.getString(2);
					source_type = rs.getString(3);
					acquisition_date = rs.getString(4);
					//			logger.trace("source " +source);
					//			logger.trace("version " +version);
					//			logger.trace("id_source " +id_source);
					StringBuilder s = new StringBuilder();
					if (source!=null){
						s.append(source);
					}
					if (source_type!=null){
						s.append(", ");
						s.append(source_type);
					}
					if (acquisition_date!=null){
						s.append(" (version ");
						s.append(version);
						s.append(").");
					}
					if (acquisition_date!=null){
						s.append(" Acquired: ");
						s.append(acquisition_date);
						s.append(".");
					}
					//					logger.trace(s);
					ElementProperty property = new ElementProperty("Source", s.toString());
					tax.addProperty(property );
				}
			}
		} catch (SQLException e) {
			logger.error("SQLException", e);
		}finally{
			try{
				if (rs != null) 
					rs.close();

			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}
		return tax;

	}



	/**
	 * set status
	 * @param con 
	 * @param pool 
	 */
	public TaxonomyStatus setTaxonomyStatus(String status, String notes, String id) {

		TaxonomyStatus tax = null;
		if (status !=null){
			if (status.equals("accepted"))		
				tax = new TaxonomyStatus("accepted", Status.ACCEPTED);
			else if (notes.equals("synonym"))
				tax = new TaxonomyStatus(Status.SYNONYM, getAcceptedTsn(id, null, null), "synonym");
			else if (notes.equals("misapplied"))
				tax = new TaxonomyStatus("misapplied",Status.MISAPPLIED);
			else if (status.equals("invalid"))
				tax = new TaxonomyStatus(Status.INVALID, getAcceptedTsn(id, null, null), "invalid");
			else if (status.equals("valid"))
				tax = new TaxonomyStatus("valid", Status.VALID);
		}
		if (tax==null)
			tax = new TaxonomyStatus(notes,Status.UNKNOWN);
		return tax;
	}


	/**
	 * get accepted tsd by tsn
	 */
	private String getAcceptedTsn(String tsn, ConnectionPool pool, Connection con) {

		String acceptTsn = "";

		ResultSet results = null;
		try {
			if (pool == null) pool = ConnectionPool.getConnectionPool();
			if (con==null) con = pool.getConnection();

			String query = "select tsn_accepted from synonym_links where tsn = ?";
			results =  pool.selectPrestatement(query, tsn);	

			if(results.next()){	
				acceptTsn = results.getString(1);
			}

		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);
		} catch (SQLException e) {
			logger.error("SQLException", e);
		} finally {
			try {
				if (results != null) 
					results.close();
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
			if ((pool!=null) && (con!=null))
				pool.releaseConnection(con);
		}		
		return acceptTsn;
	}


	@Override
	public TaxonomyItem retrieveTaxonById(String id) {
		TaxonomyItem tax = null;
		ResultSet result = null;
		ConnectionPool pool = null;
		Connection con = null;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();

			String query = "select a.tsn, a.completename, b.taxon_author_id, b.rank_id, b.update_date, b.name_usage, b.unaccept_reason, b.parent_tsn from longnames as a NATURAL join taxonomic_units as b where a.tsn = ?";
			result =  pool.selectPrestatement(query, id);	



			if (result!=null){
				//				RSItem item = null;
				if(result.next()){	
					try{
						tax = createTaxItem(result.getString(1),result.getString(2),result.getString(3),
								result.getString(4),result.getString(5),result.getString(6),
								result.getString(7),result.getString(8), null, null, null, null, null, true);
					}catch (Exception e) {
						// TODO: handle exception
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException Error", e);
		}finally{

			try {
				if (result != null) 
					result.close();
			} catch (SQLException e) {
				logger.error("SQLException", e);
			}

			if ((pool!=null) && (con!=null)){
				pool.releaseConnection(con);
			}
		}

		return tax;
	}




	@Override
	public List<TaxonomyItem> retrieveTaxonChildrenByTaxonId(String id_parent)
			throws IdNotValidException, ExternalRepositoryException {
		List<TaxonomyItem> itemList = null;
		ResultSet results = null;
		ResultSet rs = null;
		ConnectionPool pool = null;
		Connection con = null;
		try{
			pool = ConnectionPool.getConnectionPool();
			con = pool.getConnection();
			String query = "select a.tsn, b.unit_name1, b.unit_name2, b.unit_name3, b.unit_name4, b.taxon_author_id, b.rank_id, a.name_usage, a.unaccept_reason from taxonomic_units as a NATURAL join taxonomic_units as b where b.unaccept_reason ='' and a.parent_tsn = ? order by b.unit_name1";
			results =  pool.selectPrestatement(query, id_parent);	

			if (results!=null){
				itemList = new ArrayList<TaxonomyItem>(); 
				while(results.next()) {	
					try{
						TaxonomyItem tax = createTaxItem(results.getString(1), null, results.getString(6), 
								results.getString(7), null, results.getString(8), results.getString(9), null, null, 
								results.getString(2), results.getString(3), results.getString(4), results.getString(5), 
								false); 
						itemList.add(tax);
					}catch (Exception e) {
						logger.error("Exception", e);
					}
				}
			}
		} catch (SQLException e) {
			logger.error("SQL Error", e);
		} catch (ConnectionPoolException e) {
			logger.error("ConnectionPoolException", e);			
		}finally{
			try {
				if (results != null) {
					results.close();
				}
				if (rs != null) {
					rs.close();
				}
				if ((pool!=null) && (con!=null)){
					pool.releaseConnection(con);
				}
			} catch (SQLException ex) {
				logger.error("SQL Error", ex);
			}
		}
		return itemList;

	}


}
