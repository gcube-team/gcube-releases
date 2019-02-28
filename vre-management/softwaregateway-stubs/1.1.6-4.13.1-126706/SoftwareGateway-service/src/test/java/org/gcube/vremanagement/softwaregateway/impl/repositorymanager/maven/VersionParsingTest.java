package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import static org.junit.Assert.*;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.Before;

public class VersionParsingTest {
	
	private String version; 
	
	@Before
	public void init(){
		version="3.0.0.RELEASE";
	}
	
	
	@Test
	public void test() {
//		assertTrue(version.matches("^(\\*|\\d+(\\.\\d+){0,2}(\\.\\*)?)$"));
		assertTrue(version.matches("^(\\*|\\d+(\\.\\d+){0,2}(\\.\\*)?)(\\.(\\S+)){0,1}$"));
	}
	
//	@Test
	public void test1() {
		assertTrue(version.matches("(\\d+[^.]*)\\.(\\S+?)-(\\d+[^.]*)\\.(\\S+)"));
	}

}
