package gr.cite.bluebridge.analytics.web;

import javax.ws.rs.core.Response.Status;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomResponseEntity<T> extends ResponseEntity<T> {
	public CustomResponseEntity(T body, Status status) {
		super(body, null, HttpStatus.valueOf(status.getStatusCode()));
	}
	
	public CustomResponseEntity(Status status, T body) {
		super(body, null, HttpStatus.valueOf(status.getStatusCode()));
	}
	
	public CustomResponseEntity(T body, HttpStatus status) {
		super(body, null, status);
	}
	
	public CustomResponseEntity(HttpStatus status, T body) {
		super(body, null, status);
	}
	
	public CustomResponseEntity(T body, Integer status) {
		super(body, null, HttpStatus.valueOf(status));
	}
	
	public CustomResponseEntity(Integer status, T body) {
		super(body, null,HttpStatus.valueOf(status));
	}
}
