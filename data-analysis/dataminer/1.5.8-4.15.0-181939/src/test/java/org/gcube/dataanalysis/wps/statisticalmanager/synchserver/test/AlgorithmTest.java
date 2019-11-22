package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.junit.Test;


public class AlgorithmTest {

	List<String> executeOnly = Arrays.asList("#BIONYM", "#AQUAMAPS_SUITABLE", "#AQUAMAPS_SUITABLE 21 sp", "#BIONYM1024","#CMSY2");
			
	@Test
	public void executeAlgorithmsFromFile() throws Exception{
		
		String env = "dev";
		
		/*Properties prop = new Properties();
		prop.load(AlgorithmTest.class.getResourceAsStream("/test_params.properties"));
			*/
		
		String protocol = "http";
		String hostname = "dataminer-genericworkers.d4science.org";
		String token = "257800d8-24bf-4bae-83cd-ea99369e7dd6-843339462";
		String layerID = "08ee0d70-4d8b-4f42-8b06-d709482bca95";
						 
		Iterator<String> uris =  getUrisIterator();
		
		HttpClient client = new HttpClient();
		String algorithmName="NOTHING";
		while (uris.hasNext()){
			String nextLine = uris.next();
			
			if (nextLine.startsWith("#"))
				algorithmName = nextLine;
			else{
				if (executeOnly.contains(algorithmName)) continue;
				String callUrl = nextLine.replace("{PROTOCOL}", protocol).replace("{HOST}", hostname).replace("{TOKEN}", token).replace("{LAYERID}", layerID);
				try{
					long start = System.currentTimeMillis();
					URL url = new URL(callUrl);
					URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
					GetMethod get = new GetMethod(uri.toString());
					client.executeMethod(get);
					if(get.getStatusCode()!=200)
						System.out.println("algorithm "+algorithmName+" returned status "+get.getStatusCode()+" with url "+uri);
					else System.out.println("algorithm "+algorithmName+" worked in "+(System.currentTimeMillis()-start)+" millis");
				} catch(Exception e){
					System.out.println("invalid url");
				}
			}
		}
	}

	
	
	private Iterator<String> getUrisIterator() throws Exception{
		
		return new Iterator<String>(){
				
			private String line= null;
			private BufferedReader buffer=new BufferedReader(new InputStreamReader(this.getClass().getResourceAsStream("/AlgorithmTestURIs.txt")));
			
			@Override
			public boolean hasNext() {
				try {
					line = buffer.readLine();
				} catch (IOException e) {
					System.out.println("error reading buffer");
				}
				if(line==null){
					try {
						if (buffer!=null)
							buffer.close();
					} catch (IOException e) {
						System.out.println("error closing buffer");
					}
					return false;
				} else return true;
			}

			@Override
			public String next() {
				return line;
			}
			
		};
		
	}
	
}
