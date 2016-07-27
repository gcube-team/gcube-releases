package org.gcube.data.transfer.service.transfers.engine.impl;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Scanner;

import javax.inject.Singleton;

import lombok.extern.slf4j.Slf4j;

import org.gcube.data.transfer.model.TransferCapabilities;
import org.gcube.data.transfer.model.options.HttpDownloadOptions;
import org.gcube.data.transfer.model.options.TransferOptions;
import org.gcube.data.transfer.service.transfers.engine.CapabilitiesProvider;

@Singleton
@Slf4j
public class CapabilitiesProviderImpl implements CapabilitiesProvider {

	
	private TransferCapabilities capabilities=null;
	
	public CapabilitiesProviderImpl() {
		String hostName="N/A";
		try{
			hostName=getHostname();
		}catch(Exception e){
			log.warn("Unable to detect hostname",e);
		}
		HashSet<TransferOptions> meansOfTransfer=new HashSet<TransferOptions>();
		meansOfTransfer.add(HttpDownloadOptions.DEFAULT);
		capabilities=new TransferCapabilities(hostName, meansOfTransfer);
	}	
	
	
	@Override
	public TransferCapabilities get() {
		return capabilities;
	}

	
	
	private static String getHostname() throws Exception {
        String OS = System.getProperty("os.name").toLowerCase();
        log.debug("Getting hostname..");
        String hostName=null;
        if (OS.indexOf("win") >= 0) {
        	log.debug("Detected windows..");        	
            hostName=System.getenv("COMPUTERNAME");
            if(hostName==null || hostName.equals("")){
            	log.debug("System env not found, trying via hostname command..");
            	hostName=execReadToString("hostname");
            }
        } else 
            if (OS.indexOf("nix") >= 0 || OS.indexOf("nux") >= 0) {
            	log.debug("Detected linux..");
                hostName= System.getenv("HOSTNAME");
                if(hostName==null || hostName.equals("")){
                	log.debug("System env not found, trying via hostname command..");
                	hostName=execReadToString("hostname -f");
                }
                if(hostName==null || hostName.equals("")){
                	log.debug("Hostname command didn't work, trying via hostname file..");
                	hostName=execReadToString("cat /etc/hostname");
                }                
            }else throw new Exception("OS not detected");
        return hostName;
    }

    public static String execReadToString(String execCommand) throws IOException {
        Process proc = Runtime.getRuntime().exec(execCommand);
        try (InputStream stream = proc.getInputStream()) {
            try (Scanner s = new Scanner(stream).useDelimiter("\\A")) {
                return s.hasNext() ? s.next() : "";
            }
        }
    }
	
}
