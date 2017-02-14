package gr.cite.repo.auth.app.utils;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonInclude(Include.NON_NULL)
public class ErrorWithPadding { 

	@JsonProperty
	private int statusCode;
	@JsonProperty
	private String message;

	public ErrorWithPadding setStatusCode(int statusCode) {
		this.statusCode = statusCode;
		return this;
	}
	
	public int getStatusCode() {
		return statusCode;
	}
	
	public ErrorWithPadding setMessage(String message) {
		this.message = message;
		return this;
	}
	
	public String getMessage() {
		return message;
	}
	
//	/**
//	 * as Json String
//	 */
//	@Override
//	public String toString() {
//		ObjectMapper objectMapper = new ObjectMapper();
//		StringWriter w = new StringWriter();
//		try {
//			objectMapper.writeValue(w ,this);
//		} catch (IOException e) {
//			return null;
//		}
//		return w.toString();
//	}

}
