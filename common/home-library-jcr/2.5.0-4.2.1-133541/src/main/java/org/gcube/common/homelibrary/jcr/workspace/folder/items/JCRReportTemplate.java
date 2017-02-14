package org.gcube.common.homelibrary.jcr.workspace.folder.items;

import java.io.IOException;
import java.io.InputStream;
import java.util.Calendar;
import java.util.Map;

import org.apache.commons.lang.Validate;
import org.gcube.common.homelibary.model.items.ItemDelegate;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.NodeProperty;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.jcr.workspace.JCRWorkspace;
import org.gcube.common.homelibrary.model.exceptions.RepositoryException;
import org.gcube.contentmanagement.blobstorage.transport.backend.RemoteBackendException;

import com.thoughtworks.xstream.XStream;

public class JCRReportTemplate extends JCRWorkspaceFolderItem implements
ReportTemplate {


	private final JCRFile file;

	public JCRReportTemplate(JCRWorkspace workspace, ItemDelegate itemDelegate) throws RepositoryException, InternalErrorException {
		super(workspace, itemDelegate);
		this.file = new JCRFile(workspace, itemDelegate);
	}

	public JCRReportTemplate(JCRWorkspace workspace, ItemDelegate itemDelegate, String name,
			String description, Calendar created, Calendar lastEdit,
			String author, String lastEditBy, int numberOfSections, String status,
			InputStream templateData) throws RepositoryException, RemoteBackendException, InternalErrorException, IOException  {

		super(workspace,itemDelegate,name,description);

		Validate.notNull(created, "Created must be not null");
		Validate.notNull(lastEdit, "LastEdit must be not null");
		Validate.notNull(author, "Author must be not null");
		Validate.notNull(lastEditBy, "LastEditBy must be not null");
		Validate.notNull(status, "Status must be not null");
		Validate.notNull(templateData, "TemplateData must be not null");

		this.file = new JCRFile(workspace, itemDelegate, templateData);

		Map<NodeProperty, String> content = itemDelegate.getContent();
		content.put(NodeProperty.AUTHOR,author);
		content.put(NodeProperty.RT_CREATED, new XStream().toXML(created));
		content.put(NodeProperty.LAST_EDIT, new XStream().toXML(lastEdit));
		content.put(NodeProperty.LAST_EDIT_BY, lastEditBy);
		content.put(NodeProperty.NUMBER_OF_SECTION, new XStream().toXML(numberOfSections));
		content.put(NodeProperty.STATUS, status);

	}

	@Override
	public long getLength() throws InternalErrorException {
		return file.getLength();
	}

	@Override
	public InputStream getData() throws InternalErrorException {
		return file.getData();
	}

	@Override
	public Calendar getCreated() {

		Calendar created;
		try {
			created = (Calendar) new XStream().fromXML(delegate.getContent().get(NodeProperty.CREATED));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return created; 	
	}

	@Override
	public Calendar getLastEdit() {

		Calendar lastEdit;
		try {
			lastEdit = (Calendar) new XStream().fromXML(delegate.getContent().get(NodeProperty.LAST_EDIT));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return lastEdit; 		
	}

	@Override
	public String getAuthor() {

		String templateName = null;
		try {
			templateName = delegate.getContent().get(NodeProperty.AUTHOR);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return templateName; 
	}

	@Override
	public String getLastEditBy() {

		String templateName = null;
		try {
			templateName = delegate.getContent().get(NodeProperty.LAST_EDIT_BY);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return templateName; 	
	}

	@Override
	public int getNumberOfSections() {

		int section = 0;
		try {
			section = (int) new XStream().fromXML(delegate.getContent().get(NodeProperty.NUMBER_OF_SECTION));
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return section; 
	}

	@Override
	public String getStatus() {

		String status = null;
		try {
			status = delegate.getContent().get(NodeProperty.STATUS);
		} catch (Exception e) {
			throw new RuntimeException(e) ;
		}
		return status; 		

	}

	@Override
	public FolderItemType getFolderItemType() {
		return FolderItemType.REPORT_TEMPLATE;
	}

	
	@Override
	public String getMimeType() throws InternalErrorException {
		return null;
	}

}
