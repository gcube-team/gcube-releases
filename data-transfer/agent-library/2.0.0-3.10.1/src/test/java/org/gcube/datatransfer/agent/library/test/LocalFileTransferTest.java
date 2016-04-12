package org.gcube.datatransfer.agent.library.test;

import java.io.File;
import java.util.ArrayList;

import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.datatransfer.agent.library.AgentLibrary;
import org.gcube.datatransfer.common.outcome.FileTransferOutcome;
import org.junit.Test;

import static org.gcube.datatransfer.agent.library.proxies.Proxies.*;

public class LocalFileTransferTest {

	@Test
	public void testLibrary(){
		AgentLibrary library = null;
		ScopeProvider.instance.set("/gcube/devsec");
		try {
			 library =  transferAgent().at("pcitgt1012.cern.ch", 8080).build();
		
		File file = new File ("/Users/andrea/Downloads/selFAO.csv");
		ArrayList<File> files = new ArrayList<File>();
		files.add(file);
		String outUri ="tmp";
		ArrayList<FileTransferOutcome> outcomes = library.copyLocalFiles(files, outUri,true,false);
		for (FileTransferOutcome outcome : outcomes){
			System.out.println("Exception: "+outcome.getException());
			System.out.println("FileName: "+ outcome.getFilename());
			System.out.println("Success?: "+ outcome.isSuccess());
			System.out.println("Failure?: "+ outcome.isFailure());
			System.out.println("TransferTime"+ outcome.getTransferTime());
		}

		} catch (Exception e) {
			
			e.printStackTrace();
		}
	}
}
