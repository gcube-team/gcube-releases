package org.apache.commons.vfs2.provider;

import org.gcube.contentmanager.storageclient.model.protocol.smp.StringEncrypter;
import org.gcube.contentmanager.storageclient.model.protocol.smp.StringEncrypter.EncryptionException;


public class DecryptSmpUrl {

	public static String serviceClass;
	public static String serviceName;
	public static String owner;
	public static String accessType;
	public static String scopeType;

	public static void decrypt(String url){
		serviceClass=null;
		serviceName=null;
		owner=null;
		accessType=null;
		scopeType=null;
	String param = null;
	try {
		param = new StringEncrypter("DES").decrypt(url);
	} catch (EncryptionException e) {
		e.printStackTrace();
	}
	//logger.info("String decrypted: "+param);
	String [] getParam=param.split("\\&");
	

	String [] par1;
	for(String par : getParam){
		if(par.contains("ServiceClass")){
			par1=par.split("=");
			serviceClass=par1[1];
		}else if(par.contains("ServiceName")){
			par1=par.split("=");
			serviceName=par1[1];
		}else if(par.contains("owner")){
			par1=par.split("=");
			owner=par1[1];
		}else if(par.contains("scope")){
			par1=par.split("=");
			scopeType=par1[1];
		}else if(par.contains("AccessType")){
			par1=par.split("=");
			accessType=par1[1];			
		}
	}
	}
}
