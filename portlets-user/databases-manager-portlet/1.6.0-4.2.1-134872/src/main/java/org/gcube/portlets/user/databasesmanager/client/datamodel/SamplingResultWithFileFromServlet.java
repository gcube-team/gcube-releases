package org.gcube.portlets.user.databasesmanager.client.datamodel;

import java.io.Serializable;
import java.util.List;

import com.extjs.gxt.ui.client.data.BaseModelData;

public class SamplingResultWithFileFromServlet extends BaseModelData implements
		Serializable {
	private static final long serialVersionUID = 1L;

	private List<Result> output;
//	private String fileName;
	private String urlFile;

	public SamplingResultWithFileFromServlet() {
	}

//	public SamplingResultWithFileFromServlet(List<Result> result, String fileName) {
//		set("output", result);
//		set("fileName", fileName);
//	}
	
	public SamplingResultWithFileFromServlet(List<Result> result, String urlFile) {
		set("output", result);
		set("urlFile", urlFile);
	}

	public List<Result> getListOutput() {
       return get("output");
	}

//	public String getFileName() {
//		return get("fileName");
//	}

	public String getUrlFile(){
		return get("urlFile");
	}
}
