package org.apache.jackrabbit.j2ee.rest;

import java.io.File;
import java.net.URL;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.gcube.common.authorization.library.provider.SecurityTokenProvider;
import org.junit.Ignore;
import org.junit.Test;

import ch.qos.logback.classic.pattern.Util;

public class RestTest {

	private static final String ROOT_PATH = "/Home/valentina.marioli/Workspace/";
	private static final String PIPPO_IMAGE = "pippo.jpg";
	private static final String PLUTO_IMAGE = "pluto.jpg";

	@Ignore
	public void createFolder() throws Exception {
		SecurityTokenProvider.instance.set("97803466-76ff-4cfe-9acc-9d0dbafc3a76-98187548");


		String folderName = "MyFolder-" + UUID.randomUUID();
		System.out.println("CREATE FOLDER " +  folderName);
		String folder = UtilTest.createFolder(folderName, "Desc", ROOT_PATH);
		System.out.println(folder);
		System.out.println("\n");

		URL imageURL_pippo = UploadTest.class.getClassLoader().getResource(PIPPO_IMAGE);
		File file_pippo = new File(imageURL_pippo.getFile());

		String path_pippo = UtilTest.uploadFile(file_pippo, "pippo.jpg", "desc" , folder);
		System.out.println("CREATED FILE "+ path_pippo);

		System.out.println("\n");
		URL imageURL_pluoto = UploadTest.class.getClassLoader().getResource(PLUTO_IMAGE);
		File file_pluto = new File(imageURL_pluoto.getFile());
		String path_pluto = UtilTest.uploadFile(file_pluto, "pluto.jpg", "desc" , folder);
		System.out.println("CREATED FILE "+ path_pluto);

		//list
		System.out.println("\n");
		System.out.println("LIST FOLDER " +  folder);
		Map<String, Boolean> list = UtilTest.listFolder(folder);
		Set<String> keys = list.keySet();
		for (String key: keys){
			System.out.println("* " + key + " - is folder? " + list.get(key));
			//			UtilTest.delete(folder + "/" + key);
		}
		System.out.println("\n");
		//download file
		System.out.println("DOWNLOAD FILE " +  path_pippo);
		UtilTest.download(path_pippo);
		System.out.println("\n");
		System.out.println("DOWNLOAD FILE "+  path_pluto);
		UtilTest.download(path_pluto);
		System.out.println("\n");
		//download folder
		UtilTest.download(folder);
		System.out.println("\n");
		//list
		//		System.out.println("LIST FOLDER " +  folder);
		//		Map<String, Boolean> list1 = UtilTest.listFolder(folder);
		//		Set<String> keys1 = list1.keySet();
		//		for (String key: keys1){
		//			System.out.println("* " + key + " - is folder? " + list1.get(key));
		//			System.out.println("\n");
		//			System.out.println("DELETE FILE " +  folder + "/" + key);
		//			UtilTest.delete(folder + "/" + key);
		//			System.out.println("\n");
		//		}
		System.out.println("DELETE FOLDER " +  folder);
		System.out.println("\n");
		UtilTest.delete(folder);


		//list root
		System.out.println("LIST ROOT FOLDER " +  ROOT_PATH);
		Map<String, Boolean> list11 = UtilTest.listFolder(ROOT_PATH);
		Set<String> keys11 = list11.keySet();
		for (String key: keys11){
			System.out.println("* " + key + " - is folder? " + list11.get(key));
			//					UtilTest.delete(folder + "/" + key);
		}


	}

	@Ignore
	public void TestGP() throws Exception {
		//token gp
		SecurityTokenProvider.instance.set("f9d49d76-cd60-48ed-9f8e-036bcc1fc045-98187548");
		String path = "/Home/gianpaolo.coro/Workspace/MySpecialFolders/NextNext/layout_set_logo-4 (copy).png";
		
		System.out.println("DOWNLOAD FILE " +  path);
//		UtilTest.download(path);
		
		UtilTest.delete(path);
//		String isShortUrl = "false";
//		String link = UtilTest.getPublicLink(path, isShortUrl);
//		System.out.println(link);

	}


}
