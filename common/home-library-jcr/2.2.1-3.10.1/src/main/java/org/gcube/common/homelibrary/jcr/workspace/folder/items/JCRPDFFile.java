package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

import javax.jcr.PathNotFoundException;
import javax.jcr.ValueFormatException;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRServlets;
import org.gcube.common.homelibrary.jcr.workspace.util.WorkspaceItemUtil;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;

public class JCRPDFFile extends JCRFile {
	
	
	public JCRPDFFile(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException   {
		super(workspace, delegate);
	}
	

	public JCRPDFFile(JCRWorkspace workspace, ItemDelegate delegate, String mimeType, File tmpFile) throws RepositoryException,
	IOException, RemoteBackendException{
		super(workspace, delegate, mimeType, tmpFile);
		
		try {
			setProperties(delegate, tmpFile);
		} catch (ValueFormatException | PathNotFoundException e) {
			throw new RepositoryException(e.getMessage());
		}
	}
	
	
	public JCRPDFFile(JCRWorkspace workspace,
			ItemDelegate itemDelegate, String mimeType, File tmpFile, String path) throws RepositoryException,
	IOException, RemoteBackendException{
		super(workspace, itemDelegate, mimeType, tmpFile, path);
		
		try {
			setProperties(itemDelegate, tmpFile);
		} catch (ValueFormatException | PathNotFoundException e) {
			throw new RepositoryException(e.getMessage());
		}
	}


	private void setProperties(ItemDelegate itemDelegate, File tmp) throws ValueFormatException, PathNotFoundException, RepositoryException, IOException {
		Map<NodeProperty, String> content = itemDelegate.getContent();
		
		InputStream dataPdf = new FileInputStream(tmp);
		
		Map<String,String> infoPDF = WorkspaceItemUtil.getPDFInfo(dataPdf);
		int numberOfPages = Integer.parseInt(infoPDF.get(WorkspaceItemUtil.NUMBER_OF_PAGES));
		String version = infoPDF.get(WorkspaceItemUtil.VERSION);
		String author = infoPDF.get(WorkspaceItemUtil.AUTHOR);
		String title = infoPDF.get(WorkspaceItemUtil.TITLE);
		String producer = infoPDF.get(WorkspaceItemUtil.PRODUCER);
		
		content.put(NodeProperty.NUMBER_OF_PAGES, new XStream().toXML(numberOfPages));
		content.put(NodeProperty.VERSION, new XStream().toXML(version));
		content.put(NodeProperty.AUTHOR,  new XStream().toXML(author));
		content.put(NodeProperty.PDF_TITLE, new XStream().toXML(title));
		content.put(NodeProperty.PRODUCER, new XStream().toXML(producer));
				
		dataPdf.close();
		
	}


	public FolderItemType getFolderItemType() {
		return FolderItemType.EXTERNAL_PDF_FILE;	
	}

	public int getNumberOfPages() {	
		int numberOfPages = 0;
		try {
			numberOfPages = (int) new XStream().fromXML(itemDelegate.getContent().get(NodeProperty.NUMBER_OF_PAGES));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return numberOfPages; 	
	}

	public String getVersion() {
		
		String version = null;
		try {
			version = itemDelegate.getContent().get(NodeProperty.VERSION);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return version; 
	}

	public String getAuthor() {
		
		String author = null;
		try {
			author = itemDelegate.getContent().get(NodeProperty.AUTHOR);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return author; 
		
	}

	public String getTitle() {
		
		String title = null;
		try {
			title = itemDelegate.getContent().get(NodeProperty.PDF_TITLE);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return title; 
		
	}

	public String getProducer() {
		
		String title = null;
		try {
			title = itemDelegate.getContent().get(NodeProperty.PRODUCER);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return title; 	
	}
	

	@Override
	public void updateInfo(JCRServlets servlets, java.io.File tmp) throws InternalErrorException {
		super.updateInfo(servlets, tmp);
		
		try {
			setProperties(itemDelegate, tmp);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
				
	}

}
