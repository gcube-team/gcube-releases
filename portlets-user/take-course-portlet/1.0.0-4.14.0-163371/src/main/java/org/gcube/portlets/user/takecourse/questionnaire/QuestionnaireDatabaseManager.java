package org.gcube.portlets.user.takecourse.questionnaire;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.portal.GCubePortalConstants;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class QuestionnaireDatabaseManager {
	private static Log _log = LogFactoryUtil.getLog(QuestionnaireDatabaseManager.class);
	private static String INVITATION_TOKEN_TABLE_NAME = "invitationtoken";
	private static String SURVEY_TABLE_NAME = "survey";
	private static String ANSWERED_QUESTIONNAIRE_VALUE = "BURNED";
	/** The runtime resource name. */
	private static String RUNTIME_RESOURCE_NAME = "SurveyDB";
	/** The category name. */
	private static String CATEGORY_NAME = "Database";

	/** The jdbc URL. */
	private  String jdbcURL = null;
	private String dbUser = null;
	private String DBURL = null;
	private String DBName  = null;
	private String pwd = null; 

	private static QuestionnaireDatabaseManager INSTANCE;

	public static QuestionnaireDatabaseManager getInstance(String context) {
		if (INSTANCE == null)
			INSTANCE = new QuestionnaireDatabaseManager(context);
		return INSTANCE;
	}

	private QuestionnaireDatabaseManager(String context) {		
		//set the context
		ScopeProvider.instance.set(context);	
		AccessPoint ac = getSurveyDBAccessPoint(context);

		_log.debug("Got AccessPoint:" + ac.toString());
		String dbAddress = ac.address();
		this.DBURL = dbAddress;
		_log.debug("DB address: "+ dbAddress);
		String dbName = ac.name();
		this.DBName = dbName;
		_log.debug("DB name: "+ dbName);
		this.dbUser = ac.username();
		_log.debug("DB user: " + dbUser);

		this.jdbcURL = new StringBuffer("jdbc:postgresql://").append(dbAddress).append("/").append(dbName).toString();
		_log.debug("jdbc.url: "+jdbcURL);
		_log.debug("decrypting password ...");
		String pwd = null;
		try {
			pwd = StringEncrypter.getEncrypter().decrypt(ac.password());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		_log.debug("decrypting password OK");
		this.pwd = pwd;
	}

	/**
	 * Gets the survey DB access point.
	 *
	 * @return the survey DB access point
	 */
	private static AccessPoint getSurveyDBAccessPoint(String context) {

		//save the context for this resource
		String currContext = ScopeProvider.instance.get();
		//set the context for this resource
		ScopeProvider.instance.set("/"+PortalContext.getConfiguration().getInfrastructureName());

		//construct the xquery
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Name/text() eq '"+ RUNTIME_RESOURCE_NAME +"'");
		query.addCondition("$resource/Profile/Category/text() eq '"+ CATEGORY_NAME +"'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> conf = client.submit(query);
		ServiceEndpoint res = conf.get(0);

		//reset the context
		ScopeProvider.instance.set(currContext);	

		return res.profile().accessPoints().iterator().next();
	}


	/**
	 * update the groups for each user of the portal
	 * @throws Exception
	 */
	public QuestionnaireDTO getQuestionnaireURLForUser(long userId, String userEmailAddress, String context, String questionnaireId) throws Exception {
		String dbUrl = this.jdbcURL;
		String dbUser = this.dbUser;
		String dbPassword = this.pwd;
		Connection con = null;
		try {
			con = DriverManager.getConnection(dbUrl, dbUser, dbPassword);
			//field1 = email of the user , field3 == uuid if not answered or "BURNED" if answered
			String selectSQL = "SELECT titlesurvey, uuid, field3 FROM "+SURVEY_TABLE_NAME+", "+INVITATION_TOKEN_TABLE_NAME+" "
					+ " WHERE id_survey = ? AND field1 = ? AND id_survey = "+SURVEY_TABLE_NAME+".id ;"; 

			PreparedStatement statement = con.prepareStatement(selectSQL);
			int idSurvey = Integer.parseInt(questionnaireId);

			statement.setInt(1, idSurvey);
			statement.setString(2, userEmailAddress);						
			ResultSet rs = statement.executeQuery();
			boolean isInvited = false;
			while (rs.next()) {
				String titlesurvey = rs.getString("titlesurvey");
				boolean answered = rs.getString("field3").compareTo(ANSWERED_QUESTIONNAIRE_VALUE) == 0 ? true : false;
				String uuid = rs.getString("uuid");
				String qURL = GCubePortalConstants.QUESTIONNAIRE_TAKE_FRIENDLY_URL.substring(1)+"?UUID="+uuid;
				isInvited = true;
				return new QuestionnaireDTO(questionnaireId, titlesurvey, qURL, answered);
			}
			if (! isInvited) {
				int id_survey = idSurvey;
				int iduseranswer = (int) userId; //che cosa brutta
				String uuid = UUID.randomUUID().toString();
				String field1 = userEmailAddress;
				String field3 = uuid;
	
				String insertTableSQL = "INSERT INTO " + INVITATION_TOKEN_TABLE_NAME
						+ "(id_survey, iduseranswer, uuid, field1, field3) VALUES"
						+ "(?,?,?,?,?)";
				
				PreparedStatement preparedStatement = con.prepareStatement(insertTableSQL);
				preparedStatement.setInt(1, id_survey);
				preparedStatement.setInt(2, iduseranswer);
				preparedStatement.setString(3, uuid);
				preparedStatement.setString(4, field1);
				preparedStatement.setString(5, field3);
				// execute insert SQL stetement
				preparedStatement.executeUpdate();
				return getQuestionnaireURLForUser(userId, userEmailAddress, context, questionnaireId);				
			}
			con.close();
			_log.info("getQuestionnaireURLForUser OK ");
		} catch (SQLException e) {
			e.printStackTrace();

		} finally {
			con.close();
		}
		return new QuestionnaireDTO();
	}


}
