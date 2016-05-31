package org.gcube.resources.federation.fhnmanager.utils;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class VomsProxy {	
	public void setProxy() throws IOException, InterruptedException {
		 Props a = new Props();
		 String command = "voms-proxy-init -voms " + a.getHost()+" --rfc --dont_verify_ac";
		 String pwd = a.getPwd();
		 Process p = Runtime.getRuntime().exec(command);
		 BufferedWriter bw = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
		 bw.write(pwd+"\n"); 
		 bw.flush();
		 p.waitFor();
	}


}



