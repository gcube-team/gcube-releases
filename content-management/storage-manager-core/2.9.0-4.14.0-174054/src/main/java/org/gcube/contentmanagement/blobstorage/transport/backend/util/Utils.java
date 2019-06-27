/**
 * 
 */
package org.gcube.contentmanagement.blobstorage.transport.backend.util;

import java.util.Iterator;
import java.util.Map;
import java.util.TreeSet;

/**
 * @author Roberto Cirillo (ISTI-CNR) 2018
 *
 */
public class Utils {
	
	public static String checkVarEnv(String name){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        Iterator<String> iter = keys.iterator();
        String value=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase(name)){
            	value=env.get(key);
            	break;
            }
        }
        return value;
	}

	public static boolean isVarEnv(String name){
		Map<String, String> env = System.getenv();
        TreeSet<String> keys = new TreeSet<String>(env.keySet());
        Iterator<String> iter = keys.iterator();
        String value=null;
        while(iter.hasNext())
        {
            String key = iter.next();
            if(key.equalsIgnoreCase(name)){
            	value=env.get(key);
            	return true;
            }
        }
        return false;
	}

}
