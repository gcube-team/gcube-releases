package org.gcube.datatransfer.agent.test;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStr2 {

	
	public static void main(String[] args){
		List<URI> list=new ArrayList<URI>();
		try {
			list.add(new URI("http://www.testhostname1.com"));
			list.add(new URI("http://www.testhostname2.com"));
			list.add(new URI("http://www.testhostname3.com"));
		} catch (URISyntaxException e) {
			e.printStackTrace();
		}	

		String[] arrayUris = new String[list.size()];
		for(int i=0;i<arrayUris.length;i++){
			arrayUris[i]=list.get(i).toString();
		}
		
		System.out.println("arrayUris.length="+arrayUris.length);
		for(String tmp:arrayUris)System.out.println(tmp);
	}
}
