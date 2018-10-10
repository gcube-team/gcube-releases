package org.gcube.portal.trainingmodule;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portal.trainingmodule.dao.ProgressPerUnit;
import org.gcube.portal.trainingmodule.dao.TrainingProject;
import org.gcube.portal.trainingmodule.dao.TrainingUnitQuestionnaire;
import org.gcube.portal.trainingmodule.dao.TrainingVideo;
import org.gcube.portal.trainingmodule.database.EntityManagerFactoryCreator;
import org.gcube.portal.trainingmodule.database.ServerParameters;
import org.gcube.portal.trainingmodule.shared.ItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



// TODO: Auto-generated Javadoc
/**
 * The Class TrainingProjectJDBCManager.
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Jan 31, 2018
 */
public class TrainingProjectJDBCManager {
	
	
	/** The Constant TRAININGUNITQUESTIONNAIRE. */
	public static final String TRAININGUNITQUESTIONNAIRE = "trainingunitquestionnaire";

	/** The Constant TRAININGUNIT_TRAININGUNITQUESTIONNAIRE. */
	public static final String TRAININGUNIT_TRAININGUNITQUESTIONNAIRE = "trainingunit_trainingunitquestionnaire";

	/** The Constant TRAININGUNIT_TRAININGVIDEO. */
	public static final String TRAININGUNIT_TRAININGVIDEO = "trainingunit_trainingvideo";
	
	/** The Constant TRAININGVIDEO. */
	public static final String TRAININGVIDEO = "trainingvideo";
	/** The Constant logger. */
	public static final Logger logger = LoggerFactory.getLogger(TrainingProjectJDBCManager.class);

	

	/**
	 * Select training project.
	 *
	 * @param context the context
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	protected List<TrainingProject> selectTrainingProject(String context) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		try {
			dbConnection = getDBConnection(context);
			String selectSQL = "SELECT internalid,commitment,courseactive,createdby,description,languages,ownerlogin,scope,sharedwith,title,workspacefolderid,workspacefoldername FROM "+TrainingProject.class.getSimpleName().toLowerCase()+" WHERE scope = ?";
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setString(1, context);

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();
			
//			ResultSetMetaData rsmd = rs.getMetaData();		
//			for (int i = 0; i < rsmd.getColumnCount(); i++) {
//				 String name = rsmd.getColumnName(i+1);
//				 System.out.println("Col "+i+") "+name);
//			}
			
			List<TrainingProject> listP = new ArrayList<>();
			
			while (rs.next()) {

				long internalid = rs.getLong("internalid");
				String title = rs.getString("title");
				String commitment = rs.getString("commitment");
				boolean isCourseActive = rs.getBoolean("courseactive");
				String createdBy = rs.getString("createdby");
				String description = rs.getString("description");
				String languages = rs.getString("languages");
				String ownerLogin = rs.getString("ownerlogin");
				String scope = rs.getString("scope");
				String sharedWith = rs.getString("sharedwith");
				String workspaceFolderId = rs.getString("workspacefolderid");
				String workspaceFolderName = rs.getString("workspacefoldername");
				listP.add(new TrainingProject(internalid, title, description, commitment, languages, scope, ownerLogin, workspaceFolderId, workspaceFolderName, createdBy, isCourseActive, sharedWith));
			}
			
			return listP;
			

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return null;

	}
	
	
	
	/**
	 * Select training video.
	 *
	 * @param unitId the unit id
	 * @param videoId the video id
	 * @param context the context
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	protected List<TrainingVideo> selectTrainingVideo(Long unitId, Long videoId, String context) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		try {
			dbConnection = getDBConnection(context);
			String selectSQL = "SELECT internalid,description,title,url"
					+ " FROM "+TRAININGVIDEO+" v INNER JOIN "+TRAININGUNIT_TRAININGVIDEO+" e ON v.internalid = e.listvideo_internalid"
					+ " WHERE 1=1";
			
			boolean isValidUnit = (unitId!=null && unitId>-1)?true:false;
			boolean isValidVideo = (videoId!=null && videoId>-1)?true:false;
			
			if(isValidUnit) {
				selectSQL+= " AND e.trainingunit_internalid =  ?";
			}
			
			if(isValidVideo) {
				selectSQL+= " AND e.internalid =  ?";
			}
			
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			
			int i=1;
			
			if(isValidUnit) {
				preparedStatement.setLong(i++, unitId);
			}
			
			if(isValidVideo) {
				preparedStatement.setLong(i++, videoId);
			}
			
//			+ " WHERE e.trainingunit_internalid =  ?";

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			List<TrainingVideo> listP = new ArrayList<TrainingVideo>();
			
			while (rs.next()) {

				long internalid = rs.getLong("internalid");
				String title = rs.getString("title");
				String description = rs.getString("description");
				String url = rs.getString("url");
				listP.add(new TrainingVideo(internalid, title, description, url));
			}
			
			return listP;
			

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return null;

	}
	

	/**
	 * Delete training video.
	 *
	 * @param videoId the video id
	 * @param context the context
	 * @return the int
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	protected int deleteTrainingVideo(Long videoId, String context) throws SQLException, Exception {
		logger.info("Deleting video for id: "+videoId);
		
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatement2 = null;
		try {
			
			boolean isValidVideo = (videoId!=null && videoId>-1)?true:false;
			
			if(!isValidVideo) {
				throw new Exception("Error on parameter:  videoId "+videoId+" is not valid");
			}
			
			dbConnection = getDBConnection(context);
			dbConnection.setAutoCommit(false);
		
			String deleteSQL = "DELETE FROM "+TRAININGUNIT_TRAININGVIDEO+" WHERE listvideo_internalid = ?";
			logger.trace("Performing SQL: "+deleteSQL);
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			preparedStatement.setLong(1, videoId);
			// execute select SQL stetement
			int updated = preparedStatement.executeUpdate();
			
			logger.info("Deleted: "+updated +" row from table " +TRAININGUNIT_TRAININGVIDEO);
			
			deleteSQL = "DELETE FROM "+TRAININGVIDEO+" WHERE internalid = ?";
			logger.trace("Performing SQL: "+deleteSQL);
			preparedStatement2 = dbConnection.prepareStatement(deleteSQL);
			preparedStatement2.setLong(1, videoId);
			updated = preparedStatement2.executeUpdate();
			
			logger.info("Deleted: "+updated +" row from table " +TRAININGVIDEO);
			
			//DELETING FROM TABLE: "trainingunit_trainingvideo"
			dbConnection.commit();
			
			return updated;
		
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			dbConnection.rollback();

		} finally {

			if (preparedStatement != null)
				preparedStatement.close();
				
			if(preparedStatement2 != null)
				preparedStatement2.close();

			if (dbConnection != null) {
				dbConnection.setAutoCommit(true);
				dbConnection.close();
			}

		}
		return -1;

	}
	
	
	/**
	 * Delete questionnaire for id.
	 *
	 * @param questionnaireId the questionnaire id
	 * @param context the context
	 * @return the int
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	protected int deleteQuestionnaireForId(Long questionnaireId, String context) throws SQLException, Exception {
		logger.info("Deleting questionnaire for id: "+questionnaireId);
		
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		PreparedStatement preparedStatement2 = null;
		try {
			
			boolean isValidQuestionnaire = (questionnaireId!=null && questionnaireId>-1)?true:false;
			
			if(!isValidQuestionnaire) {
				throw new Exception("Error on parameter:  questionnaireId "+questionnaireId+" is not valid");
			}
			
			dbConnection = getDBConnection(context);
			dbConnection.setAutoCommit(false);
		
			String deleteSQL = "DELETE FROM "+TRAININGUNIT_TRAININGUNITQUESTIONNAIRE+" WHERE listquestionnaire_internalid = ?";
			logger.trace("Performing SQL: "+deleteSQL);
			preparedStatement = dbConnection.prepareStatement(deleteSQL);
			preparedStatement.setLong(1, questionnaireId);
			// execute select SQL stetement
			int updated = preparedStatement.executeUpdate();
			
			logger.info("Deleted: "+updated +" row from table " +TRAININGUNIT_TRAININGUNITQUESTIONNAIRE);
			
			deleteSQL = "DELETE FROM "+TRAININGUNITQUESTIONNAIRE+" WHERE internalid = ?";
			logger.trace("Performing SQL: "+deleteSQL);
			preparedStatement2 = dbConnection.prepareStatement(deleteSQL);
			preparedStatement2.setLong(1, questionnaireId);
			updated = preparedStatement2.executeUpdate();
			
			logger.info("Deleted: "+updated +" row from table " +TRAININGUNITQUESTIONNAIRE);
			
			dbConnection.commit();
			
			return updated;
			
		} catch (SQLException e) {
			logger.error(e.getMessage(), e);
			dbConnection.rollback();

		} finally {

			if (preparedStatement != null)
				preparedStatement.close();
				
			if(preparedStatement2 != null)
				preparedStatement2.close();

			if (dbConnection != null) {
				dbConnection.setAutoCommit(true);
				dbConnection.close();
			}

		}
		return -1;

	}
	

	/**
	 * Select training questionnaire.
	 *
	 * @param unitId the unit id
	 * @param context the context
	 * @return the list
	 * @throws SQLException the SQL exception
	 */
	protected List<TrainingUnitQuestionnaire> selectTrainingQuestionnaire(long unitId, String context) throws SQLException {

		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;

		try {
			dbConnection = getDBConnection(context);
			String selectSQL = "SELECT internalid,description,questionnaireid,questionnaireurl,title"
					+ " FROM "+TRAININGUNITQUESTIONNAIRE+" q INNER JOIN "+TRAININGUNIT_TRAININGUNITQUESTIONNAIRE+" e ON q.internalid = e.listquestionnaire_internalid"
					+ " WHERE e.trainingunit_internalid = ?";
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			preparedStatement.setLong(1, unitId);

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			List<TrainingUnitQuestionnaire> listP = new ArrayList<TrainingUnitQuestionnaire>();
			
			while (rs.next()) {

				long internalId = rs.getLong("internalid");
				String title = rs.getString("title");
				String description = rs.getString("description");
				String questionnaireId = rs.getString("questionnaireid");
				String questionnaireURL = rs.getString("questionnaireurl");
				listP.add(new TrainingUnitQuestionnaire(internalId, title, description, questionnaireId, questionnaireURL));
			}
			
			return listP;
			

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return null;

	}

	

	/**
	 * Select training unit progress.
	 *
	 * @param context the context
	 * @param unitId the unit id
	 * @param username the username
	 * @param itemId the item id
	 * @param itemType the item type
	 * @return the list
	 * @throws SQLException the SQL exception
	 * @throws Exception the exception
	 */
	public List<ProgressPerUnit>  selectTrainingUnitProgress(String context, long unitId, String username, String itemId, ItemType itemType) throws SQLException, Exception {
		Connection dbConnection = null;
		PreparedStatement preparedStatement = null;
		
		if(unitId<-1)
			throw new Exception("Error: Unit Id must be greater than -1");

		try {
			dbConnection = getDBConnection(context);
			
			String selectSQL = "SELECT internalid,itemid,read,type,unitid,username"
					+ " FROM progressperunit p"
					+ " WHERE p.unitid = ?";
			
			
			boolean isValidUsername = username!=null?true:false;
			boolean isValidItemId = itemId!=null?true:false;
			boolean isValidItemType = itemType!=null?true:false;
			

			if(isValidUsername) {
				selectSQL +=" AND p.username = ?";
			}
			
			if(isValidItemId) {
				selectSQL +=" AND p.itemid = ?";
			}
			
			if(isValidItemType) {
				selectSQL +=" AND p.type = ?";
			}
			
			preparedStatement = dbConnection.prepareStatement(selectSQL);
			int i = 1;
			preparedStatement.setLong(i++, unitId);
			
			if(isValidUsername) {
				preparedStatement.setString(i++, username);
			}
			
			if(isValidItemId) {
				preparedStatement.setString(i++, itemId);
			}
			
			if(isValidItemType) {
				preparedStatement.setString(i++, itemType.toString());
			}

			// execute select SQL stetement
			ResultSet rs = preparedStatement.executeQuery();

			List<ProgressPerUnit> listP = new ArrayList<ProgressPerUnit>();
			
			while (rs.next()) {

				long theInternalId = rs.getLong("internalid");
//				internalid,itemid,read,type,unitid,username
				long theUnitId = rs.getLong("unitid");
				String theItemId = rs.getString("itemid");
				String theUsername = rs.getString("username");
				String theType = rs.getString("type");
				boolean isRead = rs.getBoolean("read");
				ItemType toType = null;
				if(theType!=null) {
					try {
						toType = ItemType.valueOf(theType);
					}catch (Exception e) {
						// silent
					}
				}
				
				listP.add(new ProgressPerUnit(theInternalId, theUnitId, theUsername, toType, theItemId, isRead));
			}
			
			return listP;
			

		} catch (SQLException e) {
			logger.error(e.getMessage(), e);

		} finally {

			if (preparedStatement != null) {
				preparedStatement.close();
			}

			if (dbConnection != null) {
				dbConnection.close();
			}

		}
		return null;
		
	}

	/**
	 * Gets the DB connection.
	 *
	 * @param context the context
	 * @return the DB connection
	 */
	private static Connection getDBConnection(String context) {

		Connection connection = null;

		//System.out.println("-------- PostgreSQL " + "JDBC Connection Testing ------------");

		try {

			Class.forName("org.postgresql.Driver");

		} catch (ClassNotFoundException e) {

			logger.error("Where is your PostgreSQL JDBC Driver? " + "Include in your library path!");
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("PostgreSQL JDBC Driver Registered!");

		try {

			ServerParameters dbparams = EntityManagerFactoryCreator.getDBParameters(context);
			connection = DriverManager.getConnection(dbparams.getUrl(), dbparams.getUser(), dbparams.getPassword());

		} catch (SQLException e) {

			logger.error("Connection Failed! Check output console");
			e.printStackTrace();

		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		if (connection != null) {
			logger.info("You made it, take control your database now!");
		} else {
			logger.warn("Failed to make connection!");
		}

		return connection;

	}



	
//	public static void main(String[] argv) {
//		try {
//			//getDBConnection(context);
//			String selectSQL = "SELECT internalid,commitment,courseactive,createdby,description,languages,ownerlogin,scope,sharedwith,title,workspacefolderid,workspacefoldername FROM "+tableName +" WHERE scope = ?";
//			
//			List<TrainingProject> listP = selectRecordsFromTable(selectSQL, context);
//
//			System.out.println("Training Project are: "+listP.size());
//			for (TrainingProject trainingProject : listP) {
//				System.out.println("Id: "+trainingProject.getInternalId() + ", title: "+trainingProject.getTitle());
//			}
//			
//		} catch (SQLException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//	}

}
