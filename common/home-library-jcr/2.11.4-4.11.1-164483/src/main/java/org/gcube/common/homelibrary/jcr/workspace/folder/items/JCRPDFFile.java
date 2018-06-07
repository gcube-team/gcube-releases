package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.io.InputStream;

import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.jcr.workspace.servlet.JCRSession;
import org.gcube.common.homelibrary.jcr.workspace.util.MetaInfo;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;

public class JCRPDFFile extends JCRFile {
	
	
	public JCRPDFFile(JCRWorkspace workspace, ItemDelegate delegate) throws RepositoryException   {
		super(workspace, delegate);
	}
	

	public JCRPDFFile(JCRWorkspace workspace, ItemDelegate delegate, InputStream is) throws InternalErrorException, RemoteBackendException, IOException{
		super(workspace, delegate, is);
//		setProperties(delegate, super.info);
	}
	

	public JCRPDFFile(JCRWorkspace workspace, ItemDelegate itemDelegate, MetaInfo info) throws InternalErrorException{
		super(workspace, itemDelegate, info);
//		setProperties(itemDelegate, info);	
	}

//	private void setProperties(ItemDelegate itemDelegate, MetaInfo info) throws InternalErrorException {
//		Map<NodeProperty, String> content = itemDelegate.getContent();
//
//		content.put(NodeProperty.NUMBER_OF_PAGES, new XStream().toXML(info.getPdfMeta().getNumberOfPages()));
//		content.put(NodeProperty.VERSION, new XStream().toXML(info.getPdfMeta().getVersion()));
//		content.put(NodeProperty.AUTHOR,  new XStream().toXML(info.getPdfMeta().getAuthor()));
//		content.put(NodeProperty.PDF_TITLE, new XStream().toXML(info.getPdfMeta().getTitle()));
//		content.put(NodeProperty.PRODUCER, new XStream().toXML(info.getPdfMeta().getProducer()));
//	}	

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
	public void updateInfo(JCRSession servlets, MetaInfo info) throws InternalErrorException {
		super.updateInfo(servlets, info);
//		setProperties(itemDelegate, info);
	}

}
