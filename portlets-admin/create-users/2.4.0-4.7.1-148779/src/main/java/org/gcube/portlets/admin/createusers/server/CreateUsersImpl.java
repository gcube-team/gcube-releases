package org.gcube.portlets.admin.createusers.server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import javax.mail.internet.InternetAddress;

import org.gcube.common.portal.PortalContext;
import org.gcube.portal.mailing.message.Recipient;
import org.gcube.portal.mailing.service.EmailTemplateService;
import org.gcube.portal.mailing.templates.TemplateWelcomeCreatedAccount;
import org.gcube.portlets.admin.createusers.client.HandleUsersService;
import org.gcube.portlets.admin.createusers.shared.VreUserBean;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.exception.UserManagementPortalException;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.dao.jdbc.DataAccess;
import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.mail.MailMessage;
import com.liferay.portal.kernel.util.GetterUtil;
import com.liferay.portal.kernel.util.PropsUtil;
import com.liferay.portal.model.Company;
import com.liferay.portal.service.CompanyLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;

/**
 * The server side implementation of the RPC service.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class CreateUsersImpl extends RemoteServiceServlet implements HandleUsersService{

	private final static Logger logger = LoggerFactory.getLogger(CreateUsersImpl.class);
	private static final long serialVersionUID = -3124676000683430170L;
	private static final String REGISTERED_USERS_TABLE = "registered_users";

	// SQL TABLE FIELDS
	private static final String FIELD_EMAIL = "email";
	private static final String FIELD_NAME = "name";
	private static final String FIELD_SURNAME = "surname";
	private static final String FIELD_INSTITUTION = "institution_organization";
	private static final String FIELD_REGISTRATION_DATE = "registration_date";
	private static final String FIELD_VRE = "vre";

	public static final String DEFAULT_COMPANY_WEB_ID = "liferay.com";

	@Override
	public void init(){

		if(isWithinPortal()){
			logger.debug("Trying to get connect to liferay's DB from API");
			try {
				Connection con = DataAccess.getConnection();
				boolean exists = tableExists(con);
				if(exists)
					logger.debug("Table " + REGISTERED_USERS_TABLE + " already exists.");
				else
					initializeTable(con);
			} catch (Exception e) {
				logger.error("Failed to connect to liferay's DB");
				return;
			}
		}else
			logger.debug("DEV MODE detected");
	}

	/**
	 * check if tables exist in the database
	 * @param conn .
	 * @throws SQLException 
	 */
	private boolean tableExists(Connection con) throws SQLException {
		logger.debug("Looking for " + REGISTERED_USERS_TABLE + " table");
		Statement s = con.createStatement();
		ResultSet rs = s.executeQuery("SELECT * FROM  pg_tables where schemaname='public' and  tablename = '" + REGISTERED_USERS_TABLE +"' ");
		boolean toReturn =  rs.next();
		if (toReturn)
			logger.debug("Auxiliary Table Found! Returning ... ");
		return toReturn;
	}

	/**
	 * create the table REGISTERED_USERS_TABLE 
	 * @throws SQLException
	 */
	private void initializeTable(Connection con) {
		try {

			logger.debug("Creating table " + REGISTERED_USERS_TABLE);
			Statement s = con.createStatement();

			s.execute("CREATE TABLE " + REGISTERED_USERS_TABLE 
					+ " (id serial primary key, " // like autoincrement
					+ "email  varchar(255)  NOT NULL, "
					+ "name" + " varchar(255)  NOT NULL, "
					+ "surname" + " varchar(255)  NOT NULL, "
					+ "institution_organization" + " varchar(255)  DEFAULT NULL, "
					+ "vre" + " varchar(255)  NOT NULL, "
					+ "registration_date" + " TIMESTAMP NOT NULL)");

			logger.info(REGISTERED_USERS_TABLE + " created");

		} catch (SQLException e) {
			logger.error("Error while creating table", e);
		}
		try {
			con.close();
		} catch (SQLException e1) {
			logger.error("Error while closing connection", e1);
		}
	}

	/**
	 * Online or in development mode?
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			logger.trace("Development Mode ON");
			return false;
		}			
	}

	@Override
	public boolean deleteInvitedUser(String email) {

		// if in dev mode return some samples
		if (!isWithinPortal()) {
			logger.debug("In dev mode.");
			return false;
		}else{
			try{
				Connection con = DataAccess.getConnection();
				boolean deletedLiferay = deleteUserFromLiferay(email);
				boolean deletedTable = deleteUserFromTable(email, con);
				return deletedLiferay && deletedTable;
			}catch(SQLException e){
				logger.debug("Error while trying to delete user with email = " + email, e);
			}
			return false;
		}
	}

	@Override
	public void sendEmailToUser(String email) {
		if(isWithinPortal()){
			try{
				logger.debug("Sending welcome message to user with email " + email);
				PortalContext context = PortalContext.getConfiguration();
				String gatewayName = context.getGatewayName(getThreadLocalRequest());
				String gatewayURL = context.getGatewayURL(getThreadLocalRequest());
				long groupId = context.getCurrentGroupId(getThreadLocalRequest());
				GCubeGroup currVRE = new LiferayGroupManager().getGroup(groupId);
				UserManager um = new LiferayUserManager();
				GCubeUser theCreatedAccountUser = um.getUserByEmail(email);
				GCubeUser theManagerUser =context.getCurrentUser(getThreadLocalRequest());

				String subject = "Your user account for " + currVRE.getGroupName() + " VRE has been created";
				EmailTemplateService.send(
						subject, 
						new TemplateWelcomeCreatedAccount(gatewayName, gatewayURL, theCreatedAccountUser, theManagerUser, currVRE), getThreadLocalRequest(), new Recipient(email));

			}catch(Exception e){
				logger.error("Error while sending email to user " + email, e);
			}
		}else
			logger.debug("DEV MODE detected, mail sent...");
	}

	@Override
	public VreUserBean register(String name, String surname, String institution,
			String email, boolean sendEmail, boolean isMale) {

		// if in dev mode return ok
		if (!isWithinPortal()) {
			logger.debug("In dev mode.");
			return new VreUserBean(name, surname, institution, email, false, System.currentTimeMillis(), isMale);
		}else{

			PortalContext pContext = PortalContext.getConfiguration();
			String vre = pContext.getCurrentScope(getThreadLocalRequest());
			long timestamp = System.currentTimeMillis();

			//checking if the user has been already registered or is already in the portal
			LiferayUserManager userManager = new LiferayUserManager();
			boolean exists = userManager.userExistsByEmail(email);

			if(exists){
				logger.debug("User with this email already present in the portal.");
				return null;
			}
			else{

				logger.debug("Trying to add user: " + name + ", " + surname + ", " + email + ", " + institution);
				Connection con = null;
				try{
					con = DataAccess.getConnection();
					String insert = "INSERT into " + REGISTERED_USERS_TABLE + 
							"("+ FIELD_EMAIL + ","
							+ FIELD_NAME +"," 
							+ FIELD_SURNAME + "," 
							+ FIELD_INSTITUTION + "," 
							+ FIELD_REGISTRATION_DATE + "," 
							+ FIELD_VRE  
							+ ") values(?, ?, ?, ?, ?, ?)";

					PreparedStatement statement = con.prepareStatement(insert);
					statement.setString(1, email);
					statement.setString(2, name);
					statement.setString(3, surname);
					statement.setString(4, institution);
					statement.setTimestamp(5, new Timestamp(timestamp));
					statement.setString(6, vre);
					int res = statement.executeUpdate();
					if(res != 0){
						logger.debug("User added in " + REGISTERED_USERS_TABLE);
						// add in liferay too
						try{
							GCubeUser user = userManager.createUser(
									true,
									"", 
									email, 
									name, 
									"", 
									surname, 
									institution, 
									"", 
									"",
									isMale, 
									"What was your initial password?", 
									"training1",
									false, // determine if we need to send him/her an email
									true); 	// force the password reset

							// adding to the current VRE
							userManager.assignUserToGroup(getCurrentGroupID(), userManager.getUserId(user.getUsername()));
							if (sendEmail)
								sendEmailToUser(email);

						}catch(Exception e){

							// unable to create.. we need to delete it from the list of users
							logger.error("Unable to create the user " + email + " in liferay. Removing he/she from the table " + 
									REGISTERED_USERS_TABLE, e);

							deleteUserFromTable(email, con);
							return null;
						}
					}
					else{

						logger.debug("User NOT added in " + REGISTERED_USERS_TABLE);
						return null;
					}
				}catch(Exception e){
					logger.error("Unable to add user, sorry..", e);
					return null;
				}finally{
					try {
						if(con != null)
							con.close();
					} catch (SQLException e) {
						logger.error("Unable to close connection to the DB");
					}
				}
				return new VreUserBean(name, surname, institution, email, false, timestamp, isMale);
			}
		}
	}

	@Override
	public List<VreUserBean> getAlreadyRegisterdUsers() {

		List<VreUserBean> toReturn = new ArrayList<VreUserBean>();

		// if in dev mode return some samples
		if (!isWithinPortal()) {

			logger.debug("In dev mode.");
			toReturn.add(new VreUserBean("Dylan", "Dog", "ISTI-CNR", "dylan.dog@gmail.com", true, System.currentTimeMillis(), true));
			toReturn.add(new VreUserBean("Costantino", "Perciante", "ISTI-CNR", "costantino8@gmail.com", false, System.currentTimeMillis(), true));
			return toReturn;

		}else{

			LiferayUserManager userManager = new LiferayUserManager();

			// evaluate current vre
			PortalContext pContext = PortalContext.getConfiguration();
			String vre = pContext.getCurrentScope(getThreadLocalRequest());

			Connection con = null;
			try{

				con = DataAccess.getConnection();
				Statement stmt = con.createStatement();

				String sql = "SELECT * FROM " + REGISTERED_USERS_TABLE + " WHERE " + FIELD_VRE + "='" + vre + "';";
				ResultSet rs = stmt.executeQuery(sql);

				while(rs.next()){

					String name = rs.getString(FIELD_NAME);
					String surname = rs.getString(FIELD_SURNAME);
					String institution = rs.getString(FIELD_INSTITUTION);
					String email = rs.getString(FIELD_EMAIL);
					long registrationDate = rs.getTimestamp(FIELD_REGISTRATION_DATE).getTime();

					// check if the password has been changed or not wrt the default one
					boolean passwordChanged = userManager.isPasswordChanged(email);
					toReturn.add(new VreUserBean(name, surname, institution, email, passwordChanged, registrationDate, false));
				}

				// now, make sure these users are still on portal
				Iterator<VreUserBean> iterator = toReturn.iterator();
				while (iterator.hasNext()) {
					VreUserBean user = (VreUserBean) iterator.next();

					if(!userManager.userExistsByEmail(user.getEmail())){

						// remove from the table
						deleteUserFromTable(user.getEmail(), con);

						// remove from this collection
						iterator.remove();

					}
				}

			}catch(Exception e){
				logger.error("Unable to retrieve users list, sorry...", e);
				return null;
			}finally{
				try {
					if(con != null)
						con.close();
				} catch (SQLException e) {
					logger.error("Unable to close connection to the DB");
				}
			}

			return toReturn;
		}
	}

	/**
	 * Remove a row from the table of the registered users
	 * @param email
	 * @param con
	 */
	private boolean deleteUserFromTable(String email, Connection con) {

		try{

			logger.debug("Going to delete user with email " + email + " from the table of registered users");

			String remove = "DELETE FROM " + REGISTERED_USERS_TABLE + " WHERE " + FIELD_EMAIL + "=  ?";
			PreparedStatement statementDelete = con.prepareStatement(remove);
			statementDelete.setString(1, email);
			int res = statementDelete.executeUpdate();

			if(res == 1)
				return true;

		}catch(Exception e){

			logger.error("Error while deleting user=" + email + "from the table");
		}

		return false;
	}

	/**
	 * Delete user from liferay
	 * @param email
	 */
	private boolean deleteUserFromLiferay(String email) {

		LiferayUserManager userManager = new LiferayUserManager();
		try {
			userManager.deleteUserByEMail(email);
			return true;
		} catch (PortalException | SystemException
				| UserManagementSystemException | UserManagementPortalException e) {
			logger.error("Unable to delete user from liferay", e);
		}

		return false;
	}

	/**
	 * Get the current group ID
	 */
	private Long getCurrentGroupID() {

		PortalContext pContext = PortalContext.getConfiguration();
		return pContext.getCurrentGroupId(getThreadLocalRequest());
	}

	public static Company getCompany() throws PortalException, SystemException {
		return CompanyLocalServiceUtil.getCompanyByWebId(getDefaultCompanyWebId());
	}

	/**
	 * 
	 * @return the default company web-id (e.g. iMarine.eu)
	 */
	public static String getDefaultCompanyWebId() {
		String defaultWebId = "";
		try {
			defaultWebId = GetterUtil.getString(PropsUtil.get("company.default.web.id"));
		}
		catch (NullPointerException e) {
			logger.error("Cound not find property company.default.web.id in portal.ext file returning default web id: " + DEFAULT_COMPANY_WEB_ID);
			return DEFAULT_COMPANY_WEB_ID;
		}
		return defaultWebId;
	}
}
