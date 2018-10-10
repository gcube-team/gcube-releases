package org.gcube.portlets.user.databasesmanager.client.datamodel;

//import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;
import com.google.gwt.user.client.rpc.IsSerializable;

//class that represents an item that will be displayed in the tree. 
//The item can be a resource, a database, a schema and a table.
public class FileModel extends BaseModelData implements IsSerializable {

	private static final long serialVersionUID = 1L;

	private static int ID = 1;
	// private int id;
	private boolean isExpanded = true;
	// set to true value if the object has been managed calling rpc remote
	// methods
	private boolean isLoaded = false;
	// set if the object is a table
	private boolean isTable = false;
	// set if the object is a schema
	private boolean isSchema = false;
	// set if the object is a database
	private boolean isDatabase = false;
	// set if the node in the tree is collapsed
	private boolean isCollapsed = false;
	// to keep track information about the database
	private List<Result> DBInfo = null;
	// set if database information are displayed
	private boolean isDBInfoDisplayed = false;
	// to keep track information about the details of a table
	private List<Result> TableDetails = null;
	// set if table information are displayed
	private boolean isTableDetailsDisplayed = false;
	// to keep track of the database type
	private String DatabaseType = null;
	// resource data
	private String ResourceName = null;
	private String DatabaseName = null;
	//flag data cached
//	private boolean isDataCached = false;

	public FileModel() {
		setId();
		// setIsExpanded(true);
		// setIsSchema(false);
	}

	public FileModel(String name) {
		setName(name);
		setId();
	}

	// constructor for the root element in the tree
	public FileModel(String name, int id) {
		setName(name);
		set("ID", id);
	}

	public void setId() {
		set("ID", ID++);
	}

	public int getId() {
		return get("ID");
	}

	public void setName(String name) {
		set("name", name);
	}

	public String getName() {
		return get("name");
	}

	public void setIsExpanded(boolean value) {
		isExpanded = value;
	}

	public boolean isExpanded() {
		return isExpanded;
	}

	public void setIsLoaded(boolean value) {
		isLoaded = value;
	}

	public boolean isLoaded() {
		return isLoaded;
	}

	// // set if the object is a schema
	// public void setIsSchema(boolean value) {
	// set("isSchema", value);
	// }
	//
	// public boolean isSchema() {
	// return get("isSchema");
	// }
	
	// public void setIsDataCached(boolean value){
	// set("isDataCached", value);
	// }
	// public boolean IsDataCached(){
	// return get("isDataCached");
	// }

	// set if the object is a schema
	public void setIsSchema(boolean value) {
		// set("isSchema", value);
		isSchema = value;
	}

	public boolean isSchema() {
		// return get("isSchema");
		return isSchema;
	}

	// // set if the object is a table
	// public void setIsTable(boolean value) {
	// set("isTable", value);
	// }
	//
	// public boolean isTable() {
	// return get("isTable");
	// }

	// set if the object is a table
	public void setIsTable(boolean value) {
		// set("isTable", value);
		isTable = value;
	}

	public boolean isTable() {
		// return get("isTable");
		return isTable;
	}

	public void setIsDatabase(boolean value) {
		isDatabase = value;
	}

	public boolean isDatabase() {
		return isDatabase;
	}

	public void setDBInfo(List<Result> data) {
		DBInfo = data;
	}

	public List<Result> getDBInfo() {
		return DBInfo;
	}

	public void setTableDetails(List<Result> data) {
		TableDetails = data;
	}

	public List<Result> getTableDetails() {
		return TableDetails;
	}

	public void setIsDBInfoDisplayed(boolean value) {
		isDBInfoDisplayed = value;
	}

	public boolean isDBInfoDisplayed() {
		return isDBInfoDisplayed;
	}

	public void setTableDetailsDisplayed(boolean value) {
		isTableDetailsDisplayed = value;
	}

	public boolean isTableDetailsDisplayed() {
		return isTableDetailsDisplayed;
	}

	public void setDatabaseType(String value) {
		DatabaseType = value;
	}

	public String getDatabaseType() {
		return this.DatabaseType;
	}

	public void setResourceName(String value) {
		ResourceName = value;
	}

	public String getResourceName() {
		return ResourceName;
	}

	public void setDatabaseName(String value) {
		DatabaseName = value;
	}

	public String getDatabaseName() {
		return DatabaseName;
	}

	public void setIsCollapsed(boolean value) {
		isCollapsed = true;
	}

	public boolean getIsCollapsed() {
		return isCollapsed;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj != null && obj instanceof FileModel) {
			FileModel mobj = (FileModel) obj;
			return getName().equals(mobj.getName());
		}
		return super.equals(obj);
	}

	@Override
	public String toString() {
		return "FileModel [isExpanded=" + isExpanded + ", isLoaded=" + isLoaded
				+ ", isTable=" + isTable + ", isSchema=" + isSchema
				+ ", isDatabase=" + isDatabase + ", isCollapsed=" + isCollapsed
				+ ", DBInfo=" + DBInfo + ", isDBInfoDisplayed="
				+ isDBInfoDisplayed + ", TableDetails=" + TableDetails
				+ ", isTableDetailsDisplayed=" + isTableDetailsDisplayed
				+ ", DatabaseType=" + DatabaseType + ", ResourceName="
				+ ResourceName + ", DatabaseName=" + DatabaseName + "]";
	}
	
	
	
}