package org.gcube.resource.management.quota.manager.util;

import java.util.List;
/**
 * 
 * @author pieve
 *
 */
public class ListUser {
	/*
 	"success" : false,
  	"message" : "String index out of range: 5",
  	"result" : null
	 */
    private Boolean success;
    private String message;
    private List<String> result;
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
	public List<String> getResult() {
		return result;
	}
	public void setResult(List<String> result) {
		this.result = result;
	}
	
   
}