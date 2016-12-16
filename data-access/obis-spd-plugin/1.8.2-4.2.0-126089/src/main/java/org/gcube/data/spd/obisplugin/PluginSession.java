/**
 * 
 */
package org.gcube.data.spd.obisplugin;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import java.util.List;

import org.gcube.data.spd.model.Condition;
import org.gcube.data.spd.model.Coordinate;
import org.gcube.data.spd.obisplugin.data.SearchFilters;
import org.gcube.data.spd.obisplugin.pool.DatabaseCredential;

/**
 * @author "Federico De Faveri defaveri@isti.cnr.it"
 *
 */
public class PluginSession {

	protected static final String SCHEMA = "obis";

	protected DatabaseCredential databaseCredential;
	protected Connection connection;
	protected PreparedStatement taxonPreparedStatement;
	protected PreparedStatement taxonCommonNamePreparedStatement;
	protected PreparedStatement datasetPreparedStatement;
	protected PreparedStatement commonNameFromScientificNamePreparedStatement;
	protected PreparedStatement scientificNameFromCommonNamePreparedStatement;
	protected PreparedStatement searchCommonNamePreparedStatement;
	protected PreparedStatement searchScientificNamePreparedStatement;
	protected PreparedStatement scientificNamePreparedStatement;
	protected PreparedStatement commonNamePreparedStatement;
	protected PreparedStatement childrenTaxonPreparedStatement;
	protected PreparedStatement occurrenceByIdPreparedStatement;
	protected PreparedStatement datasetPerIdPreparedStatement;

	/**
	 * @param connection
	 */
	public PluginSession(Connection connection) {
		this(null,connection);
	}
	/**
	 * @param credentialToken
	 * @param connection
	 */
	public PluginSession(DatabaseCredential databaseCredential, Connection connection) {
		this.databaseCredential = databaseCredential;
		this.connection = connection;
	}

	/**
	 * @return the connection
	 */
	public Connection getConnection() {
		return connection;
	}

	/**
	 * The session is valid if the connection is OK and if the connection has been created using the same credentials.
	 * @param credentialToken
	 * @return
	 * @throws SQLException
	 */
	public boolean isValid(DatabaseCredential databaseCredential) throws SQLException
	{
		return (this.databaseCredential!=null?(this.databaseCredential.equals(databaseCredential)):true) && !connection.isClosed() && isValid();
	}

	protected boolean isValid()
	{
		try{
			ResultSet result = connection.createStatement().executeQuery("SELECT 1");
			result.close();
		}catch (Exception e) {
			return false;
		}
		return true;
	}

	public void expire() throws SQLException
	{
		connection.close();
	}

	public void preCacheStatements() throws SQLException
	{
		createTaxonPreparedStatement();
		createTaxonCommonNamePreparedStatemen();
		createDatasetPreparedStatement();
		createCommonNameFromScientificNamePreparedStatement();
		createScientificNameFromCommonNamePreparedStatement();
		createSearchCommonNamePreparedStatement();
		createSearchScientificNamePreparedStatement();
		createScientificNamePreparedStatement();
		createCommonNamePreparedStatement();
		createChildrenTaxonPreparedStatement();
		createOccurrenceByIdPreparedStatement();
		createDatasetPerDatasetIdPreparedStatement();
	}


	public PreparedStatement getTaxonPreparedStatement(int id) throws SQLException
	{
		if (taxonPreparedStatement==null) createTaxonPreparedStatement();
		taxonPreparedStatement.clearParameters();
		taxonPreparedStatement.setInt(1, id);
		return taxonPreparedStatement;
	}

	protected void createTaxonPreparedStatement() throws SQLException
	{
		taxonPreparedStatement = connection.prepareStatement("SELECT t.tname, t.id, t.parent_id, t.tauthor, t.worms_id, t.col_id, t.irmng_id, t.itis_id, r.rank_name " +
				"FROM "+SCHEMA+".tnames t " +
				"LEFT JOIN "+SCHEMA+".ranks r ON t.rank_id = r.rank_id and r.kingdom_id = CASE WHEN t.rank_id = 10 THEN 738303 ELSE (string_to_array(storedpath, 'x')::text[])[3]::int END "+
				"WHERE t.id = ?");
	}


	public PreparedStatement getTaxonCommonNamePreparedStatement(int taxonNameId) throws SQLException
	{
		if (taxonCommonNamePreparedStatement==null) createTaxonCommonNamePreparedStatemen();
		taxonCommonNamePreparedStatement.clearParameters();
		taxonCommonNamePreparedStatement.setInt(1, taxonNameId);
		return taxonCommonNamePreparedStatement;
	}

	protected void createTaxonCommonNamePreparedStatemen() throws SQLException
	{
		taxonCommonNamePreparedStatement = connection.prepareStatement("select c.cname, l.lanname FROM "+SCHEMA+".cnames c, "+SCHEMA+".languages l WHERE c.tname_id = ? AND c.language_id = l.id");
	}

	public PreparedStatement getDatasetPreparedStatement(int id) throws SQLException
	{
		if (datasetPreparedStatement==null) createDatasetPreparedStatement();
		datasetPreparedStatement.clearParameters();
		datasetPreparedStatement.setInt(1, id);
		return datasetPreparedStatement;
	}
	
	public PreparedStatement getDatasetPerDatasetIdPreparedStatement(int dataSetId) throws SQLException
	{
		if (datasetPerIdPreparedStatement==null) createDatasetPerDatasetIdPreparedStatement();
		datasetPerIdPreparedStatement.clearParameters();
		datasetPerIdPreparedStatement.setInt(1, dataSetId);
		return datasetPerIdPreparedStatement;
	}
	
	public PreparedStatement getCommonNameFromScientificNamePreparedStatement(String scientificaName) throws SQLException
	{
		if (commonNameFromScientificNamePreparedStatement==null) createCommonNameFromScientificNamePreparedStatement();
		commonNameFromScientificNamePreparedStatement.clearParameters();
		commonNameFromScientificNamePreparedStatement.setString(1, scientificaName);
		return commonNameFromScientificNamePreparedStatement;
	}

	protected void createCommonNameFromScientificNamePreparedStatement() throws SQLException
	{
		commonNameFromScientificNamePreparedStatement = connection.prepareStatement("SELECT c.cname FROM "+SCHEMA+".cnames c, "+SCHEMA+".tnames t WHERE t.tname ILIKE ? AND c.tname_id = t.id");
	}

	public PreparedStatement getScientificNameFromCommonNamePreparedStatement(String commonName) throws SQLException
	{
		if (scientificNameFromCommonNamePreparedStatement==null) createScientificNameFromCommonNamePreparedStatement();
		scientificNameFromCommonNamePreparedStatement.clearParameters();
		scientificNameFromCommonNamePreparedStatement.setString(1, commonName);
		return scientificNameFromCommonNamePreparedStatement;
	}

	protected void createScientificNameFromCommonNamePreparedStatement() throws SQLException
	{
		scientificNameFromCommonNamePreparedStatement = connection.prepareStatement("SELECT DISTINCT t.tname FROM "+SCHEMA+".cnames c, "+SCHEMA+".tnames t WHERE c.cname ILIKE ? AND c.tname_id = t.id");
	}

	public PreparedStatement getSearchCommonNamePreparedStatement(String searchTerm) throws SQLException
	{
		if (searchCommonNamePreparedStatement == null) createSearchCommonNamePreparedStatement();
		searchCommonNamePreparedStatement.clearParameters();
		searchCommonNamePreparedStatement.setString(1, "%"+searchTerm+"%");
		return searchCommonNamePreparedStatement;
	}

	protected void createSearchCommonNamePreparedStatement() throws SQLException
	{
		String query = "SELECT DISTINCT c.tname_id AS id FROM obis.cnames c WHERE c.cname ILIKE ?";
		searchCommonNamePreparedStatement = connection.prepareStatement(query);
	}

	public PreparedStatement getSearchScientificNamePreparedStatement(String searchTerm) throws SQLException
	{
		if (searchScientificNamePreparedStatement == null) createSearchScientificNamePreparedStatement();
		searchScientificNamePreparedStatement.clearParameters();
		searchScientificNamePreparedStatement.setString(1, "%"+searchTerm+"%");
		return searchScientificNamePreparedStatement;
	}

	protected void createSearchScientificNamePreparedStatement() throws SQLException
	{
		String query = "SELECT t.id as id FROM obis.tnames t WHERE t.tname ILIKE ? AND exists (SELECT 1 FROM obis.drs WHERE valid_id = t.id)";
		searchScientificNamePreparedStatement = connection.prepareStatement(query);	
	}

	public PreparedStatement getOccurrencesCountPreparedStatement(int taxonId, int datasetId, SearchFilters filters) throws SQLException{

		//We don't cache it because in this case a PS is less performant
		StringBuilder query = new StringBuilder("SELECT count(*) AS occurrences FROM "+SCHEMA+".drs WHERE valid_id = ? AND resource_id = ?");

		buildConditions(query, "", filters.getConditions());

		/*if (filters.getUpperBound()!=null) query.append(" AND latitude <= ? AND longitude <= ?");
		if (filters.getLowerBound()!=null) query.append(" AND latitude >= ? AND longitude >= ?");
		if (filters.getFromDate()!=null) query.append(" AND datecollected >= ?");
		if (filters.getToDate()!=null) query.append(" AND datecollected <= ?");*/

		PreparedStatement occurrencesCountPreparedStatement = connection.prepareStatement(query.toString());

		int parameterCounter = 1;
		occurrencesCountPreparedStatement.setInt(parameterCounter++, taxonId);
		occurrencesCountPreparedStatement.setInt(parameterCounter++, datasetId);
		
		addParameters(occurrencesCountPreparedStatement, parameterCounter, filters.getConditions());
		
		/*for (Condition condition:filters.getConditions()) {
			switch (condition.getProperty()) {
				case COORDINATE: {
					Coordinate coordinate = (Coordinate) condition.getValue();
					occurrencesCountPreparedStatement.setDouble(parameterCounter++, coordinate.getLatitude());
					occurrencesCountPreparedStatement.setDouble(parameterCounter++, coordinate.getLongitude());
				} break;
				case EVENT_DATE: {
					Calendar calendar = (Calendar) condition.getValue();
					occurrencesCountPreparedStatement.setDate(parameterCounter++, new Date(calendar.getTimeInMillis()));
				}
			}
		}*/

		/*if (filters.getUpperBound() != null) {
			occurrencesCountPreparedStatement.setDouble(parameterCounter++, filters.getUpperBound().getLatitude());
			occurrencesCountPreparedStatement.setDouble(parameterCounter++, filters.getUpperBound().getLongitude());
		}

		if (filters.getLowerBound() != null) {
			occurrencesCountPreparedStatement.setDouble(parameterCounter++, filters.getLowerBound().getLatitude());
			occurrencesCountPreparedStatement.setDouble(parameterCounter++, filters.getLowerBound().getLongitude());
		}

		if (filters.getFromDate() != null) occurrencesCountPreparedStatement.setDate(parameterCounter++, new Date(filters.getFromDate().getTimeInMillis()));
		if (filters.getToDate() != null) occurrencesCountPreparedStatement.setDate(parameterCounter++, new Date(filters.getToDate().getTimeInMillis()));*/

		return occurrencesCountPreparedStatement;
	}

	protected void buildConditions(StringBuilder query, String prefix, List<Condition> conditions)
	{
		for (Condition condition:conditions) buildCondition(query, prefix, condition);
	}

	protected void buildCondition(StringBuilder query, String prefix, Condition condition)
	{
		String op = "";
		switch (condition.getOp()) {
			case EQ: op = "=="; break;
			case GE: op = ">="; break;
			case GT: op = ">"; break;
			case LE: op = "<="; break;
			case LT: op = "<"; break;
		}

		switch (condition.getType()) {
			case COORDINATE: {
				query.append(" AND ");
				query.append(prefix);
				query.append("latitude ");
				query.append(op);
				query.append(" ? AND ");
				query.append(prefix);
				query.append("longitude ");
				query.append(op);
				query.append(" ?");
			} break;
			case DATE: {
				query.append(" AND ");
				query.append(prefix);
				query.append("datecollected ");
				query.append(op);
				query.append(" ?");
			} break;
		}
	}
	
	protected void addParameters(PreparedStatement preparedStatement, int parameterCounter, List<Condition> conditions) throws SQLException {
		for (Condition condition:conditions) {
			switch (condition.getType()) {
				case COORDINATE: {
					Coordinate coordinate = (Coordinate) condition.getValue();
					preparedStatement.setDouble(parameterCounter++, coordinate.getLatitude());
					preparedStatement.setDouble(parameterCounter++, coordinate.getLongitude());
				} break;
				case DATE: {
					Calendar calendar = (Calendar) condition.getValue();
					preparedStatement.setDate(parameterCounter++, new Date(calendar.getTimeInMillis()));
				}
			}
		}
	}

	protected PreparedStatement getOccurrencesPreparedStatement(int taxonId, int datasetId, SearchFilters filters) throws SQLException
	{
		//We don't cache it because in this case a PS is less performant
		StringBuilder query = new StringBuilder("SELECT drs.id, drs.valid_id, drs.latitude, drs.longitude, drs.datecollected, drs.basisofrecord, dxs.citation, dxs.institutioncode, dxs.collectioncode, dxs.catalognumber, dxs.collector, dxs.datelastmodified, dxs.country, dxs.locality, dxs.minimumdepth, dxs.maximumdepth, dxs.coordinateprecision, dxs.concatenated, dxs.identifiedBy, dxs.yearcollected, dxs.monthcollected, dxs.daycollected, tn.tauthor as snAuthor FROM obis.drs drs, obis.dxs dxs, obis.tnames tn WHERE drs.valid_id = ? AND drs.resource_id = ? AND drs.id = dxs.dr_id AND tn.id = drs.valid_id ");

		buildConditions(query, "drs.", filters.getConditions());
		
		/*if (filters.getUpperBound() != null) query.append(" AND drs.latitude <= ? AND drs.longitude <= ?");
		if (filters.getLowerBound() != null) query.append(" AND drs.latitude >= ? AND drs.longitude >= ?");
		if (filters.getFromDate() != null) query.append(" AND drs.datecollected >= ?");
		if (filters.getToDate() != null) query.append(" AND drs.datecollected <= ?");*/

		PreparedStatement occurrencesPreparedStatement = connection.prepareStatement(query.toString());

		int parameterCounter = 1;
		occurrencesPreparedStatement.setInt(parameterCounter++, taxonId);
		occurrencesPreparedStatement.setInt(parameterCounter++, datasetId);
		
		addParameters(occurrencesPreparedStatement, parameterCounter, filters.getConditions());

		/*if (filters.getUpperBound() != null) {
			occurrencesPreparedStatement.setDouble(parameterCounter++, filters.getUpperBound().getLatitude());
			occurrencesPreparedStatement.setDouble(parameterCounter++, filters.getUpperBound().getLongitude());
		}

		if (filters.getLowerBound() != null) {
			occurrencesPreparedStatement.setDouble(parameterCounter++, filters.getLowerBound().getLatitude());
			occurrencesPreparedStatement.setDouble(parameterCounter++, filters.getLowerBound().getLongitude());
		}

		if (filters.getFromDate() != null) occurrencesPreparedStatement.setDate(parameterCounter++, new Date(filters.getFromDate().getTimeInMillis()));
		if (filters.getToDate() != null) occurrencesPreparedStatement.setDate(parameterCounter++, new Date(filters.getToDate().getTimeInMillis()));*/

		return occurrencesPreparedStatement;
	}

	public PreparedStatement getScientificNamePreparedStatement(String scientificName) throws SQLException
	{
		if (scientificNamePreparedStatement == null) createScientificNamePreparedStatement();
		scientificNamePreparedStatement.clearParameters();
		scientificNamePreparedStatement.setString(1, "%"+scientificName+"%");
		return scientificNamePreparedStatement;
	}

	protected void createScientificNamePreparedStatement() throws SQLException
	{
		String query = "SELECT t.id as id FROM obis.tnames t WHERE t.tname ILIKE ?";
		scientificNamePreparedStatement = connection.prepareStatement(query);	
	}

	public PreparedStatement getCommonNamePreparedStatement(String commonName) throws SQLException
	{
		if (commonNamePreparedStatement==null) createCommonNamePreparedStatement();
		commonNamePreparedStatement.clearParameters();
		commonNamePreparedStatement.setString(1, commonName);
		return commonNamePreparedStatement;
	}

	protected void createCommonNamePreparedStatement() throws SQLException
	{
		commonNamePreparedStatement = connection.prepareStatement("SELECT c.tname_id as id FROM "+SCHEMA+".cnames c WHERE c.cname LIKE ?");
	}

	public PreparedStatement getChildrenTaxonPreparedStatement(int id) throws SQLException
	{
		if (childrenTaxonPreparedStatement==null) createChildrenTaxonPreparedStatement();
		childrenTaxonPreparedStatement.clearParameters();
		childrenTaxonPreparedStatement.setInt(1, id);
		return childrenTaxonPreparedStatement;
	}

	protected void createChildrenTaxonPreparedStatement() throws SQLException
	{
		childrenTaxonPreparedStatement = connection.prepareStatement("SELECT t.tname, t.id as id, t.parent_id, t.tauthor, r.rank_name " +
				"FROM "+SCHEMA+".tnames t " +
				"LEFT JOIN "+SCHEMA+".ranks r ON t.rank_id = r.rank_id and r.kingdom_id = CASE WHEN t.rank_id = 10 THEN 738303 ELSE (string_to_array(storedpath, 'x')::text[])[3]::int END "+
				"WHERE t.parent_id = ?");
	}

	protected PreparedStatement getOccurrenceByIdPreparedStatement(int occurrenceId) throws SQLException
	{
		if (occurrenceByIdPreparedStatement==null) createOccurrenceByIdPreparedStatement();
		occurrenceByIdPreparedStatement.clearParameters();
		occurrenceByIdPreparedStatement.setInt(1, occurrenceId);
		return occurrenceByIdPreparedStatement;
	}

	protected void createOccurrenceByIdPreparedStatement() throws SQLException
	{
		String query = "SELECT drs.id, drs.latitude, drs.longitude, drs.datecollected, drs.basisofrecord, dxs.citation, dxs.institutioncode, dxs.collectioncode, dxs.catalognumber, dxs.collector, dxs.datelastmodified, dxs.country, dxs.locality, dxs.minimumdepth, dxs.maximumdepth, dxs.coordinateprecision, dxs.concatenated, dxs.yearcollected, dxs.monthcollected, dxs.daycollected, tn.tauthor as snAuthor FROM obis.drs drs, obis.dxs dxs, obis.tnames as tn WHERE drs.id = ? AND drs.id = dxs.dr_id AND drs.valid_id = tn.id";
		occurrenceByIdPreparedStatement = connection.prepareStatement(query);
	}

	protected void createDatasetPreparedStatement() throws SQLException
	{
		/*String query = "SELECT r.id as datasetId, r.resname as datasetName, r.citation as datasetCitation, p.id as providerId, p.providername as providerName " +
		"FROM obis.resources r, obis.providers p WHERE " +
		"exists (SELECT r.id FROM obis.drs d WHERE d.valid_id = ? AND d.resource_id = r.id) AND r.provider_id = p.id";*/

		String query = "SELECT r.id as datasetId, r.resname as datasetName, r.citation as datasetCitation, p.id as providerId, p.providername as providerName " +
				"FROM obis.resources r, obis.providers p WHERE " +
				"r.id in (SELECT resource_id from portal.species_per_resource where valid_id = ?) AND r.provider_id = p.id";
		datasetPreparedStatement = connection.prepareStatement(query);
	}

	protected void createDatasetPerDatasetIdPreparedStatement() throws SQLException
	{
		/*String query = "SELECT r.id as datasetId, r.resname as datasetName, r.citation as datasetCitation, p.id as providerId, p.providername as providerName " +
		"FROM obis.resources r, obis.providers p WHERE " +
		"exists (SELECT r.id FROM obis.drs d WHERE d.valid_id = ? AND d.resource_id = r.id) AND r.provider_id = p.id";*/

		String query = "SELECT r.id as datasetId, r.resname as datasetName, r.citation as datasetCitation, p.id as providerId, p.providername as providerName " +
				"FROM obis.resources r, obis.providers p WHERE " +
				"r.id  = ? AND r.provider_id = p.id";
		datasetPerIdPreparedStatement = connection.prepareStatement(query);
	}

}
