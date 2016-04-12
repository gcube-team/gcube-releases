package org.gcube.datatransfer.portlets.user.test;

import java.util.ArrayList;

import org.gcube.datatransfer.portlets.user.server.workers.ConnectionSMP;
import org.gcube.datatransfer.portlets.user.shared.obj.FolderDto;

public class TestConnectionSMP {
	private static String serviceClass="data-transfer";
	private static String serviceName="scheduler-portlet";
	private static String owner="testing";
	private static String accessType="SHARED";
	private static String scope="/gcube/devsec";
	
		
	private static String path="/temporary/";

	public static void main(String[] args) {	
		ConnectionSMP connection = new ConnectionSMP(serviceClass,serviceName,owner,accessType,scope,path);
		
		//FolderDto folder=null;
		//folder = connection.browse();
		//connection.printFolder(folder, 0);
		
		String path="desiredFolder1/nick/";
		connection.storeNewFolder(path);
	}

}
