package org.gcube.data.analysis.statisticalmanager.dataspace.importer;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.UUID;

import org.gcube.data.analysis.statisticalmanager.persistence.DataBaseManager;
import org.gcube.data.spd.model.products.OccurrencePoint;
import org.gcube.data.streams.Stream;
import org.gcube.dataanalysis.ecoengine.datatypes.enumtypes.TableTemplates;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class OccurrenceStreamConverter {

	public String getTableName() {
		return tableName;
	}

	private DataBaseManager dataSource;
	private Stream<OccurrencePoint> stream;
	private String tableName;
	 
	private static Logger logger = LoggerFactory.getLogger(OccurrenceStreamConverter.class);

	public OccurrenceStreamConverter(DataBaseManager dataSource, Stream<OccurrencePoint> stream) throws Exception {

		this.stream = stream;
		this.dataSource = dataSource;
		createTable();
	}
	
	
	
	private void createTable() throws Exception {

		Connection conn = dataSource.getDataSource().getConnection();

		Statement stmt = conn.createStatement();
		String dbTableName = TableTemplates.OCCURRENCE_SPECIES.toString() +  "_ID_" + UUID.randomUUID().toString().replace("-", "_");
		dbTableName = dbTableName.toLowerCase();
		
		String sql = "CREATE TABLE " + dbTableName + 
		"(institutionCode VARCHAR,"+
		"collectionCode VARCHAR," +
		"catalogueNumber VARCHAR," +
		"dataSet VARCHAR," +
		"dataProvider VARCHAR," +
		"dataSource VARCHAR," +
		"scientificNameAuthorship VARCHAR," +
		"identifiedBy VARCHAR,"+
		"credits VARCHAR," +
		"recordedBy VARCHAR," +
		"eventDate TIMESTAMP," +
		"modified TIMESTAMP," +
		"scientificName VARCHAR," +	
		"kingdom VARCHAR," +
		"family VARCHAR," +
		"locality VARCHAR," +
		"country VARCHAR," +
		"citation VARCHAR," +
		"decimalLatitude double precision," +
		"decimalLongitude double precision," +
		"coordinateUncertaintyInMeters VARCHAR," +
		"maxDepth double precision," +
		"minDepth double precision," +
		"basisOfRecord VARCHAR)";
		try {
			stmt.executeUpdate(sql);
		} finally {
			dataSource.closeStatement(stmt);
			dataSource.closeConn(conn);
		}

		this.tableName = dbTableName;
	}

	

	public void run() throws Exception {
		
		Connection conn = null;
		PreparedStatement pstmt = null;
		try {
			
			conn = dataSource.getDataSource().getConnection();

			String sql = "insert into " + this.tableName + 
			"(institutionCode, "+
			"collectionCode, " +
			"catalogueNumber, " +
			"dataSet, " +
			"dataProvider, " +
			"dataSource, " +
			"scientificNameAuthorship," +
			"identifiedBy"+
			"credits," +
			"recordedBy, " +
			"eventDate, " +
			"modified, " +
			"scientificName, " +	
			"kingdom, " +
			"family, " +
			"locality, " +
			"country, " +
			"citation, " +
			"decimalLatitude, " +
			"decimalLongitude, " +
			"coordinateUncertaintyInMeters, " +
			"maxDepth, " +
			"minDepth, " +
			"basisOfRecord) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?,?,?)";
			
			pstmt = conn.prepareStatement(sql);
			
			
			while(stream.hasNext()) {

				logger.debug("----- Stream has element");
				OccurrencePoint occurrence = stream.next();
				logger.debug("Occurrence " + occurrence);
				
				pstmt.setString(1, occurrence.getInstitutionCode());
				pstmt.setString(2, occurrence.getCollectionCode());
				
				if (occurrence.getDataSet() != null) {
					pstmt.setString(4, occurrence.getDataSet().getName());
					if (occurrence.getDataSet().getDataProvider() != null)
						pstmt.setString(5, occurrence.getDataSet().getDataProvider().getName());
					else
						pstmt.setString(5, null);
				} else {
					pstmt.setString(4,null);
					pstmt.setString(5, null);
				}
					
				pstmt.setString(3, occurrence.getCatalogueNumber());
				
				pstmt.setString(6, occurrence.getProvider());
				
				pstmt.setString(7, occurrence.getScientificNameAuthorship());
				
				pstmt.setString(8, occurrence.getIdentifiedBy());
				pstmt.setString(9, occurrence.getCredits());
				
				pstmt.setString(10, occurrence.getRecordedBy());
				
				if (occurrence.getEventDate() != null)
					pstmt.setDate(11, new java.sql.Date(occurrence.getEventDate().getTime().getTime()));
				else
					pstmt.setDate(11, null);
				if (occurrence.getModified() != null) 
					pstmt.setDate(12, new java.sql.Date(occurrence.getModified().getTime().getTime()));
				else
					pstmt.setDate(12, null);
				
				pstmt.setString(13, occurrence.getScientificName());
				pstmt.setString(14, occurrence.getKingdom());
				pstmt.setString(15, occurrence.getFamily());
				pstmt.setString(16, occurrence.getLocality());
				pstmt.setString(17, occurrence.getCountry());
				pstmt.setString(18, occurrence.getCitation());
				pstmt.setDouble(19, occurrence.getDecimalLatitude());
				pstmt.setDouble(20, occurrence.getDecimalLongitude());
				pstmt.setString(21, occurrence.getCoordinateUncertaintyInMeters());
				pstmt.setDouble(22, occurrence.getMaxDepth());
				pstmt.setDouble(23, occurrence.getMinDepth());
				
				if (occurrence.getBasisOfRecord() != null)
					pstmt.setString(24, occurrence.getBasisOfRecord().toString());
				else 
					pstmt.setString(24, null);
				
				pstmt.executeUpdate();
			}
			
		}  finally {			
			dataSource.closeStatement(pstmt);
			dataSource.closeConn(conn);
		}

	}	
}
