package org.gcube.resource.management.quota.manager.check;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.accounting.analytics.Filter;
import org.gcube.resource.management.quota.library.quotalist.TimeInterval;
import org.gcube.resource.management.quota.library.status.QuotaStorageStatus;
import org.gcube.resource.management.quota.manager.util.Constants;
import org.gcube.resource.management.quota.manager.util.ConstantsDb;
import org.gcube.resource.management.quota.manager.util.DiscoveryConfiguration;
import org.gcube.resource.management.quota.manager.util.DiscoveryListUser;
import org.gcube.resource.management.quota.manager.util.QuotaUsageServiceValue;
import org.gcube.resource.management.quota.manager.util.QuotaUsageStorageValue;
import org.gcube.resource.management.quota.manager.util.SendNotification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * QuotaUsage
 *  
 * @author Alessandro Pieve (alessandro.pieve@isti.cnr.it)
 *
 */
public class QuotaUsage {

	private static Logger log = LoggerFactory.getLogger(QuotaUsage.class);	 
	private DiscoveryConfiguration discoveryCheck;

	private SendNotification sendNotification;

	public QuotaUsage(DiscoveryConfiguration discoveryCheck){
		super();
		this.discoveryCheck=discoveryCheck;
		sendNotification=new SendNotification(discoveryCheck);
	};

	public List<String> userExceedQuota =new ArrayList<String>();

	public void insertServiceQuota(QuotaUsageServiceValue usageSerVal) throws Exception {


		String context_label=usageSerVal.getContext().replace('/', '_').toUpperCase();
		Connection connection = getDBConnection();
		Statement stmt = null;
		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			String periodQuota=usageSerVal.getTemporalConstraint().getAggregationMode().toString();
			if (!verifyTable(connection,ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_SERVICE+ConstantsDb.SEPARATOR+
					context_label+ConstantsDb.SEPARATOR+periodQuota)){
				String queryCreate="CREATE TABLE "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_SERVICE+ConstantsDb.SEPARATOR+
						context_label+ConstantsDb.SEPARATOR+periodQuota+""
						+ "(CALLERTYPE varchar(255),IDENTIFIER varchar(255) ,SERVICE varchar(255),"
						+ "ACCESSTYPE varchar(50),QUOTAASSIGNED DOUBLE PRECISION, QUOTAUSAGE DOUBLE PRECISION,POLICY integer,"
						+ "primary key(CALLERTYPE,IDENTIFIER,SERVICE))";
				stmt.execute(queryCreate);
			}

			String serviceIdentifier = "";
			for (Filter tmp:usageSerVal.getFilters()){
				serviceIdentifier=serviceIdentifier+tmp.getValue()+":";
			}
			//TODO
			serviceIdentifier=serviceIdentifier+"0";

			String queryVerify="SELECT count(*) FROM "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
					+ " WHERE IDENTIFIER ='"+usageSerVal.getIdentifier()+"'";
			ResultSet rs = stmt.executeQuery(queryVerify);
			rs.next();
			String queryInsert;
			if (rs.getInt(1)==0){	

				queryInsert="INSERT into "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_SERVICE+ConstantsDb.SEPARATOR+
						context_label+ConstantsDb.SEPARATOR+periodQuota+""
						+ " (CALLERTYPE,IDENTIFIER, SERVICE,ACCESSTYPE,QUOTAASSIGNED, QUOTAUSAGE)  VALUES('"+usageSerVal.getCallerType()+"',"
						+ "'"+usageSerVal.getIdentifier()+"','"+serviceIdentifier+"',"
						+"'"+usageSerVal.getAccessType()+"'," 		+usageSerVal.getdQuota()+","+usageSerVal.getD()+")";

			}else{
				queryInsert="UPDATE "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_SERVICE+ConstantsDb.SEPARATOR+
						context_label+ConstantsDb.SEPARATOR+periodQuota+""
						+ "SET ACCESSTYPE='"+usageSerVal.getAccessType()+"' ,"
						+ "QUOTAASSIGNED="+usageSerVal.getdQuota()+" ,"
						+ "QUOTAUSAGE="+usageSerVal.getD()+""
						+ "WHERE CALLERTYPE='"+usageSerVal.getCallerType()+"' AND"
						+ " IDENTIFIER='"+usageSerVal.getIdentifier()+"' AND"
						+ " SERVICE='"+serviceIdentifier+"'";
			}


			log.debug("Insert data into table:{}",queryInsert);
			stmt.execute(queryInsert);

			stmt.close();
			connection.commit();

		} catch (SQLException e) {
			log.error("Sql error  Message " + e);

		} catch (Exception e) {
			log.error("Exception error  Message " + e);

		} finally {
			connection.close();
		}

	} 

	public void insertStorageQuota(QuotaUsageStorageValue usageStorVal) throws Exception {		 

		Connection connection = getDBConnection();
		Statement stmt = null;
		try {

			Boolean notificationWarning1 =false;
			Boolean notificationWarning2 =false;
			Boolean notificationExceed =false;
			Double quotaAssignedOld = 0.0;
			log.debug("quota consistent value getOrderingProperty :{}",usageStorVal.getOrderingProperty());
			if (usageStorVal.getOrderingProperty()!=null){
				//consistence result
				connection.setAutoCommit(false);
				stmt = connection.createStatement();
				String periodQuota=ConstantsDb.DATABASE_PERIOD_TOTAL;
				if (usageStorVal.getTemporalConstraint()!=null){
					periodQuota=usageStorVal.getTemporalConstraint().getAggregationMode().toString();
				}
				if (!verifyTable(connection,ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+
						ConstantsDb.SEPARATOR+periodQuota)){
					String queryCreate="CREATE TABLE "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
							+ "(IDENTIFIER varchar(255) primary key,QUOTAASSIGNED DOUBLE PRECISION, QUOTAUSAGE DOUBLE PRECISION, "
							+ "NOTIFICATIONWARNING1 boolean default false,NOTIFICATIONWARNING2 boolean default false,NOTIFICATIONEXCEED boolean default false)";
					log.debug("Create table:{}",queryCreate);
					stmt.execute(queryCreate);
	
				}
	
				Double percent=((usageStorVal.getD()/usageStorVal.getdQuota())*100);
				percent=Math.round(percent * 100.0) / 100.0;
				log.debug("Quota Percent quota:{} used:{} percent:{}",usageStorVal.getdQuota(),usageStorVal.getD(),percent);
				
				String queryVerify="SELECT NOTIFICATIONWARNING1,NOTIFICATIONWARNING2,NOTIFICATIONEXCEED,QUOTAASSIGNED FROM "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
						+ " WHERE IDENTIFIER ='"+usageStorVal.getIdentifier()+"'";
				ResultSet rs = stmt.executeQuery(queryVerify);
				while (rs.next()) {
					notificationWarning1 = rs.getBoolean("NOTIFICATIONWARNING1");
					notificationWarning2 = rs.getBoolean("NOTIFICATIONWARNING2");
					notificationExceed = rs.getBoolean("NOTIFICATIONEXCEED");
					quotaAssignedOld= rs.getDouble("QUOTAASSIGNED");
				}
			
			
		
				if (!quotaAssignedOld.equals(usageStorVal.getdQuota())){
					//verify if your quota assigned is changed
					log.debug("quota changed old value:{} new value:{}",quotaAssignedOld,usageStorVal.getdQuota());
					if (!quotaAssignedOld.equals(0.0)){
						//verify if your old quota is consistent
						log.debug("your old quota is consistent:{}",quotaAssignedOld);
						sendNotification.sendNotificationUser(usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD(),percent,"change");
					}
					
				}
	
				if (!usageStorVal.getdQuota().equals(-1)){
					if ((percent>Constants.LIMIT_MSG_QUOTA_PERC_USAGE_1)&&(percent<Constants.LIMIT_MSG_QUOTA_PERC_USAGE_2)){
						//verify if your quota is between 90 and 95 % first warning
						if (!notificationWarning1){
							if (sendNotification.sendNotificationUser(usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD(),percent,"warning")){
								log.debug("notification Send! warning level1"); 
								notificationWarning1=true;
								notificationWarning2=false;
								notificationExceed=false;
							}
						}
					}
					else if ((percent>Constants.LIMIT_MSG_QUOTA_PERC_USAGE_2)&&(percent<100)){
						//verify if your quota is between 95 and 100 % second warning
						if (!notificationWarning2){
							if (sendNotification.sendNotificationUser(usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD(),percent,"warning")){
								log.debug("notification Send! warning level2");
								notificationWarning2=true;						
								notificationExceed=false;	
							}
						}
					}
					else if ((percent>=100)){
						//your quota is exceed
						log.debug("Quota exceed for:{} identifier:{} notification:{} ",percent,usageStorVal.getIdentifier(),notificationExceed);
						
						if (!notificationExceed){
							if(sendNotification.sendNotificationUser(usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD(),percent,"exceed")){
								log.debug("notification Send! warning level3");						
								notificationExceed=true;
							}
							String fullname=DiscoveryListUser.getMapUser(usageStorVal.getIdentifier());
							String msgName=fullname+" ("+usageStorVal.getIdentifier()+") - storage space used: "+percent+"%";
							userExceedQuota.add(msgName);
						}
					}
					else if (notificationWarning1 || notificationWarning2 || notificationExceed){
						//your quota value is restored
						log.debug("---Quota reset for identifier:{} and usage :{} and quota:{}",
								usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD());
						log.debug("---Quota reset for notificationWarning1:{} and notificationWarning2 :{} and notificationExceed:{}",
								notificationWarning1,notificationWarning2,notificationExceed);
						if(sendNotification.sendNotificationUser(usageStorVal.getIdentifier(),usageStorVal.getdQuota(),usageStorVal.getD(),percent,"reset")){
							log.debug("reset notification Send!");	
							notificationWarning1=false;
							notificationWarning2=false;
							notificationExceed=false;
						}
					}
	
				}
				else{
					log.debug("quote infinite for identifier:{}",usageStorVal.getIdentifier());
				}
			
				queryVerify="SELECT count(*) FROM "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
						+ " WHERE IDENTIFIER ='"+usageStorVal.getIdentifier()+"'";
				rs = stmt.executeQuery(queryVerify);
				rs.next();
	
				String queryInsert;
				if (rs.getInt(1)==0){			
					queryInsert="INSERT into "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
							+ " (IDENTIFIER, QUOTAASSIGNED, QUOTAUSAGE,NOTIFICATIONWARNING1,NOTIFICATIONWARNING2,NOTIFICATIONEXCEED)  VALUES('"+usageStorVal.getIdentifier()+"',"
							+usageStorVal.getdQuota()+","+usageStorVal.getD()+","+notificationWarning1+""
							+ ","+notificationWarning2+""
							+ ","+notificationExceed+")";
	
				}else{
					queryInsert="UPDATE "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
							+ " set QUOTAASSIGNED="+usageStorVal.getdQuota()+","
							+ "QUOTAUSAGE="+usageStorVal.getD()+" ,"
							+ "NOTIFICATIONEXCEED="+notificationExceed+" ,"
							+ "NOTIFICATIONWARNING1="+notificationWarning1+","
							+ "NOTIFICATIONWARNING2="+notificationWarning2+" "
							+ "where IDENTIFIER='"+usageStorVal.getIdentifier()+"'";
				}
			
				log.debug("Insert data into table:{}",queryInsert);
				stmt.execute(queryInsert);
				stmt.close();
				connection.commit();
			}
			
		} catch (SQLException e) {
			log.error("Sql error  Message " + e);

		} catch (Exception e) {
			log.error("Exception error  Message " + e);

		} finally {
			connection.close();
		}
	} 

	public void SendNotificationAdmin() {

		try {
			log.debug("Send Notification to Admin");
			sendNotification.SendNotificationAdmin(userExceedQuota);
			userExceedQuota.clear();
		} catch (Exception e) {

			e.printStackTrace();
		}
	}

	/**
	 * selectStorageQuota
	 * @param identifier
	 * @param interval
	 * @return Quota Storage Status
	 * @throws Exception
	 */
	public  QuotaStorageStatus selectStorageQuota(String identifier, TimeInterval interval)throws Exception {

		Connection connection = getDBConnection();
		QuotaStorageStatus quotaStorageStatus = null;
		Statement stmt = null;
		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			String periodQuota=ConstantsDb.DATABASE_PERIOD_TOTAL;
			Double quotaAssigned = 0.0;
			Double quotaUsage=0.0;
			//Quota Storage Total
			if (interval.equals(TimeInterval.FOREVER)){
				String query="SELECT * FROM "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+""
						+ " WHERE IDENTIFIER='"+identifier+"'";
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					quotaAssigned=rs.getDouble("QUOTAASSIGNED");
					quotaUsage=rs.getDouble("QUOTAUSAGE");
				}
			}

			stmt.close();
			connection.commit();
			quotaStorageStatus =new QuotaStorageStatus(identifier,interval,quotaAssigned,quotaUsage);
		} catch (SQLException e) {
			log.error("Sql error  Message " + e);

		} catch (Exception e) {
			log.error("Exception error  Message " + e);

		} finally {
			connection.close();
		}
		return quotaStorageStatus;
	}

	
	
	
	
	/**
	 * selectStorageQuotaList list of quota storage
	 * @param identifier
	 * @param interval
	 * @return Quota Storage Status
	 * @throws Exception
	 */
	public  List<QuotaStorageStatus> selectStorageQuotaList(TimeInterval interval)throws Exception {

		Connection connection = getDBConnection();		
		List<QuotaStorageStatus> quotaStorageStatusList=new ArrayList<QuotaStorageStatus>();
		Statement stmt = null;
		try {
			connection.setAutoCommit(false);
			stmt = connection.createStatement();
			String periodQuota=ConstantsDb.DATABASE_PERIOD_TOTAL;
			Double quotaAssigned = 0.0;
			Double quotaUsage=0.0;
			String identifier;
			//Quota Storage Total
			if (interval.equals(TimeInterval.FOREVER)){
				String query="SELECT * FROM "+ConstantsDb.DATABASE_PREFIX+ConstantsDb.SEPARATOR+ConstantsDb.DATABASE_TYPE_STORAGE+ConstantsDb.SEPARATOR+periodQuota+" order by QUOTAUSAGE DESC";
				ResultSet rs = stmt.executeQuery(query);

				while (rs.next()) {
					quotaAssigned=rs.getDouble("QUOTAASSIGNED");
					quotaUsage=rs.getDouble("QUOTAUSAGE");
					identifier=rs.getString("IDENTIFIER");
					quotaStorageStatusList.add(new QuotaStorageStatus(identifier,interval,quotaAssigned,quotaUsage));
				}
			}

			stmt.close();
			connection.commit();
			
		} catch (SQLException e) {
			log.error("Sql error  Message " + e);

		} catch (Exception e) {
			log.error("Exception error  Message " + e);

		} finally {
			connection.close();
		}
		return quotaStorageStatusList;
	}
	
	/**
	 * Verify if exist a table 
	 * @param connection
	 * @param tableName
	 * @return boolean true if exist
	 * @throws SQLException
	 */
	private Boolean verifyTable(Connection connection, String tableName) throws SQLException{		
		boolean tExists = false;
		DatabaseMetaData dbm = connection.getMetaData();
		ResultSet tables = dbm.getTables(null, null, tableName, null);
		if (tables.next()) {
			tExists = true;
		}
		log.trace("verifyTable :{} result:{}",tableName,tExists);
		return tExists;
	}

	/**
	 * Init connection db
	 * @return connection 
	 */
	private Connection getDBConnection() {

		Connection dbConnection = null;
		try {
			Class.forName(ConstantsDb.DB_DRIVER);
		} catch (ClassNotFoundException e) {
			log.error("error class not found exception",e);			
		}
		try {			
			String nameConnection="jdbc:postgresql://"+discoveryCheck.getDatabasePath()+"/"+discoveryCheck.getDbname();
			dbConnection = DriverManager.getConnection(nameConnection, discoveryCheck.getUsernameDb(),discoveryCheck.getPwdnameDb());

			return dbConnection;
		} catch (SQLException e) {
			log.error("error connecting to db",e);

		}
		return dbConnection;
	}

}
