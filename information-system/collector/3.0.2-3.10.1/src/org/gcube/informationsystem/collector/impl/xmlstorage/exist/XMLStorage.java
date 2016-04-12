package org.gcube.informationsystem.collector.impl.xmlstorage.exist;

import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.DatabaseManager;
import org.xmldb.api.modules.XMLResource;
import org.xmldb.api.modules.CollectionManagementService;

import org.exist.xmldb.DatabaseInstanceManager;

import org.exist.storage.DBBroker;

import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.collector.impl.xmlstorage.exist.XMLStorage;

import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource;
import org.gcube.informationsystem.collector.impl.resources.GCUBEXMLResource.MalformedXMLResourceException;


import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * A thread safe manager to interact with the XML Storage repository.
 * 
 * RESOURCE the type of the resource to store
 * 
 * @author Manuele Simi (ISTI-CNR)
 * 
 */
public class XMLStorage {

    protected static String URI = "xmldb:exist://";
    
    protected static String USER = "admin";
    
    protected static String PWD = "";

    protected static String driver = "org.exist.xmldb.DatabaseImpl";

    private static GCUBELog logger = new GCUBELog(XMLStorage.class);

    private Database database;

    // private Collection currentCollection;

    protected Collection rootCollection;

    protected Lock lock;
    
    // lock for writing operations
    private Lock writeLock;
    
    // lock for reading operations
    protected Lock readLock;
    
    // flag to warn when the DB is locked
    private boolean locked = false;

    enum STATUS {INITIALISED, CLOSED, SHUTDOWN};
    
    private STATUS status = STATUS.CLOSED;
    
    protected long maxOperationsPerConnection = 1000;
    
    protected long operationsCounter = 0;
    
    
    /**
     * Creates a new manager
     * 
     */
    public XMLStorage() {
	logger.debug("Creating a new XMLStorage");
	lock = new ReentrantLock();
	writeLock = lock;
	readLock = lock;
    }


    /**
     * Initializes the local XML Storage repository 
     * @param lock  if the storage has to be locked or not during the initalization
     * 
     * @throws Exception if the connection to eXist or its initialization fail
     */
    public void initialize(boolean ... lock) throws XMLStorageNotAvailableException {
	this.initialize(this.maxOperationsPerConnection, lock);
    }
    /**
     * Initializes the local XML Storage repository
     * @param maxOperationsPerConnection max number of operation per active connection
     * @param lock  if the storage has to be locked or not during the initalization
     * 
     * @throws Exception if the connection to eXist or its initialization fail
     */
    public void initialize(long maxOperationsPerConnection, boolean ... lock) throws XMLStorageNotAvailableException {
	
	if (this.getStatus() == STATUS.INITIALISED) {
	    logger.warn("XMLStorage already initialized");
	    return;
	}
	   // lock the instance
	if (lock != null && lock.length > 0 && lock[0]) {
	    this.lock();
	}
	try {
	    logger.info("Initializing XMLStorage...");
	    // this.printEnv();
	    this.maxOperationsPerConnection = maxOperationsPerConnection;//Long.valueOf((String) ICServiceContext.getContext().getProperty("maxOperationsPerConnection", true));

	    // register/create the DB instance
	    Class<?> cl = Class.forName(driver);
	    this.database = (Database) cl.newInstance();
	    database.setProperty("create-database", "true");
	    DatabaseManager.registerDatabase(this.database);	    

	    // try to load the collections for props and profiles
	    logger.info("Initializing the root collection");
	    this.rootCollection = DatabaseManager.getCollection(URI + DBBroker.ROOT_COLLECTION, USER, PWD);
	    if (this.rootCollection == null) {
		logger.error("invalid root collection!");
		throw new XMLStorageNotAvailableException("unable to load root collection");
	    }	   
	    this.rootCollection.setProperty("pretty", "true");
	    this.rootCollection.setProperty("encoding", "UTF-8");	    
	    this.setStatus(STATUS.INITIALISED);
	   
	    logger.info("XMLStorage initialized with success");
	} catch (XMLDBException edb) {
	    logger.error("unable to initialize XML storage ", edb);
	    throw new XMLStorageNotAvailableException("unable to initialize XML storage");
	} catch (Exception e) {
	    logger.error("unable to initialize XML storage ", e);
	    throw new XMLStorageNotAvailableException("unable to initialize XML storage");
	} catch (java.lang.NoClassDefFoundError ncdfe) {
	    logger.error("unable to initialize XML storage", ncdfe);
	    throw new XMLStorageNotAvailableException("unable to initialize XML storage");
	} finally {
	    if (lock != null && lock.length > 0 && lock[0]) {
	      this.unlock();
	    }
	}

    }
    
    /**
     * Creates a collection in the XML storage. If the input includes a full path, collections are created recursively
     * @param name the collection to create
     * @return the created collection
     * @throws XMLDBException 
     * @throws XMLStorageNotAvailableException
     */
    public Collection createCollection(String name) throws XMLDBException, XMLStorageNotAvailableException {
	String[] subcollections = name.split("/");
	Collection parent = this.rootCollection;
	for (String subcollection : subcollections) {
	    Collection child = parent.getChildCollection(subcollection);
	    logger.info("Creating subcollection " + subcollection);
	    if (child == null) {
		child = this.createCollection(parent, subcollection);
		if (child == null)
		    throw new XMLStorageNotAvailableException("unable to create collection " + name);
		else {
		    child.setProperty("pretty", "true");
		    child.setProperty("encoding", "UTF-8");
		}
	    }
	    parent = child;
	}
	return parent;
    }
    
    /**
     * Blocks all the writing operations
     */
    protected void lock() {
	writeLock.lock();
	this.locked = true;
	logger.trace("WRITE LOCK acquired");
	
    }

    /**
     * Unblocks all the writing operations
     */
    protected void unlock() {
	writeLock.unlock();	
	this.locked = false;
	logger.trace("WRITE LOCK released");
    }
    

    /**
     * Shutdowns the XML Storage repository
     * 
     * @return true if the operation succeed
     */
    public boolean shutdown(boolean lock) {
	if (lock) 
    	  this.lock();
	
	logger.info("XML storage is shutting down");
	try {
	    DatabaseInstanceManager manager = (DatabaseInstanceManager) rootCollection.getService("DatabaseInstanceManager", "1.0");
	    manager.shutdown();
	    this.setStatus(STATUS.SHUTDOWN);
	    logger.info("...XML storage is down");
		if (lock) 
		    this.lock();			
	} catch (XMLDBException edb) {
	    logger.fatal("Unable to shutdown XML storage", edb);	    
	} 
	return true;
    }

    private Collection loadCollection(String collectionName) throws XMLStorageNotAvailableException {
	return this.loadCollection(this.rootCollection, collectionName);
    }
    
    /**
     * Loads a collection. If it does not exist, the collection is created.
     * 
     * @param parentCollection
     *            the parent collection of the collection to load
     * @param collectionName
     *            name of the collection to load
     * @return the collection
     * @throws XMLStorageNotAvailableException 
     */
    private Collection loadCollection(Collection parentCollection, String collectionName) throws XMLStorageNotAvailableException {
	String[] subcollections = collectionName.split("/");
	if (subcollections.length < 1 ) {
	    logger.error("Invalid collection name " + collectionName);
	    return null;
	}
	// set the current collection
	Collection currentCollection = null;
	try {
	    for (String subcollection : subcollections) {
        	    currentCollection = parentCollection.getChildCollection(subcollection);
        	    if (currentCollection == null) {
        		// the collection does not exist, it is created
        		logger.info("Creating a new collection " + collectionName + "...");
        		currentCollection = this.createCollection(collectionName);
        	    }
        	    currentCollection.setProperty("pretty", "true");
        	    currentCollection.setProperty("encoding", "UTF-8");
        	    parentCollection = currentCollection;
	    }
	    
	} catch (XMLDBException edb) {
	    logger.error("failed to create collection " + collectionName + "!");
	    logger.error("" + edb.getCause());

	} catch (java.lang.NullPointerException e) {
	    logger.fatal("the XMLStorage is GONE!! a Restore is needed");
	    
	}
	return currentCollection;
    }

    /**
     * Discards the current collection
     * 
     */
    protected void resetCollection(Collection currentCollection) {
	try {
	    currentCollection.close();
	} catch (XMLDBException edb) {
	    // Catch any issues with closing the exception.
	    logger.error("unable to close collection " + edb.getMessage());
	}
	currentCollection = null;
    }

    /**
     * Loads the collection containing the WS-ResourceProperties documents. It must be used when
     * quering/storing/updating WS-ResourceProperties documents
     * 
     * @return the Collection
     */
    /*public Collection loadPropertiesCollection() {
	logger.debug("Loading collection Properties... ");
	return this.loadCollection(this.rootCollection, XMLStorage.PROPERTIES_COLLECTION_NAME);
    } */

    /**
     * Loads from the children of the Profile Collection, the collection identified by the given
     * name. It must be used when quering/storing/updating a particular kind of profile
     * 
     * @param collectionName
     *            the child collection of the Profile collection to load
     * @return the Collection
     */
   /* public Collection loadProfileCollection(String collectionName) {
	logger.debug("Loading collection " + collectionName + "... ");
	return this.loadCollection(this.profilesRootCollection, collectionName);
    }*/

    /**
     * Loads the parent collection of all collections containing resources profiles. It must be used
     * when quering all the profiles at the same time
     * 
     * @return the Collection
     */
    /*public Collection loadAllProfilesCollection() {
	logger.debug("Loading all profiles collection... ");
	return this.loadCollection(this.rootCollection, XMLStorage.PROFILES_COLLECTION_NAME);
    }*/

    /**
     * Loads the root collection. It must be used when quering all the information maintained by the
     * DB instance at the same time
     * 
     * @return the Collection
     */
    public Collection loadAllCollections() {

	Collection currentCollection = null;
	logger.debug("Loading all collections... ");
	// return this.loadCollection(this.rootCollection,
	// XMLStorage.PROFILES_COLLECTION_NAME);
	try {
	    currentCollection = DatabaseManager.getCollection(URI + DBBroker.ROOT_COLLECTION, USER, PWD);
	} catch (XMLDBException edb) {
	    logger.error("Failed to load all collections!");
	    logger.error("", edb);
	}
	return currentCollection;
    }

    /**
     * Stores a XMLDBDocument in the current collection. If the resource already
     * exists in the storage, it is updated.
     * 
     * @param resource
     *            the resource to store
     * @throws MalformedXMLResourceException 
     * @throws Exception
     *             if the storing fails
     */
    public void storeResource(GCUBEXMLResource resource) throws XMLStorageNotAvailableException, MalformedXMLResourceException {

	this.lock();	
	if (status != STATUS.INITIALISED) {
	    this.unlock();
	    throw new XMLStorageNotAvailableException("XMLStorage not initialized");
	}
	    
	Collection currentCollection = this.loadCollection(resource.getCollectionName());    

	if (currentCollection == null) {
	    logger.error("Unable to open the Collection");
	    this.unlock();
	    throw new XMLStorageNotAvailableException("Unable to open the Collection");
	}
	
	try {
	    XMLResource document = (XMLResource) currentCollection.createResource(resource.getResourceName(), "XMLResource");
	    document.setContent(resource.toString());	    
	    logger.debug("Storing/updating resource " + document.getId() + " in collection " + currentCollection.getName() + "...");
	    logger.trace("Resource content: " + document.getContent().toString());
	    currentCollection.storeResource(document);
	    logger.debug("...done");
	} catch (XMLDBException edb) {
	    logger.error("Failed to store resource " + resource.getResourceName());
	    logger.error("" + edb.errorCode + " " + edb.getMessage(), edb);
	    this.resetCollection(currentCollection);
	    operationsCounter++;
	    this.unlock();
	    throw new MalformedXMLResourceException(edb);
	} catch (Exception e) {
	    logger.error("" + e.getMessage(), e);
	    this.resetCollection(currentCollection);
	    operationsCounter++;
	    this.unlock();
	    throw new MalformedXMLResourceException(e);
	} finally {
	    this.resetCollection(currentCollection);
	    operationsCounter++;
	    this.unlock();	    
	}
	this.checkConnection();
    }

    /**
     * 
     * @return true if the connection to eXist is locked
     */
    public boolean isLocked() {
	return this.locked;
    }

    /**
     * Retrieves a resource from the storage given its ID
     * 
     * @param resourceID
     * @return
     * @throws Exception
     */
  /*  public GCUBEXMLResource retrieveResourceFromID(String resourceID) throws Exception {
	XMLResource res = null;
	Collection currentCollection = this.loadAllCollections();
	String collectionName = currentCollection.getName();
	try {
	    res = (XMLResource) currentCollection.getResource(resourceID);
	    if (res == null)
		logger.warn("Resource " + resourceID + " not found!");
	} catch (XMLDBException edb) {
	    logger.error("Failed to retrieve document " + resourceID);
	    logger.error("" + edb.errorCode + " " + edb.getMessage(), edb);
	    throw new Exception();
	}	
	return new GCUBEXMLResource(res, collectionName);
    }
    */
    

    /**
     * Retrieves a resource's content from the storage 
     * 
     * @param resource
     * @return
     * @throws Exception
     */
    synchronized public void retrieveResourceContent(GCUBEXMLResource resource) throws Exception {
	XMLResource res = null;
	Collection currentCollection = this.loadCollection(resource.getCollectionName());	
	try {
	    res = (XMLResource) currentCollection.getResource(resource.getResourceName());
	    if (res == null) {
		logger.warn("Resource " + resource.getResourceName() + " not found!");
		throw new Exception("Resource " + resource.getResourceName() + " not found!");
	    }
	    if (res.getContent() == null) {
		logger.warn("Resource content for " + resource.getResourceName() + " not found!");
		throw new Exception("Resource content for " + resource.getResourceName() + " not found!");
	    }
	    resource.deserializeFromIndexing(res.getContent().toString(), true);
	} catch (XMLDBException edb) {
	    logger.error("Failed to retrieve document " + resource.getResourceName());
	    logger.error("" + edb.errorCode + " " + edb.getMessage(), edb);
	    throw new Exception("Failed to retrieve document " + resource.getResourceName());
	} finally {
	    this.resetCollection(currentCollection);
	}
    }

    /**
     * Checks if the resource does exist in the storage
     * @param resource the resource to check
     * @return true if the resource is stored, false otherwise
     * @throws MalformedXMLResourceException if the input resource is not valid
     * @throws XMLStorageNotAvailableException 
     */
    public boolean resourceExists(GCUBEXMLResource resource) throws MalformedXMLResourceException, XMLStorageNotAvailableException {
	XMLResource res = null;
	Collection currentCollection = this.loadCollection(resource.getCollectionName());
	try {
	    res = (XMLResource) currentCollection.getResource(resource.getResourceName());
	} catch (XMLDBException edb) {
	    logger.warn("Resource " + resource.getResourceName() + " not found!");
	} finally {
	    this.resetCollection(currentCollection);
	}
	return (res == null)? false: true;
	
    }
    
    /**
     * 
     * @param xpathquery
     * @return
     */
    synchronized public GCUBEXMLResource executeXPathQuery(String xpathquery) {

	GCUBEXMLResource res = null;
	// ArrayList<XMLDBDocument> results = new
	// ArrayList<XMLDBDocument>();
	/*
	 * try { // get query-service XPathQueryServiceImpl service = (XPathQueryServiceImpl)
	 * currentCollection.getService("XPathQueryService", "1.0"); // set pretty-printing on
	 * service.setProperty(OutputKeys.INDENT, "yes"); service.setProperty(OutputKeys.ENCODING,
	 * "UTF-8"); ResourceSet set = service.query(xpathquery); logger.debug("number of returned
	 * documents: " + set.getSize()); ResourceIterator i = set.getIterator();
	 * while(i.hasMoreResources()) { res = new XMLDBDocument((BaseDAIXResource)
	 * i.nextResource()); System.out.println("DILIGENT resource " + i + " " + res.toString()); }
	 * 
	 * for (int i = 0; i < (int) set.getSize(); i++) { res = new
	 * XMLDBDocument((BaseDAIXResource) set.getResource((long) i));
	 * System.out.println("DILIGENT resource " + i + " " + res.toString()); } } catch
	 * (XMLDBException edb) { logger.error("failed to execute Xpath query " + xpathquery);
	 * edb.printStackTrace(); } catch (Exception e) { logger.error("exception " + xpathquery);
	 * logger.error(e.getStackTrace()); }
	 */
	return res;

    }

    /**
     * Deletes a resource 
     */
    synchronized public void deleteResource(GCUBEXMLResource resource) throws Exception {

	if (resource.getResourceName() == null) {
	    logger.warn("Invalid resource name");
	    return;
	}
	this.lock();	
	Collection collection = this.loadCollection(resource.getCollectionName());
	if (collection == null) {
	    //logger.error("Unable to load collection Properties!");
	    //throw new Exception();
	    collection = this.rootCollection;
	}
	try {
	    logger.info("Trying to remove resource " + resource.getResourceName() + " from collection " + collection.getName());
	    deleteResource(resource.getResourceName(), collection, false);
	} catch (XMLDBException edb) {
	    logger.error("Failed to remove the resource from the storage! ");
	    logger.error("", edb);
	    throw new Exception();
	} finally {
	    this.resetCollection(collection);
	    this.unlock();
	}

    }

    /**
     * Deletes a Profile resource identified by the given ID
     */
   /* synchronized public void retrieveAndDeleteProfileFromID(String profileID, String profileType) throws Exception {

	if (profileID == null) {
	    logger.warn("Invalid profile ID");
	    return;
	}
	this.lock();
	Collection profileCollection = this.loadProfileCollection(profileType);
	if (profileCollection == null) {
	    logger.error("Unable to load collection Profile!");
	    this.unlock();
	    throw new Exception("unable to load collection Profile");
	}
	try {
	    logger.info("Trying to remove profile '" + profileID + "' from collection " + profileCollection.getName());
	    deleteResource(profileID, profileCollection, false);
	} catch (Exception edb) {
	    logger.error("Failed to remove the profile from the storage! ");
	    throw new Exception(edb);		
    	} finally {
	    this.resetCollection(profileCollection);
	    this.unlock();
	}

    }*/

    /**
     * Deletes the resource with the given ID from the local storage
     * 
     * @param resourceID
     *            - the ID of the resource
     * @param col
     *            - the collection from which the resource has to be removed
     * @throws Exception
     */
    private void deleteResource(String resourceID, Collection col, boolean ... lock) throws Exception {

	XMLResource res = null;
	// lock the instance
	if (lock != null && lock.length > 0 && lock[0])
	    this.lock();	
	try {
	    res = (XMLResource) col.getResource(resourceID);
	    if (res == null)
		logger.warn("Resource " + resourceID + " not found!");
	    else {
		col.removeResource(res);
		logger.info("Resource successfully removed");
	    }
	} catch (XMLDBException edb) {
	    logger.error("Failed to retrieve resource " + resourceID);
	    throw new Exception(edb);
	} finally {
	    this.resetCollection(col);
	    if (lock != null && lock.length > 0 && lock[0])
	    	this.unlock();
	}
    }

    /**
     * Prints all the IDs of the Resources stored in the DB instance
     * 
     */
    public void printResourcesIDs() {

	String[] ress;
	Collection currentCollection = null;
	currentCollection = this.loadAllCollections();
	try {
	    if (currentCollection == null) {
		ress = this.rootCollection.listResources();
	    } else {
		ress = currentCollection.listResources();
	    }
	    for (int i = 0; i < ress.length; i++) {
		logger.debug("Resource ID:" + ress[i]);
	    }
	} catch (XMLDBException edb) {
	    logger.error("Failed to read resource IDs ", edb);
	} finally {
	    this.resetCollection(currentCollection);
	}
    }
    
    /**
     * Checks whether a collection exists or not
     * @param collectionName the name of the collection to check
     * @return true or false, depending if the collection exists or not
     */
    public boolean collectionExists(String collectionName) {
	String[] subcollections = collectionName.split("/");
	Collection parent = this.rootCollection;
	for (String subcollection : subcollections) {
	    try {
		Collection child = parent.getChildCollection(subcollection);
		if (child == null) {
		    return false;
		}
		parent = child;
	    } catch (XMLDBException e) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Creates a new collection
     * 
     * @param collectionName
     * @return the create Collection object
     */
    private Collection createCollection(Collection parentCollection, String collectionName) {

	Collection col = null;
	this.lock();
	try {
	    CollectionManagementService mgtService = (CollectionManagementService) parentCollection.getService("CollectionManagementService", "1.0");
	    col = mgtService.createCollection(collectionName);
	} catch (XMLDBException edb) {
	    logger.error("Failed to create collection " + collectionName, edb);
	} finally {
	    this.unlock();
	}

	return col;
    }

    /**
     * Deletes the collection from the storage
     * @param collectionName the name of the collection to delete
     */
    public void deleteCollection(String collectionName) throws XMLStorageNotAvailableException {

	this.lock();
	try {
	    //logger.info("Trying to delete the collection " + XMLStorage.PROPERTIES_COLLECTION_NAME + "...");
	    CollectionManagementService mgtService = (CollectionManagementService) rootCollection.getService("CollectionManagementService", "1.0");
	    mgtService.removeCollection(collectionName);
	    logger.info("Collection deleted");
	} catch (XMLDBException edb) {
	    logger.warn("Unable to delete the collection " + collectionName + ": " + edb.toString());
	    throw new XMLStorageNotAvailableException("Unable to delete the collection " + collectionName);
	} finally {
	    this.unlock();
	}
    }

    /**
     * Lists all the identifiers of the resources belonging the collection
     * @param collectionName the collection name
     * @return the list of identifiers
     * @throws XMLStorageNotAvailableException 
     */
    public String[] listAllCollectionResourceIDs(String collectionName) throws XMLStorageNotAvailableException {
	return listAllCollectionResourceIDs(this.loadCollection(collectionName));
    }

    /**
     * @return the status
     */
    public STATUS getStatus() {
	logger.trace("Status is " + status);
        return status;
    }

    /**
     * @param status the status to set
     */
    public void setStatus(STATUS status) {
	logger.trace("New status is " + status);
        this.status = status;
    }

    
    private String[] listAllCollectionResourceIDs(Collection collection) {
	String[] ids = null;
	String collectionName = "";
	try {
	    collectionName = collection.getName();
	    logger.debug("Retrieving all resource IDs from collection " + collectionName);
	    ids = new String[collection.getResourceCount()];
	    ids = collection.listResources();
	    logger.debug("Retrieved " + ids.length + " elements");
	} catch (XMLDBException edb) {
	    logger.warn("Unable to retrieve ids from collection " + collectionName + " " + edb.toString());
	}
	return ids;
    }
    
    protected void checkConnection() {
	if (this.operationsCounter > this.maxOperationsPerConnection) {
	    logger.info("It's time to reset the connection...");	   
	    this.lock();	    
	    this.shutdown(false);
	    //give a breath to the Transaction Manager
	    try {Thread.sleep(5000);} catch (InterruptedException e) {}
	    try {
		this.initialize(false);
	    } catch (XMLStorageNotAvailableException e) {
		logger.fatal("Unable to initialize XML storage", e);
	    }	    
	    this.operationsCounter = 0; 					    		
	    this.unlock();
	    logger.info("Connection reset");
	}
    }

  
    /**
     * 
     * XMLStorage not initialized exception
     *
     * @author Manuele Simi (ISTI-CNR)
     *
     */
    public static class XMLStorageNotAvailableException extends Exception {
	private static final long serialVersionUID = 1L;
	public XMLStorageNotAvailableException(String message) { super(message);}

    }
}
