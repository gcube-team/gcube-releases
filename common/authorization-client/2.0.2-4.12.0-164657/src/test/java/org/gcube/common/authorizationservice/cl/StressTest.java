package org.gcube.common.authorizationservice.cl;

import static org.gcube.common.authorization.client.Constants.authorizationService;

import java.io.IOException;
import java.util.ArrayList;

import org.gcube.common.authorization.library.provider.UserInfo;
import org.junit.Test;

public class StressTest {

	
	@Test
	public void stressing(){
		int counter = 0;
		for (int i =1 ; i<=10000; i++){
			if ((i-(counter*4))>4)
				counter++;
			final int index = counter;
			Thread t = new Thread(){
				
				public void run(){
					try {
						requestTestToken("/gcube", "stress.test19-"+index);
					} catch (Exception e) {
						System.out.println("erorr in thread "+Thread.currentThread().getName());
						e.printStackTrace();
					}
				}
				
			};
			t.start();
			System.out.println("next execution");
		}
		System.out.println("waiting");
		try {
			System.in.read();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private String requestTestToken(String context, String user) throws Exception{
		return authorizationService().generateUserToken(new UserInfo(user, new ArrayList<String>()), context);
	}
}
