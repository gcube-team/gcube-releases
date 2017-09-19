package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SubmitQueryResultWithFileFromServlet extends BaseModelData
		implements Serializable {
	private static final long serialVersionUID = 1L;

	private List<String> attributes;
	private String convertedQuery;
	private String urlFile;
	private int submitQueryTotalRows;


	public SubmitQueryResultWithFileFromServlet() {
	}

//	public SubmitQueryResultWithFileFromServlet(List<String> attributes,
//			String query, String fileName, String urlFile) {
//		set("attributes", attributes);
//		set("convertedQuery", query);
//		set("fileName", fileName);
//		set("urlFile", urlFile);
//		
//	}
	
	public SubmitQueryResultWithFileFromServlet(List<String> attributes,
			String query, String urlFile, int submitQueryTotalRows) {
		set("attributes", attributes);
		set("convertedQuery", query);
//		set("fileName", fileName);
		set("urlFile", urlFile);
		set("submitQueryTotalRows", submitQueryTotalRows);
		
	}

	public List<String> getListOutput() {
		return get("attributes");
	}

//	public String getFileName() {
//		return get("fileName");
//	}

	public String getConvertedQuery() {
		return get("convertedQuery");
	}
	
	public String getUrlFile(){
		return get("urlFile");
	}
	
	public int getSubmitQueryTotalRows(){
		return get("submitQueryTotalRows");
	}

	@Override
	public String toString() {
		return "SubmitQueryResultWithFileFromServlet [attributes=" + attributes
				+ ", convertedQuery=" + convertedQuery + ", urlFile=" + urlFile
				+ ", submitQueryTotalRows=" + submitQueryTotalRows + "]";
	}
	
	
		
}
