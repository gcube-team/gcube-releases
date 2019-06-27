/**
 *
 */

package org.gcube.datatransfer.resolver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import javax.ws.rs.Path;

import org.glassfish.jersey.server.model.Resource;
import org.glassfish.jersey.server.model.ResourceMethod;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;


/**
 * The Class UriResolverServices.
 *
 * @author Francesco Mangiacrapa at ISTI-CNR (francesco.mangiacrapa@isti.cnr.it)
 * Feb 19, 2019
 */
public class UriResolverServices {


	private static Logger log = LoggerFactory.getLogger(UriResolverServices.class);

	private ObjectNode rootResources = null;
	private ArrayNode arrayResources = null;
	private List<String> listResourcePath = new ArrayList<String>();

	private static UriResolverServices INSTANCE;

	/**
	 * Instantiates a new uri resolver services.
	 */
	private UriResolverServices() {

	}

	/**
	 * Gets the single instance of UriResolverServices.
	 *
	 * @return single instance of UriResolverServices
	 */
	public static UriResolverServices getInstance() {

		if (INSTANCE == null) {
			// synchronized block to remove overhead
			synchronized (UriResolverServices.class) {
				if (INSTANCE == null) {
					// if instance is null, initialize
					INSTANCE = new UriResolverServices();
				}
			}
		}
		return INSTANCE;
	}

	/**
	 * Read resources.
	 *
	 * @param applicationClasses the application classes
	 * @return the list
	 */
	private List<String> readResources(Set<Class<?>> applicationClasses){
		log.info("Read Resources called");
		String basePath = "";
		rootResources = JsonNodeFactory.instance.objectNode();
		arrayResources = JsonNodeFactory.instance.arrayNode();
		rootResources.set("resources", arrayResources);
		log.info("Checking basePath: {}",basePath);
		for (Class<?> aClass : applicationClasses) {
			if (isAnnotatedResourceClass(aClass)) {

		        Resource resource = Resource.builder(aClass).build();
				//String uriPrefix = resource.getPath();
				log.info("The resource: {} isAnnotatedResource",resource.getNames());
		        process(basePath, resource);
			}
		}

		if(log.isDebugEnabled()){
			for (String path : listResourcePath) {
				log.debug("Found path: {}", path);
			}
		}

		return listResourcePath;
	}


    /**
     * Process.
     *
     * @param uriPrefix the uri prefix
     * @param resource the resource
     * @return the list
     */
    private void process(String uriPrefix, Resource resource) {
        String pathPrefix = uriPrefix;
        List<Resource> resources = new ArrayList<>();
        resources.addAll(resource.getChildResources());
        if (resource.getPath() != null) {
            pathPrefix+= resource.getPath();
        }
        for (ResourceMethod method : resource.getAllMethods()) {
            if (method.getType().equals(ResourceMethod.JaxrsType.SUB_RESOURCE_LOCATOR)) {
                resources.add(Resource.from(resource.getResourceLocator().getInvocable().getDefinitionMethod().getReturnType()));
            }
            else {
                addTo(uriPrefix, method, pathPrefix);
                log.info("Adding path: {} to ListPrefix",pathPrefix);
                listResourcePath.add(pathPrefix);
            }
        }
        for (Resource childResource : resources) {
        	log.debug("SUB RESOURCES adding: {}  with path: {}"+childResource.getName(), childResource.getPath());
            process(pathPrefix, childResource);
        }
    }


    /**
     * Adds the to.
     *
     * @param uriPrefix the uri prefix
     * @param srm the srm
     * @param pathPrefix the path prefix
     */
    private void addTo(String uriPrefix, ResourceMethod srm, String pathPrefix){

    	ObjectNode resourceNode = (ObjectNode) arrayResources.get(uriPrefix);
    	log.debug("The Resource Node with uriPrefix: {} is null: {}", uriPrefix, resourceNode==null);

    	if (resourceNode == null){
    		//THE RESOURCE NODE DOES NOT EXIST CREATING IT...
    		ObjectNode theNode = JsonNodeFactory.instance.objectNode();
            ObjectNode inner = JsonNodeFactory.instance.objectNode();
            inner.put("path", pathPrefix);
            inner.set("verbs", JsonNodeFactory.instance.arrayNode());
            theNode.set(uriPrefix, inner);
            resourceNode = inner;
            //ADDING THE RESOURCE NODE CREATED TO THE LIST OF RESOURCES
            arrayResources.add(theNode);
        }

        //THE RESOURCE ALREADY ADDED SO ADDING ONLY VERB TO IT
        ((ArrayNode) resourceNode.get("verbs")).add(srm.getHttpMethod());
    }

	/**
	 * Checks if is annotated resource class.
	 *
	 * @param rc the rc
	 * @return true, if is annotated resource class
	 */
	@SuppressWarnings({
		"rawtypes", "unchecked"
	})
	private static boolean isAnnotatedResourceClass(Class rc) {

		if (rc.isAnnotationPresent(Path.class)) {
			return true;
		}
		for (Class i : rc.getInterfaces()) {
			if (i.isAnnotationPresent(Path.class)) {
				return true;
			}
		}
		return false;
	}



	/**
	 * Gets the list of resource path.
	 *
	 * @param applicationClasses the application classes
	 * @return the list of resource path
	 */
	public List<String> getListOfResourcePath(Set<Class<?>> applicationClasses) {
		log.trace("The Application Classes are {}", applicationClasses);

		if(listResourcePath.isEmpty()){
			log.info("Reference to the List of Resources/Services is empty, creating it...");
			readResources(applicationClasses);
			log.info("Hard-Coding the resource/s: "+ConstantsResolver.resourcesHardCoded);
			listResourcePath.addAll(Arrays.asList(ConstantsResolver.resourcesHardCoded));
		}

		return listResourcePath;
	}


	/**
	 * Gets the list of resource node.
	 *
	 * @param applicationClasses the application classes
	 * @return the list of resource node
	 */
	public ObjectNode getListOfResourceNode(Set<Class<?>> applicationClasses) {

		if(rootResources==null){
			readResources(applicationClasses);
		}

		return rootResources;
	}
}
