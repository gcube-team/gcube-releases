package org.gcube.common.homelibrary.jcr.workspace.util;

import java.util.Arrays;
import java.util.List;

import org.gcube.common.scope.impl.ScopeBean;
import org.gcube.common.scope.impl.ScopeBean.Type;

public class Utils {

	public static String getRootScope(String scope){
		ScopeBean tmpScope = new ScopeBean(scope);
		while (!tmpScope.is(Type.INFRASTRUCTURE))
			tmpScope = tmpScope.enclosingScope();
		return tmpScope.toString();
	}

	/**
	 * Get Group name by scope
	 * @param scope
	 * @return
	 */
	public static String getGroupByScope(String scope) {
		String VREFolder;
		if (scope.startsWith("/"))			
			VREFolder = scope.replace("/", "-").substring(1);
		else
			VREFolder = scope.replace("/", "-");
		return VREFolder;

	}


	public static String commonPath(String path, String destpath){
		String[] pathsplit = path.split("/");
		String[] destpathsplit = destpath.split("/");
		StringBuilder mystring = new StringBuilder();
		for (int i=0; i<pathsplit.length -1; i++){
		  String token = pathsplit[i];
		  if (token.equals(""))
			  continue;
				if (destpathsplit[i].equals(token))
					mystring.append("/"+token);
				else
					return "";
			}
		return mystring.toString();

		   



		//        for(int j = 0; j < folders[0].length; j++){
		//            String thisFolder = folders[0][j]; //grab the next folder name in the first path
		//            boolean allMatched = true; //assume all have matched in case there are no more paths
		//            for(int i = 1; i < folders.length && allMatched; i++){ //look at the other paths
		//                if(folders[i].length < j){ //if there is no folder here
		//                    allMatched = false; //no match
		//                    break; //stop looking because we've gone as far as we can
		//                }
		//                //otherwise
		//                allMatched &= folders[i][j].equals(thisFolder); //check if it matched
		//            }
		//            if(allMatched){ //if they all matched this folder name
		//                commonPath += thisFolder + "/"; //add it to the answer
		//            }else{//otherwise
		//                break;//stop looking
		//            }
		//        }
//		return commonPath;
	}


}
