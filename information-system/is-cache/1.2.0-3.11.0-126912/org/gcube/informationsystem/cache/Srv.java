package org.gcube.informationsystem.cache;

import java.io.Serializable;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.axis.message.addressing.Address;
import org.apache.axis.message.addressing.EndpointReference;
import org.apache.axis.message.addressing.EndpointReferenceType;
import org.gcube.common.core.informationsystem.client.AtomicCondition;
import org.gcube.common.core.informationsystem.client.RPDocument;
import org.gcube.common.core.informationsystem.client.ISClient.ISUnsupportedQueryException;
import org.gcube.common.core.informationsystem.client.queries.GCUBERIQuery;
import org.gcube.common.core.informationsystem.client.queries.WSResourceQuery;
import org.gcube.common.core.resources.GCUBERunningInstance;
import org.gcube.common.core.resources.runninginstance.Endpoint;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.common.core.utils.logging.GCUBELog;
import org.gcube.informationsystem.cache.consistency.manager.ConsistencyManagerIF;

/**
 * Srv class represents a service entity
 * 
 * @author UoA
 * 
 */
public class Srv implements Serializable, Cloneable {

	/** autogen serialUID */
	private static final long serialVersionUID = -2699724529778231761L;

	/** service class */
	private String srvClass;

	/** service name */
	private String srvName;
	
	/** service portType name -  optional */
	private String portTypeName = null;
	
	private ConsistencyManagerIF myManager = null;
	
	private GCUBEScope scope = null;

	/**
	 * Hashtable<br>
	 * <dl>
	 * <dt>key</dt>
	 * <dd>service subtype, e.g. simple ws, factory, ws-resource</dd>
	 * <dt>value</dt>
	 * <dd>endpoint reference set</dd>
	 * </dl>
	 */
	private HashMap<String, Set<EndpointReference>> eprs;
	
	/**
	 * Hashtable<br>
	 * <dl>
	 * <dt>key</dt>
	 * <dd>service subtype, e.g. simple ws, factory, ws-resource
	 * <dt>value</dt>
	 * <dd>filtering criteria</dd>
	 */
	private HashMap<String, HashMap<String, String>> filterCriteria = new HashMap<String, HashMap<String,String>>();

	/** logger */
	private static GCUBELog log = new GCUBELog(Srv.class);

	/** 
	 * Constructor
	 * @param srvClass service class
	 * @param srvName service name
	 */
	public Srv(String srvClass, String srvName) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
		this.setEprs(new HashMap<String, Set<EndpointReference>>());
	}
	
	/** 
	 * Constructor
	 * @param srvClass service class
	 * @param srvName service name
	 * @param portTypeName portType specific name
	 */
	public Srv(String srvClass, String srvName, String portTypeName) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
		this.setPortTypeName(portTypeName);
		this.setEprs(new HashMap<String, Set<EndpointReference>>());
	}

	/** 
	 * Constructor
	 * @param srvClass service class
	 * @param srvName service name
	 * @param h {@link #filterCriteria} instance
	 */
	public Srv(String srvClass, String srvName, HashMap<String, Set<EndpointReference>> h) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
		this.setEprs(h);
	}
	
	/** 
	 * Constructor
	 * @param srvClass service class
	 * @param srvName service name
	 * @param portTypeName portType specific name
	 * @param h {@link #filterCriteria} instance
	 */
	public Srv(String srvClass, String srvName,  String portTypeName, HashMap<String, Set<EndpointReference>> h) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
		this.setPortTypeName(portTypeName);
		this.setEprs(h);
	}

	/**
	 * equals method.
	 * @return true if both objects are equal; false otherwise
	 * @param o object to compare against
	 * @see Object#equals(Object)
	 */
	@Override
	public boolean equals(Object o) {
		if (o instanceof Srv) {
			if (((Srv) o).getSrvName().compareTo(this.getSrvName()) != 0)
				return false;
			if (((Srv) o).getSrvClass().compareTo(this.getSrvClass()) != 0)
				return false;
			if (((Srv) o).getPortTypeName() == null && this.getPortTypeName() != null)
				return false;
			if (((Srv) o).getPortTypeName() != null && this.getPortTypeName() == null)
				return false;
			if (((Srv) o).getPortTypeName() == null && this.getPortTypeName() == null)
				return true;
			if (((Srv) o).getPortTypeName().compareTo(this.getPortTypeName()) != 0)
				return false;
			return true;
		}
		return false;
	}

	/**
	 * Atomic setter
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 */
	public void setSrv(String srvClass, String srvName) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
	}
	
	/**
	 * Atomic setter
	 * 
	 * @param srvClass
	 *            service class
	 * @param srvName
	 *            service name
	 * @param portTypeName
	 * 			  portType name
	 */
	public void setSrv(String srvClass, String srvName, String portTypeName) {
		this.setSrvClass(srvClass);
		this.setSrvName(srvName);
		this.setPortTypeName(portTypeName);
	}

	/**
	 * Clone Srv object.
	 * @return new cloned object
	 * @throws CloneNotSupportedException in case of CloneNotSupported
	 */
	public Object clone() throws CloneNotSupportedException {
		Srv cloned = (Srv)super.clone();
		cloned.myManager=this.myManager;
		return cloned;
		//return new Srv(this.getSrvClass(), this.getSrvName(), this.getEprs());
	}

	/**
	 * toString
	 * @return string representation of this object
	 * 
	 */
	@Override
	public String toString() {
		return "ServiceClass: " + this.getSrvClass() + "\tServiceName: "
				+ this.getSrvName() + "\tPortTypeName: " + this.getPortTypeName() + ".";
	}

	/**
	 * Used by the cache refreshing component. Retrieves the Running Instances
	 * (RIs) of the Srv instance.
	 * 
	 * @return the Running Instances (RIs) of the Srv instance.
	 * @throws Exception
	 *             in case of error; most probably IS querying exceptions
	 */
/*	public String[] getMyRIs() throws Exception {
		if (this.getSrvType().compareTo(SrvType.SIMPLE.toString()) == 0)
			return goForSimple();
		if (this.getSrvType().compareTo(SrvType.FACTORY.toString()) == 0)
			return goForFactory();
		if (this.getSrvType().compareTo(SrvType.STATEFULL.toString()) == 0)
			return goForStateful();
		throw new Exception("Service Type (" + this.getSrvType()
				+ ") is not valid!");
	}*/

	/**
	 * Invoked if the service is a simple web service.
	 * 
	 * @return corresponding RIs
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public Set<EndpointReference> goForSimple() throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException, Exception {
/*
		log.debug("**************************************************");
		log.debug("SimpleRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		for(String myscope : ScopesUtil.enumerateParentScopes(scope.toString())) {
			ret.addAll(goForSimple(myscope));
		}
		log.debug("**************************************************");
		return ret;
		*/
		
		
		log.debug("SimpleRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		LinkedList<AtomicCondition> riACs = new LinkedList<AtomicCondition>();
		riACs.add(new AtomicCondition("//ServiceClass", this.getSrvClass()));
		riACs.add(new AtomicCondition("//ServiceName", this.getSrvName()));

		if(this.getFilterCriteria().get(SrvType.SIMPLE.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.SIMPLE.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.SIMPLE.toString()).get(var);
				log.debug("Adding " + var + ":" + val);
				riACs.add(new AtomicCondition(var, val));
			}
		}
		
		GCUBERIQuery query = ISRetriever.getRIQuery();
		for (AtomicCondition ac : riACs)
			query.addAtomicConditions(ac);
		log.info("GetEPRs query: " + query.getExpression());
		List<GCUBERunningInstance> rp = ISRetriever.getISClient().execute(
				query, this.getScope());
		log.info("Number of results: " + rp.size());
		for (GCUBERunningInstance rpd : rp) {
			//in case a specific portType name is specified only the related EPR is added to the results
			if(this.portTypeName != null){
				EndpointReferenceType epr = rpd.getAccessPoint().getEndpoint(this.portTypeName);
				if(epr != null){
					log.debug("Simple - adding epr for specific portType: " + epr.getAddress().toString());
					ret.add(new EndpointReference(new Address(epr.getAddress().toString())));
				}
			}else{
				
				List<Endpoint> lis = rpd.getAccessPoint()
					.getRunningInstanceInterfaces().getEndpoint();
				for (Endpoint e : lis) {
					log.debug("Simple - adding epr: " + e.getValue());
					EndpointReference epr = new EndpointReference(new Address(e.getValue()));
					ret.add(epr);
				}
			
			}
		}
		return ret;
	}
	
	private Set<EndpointReference> goForSimple(String scope) throws ISUnsupportedQueryException,
	InstantiationException, IllegalAccessException, Exception {
		String serviceClass = this.getSrvClass();
		String serviceName = this.getSrvName();
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		String queryStr = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
							"declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';" +                                                                                                                                  
							"for $result in collection(\"/db/Profiles/RunningInstance\")//Document/Data/is:Profile/Resource " +
							"where ($result/Profile/DeploymentData/Status/string() eq \"ready\") and ($result//ServiceClass/string() eq \"" + serviceClass + "\") and " +
							"($result//ServiceName/string() eq \"" + serviceName + "\") and ($result//Scope = \"" + scope + "\") ";

		if(this.getFilterCriteria().get(SrvType.SIMPLE.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.SIMPLE.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.SIMPLE.toString()).get(var);
				queryStr += " ($result//" + var + "/string() eq \"" + val + "\")";
			}
		}
		queryStr += " return $result";
		
		GCUBERIQuery query = ISRetriever.getRIQuery();
		query.setExpression(queryStr);
		
		log.info("GetEPRss query: " + query.getExpression());
		List<GCUBERunningInstance> rp = ISRetriever.getISClient().execute(
				query, this.getScope());
		log.info("Number of results: " + rp.size());
		for (GCUBERunningInstance rpd : rp) {
			//in case a specific portType name is specified only the related EPR is added to the results
			if(this.portTypeName != null){
				EndpointReferenceType epr = rpd.getAccessPoint().getEndpoint(this.portTypeName);
				if(epr != null){
					log.debug("Simple(private) - adding epr for specific portType: " + epr.getAddress().toString());
					ret.add(new EndpointReference(new Address(epr.getAddress().toString())));
				}
			}else{
				List<Endpoint> lis = rpd.getAccessPoint()
					.getRunningInstanceInterfaces().getEndpoint();
				for (Endpoint e : lis) {
					log.debug("Simple(private) - adding epr: " + e.getValue());
					EndpointReference epr = new EndpointReference(new Address(e.getValue()));
					ret.add(epr);
				}
			}
		}

		return ret;
	}

	/**
	 * Invoked if the service is a factory service.
	 * 
	 * @return corresponding RIs
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public Set<EndpointReference> goForFactory() throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException, Exception {
/*		
		log.debug("**************************************************");
		log.debug("FactoryRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		for(String myscope : ScopesUtil.enumerateParentScopes(scope.toString())) {
			ret.addAll(goForFactory(myscope));
		}
		log.debug("**************************************************");
		return ret;*/
		
		log.debug("FactoryRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		LinkedList<AtomicCondition> riACs = new LinkedList<AtomicCondition>();
		riACs.add(new AtomicCondition("//ServiceClass", this.getSrvClass()));
		riACs.add(new AtomicCondition("//ServiceName", this.getSrvName()));

		if(this.getFilterCriteria().get(SrvType.FACTORY.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.FACTORY.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.FACTORY.toString()).get(var);
				log.debug("Adding " + var + ":" + val);
				riACs.add(new AtomicCondition(var, val));
			}
		}
		
		GCUBERIQuery query = ISRetriever.getRIQuery();
		for (AtomicCondition ac : riACs)
			query.addAtomicConditions(ac);
		log.info("GetEPRs query: " + query.getExpression());
		List<GCUBERunningInstance> rp = ISRetriever.getISClient().execute(
				query, this.getScope());
		log.info("Number of results: " + rp.size());
		for (GCUBERunningInstance rpd : rp) {
			//in case a specific portType name is specified only the related EPR is added to the results
			if(this.portTypeName != null){
				EndpointReferenceType epr = rpd.getAccessPoint().getEndpoint(this.portTypeName);
				if(epr != null){
					log.debug("Factory - adding epr for specific portType: " + epr.getAddress().toString());
					ret.add(new EndpointReference(new Address(epr.getAddress().toString())));
				}
			}else{
				List<Endpoint> lis = rpd.getAccessPoint()
					.getRunningInstanceInterfaces().getEndpoint();
				for (Iterator iterator = lis.iterator(); iterator.hasNext();) {
					Endpoint endpoint = (Endpoint) iterator.next();
					if (endpoint.getEntryName().indexOf("Factory") != -1) {
						log.debug("Factory - adding epr: " + endpoint.getValue());
						EndpointReference epr = new EndpointReference(new Address(endpoint.getValue()));
						ret.add(epr);
					}
				}
			
			}
		}

		return ret;
	}

	/**
	 * Invoked if the service is a factory service.
	 * 
	 * @return corresponding RIs
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	private Set<EndpointReference> goForFactory(String scope) throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException, Exception {
		log.debug("FactoryRefresh for " + this.toString());
		String serviceClass = this.getSrvClass();
		String serviceName = this.getSrvName();
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		String queryStr = "declare namespace is = 'http://gcube-system.org/namespaces/informationsystem/registry';" +
							"declare namespace gc = 'http://gcube-system.org/namespaces/common/core/porttypes/GCUBEProvider';" +                                                                                                                                  
							"for $result in collection(\"/db/Profiles/RunningInstance\")//Document/Data/is:Profile/Resource " +
							"where ($result/Profile/DeploymentData/Status/string() eq \"ready\") and ($result//ServiceClass/string() eq \"" + serviceClass + "\") and " +
							"($result//ServiceName/string() eq \"" + serviceName + "\") and ($result//Scope = \"" + scope + "\") ";

		if(this.getFilterCriteria().get(SrvType.FACTORY.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.FACTORY.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.FACTORY.toString()).get(var);
				queryStr += " ($result//" + var + "/string() eq \"" + val + "\")";
			}
		}
		queryStr += " return $result";
		
		GCUBERIQuery query = ISRetriever.getRIQuery();
		query.setExpression(queryStr);
		
		log.info("GetEPRss query: " + query.getExpression());
		List<GCUBERunningInstance> rp = ISRetriever.getISClient().execute(
				query, this.getScope());
		log.info("Number of results: " + rp.size());
		for (GCUBERunningInstance rpd : rp) {
			if(this.portTypeName != null){
				EndpointReferenceType epr = rpd.getAccessPoint().getEndpoint(this.portTypeName);
				if(epr != null){
					log.debug("Factory(private) - adding epr for specific portType: " + epr.getAddress().toString());
					ret.add(new EndpointReference(new Address(epr.getAddress().toString())));
				}
			}else{
				List<Endpoint> lis = rpd.getAccessPoint()
					.getRunningInstanceInterfaces().getEndpoint();
				for (Iterator iterator = lis.iterator(); iterator.hasNext();) {
					Endpoint endpoint = (Endpoint) iterator.next();
					if (endpoint.getEntryName().indexOf("Factory") != -1) {
						log.debug("Factory(private) - adding epr: " + endpoint.getValue());
						EndpointReference epr = new EndpointReference(new Address(endpoint.getValue()));
						ret.add(epr);
					}
				}
			}
		}

		return ret;
	}
	
	
	/**
	 * Invoked if the service is a ws-resource. Note that the respective IS
	 * query is slightly changed compared to the other service types. It only
	 * retrieves RIs if their is an existing, running ws-resource.
	 * 
	 * @return corresponding RIs
	 * @throws ISUnsupportedQueryException
	 *             in case of error
	 * @throws InstantiationException
	 *             in case of error
	 * @throws IllegalAccessException
	 *             in case of error
	 * @throws Exception
	 *             in case of error
	 */
	public Set<EndpointReference> goForStateful() throws ISUnsupportedQueryException,
			InstantiationException, IllegalAccessException, Exception {
/*		log.debug("**************************************************");
		log.debug("StatefulRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();

		for(String myscope : ScopesUtil.enumerateParentScopes(scope.toString())) {
			ret.addAll(goForStateful(myscope));
		}
//		ret.addAll(goForStateful(scope.toString()));
		log.debug("**************************************************");
		return ret;*/
		
		log.debug("**************************************************");
		log.debug("StatefulRefresh for " + this.toString());
		Set<EndpointReference> ret = new HashSet<EndpointReference>();
		
		//if a portType name is specified, we return an empty set, since the specific portType of the RI
		//that created the WS resource is not published
		if(this.portTypeName != null)
		{
			log.debug("Statefull - return an empty set, since a specific portType of the RI that created a WS resource is not published");
			return ret;
		}

		LinkedList<AtomicCondition> wsPropDocACs = new LinkedList<AtomicCondition>();
		wsPropDocACs.add(new AtomicCondition("//gc:ServiceClass", this
				.getSrvClass()));
		wsPropDocACs.add(new AtomicCondition("//gc:ServiceName", this
				.getSrvName()));
		
		if(this.getFilterCriteria().get(SrvType.STATEFULL.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.STATEFULL.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.STATEFULL.toString()).get(var);
				log.debug("Adding " + var + ":" + val);
				wsPropDocACs.add(new AtomicCondition(var, val));
			}
		}

		WSResourceQuery query = ISRetriever.getWSResourceQuery();
		for (AtomicCondition ac : wsPropDocACs)
			query.addAtomicConditions(ac);
		log.info("EPR query: " + query.getExpression());
		List<RPDocument> rp = ISRetriever.getISClient().execute(query,
				this.getScope());

		log.debug("Number of results " + rp.size());
		for (RPDocument rpd : rp)
			ret.add(new EndpointReference(rpd.getEndpoint()));
		log.debug("**************************************************");
		return ret;
	}
	
	private Set<EndpointReference> goForStateful(String scope) throws ISUnsupportedQueryException,
	InstantiationException, IllegalAccessException, Exception {
		Set<EndpointReference> ret = new HashSet<EndpointReference>();
		
		//if a portType name is specified, we return an empty set, since the specific portType of the RI
		//that created the WS resource is not published
		if(this.portTypeName != null)
		{
			log.debug("Statefull(private) - return an empty set, since a specific portType of the RI that created a WS resource is not published");
			return ret;
		}
		
		LinkedList<AtomicCondition> wsPropDocACs = new LinkedList<AtomicCondition>();
		wsPropDocACs.add(new AtomicCondition("//gc:ServiceClass", this
				.getSrvClass()));
		wsPropDocACs.add(new AtomicCondition("//gc:ServiceName", this
				.getSrvName()));
		wsPropDocACs.add(new AtomicCondition("//gc:Scope", scope));
		
		if(this.getFilterCriteria().get(SrvType.STATEFULL.toString()) != null) {
			Iterator<String> it = this.getFilterCriteria().get(SrvType.STATEFULL.toString()).keySet().iterator();
			while (it.hasNext()) {
				String var = it.next();
				String val = this.getFilterCriteria().get(SrvType.STATEFULL.toString()).get(var);
				log.debug("Adding " + var + ":" + val);
				wsPropDocACs.add(new AtomicCondition(var, val));
			}
		}
		
		WSResourceQuery query = ISRetriever.getWSResourceQuery();
		for (AtomicCondition ac : wsPropDocACs)
			query.addAtomicConditions(ac);
		log.info("EPR query: " + query.getExpression());
		List<RPDocument> rp = ISRetriever.getISClient().execute(query,
				this.getScope());
		
		log.debug("Number of results " + rp.size());
		for (RPDocument rpd : rp)
			ret.add(new EndpointReference(rpd.getEndpoint()));
		return ret;
	}

	/**
	 * @see Object#hashCode()
	 */
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}

	public void forceRefreshService() throws Exception {
		this.getEprs().put(SrvType.FACTORY.toString(),
				this.goForFactory());
		this.getEprs().put(SrvType.SIMPLE.toString(), this.goForSimple());
		this.getEprs().put(SrvType.STATEFULL.toString(),
				this.goForStateful());
	}
	
	/*
	 * Test main method
	 */
	public static void main(String[] args) {

		Srv srv1 = null;
		srv1 = new Srv("1", "2");
		Srv srv2 = null;
		srv2 = new Srv("1", "2");
		System.out.println(srv1.equals(srv2));
		System.out.println(srv1.equals(srv1));
		System.out.println(srv2.equals(srv2));
		if (args.length != 0)
			printUsageAndExit();
	}

	/*
	 * Test printUsageAndExit method
	 */
	private static void printUsageAndExit() {
		System.err.println("Wrong number of arguments.\n" + "Usage: java\n"
				+ "Aborting...\n");
		System.exit(1);
	}

	// Getters / Setters

	/**
	 * Get service class
	 * 
	 * @return service class
	 */
	public String getSrvClass() {
		return this.srvClass;
	}

	/**
	 * Get service name
	 * 
	 * @return service name
	 */
	public String getSrvName() {
		return this.srvName;
	}
	
	/**
	 * Get portType name
	 * 
	 * @return portType name
	 */
	public String getPortTypeName() {
		return this.portTypeName;
	}

	/**
	 * Set service class
	 * 
	 * @param srvClass
	 *            service class
	 */
	public void setSrvClass(String srvClass) {
		this.srvClass = srvClass;
	}

	/**
	 * Set service name
	 * 
	 * @param srvName
	 *            service name
	 */
	public void setSrvName(String srvName) {
		this.srvName = srvName;
	}
	
	/**
	 * Set portType name
	 * 
	 * @param portTypeName
	 *            portType name
	 */
	public void setPortTypeName(String portTypeName) {
		this.portTypeName = portTypeName;
	}

	/**
	 * @param eprs
	 *            the eprs to set
	 */
	public void setEprs(HashMap<String, Set<EndpointReference>> eprs) {
		this.eprs = eprs;
	}

	/**
	 * @return the eprs
	 */
	public HashMap<String, Set<EndpointReference>> getEprs() {
		return eprs;
	}
	
	/**
	 * @return the eprs
	 */
	public Set<EndpointReference> getEPRs() throws Exception {
/*		Iterator<String> it = eprs.keySet().iterator();
		Set<String> col = new HashSet<String>();
		while(it.hasNext()) {
			String key = it.next();
			col.addAll(eprs.get(key));
		}
		return col;*/
		return this.getMyManager().getEPRs(this);
	}
	
	/**
	 * @return the eprs
	 */
	public Set<EndpointReference> getEPRs(String serviceType) throws Exception {
/*		return eprs.get(serviceType);*/
		return this.getMyManager().getEPRs(this, serviceType);
	}

	/**
	 * @param filterCriteria the filterCriteria to set
	 */
	public void setFilterCriteria(HashMap<String, HashMap<String, String>> filterCriteria) {
		this.filterCriteria = filterCriteria;
	}

	/**
	 * @return the filterCriteria
	 */
	public HashMap<String, HashMap<String, String>> getFilterCriteria() {
		return filterCriteria;
	}

	/**
	 * @param myManager the myManager to set
	 */
	protected void setMyManager(ConsistencyManagerIF myManager) {
		this.myManager = myManager;
	}

	/**
	 * @return the myManager
	 */
	protected ConsistencyManagerIF getMyManager() {
		return myManager;
	}
	
	public static boolean isServiceTypeValid(String srvType) {
		if(srvType.equals(SrvType.SIMPLE.toString()) ||
			srvType.equals(SrvType.FACTORY.toString()) ||
			srvType.equals(SrvType.STATEFULL.toString()))
			return true;
		return false;
	}

	/**
	 * @param scope the scope to set
	 */
	protected void setScope(GCUBEScope scope) {
		this.scope = scope;
	}

	/**
	 * @return the scope
	 */
	protected GCUBEScope getScope() {
		return scope;
	}
}
