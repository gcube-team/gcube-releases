package org.gcube.data.analysis.statisticalmanager.util;

import org.gcube.data.analysis.statisticalmanager.SMResourceType;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMError;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMFile;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMObject;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMResource;
import org.gcube_system.namespaces.data.analysis.statisticalmanager.types.SMTable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ObjectFormatter {

	private static Logger logger = LoggerFactory.getLogger(ObjectFormatter.class);
	//	String.format("%s , ID : %s , Name : %s ",type,resource.getResourceId(),resource.getName(),resource.getDescription());

	/**
	 * Utility to log generated classes
	 * 
	 * @param res
	 * @return
	 */

	public static final String log(SMResource res,boolean full){
		if(res!=null) {
			String user = res.getPortalLogin();
			SMResourceType type=SMResourceType.values()[res.getResourceType()];
			String shortLog=String.format("%s , ID : %s , Name : %s , Owner : %s ",type,res.getResourceId(),res.getName(),user);

			if(full){
				StringBuilder toReturn=new StringBuilder(shortLog);
				// all common info
				try{
				toReturn.append(String.format(", ALG : %s , Created: %s, Desc : %s, OPID : %s, Provenance : %s ",
						res.getAlgorithm(),
						ServiceUtil.format(res.getCreationDate()),
						res.getDescription(),
						res.getOperationId(),
						res.getProvenance()));
					switch(type){
					case ERROR : 
						SMError err=(SMError) res;
						toReturn.append("ERR MSG : "+err.getMessage());
						break;
					case FILE : 
						SMFile file=(SMFile) res;
						toReturn.append(String.format("Mime : %s, RemoteName : %s, Url %s",file.getMimeType(),file.getRemoteName(),file.getUrl()));
						break;
					case OBJECT :
						SMObject obj=(SMObject) res;
						toReturn.append("Url : "+obj.getUrl());
						break;
					case TABULAR : 
						SMTable table=(SMTable) res;
						toReturn.append("Template : "+table.getTemplate());
					}
				}catch(Throwable t){
					logger.warn("Unable to fully format resource "+shortLog,t);
				}
				return toReturn.toString();
			}else return shortLog;
		}else return "NULL";
	}

}
