package org.gcube.application.perform.service.rest;

import java.sql.SQLException;
import java.util.Collections;
import java.util.HashMap;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.gcube.application.perform.service.PerformServiceManager;
import org.gcube.application.perform.service.ServiceConstants;
import org.gcube.application.perform.service.engine.MappingManager;
import org.gcube.application.perform.service.engine.model.BeanNotFound;
import org.gcube.application.perform.service.engine.model.DBField;
import org.gcube.application.perform.service.engine.model.DBQueryDescriptor;
import org.gcube.application.perform.service.engine.model.InternalException;
import org.gcube.application.perform.service.engine.model.InvalidRequestException;
import org.gcube.application.perform.service.engine.model.anagraphic.Batch;
import org.gcube.application.perform.service.engine.model.anagraphic.Farm;
import org.gcube.common.authorization.control.annotations.AuthorizationControl;
import org.gcube.smartgears.annotations.ManagedBy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@Path(ServiceConstants.Mappings.PATH)

@ManagedBy(PerformServiceManager.class)


public class Mappings {

	private static final Logger log= LoggerFactory.getLogger(Mappings.class);
	
	
	@Inject 
	private MappingManager mappings;
	
	/**
	 * Returns Complete Batch Bean
	 * 
	 * @param name
	 * @param type
	 * @param farmid
	 * @return
	 */

		
	
	@GET
	@Path(ServiceConstants.Mappings.BATCHES_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	@AuthorizationControl(allowed={ServiceConstants.Mappings.AUTH}, exception=MyAuthException.class)
	public Batch getBatch(@QueryParam(ServiceConstants.Mappings.BATCH_NAME_PARAMETER) String name,
			@QueryParam(ServiceConstants.Mappings.BATCH_TYPE_PARAMETER) String type,
			@QueryParam(ServiceConstants.Mappings.FARM_ID_PARAMETER) Long farmid) {

		InterfaceCommons.checkMandatory(name, ServiceConstants.Mappings.BATCH_NAME_PARAMETER);
		InterfaceCommons.checkMandatory(type, ServiceConstants.Mappings.BATCH_TYPE_PARAMETER);
		InterfaceCommons.checkMandatory(farmid, ServiceConstants.Mappings.FARM_ID_PARAMETER);

		HashMap<DBField,Object> condition=new HashMap<DBField,Object>();

		condition.put(DBField.Batch.fields.get(DBField.Batch.BATCH_NAME), name);
		condition.put(DBField.Batch.fields.get(DBField.Batch.BATCH_TYPE), type);
		condition.put(DBField.Batch.fields.get(DBField.Batch.FARM_ID), farmid);
		
		DBQueryDescriptor desc=new DBQueryDescriptor(condition);
		
		try{
			return mappings.getBatch(desc);
		}catch(BeanNotFound e) {
			log.debug("Exception while getting Batch",e);
			throw new WebApplicationException("Unable to find Batch with condition "+desc,Response.Status.NOT_FOUND);
		}catch(SQLException e) {
			log.debug("Exception while getting Batch",e);
			throw new WebApplicationException("Unexpected Exception occurred while dealing with database.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvalidRequestException e) {
			log.debug("Exception while getting Batch",e);
			throw new WebApplicationException("Unable to search for Batch. ",e,Response.Status.BAD_REQUEST);
		} catch (InternalException e) {			
			log.warn("Unexpected Exception while getting Batch",e);
			throw new WebApplicationException("Unexpected Exception.", e,Response.Status.INTERNAL_SERVER_ERROR); 
		}catch(Throwable t) {
			log.warn("Unexpected Exception while getting Batch",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}


	/**
	 * Returns complete FARM Bean
	 * 
	 * @param farmid
	 * @param farmuuid
	 * @return
	 */
	@GET
	@Path(ServiceConstants.Mappings.FARM_METHOD)
	@Produces(MediaType.APPLICATION_JSON)
	@AuthorizationControl(allowed={ServiceConstants.Mappings.AUTH}, exception=MyAuthException.class)
	public Farm getFarm(@Context UriInfo info){
		
		
		DBQueryDescriptor desc=null;
		
		try {
			String farmidString=info.getQueryParameters().getFirst(ServiceConstants.Mappings.FARM_ID_PARAMETER);
			InterfaceCommons.checkMandatory(farmidString, ServiceConstants.Mappings.FARM_ID_PARAMETER);
			desc=new DBQueryDescriptor(Collections.singletonMap(DBField.Farm.fields.get(DBField.Farm.FARM_ID), Long.parseLong(farmidString)));
		}catch(WebApplicationException e) {
			try {
				String farmUUID=info.getQueryParameters().getFirst(ServiceConstants.Mappings.FARM_UUID_PARAMETER);
				InterfaceCommons.checkMandatory(farmUUID, ServiceConstants.Mappings.FARM_UUID_PARAMETER);
				desc=new DBQueryDescriptor(Collections.singletonMap(DBField.Farm.fields.get(DBField.Farm.UUID), farmUUID));
			}catch(WebApplicationException e1) {
				throw new WebApplicationException("Specify either "+ServiceConstants.Mappings.FARM_UUID_PARAMETER+" or "+ServiceConstants.Mappings.FARM_ID_PARAMETER,Response.Status.BAD_REQUEST);
			}
		}

		try{
			return mappings.getFarm(desc);
		}catch(BeanNotFound e) {
			log.debug("Exception while getting Farm",e);
			throw new WebApplicationException("Unable to find Farm with condition "+desc,Response.Status.NOT_FOUND);
		}catch(SQLException e) {
			log.debug("Exception while getting Farm",e);
			throw new WebApplicationException("Unexpected Exception occurred while dealing with database.", e,Response.Status.INTERNAL_SERVER_ERROR);
		} catch (InvalidRequestException e) {
			log.debug("Exception while getting Farm",e);
			throw new WebApplicationException("Unable to search for Farm. ",e,Response.Status.BAD_REQUEST);
		} catch (InternalException e) {
			log.warn("Unexpected Exception while getting Farm",e);
			throw new WebApplicationException("Unexpected Exception.", e,Response.Status.INTERNAL_SERVER_ERROR); 
		}catch(NumberFormatException t) {
			throw new WebApplicationException("Invalid FarmID format "+desc,Response.Status.BAD_REQUEST);
		}catch(Throwable t) {
			log.warn("Unexpected Exception while getting Farm",t);
			throw new WebApplicationException("Unexpected Exception.", t,Response.Status.INTERNAL_SERVER_ERROR);
		}
	}
	

}
