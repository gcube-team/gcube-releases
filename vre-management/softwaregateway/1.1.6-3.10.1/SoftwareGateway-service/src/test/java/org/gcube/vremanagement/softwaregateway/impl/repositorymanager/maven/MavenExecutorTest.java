/**
 * 
 */
package org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven;

import static org.junit.Assert.*;

import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenConfiguration;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenExecutor;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenRequestBuilder;
import org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenSettingsReader;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 * Tested for {@link MavenExecutor}
 * 
 * @author Manuele Simi (CNR)
 * 
 */
public class MavenExecutorTest {

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * Test method for
	 * {@link org.gcube.vremanagement.softwaregateway.impl.repositorymanager.maven.MavenExecutor#exec(org.apache.maven.execution.MavenExecutionRequest, org.apache.maven.settings.Settings)}
	 * .
	 */
	@Test
	public final void testExec() {
		// prepare the properties
		String[][] properties = new String[1][2];
		properties[0][0] = "outputFile";
		properties[0][1] = new File(System.getProperty("java.io.tmpdir"),
				"resultTree.txt").getAbsolutePath();
		System.out.println("Output will be stored in " + properties[0][1]);
		// prepare the goal
		String goal = "dependency:tree";
//		String url = " http://maven.research-infrastructures.eu/nexus/service/local/repositories/gcube-snapshots/content/org/gcube/resourcemanagement/softwaregateway-service/1.1.0-SNAPSHOT/softwaregateway-service-1.1.0-20120703.155259-1.pom";
//		String url = "http://maven.research-infrastructures.eu/nexus/service/local/repositories/gcube-snapshots/content/org/gcube/resourcemanagement/resource-manager-stubs/2.0.0-SNAPSHOT/resource-manager-stubs-2.0.0-20120618.184811-1.pom";
//		String url= "http://maven.research-infrastructures.eu/nexus/service/local/repositories/gcube-snapshots/content/org/gcube/resourcemanagement/resource-manager-stubs/2.0.0-SNAPSHOT/resource-manager-stubs-2.0.0-20121017.015418-217.pom";
		String url="http://maven.research-infrastructures.eu/nexus/service/local/repositories/gcube-snapshots/content/org/gcube/resourcemanagement/resource-manager-stubs/2.0.1-SNAPSHOT/resource-manager-stubs-2.0.1-20130116.145719-59.pom";
		// prepare the POM file
		File pom = new File(System.getProperty("java.io.tmpdir"), "pom.xml");
		BufferedWriter out;
		try {
			out = new BufferedWriter(new FileWriter(pom));
			out.write(getPomContentFromURL(new URL(url)));
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
			fail("can't prepare the POM file");
		}

		// prepare the full request
		MavenRequestBuilder builder = new MavenRequestBuilder();
		builder.setProjectFolder(new File(System.getProperty("java.io.tmpdir")))
				.setPom(pom).setUserProperties(properties).execThisGoal(goal)
				.useThisRepository(MavenConfiguration.getLocalRepository());

		// prepare the settings
		InputStreamReader input = new InputStreamReader(MavenSettingsReaderTest.class.getResourceAsStream("settings.xml"));

		// build & send the request
		try {
			MavenExecutor.exec(builder.build(), MavenSettingsReader.getSettings(input));
			printDependencyTree(new File(properties[0][1]));
		} catch (Throwable e) {
			e.printStackTrace();
			fail("sorry, I failed to exec the goal");
		} finally {
			pom.delete();
		}
	}

	/**
	 * Gets the POM from the given URL
	 * @param url the pom's URL
	 * @return the pom as string
	 * @throws IOException
	 */
	private static String getPomContentFromURL(URL url) throws IOException {
		URLConnection connection = url.openConnection();
		InputStream in = connection.getInputStream();
		int contentLength = connection.getContentLength();
		ByteArrayOutputStream tmpOut;
		if (contentLength != -1) {
			tmpOut = new ByteArrayOutputStream(contentLength);
		} else {
			tmpOut = new ByteArrayOutputStream(16384); 
		}

		byte[] buf = new byte[512];
		while (true) {
			int len = in.read(buf);
			if (len == -1) {
				break;
			}
			tmpOut.write(buf, 0, len);
		}
		in.close();
		tmpOut.close(); 
		return tmpOut.toString();
	}

	/**
	 * Prints the dependency tree
	 * @param sourceFile the serialized dependency tree
	 */
	private static void printDependencyTree(File sourceFile) {
		FileReader fr = null;
		System.out.println("DEPENDECY TREE:");
		try {
			fr = new FileReader(sourceFile);
			int inChar;

			while ((inChar = fr.read()) != -1) {
				System.out.printf("%c", inChar);
			}
		} catch (IOException e) {
			e.printStackTrace();
			System.err.println("Failure while reading:"
					+ sourceFile.getAbsolutePath());
			e.printStackTrace();
		} finally {
			try {
				if (fr != null) {
					fr.close();
				}
			} catch (IOException e) {
				System.err.printf("Error closing file reader: %s\n",
						e.getMessage());
				e.printStackTrace();
			}
		}

	}
}
