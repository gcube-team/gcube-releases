package gr.cite.bluebridge.analytics.web;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WrongItemTypeException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;

import gr.cite.bluebridge.analytics.model.Economics;
import gr.cite.bluebridge.workspace.exceptions.CustomException;

public class WorkspaceUtils {
	
	private static Logger logger = LoggerFactory.getLogger(WorkspaceUtils.class);
	private static final ObjectMapper mapper = new ObjectMapper();
	
	public static boolean isValidAnalysis(String economics) {

		try {
			mapper.readValue(economics, Economics.class);
		} catch (IOException e) {
			logger.debug("Unparsable Analysis", e);
			return false;
		}

		return true;
	}
	
	public static void isEmpty(String field) throws CustomException{	
		if(field == null || field.length() < 1){			
			throw new CustomException(400, "Field is required and cannot be empty");
		}
	}
	
	public static void fileExists(Workspace ws, String fileName, String destinationFolderId) throws Exception {	
		try {
			if(ws.exists(fileName, destinationFolderId)){		
				throw new CustomException(422, "File " + fileName + " already exists!");
			}
		} catch (InternalErrorException | ItemNotFoundException | WrongItemTypeException e) {
			throw e;
		}	
	}
	
	public static String streamToString(InputStream inputStream) throws IOException{
		ByteArrayOutputStream result = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int length;
		while ((length = inputStream.read(buffer)) != -1) {
		    result.write(buffer, 0, length);
		}
		return result.toString("UTF-8");
	}
}
