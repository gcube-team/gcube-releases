package org.gcube.data.analysis.wps;

import java.io.InputStream;

import org.n52.wps.server.ExceptionReport;
import org.n52.wps.server.response.Response;

public class ExecuteResponse extends Response {

	private ExecuteResponseBuilder builder;
	
	public ExecuteResponse(ExecuteRequest request) throws ExceptionReport{
		super(request);
		this.builder = ((ExecuteRequest)this.request).getExecuteResponseBuilder();
	}
	
    @Override
	public InputStream getAsStream() throws ExceptionReport{
		return this.builder.getAsStream();
	}
	
	public ExecuteResponseBuilder getExecuteResponseBuilder(){
		return builder;
	}
	
	public String getMimeType(){
		return builder.getMimeType();
	}
}