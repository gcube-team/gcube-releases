package gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils;

public enum StorageTypes {
	PATH, GRS2, FTP, TM, JDBC;

	public String protocolPrefix() {
		switch (this) {
		case PATH:
			return "file:";
		case GRS2:
			return " grs2-proxy://";
		case FTP:
			return "ftp://";
		case TM:
			return "tm://";
		case JDBC:
			return "jdbc:";
		default:
			return null;
		}
	}
}
