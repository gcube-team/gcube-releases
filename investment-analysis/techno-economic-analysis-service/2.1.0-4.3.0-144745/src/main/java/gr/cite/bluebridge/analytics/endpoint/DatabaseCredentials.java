package gr.cite.bluebridge.analytics.endpoint;

public class DatabaseCredentials {
	private String dbname;	
	private String dbuser;
	private String dbpass;
	private String dbhost;
	
	public String getDbname() {
		return dbname;
	}
	public void setDbname(String dbname) {
		this.dbname = dbname;
	}
	public String getDbuser() {
		return dbuser;
	}
	public void setDbuser(String dbuser) {
		this.dbuser = dbuser;
	}
	public String getDbpass() {
		return dbpass;
	}
	public void setDbpass(String dbpass) {
		this.dbpass = dbpass;
	}
	public String getDbhost() {
		return dbhost;
	}
	public void setDbhost(String dbhost) {
		this.dbhost = dbhost;
	}
	
	public DatabaseCredentials clone(){
		DatabaseCredentials clone = new DatabaseCredentials();
		clone.setDbhost(dbhost);
		clone.setDbname(dbname);
		clone.setDbpass(dbpass);
		clone.setDbuser(dbuser);
		return clone;
	}
	
	@Override
	public int hashCode(){
		final int prime = 31;
		int result = 1;
		
		result = prime * result + ((dbhost == null) ? 0 : dbhost.hashCode());
		result = prime * result + ((dbname == null) ? 0 : dbname.hashCode());
		result = prime * result + ((dbuser == null) ? 0 : dbuser.hashCode());
		result = prime * result + ((dbpass == null) ? 0 : dbpass.hashCode());
		
		return result;
	}
}