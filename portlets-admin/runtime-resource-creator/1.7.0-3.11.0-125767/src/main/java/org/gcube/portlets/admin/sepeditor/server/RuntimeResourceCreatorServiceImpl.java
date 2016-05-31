package org.gcube.portlets.admin.sepeditor.server;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.informationsystem.publisher.RegistryPublisher;
import org.gcube.portlets.admin.sepeditor.client.RuntimeResourceCreatorService;
import org.gcube.portlets.admin.sepeditor.shared.FilledRuntimeResource;
import org.gcube.portlets.admin.sepeditor.shared.InitInfo;
import org.gcube.portlets.admin.sepeditor.shared.Property;
import org.gcube.portlets.admin.sepeditor.shared.RRAccessPoint;
import org.gcube.resourcemanagement.support.server.managers.resources.RuntimeResourceManager;
import org.gcube.resourcemanagement.support.server.managers.scope.ScopeManager;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;



/**
 * The server side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class RuntimeResourceCreatorServiceImpl extends RemoteServiceServlet implements	RuntimeResourceCreatorService {
	private static final Logger _log = Logger.getLogger(RuntimeResourceCreatorServiceImpl.class);
	
	private boolean withinPortal = false;
	private static final String SCOPE = "/gcube/devsec";

	private final String EDIT_ID_ATTR = "RMP_EDIT_ID";
	String idToTest = "b7fce5e0-b0e5-11e2-9d26-c9dc2c525e1c";

	/**
	 * the current ASLSession
	 * @return the session
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute("username");
		if (user == null) {
			_log.warn("USER IS NULL setting test.user");
			user = "test.user";		
			sessionID = "123";
		}
		else {
			withinPortal = true;
			_log.info("LIFERAY PORTAL DETECTED user=" + user);
		}
		ASLSession toReturn = SessionManager.getInstance().getASLSession(sessionID, user);
		if (!withinPortal)
			toReturn.setScope(SCOPE);
		return toReturn;
	}

	@Override
	public Boolean createRuntimeResource(String scope,	FilledRuntimeResource rs, boolean isUpdate) {
		String currScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		ServiceEndpoint rt;
		boolean result = true;
		try {
			if (!isUpdate) {
				_log.info("Trying creating Runtime Resource: " + rs.getResourceName() + " SCOPE: " + scope);
				rt = new ServiceEndpoint();
			}
			else {
				SimpleQuery query = queryFor(ServiceEndpoint.class);
				DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
				query.addCondition("$resource/ID/text() eq '" + rs.getResourceId() + "'");
				_log.info("Trying editing  Runtime Resource ID: " + rs.getResourceId());
				rt = client.submit(query).get(0); //only one rr per id


			}
			rt.newProfile();
			rt.profile().category(rs.getCategory());
			rt.profile().description(rs.getDescription());
			rt.profile().newRuntime().ghnId(rs.getRuntimegHNUniqueID());
			rt.profile().runtime().hostedOn(rs.getRuntimeHostedOn());
			rt.profile().name(rs.getResourceName());
			rt.profile().runtime().status(rs.getRuntimeStatus());
			
			rt.profile().newPlatform().name(rs.getPlatformName());
			rt.profile().platform().version((short) Integer.parseInt( (rs.getPlatformVersion().equals("")) ? "0" : rs.getPlatformVersion()) );

			rt.profile().platform().minorVersion((short) Integer.parseInt( (rs.getPlatformMinorVersion().equals("")) ? "0" : rs.getPlatformMinorVersion()) );
			rt.profile().platform().revisionVersion((short) Integer.parseInt( (rs.getPlatformRevisionVersion().equals("")) ? "0" : rs.getPlatformRevisionVersion()) );
			rt.profile().platform().buildVersion((short) 	Integer.parseInt( (rs.getPlatformBuildVersion().equals("")) ? "0" : rs.getPlatformBuildVersion()) );

			ArrayList<RRAccessPoint> myAccessPoints = rs.getRRAccessPoints();

			for (RRAccessPoint p : myAccessPoints) {
				AccessPoint a = new AccessPoint();
				a.address(p.getInterfaceEndPoint());
				a.name(p.getInterfaceEntryNameAttr());
				a.description(p.getDesc());

				String encryptedPassword = StringEncrypter.getEncrypter().encrypt(p.getPassword());
				a.credentials(encryptedPassword, p.getUsername());
			
				for (Property prop : p.getProperties()) {
					String propValue = prop.isCrypted() ? StringEncrypter.getEncrypter().encrypt(prop.getValue()) : prop.getValue();
					org.gcube.common.resources.gcore.ServiceEndpoint.Property pToAdd = 
							new org.gcube.common.resources.gcore.ServiceEndpoint.Property().nameAndValue(prop.getKey(), propValue);
					pToAdd.encrypted(prop.isCrypted());
					a.properties().add(pToAdd);
				}

				rt.profile().accessPoints().add(a);
			}
	
			ScopeProvider.instance.set(scope.toString());		
			RuntimeResourceManager gm = new RuntimeResourceManager();
			RegistryPublisher publisher = gm.getRegistryPublisher();		
				
			if (! isUpdate) {
				String id  = publisher.create(rt).id();
				_log.trace("Created new RR sent, Got from publisher: id=" + id);
			} else {
				System.out.println("Updating " + rs.getResourceId());
				for (String scope2Update: rt.scopes()) {
					ScopeProvider.instance.set(scope2Update);
					publisher.update(rt);
					System.out.println("Updated " + rs.getResourceId() + " on " + scope2Update);
				}
				_log.trace("Updated RR sent");
			}

		} catch (Throwable e) {
			_log.error("ERROR While Creating or Updating RT Resource");
			e.printStackTrace();
			return false;
		}
		_log.info("Registration Request successfully Sent");
		ScopeProvider.instance.set(currScope);
		return result;
	}

	@Override
	public InitInfo getInitialInfo(boolean isEditMode, String idToEdit, String curScope) {
		String rootScope = "";
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute("username");
		if (user != null) {
			rootScope = getASLSession().getScope();
			_log.debug("Portal mode, scope = " + rootScope);
		}
		else {
			rootScope = curScope;
			_log.debug("Standalone mode, scope = " + rootScope);
		}
	
		ArrayList<String> scopes = getAvailableScopes(rootScope);
		if (! isEditMode) {
			_log.info("Editing Mode OFF");
			return new InitInfo(scopes, null);
		}
		/*
		 * else return the bean of the resource to edit
		 */
		//get the id from the session
		String rr2editId = idToEdit;
		_log.info("Editing Mode ON for id: " + rr2editId);
		FilledRuntimeResource fRR = getResource2EditById(rr2editId, rootScope);
		return new InitInfo(scopes, fRR);
	}

	private FilledRuntimeResource getResource2EditById(String id, String scope) {
		try {
			SimpleQuery query = queryFor(ServiceEndpoint.class);
			DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
			query.addCondition("$resource/ID/text() eq '" + id + "'");
			String currScope = ScopeProvider.instance.get();
			ScopeProvider.instance.set(scope);
			_log.info("Looking fro id: " + id + "on " + scope);
			List<ServiceEndpoint> resources = client.submit(query);
			ScopeProvider.instance.set(currScope);
			ServiceEndpoint sEndPoint = null;
			try {
				sEndPoint = resources.get(0);
			} catch (IndexOutOfBoundsException e) {
				return new FilledRuntimeResource();
			}
			
			ArrayList<RRAccessPoint> acPoints = new ArrayList<RRAccessPoint>();
			for (AccessPoint ac : sEndPoint.profile().accessPoints()) {
				RRAccessPoint rac = new RRAccessPoint();

				rac.setInterfaceEndPoint(ac.address());
				rac.setInterfaceEntryNameAttr(ac.name());
				rac.setDesc(ac.description());
				rac.setUsername(ac.username());
				String decryptedPassword = StringEncrypter.getEncrypter().decrypt(ac.password());
				rac.setPassword(decryptedPassword); 

				ArrayList<Property> props = new ArrayList<Property>();
				for (org.gcube.common.resources.gcore.ServiceEndpoint.Property prop : ac.properties()) {
					String propDecValue = prop.isEncrypted() ? StringEncrypter.getEncrypter().decrypt(prop.value()) :prop.value();
					props.add(new Property(prop.name(), propDecValue, prop.isEncrypted()));
				}
				rac.setProperties(props);
				acPoints.add(rac);
			}
			return new FilledRuntimeResource(
					id, 
					acPoints, 
					sEndPoint.profile().name(), 
					sEndPoint.profile().version(),
					sEndPoint.profile().category(),
					sEndPoint.profile().description(),
					sEndPoint.profile().platform().name(),
					""+sEndPoint.profile().platform().version(), 
					""+sEndPoint.profile().platform().minorVersion(),
					""+sEndPoint.profile().platform().revisionVersion(),
					""+sEndPoint.profile().platform().buildVersion(), 
					sEndPoint.profile().runtime().hostedOn(),
					sEndPoint.profile().runtime().status(),
					sEndPoint.profile().runtime().ghnId());
		} 
		catch (Exception e) {
			e.printStackTrace();
			return new FilledRuntimeResource();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<String> getAvailableScopes(String rootScope) {
		ArrayList<String> retval = new ArrayList<String>();
		System.out.println("currentScope: " + rootScope);
		try {
			Map<String, ScopeBean> scopes = ScopeManager.readScopes(this.getScopeDataPath());
			for (ScopeBean scope : scopes.values()) {
				if (scope.toString().startsWith(rootScope))
					retval.add(scope.toString());
			}
			Collections.sort(retval);
			return retval;
		} catch (Exception e) {
			_log.warn("Exception during getAvailableScopes(), if you run standalone you must pass the scope via GET -> &curscope=$TheScope"); 
			return retval;
		}
	}

	

	private String getScopeDataPath() {
		String startDirectory = getServletFSPath();
		return startDirectory + File.separator + "xml" + File.separator +"scopedata_admin.xml";

	}
	private String getServletFSPath() {
		return this.getServletContext().getRealPath("") + File.separator + "WEB-INF";
	}


	public static String fileToString(final String path) throws IOException {
		BufferedReader filebuf = null;
		String nextStr = null;
		StringBuilder ret = new StringBuilder();

		filebuf = new BufferedReader(new FileReader(path));
		nextStr = filebuf.readLine(); // legge una riga dal file
		while (nextStr != null) {
			ret.append(nextStr);
			nextStr = filebuf.readLine(); // legge la prossima riga
		}
		filebuf.close(); // chiude il file

		return ret.toString();
	}

	public static Document getDocumentGivenXML(final String result) {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		dbf.setValidating(false);
		DocumentBuilder db;
		Document document = null;
		try {
			db = dbf.newDocumentBuilder();
			document = db.parse(new ByteArrayInputStream(result.getBytes()));
		} catch (ParserConfigurationException e1) {
			e1.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return document;
	}



}
