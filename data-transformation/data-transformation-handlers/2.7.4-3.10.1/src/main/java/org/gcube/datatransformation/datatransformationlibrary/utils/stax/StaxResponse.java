package org.gcube.datatransformation.datatransformationlibrary.utils.stax;

public class StaxResponse {
	private String result;
	private String path;

	public StaxResponse(String result, String path) {
		this.result = result;
		this.path = path;
	}

	public String getResult() {
		return result;
	}

	public String getPath() {
		return path;
	}

	@Override
	public String toString() {
		return "StaxResponse [result=" + result + ", path=" + path + "]";
	}
	
	
}
