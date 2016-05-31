package org.gcube.portlets.admin.wfdocslibrary.server.db;


import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Properties;

import org.gcube.portlets.admin.wfdocslibrary.shared.ActionChange;
import org.gcube.portlets.admin.wfdocslibrary.shared.LogAction;
import org.gcube.portlets.admin.wfdocslibrary.shared.UserComment;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfGraphDetails;
import org.gcube.portlets.admin.wfdocslibrary.shared.WfRole;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * <code> MyDerbyStore </code> class is the derby impl of the store
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
public class MyDerbyStore implements Store {

	private static final Logger log = LoggerFactory.getLogger(MyDerbyStore.class);

	private final String dbName = "System.WorkflowDocs-derbyDB"; // the name of the database

	private final static String ROLES_TABLE = "ROLES";
	private final static String WORKFLOW_TEMPLATES = "WFTEMPLATES";
	private final static String WORKFLOW_INSTANCES = "WORKFLOWS";
	private final static String ACTIONS_LOG = "ACTIONS_LOG";
	private final static String CHANGES_LOG = "CHANGES_LOG";
	private final static String USER_COMMENTS = "USER_COMMENTS";


	private String driver = "org.apache.derby.jdbc.EmbeddedDriver";
	private String protocol = "jdbc:derby:";  

	private Connection conn;
	private Statement s;
	ResultSet rs;
	String[] default_rolesname = {"EDITOR", "AUTHOR", "REVIEWER", "AUTHORIZER"};
	String[] default_rolesdesc = {"The Editor of a document", "The contributor", "The reviewer for a document", "Person in charge of giving authorization fro publishing"};
	/**
	 * Initializes the connection and create the default data if non existents 
	 */
	public MyDerbyStore() {
		try {			
			loadDriver();
			conn = getConnection();	
			// even though Autocommit is on by default in JDBC.
			conn.setAutoCommit(true);
			System.out.println("\n*** Connected to database " + dbName + " Checking its status ... ");
			/* Creating a statement object that we can use for running various
			 * SQL statements commands against the database.*/
			s = conn.createStatement();

			if (! dbExists(conn)) {
				initializeDB(s);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}


	/**
	 * @throws SQLException 
	 * 
	 */
	private void initializeDB(Statement s) throws SQLException {
		log.info("Workflow Database to initiliaze");
		s.execute("CREATE TABLE " + ROLES_TABLE + "(roleid INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name varchar(256)  NOT NULL, " +
		"description varchar(256)  NOT NULL)");
		log.info("Created table ROLES");
		for (int i = 0; i < default_rolesname.length; i++) {
			s.execute("INSERT into ROLES(name , description) values('"+ default_rolesname[i] +"' , '"+ default_rolesdesc[i] +"')");
		}
		log.info("Added default ROLES");

		s.execute("CREATE TABLE " + WORKFLOW_TEMPLATES + "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name varchar(256)  NOT NULL, " +
		"author varchar(256) NOT NULL, graph LONG VARCHAR NOT NULL, datecreated TIMESTAMP NOT NULL, disabled SMALLINT DEFAULT 0, PRIMARY KEY(id) )");
		log.info("Created table for WORKFLOW TEMPLATES: " + WORKFLOW_TEMPLATES);
		s.execute("CREATE TABLE " + WORKFLOW_INSTANCES + "(id INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), name varchar(256)  NOT NULL, " +
		"status varchar(256) NOT NULL, author varchar(256) NOT NULL, graph LONG VARCHAR NOT NULL, datecreated TIMESTAMP NOT NULL, disabled SMALLINT DEFAULT 0, PRIMARY KEY(id) )");
		log.info("Created table for WORKFLOW INSTANCES: " + WORKFLOW_INSTANCES);	

		s.execute("CREATE TABLE " + ACTIONS_LOG + "(actionid INT NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1), workflowid INT CONSTRAINT wfr_foreign_key REFERENCES " + WORKFLOW_INSTANCES + 
		" ON DELETE CASCADE, logtime TIMESTAMP NOT NULL , author varchar(256) NOT NULL, actiontype varchar(256) NOT NULL, PRIMARY KEY(actionid) )");
		log.info("Created table for ACTIONS LOG: " + ACTIONS_LOG);	

		s.execute("CREATE TABLE " + CHANGES_LOG + "(actionid INT CONSTRAINT acfr_foreign_key REFERENCES " + ACTIONS_LOG + 
		" ON DELETE CASCADE, changetime TIMESTAMP NOT NULL , author varchar(256) NOT NULL, sectionchangetype varchar(256) NOT NULL, sectionid INT NOT NULL, componenttype varchar(256), componentid INT, previouscontent LONG VARCHAR )");
		log.info("Created table for CHANGES_LOG: " + CHANGES_LOG);	

		s.execute("CREATE TABLE " + USER_COMMENTS + "(workflowid INT CONSTRAINT wcm_foreign_key REFERENCES " + WORKFLOW_INSTANCES + " ON DELETE CASCADE, " +
		"time TIMESTAMP NOT NULL , author varchar(256) NOT NULL, comment LONG VARCHAR NOT NULL )");
		log.info("Created table for USER COMMENTS: " + USER_COMMENTS);		

		log.info("Trying to add default  Sample Workflow");
		boolean result = addWorkflowTemplate("Sample Workflow", "Sample User", getSampleWorkflow());
		if (result)
			log.info("Sample Workflow created");		
		else
			log.error("Could not create Sample Workflow created");			
	}
	/**
	 * check if tables exist in the database
	 * @param conn .
	 */
	private boolean dbExists(Connection conn) {
		DatabaseMetaData md = null;
		try {
			md = conn.getMetaData();
			String[] names = { "TABLE"};
			ResultSet rs = md.getTables(null, null, null, names);
			int i = 0;
			while (rs.next()) {
				String tab = rs.getString( "TABLE_NAME");
				System.out.println("Found table: " + tab);
				i++;
			}
			return (i > 0);
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}


	private Connection getConnection() throws SQLException {
		Properties props = new Properties(); // connection properties
		// providing a user name and password is optional in the embedded and derbyclient frameworks
		props.put("user", "user1");
		props.put("password", "user1");

		String sl = File.separator;		
		String dbpath = System.getenv("CATALINA_HOME");
		dbpath +=	sl + "webapps" + sl +"usersArea" + sl + dbName;

		return DriverManager.getConnection(protocol + dbpath + ";create=true", null);
	}


	/**
	 * add a role into db
	 */
	public WfRole add(WfRole wfRole) {
		if (wfRole != null) {
			try {
				s.execute("INSERT into ROLES(name , description) values('"+ wfRole.getRolename() +"' , '"+ wfRole.getRoledescription() +"')");
				return wfRole;
			} catch (SQLException e) {
				System.out.println("ERROR while trying to INSERT ROLE");
				e.printStackTrace();
			}
		}
		return null;
	}

	protected static final String query = "INSERT into "+WORKFLOW_TEMPLATES+" (name , author, graph, datecreated) values(?, ?, ?, ?)";

	/**
	 * add a owrkflow template in the db
	 */
	public Boolean addWorkflowTemplate(String wfName, String wfAuthor, String wfXML) {
		if (wfXML != null) {
			try {
				PreparedStatement statement = conn.prepareStatement(query);

				statement.setString(1, wfName);
				statement.setString(2, wfAuthor);
				statement.setString(3, wfXML);
				java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
				statement.setTimestamp(4, now);

				statement.executeUpdate();

				//s.execute("INSERT into "+WORKFLOW_TEMPLATES+" (name , author, graph, datecreated)) values('"+ wfName +"' , '"+ wfAuthor +"' , '"+ wfXML +"')");
				return true;
			} catch (SQLException e) {
				System.out.println("ERROR while trying to INSERT Workflow Template");
				e.printStackTrace();
			}
		}
		return null;
	}
	/**
	 * delete a role from db
	 */
	public Boolean deleteRole(String roleid) {
		try {
			return s.execute("DELETE FROM " + ROLES_TABLE + " WHERE roleid=" + roleid);
		} catch (SQLException e) {
			System.out.println("ERROR while trying to DELETE ROLE");
			e.printStackTrace();
			return false;
		}
	}
	/**
	 * delete a list of roles
	 * @return the list od remaining roles
	 */
	public ArrayList<WfRole> deleteRoles(List<String> ids) {
		for (String roleid : ids) {
			try {
				s.execute("DELETE FROM " + ROLES_TABLE + " WHERE roleid=" + roleid);

			} catch (SQLException e) {
				System.out.println("ERROR while trying to DELETE ROLE");
				e.printStackTrace();
			}
		}
		return getAllRoles();
	}
	/**
	 * get a role
	 */
	public WfRole getRole(String id) {
		try {
			ResultSet rs = s.executeQuery("SELECT * FROM " + ROLES_TABLE + " WHERE roleid=" + id + "");
			if (rs.next()) {
				return new WfRole(rs.getString("roleid"), rs.getString("name"), rs.getString("description"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * return the initial list of roles
	 */
	public ArrayList<WfRole> getAllRoles() {
		ArrayList<WfRole> toReturn = new ArrayList<WfRole>();
		try {
			ResultSet rs = s.executeQuery("SELECT roleid, name, description FROM " + ROLES_TABLE + " ORDER BY roleid");
			while (rs.next()) {
				toReturn.add(new WfRole(rs.getString("roleid"), rs.getString("name"),  rs.getString("description")));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toReturn;
	}
	/**
	 * return the initial list of workflows instances
	 */
	public ArrayList<WfGraphDetails> getAllWorkflowTemplates() {
		ArrayList<WfGraphDetails> toRet = new ArrayList<WfGraphDetails>();
		try {
			ResultSet rs = s.executeQuery("SELECT id, name, graph, author, datecreated FROM " + WORKFLOW_TEMPLATES+ " WHERE disabled = 0 ORDER BY id DESC");
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String author = rs.getString("author");
				String xmlGraph = rs.getString("graph");
				Date datecreated = rs.getTimestamp("datecreated");
				toRet.add(new WfGraphDetails(id, name, author, "", datecreated, xmlGraph));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}
	/**
	 * return the initial list of workflows instances
	 */
	public ArrayList<WfGraphDetails> getAllWorkflows() {
		ArrayList<WfGraphDetails> toRet = new ArrayList<WfGraphDetails>();
		try {
			ResultSet rs = s.executeQuery("SELECT id, name, graph, author, status, datecreated FROM " + WORKFLOW_INSTANCES + " WHERE disabled = 0 ORDER BY id DESC");
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String author = rs.getString("author");
				String xmlGraph = rs.getString("graph");
				String status = rs.getString("status");
				Date datecreated = rs.getTimestamp("datecreated");
				toRet.add(new WfGraphDetails(id, name, author, status, datecreated, xmlGraph));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}

	/**
	 * updtae a role
	 */
	public WfRole updateRole(WfRole wfRole) {
		try {
			if (s.execute("UPDATE ROLES SET name='"+ wfRole.getRolename() +"', description='"+ wfRole.getRoledescription() +"' WHERE roleid="+wfRole.getRoleid()))
				return wfRole;
		} catch (SQLException e) {
			System.out.println("ERROR while trying to UPDATE ROLE");
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * generate a workflow sample
	 * @return
	 */
	private String getSampleWorkflow() {
		InputStream is = MyDerbyStore.class.getResourceAsStream("/org/gcube/portlets/admin/wfdocslibrary/server/db/resources/WorkflowExample.xml");
		try {
			return convertStreamToString(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		//empty workflow	
		return "<org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph></org.gcube.portlets.admin.wfdocslibrary.shared.WfGraph>";
	}

	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 */
	private String convertStreamToString(InputStream is) throws IOException {
		if (is != null) {
			Writer writer = new StringWriter();

			char[] buffer = new char[1024];
			try {
				Reader reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
				int n;
				while ((n = reader.read(buffer)) != -1) {
					writer.write(buffer, 0, n);
				}
			} finally {
				is.close();
			}
			return writer.toString();
		} else {        
			return "";
		}
	}


	@Override
	public Boolean deleteWfTemplate(String wfTemplateid) {
		try {
			return s.execute("UPDATE " +  WORKFLOW_TEMPLATES + " SET disabled = 1 WHERE id=" + wfTemplateid);
		} catch (SQLException e) {
			System.out.println("ERROR while trying to DELETE WfTemplate with id " + wfTemplateid);
			e.printStackTrace();
			return false;
		}
	}


	@Override
	public WfGraphDetails getWfTemplateById(String templateid) {
		WfGraphDetails toRet = new WfGraphDetails();
		try {
			ResultSet rs = s.executeQuery("SELECT id, name, graph, author FROM " + WORKFLOW_TEMPLATES + " WHERE id="+templateid);
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String author = rs.getString("author");
				String xmlGraph = rs.getString("graph");
				toRet = new WfGraphDetails(id, name, author, "start", null, xmlGraph);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}

	protected static final String queryw = "INSERT into "+WORKFLOW_INSTANCES+" (name , author, graph, status, datecreated) values(?, ?, ?, ?, ?)";
	@Override
	public String addWorkflowReport(String wfReportid, String wfReportName, String status, String wfAuthor, String wfXML) {
		String idToReturn = "";
		if (wfXML != null) {
			try {
				PreparedStatement statement = conn.prepareStatement(queryw);

				statement.setString(1, wfReportName);
				statement.setString(2, wfAuthor);
				statement.setString(3, wfXML);
				statement.setString(4, status);
				java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
				statement.setTimestamp(5, now);

				statement.executeUpdate();

				ResultSet rs = s.executeQuery("SELECT Count(*) as N FROM " + WORKFLOW_INSTANCES );
				while (rs.next()) {
					idToReturn = rs.getString("N");
				}
			} catch (SQLException e) {
				System.out.println("ERROR while trying to INSERT WfReport");
				e.printStackTrace();
			}
		}		
		return idToReturn;
	}


	@Override
	public WfGraphDetails getWorkflowById(String wfid) {
		WfGraphDetails toRet = new WfGraphDetails();
		try {
			ResultSet rs = s.executeQuery("SELECT id, name, graph, author, status, datecreated FROM " + WORKFLOW_INSTANCES + " WHERE id="+wfid);
			while (rs.next()) {
				String id = rs.getString("id");
				String name = rs.getString("name");
				String author = rs.getString("author");
				String xmlGraph = rs.getString("graph");
				String status = rs.getString("status");
				Date datecreated = rs.getTimestamp("datecreated");
				toRet = new WfGraphDetails(id, name, author, status, datecreated, xmlGraph);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}

	protected static final String queryc = "INSERT into "+USER_COMMENTS+" (workflowid , time, author, comment) values(?, ?, ?, ?)";
	@Override
	public Boolean addWorkflowComment(String workflowid, String author,	String comment) {
		int result = 0;
		try {
			PreparedStatement statement = conn.prepareStatement(queryc);
			statement.setString(1, workflowid);
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			statement.setTimestamp(2, now);
			statement.setString(3, author);
			statement.setString(4, comment);
			statement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("ERROR while trying to INSERT Comment");
			e.printStackTrace();
		}
		return result > 0;
	}


	@Override
	public ArrayList<UserComment> getCommentsByWorkflowId(String workflowid) {
		ArrayList<UserComment> toRet = new ArrayList<UserComment>();
		try {
			ResultSet rs = s.executeQuery("SELECT workflowid, time, author, comment FROM " + USER_COMMENTS + " WHERE workflowid = " + workflowid + " ORDER BY time DESC");
			while (rs.next()) {
				String id = rs.getString("workflowid");
				Date time = rs.getTimestamp("time");
				String author = rs.getString("author");
				String comment = rs.getString("comment");
				toRet.add(new UserComment(id, time, author, comment));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}


	protected static final String querylogs = "INSERT into "+ACTIONS_LOG+" (workflowid , logtime, author, actiontype) values(?, ?, ?, ?)";

	public Boolean addWorkflowLogAction(String workflowid, String author, String actiontype) {
		int result = 0;
		try {
			PreparedStatement statement = conn.prepareStatement(querylogs);
			statement.setString(1, workflowid);
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			statement.setTimestamp(2, now);
			statement.setString(3, author);
			statement.setString(4, actiontype);
			statement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("ERROR while trying to INSERT Log Action");
			e.printStackTrace();
		}
		return result > 0;
	}

	@Override
	public ArrayList<LogAction> getLogActionsByWorkflowId(String workflowid) {
		ArrayList<LogAction> toRet = new ArrayList<LogAction>();
		try {
			ResultSet rs = s.executeQuery("SELECT workflowid, logtime, author, actiontype FROM " + ACTIONS_LOG + " WHERE workflowid = " + workflowid + " ORDER BY logtime DESC");
			while (rs.next()) {
				String id = rs.getString("workflowid");
				Date logtime = rs.getTimestamp("logtime");
				String author = rs.getString("author");
				String actiontype = rs.getString("actiontype");
				toRet.add(new LogAction(id, logtime, author, actiontype));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return toRet;
	}

	
	/**
	 * insert the changes relative to an action log (e.g. actionlog: updated, changes regarding the document update)
	 */
	protected static final String queryInsertChange = "INSERT into "+CHANGES_LOG+" " +	"(actionid , changetime, author, " +
								"sectionchangetype, sectionid, componenttype, componentid, previouscontent) values(?, ?, ?, ?, ?, ?, ?, ?)";

	@Override
	public Boolean addWorkflowActionChange(String actionid, String author, String sectionChangeType, int sectionid, 
			String componentType, int componentId, String previousContent) {
		int result = 0;
		try {
			PreparedStatement statement = conn.prepareStatement(queryInsertChange);
			statement.setString(1, actionid);
			java.sql.Timestamp now = new java.sql.Timestamp(System.currentTimeMillis());
			statement.setTimestamp(2, now);
			statement.setString(3, author);
			statement.setString(4, sectionChangeType);
			statement.setInt(5, sectionid);
			statement.setString(6, componentType);
			statement.setInt(7, componentId);
			statement.setString(8, previousContent);

			statement.executeUpdate();

		} catch (SQLException e) {
			System.out.println("ERROR while trying to INSERT Action Changes");
			e.printStackTrace();
		}
		return result > 0;
	}


	@Override
	public ArrayList<ActionChange> getChangesByActionId(String actionid) {
			ArrayList<ActionChange> toRet = new ArrayList<ActionChange>();
			try {
				ResultSet rs = s.executeQuery("SELECT * FROM " + CHANGES_LOG + " WHERE actionid = " + actionid + " ORDER BY sectionid");
				while (rs.next()) {
					String id = rs.getString("actionid");
					Date changetime = rs.getTimestamp("changetime");
					String author = rs.getString("author");
					String sectionchangetype = rs.getString("sectionchangetype");
					int sectionid = Integer.parseInt(rs.getString("sectionid"));	
					String componenttype = rs.getString("componenttype");
					int componentid = Integer.parseInt(rs.getString("componentid"));	
					String previouscontent = rs.getString("previouscontent");
					toRet.add(new ActionChange(id, changetime, author, sectionchangetype, sectionid, componenttype, componentid, previouscontent));
				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
			return toRet;
	}


	/**
	 * Loads the appropriate JDBC driver for this environment/framework. For
	 * example, if we are in an embedded environment, we load Derby's
	 * embedded Driver, <code>org.apache.derby.jdbc.EmbeddedDriver</code>.
	 */
	private void loadDriver() {
		/*
		 *  The JDBC driver is loaded by loading its class.
		 *  If you are using JDBC 4.0 (Java SE 6) or newer, JDBC drivers may
		 *  be automatically loaded, making this code optional.
		 *
		 *  In an embedded environment, this will also start up the Derby
		 *  engine (though not any databases), since it is not already
		 *  running. In a client environment, the Derby engine is being run
		 *  by the network server framework.
		 *
		 *  In an embedded environment, any static Derby system properties
		 *  must be set before loading the driver to take effect.
		 */
		try {
			Class.forName(driver).newInstance();
			System.out.println("Loaded the appropriate driver");
		} catch (ClassNotFoundException cnfe) {
			System.err.println("\nUnable to load the JDBC driver " + driver);
			System.err.println("Please check your CLASSPATH.");
			cnfe.printStackTrace(System.err);
		} catch (InstantiationException ie) {
			System.err.println(
					"\nUnable to instantiate the JDBC driver " + driver);
			ie.printStackTrace(System.err);
		} catch (IllegalAccessException iae) {
			System.err.println(
					"\nNot allowed to access the JDBC driver " + driver);
			iae.printStackTrace(System.err);
		}
	}


	@Override
	public Boolean updateWorkflowGraph(String workflowid, String wfXML) {
		try {
			s.execute("UPDATE " + WORKFLOW_INSTANCES + " SET graph='"+ wfXML +"'  WHERE id="+workflowid);
		} catch (SQLException e) {
			System.out.println("ERROR while trying to UPDATE Workflow Graph");
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public Boolean updateWorkflowStatusAndGraph(String workflowid, String newStatus, String wfXML) {
		try {
			s.execute("UPDATE " + WORKFLOW_INSTANCES + " SET status='"+ newStatus +"' , graph='"+ wfXML +"'  WHERE id="+workflowid);
		} catch (SQLException e) {
			System.out.println("ERROR while trying to UPDATE Workflow and Status Graph");
			e.printStackTrace();
			return false;
		}
		return true;
	}


	@Override
	public Boolean deleteWorkflowReport(String wfReportid) {
		try {
			return s.execute("UPDATE " + WORKFLOW_INSTANCES + " SET disabled = 1 WHERE id=" + wfReportid);
		} catch (SQLException e) {
			System.out.println("ERROR while trying to DELETE WORKFLOW DOCUMENT");
			e.printStackTrace();
			return false;
		}
	}



}
