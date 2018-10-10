package org.gcube.portlets.user.performfish.util.db;

public class DB_Credentials {

	private String DBURL, DBName, user, pwd;

	public DB_Credentials(String DBURL, String DBName, String user, String pwd) {
		super();
		this.DBURL = DBURL;
		this.DBName = DBName;
		this.user = user;
		this.pwd = pwd;
	}
	
	public DB_Credentials() {
		// TODO Auto-generated constructor stub
	}

	public String getJDBCURL() {
		return new StringBuilder("jdbc:postgresql://").append(DBURL).append("/").append(DBName).append("?user=").append(this.user).toString();
	}

	public String getDBURL() {
		return DBURL;
	}

	public void setDBURL(String dBURL) {
		DBURL = dBURL;
	}

	public String getDBName() {
		return DBName;
	}

	public void setDBName(String dBName) {
		DBName = dBName;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("DB_Credentials [DBURL=");
		builder.append(DBURL);
		builder.append(", DBName=");
		builder.append(DBName);
		builder.append(", user=");
		builder.append(user);
		builder.append(", pwd=");
		builder.append(pwd);
		builder.append("]");
		return builder.toString();
	}
	
}
