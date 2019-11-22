/**
 * 
 */
package org.gcube.common.homelibrary.util;

import java.io.PrintStream;

import org.gcube.common.homelibrary.home.User;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalPDFFile;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalUrl;
import org.gcube.common.homelibrary.home.workspace.folder.items.Image;
import org.gcube.common.homelibrary.home.workspace.folder.items.PDF;
import org.gcube.common.homelibrary.home.workspace.folder.items.Query;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;


/**
 * An utility to visit a workspace tree.
 * @author Federico De Faveri defaveri@isti.cnr.it
 *
 */
public class WorkspaceTreeVisitor extends IndentedVisitor {
	
	/**
	 * 
	 */
	public WorkspaceTreeVisitor()
	{
		super();
	}

	/**
	 * @param indentationLevel the indentation level.
	 * @param indentationChar the indentation char.
	 * @param os the output stream.
	 * @param logger the visitor logger.
	 */
	public WorkspaceTreeVisitor(String indentationLevel, String indentationChar, PrintStream os) {
		super(indentationLevel, indentationChar, os);
	}

	/**
	 * Visit the tree in verbose mode.
	 * @param folder the tree root.
	 * @throws InternalErrorException if an error occurs.
	 */
	public void visitVerbose(WorkspaceFolder folder) throws InternalErrorException
	{
		reset();
		visitWorkspaceItem(folder);
	}

	/**
	 * Visit a workspace item.
	 * @param item the item to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void visitWorkspaceItem(WorkspaceItem item) throws InternalErrorException
	{
		println();
		switch (item.getType()) {
			case FOLDER: visitWorkspaceFolder((WorkspaceFolder) item); break;
			case FOLDER_ITEM:visitFolderItem((FolderItem) item); break;
		}
	}

	/**
	 * Visits a workspace folder.
	 * @param folder the workspace folder to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void visitWorkspaceFolder(WorkspaceFolder folder) throws InternalErrorException
	{
		println("/FOLDER/");
		printWorkspaceItem(folder);
		indent();
		for (WorkspaceItem child:folder.getChildren()){
			visitWorkspaceItem(child);
		}
		outdent();
	}

	/**
	 * Visits a folder item.
	 * @param item the item to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	public void visitFolderItem(FolderItem item) throws InternalErrorException
	{
		println();
		println("/ITEM/");
		
		switch (item.getFolderItemType()) {
			case EXTERNAL_IMAGE: visitExternalImage((ExternalImage) item); break;
			case EXTERNAL_FILE: visitExternalFile((ExternalFile)item); break;
			case EXTERNAL_PDF_FILE: visitExternalPDFFile((ExternalPDFFile)item); break;
			case EXTERNAL_URL: visitExternalURL((ExternalUrl)item); break;
			case QUERY: visitQuery((Query)item); break;
			case REPORT: visitReport((Report)item); break;
			case REPORT_TEMPLATE: visitReportTemplate((ReportTemplate)item); break;
			//TODO add more types
			default: {
				printWorkspaceItem(item);
				println("UNSUPPORTED TYPE: "+item.getFolderItemType()); break;
			}
		}
		
	}
	
	protected void visitImage(Image img) throws InternalErrorException
	{
		println("MimeType "+img.getMimeType());
		println("Width "+img.getWidth());
		println("Height "+img.getHeight());
		println("Length "+img.getLength());
	}
	
	protected void visitPDF(PDF pdf) throws InternalErrorException
	{
		println("Number Of Pages "+pdf.getNumberOfPages());
		println("Version "+pdf.getVersion());
		println("Author "+pdf.getAuthor());
		println("Title "+pdf.getTitle());
		println("Producer "+pdf.getProducer());
	}

	/**
	 * Visits an image.
	 * @param img the image to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void visitExternalImage(ExternalImage img) throws InternalErrorException
	{
		println("[ExternalImage]");
		printFolderItem(img);
		visitImage(img);
	}
	
	protected void visitExternalFile(ExternalFile file) throws InternalErrorException
	{
		println("[ExternalFile]");
		printFolderItem(file);
		println("MimeType "+file.getMimeType());
		println("Length "+file.getLength());
	}
	
	protected void visitImageH(Image img) throws InternalErrorException
	{
		println("MimeType: "+img.getMimeType()+", Width: "+img.getWidth()+", Height: "+img.getHeight()+", Length: "+img.getLength());
	}
	

	
	protected void visitExternalPDFFile(ExternalPDFFile pdf) throws InternalErrorException
	{
		println("[ExternalPDFFile]");
		printFolderItem(pdf);
		println("MimeType "+pdf.getMimeType());
		visitPDF(pdf);
		println("Length "+pdf.getLength());
	}
	
	protected void visitExternalURL(ExternalUrl url) throws InternalErrorException
	{
		println("[ExternalUrl]");
		printFolderItem(url);
		println("Url "+url.getUrl());
		println("Length "+url.getLength());
	}
	
	protected void visitQuery(Query query) throws InternalErrorException
	{
		println("[Query]");
		printFolderItem(query);
		println("Query "+query.getQuery());
		println("Query type "+query.getQueryType());
		println("Length "+query.getLength());
	}
	
	protected void visitReport(Report report) throws InternalErrorException
	{
		println("[Report]");
		printFolderItem(report);
		println("Author "+report.getAuthor());
		println("LastEditBy "+report.getLastEditBy());
		println("TemplateName "+report.getTemplateName());
		println("NumberOfSections "+report.getNumberOfSections());
		println("Created "+sdf.format(report.getCreated().getTime()));
		println("LastEdit "+sdf.format(report.getLastEdit().getTime()));
		println("Status "+report.getStatus());
		println("Length "+report.getLength());
	}
	
	protected void visitReportTemplate(ReportTemplate reportTemplate) throws InternalErrorException
	{
		println("[ReportTemplate]");
		printFolderItem(reportTemplate);
		println("Author "+reportTemplate.getAuthor());
		println("LastEditBy "+reportTemplate.getLastEditBy());
		println("NumberOfSections "+reportTemplate.getNumberOfSections());
		println("Created "+sdf.format(reportTemplate.getCreated().getTime()));
		println("LastEdit "+sdf.format(reportTemplate.getLastEdit().getTime()));
		println("Status "+reportTemplate.getStatus());
		println("Length "+reportTemplate.getLength());
	}
	

	/**
	 * Visits a Workspace item.
	 * @param item the item to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void printWorkspaceItem(WorkspaceItem item) throws InternalErrorException
	{
		println("ID: "+item.getId());
		println("NAME: "+item.getName());
		println("DESCRIPTION: "+item.getDescription());
		println("CREATION TIME: "+sdf.format(item.getCreationTime().getTime()));
		println("OWNER: "+item.getOwner());
		//TODO print metadata
	}
	
	protected void printFolderItem(FolderItem item) throws InternalErrorException
	{
		printWorkspaceItem(item);
	}
	
	

	/**
	 * Visits an user.
	 * @param user the user to visit.
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void visitUser(User user) throws InternalErrorException
	{
		println("ID: "+user.getId());
		println("LOGIN: "+user.getPortalLogin());
	}
	
	/**
	 * Visit an item tree without verbose information.
	 * @param item the tree root.
	 * @throws InternalErrorException if an error occurs.
	 */
	public void visitSimple(WorkspaceItem item) throws InternalErrorException
	{
		reset();
		visitItem(item);
	}
	
	/**
	 * Visit an item tree without verbose information.
	 * @param item the item to visit
	 * @throws InternalErrorException if an error occurs.
	 */
	protected void visitItem(WorkspaceItem item) throws InternalErrorException
	{
//System.out.println(item.getPath());
		switch (item.getType()) {
			case FOLDER: println("/["+item.getName()+"]"); break;
			case FOLDER_ITEM: println("/"+item.getName()); break;
		}
		
		indent();
		
		for (WorkspaceItem child:item.getChildren()) visitItem(child);
		
		outdent();
	}

}
