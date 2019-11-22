package org.gcube.application.framework.core;

import java.rmi.RemoteException;
import java.util.HashMap;
import java.util.List;
import java.util.Vector;

import org.gcube.application.framework.core.genericresources.model.ISGenericResource;
import org.gcube.application.framework.core.util.Pair;


/**
 * @author Valia Tsagkalidou (NKUA)
 *
 */
public interface GenericResourceInfoI {
	
	/**
	 * @param name the name of the generic resource
	 * @return a list containing the generic resources that have as name the given
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public   List<ISGenericResource> getGenericResourceByName(String name) throws RemoteException ;
	
	/**
	 * @param id the id of the generic resource
	 * @return a list containing the corresponding generic resources
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public   List<ISGenericResource> getGenericResourceByID(String id) throws RemoteException;
	
	/**
	 * @return a list containing the  generic resources that describe which collections are part of the active VRE as well as their hierarchical structure (the name of this generic resource is "ScenarioCollectionInfo")  
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public  List<ISGenericResource> getGenericResourceForScenario() throws RemoteException ;	

	/**
	 * Updates a generic resource based on it's ID
	 * @param genericResource the generic resource to be updated
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public   void updateGenericResourceByID(ISGenericResource genericResource) throws RemoteException;
	
	/**
	 * Creates a new generic resource
	 * @param genericResource the new generic resource
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public   String createGenericResource(ISGenericResource genericResource) throws RemoteException;
	
	/**
	 * Reomoves an existing generic resource
	 * @param genericResource the generic resource to be removed
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public void removeGenericResource(ISGenericResource genericResource) throws RemoteException;
	
	/**
	 * @return a list containing pairs of (name, id) of the available generic resources 
	 * @throws RemoteException when an error has occurred while communicating with  IS
	 */
	public List<Pair> getAvailableGenericResourceNames() throws RemoteException;
	
	/**
	 * 
	 * @param xsltType Presentation or Metadata. The type of the xslt
	 * @return A vector which contains all the generic resources, xslts of this type. The xslts are sorted by the schema.
	 */
	public HashMap<String,Vector<String[]>> getAllXslts(String xsltType);
}
