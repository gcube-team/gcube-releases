package org.gcube.portlets.admin.authportletmanager.server;

import static org.gcube.common.authorization.client.Constants.authorizationService;
import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.common.authorization.client.exceptions.ObjectNotFound;
import org.gcube.common.authorization.library.policies.Action;
import org.gcube.common.authorization.library.policies.Policy;
import org.gcube.common.authorization.library.policies.PolicyType;
import org.gcube.common.authorization.library.policies.Roles;
import org.gcube.common.authorization.library.policies.Service2ServicePolicy;
import org.gcube.common.authorization.library.policies.ServiceAccess;
import org.gcube.common.authorization.library.policies.Services;
import org.gcube.common.authorization.library.policies.User2ServicePolicy;
import org.gcube.common.authorization.library.policies.Users;
import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.gcube.common.authorization.library.provider.UserInfo;
import org.gcube.common.portal.PortalContext;
import org.gcube.common.resources.gcore.GCoreEndpoint;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portlets.admin.authportletmanager.client.rpc.AuthManagerService;
import org.gcube.portlets.admin.authportletmanager.shared.Access;
import org.gcube.portlets.admin.authportletmanager.shared.Caller;
import org.gcube.portlets.admin.authportletmanager.shared.Caller.TypeCaller;
import org.gcube.portlets.admin.authportletmanager.shared.ConstantsSharing;
import org.gcube.portlets.admin.authportletmanager.shared.PolicyAuth;
import org.gcube.portlets.admin.authportletmanager.shared.Quote;
import org.gcube.portlets.admin.authportletmanager.shared.Service;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.ServiceException;
import org.gcube.portlets.admin.authportletmanager.shared.exceptions.TypeCallerException;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.UserManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.impl.LiferayUserManager;
import org.gcube.vomanagement.usermanagement.model.GCubeGroup;
import org.gcube.vomanagement.usermanagement.model.GCubeRole;
import org.gcube.vomanagement.usermanagement.model.GCubeUser;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.kernel.log.Log;
import com.liferay.portal.kernel.log.LogFactoryUtil;

/**
 * AuthServiceImpl
 * 
 * Support service request
 * 
 * @author "Alessandro Pieve " <a
 *         href="mailto:alessandro.pieve@isti.cnr.it">alessandro.pieve@isti.cnr.it</a>
 *
 */

public class AuthServiceImpl extends RemoteServiceServlet
implements AuthManagerService {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5302822657119071306L;
	private static final Log logger = LogFactoryUtil.getLog(AuthServiceImpl.class);

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void init() throws ServletException {
		super.init();
		System.out.println("Fix JAXP: jdk.xml.entityExpansionLimit=0");
		System.setProperty("jdk.xml.entityExpansionLimit", "0");

		System.out.println("initializing AccountingManager");

	}

	/**
	 * Load all list policy 
	 */
	@Override
	public ArrayList<PolicyAuth> loadListPolicy(String context) throws ServiceException {
		try {
			ArrayList<PolicyAuth> policyList =new ArrayList<PolicyAuth>();
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			String username = pContext.getCurrentUser(httpServletRequest).getUsername();

			if (context==null){
				logger.debug("loadListPolicy found context:"+null);		
				context = pContext.getCurrentScope(httpServletRequest);
			}
			String token;
			try{
				token= authorizationService().resolveTokenByUserAndContext(username, context);
			}catch  (ObjectNotFound e){
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
			}
			if (ConstantsSharing.DEBUG_MODE) {
				token=ConstantsSharing.DEBUG_TOKEN;
				context="/gcube/devNext";
				logger.debug("Debug load list policy in scope:"+context);					
			}
			logger.debug("load list policy in scope:"+context+"-token:"+token+"-username:"+username);	
			ScopeProvider.instance.set(context);
			SecurityTokenProvider.instance.set(token);

			List<Policy> policies = authorizationService().getPolicies(context);					
			if (policies!=null){
				logger.debug("Load List Policy Find:"+policies.size());
				if (policies.size()>0){
					for (Policy policy : policies){
						ArrayList<Caller> callers =new ArrayList<Caller>();
						boolean excludeCallers=false;
						if  (policy.getPolicyType()==PolicyType.USER){
							//retrive caller type user or role
							List<String> callerExcludes=((User2ServicePolicy) policy).getEntity().getExcludes();
							TypeCaller typecaller=TypeCaller.valueOf(((User2ServicePolicy) policy).getEntity().getType().toString().toLowerCase());
							//logger.debug("callerExcludes:"+callerExcludes +" and size:"+callerExcludes.size());
							if (callerExcludes.size()>0){
								//condition with all excepiton 
								for (String callerExclude: callerExcludes){
									callers.add(new Caller(typecaller,callerExclude));
								}		
								excludeCallers=true;
							}
							else{
								String callerIdentifier=((User2ServicePolicy) policy).getEntity().getIdentifier();

								if (callerIdentifier==null)
									callerIdentifier=ConstantsSharing.Star;
								callers.add(new Caller(typecaller,callerIdentifier));
							}
						}
						else{
							//retrive caller type service
							//logger.debug("retrieve a policy service"+policy.toString());
							String callerIdentifier=((Service2ServicePolicy) policy).getClient().getService().getName().toString()+
									":"+((Service2ServicePolicy) policy).getClient().getService().getServiceClass()+
									":"+((Service2ServicePolicy) policy).getClient().getService().getServiceId().toString();
							TypeCaller typecaller=TypeCaller.valueOf("service");
							callers.add(new Caller(typecaller,callerIdentifier));
						}						
						Long idpolicy=policy.getId();
						String serviceClass="ALL";
						String serviceName="ALL";
						String serviceId="ALL";
						if (!policy.getServiceAccess().toString().equals("*")){
							serviceClass=policy.getServiceAccess().getServiceClass();
							serviceName=policy.getServiceAccess().getName();
							serviceId=policy.getServiceAccess().getServiceId();
						}
						Service service =new Service(serviceClass,serviceName,serviceId);
						Calendar creationTime = policy.getCreationTime(); 

						Date dataInsert = creationTime.getTime(); 
						Calendar lastUpdateTime = policy.getLastUpdateTime(); 
						Date dataUpdate = lastUpdateTime.getTime(); 
						Access access= Access.valueOf(policy.getMode().toString());
						PolicyAuth policyAuth =new PolicyAuth(idpolicy,callers,excludeCallers,service,access,dataInsert,dataUpdate);
						logger.debug("traduce into a policy auth:"+policyAuth.toString());
						policyList.add(policyAuth);
					}
				}
			}
			return policyList;			
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("loadListPolicy error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}




	/**
	 * Load all caller 
	 */
	@Override
	public ArrayList<Caller> loadListCaller(String context) throws ServiceException {
		try {
			ArrayList<Caller> callers =new ArrayList<Caller>();

			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			String username = pContext.getCurrentUser(httpServletRequest).getUsername();
			if (context==null){
				context = pContext.getCurrentScope(httpServletRequest);
			}

			String token;
			try{
				token= authorizationService().resolveTokenByUserAndContext(username, context);
			}catch  (ObjectNotFound e){
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
			}
			if (ConstantsSharing.DEBUG_MODE) {
				token=ConstantsSharing.DEBUG_TOKEN;							
			}
			ScopeProvider.instance.set(context);
			SecurityTokenProvider.instance.set(token);
			if (ConstantsSharing.MOCK_UP) {
				logger.debug("List Caller on debug mode");
				callers.add(new Caller(TypeCaller.role,"administrator"));
				callers.add(new Caller(TypeCaller.user,"lucio.lelii"));
				callers.add(new Caller(TypeCaller.role,"agent"));
				callers.add(new Caller(TypeCaller.user,"luca.frosini"));
				callers.add(new Caller(TypeCaller.user,"giancarlo.panichi"));
				callers.add(new Caller(TypeCaller.role,"user"));
				callers.add(new Caller(TypeCaller.service,"InformationSystem:IC:all"));
				callers.add(new Caller(TypeCaller.service,"InformationSystem:IRegisrty:all"));
			}
			else{
				RoleManager roleManager = new LiferayRoleManager();
				UserManager userManager = new LiferayUserManager();
				//String scope=SessionUtil.getASLSession(session).getScope();
				GroupManager groupManager= new LiferayGroupManager();
				Long groupId= groupManager.getGroupIdFromInfrastructureScope(context);
				for (GCubeUser user :userManager.listUsersByGroup(groupId)){
					callers.add(new Caller(TypeCaller.user,user.getUsername()));
				}
				//List All roles
				for (GCubeRole role :roleManager.listAllGroupRoles()){
					callers.add(new Caller(TypeCaller.role,role.getRoleName()));
				}
				//List Service
				SimpleQuery query = queryFor(GCoreEndpoint.class);
				query.setResult("<Service><ServiceClass>{$resource/Profile/ServiceClass/text()}</ServiceClass>" +
						"<ServiceName>{$resource/Profile/ServiceName/text()}</ServiceName></Service>");

				DiscoveryClient<ServiceResult> client = clientFor(ServiceResult.class);
				List<ServiceResult> resources = client.submit(query);
				for (ServiceResult result: resources){
					callers.add(new Caller(TypeCaller.service,result.getServiceClass()+":"+result.getServiceName()));
				}
			}
			return callers;		
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("loadListCaller  error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage(),e);
		}

	}

	/**
	 * Load all service
	 */
	@Override
	public Map<String, List<String>> loadListService(String context) throws ServiceException {
		try {
			logger.debug("loadListService init with context:"+context);
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (context==null){
				context = pContext.getCurrentScope(httpServletRequest);
				logger.debug("Context void,loadListService init with context:"+context);

			}
			Map<String, List<String>> services =new HashMap<String, List<String>>();
			if (ConstantsSharing.DEBUG_MODE) {
				String token=ConstantsSharing.DEBUG_TOKEN;
				SecurityTokenProvider.instance.set(token);		
			}
			if (ConstantsSharing.MOCK_UP) {
				services.put("InformationSystem", Arrays.asList("IC", "ISRegistry"));
				services.put("DataAccess", Arrays.asList("SpeciesProductsDiscovery","dAAaCCESS@"));	
				services.put("DataAnalysis", Arrays.asList("StatisticalManager"));
			}
			else{
				String username = pContext.getCurrentUser(httpServletRequest).getUsername();
				String token;
				try{
					token= authorizationService().resolveTokenByUserAndContext(username, context);
				}catch  (ObjectNotFound e){
					token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				}
				//String token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
				SecurityTokenProvider.instance.set(token);
				ScopeProvider.instance.set(context);
				SimpleQuery query = queryFor(GCoreEndpoint.class);
				query.setResult("<Service><ServiceClass>{$resource/Profile/ServiceClass/text()}</ServiceClass>" +
						"<ServiceName>{$resource/Profile/ServiceName/text()}</ServiceName></Service>");
				DiscoveryClient<ServiceResult> client = clientFor(ServiceResult.class);

				List<ServiceResult> resources = client.submit(query);
				for (ServiceResult result: resources){
					if (!services.containsKey(result.getServiceClass()))
						services.put(result.getServiceClass(),new ArrayList<String>());
					services.get(result.getServiceClass()).add(result.getServiceName());
				}
			}
			return services;		
		} catch (ServiceException e) {
			logger.error("service errore"+e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {			
			logger.error("loadListService error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	/**
	 * Load all access
	 */
	@Override
	public ArrayList<String> loadListAccess() throws ServiceException {
		try {
			ArrayList<String> access =new ArrayList<String>();
			for( Access accessValue : Access.values() ) 
				access.add(access.toString());
			return access;		
		} catch (Throwable e) {			
			logger.error("loadListAccess error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}

	}



	public ArrayList<String> loadRetrieveListContexts() throws ServiceException {
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			String token = pContext.getCurrentUserToken(httpServletRequest);

			SecurityTokenProvider.instance.set(token);
			ArrayList<String> toReturn = new ArrayList<String>();
			String currentContext = ScopeProvider.instance.get();

			GroupManager gm = new LiferayGroupManager();
			long currentGroupId = gm.getGroupIdFromInfrastructureScope(currentContext);
			GCubeGroup currentGroup = gm.getGroup(currentGroupId);
			// three cases
			if(gm.isVRE(currentGroupId)){
				// do nothing
			}else if(gm.isVO(currentGroupId)){
				// iterate over its vres
				List<GCubeGroup> children = currentGroup.getChildren();
				for (GCubeGroup gCubeGroup : children) {
					logger.debug("loadListContext add: " + gm.getInfrastructureScope(gCubeGroup.getGroupId()));
					toReturn.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));
				}
			}else{
				// is root
				List<GCubeGroup> children = currentGroup.getChildren();
				for (GCubeGroup gCubeGroup : children) {
					toReturn.add(gm.getInfrastructureScope(gCubeGroup.getGroupId()));

					// get the vo children
					List<GCubeGroup> childrenVO = gCubeGroup.getChildren();
					for (GCubeGroup voChildren : childrenVO) {
						logger.debug("loadListContext add: " + gm.getInfrastructureScope(voChildren.getGroupId()));
						toReturn.add(gm.getInfrastructureScope(voChildren.getGroupId()));
					}
				}
			}
			toReturn.add(currentContext);
			// revert
			Collections.reverse(toReturn);
			return toReturn;
		} catch (Throwable e) {			
			logger.error("loadRetrieveListContexts error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	/**
	 * Insert a new List Policy
	 * @return 
	 * @throws Throwable 
	 */
	@Override
	public  void addPolicies(String context,List<PolicyAuth> policiesAuth) throws ServiceException {
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (context==null){
				//context=aslSession.getScope();
				context = pContext.getCurrentScope(httpServletRequest);
				logger.debug("Context void,addPolicies init with context:"+context);

			}
			List<Policy> policies = new ArrayList<Policy>();
			String username = pContext.getCurrentUser(httpServletRequest).getUsername();
			String token;
			try{
				token= authorizationService().resolveTokenByUserAndContext(username, context);
			}catch  (ObjectNotFound e){
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
			}
					if (ConstantsSharing.DEBUG_MODE) {
				token=ConstantsSharing.DEBUG_TOKEN;

			}
			ScopeProvider.instance.set(context);
			SecurityTokenProvider.instance.set(token);
			for (PolicyAuth policy: policiesAuth){
				ServiceAccess service=new ServiceAccess(policy.getService().getServiceName(), policy.getService().getServiceClass(), policy.getService().getServiceId());
				Action access =Action.valueOf(policy.getAccessString());
				logger.debug("policy translate:"+policy.getCallerTypeAsString());
				if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.user.toString())){
					logger.debug("add policy with user");
					policies.add(new User2ServicePolicy(context, service, Users.one(policy.getCallerAsString()), access  ));
				}
				else if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.role.toString())){
					logger.debug("add policy with role");
					policies.add(new User2ServicePolicy(context, service, Roles.one(policy.getCallerAsString()), access  ));
				}
				else if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.service.toString())){
					logger.debug("add policy with service");
					String[] policyService=policy.getCallerAsString().split(":");
					String serviceName=policyService[0].trim();
					String serviceClass=policyService[1].trim();
					String serviceId="All";
					if (policyService.length==3)
						serviceId=policy.getCallerAsString().split(":")[2];
					ServiceAccess serviceCaller =new ServiceAccess(serviceName, serviceClass, serviceId);
					policies.add(new Service2ServicePolicy(context,service,Services.specialized(serviceCaller),access));
				}
				else{
					String[] allExecpt=policy.getCallerTypeAsString().trim().split(" ");
					if (allExecpt.length>0){
						if (allExecpt[0].equalsIgnoreCase(TypeCaller.user.toString())){
							logger.debug("add policy with user execpt");
							policies.add(new User2ServicePolicy(context, service, Users.allExcept(policy.getCallerExecptAsString()), access  ));
						}
						else if (allExecpt[0].equalsIgnoreCase(TypeCaller.role.toString())){
							logger.debug("add policy with role execpt");
							policies.add(new User2ServicePolicy(context, service, Roles.allExcept(policy.getCallerExecptAsString()), access  ));
						}
						else{
							logger.error("ERROR caller type not recognized"+allExecpt[0]);
							throw new TypeCallerException("Caller type not found");
						}
					}
					else{
						logger.error("ERROR caller type not recognized"+policy.getCallerTypeAsString());
						throw new TypeCallerException("Caller type not found");
					}
				}
			}
			logger.info("INSERT CALLER:"+policies.toString());
			authorizationService().addPolicies(policies);
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;				
		} catch (Exception e) {
			logger.error("addPolicies error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());

		} 
	}

	/**
	 * Update an existing policy 
	 */
	@Override
	public void updatePolicy(String context,PolicyAuth policy) throws ServiceException{
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (context==null){
				//context=aslSession.getScope();
				context = pContext.getCurrentScope(httpServletRequest);
				logger.debug("Context void,addPolicies init with context:"+context);
			}
			String username = pContext.getCurrentUser(httpServletRequest).getUsername();
			String token;
			try{
				token= authorizationService().resolveTokenByUserAndContext(username, context);
			}catch  (ObjectNotFound e){
				token = authorizationService().generateUserToken(new UserInfo(username, new ArrayList<String>()), context);
			}
			if (ConstantsSharing.DEBUG_MODE) {
				token=ConstantsSharing.DEBUG_TOKEN;
			}
			SecurityTokenProvider.instance.set(token);
			ScopeProvider.instance.set(context);
			//remove old policy 
			authorizationService().removePolicies(policy.getIdpolicy());
			//add a new policy
			List<Policy> policies = new ArrayList<Policy>();
			ServiceAccess service=new ServiceAccess(policy.getService().getServiceName(), policy.getService().getServiceClass(), policy.getService().getServiceId());
			logger.info("updatePolicy -policy.getCallerAsString()"+policy.getCallerAsString());
			Action access =Action.valueOf(policy.getAccessString());
			

			if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.user.toString())){
				logger.debug("add policy with user");
				policies.add(new User2ServicePolicy(context, service, Users.one(policy.getCallerAsString()), access  ));
			}
			else if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.role.toString())){
				logger.debug("add policy with role");
				policies.add(new User2ServicePolicy(context, service, Roles.one(policy.getCallerAsString()), access  ));
			}
			else if (policy.getCallerTypeAsString().equalsIgnoreCase(TypeCaller.service.toString())){
				logger.debug("add policy with service");
				String[] policyService=policy.getCallerAsString().split(":");
				String serviceName=policyService[0].trim();
				String serviceClass=policyService[1].trim();
				String serviceId="All";
				if (policyService.length==3)
					serviceId=policy.getCallerAsString().split(":")[2];
				ServiceAccess serviceCaller =new ServiceAccess(serviceName, serviceClass, serviceId);
				policies.add(new Service2ServicePolicy(context,service,Services.specialized(serviceCaller),access));
			}
			else{
				String[] allExecpt=policy.getCallerTypeAsString().trim().split(" ");
				if (allExecpt.length>0){
					if (allExecpt[0].equalsIgnoreCase(TypeCaller.user.toString())){
						logger.debug("add policy with user execpt");
						policies.add(new User2ServicePolicy(context, service, Users.allExcept(policy.getCallerExecptAsString()), access  ));
					}
					else if (allExecpt[0].equalsIgnoreCase(TypeCaller.role.toString())){
						logger.debug("add policy with role execpt");
						policies.add(new User2ServicePolicy(context, service, Roles.allExcept(policy.getCallerExecptAsString()), access  ));
					}
					else{
						logger.error("ERROR caller type not recognized"+allExecpt[0]);
						throw new TypeCallerException("Caller type not found");
					}
				}
				else{
					logger.error("ERROR caller type not recognized"+policy.getCallerTypeAsString());
					throw new TypeCallerException("Caller type not found");
				}
			}
			//policies.add(new User2ServicePolicy(context, service, Users.one(policy.getCallerAsString()), access  ));
			authorizationService().addPolicies(policies);

		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			e.printStackTrace();
			logger.error("updatePolicy error: " + e.getLocalizedMessage(), e);
			throw new ServiceException(e.getLocalizedMessage());
		}

	}


	@Override
	public List<Long> deletePolicies(List<Long> identifiers)throws ServiceException {
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();

			ArrayList<PolicyAuth> policy =new ArrayList<PolicyAuth>();
			String token = pContext.getCurrentUserToken(httpServletRequest);

			if (ConstantsSharing.DEBUG_MODE) {
				token=ConstantsSharing.DEBUG_TOKEN;
			}
			String context = pContext.getCurrentScope(httpServletRequest);
			ScopeProvider.instance.set(context);

			SecurityTokenProvider.instance.set(token);
			for (Long identifier:identifiers){
				logger.info("Remove policy idpolicy "+identifier);
				authorizationService().removePolicies(identifier);
			}
			return identifiers;		
		} catch (ServiceException e) {
			logger.error(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		} catch (Throwable e) {
			logger.error("deletePolicies error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();			
			throw new ServiceException(e.getLocalizedMessage());
		}
	}



	/**
	 * Load all list quota 
	 */
	@Override
	public ArrayList<Quote> loadListQuota() throws ServiceException {
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();

			ArrayList<Quote> quote =new ArrayList<Quote>();
			if (ConstantsSharing.MOCK_UP) {
				logger.info("loadListQuota");
				for (Quote quoteList : TableUtils.SERVICESQUOTE.values()){
					quote.add(quoteList);
				}
			}
			else{
				logger.info("loadListQuota ");
				for (Quote quoteList : TableUtils.SERVICESQUOTE.values()){

					quote.add(quoteList);
				}
			}
			return quote;		
		} catch (Throwable e) {			
			logger.error("loadListQuota error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

	@Override
	public List<Long> deleteQuote(List<Long> identifiers)throws ServiceException {
		try {
		
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (ConstantsSharing.MOCK_UP) {
				for (Long identifier:identifiers){
					logger.info("Remove quote idquote "+identifier);
					TableUtils.SERVICESQUOTE.remove(identifier);
				}
			}
			else{
				for (Long identifier:identifiers){
					logger.info("Remove quote idquote "+identifier);
					TableUtils.SERVICESQUOTE.remove(identifier);
				}
			}

			return identifiers;		
		} catch (Throwable e) {			
			logger.error("deleteQuote error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}
	}

	@Override
	public List<Quote> addQuote(List<Quote> quote) throws ServiceException {
		try {
			Long identifier;
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (ConstantsSharing.MOCK_UP) {
				for (Quote quota: quote){
					identifier =(long)(Math.random()*1000000000);
					logger.info("Insert new quota for "+quota.getCallerAsString()+ "return with:"+identifier);
					quota.setIdQuote(identifier);
					Calendar calendarStart = Calendar.getInstance();
					Date dateStart =  calendarStart.getTime();
					quota.setDataInsert(dateStart);
					TableUtils.SERVICESQUOTE.put(identifier, quota);
				}
			}
			else{
				for (Quote quota: quote){
					identifier =(long)(Math.random()*1000000000);
					logger.info("Insert new quota for "+quota.getCallerAsString()+ "return with:"+identifier);
					quota.setIdQuote(identifier);
					Calendar calendarStart = Calendar.getInstance();
					Date dateStart =  calendarStart.getTime();
					quota.setDataInsert(dateStart);
					TableUtils.SERVICESQUOTE.put(identifier, quota);
				}
			}
			return quote;		
		} catch (Throwable e) {
			logger.error("addQuote error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();			
			throw new ServiceException(e.getLocalizedMessage());
		}
	}


	/**
	 * Update an existing quote 
	 */
	@Override
	public Quote updateQuote(Quote quote) throws ServiceException{
		try {
			HttpServletRequest httpServletRequest=this.getThreadLocalRequest();
			PortalContext pContext = PortalContext.getConfiguration();
			if (ConstantsSharing.MOCK_UP) {
				//TableUtils.SERVICES.
				TableUtils.SERVICESQUOTE.remove(quote.getIdQuote());	

				Calendar calendarStart = Calendar.getInstance();
				Date dateUpdate =  calendarStart.getTime();
				quote.setDataUpdate(dateUpdate);

				TableUtils.SERVICESQUOTE.put(quote.getIdQuote(), quote);
				logger.info("Update quote identifier:"+quote.getIdQuote());
			}
			else{
				TableUtils.SERVICESQUOTE.remove(quote.getIdQuote());	
				Calendar calendarStart = Calendar.getInstance();
				Date dateUpdate =  calendarStart.getTime();
				quote.setDataUpdate(dateUpdate);
				TableUtils.SERVICESQUOTE.put(quote.getIdQuote(), quote);
				logger.info("Update quote identifier:"+quote.getIdQuote());
			}

			return quote;		
		} catch (Throwable e) {			
			logger.error("updateQuote error: " + e.getLocalizedMessage(), e);
			e.printStackTrace();
			throw new ServiceException(e.getLocalizedMessage());
		}

	}

}
