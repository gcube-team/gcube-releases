package org.gcube.portal.social.networking.ws.providers;

import java.io.IOException;

import org.gcube.portal.databook.shared.JobStatusType;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Deserializer used to map a string representing the status in this JobNotificationBean to the JobStatusType enum.
 * @author Costantino Perciante at ISTI-CNR (costantino.perciante@isti.cnr.it)
 */
public class JobStatusTypeDeserializer extends JsonDeserializer<JobStatusType>{
	
	private static final org.slf4j.Logger logger = LoggerFactory.getLogger(JobStatusTypeDeserializer.class);
	
	@Override
	public JobStatusType deserialize(JsonParser p,
			DeserializationContext ctxt) throws IOException,
			JsonProcessingException {
		
		logger.debug("Status deserializer called");

		String status = p.getText();
		JobStatusType toReturn = null;
		
		logger.debug("Status coming from json object is " + status);
		
		JobStatusType[] values = JobStatusType.values();

		for (JobStatusType jobStatusType : values) {
			if(jobStatusType.toString().toLowerCase().contains(status.toLowerCase())){
				toReturn = jobStatusType;
				break;
			}
		}

		logger.debug("JOB STATUS deserialized as " + toReturn);
		return toReturn;
	}

}