package org.gcube.portlets.user.performfish.util.db;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import org.gcube.portlets.user.performfish.bean.Company;
import org.gcube.portlets.user.performfish.bean.Farm;

import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

public class DBUtil {
	private static Log _log = LogFactoryUtil.getLog(DBUtil.class);

	public static List<Company> getCompanies(Connection conn) throws Exception {
		_log.debug("getting Companies ");
		List<Company> toReturn = new ArrayList<>();
		String selectSQL = "SELECT * FROM companies";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Long companyId = rs.getLong("companyid");
			Long associationid = rs.getLong("associationid");
			boolean staged = rs.getBoolean("isstaged");
			Company toAdd = new Company(companyId, associationid, staged);
			_log.debug("Adding " + toAdd);
			toReturn.add(toAdd);
		}
		return toReturn;
	}

	public static List<Farm> getAllFarms(Connection conn) throws Exception {
		_log.debug("getting Farms ");
		List<Farm> toReturn = new ArrayList<>();
		String selectSQL = "SELECT * FROM Farms";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Long farmid = rs.getLong("farmid");
			String location = rs.getString("opt1");
			Long companyid = rs.getLong("companyid");
			String creator = rs.getString("opt2");
			Farm toAdd = new Farm(farmid, location, companyid, creator);
			_log.debug("Adding " + toAdd);
			toReturn.add(toAdd);
		}
		return toReturn;
	}

	public static List<Farm> listFarmsByCompanyId(Connection conn, long companyId) throws Exception {
		_log.debug("getting Farms of company with id = " +companyId);
		List<Farm> toReturn = new ArrayList<>();
		String selectSQL = "SELECT farmid, F.opt1, F.opt2, F.companyid FROM Farms as F, Companies as C WHERE C.companyid = F.companyid and F.companyId = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		preparedStatement.setLong(1, companyId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Long farmid = rs.getLong("farmid");
			String location = rs.getString("opt1");
			Long companyid = rs.getLong("companyid");
			String creator = rs.getString("opt2");
			Farm toAdd = new Farm(farmid, location, companyid, creator);
			_log.debug("Adding " + toAdd);
			toReturn.add(toAdd);
		}
		return toReturn;
	}
	
	public static long getCompanyByFarmId(Connection conn, long farmId) throws Exception {
		_log.debug("getting company of farm with id = " +farmId);
		long toReturn = -1;
		String selectSQL = "SELECT C.companyid FROM Farms as F, Companies as C WHERE C.companyid = F.companyid and F.farmId = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		preparedStatement.setLong(1, farmId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Long companyid = rs.getLong("companyid");
			toReturn = companyid;
		}
		return toReturn;
	}


	public static boolean addCompany(Connection conn, String[] companyIdsToAdd, String username) {
		String insertTableSQL = "INSERT INTO companies"
				+ "(companyid, opt1) VALUES"
				+ "(?,?)";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			for (int i = 0; i < companyIdsToAdd.length; i++) {
				preparedStatement.setLong(1, Long.parseLong(companyIdsToAdd[i]));
				preparedStatement.setString(2, "Created by: "+username);
				// execute insert SQL stetement
				preparedStatement .executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean addFarm(Connection conn, long farmId, long companyId, String farmName, String farmLocation, String usernameCreator) {
		String insertTableSQL = "INSERT INTO Farms"
				+ "(farmid, opt1, companyid, opt2) VALUES"
				+ "(?,?,?,?)";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setLong(1, farmId);
			preparedStatement.setString(2, farmLocation);
			preparedStatement.setLong(3, companyId);
			preparedStatement.setString(4, usernameCreator);
			// execute insert SQL stetement
			preparedStatement .executeUpdate();

		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean removeCompany(Connection conn, String[] companyIdsToRemove) {
		String insertTableSQL = "DELETE FROM companies WHERE companyId = ?";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			for (int i = 0; i < companyIdsToRemove.length; i++) {
				preparedStatement.setLong(1, Long.parseLong(companyIdsToRemove[i]));
				// execute insert SQL stetement
				preparedStatement .executeUpdate();
			}
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	public static boolean setCompanyStaged(Connection conn, long companyId) {
		String insertTableSQL = "UPDATE companies SET isStaged = true WHERE companyId = ?";
		try {
			PreparedStatement preparedStatement = conn.prepareStatement(insertTableSQL);
			preparedStatement.setLong(1, companyId);
			// execute insert SQL stetement
			preparedStatement .executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	public static Company getCompanyById(Connection conn, long companyId) throws Exception {
		_log.debug("getting Companies ");
		Company toReturn = null;
		String selectSQL = "SELECT companyid, associationid, isStaged FROM companies WHERE companyId = ?";
		PreparedStatement preparedStatement = conn.prepareStatement(selectSQL);
		preparedStatement.setLong(1, companyId);
		ResultSet rs = preparedStatement.executeQuery();
		while (rs.next()) {
			Long associationid = rs.getLong("associationid");
			boolean staged = rs.getBoolean("isStaged");
			toReturn = new Company(companyId, associationid, staged);
		}
		return toReturn;
	}
	/**
	 * 
	 * @param conn
	 * @throws SQLException
	 */
	protected static void initializeTablesIfNotExist(Connection conn) throws SQLException {
 		String assTable = "CREATE TABLE IF NOT EXISTS Associations ("
				+ "AssociationId bigint PRIMARY KEY NOT NULL, "
				+ "Opt1 text, "
				+ "Opt2 text, "
				+ "dateCreated date)";


		String companyTable = "CREATE TABLE IF NOT EXISTS Companies ("
				+ "CompanyId bigint PRIMARY KEY NOT NULL, "
				+ "isStaged boolean NOT NULL DEFAULT FALSE, "			
				+ "Opt1 text, "
				+ "Opt2 text, "
				+ "AssociationId bigint, "
				+ "FOREIGN KEY(AssociationId) REFERENCES Associations(AssociationId))";

		String farmTable = "CREATE TABLE IF NOT EXISTS Farms ("
				+ "FarmId bigint PRIMARY KEY NOT NULL, "
				+ "Opt1 text, "
				+ "Opt2 text, "
				+ "CompanyId bigint, "
				+ "FOREIGN KEY(CompanyId) REFERENCES Companies(CompanyId))";
		Statement stmt = conn.createStatement();
		stmt.execute(assTable);
		stmt.execute(companyTable);
		stmt.execute(farmTable);	
	}
}
