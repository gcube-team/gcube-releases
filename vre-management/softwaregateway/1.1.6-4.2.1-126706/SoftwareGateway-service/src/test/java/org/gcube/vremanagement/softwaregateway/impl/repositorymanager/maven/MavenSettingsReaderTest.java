/**
 * 
 */
package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import static org.junit.Assert.*;

import java.io.InputStreamReader;

import org.apache.maven.settings.Settings;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenSettingsReader;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenSettingsReader.InvalidMavenSettings;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * @author manuele
 *
 */
public class MavenSettingsReaderTest {

	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {}

	/**
	 * Test method for {@link org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenSettingsReader#getSettings(java.io.Reader)}.
	 */
//	@Test
//	public final void testGetSettings() {
//		InputStreamReader input = new InputStreamReader(MavenSettingsReaderTest.class.getResourceAsStream("settings.xml"));
//		try {
//			Settings settings = MavenSettingsReader.getSettings(input);
//			for (String profile : settings.getActiveProfiles())
//				System.out.println("ACTIVE PROFILE: " + profile);
//			for (org.apache.maven.settings.Profile profile : settings.getProfiles()) {
//				for (org.apache.maven.settings.Repository rep : profile.getRepositories()) {
//					System.out.println("ACTIVE REPOSITORY Name: " + rep.getName());
//					System.out.println("ACTIVE REPOSITORY URL: " + rep.getUrl());
//				}
//			}
//		} catch (InvalidMavenSettings e) {
//			e.printStackTrace();
//			fail("Failed to load settings.xml");
//		}
//	
//	}

}
