package gr.cite.regional.data.collection.application.controllers;

import gr.cite.regional.data.collection.dataccess.exceptions.ServiceException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.NoSuchElementException;

@Component
public class BaseController {
	private static final Logger logger = LogManager.getLogger(BaseController.class);
	static final ObjectMapper objectMapper = new ObjectMapper();
	
	@ExceptionHandler(ServiceException.class)
	public final ResponseEntity<String> handleServiceLayerException(ServiceException e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}
	
	@ExceptionHandler(NoSuchElementException.class)
	public final ResponseEntity<String> handleNoSuchElementException(NoSuchElementException e) {
		logger.info(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.NOT_FOUND).body(e.getMessage());
	}
	
	@ExceptionHandler(IllegalArgumentException.class)
	public final ResponseEntity<String> handleIllegalArgumentException(IllegalArgumentException e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
	}
	
	@ExceptionHandler(Exception.class)
	public final ResponseEntity<String> handleGenericException(Exception e) {
		logger.error(e.getMessage(), e);
		return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(e.getMessage());
	}

}
