package org.gcube.vomanagement.occi;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Collection;

import org.gcube.resources.federation.fhnmanager.api.type.ResourceTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Network;
import org.gcube.vomanagement.occi.datamodel.cloud.OSTemplate;
import org.gcube.vomanagement.occi.datamodel.cloud.Storage;
import org.gcube.vomanagement.occi.datamodel.cloud.VM;

import cz.cesnet.cloud.occi.api.exception.CommunicationException;
import cz.cesnet.cloud.occi.api.exception.EntityBuildingException;

public interface FHNConnector {

  public void setTrustStore(String path);

  /**
   * Connect to the remote infrastructure.
   * 
   * @throws CommunicationException
   */
  public void connect() throws CommunicationException;

  /**
   * Return a list of URIs of the available OS templates.
   * 
   * @return a collection of URIs. of available OS templates.
   * @throws CommunicationException
   */
  public Collection<URI> listOSTemplatesURIs() throws CommunicationException;

  /**
   * Return the list of available OS templates.
   * 
   * @return available OS templates.
   * @throws CommunicationException
   */
  public Collection<OSTemplate> listOSTemplates() throws CommunicationException;

  /**
   * Return the VM template identified by the given URI.
   * 
   * @param uri
   *          the URI of the OS template.
   * @return the OS template with the given URI.
   */
  public OSTemplate getOSTemplate(URI uri);

  /**
   * Return the list of URIs of available resource templates.
   * 
   * @return the list of URIs of available resource templates.
   * @throws CommunicationException
   */
  public Collection<URI> listResourceTemplatesURIs()
      throws CommunicationException;

  /**
   * Return the list of available Resource templates.
   * 
   * @return the list of available resource templates.
   * @throws CommunicationException
   */
  public Collection<ResourceTemplate> listResourceTemplates()
      throws CommunicationException;

  /**
   * 
   * @param uri
   *          the URI of the resource templates.
   * @return the resource template with the given URI.
   */
  public ResourceTemplate getResourceTemplate(URI uri);

  /**
   * Return the list of URIs of the available VMs (in any state).
   * 
   * @throws CommunicationException
   */
  public Collection<URI> listVMURIs() throws CommunicationException;

  /**
   * Return the list of available VMs (in any state).
   * 
   * @throws CommunicationException
   */
  public Collection<VM> listVM() throws CommunicationException;

  /**
   * Return the VM identified by the given URI.
   * 
   * @param vmUri
   *          the uri of the VM
   */
  public VM getVM(URI vmUri);

  /**
   * Create a VM
   * 
   * @param vmName
   *          the name of the VM
   * @param ot
   *          the OS template (the image to start from)
   * @param rt
   *          the Resource template (i.e. cores, memory, ...)
   * @param script
   *          the contextualization script to run at startup
   * @return the uri of the newly created VM
   * @throws CommunicationException
   * @throws EntityBuildingException
   */
  public URI createVM(String vmName, OSTemplate osTemplate,
      ResourceTemplate resourceTemplate, String script)
      throws CommunicationException;

  /**
   * Create a VM.
   */
  public URI createVM(String vmName, OSTemplate osTemplate,
      ResourceTemplate resourceTemplate, File script)
      throws CommunicationException;

  /**
   * Create a VM.
   */
  public URI createVM(String vmName, OSTemplate osTemplate,
      ResourceTemplate resourceTemplate, URL script)
      throws CommunicationException;

  /**
   * Start the VM with the given URI.
   */
  public void startVM(URI vmUri) throws CommunicationException;

  /**
   * Start the given VM.
   */
  public void startVM(VM vm) throws CommunicationException;

  /**
   * Stop the VM with the given URI.
   */
  public void stopVM(URI vmUri) throws CommunicationException;

  /**
   * Start the given VM.
   */
  public void stopVM(VM vm) throws CommunicationException;

  /**
   * Destroy the VM with the given URI.
   */
  public void destroyVM(URI vmUri) throws CommunicationException;

  /**
   * Destroy a VM.
   * 
   * @param vm
   *          the VM to destroy.
   * @throws CommunicationException
   */
  public void destroyVM(VM vm) throws CommunicationException;

  /**
   * Return the list of URIs of available storage items.
   * 
   * @return a list of URIs.
   */
  public Collection<URI> listStorageURIs() throws CommunicationException;

  /**
   * Return the list of available storage items.
   * 
   * @return a list of Storage elements.
   */
  public Collection<Storage> listStorages() throws CommunicationException;

  /**
   * Return the storage identified by the given URI.
   * 
   * @param uri
   *          the uri.
   * @return the storage.
   */
  public Storage getStorage(URI uri) throws CommunicationException;

  /**
   * Return the list of URIs of available networks.
   * 
   * @return the list of URIs of available networks.
   * @throws CommunicationException
   */
  public Collection<URI> listNetworkURIs() throws CommunicationException;

  /**
   * Return the list of available networks.
   * 
   * @return the list of available networks.
   * @throws CommunicationException
   */
  public Collection<Network> listNetworks() throws CommunicationException;

  /**
   * 
   * @param uri
   *          the URI of the network.
   * @return the network for the given URI.
   * @throws CommunicationException
   */
  public Network getNetwork(URI uri) throws CommunicationException;

public void associatePublicIp(URI uri) throws CommunicationException, UnknownHostException, URISyntaxException;

//void associatePublicIp(URI uri) throws CommunicationException, UnknownHostException, URISyntaxException;

}