package org.gcube.data.spd.resources;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.gcube.data.spd.model.Constants;
import org.gcube.data.spd.model.service.exceptions.InvalidIdentifierException;
import org.gcube.data.spd.model.util.SerializableList;
import org.gcube.data.spd.utils.DynamicList;
import org.gcube.data.spd.utils.DynamicMap;
import org.gcube.data.spd.utils.ResultWrapperMantainer;
import org.glassfish.jersey.server.ChunkedOutput;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path(value = Constants.RESULTSET_PATH)
public class ResultSetEndpoint {

	private static Logger logger = LoggerFactory.getLogger(ResultSetEndpoint.class);
		
	@GET
	@Produces(MediaType.TEXT_XML)
	@Path("{locator}")
	public ChunkedOutput<String> get(@PathParam("locator") String locator){
		logger.info("requesting locator {} ",locator);
		return ResultWrapperMantainer.getWriterById(locator).getOutput();
	}
	
	@DELETE
	@Produces(MediaType.TEXT_XML)
	@Path("{locator}")
	public void close(@PathParam("locator") String locator){
		logger.info("removing locator {} ",locator);
		ResultWrapperMantainer.remove(locator);
	}

	@PUT
	@Consumes(MediaType.APPLICATION_XML)
	@Path("{locator}")
	public boolean sendInput(@PathParam("locator") String id,  SerializableList<String> input) throws InvalidIdentifierException {
		//String node;
		DynamicList list = DynamicMap.get(id);
		if (list==null){
			logger.error("id not valid "+id);
			throw new InvalidIdentifierException(id);
		}
		logger.trace("input id is {} ",id);
		if (input.getValuesList().isEmpty()){
			logger.info("closing input stream");
			DynamicMap.remove(id);
		}
		else {
			for (String singleInput : input.getValuesList()){
				logger.trace("elaborating input",singleInput);
				if (!list.add(singleInput)) return false;
			}
		}
		return true;
	}
	
}
