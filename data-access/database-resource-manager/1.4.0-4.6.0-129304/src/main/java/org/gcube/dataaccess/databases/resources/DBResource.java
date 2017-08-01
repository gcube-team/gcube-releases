package org.gcube.dataaccess.databases.resources;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementRef;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlValue;

import org.gcube.common.encryption.StringEncrypter;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.dataaccess.databases.resources.processing.Normalizer;

import com.adventnet.swissqlapi.sql.functions.aggregate.count;

/**
 * Class that describes a resource database considering information specified
 * from the user in a xml file. Information are retrieved from the xml file
 * Through the JAXB and the relative object is initialized
 */

// Database Resource Description class
@XmlRootElement(name = "Resource")
public class DBResource {

	// Variables
	private String ResourceName;
	private String PlatformName;
	private String PlatformVersion;
	private String HostedOn;
	private String Port = null;
	private String dbguessed = null;
	private List<AccessPoint> ap = null;

	@XmlElement(name = "ID")
	private String id;

	@XmlElementRef
	private Profile profile;

	// Methods
	public String getID() {

		if (id == null)
			id = "";

		return id;

	}

	public String getPort() {

		if (Port == null) {

			Port = "";

		}

		return Port;

	}

	public void setPort(String value) {

		Port = value;

	}

	public String getResourceName() {

		ResourceName = this.profile.getname().trim();

		return ResourceName;

	}

	public String getHostedOn() {

		HostedOn = this.profile.getHostedOn();

		return HostedOn;

	}

	public String getPlatformName() {

		PlatformName = this.profile.getPlatformName();

		if (PlatformName.contains(" ")) {

			PlatformName = PlatformName.trim();

		}

		return PlatformName;

	}

	public void setPlatformName(String name) {

		this.profile.platform.name = name;

	}

	public String getPlatformVersion() {

		PlatformVersion = this.profile.getPlatformVersion();

		return PlatformVersion;

	}

	public void setHostedOn(String value) {

		HostedOn = value;

	}

	public String getDBguessed() {

		return dbguessed;

	}

	public void setDBguessed(String name) {

		dbguessed = name;

	}

	public void normalize(int index) throws Exception {

		try {
			Normalizer.normalize(this, index);
		} catch (IOException e) {

			// e.printStackTrace();

			throw e;
		}

	}

	public List<AccessPoint> getAccessPoints() throws Exception {

		// List<AccessPoint> ap = this.profile.accessPoints();
		//
		// return ap;

		// List<AccessPoint> ap = this.profile.accessPoints();
		if (ap == null) {
			List<AccessPoint> data = this.profile.accessPoints();
			ap = getNoDuplicatedAccessPointsItems(data);
		}
		return ap;
	}

	// returns a list of access points with no duplicated access points. It
	// removes the duplicated access point. We consider that two access points
	// are duplicated if they have these 3 parameters equal: endpoint, username
	// and password
	private List<AccessPoint> getNoDuplicatedAccessPointsItems(
			List<AccessPoint> data) throws Exception {

		List<AccessPoint> result = new ArrayList<AccessPoint>(data);
		List<AccessPoint> ap;

		for (int j = 0; j < result.size(); j++) {
			AccessPoint item = result.get(j);
			ap = new ArrayList<AccessPoint>(result.subList(j, result.size()));

			ap.remove(item);

			for (int i = 0; i < ap.size(); i++) {
				if (item.equals(ap.get(i))) {
					result.remove(ap.get(i));

					AnalysisLogger
							.getLogger()
							.debug("In class DBResource-> duplicated access point element founded and removed");
				}
			}
		}
		return result;
	}

	// Class Profile

	@XmlRootElement(name = "Profile")
	static class Profile {

		@XmlElement(name = "Name")
		private String name;

		@XmlElementRef
		private Platform platform;

		@XmlElementRef
		private Runtime runtime;

		@XmlElementRef
		private List<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

		public String getname() {
			if (name == null)
				name = "";

			return name;
		}

		public List<AccessPoint> accessPoints() {

			return accessPoints;

		}

		public String getHostedOn() {

			return this.runtime.getHostedOn().trim();

		}

		public String getPlatformName() {

			return this.platform.getName();

		}

		public String getPlatformVersion() {

			return this.platform.getVersion();

		}

	}

	// Class Runtime

	@XmlRootElement(name = "RunTime")
	public static class Runtime {

		@XmlElement(name = "HostedOn")
		private String hostedOn;

		public String getHostedOn() {

			if (hostedOn == null)
				hostedOn = "";

			return hostedOn;
		}

	}

	// Class Platform

	@XmlRootElement(name = "Platform")
	public static class Platform {

		private String version;

		@XmlElement(name = "Name")
		private String name;

		@XmlElement(name = "Version")
		private String Version;

		@XmlElement(name = "MinorVersion")
		private String minorVersion;

		@XmlElement(name = "RevisionVersion")
		private String revisionVersion;

		public String getVersion() {

			// Version's computation
			if ((Version == null) || (Version.equals(""))) {

				// Version="8";
				// minorVersion="4";
				// revisionVersion="0";

				Version = "";
				minorVersion = "";
				revisionVersion = "";

				version = Version + "." + minorVersion + "." + revisionVersion;

			} else {
				version = Version;

				if ((minorVersion != null) && (!(minorVersion.equals("")))) {

					version = version.concat(".").concat(minorVersion);

					if ((revisionVersion != null)
							&& (!(revisionVersion.equals("")))) {

						version = version.concat(".").concat(revisionVersion);

					}

				}

			}

			return version;

		}

		public String getName() {

			return name;

		}

	}

	// Class AccessPoint

	@XmlRootElement(name = "AccessPoint")
	public static class AccessPoint {

		private String endpoint;
		// private String port;
		private String username;
		private String password;
		private String DatabaseName = null;
		private String Driver = null;
		private String Dialect = null;
		private String MaxConnections = null;
		private String schema = null;
		private String tableSpaceCount = null;
		private String tableSpacePrefix = null;

		/* it contains the variables aquamapsWorldTable,aquamapsDataStore */
		private HashMap<String, String> auxiliaryProperties = new HashMap<String, String>();

		@XmlElementRef
		private Interface itfce = new Interface();

		@XmlElementRef
		private AccessData accessData = new AccessData();

		@XmlElementWrapper(name = "Properties")
		@XmlElementRef
		private List<Property> properties = new ArrayList<Property>();

		@XmlElement(name = "Description")
		private String description;

		public String name() {

			return itfce.endpoint().name();
		}

		public String address() {

			endpoint = itfce.endpoint().address().trim();

			if (endpoint == null)
				endpoint = "";

			return endpoint;
		}

		public void setUrl(String value) {

			itfce.endpoint.address = value;
			endpoint = itfce.endpoint.address.trim();

		}

		public String getUsername() {

			username = this.accessData.username();

			return username;

		}

		public String getPassword() throws Exception {

			String pwd = this.accessData.password();

			try {
				password = StringEncrypter.getEncrypter().decrypt(pwd);
			} catch (Exception e) {

				// e.printStackTrace();
				throw e;

			}

			return password;

		}

		public String getDescription() {

			if ((description == null) || (description.equals(""))) {

				description = "jdbc connection url";

			}

			return description;

		}

		public String getDatabaseName() {

			if (properties.size() == 0) {

				Property p = new Property();
				p.name = "dbname";
				p.value = "";
				properties.add(p);

				return DatabaseName = "";

			} else {

				for (int i = 0; i < properties.size(); i++) {

					if (((properties.get(i).name()).toLowerCase()
							.contains("dbname"))
							|| (properties.get(i).name()).toLowerCase()
									.contains("databasename")
							|| ((properties.get(i).name()).toLowerCase()
									.contains("database"))) {
						DatabaseName = properties.get(i).value();

					}

				}

				if (DatabaseName == null) {

					Property p = new Property();
					p.name = "dbname";
					p.value = "";
					properties.add(p);

					DatabaseName = "";
					return DatabaseName;
				}

			}

			return DatabaseName;

		}

		public void setDatabaseName(String value) {

			DatabaseName = value;

			for (int i = 0; i < properties.size(); i++) {

				if (((properties.get(i).name()).toLowerCase()
						.contains("dbname"))
						|| (properties.get(i).name()).toLowerCase().contains(
								"databasename")
						|| ((properties.get(i).name()).toLowerCase()
								.contains("database"))) {
					DatabaseName = properties.get(i).setvalue(value);

					AnalysisLogger.getLogger().debug(
							"In class DBResource->setting the database's name to value : "
									+ DatabaseName);

				}

			}

		}

		public String getDriver() {

			if (properties.size() == 0) {

				Property p = new Property();
				p.name = "driver";
				p.value = "";
				properties.add(p);

				return Driver = "";

			} else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).toLowerCase().contains(
							"driver")) {
						Driver = properties.get(i).value();

					}

				}

				if (Driver == null) {

					Property p = new Property();
					p.name = "driver";
					p.value = "";
					properties.add(p);

					Driver = "";

					return Driver;
				}

			}

			return Driver;

		}

		public void SetDriver(String value) {

			// Driver="org"+"."+value+"."+"Driver";

			for (int i = 0; i < properties.size(); i++) {

				if ((properties.get(i).name()).toLowerCase().contains("driver")) {
					Driver = properties.get(i).setvalue(value);

					AnalysisLogger.getLogger().debug(
							"In class DBResource->setting the driver's name to value : "
									+ Driver);

				}

			}

		}

		public String getDialect() {

			if (properties.size() == 0) {

				Property p = new Property();
				p.name = "dialect";
				p.value = "";
				properties.add(p);

				return Dialect = "";

			} else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).toLowerCase().contains(
							"dialect")) {
						Dialect = properties.get(i).value();

					}

				}

				if (Dialect == null) {

					Property p = new Property();
					p.name = "dialect";
					p.value = "";
					properties.add(p);

					Dialect = "";

					return Dialect;
				}

			}

			return Dialect;

		}

		public void SetDialect(String value) {

			// Driver="org"+"."+value+"."+"Driver";

			for (int i = 0; i < properties.size(); i++) {

				if ((properties.get(i).name()).toLowerCase()
						.contains("dialect")) {

					Dialect = properties.get(i).setvalue(value);

					AnalysisLogger.getLogger().debug(
							"In class DBResource->Setting the dialect: "
									+ Dialect);

				}

			}

		}

		public String getMaxConnections() {

			/*
			 * Check if the AccessPoint object does not have a Properties
			 * section
			 */
			if (properties.size() == 0) {

				return MaxConnections = "2";
			}

			else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).equals("maxConnection")) {
						MaxConnections = properties.get(i).value();

					}

				}

				if (MaxConnections == null)
					return MaxConnections = "2";
				else
					return MaxConnections;

			}

		}

		public String getSchema() {

			/*
			 * Check if the AccessPoint object does not have a Properties
			 * section
			 */
			if (properties.size() == 0)
				return schema = "public";

			else {

				for (int i = 0; i < properties.size(); i++) {

					//
					if ((properties.get(i).name()).equals("schema")) {
						schema = properties.get(i).value();

					}

				}

				if (schema == null)
					return schema = "public";
				else
					return schema;

			}

		}

		public String getTableSpaceCount() {

			/*
			 * Check if the AccessPoint object does not have a Properties
			 * section
			 */
			if (properties.size() == 0)
				return tableSpaceCount = "0";

			else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).equals("tableSpaceCount")) {
						tableSpaceCount = properties.get(i).value();

					}

				}

				if (tableSpaceCount == null)
					return tableSpaceCount = "0";
				else
					return tableSpaceCount;

			}

		}

		public String getTableSpacePrefix() {

			/*
			 * Check if the AccessPoint object does not have a Properties
			 * section
			 */
			if (properties.size() == 0)
				return tableSpacePrefix = "";

			else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).equals("tableSpacePrefix")) {
						tableSpacePrefix = properties.get(i).value();

					}

				}

				if (tableSpacePrefix == null)
					return tableSpacePrefix = "";
				else
					return tableSpacePrefix;

			}

		}

		public HashMap<String, String> getAuxiliaryProperties() {

			String AuxiliaryProperty;

			/*
			 * Check if the AccessPoint object does not have a Properties
			 * section
			 */
			if (properties.size() == 0)
				return auxiliaryProperties;

			else {

				for (int i = 0; i < properties.size(); i++) {

					if ((properties.get(i).name()).equals("aquamapsWorldTable")) {
						AuxiliaryProperty = properties.get(i).value();

						auxiliaryProperties.put("aquamapsWorldTable",
								AuxiliaryProperty);

					}

					if ((properties.get(i).name()).equals("aquamapsDataStore")) {
						AuxiliaryProperty = properties.get(i).value();

						auxiliaryProperties.put("aquamapsDataStore",
								AuxiliaryProperty);

					}

				}

				return auxiliaryProperties;

			}

		}

		// it checks if two access points have these 3 parameters equal:
		// endpoint, username and password
		public boolean equals(AccessPoint obj) throws Exception {
			try {
				if ((this.address().equals(obj.address()))
						&& (this.getUsername().equals(obj.getUsername()))
						&& (this.getPassword().equals(obj.getPassword()))) {
					// to check if elements are not equal
					// System.out.println(this.getDescription());
					// System.out.println(obj.getDescription());
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				// e.printStackTrace();
				throw e;
			}
		}
	}

	// Class Interface
	@XmlRootElement(name = "Interface")
	public static class Interface {

		@XmlElementRef
		private Endpoint endpoint = new Endpoint();

		public Endpoint endpoint() {
			return endpoint;
		}
	}

	// Class Endpoint
	@XmlRootElement(name = "Endpoint")
	public static class Endpoint {

		@XmlAttribute(name = "EntryName")
		private String name;

		@XmlValue
		private String address;

		public String name() {
			return name;
		}

		public String address() {

			if (address == null)
				address = "";
			return address;
		}

	}

	// Class AccessData
	@XmlRootElement(name = "AccessData")
	public static class AccessData {

		@XmlElement(name = "Username")
		private String username;

		@XmlElement(name = "Password")
		private String password;

		public String username() {

			if ((username == null) || (username.equals(""))) {

				username = "gcube";

			}

			return username;
		}

		public String password() {

			if ((password == null) || (password.equals(""))) {

				password = "d4science";

			}
			return password;
		}

	}

	// Class Property
	@XmlRootElement(name = "Property")
	public static class Property {

		@XmlElement(name = "Name")
		private String name;

		@XmlElement(name = "Value")
		private String value;

		public String name() {
			return name;
		}

		public String value() {
			return value;
		}

		public String setvalue(String val) {

			value = val;
			return value;

		}

	}

}
