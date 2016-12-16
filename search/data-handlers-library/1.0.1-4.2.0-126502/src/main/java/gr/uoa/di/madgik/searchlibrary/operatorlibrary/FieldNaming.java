package gr.uoa.di.madgik.searchlibrary.operatorlibrary;

public class FieldNaming {
	public enum LocalFieldName {
		id, bytestream, mimeType
	}
	
	public enum FTPFieldName {
		id, bytestream, mimeType
	}

	public enum CMFieldName {
		id, name, creationTime, lastUpdateTime, mimeType, length, bytestream, bytestreamURI, language, schemaURI, schemaName
	}

	public enum TMFieldName {
		id, sourceId, uri, payload
	}

}
