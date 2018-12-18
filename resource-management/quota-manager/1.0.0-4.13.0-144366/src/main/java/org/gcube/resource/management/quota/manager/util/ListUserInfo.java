package org.gcube.resource.management.quota.manager.util;

import java.util.Map;
/**
 * 
 * @author pieve
 *
 */
public class ListUserInfo {
	/*
 	"success" : false,
  	"message" : "String index out of range: 5",
  	"result" : null
	 */
    private Boolean success;
    private String message;
    private Map<String, String> result;
	public Boolean getSuccess() {
		return success;
	}
	public void setSuccess(Boolean success) {
		this.success = success;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public Map<String, String> getResult() {
		return result;
	}
	public void setResult(Map<String, String> result) {
		this.result = result;
	}
	
   
}