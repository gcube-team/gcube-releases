package it.eng.edison.usersurvey_portlet.server.util;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import com.liferay.portal.kernel.dao.jdbc.DataAccess;

public class CreateDB {

 
	private static final String REGISTERED_TABLE = "survey";

	public CreateDB(String DBURL, String DBName,String uName ,String pwd){
            
		Connection con = null;
		try {
		 
			con = DriverManager.getConnection(DBURL, uName, pwd);
			initializeTable(con);

		} catch (SQLException e) {
			e.printStackTrace();
		} 
		finally {
			try {
				con.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * create the table REGISTERED_USERS_TABLE 
	 * @throws SQLException
	 */
	private void initializeTable(Connection con) {
		try {
                    
			Statement s = con.createStatement();

			String surveyTable = "CREATE TABLE survey ("
					+ "id serial primary key NOT NULL, "
					+ "titleSurvey text NOT NULL, "
					+ "idUserCreator int NOT NULL, "
					+ "groupId bigint NOT NULL, "
					+ "dateSurvay date, "
					+ "expiredDateSurvay date, "
					+ "isAnonymous boolean)";

			String surveyQuestionTable = "CREATE TABLE surveyQuestion ("
					+ "id serial primary key NOT NULL,"
					+ "id_Survey int NOT NULL,"
					+ "numberQuestion int NOT NULL, "
					+ "isMandatory boolean default FALSE,"
					+ "questionType text NOT NULL,"
					+ "question text,"
					+ "answer1 text,"
					+ "answer2 text,"
					+ "answer3 text,"
					+ "answer4 text,"
					+ "answer5 text,"
					+ "answer6 text,"
					+ "answer7 text,"
					+ "answer8 text,"
					+ "answer9 text,"
					+ "answer10 text,"
					+ "imageFileName text,"
					+ "folderIdImage bigint,"
					+ "dateAnswer date,"
					+ "sectionTitle text,"
					+ "sectionDescription text,"
					+ "FOREIGN KEY(id_Survey) REFERENCES survey(id))";

			String surveyUserAnswerTable = "CREATE TABLE surveyUserAnswer ("
					+ "	id serial primary key NOT NULL,"
					+ "	idUserAnswer int NOT NULL,"
					+ "	id_Survey int NOT NULL,"
					+ " questionType text NOT NULL,"
					+ "	numberQuestion int,"
					+ "	answer1 text,"
					+ "	answer2 text,"
					+ "	answer3 text,"
					+ "	answer4 text,"
					+ "	answer5 text,"
					+ "	answer6 text,"
					+ "	answer7 text,"
					+ "	answer8 text,"
					+ "	answer9 text,"
					+ "	answer10 text,"
					+ " dateAnswer date,"
					+ "	FOREIGN KEY(id_Survey) REFERENCES survey(id))";


			String invitationToken = "CREATE TABLE invitationToken ("
					+ "	id serial primary key NOT NULL,"
					+ "	idUserAnswer int NOT NULL,"
					+ "	id_Survey int NOT NULL,"
					+ "	UUID text NOT NULL,"
					+ "	field1 text,"
					+ "	field2 text,"
					+ "	field3 text,"
					+ "	field4 text,"
					+ "	field5 text)";

			String choiceQuestion = "CREATE TABLE choiceQuestion ("
					+ "	id serial primary key NOT NULL,"
					+ "	id_Survey int NOT NULL,"
					+ " numberQuestion int NOT NULL,"
					+ " questionType text NOT NULL,"
					+ "	choice text,"
					+ "	field1 text,"
					+ "	field2 text)";

			String choiceAnswer = "CREATE TABLE choiceAnswer ("
					+ "	id serial primary key NOT NULL,"
					+ "	id_Survey int NOT NULL,"
					+ "	idUserAnswer int NOT NULL,"
					+ " numberQuestion int NOT NULL,"
					+ " questionType text NOT NULL,"
					+ "	choice text,"
					+ "	field1 text,"
					+ "	field2 text)";
			
			String gridQuestion = "CREATE TABLE gridQuestion ("
					+ "id serial primary key NOT NULL,"
					+ "id_Survey int NOT NULL,"
					+ "numberQuestion int NOT NULL,"
					+ "questionType text NOT NULL,"
					+ "gridLabel text NOT NULL,"
					+ "rowOrColumnLabel text NOT NULL,"
					+ "field1 text,"
					+ "field2 text)";
			
			String gridAnswer = "CREATE TABLE gridAnswer ("
					+ "id serial primary key NOT NULL,"
					+ "id_Survey int NOT NULL,"
					+ "idUserAnswer int NOT NULL,"
					+ "numberQuestion int NOT NULL,"
					+ "questionType text NOT NULL,"
					+ "answer text NOT NULL,"
					+ "rowQuestionLabel text,"
					+ "field1 text,"
					+ "field2 text)";
	
			s.execute(surveyTable);
			s.execute(surveyQuestionTable);
			s.execute(surveyUserAnswerTable);			
			s.execute(invitationToken);
			s.execute(choiceQuestion);
			s.execute(choiceAnswer);
			s.execute(gridQuestion);
			s.execute(gridAnswer);

			s.close();
		} catch (SQLException e) {
			
		} 

	}
}
