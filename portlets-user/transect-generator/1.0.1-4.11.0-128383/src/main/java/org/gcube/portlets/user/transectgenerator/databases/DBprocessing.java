package org.gcube.portlets.user.transectgenerator.databases;

import java.util.List;

public class DBprocessing {

	
	public static String buildWhereQuery(List<String> wherecodes,String whereQuery){
		
		int max = wherecodes.size();
		StringBuffer sb = new StringBuffer();
		
		for (int i=0;i<max;i++){
			String where = String.format(whereQuery,wherecodes.get(i));
			sb.append(where);
			if (i<max-1)
				sb.append(" or ");
		}
		return sb.toString();
	}
	
}
