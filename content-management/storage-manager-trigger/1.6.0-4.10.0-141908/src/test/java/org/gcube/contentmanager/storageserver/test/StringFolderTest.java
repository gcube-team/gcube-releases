package org.gcube.contentmanager.storageserver.test;

import static org.junit.Assert.*;

import org.junit.Test;

public class StringFolderTest {
	
	String currentFolder="a/b/c/d";
	

	@Test
	public void test() {
		System.out.println("folder start: "+currentFolder);
		while(currentFolder!= null && currentFolder.contains("/")){
			currentFolder=currentFolder.substring(0, currentFolder.lastIndexOf("/"));
			System.out.println("dir parent: "+currentFolder);
		}
		System.out.println("folder end: "+currentFolder);
	}
	

}
