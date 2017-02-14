package gr.uoa.di.madgik.searchlibrary.operatorlibrary.utils;

public class StorageUtils {
	public static StorageTypes evaluate(String str) throws Exception {
		if (str.startsWith(StorageTypes.PATH.protocolPrefix()))
			return StorageTypes.PATH;
		else if (str.startsWith(StorageTypes.GRS2.protocolPrefix()))
			return StorageTypes.GRS2;
		else if (str.startsWith(StorageTypes.FTP.protocolPrefix()))
			return StorageTypes.FTP;
		else if (str.startsWith(StorageTypes.TM.protocolPrefix()))
			return StorageTypes.TM;
		else if (str.startsWith(StorageTypes.JDBC.protocolPrefix()))
			return StorageTypes.JDBC;
		
		throw new Exception("Undefined storage type");
	}
	
	public static String removeProtocolPrefix(String str) throws Exception {
		switch (evaluate(str)) {
		case PATH:
			str = str.replaceFirst(StorageTypes.PATH.protocolPrefix(), "");
			break;
		default:
			break;
		}
		return str;
	}
}
