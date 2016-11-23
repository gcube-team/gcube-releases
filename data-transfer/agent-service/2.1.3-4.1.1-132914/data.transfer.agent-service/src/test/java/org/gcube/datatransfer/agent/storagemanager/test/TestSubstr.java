package org.gcube.datatransfer.agent.storagemanager.test;

import org.apache.commons.vfs2.provider.UriParser;
import org.gcube.common.core.scope.GCUBEScope;
import org.gcube.contentmanager.storageclient.wrapper.AccessType;


public class TestSubstr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
	//	String  rootName = "smp://temporary/testFile?ServiceClass=TestClass&ServiceName=TestService&owner=Nick";
		String  rootName = "smp://temporary/testFile?ServiceClass=TestClass&servicename=TestService&owner=Nick&accesstype=SHarED";
		String scheme = UriParser.extractScheme(rootName);
		 System.out.println("scheme="+scheme);
		 
		 String serviceClass=getServiceClass(rootName);
         String serviceName=getServiceName(rootName);
         String owner=getOwner(rootName);
         AccessType accessType=getAccessType(rootName);
         GCUBEScope scope=getScope(rootName);
         System.out.println("'"+serviceClass+"' - '"+
        		 serviceName+"' - '"+
        		 owner+"' - '"+
        		 accessType+"' - '"+
        		 scope+"'");

	}
	 public static String getServiceClass(String name){
		 String[] parts = name.toString().split("ServiceClass");
	    	if(parts!=null){
	    		if(parts.length==2){
	    			String msg=parts[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	String[] parts2 = name.toString().split("serviceclass");
	    	if(parts2!=null){
	    		if(parts2.length==2){
	    			String msg=parts2[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	return null;	    	
	    }
	    public static String getServiceName(String name){
	    	String[] parts = name.toString().split("ServiceName");
	    	if(parts!=null){
	    		if(parts.length==2){
	    			String msg=parts[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	String[] parts2 = name.toString().split("servicename");
	    	if(parts2!=null){
	    		if(parts2.length==2){
	    			String msg=parts2[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	return null;
	    }
	    public static String getOwner(String name){
	    	String[] parts = name.toString().split("Owner");
	    	if(parts!=null){
	    		if(parts.length==2){
	    			String msg=parts[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	String[] parts2 = name.toString().split("owner");
	    	if(parts2!=null){
	    		if(parts2.length==2){
	    			String msg=parts2[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return msg.split("&")[0];
	    			else return msg;
	    		}
	    	}
	    	return null;
	    }
	    public static AccessType getAccessType(String name){
	    	String[] parts = name.toString().split("AccessType");
	    	if(parts!=null){
	    		if(parts.length==2){
	    			String msg=parts[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return AccessType.valueOf(msg.split("&")[0].toUpperCase());
	    			else return AccessType.valueOf(msg);
	    			
	    		}
	    	}
	    	String[] parts2 = name.toString().split("accesstype");
	    	if(parts2!=null){
	    		if(parts2.length==2){
	    			String msg=parts2[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return AccessType.valueOf(msg.split("&")[0].toUpperCase());
	    			else return AccessType.valueOf(msg);
	    		}
	    	}
	    	return null;
	    }
	    public static GCUBEScope getScope(String name){
	    	String[] parts = name.toString().split("Scope");
	    	if(parts!=null){
	    		if(parts.length==2){
	    			String msg=parts[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return GCUBEScope.getScope(msg.split("&")[0]);
	    			else return GCUBEScope.getScope(msg);
	    		}
	    	}
	    	String[] parts2 = name.toString().split("scope");
	    	if(parts2!=null){
	    		if(parts2.length==2){
	    			String msg=parts2[1];
	    			msg=msg.substring(1, msg.length());
	    			if(msg.split("&")!=null)return GCUBEScope.getScope(msg.split("&")[0]);
	    			else return GCUBEScope.getScope(msg);
	    		}
	    	}
	    	return null;
	    }
}
