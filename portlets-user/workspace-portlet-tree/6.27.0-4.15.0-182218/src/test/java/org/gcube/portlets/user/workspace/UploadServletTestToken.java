////TODO IT MUST BE MOVED TO SHUB
///**
// *
// */
//package org.gcube.portlets.user.workspace;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.io.OutputStream;
//
//import org.gcube.common.homelibrary.home.HomeLibrary;
//import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
//import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
//import org.gcube.common.homelibrary.home.exceptions.UserNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.Workspace;
//import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
//import org.gcube.common.homelibrary.home.workspace.exceptions.InsufficientPrivilegesException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
//import org.gcube.common.homelibrary.home.workspace.exceptions.WrongDestinationException;
//import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
//import org.gcube.common.homelibrary.util.WorkspaceUtil;
//import org.gcube.common.scope.api.ScopeProvider;
//import org.gcube.common.scope.impl.ScopeBean;
//
///**
// * @author Francesco Mangiacrapa francesco.mangiacrapa{@literal @}isti.cnr.it
// * Jun 20, 2013
// *
// */
//public class UploadServletTestToken {
//
//	public static void main(String[] args) throws InternalErrorException,
//			WorkspaceFolderNotFoundException, HomeNotFoundException,
//			UserNotFoundException, InsufficientPrivilegesException,
//			ItemAlreadyExistException, WrongDestinationException, IOException {
//
//		ScopeBean scope = new ScopeBean("/gcube/devsec");
//		ScopeProvider.instance.set(scope.toString());
//
//		Workspace workspace = HomeLibrary.getHomeManagerFactory()
//				.getHomeManager().getHome("francesco.mangiacrapa")
//				.getWorkspace();
//
//		String filePath = "/home/francesco-mangiacrapa";
//
//		String fileName = "Geo Explorer 2016-01-15 18-51-51.png";
//
//		String fullPath = filePath + "/" + fileName;
//
//		// String newFilePath =
//		// "/home/francesco-mangiacrapa/Desktop/icongeoexplorer/testupload.txt";
//
//		// String name = "TestReplaceContentImage";
//
//		// ExternalPDFFile file = workspace.createExternalPDFFile(name, "",
//		// null, new FileInputStream(new File(filePath)),
//		// workspace.getRoot().getId());
//
//		WorkspaceFolder root = workspace.getRoot();
//		// ExternalImage file = (ExternalImage) root.find(name);
//
//		// UPLOAD FILE
////		String contentType = MimeTypeUtil.getMimeType(fileName, new BufferedInputStream(
////				new FileInputStream(new File(fullPath))));
//
//		String itemName = WorkspaceUtil.getUniqueName(fileName, root);
//		System.out.println("Storing data....");
//		System.out.println("content type " + null);
//		System.out.println("itemName " + itemName);
//
//		ExternalFile file = (ExternalFile) WorkspaceUtil.createExternalFile(
//				root, itemName, "", null, new FileInputStream(new File(
//						fullPath)));
//
//		System.out.println("Storing data - OK" + "File [id: " + file.getId()
//				+ ", name: " + file.getName() + "]");
//		//
//		// System.out.println("File mimeType" + file.getMimeType());
//		// System.out.println("File size" + file.getLength());
//		//
//
//		//
//		// file.setData(new FileInputStream(new File(newFilePath)));
//		// System.out.println("New File mimeType" + file.getMimeType());
//		// System.out.println("New File size" + file.getLength());
//
//		//
//
//		System.out.println("Recovering data");
//		InputStream inputStream = file.getData();
//		// write the inputStream to a FileOutputStream
//		OutputStream out = new FileOutputStream(new File(
//				"/home/francesco-mangiacrapa/Desktop/download/"+fileName));
//
//		int read = 0;
//		byte[] bytes = new byte[1024];
//
//		while ((read = inputStream.read(bytes)) != -1) {
//			out.write(bytes, 0, read);
//		}
//
//		inputStream.close();
//		out.flush();
//		out.close();
//
//		System.out.println("Recovering data - OK");
//
//		// File file = new File("/home/gioia/Desktop/test");
//		// InputStream is = new FileInputStream(file);
//
//	}
//
//}
