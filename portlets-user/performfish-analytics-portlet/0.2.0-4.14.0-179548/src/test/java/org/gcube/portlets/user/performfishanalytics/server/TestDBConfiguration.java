/**
 *
 */
package org.gcube.portlets.user.performfishanalytics.server;

import java.io.File;

import org.gcube.portlets.user.performfishanalytics.server.database.EntityManagerFactoryCreator;
import org.gcube.portlets.user.performfishanalytics.server.util.FileUtil;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * Feb 4, 2019
 */
public class TestDBConfiguration {

	public static void main(String[] args) throws Exception {
		String path = EntityManagerFactoryCreator.getPersistenceFolderPath();

		String connectionURL = EntityManagerFactoryCreator.getJDBCConnectionUrl(true);
		//String path = /home/francesco-mangiacrapa/Portal-Bundle-3.0.0-3.2.0/tomcat-6.0.29/persistence/PerformFISH"
		System.out.println(path);
		System.out.println(connectionURL);

		FileUtil.deleteDirectoryRecursion(new File(path).toPath());

	}
}
