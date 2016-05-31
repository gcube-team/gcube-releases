package org.gcube.resources.federation.fhnmanager.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.gcube.resources.federation.fhnmanager.api.type.VMProvider;
import org.gcube.resources.federation.fhnmanager.utils.Props;
import org.gcube.resources.federation.fhnmanager.utils.VomsProxy;
import org.gcube.vomanagement.occi.FHNConnector;
import org.gcube.vomanagement.occi.OcciConnector;
import org.gcube.vomanagement.occi.exceptions.UnsupportedCredentialsTypeException;
import org.gcube.vomanagement.occi.utils.ScriptUtil;
import org.gcube.vomanagement.occi.utils.X509CredentialManager;

public class ConnectorFactory {

	private Map<String, FHNConnector> connectors = new HashMap<>();
	
	
	public FHNConnector getConnector(VMProvider vmp){
		
		String vmpId = vmp.getId();
		
		if(!connectors.containsKey(vmpId)){
			connectors.put(vmpId, this.createConnector(vmp));
		}
		return connectors.get(vmpId);
	}
	
		
	/*
	public void setProxy(){
		Props a = new Props();
		VomsProxy b = new VomsProxy();
		File proxy = new File(a.getProxy());
		if (!proxy.exists()) {
			try {
				b.setProxy();
			} catch (IOException | InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
*/
	
	
	public FHNConnector createConnector(VMProvider vmp){
		
		//this.setProxy();
		try {
			vmp.getCredentials().setEncodedCredentails(ScriptUtil.getScriptFromFile(new File(vmp.getCredentials().getEncodedCredentails())));
			//X509CredentialManager.createProxy(vmp.getCredentials().getEncodedCredentails(), ""); // 2nd
            																				       // level
            																					   // prox
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		FHNConnector connector = null;
		try {
			connector = new OcciConnector(vmp);
			connector.setTrustStore("/etc/grid-security/certificates");
		} catch (UnsupportedCredentialsTypeException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return connector;
	}
	
}
