package gr.cite.geoanalytics.util.http;

import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class CustomResponseEntity<T> extends ResponseEntity<T> {
	
	private static final Logger logger = LoggerFactory.getLogger(CustomResponseEntity.class);
	
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
		
	public CustomResponseEntity(T body, HttpStatus status, Exception e) {
		super(body, null, status);
		if(e != null){
			logger.error(null, e);
		}
	}
	
	public CustomResponseEntity(HttpStatus status, T body, Exception e) {
		super(body, null, status);
		if(e != null){
			logger.error(null, e);
		}
	}
	
	public CustomResponseEntity(T body, Integer status, Exception e) {
		super(body, null, HttpStatus.valueOf(status));
		if(e != null){
			logger.error(null, e);
		}
	}
	
	public CustomResponseEntity(Integer status, T body, Exception e) {
		super(body, null,HttpStatus.valueOf(status));
		if(e != null){
			logger.error(null, e);
		}
	}
}