package org.gcube.datapublishing.sdmx.is;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.resources.gcore.GenericResource;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datapublishing.sdmx.model.TableAssociationResource;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.informationsystem.publisher.RegistryPublisherFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ISDataWriter 
{
	
	private Logger logger;
	private TableAssociationResource resource;
	private final boolean update;
	
	public ISDataWriter(TableAssociationResource resource) {
		this (resource,true);
	}
	
	private ISDataWriter(TableAssociationResource resource, boolean update) {
		this.logger = LoggerFactory.getLogger(this.getClass());
		this.resource = resource;
		this.update = update;
	}
	

	
	public ISDataWriter ()
	{
		this (new TableAssociationResource(),false);
	}
	

	public void addAssociation (String flowID,String tabularResourceID, String tableID)
	{
		this.resource.addAssociation(flowID, tabularResourceID,tableID);
	}
	
	public void removeAssociation (String flowID)
	{
		this.resource.removeAssociation(flowID);
	}
	
	public boolean commit ()
	{
		this.logger.debug("Commit new value on information system");
		RegistryPublisher rp = RegistryPublisherFactory.create();
		GenericResource response = null;
		
		if (this.update)
		{
			this.logger.debug("Updating IS...");
			response = rp.update(this.resource.getGenericResurce());
		}
		else
		{
			this.logger.debug("Creating new resource");
			response = rp.create(this.resource.getGenericResurce());
		}
		
		this.logger.debug("Response "+response);
		return response != null;
	}
	


	
	public static void main(String[] args) {
		
		ScopeProvider.instance.set("/gcube/devNext/NextNext");

		SecurityTokenProvider.instance.set("adb146d7-1b6d-43ac-9c9a-d3c7187516c8-98187548");
		
		ISDataWriter dataWriter = new ISDataWriter();
		dataWriter.addAssociation("flow3", "tabularresource3","table3");
		System.out.println(dataWriter.commit());

		
	}
	
}
