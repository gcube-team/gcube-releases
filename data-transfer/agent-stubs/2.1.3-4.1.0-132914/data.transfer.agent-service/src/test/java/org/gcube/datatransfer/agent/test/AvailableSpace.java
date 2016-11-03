package org.gcube.datatransfer.agent.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileSystemUtils;
import org.junit.Test;

public class AvailableSpace {
		
	@Test
	public void process() throws IOException{
		File localFile=new File("/");
		System.out.println("FileSystemUtils.freeSpaceKb(\"/\")="+FileSystemUtils.freeSpaceKb("/")+"\n"+
				"getTotalSpace="+localFile.getTotalSpace()+"\n"+
				"getFreeSpace="+localFile.getFreeSpace()+"\n"+
				"getUsableSpace="+localFile.getUsableSpace());		
	}

}
