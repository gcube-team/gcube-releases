package gr.cite.bluebridge.analytics.web;

import java.io.IOException;

import javax.portlet.ResourceResponse;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.liferay.portal.kernel.json.JSONFactoryUtil;
import com.liferay.portal.kernel.json.JSONSerializer;

public class PortletUtils {	
	
	private static Logger logger = LoggerFactory.getLogger(PortletUtils.class);

	public static String buildJSON(Object object){
		JSONSerializer jsonSerializer = JSONFactoryUtil.createJSONSerializer();
		return jsonSerializer.serializeDeep(object).toString();
	}	
	
	public static void returnResponse(ResourceResponse response, Object statusCode, Object object){		
		try {
			if(statusCode instanceof Integer){
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, Integer.toString((int) statusCode));
			}else{
				response.setProperty(ResourceResponse.HTTP_STATUS_CODE, (String) statusCode);
			}			
			response.getWriter().write(object.toString());
		} catch (IOException e) {
			logger.error("Could not return response", e);
		}
	}
	
	public static void returnResponse(ResourceResponse response, Response clientResponse){		
		String result = clientResponse.readEntity(String.class);
		String status = Integer.toString(clientResponse.getStatus());
		
		if(!status.equals("200")){
			logger.info("Error. Request has failed: " + result);
		}else{
			logger.info("Request has been successful");
		}
		
		logger.debug("Result = " + result);

		try {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, status);
			response.getWriter().write(result);
		} catch (IOException e) {
			logger.error(e.getMessage(), e);
		}
	}
	

	public static void returnResponseAsJson(ResourceResponse response, Integer statusCode, Object object){	
		PortletUtils.returnResponseAsJson(response, Integer.toString(statusCode), object);
	}	

	public static void returnResponseAsJson(ResourceResponse response, String statusCode, Object object){	
		try {
			response.setProperty(ResourceResponse.HTTP_STATUS_CODE, statusCode);			
			response.getWriter().write(buildJSON(object));
		} catch (IOException e) {
			logger.error("Could not return response as JSON", e);
		}
	}
}
