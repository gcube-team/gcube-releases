package org.gcube.vomanagement.occi;

import cz.cesnet.cloud.occi.Model;
import cz.cesnet.cloud.occi.api.Authentication;
import cz.cesnet.cloud.occi.api.Client;
import cz.cesnet.cloud.occi.api.EntityBuilder;
import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.exception.EntityBuildingException;
import cz.cesnet.cloud.occi.api.http.HTTPClient;
import cz.cesnet.cloud.occi.api.http.auth.HTTPAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.KeystoneAuthentication;
import cz.cesnet.cloud.occi.api.http.auth.VOMSAuthentication;
import cz.cesnet.cloud.occi.core.ActionInstance;
import cz.cesnet.cloud.occi.core.Entity;
import cz.cesnet.cloud.occi.core.Link;
import cz.cesnet.cloud.occi.core.Mixin;
import cz.cesnet.cloud.occi.core.Resource;
import cz.cesnet.cloud.occi.exception.AmbiguousIdentifierException;
import cz.cesnet.cloud.occi.exception.InvalidAttributeValueException;
import cz.cesnet.cloud.occi.infrastructure.Compute;
import cz.cesnet.cloud.occi.infrastructure.IPNetworkInterface;
import cz.cesnet.cloud.occi.infrastructure.NetworkInterface;

import org.bouncycastle.ocsp.OCSPException;
import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.api.type.VMProviderCredentials;
import org.gcube.vomanagement.occi.datamodel.cloud.Network;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Storage;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;
import org.gcube.vomanagement.occi.datamodel.cloud.VMNetwork;
import org.gcube.vomanagement.occi.datamodel.cloud.VMStorage;
import org.gcube.vomanagement.occi.datamodel.security.Credentials;
import org.gcube.vomanagement.occi.datamodel.security.X509Credentials;
import org.gcube.vomanagement.occi.exceptions.UnsupportedCredentialsTypeException;
import org.gcube.vomanagement.occi.utils.ParseUtil;
import org.gcube.vomanagement.occi.utils.ScriptUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import javax.xml.bind.DatatypeConverter;

public class OcciConnector implements FHNConnector {
	private static final Logger LOGGER = LoggerFactory.getLogger(OcciConnector.class);


  /**
   * The path to the certificate to use when accessing the remote
   * infrastructure.
   */
  private String proxyCertificatePath;

  /**
   * The path to the directory containing CA certificates.
   */
  private String trustStorePath;

  /**
   * The infrastructure provider this connector refers to.
   */
  private VMProvider provider;

  private Client client;
  
  protected EntityBuilder eb;

  /**
   * Create an OCCI connector for the given VM provider.
   * 
   * @param provider
   *          the provider this connector has been created for.
   * @throws UnsupportedCredentialsTypeException
   *           in case this connector does not support the credentials
   *           associated with the provider.
   */
  public OcciConnector(VMProvider provider)
      throws UnsupportedCredentialsTypeException {
    // set the endpoint
    this.provider = provider;
    // set credentials
    VMProviderCredentials creds = provider.getCredentials();
    if (creds != null) {
      this.setCredentials(creds.getType(), creds.getEncodedCredentails());
    }
  }

  /**
   * Return the provider this connector has been created for.
   * 
   * @return the VMProvider
   */
  public VMProvider getProvider() {
    return provider;
  }

  /** 
   * {@inheritDoc}.
   */
  @Override
  public void connect() throws CommunicationException {
    if (this.client == null) {
      System.out.println("Connecting...");
      this.client = this.createOcciClient(this.proxyCertificatePath,
          this.trustStorePath);
      // the following takes a while to execute
      //this.client.connect();
      System.out
      .println("Connected: " + this.getProvider().getEndpoint());
      Model model = this.client.getModel();
      this.eb = new EntityBuilder(model);
    
    }
  }

  private Client createOcciClient(String proxyCertificatePath,
      String trustStorePath) throws CommunicationException {
    Client client = null;
    if (proxyCertificatePath != null) {
      final HTTPAuthentication authentication = new VOMSAuthentication(
          proxyCertificatePath);
      authentication.setCAPath(trustStorePath);
      // the following takes a while to execute
      client = new HTTPClient(URI.create(this.getEndpoint()),
          authentication);
    } else {
      client = new HTTPClient(URI.create(this.getEndpoint()));
    }
    return client;
  }
  

  
  /**
   * Return the endpoint of the remote infrastructure.
   * 
   * @return the endpoint of the remote infrastructure
   */
  public String getEndpoint() {
    return this.provider.getEndpoint();
  }

  /*
   * ------------------------------------------------------------------------
   * T E M P L A T E S
   * ------------------------------------------------------------------------
   */

  /**
   * Return the mixin corresponding to the given OS template.
   * 
   * @param template
   * @return the mixin corresponding to the given template.
   */
  private Mixin getMixin(OSTemplate template) {
    return this.client.getModel().findMixin(URI.create(template.getId()));
  }

  /**
   * Return the mixin corresponding to the given Resource template.
   * 
   * @param template
   * @return the mixin corresponding to the given template.
   */
  private Mixin getMixin(ResourceTemplate template) {
    return this.client.getModel().findMixin(URI.create(template.getId()));
  }

  @Override
  public Collection<OSTemplate> listOSTemplates()
      throws CommunicationException {
    Collection<OSTemplate> out = new ArrayList<>();
    try {
      List<Mixin> mixins = this.client.getModel().findRelatedMixins(
          OcciConstants.OS_TPL);
      for (Mixin m : mixins) {
        OSTemplate template = new OSTemplate();
        template.setId(m.getScheme() + m.getTerm());
        template.setName(m.getTitle());
        template = this.enrichWithCached(template);
        out.add(template);
      }
    } catch (AmbiguousIdentifierException e) {
      throw new CommunicationException(e);
    }
    return out;
  }

  @Override
  public Collection<URI> listOSTemplatesURIs() throws CommunicationException {
    Collection<URI> out = new ArrayList<>();
    try {
      List<Mixin> mixins = this.client.getModel().findRelatedMixins(
          OcciConstants.OS_TPL);
      for (Mixin m : mixins) {
        out.add(m.getLocation());
      }
    } catch (AmbiguousIdentifierException e) {
      throw new CommunicationException(e);
    }
    return out;
  }

  @Override
  public Collection<URI> listResourceTemplatesURIs()
      throws CommunicationException {
    Collection<URI> out = new ArrayList<>();
    try {
      List<Mixin> mixins = this.client.getModel().findRelatedMixins(
          OcciConstants.RESOURCE_TPL);
      for (Mixin m : mixins) {
        out.add(m.getLocation());
      }
    } catch (AmbiguousIdentifierException e) {
      throw new CommunicationException(e);
    }
    return out;
  }

  /**
   * Enriches the given OS template with data stored in the local cache.
   * 
   * @param template the template to enrich
   * @return the enriched template
   */
  private OSTemplate enrichWithCached(OSTemplate template) {
    OSTemplate cached = TemplatesCache.getInstance().getOSTemplate(
        this.getProvider().getEndpoint(), template.getId());
    if (cached != null) {
      template.setDescription(cached.getDescription());
      template.setDiskSize(cached.getDiskSize());
      template.setOs(cached.getOs());
      template.setOsVersion(cached.getOsVersion());
      template.setVersion(cached.getVersion());
    }
    return template;
  }
  
  @Override
  public OSTemplate getOSTemplate(URI uri) {
    Mixin mixin = this.client.getModel().findMixin(uri);
    OSTemplate template = null;
    if (mixin != null) {
      template = new OSTemplate();
      template.setId(mixin.getScheme() + mixin.getTerm());
      template.setName(mixin.getTitle());
      template = this.enrichWithCached(template);
    }
    return template;
  }

  /**
   * Enriches the given Resource template with data stored in the local cache.
   * 
   * @param template the template to enrich
   * @return the enriched template
   */
  private ResourceTemplate enrichWithCached(ResourceTemplate template) {
    ResourceTemplate cached = TemplatesCache.getInstance()
    		.getResourceTemplate(this.getProvider().getEndpoint(),
    				template.getId());
    if (cached != null) {
      template.setCores(cached.getCores());
      template.setMemory(cached.getMemory());
    }
    return template;
  }
  
  @Override
  public Collection<ResourceTemplate> listResourceTemplates()
      throws CommunicationException {
    Collection<ResourceTemplate> out = new ArrayList<>();
    try {
      List<Mixin> mixins = this.client.getModel().findRelatedMixins(
          OcciConstants.RESOURCE_TPL);
      for (Mixin m : mixins) {
        ResourceTemplate template = new ResourceTemplate();
        template.setId(m.getScheme() + m.getTerm());
        template.setName(m.getTitle());
        template = this.enrichWithCached(template);
        out.add(template);

      }
    } catch (AmbiguousIdentifierException e) {
      throw new CommunicationException(e);
    }
    return out;
  }

  @Override
  public ResourceTemplate getResourceTemplate(URI uri) {
    Mixin mixin = this.client.getModel().findMixin(uri);
    ResourceTemplate template = null;
    if (mixin != null) {
      template = new ResourceTemplate();
      template.setId(mixin.getScheme() + mixin.getTerm());
      template.setName(mixin.getTitle());
      template = this.enrichWithCached(template);
    }
    return template;
  }



  /*
   * ------------------------------------------------------------------------
   * C O M P U T E
   * ------------------------------------------------------------------------
   */

  @Override
  public Collection<URI> listVMURIs() throws CommunicationException {
    Collection<URI> out = new ArrayList<>();
    for (URI uri : this.client.list(Compute.TERM_DEFAULT)) {
      if (!uri.toString().isEmpty()) {
        out.add(uri);
      }
    }
    return out;
  }

  @Override
  public Collection<VM> listVM() throws CommunicationException {
    Collection<VM> out = new ArrayList<>();
    for (URI uri : this.listVMURIs()) {
      if (uri.toString().trim().isEmpty()) {
        continue;
      }
      VM vm = this.getVM(uri);
      if (vm != null) {
        out.add(vm);
      }
    }
    return out;
  }

  /**
   * Create a VMNetwork starting from the corresponding occi link.
   * 
   * @param link
   *          the occi link
   * @param uri
   *          the uri of the corresponding VM
   * @return a VMNetwork object representing the link between the VM and the
   *         Network
   */
  private VMNetwork linkToVMNetwork(Link link, URI uri) {
    VMNetwork vmNetwork = new VMNetwork();
    vmNetwork.setId(link.getValue(OcciConstants.CORE_ID));
    vmNetwork.setName(link.getValue(OcciConstants.CORE_TITLE));
    vmNetwork.setAddress(link.getValue(OcciConstants.NETWORKINTERFACE_ADDRESS));
    vmNetwork.setInterface(link.getValue(OcciConstants.NETWORKINTERFACE_INTERFACE));
    vmNetwork.setMac(link.getValue(OcciConstants.NETWORKINTERFACE_MAC));
    vmNetwork.setStatus(link.getValue(OcciConstants.NETWORKINTERFACE_STATE));
    return vmNetwork;
  }

  /**
   * Create a VMStorage starting from the corresponding occi link.
   * 
   * @param link
   *          the occi link
   * @param uri
   *          the uri of the corresponding VM
   * @return a VMStorage object representing the link between the VM and the
   *         Storage.
   */
  private VMStorage linkToVMStorage(Link link, URI uri) {
    VMStorage vmStorage = new VMStorage();
    vmStorage.setId(link.getValue(OcciConstants.CORE_ID));
    vmStorage.setName(link.getValue(OcciConstants.CORE_TITLE));
    vmStorage.setDeviceId(link.getValue(OcciConstants.STORAGELINK_DEVICEID));
    vmStorage.setStatus(link.getValue(OcciConstants.STORAGELINK_STATE));
    return vmStorage;
  }

  /**
   * Create a VM starting from the corresponding occi entity.
   * 
   * @param entity
   *          the OCCI entity for the VM
   * @param uri
   *          the uri of the VM
   * @return the VM
   */
  private VM entityToVM(Entity entity, URI uri) {
    VM vm = new VM();
    vm.setProvider(this.getEndpoint());
    vm.setId(entity.getValue(OcciConstants.CORE_ID));
    vm.setName(entity.getValue(OcciConstants.CORE_TITLE));
    vm.setStatus(entity.getValue(Compute.STATE_ATTRIBUTE_NAME));
    vm.setHostname(entity.getValue(Compute.HOSTNAME_ATTRIBUTE_NAME));
    vm.setMemory(Double.valueOf(entity.getValue(Compute.MEMORY_ATTRIBUTE_NAME)));
    vm.setCores(Integer.valueOf(entity.getValue(Compute.CORES_ATTRIBUTE_NAME)));;
    vm.setEndpoint(uri);
    Double memory = ParseUtil.extractDoubleAttribute(entity,
        Compute.MEMORY_ATTRIBUTE_NAME);
    if (memory != null) {
      vm.setMemory(Math.round(memory * 1024 * 1024 * 1024));
    }
    Double cores = ParseUtil.extractDoubleAttribute(entity,
        Compute.CORES_ATTRIBUTE_NAME);
    if (cores != null) {
      vm.setCores((int) (1d * cores));
    }
    return vm;
  }

  
  private String getResourceTemplateId(Entity e){
	  try {
		
		  // find the schema for resource template on this provider
		  List<Mixin> mixins = this.client.getModel().findRelatedMixins(OcciConstants.RESOURCE_TPL);
		  URI scheme = mixins.iterator().next().getScheme();
		  
		  for(Mixin em: e.getMixins()){
			  if(em.getScheme().equals(scheme)){
				  return em.getIdentifier();
			  }
		  }
	
	  
	  } catch (AmbiguousIdentifierException e1) {
		e1.printStackTrace();
	  }
    
	return null;
  }
  
  private String getOSTemplateId(Entity e){
	  try {
		
		  // find the schema for resource template on this provider
		  List<Mixin> mixins = this.client.getModel().findRelatedMixins(OcciConstants.OS_TPL);
		  URI scheme = mixins.iterator().next().getScheme();
		  
		  for(Mixin em: e.getMixins()){
			  if(em.getScheme().equals(scheme)){
				  return em.getIdentifier();
			  }
		  }
	
	  
	  } catch (AmbiguousIdentifierException e1) {
		e1.printStackTrace();
	  }
    
	return null;
  }
  
  
  @Override
  public VM getVM(URI uri) {
    VM vm = null;
    try {
      List<Entity> entities = this.client.describe(uri);
      for (Entity e : entities) {
        
    	  vm = this.entityToVM(e, uri);
    	  
    	  
    	  String osTemplateId = this.getOSTemplateId(e);
    	  String resourceTemplateId = this.getResourceTemplateId(e);
    	  
    	  
         // cache for later usage
         ResourceTemplate toCache = new ResourceTemplate();
         toCache.setId(resourceTemplateId);
         
         toCache.setCores(vm.getCores());
         toCache.setMemory(vm.getMemory());
         TemplatesCache.getInstance().cache(this.getEndpoint(), toCache);
         
         // cache for later usage
         OSTemplate ostToCache = new OSTemplate();
         ostToCache.setId(osTemplateId);
         ostToCache.setDiskSize(vm.getDiskSize());
         TemplatesCache.getInstance().cache(this.getEndpoint(), ostToCache);
         
        // links
        if (e instanceof Resource) {
          Resource resource = (Resource) e;
          for (Link l : resource.getLinks()) {
            String term = l.getKind().getTerm();
            if (OcciConstants.STORAGELINK.equalsIgnoreCase(term)) {
              VMStorage link = this.linkToVMStorage(l, uri);
              /*
               * VMStorage link = new VMStorage();
               * link.setId(l.getValue(OcciConstants.CORE_ID));
               * link
               * .setName(l.getValue(OcciConstants.CORE_TITLE));
               * link.setDeviceId(l
               * .getValue(OcciConstants.STORAGELINK_DEVICEID));
               * link.setStatus(l
               * .getValue(OcciConstants.STORAGELINK_STATE));
               */
              URI storageUri = new URI(uri.getScheme(),
                  uri.getUserInfo(), uri.getHost(),
                  uri.getPort(),
                  l.getValue(OcciConstants.CORE_TARGET),
                  uri.getQuery(), uri.getFragment());
              Storage storage = this.getStorage(storageUri);
              link.setStorage(storage);
              vm.addStorage(link);
            } else if (OcciConstants.NETWORKINTERFACE
                .equalsIgnoreCase(term)  || (term.equals("link"))) {
              VMNetwork link = this.linkToVMNetwork(l, uri);
              /*
               * VMNetwork link = new VMNetwork();
               * link.setId(l.getValue(OcciConstants.CORE_ID));
               * link
               * .setName(l.getValue(OcciConstants.CORE_TITLE));
               * link.setAddress(l
               * .getValue(OcciConstants.NETWORKINTERFACE_ADDRESS
               * )); link.setInterface(l
               * .getValue(OcciConstants.NETWORKINTERFACE_INTERFACE
               * )); link.setMac(l
               * .getValue(OcciConstants.NETWORKINTERFACE_MAC));
               * link.setStatus(l
               * .getValue(OcciConstants.NETWORKINTERFACE_STATE));
               */
              
              /*
               * try to get the network assuming CORE_TARGET is the absolute url of the network
               * if it fails, assume CORE_TARGET is just the path
               */
              URI networkUri = null;
              try {
            	  networkUri = new URI(l.getValue(OcciConstants.CORE_TARGET));
              } catch(URISyntaxException ex) {
                networkUri = new URI(uri.getScheme(),
                uri.getUserInfo(), uri.getHost(),
                uri.getPort(),
                l.getValue(OcciConstants.CORE_TARGET),
                uri.getQuery(), uri.getFragment());                
              }
              Network network = this.getNetwork(networkUri);
              link.setNetwork(network);
              vm.addNetwork(link);
            }
          }
        }
        break;
      }
    } catch (CommunicationException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    return vm;
  }

 

  
  public boolean isPublicNetwork(Resource netResource){
	  return netResource.getId().contains("public") ||
			  (netResource.getKind().getTitle() != null && netResource.getKind().getTitle().contains("public")) ||
			  (netResource.getTitle() != null && netResource.getTitle().contains("public")) ||
			  (netResource.getSummary() != null && netResource.getSummary().contains("public"));
  }
 
  

/**
 * @throws URISyntaxException 
 * @throws UnknownHostException ****************************************************************************/

  	public void associatePublicIp(URI uri) throws CommunicationException, UnknownHostException, URISyntaxException {
		
		//1. get the VM
		Resource vmResource = null;
		List<Entity> entities = client.describe(uri);
		vmResource = (Resource) entities.get(0);
		
		
		//2. find link with the private ip
		URI privateNetworkLink = null;
		Set<Link> links = vmResource.getLinks();
		for (Link link : links) {
			//FIXME: workaround to return also networks of bari node (networkinterface term is "Link")
			if (link.getKind().getTerm().equals(NetworkInterface.TERM_DEFAULT) || link.getKind().getTerm().equals("link")) {
				String ipAddress = link.getValue(IPNetworkInterface.ADDRESS_ATTRIBUTE_NAME);
				if (InetAddress.getByName(ipAddress).isSiteLocalAddress()) {
					privateNetworkLink = new URI(link.getKind().getLocation() + link.getId());
					break;
				}
			}
		}
		
		if(privateNetworkLink != null){

		
//			//4. search public ip network
			Resource publicIpNetwork = null;
			List<URI> uris = client.list("network");
			for (URI uri2 : uris) {
				entities = client.describe(uri2);
				for (Entity entity2 : entities) {
					Resource res = (Resource) entity2;
					if(isPublicNetwork(res)){
						publicIpNetwork = res;
						break;
					}					
				}
			}
		
			
			
			//4. search public ip network
//			Resource publicIpNetwork = null;
//			List<URI> uris = client.list("network");
//			for (URI uri2 : uris) {
//				entities = client.describe(uri2);
//				for (Entity entity2 : entities) {
//					Resource resource2 = (Resource) entity2;
//					LOGGER.debug("Found resource with id = "+resource2.getId()+" and title ="+resource2.getTitle()+" and kind title = "+resource2.getKind().getTitle()+" and summary= "+resource2.getSummary());
//					try{
//					//FIXME: workaround to return also publicIP of UPV node (find out whether network provides public IPs (This can be done either from network ID, title or summary. There is no unified way.)
//					if (resource2.getId().contains("public")) { 
//							publicIpNetwork = resource2;
//							break;
//							}
//					else if (resource2.getTitle().contains("public")){
//						publicIpNetwork = resource2;
//						break;
//					}
//					else if (resource2.getKind().getTitle().contains("public")){
//						publicIpNetwork = resource2;
//					break;
//					}
//					else if (resource2.getSummary().contains("public")){
//						publicIpNetwork = resource2;
//						break;
//					}
//					} catch (Exception a) {}
//					
//				}
//			}
			
			//5. create the link with the public ip
			if(publicIpNetwork != null){
				 IPNetworkInterface ipni;
				try {
					ipni = eb.getIPNetworkInterface();
					 ipni.setSource(vmResource);
					 
					 
					 //Not using setTarget() because it adds the target attribute 
					 //like resource.getLocation() + resource.getId()
					 //Since id contains also the location, the result is:
					 //  /network//network/public
					 // instead of:
					 // /network/public
					 //ipni.setTarget(publicIpNetwork);
					 
					 if (publicIpNetwork.getId().contains("public")){
					 ipni.addAttribute(
							 IPNetworkInterface.TARGET_ATTRIBUTE_NAME, 
							 publicIpNetwork.getId());
					 }
					 
					//FIXME: workaround to set public network in case of UPV provider
					//usually /network/public reference is in the publicIpNetwork.getId()
					//since it is not present in UPV, it has been cabled in the code
					 else {
						 ipni.addAttribute(
								 IPNetworkInterface.TARGET_ATTRIBUTE_NAME, 
								 "/network/public");
						 }
					 client.create(ipni);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			
			
			//3. remove private network
			//client.delete(privateNetworkLink);
		}
		
  	}		
			
//		
//		
//		
//		
//		String address = null;
//		
//		
//		
//		List<Entity> entities = client.describe(uri);
//		for (Entity entity : entities) {
//			Resource resource = (Resource) entity;
//			Set<Link> links = resource.getLinks(NetworkInterface.TERM_DEFAULT);
//			for (Link link : links) {
//				// prendo l ip privato
//				address = link.getValue(IPNetworkInterface.ADDRESS_ATTRIBUTE_NAME);
//				LOGGER.debug("Ip for url " + uri + " is " + address);
//				// se è privato
//				if (InetAddress.getByName(address).isSiteLocalAddress()) {
//					LOGGER.debug(address + " is a private Ip");
//					// slego la vecchia rete
//					URI networkInterfaceLocation = new URI(link.getKind().getLocation() + link.getId());
//					LOGGER.debug("found network: " + networkInterfaceLocation);
//					client.delete(networkInterfaceLocation);
//					LOGGER.debug("private network deleted");
//				}
//				// trovo una rete con ip pubblici
//				List<URI> uris = client.list("network");
//				for (URI uri2 : uris) {
//					entities = client.describe(uri2);
//					for (Entity entity2 : entities) {
//						Resource resource2 = (Resource) entity2;
//						if (resource2.getId().contains("public")) {
//							// aggiungo link a rete pubbica
//							LOGGER.debug("found public network: " + resource2.getId());
//							
//							
//							
//							//Resource vm = (Resource) entity;
//							
//							//Resource network = (Resource) entity;
//							// Resource vm = obtainVm(); // virtual machine you
//							// want to add link to, casted from Entity as before
//							// Resource network = obtainNetwork(); // network
//							// resource that provides public IPs, also casted
//							// from Entity
//							//
//							// IPNetworkInterface ipni =
//							// eb.getIPNetworkInterface();
//							// ipni.setSource(vm);
//							// ipni.setTarget(network);
//							// client.create(ipni);
//						}
//					}
//
//				}
//			}
//
//		}

	
		
	/******************************************************************************/
		
			
	
	

  @Override
  public URI createVM(String vmName, OSTemplate ot, ResourceTemplate rt,
      File scriptPath) throws CommunicationException {
    String script = null;
    try {
      script = ScriptUtil.getScriptFromFile(scriptPath);
    } catch (IOException e) {
      e.printStackTrace();
      throw new CommunicationException(e);
    }
    return this.createVM(vmName, ot, rt, script);
  }

  @Override
  public URI createVM(String vmName, OSTemplate ot, ResourceTemplate rt,
      String script) throws CommunicationException {
    Mixin osMixin = this.getMixin(ot);
    Mixin resMixin = this.getMixin(rt);
    return this.createComputeResource(vmName, osMixin, resMixin, script);
  }

  @Override
  public URI createVM(String vmName, OSTemplate ot, ResourceTemplate rt,
      URL scriptURL) throws CommunicationException {
    String script = null;
    try {
      script = ScriptUtil.getScriptFromURL(scriptURL)/*.replace("a", "aaaa")*/;
    } catch (IOException e) {
      e.printStackTrace();
      throw new CommunicationException(e);
    }
    return this.createVM(vmName, ot, rt, script);
  }

  @Override
  public void startVM(VM vm) throws CommunicationException {
    this.startVM(vm.getEndpoint());
  }

  @Override
  public void startVM(URI vmURI) throws CommunicationException {
    try {
      this.startComputeResource(vmURI);
    } catch (EntityBuildingException e) {
      throw new CommunicationException(e.getMessage());
    }
  }

  @Override
  public void stopVM(VM vm) throws CommunicationException {
    this.stopVM(vm.getEndpoint());
  }

  @Override
  public void stopVM(URI vmURI) throws CommunicationException {
    try {
      this.stopComputeResource(vmURI);
    } catch (EntityBuildingException e) {
      throw new CommunicationException(e.getMessage());
    }
  }

  @Override
  public void destroyVM(VM vm) throws CommunicationException {
    this.destroyVM(vm.getEndpoint());
  }

  @Override
  public void destroyVM(URI vmUri) throws CommunicationException {
    this.deleteComputeResource(vmUri);
  }

  /*
   * ------------------------------------------------------------------------
   * N E T W O R K
   * ------------------------------------------------------------------------
   */

  @Override
  public Collection<URI> listNetworkURIs() throws CommunicationException {
    return this.client
        .list(cz.cesnet.cloud.occi.infrastructure.Network.TERM_DEFAULT);
  }

  @Override
  public Collection<Network> listNetworks() throws CommunicationException {
    Collection<Network> out = new ArrayList<>();
    for (URI uri : this.listNetworkURIs()) {
      if (uri.toString().trim().isEmpty()) {
        continue;
      }
      out.add(this.getNetwork(uri));
    }
    return out;
  }

  @Override
  public Network getNetwork(URI uri) {
    Network network = null;
    try {
      List<Entity> entities = this.client.describe(uri);
      for (Entity e : entities) {
        network = new Network();
        network.setDescription(e.getValue(OcciConstants.CORE_SUMMARY));
        network.setId(e.getValue(OcciConstants.CORE_ID));
        network.setName(e.getValue(OcciConstants.CORE_TITLE));
        network.setStatus(e
            .getValue(cz.cesnet.cloud.occi.infrastructure.Network.STATE_ATTRIBUTE_NAME));
        network.setAddress(e.getValue(OcciConstants.NETWORK_ADDRESS));
        network.setAllocation(e
            .getValue(OcciConstants.NETWORK_ALLOCATION));
        network.setGateway(e.getValue(OcciConstants.NETWORK_GATEWAY));
        network.setEndpoint(uri);
        break;
      }
    } catch (CommunicationException e) {
      e.printStackTrace();
    }
    return network;
  }

  /*
   * ------------------------------------------------------------------------
   * S T O R A G E
   * ------------------------------------------------------------------------
   */

  @Override
  public Collection<URI> listStorageURIs() throws CommunicationException {
    return this.client.list(OcciConstants.STORAGE);
  }

  @Override
  public Collection<Storage> listStorages() throws CommunicationException {
    Collection<Storage> out = new ArrayList<>();
    for (URI uri : this.listStorageURIs()) {
      if (uri.toString().trim().isEmpty()) {
        continue;
      }
      out.add(this.getStorage(uri));
    }
    return out;
  }

  @Override
  public Storage getStorage(URI uri) {
    Storage storage = null;
    try {
      List<Entity> entities = this.client.describe(uri);
      for (Entity e : entities) {
        System.out.println(e);
        storage = new Storage();
        storage.setId(e.getValue(OcciConstants.CORE_ID));
        storage.setName(e.getValue(OcciConstants.CORE_TITLE));
        storage.setSummary(e.getValue(OcciConstants.CORE_SUMMARY));
        Double disk = ParseUtil
            .extractDoubleAttribute(
                e,
                cz.cesnet.cloud.occi.infrastructure.Storage.SIZE_ATTRIBUTE_NAME);
        if (disk != null) {
          storage.setSize(Math.round(disk * 1024 * 1024 * 1024));
        }
        storage.setStatus(e
            .getValue(cz.cesnet.cloud.occi.infrastructure.Storage.SIZE_ATTRIBUTE_NAME));
        storage.setEndpoint(uri);

        // cache for later usage
        OSTemplate toCache = new OSTemplate();
        toCache.setId(storage.getEndpoint().toString());
        toCache.setDiskSize(storage.getSize());
        TemplatesCache.getInstance().cache(
            this.getProvider().getEndpoint(), toCache);

        break;
      }
    } catch (CommunicationException e) {
      e.printStackTrace();
    }
    return storage;
  }

  /*
   * ------------------------------------------------------------------------
   * G E N E R I C
   * ------------------------------------------------------------------------
   */

  private URI createComputeResource(String vmName, Mixin osMixin,
      Mixin resourceMixin, String script) throws CommunicationException {
    try {
      System.out.println("Creating compute resource...");
      Model model = this.client.getModel();
      EntityBuilder eb = new EntityBuilder(model);
      Resource compute = eb.getResource(Compute.TERM_DEFAULT);
      System.out.println("Mixin:");
      compute.addMixin(osMixin);
      compute.addMixin(resourceMixin);
      System.out.println(osMixin.toText());
      System.out.println(resourceMixin.toText());
      compute.setTitle(vmName);
      compute.addAttribute(Compute.HOSTNAME_ATTRIBUTE_NAME, vmName);
       System.out.println("script:\n" + script);
      if (script != null) {
        byte[] message = script.getBytes("UTF-8");
        String encoded = DatatypeConverter.printBase64Binary(message);
        compute.addMixin(model.findMixin(URI
            .create("http://schemas.openstack.org/compute/instance#user_data")));
        compute.addAttribute("org.openstack.compute.user_data", encoded);
      }
      URI location = this.client.create(compute);
      System.out.println("Created compute instance at location: '"
          + location + "'.");
      
      
      
      try {
    	Thread.sleep(20000);
		this.associatePublicIp(location);
	} catch (Exception e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	}

      // cache for later usage
      ResourceTemplate toCache = new ResourceTemplate();
      toCache.setId(resourceMixin.getScheme() + resourceMixin.getTerm());
      VM vm = this.getVM(location);
      toCache.setCores(vm.getCores());
      toCache.setMemory(vm.getMemory());
      TemplatesCache.getInstance().cache(this.getEndpoint(), toCache);
      
      // cache for later usage
      OSTemplate ostToCache = new OSTemplate();
      ostToCache.setId(osMixin.getScheme() + osMixin.getTerm());
      ostToCache.setDiskSize(vm.getDiskSize());
      TemplatesCache.getInstance().cache(this.getEndpoint(), ostToCache);

      return location;

    } catch (UnsupportedEncodingException | CommunicationException
        | EntityBuildingException | InvalidAttributeValueException ex) {
      throw new CommunicationException(ex);
    }
  }

  /**
   * Start the resource identified by the given URI.
   * 
   * @param uri the URI of the resource to start.
   * @return whether the resource was started or not.
   * @throws CommunicationException
   * @throws EntityBuildingException
   */
  private boolean startComputeResource(URI uri)
      throws CommunicationException, EntityBuildingException {
    this.connect();
    System.out.println("Starting created compute...");
    ActionInstance actionInstance = this.eb
        .getActionInstance(URI
            .create("http://schemas.ogf.org/occi/infrastructure/compute/action#start"));
    return this.client.trigger(uri, actionInstance);
  }

  /**
   * Stop the resource identified by the given URI.
   * 
   * @param uri the URI of the resource to stop.
   * @return whether the resource was stopped or not.
   * @throws CommunicationException
   * @throws EntityBuildingException
   */
  private boolean stopComputeResource(URI uri) throws CommunicationException,
  EntityBuildingException {
    this.connect();
    System.out.println("Stopping compute...");
    ActionInstance actionInstance = this.eb
        .getActionInstance(URI
            .create("http://schemas.ogf.org/occi/infrastructure/compute/action#stop"));
    return this.client.trigger(uri, actionInstance);
  }

  /**
   * Delete the resource identified by the given URI.
   * 
   * @param uri the URI of the resource to delete
   * @return whether the resource was deleted or not.
   * @throws CommunicationException
   */
  private boolean deleteComputeResource(URI uri)
      throws CommunicationException {
    this.connect();
    System.out.println("Deleting created resource...");
    return this.client.delete(uri);
  }

  /*
   * ------------------------------------------------------------------------
   * S E C U R I T Y
   * ------------------------------------------------------------------------
   */

  @Override
  public void setTrustStore(String path) {
    this.trustStorePath = path;

  }

  private void setCredentials(String type, String encoded)
      throws UnsupportedCredentialsTypeException {
    if ("x509".equals(type)) {
      this.setCredentials(new X509Credentials(encoded));
    }
  }

  private void setCredentials(Credentials credentials)
      throws UnsupportedCredentialsTypeException {
    if (credentials instanceof X509Credentials) {
      this.proxyCertificatePath = ((X509Credentials) credentials)
          .getCertificateFile().getPath();
    } else {
      throw new UnsupportedCredentialsTypeException(
          "unsupported credentials type");
    }
  }
}
  
