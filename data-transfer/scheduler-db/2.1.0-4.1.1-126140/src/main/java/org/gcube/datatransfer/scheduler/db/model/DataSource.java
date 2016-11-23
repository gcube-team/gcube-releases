package org.gcube.datatransfer.scheduler.db.model;


import javax.jdo.annotations.PersistenceCapable;
import javax.jdo.annotations.Persistent;
import javax.jdo.annotations.PrimaryKey;


//@Persistent(customValueStrategy="uuid") katw apo to annotation tou primary key


@PersistenceCapable(table="DATASOURCE")
public class DataSource implements java.io.Serializable  {

	private static final long serialVersionUID = -8200619255632863470L;

	@PrimaryKey
	private String dataSourceId;
	//tmpDataSource structure: 
	// resultIdOfIS--name--description--endpoint--username--password--propertyFolders
	
	private String dataSourceIdOfIS;
	private String dataSourceName;
	private String cardinality;
	private String type;
	private String status;
	
	//adds
	private String scope;
	private String description;
	private String endpoint;
	private String username;
	private String pass;
	private String folder;

	//private String[] inputUrls;   the inputUrl can be found in TransferObject
	//private String InputPattern;
	
	
	public DataSource(){
		this.dataSourceName=null;
		this.cardinality=null;
		this.type=null;
		this.endpoint=null;
		this.scope=null;
		this.username=null;	
		this.folder=null;
		this.description=null;
		this.pass=null;
	}
	
	//@Id
	public String getDataSourceId() {
		return dataSourceId;
	}
	public void setDataSourceId(String dataSourceId) {
		this.dataSourceId = dataSourceId;
	}
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	public String getCardinality() {
		return cardinality;
	}
	public void setCardinality(String cardinality) {
		this.cardinality = cardinality;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}


	public String getEndpoint() {
		return endpoint;
	}

	public void setEndpoint(String endpoint) {
		this.endpoint = endpoint;
	}

	public String getScope() {
		return scope;
	}
	public void setScope(String scope) {
		this.scope = scope;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}


	public String getPass() {
		return pass;
	}

	public void setPass(String pass) {
		this.pass = pass;
	}

	public String getDataSourceIdOfIS() {
		return dataSourceIdOfIS;
	}

	public void setDataSourceIdOfIS(String dataSourceIdOfIS) {
		this.dataSourceIdOfIS = dataSourceIdOfIS;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getFolder() {
		return folder;
	}

	public void setFolder(String folder) {
		this.folder = folder;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	

}
