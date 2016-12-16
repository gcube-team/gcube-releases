//package org.gcube.application.framework.core.security;
//
//import java.io.BufferedReader;
//import java.io.File;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.rmi.RemoteException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.xml.rpc.ServiceException;
//
//import org.apache.axis.message.addressing.AttributedURI;
//import org.apache.axis.message.addressing.EndpointReferenceType;
//import org.apache.axis.types.URI.MalformedURIException;
//import org.gcube.application.framework.core.session.ASLSession;
//import org.gcube.application.framework.core.util.ASLGroupModel;
//import org.gcube.application.framework.core.util.Settings;
//import org.gcube.application.framework.core.util.UsersManagementServiceConstants;
//import org.gcube.vomanagement.usermanagement.ws.GetGroupParentId;
//import org.gcube.vomanagement.usermanagement.ws.GetRootVO;
//import org.gcube.vomanagement.usermanagement.ws.GetScope;
//import org.gcube.vomanagement.usermanagement.ws.GetUserId;
//import org.gcube.vomanagement.usermanagement.ws.GroupModel;
//import org.gcube.vomanagement.usermanagement.ws.GroupRetrievalFault;
//import org.gcube.vomanagement.usermanagement.ws.IsRootVO;
//import org.gcube.vomanagement.usermanagement.ws.IsVO;
//import org.gcube.vomanagement.usermanagement.ws.LiferaySOAPGroupManagerPortType;
//import org.gcube.vomanagement.usermanagement.ws.LiferaySOAPUserManagerPortType;
//import org.gcube.vomanagement.usermanagement.ws.ListGroups;
//import org.gcube.vomanagement.usermanagement.ws.ListGroupsByUser;
//import org.gcube.vomanagement.usermanagement.ws.ListGroupsResponse;
//import org.gcube.vomanagement.usermanagement.ws.UserManagementSystemException;
//import org.gcube.vomanagement.usermanagement.ws.service.LiferaySOAPGroupManagerServiceAddressingLocator;
//import org.gcube.vomanagement.usermanagement.ws.service.LiferaySOAPUserManagerServiceAddressingLocator;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//public class UsersManagementUtils {
//	
//	/** The logger. */
//	private static final Logger logger = LoggerFactory.getLogger(UsersManagementUtils.class);
//	
//	String umServiceLocation = new String();
//	
//	public UsersManagementUtils() {
//		StringBuffer fileData = new StringBuffer(1000);
//        BufferedReader reader = null;
//		try {
//			reader = new BufferedReader(new FileReader(Settings.getInstance().getProperty("sharedDir")+ File.separator +  "UMServiceLocation.config"));
//		} catch (FileNotFoundException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		int numRead = 0;
//		char[] buf = new char[1024];
//		try {
//			while((numRead=reader.read(buf)) != -1){
//			    String readData = String.valueOf(buf, 0, numRead);
//			    fileData.append(readData);
//			    buf = new char[1024];
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//        try {
//			reader.close();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//        umServiceLocation = fileData.toString().trim();
//	}
//	
//	public ArrayList<String> getAllScopes(ASLSession session) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		ArrayList<String> groupsNames = new ArrayList<String>();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			ListGroups groups = new ListGroups();
//			
//			ListGroupsResponse resGroup = groupStub.listGroups(groups);
//			
//			GroupModel[] groupList = resGroup.get_return();
//			
//			for (GroupModel groupModel : groupList) {
//				logger.info("Group NAME " + groupModel.getGroupName());
//				
//				GetRootVO rootVO = new GetRootVO();
//				if (!groupStub.getRootVO(rootVO).get_return().getGroupId().equals(groupModel.getGroupId())) {
//					String scope = null;
//					
//					GetScope getScope = new GetScope();
//					getScope.setArg0(groupModel.getGroupId());
//					scope = groupStub.getScope(getScope).get_return();
//					
//					if (scope.startsWith("/")) 
//						groupsNames.add(scope);
//				}
//			}
//			
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return groupsNames;
//		
//	}
//	
//	
//	public String getRootVO() {
//		
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		ArrayList<String> groupsNames = new ArrayList<String>();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		LiferaySOAPGroupManagerPortType groupStub = null;
//		try {
//			groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		ListGroups groups = new ListGroups();
//		
//		try {
//			ListGroupsResponse resGroup = groupStub.listGroups(groups);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		GetRootVO rootVO = new GetRootVO();
//		try {
//			String rootVo = groupStub.getRootVO(rootVO).get_return().getGroupId();
//			String rootVoName = getScope(rootVo);
//			logger.info("RootVo returning: " + rootVoName);
//			return rootVoName;
//		} catch (GroupRetrievalFault e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return null;
//	}
//
//	
//	public String getUserId(String username) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		String userId = null;
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPUserManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPUserManager));
//			
//			try {
//				LiferaySOAPUserManagerPortType userManagerStub = new LiferaySOAPUserManagerServiceAddressingLocator().getLiferaySOAPUserManagerPortTypePort(endpointUser);
//				GetUserId getUserId = new GetUserId();
//				getUserId.setArg0(username);
//				userId = userManagerStub.getUserId(getUserId).get_return();
//			} catch (ServiceException e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception:", e);
//			} catch (UserManagementSystemException e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception:", e);
//			} catch (RemoteException e) {
//				// TODO Auto-generated catch block
//				logger.error("Exception:", e);
//			}
//			
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		return userId;
//	}
//	
//	
//	public List<ASLGroupModel> listGroupsByUser(String userId) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		List<ASLGroupModel> groupsByUser = new ArrayList<ASLGroupModel>();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			ListGroupsByUser listGroupsByUser = new ListGroupsByUser();
//			listGroupsByUser.setArg0(userId);
//			GroupModel[] groups = groupStub.listGroupsByUser(listGroupsByUser).get_return();
//			if(groups==null)
//				logger.debug("User is in no group.");
//			for (int i = 0; i < groups.length; i++) {
//				ASLGroupModel grm = new ASLGroupModel();
//				grm.setGroupId(new Long(groups[i].getGroupId()));
//				grm.setGroupName(groups[i].getGroupName());
//				grm.setDescription(groups[i].getDescription());
//				groupsByUser.add(grm);
//			}
//			
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return groupsByUser;
//	}
//	
//	
//	public List<ASLGroupModel> listGroups()  {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		List<ASLGroupModel> groups = new ArrayList<ASLGroupModel>();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			ListGroups listGroups = new ListGroups();
//			GroupModel[] groupsArray = groupStub.listGroups(listGroups).get_return();
//			for (int i = 0; i < groupsArray.length; i++) {
//				ASLGroupModel gm = new ASLGroupModel();
//				gm.setGroupId(new Long(groupsArray[i].getGroupId()));
//				gm.setGroupName(groupsArray[i].getGroupName());
//				gm.setDescription(groupsArray[i].getDescription());
//				groups.add(gm);
//			}
//			
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return groups;
//	}
//	
//	
//	public boolean isVO(String groupId) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		List<GroupModel> groups = new ArrayList<GroupModel>();
//		boolean isVO = false;
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			
//			IsVO isVo = new IsVO();
//			isVo.setArg0(groupId);
//			isVO = groupStub.isVO(isVo).get_return();
//			return isVO;
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (GroupRetrievalFault e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return false;
//	}
//	
//	
//	public boolean isRootVO(String groupId) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		boolean isVO = false;
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			
//			IsRootVO isRootVo = new IsRootVO();
//			isRootVo.setArg0(groupId);
//			isVO = groupStub.isRootVO(isRootVo).get_return();
//			return isVO;
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (GroupRetrievalFault e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return false;
//	}
//	
//	
//	public String getScope(String groupId) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			
//			GetScope getScope = new GetScope();
//			getScope.setArg0(groupId);
//			String scope = groupStub.getScope(getScope).get_return();
//			return scope;
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (GroupRetrievalFault e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return null;
//	}
//	
//	
//	public String getGroupParentId(String groupId) {
//		EndpointReferenceType endpointUser = new EndpointReferenceType();
//		try {
//			//endpointUser.setAddress(new AttributedURI("http://dl10.di.uoa.gr:8181/usermanagement-ws/LiferaySOAPGroupManager"));
//			endpointUser.setAddress(new AttributedURI(umServiceLocation + UsersManagementServiceConstants.LiferaySOAPGroupManager));
//		} catch (MalformedURIException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		LiferaySOAPGroupManagerServiceAddressingLocator loc = new LiferaySOAPGroupManagerServiceAddressingLocator();
//		try {
//			LiferaySOAPGroupManagerPortType groupStub = loc.getLiferaySOAPGroupManagerPortTypePort(endpointUser);
//			
//			GetGroupParentId getGroupParentId = new GetGroupParentId();
//			getGroupParentId.setArg0(groupId);
//			long parentId = groupStub.getGroupParentId(getGroupParentId).get_return();
//			return Long.toString(parentId);
//		} catch (ServiceException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (GroupRetrievalFault e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (UserManagementSystemException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		} catch (RemoteException e) {
//			// TODO Auto-generated catch block
//			logger.error("Exception:", e);
//		}
//		
//		return null;
//	}
//	
//	
//}
