package gr.cite.bluebridge.analytics.utils;

import com.sun.jersey.api.client.Client;


public class SingletonHttpClient {

	private final Client client = Client.create();
	
	private static SingletonHttpClient singletonHttpClient = new SingletonHttpClient();
		
	private SingletonHttpClient(){}
	
	public static SingletonHttpClient getSingletonHttpClient(){
		return singletonHttpClient;
	}
	
	public Client getClient(){
		return client;
	}
	
}
