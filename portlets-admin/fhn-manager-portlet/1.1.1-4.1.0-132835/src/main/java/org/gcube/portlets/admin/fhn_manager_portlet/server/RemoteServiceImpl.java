package org.gcube.portlets.admin.fhn_manager_portlet.server;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.rmi.RemoteException;
import java.util.Collection;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.ObjectType;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.DescribedResource;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.RemoteNode;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.ServiceProfile;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMProvider;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMRequirement;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.VMTemplate;
import org.gcube.portlets.admin.fhn_manager_portlet.shared.model.exceptions.ServiceException;
import org.gcube.resources.federation.fhnmanager.api.type.FHNResource;
import org.gcube.resources.federation.fhnmanager.cl.FHNManagerProxy;
import org.gcube.resources.federation.fhnmanager.cl.fwsimpl.FHNManagerClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class RemoteServiceImpl implements VMManagerServiceInterface {

	private static final Logger logger = LoggerFactory.getLogger(RemoteServiceImpl.class);

	static JAXBContext ctx =null;

	private static String toUseEndpoint=null;
	private static Marshaller marshaller=null;
	
	private static Transformer transformer;
	
	private static final String xslFile="xmlverbatim.xslt";
	
	static{
		try{
			TransformerFactory factory = TransformerFactory.newInstance();
	        Source xslt = new StreamSource(RemoteServiceImpl.class.getResourceAsStream(xslFile));
	        transformer = factory.newTransformer(xslt);

		}catch(Throwable t){
			logger.error("Unable to create transformer",t);
		}
		
		
		
		
		try{
		ctx = JAXBContext.newInstance(
				FHNResource.class,
				org.gcube.resources.federation.fhnmanager.api.type.Node.class,
				org.gcube.resources.federation.fhnmanager.api.type.NodeTemplate.class,
				org.gcube.resources.federation.fhnmanager.api.type.NodeWorkload.class,
				org.gcube.resources.federation.fhnmanager.api.type.ResourceReference.class,
				org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate.class,
				org.gcube.resources.federation.fhnmanager.api.type.ServiceProfile.class,
				org.gcube.resources.federation.fhnmanager.api.type.Software.class,
				org.gcube.resources.federation.fhnmanager.api.type.VMProvider.class,
				org.gcube.resources.federation.fhnmanager.api.type.VMProviderCredentials.class);
		
		marshaller= ctx.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
		
		}catch(Throwable t){
			logger.error("Unable to create JAXB Context.",t);
		}
	}
	

	public RemoteServiceImpl() {
		// TODO Auto-generated constructor stub
	}

	public RemoteServiceImpl(String endpoint){
		toUseEndpoint=endpoint;
	}

	private static FHNManagerClient getClient() throws IllegalArgumentException, MalformedURLException{
		logger.debug("Instantiating cl, scope provider instance value : "+ScopeProvider.instance.get());
		if(toUseEndpoint==null)
			return FHNManagerProxy.getService().build();
		else {
			logger.debug("Using specified url : "+toUseEndpoint);
			return FHNManagerProxy.getService(new URL(toUseEndpoint)).build();
		}
	}


	@Override
	public List<ServiceProfile> getServiceProfiles() throws RemoteException,
	ServiceException {		
		try {
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request service profiles. Scope provider instance value : "+ScopeProvider.instance.get());
			return ModelTranslation.toServiceProfiles(client.allServiceProfiles());
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}		

	}

	@Override
	public List<VMTemplate> getVMTemplates(String serviceProfileId,
			String vmProviderId) throws RemoteException, ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request vm templates. Scope provider instance value : "+ScopeProvider.instance.get());
			return ModelTranslation.toVMTemplates(client.findResourceTemplate(vmProviderId));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public List<VMTemplate> getVMTemplatesByRequirement(
			VMRequirement requirements) throws RemoteException,
			ServiceException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<VMProvider> getVMProviders(String serviceProfileId,
			String vmTemplateId) throws RemoteException, ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request vm providers. Scope provider instance value : "+ScopeProvider.instance.get());
			return ModelTranslation.toVMProviders(client.findVMProviders(serviceProfileId));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public List<RemoteNode> getNodes(String serviceProfileId,
			String vmProviderId) throws RemoteException, ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request nodes. Scope provider instance value : "+ScopeProvider.instance.get());
			return ModelTranslation.toRemoteNodes(client.findNodes(serviceProfileId, vmProviderId));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public RemoteNode createNode(String serviceProfileId, String vmTemplateId,
			String vmProviderId) throws RemoteException, ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request create node. Scope provider instance value : "+ScopeProvider.instance.get());
			return ModelTranslation.toClient(client.createNode(vmProviderId,serviceProfileId,vmTemplateId));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public void startNode(String remoteNodeId) throws RemoteException,
	ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing start node. Scope provider instance value : "+ScopeProvider.instance.get());
			client.startNode(remoteNodeId);
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public void stopNode(String remoteNodeId) throws RemoteException,
	ServiceException {
		try{
			FHNManagerClient client=getClient();
			logger.debug("Got client, performing request stop node. Scope provider instance value : "+ScopeProvider.instance.get());
			client.stopNode(remoteNodeId);
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public void destroyNode(String remoteNodeId) throws RemoteException,
	ServiceException {
		try{
			getClient().deleteNode(remoteNodeId);
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public DescribedResource describeResource(ObjectType type, String id)
			throws RemoteException, ServiceException {
		try{
			logger.debug("Describing "+type+" ID : "+id);
			Object toDescribe=null;			
			switch(type){
			case REMOTE_NODE : {toDescribe=getClient().getNodeById(id);
			break;
			}
			case SERVICE_PROFILE :{
				toDescribe=getById(getClient().allServiceProfiles(),id,type);				 
				break;
			}
			case VM_PROVIDER : {
				toDescribe=getClient().getVMProviderbyId(id);
				((org.gcube.resources.federation.fhnmanager.api.type.VMProvider)toDescribe).setCredentials(null);
				break;
			}
			case VM_TEMPLATES : {
				toDescribe=getById(getClient().findResourceTemplate(null),id,type);
				break;
			}
			}
			logger.debug("goind to marshall / translate : "+toDescribe);
			return new DescribedResource(ModelTranslation.toClient(toDescribe),transform(marshall(toDescribe)));			
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}
	
	
	private <T extends FHNResource> T getById(Collection<T> toLookInto,String id, ObjectType type)throws ServiceException{
		for(T prof:toLookInto)
			if(prof.getId().equals(id))	return prof;
		throw new ServiceException(type.getLabel()+" [ID : "+id+"] not found."); 
	}
	
	private static String marshall(Object toMarshall) throws JAXBException{
		StringWriter writer=new StringWriter();
		marshaller.marshal(toMarshall,writer);
		writer.flush();
		return writer.toString();
	}

	private static String transform(String xml) throws IOException, URISyntaxException, TransformerException {
		StringWriter writer=new StringWriter();
        Source text = new StreamSource(new StringReader(xml));
        transformer.transform(text, new StreamResult(writer));
        writer.flush();
		return writer.toString();
    }

	@Override
	public RemoteNode getNodeById(String id) throws RemoteException,
			ServiceException {
		try{
			return ModelTranslation.toClient(getClient().getNodeById(id));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}

	@Override
	public VMProvider getProviderById(String id) throws RemoteException,
			ServiceException {
		try{
			return ModelTranslation.toClient(getClient().getVMProviderbyId(id));
		} catch (Exception e) {
			logger.error("Unexpected error from server ",e);
			throw new ServiceException("Unexpected error from server : "+e.getMessage());
		}
	}
	
}
