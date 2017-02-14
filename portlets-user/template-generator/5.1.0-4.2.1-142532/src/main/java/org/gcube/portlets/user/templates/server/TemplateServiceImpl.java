package org.gcube.portlets.user.templates.server;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.util.Calendar;
import java.util.Date;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.d4sreporting.common.server.ServiceUtil;
import org.gcube.portlets.d4sreporting.common.shared.Metadata;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.portlets.user.templates.client.TGenConstants;
import org.gcube.portlets.user.templates.client.TemplateService;
import org.gcube.portlets.user.templates.server.loggers.CreateTemplateLogEntry;
import org.gcube.portlets.user.templates.server.loggers.OpenTemplateLogEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 *
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class TemplateServiceImpl extends RemoteServiceServlet implements TemplateService {

	private static final Logger _log = LoggerFactory.getLogger(TemplateServiceImpl.class);
	/**
	 * the current D4SSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user = "massimiliano.assante";
			this.getThreadLocalRequest().getSession().setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec");
		}
		return SessionManager.getInstance().getASLSession(sessionID, user);
	}
	/**
	 * recursively delete the templates folder of the dir dir
	 * @param dir the dir to delete
	 */
	public void delTemplateDir(File dir) {
		try {
			File[] files = dir.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory())
					delTemplateDir(files[i]);
				files[i].delete();
			}
		} catch (NullPointerException n) {
			_log.error("Nothing 2 delete");
		}
	}

	/**
	 * 
	 * @param repTmp
	 * @return
	 */
	private boolean getTemplateFromBasket(ReportTemplate repTmp, String pathToFile, String filename)  {
		try	{
			File dir = new File(pathToFile);
			_log.trace("DIR: " + pathToFile);
			if (! dir.exists() )
				dir.mkdirs();

			File f = new File(pathToFile+filename);
			InputStream inputStream = null;
			try {
				inputStream = repTmp.getData();
			} catch (InternalErrorException e) {
				e.printStackTrace();
				return false;
			}

			OutputStream out = new FileOutputStream(f);

			byte buf[] = new byte[1024];
			int len;
			while((len = inputStream.read(buf))>0)
				out.write(buf,0,len);
			out.close();
			inputStream.close();
			_log.debug("Successfully got ReportTemplate from Basket: " + pathToFile);
			return true;
		}
		catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}

	

	/**
	 * each portlet instance runs in a scope
	 * each portlet instance is used by a unique username from within the portal
	 * @return a String[2] containing the username in [0] and the scope in [1]
	 */
	public String[] getUserAndScope() {
		getASLSession();
		String[] toReturn = { getUsername(), getVreName() };
		return toReturn;
	}


	/**
	 * 
	 * @return the shared session
	 */
	public String getUsername() {
		HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
		HttpSession session = httpServletRequest.getSession();
		String user = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if(session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE)== null)
		{
			user = "massimiliano.assante";
			_log.debug("D4ScienceSession user NULL set to: " + user);
		}
		_log.debug("D4ScienceSession user: " + user);

		ASLSession d4session = SessionManager.getInstance().getASLSession(session.getId(), user);

		d4session.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
		return user;
	}




	/**
	 * 	Retrieve the user saved template names
	 * 
	 * 	@return a String[] containing the template names 
	 */
	public String[] getUserTemplateNames() {
		ServiceUtil myUtil = new ServiceUtil(getASLSession());
		Vector<String> tmp = new Vector<String>();
		String userDir = myUtil.getTemplateFolder(getVreName(), getUsername());

		File f  = new File(userDir);
		//checking if dir exists
		if (! f.exists()) {
			try {
				f.mkdirs();
				return new String[0];
			} catch (SecurityException ex) { 
				return new String[0];
			}
		}
		else {
			File []f2 = f.listFiles();
			for(int i = 0; i < f2.length; i++){
				if(f2[i].isDirectory()) {
					tmp.add(f2[i].getName());
				}
			}		
			return tmp.toArray(new String[0]);
		}
	}


	/**
	 * 
	 * @return the shared session
	 */
	public String getVreName() {
		String scope ="";
		if (TGenConstants.isDeployed) {
			ASLSession aSLsession = getASLSession();
			scope = aSLsession.getScopeName();
			//need to remove the initial / of the scope
			if (scope.charAt(0) == '/')
				scope = scope.substring(1, scope.length());
			_log.trace("TemplateGenServlet SCOPE: " + scope);
		}
		else 
			scope = "gcube/devsec";
		return scope;
	}





	/**
	 * 
	 * @return an instance of the user WorkspaceArea
	 * @throws HomeNotFoundException 
	 * @throws InternalErrorException 
	 * @throws WorkspaceFolderNotFoundException 
	 * @throws WorkspaceNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException 	{
		return HomeLibrary.getUserWorkspace(getASLSession().getUsername());	
	}

	/**
	 * Return an instance of
	 * @param folderid
	 * @return
	 */
	private WorkspaceFolder getWorkspaceInstance(String folderid) {
		Workspace root = null;
		WorkspaceItem item = null;
		try {
			root = getWorkspaceArea();
			item = root.getItem(folderid);
		} catch (Exception e) {
			e.printStackTrace();
		}

		_log.debug("Item Type: "+item.getType());
		if (item.getType() == WorkspaceItemType.FOLDER || item.getType() == WorkspaceItemType.SHARED_FOLDER) { 
			return (WorkspaceFolder) item;
		}
		_log.error("The item id does not belong to a valid folder id:" + folderid);
		return null;
	}


	/**
	 * @return a SerializableModel instance of the templatename passed as parameter
	 * @param templateName : the template to read from disk
	 * @param isImporting says if your importing or youre loading a template in the UI
	 * 
	 */
	public Model readModel(String templateName, String templateObjectID,boolean isImporting) {
		if (! TGenConstants.isDeployed) {
			Model toConvert = null;
			FileInputStream fis = null;
			ObjectInputStream in = null;
			try {
				fis = new FileInputStream(System.getenv("CATALINA_HOME") + "/webapps/usersArea/d4science.research-infrastructures.eu/FARM/FCPPS/templates/massimiliano.assante/CURRENT_OPEN/CURRENT_OPEN.d4st");
				in = new ObjectInputStream(fis);
				toConvert = (Model) in.readObject();
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			_log.debug("Converting fileToRead to Serializable object");
			ServiceUtil myUtil = new ServiceUtil(getASLSession());
			Model toReturn = (toConvert);
			storeTemplateInSession(toReturn);

			return toReturn;
		}
		else {
			Workspace root = null;

			try {
				root = getWorkspaceArea();
			} catch (Exception e) {
				e.printStackTrace();
			} 
			WorkspaceItem item = null;
			try {
				item = root.getItem(templateObjectID);
			} catch (ItemNotFoundException e) {
				e.printStackTrace();
			}
			_log.debug("getItem: " + templateObjectID);
			ServiceUtil myUtil = new ServiceUtil(getASLSession());
			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
				_log.trace("Item is a BASKET_ITEM");
				FolderItem bi = (FolderItem) item;
				if (bi.getFolderItemType() == FolderItemType.REPORT_TEMPLATE) {
					_log.trace("Item is a REPORT_TEMPLATE");
					ReportTemplate zippedTemplate = (ReportTemplate) bi;
					String zipFilename = templateName + ".zip"; //d4science template
					String zipPath = myUtil.getTemplatePath(templateName, getVreName(), getUsername());

					boolean fromBasket = getTemplateFromBasket(zippedTemplate, zipPath, zipFilename);
					String zipToExtract = zipPath + zipFilename;

					if (fromBasket) {
						File toExtract = new File(zipToExtract);
						File outputDir = new File( myUtil.getTemplatePath(templateName, getVreName(), getUsername()) );
						ZipUtil.unzipArchive(toExtract, outputDir);

						String templatePath = myUtil.getTemplatePath(templateName, getVreName(), getUsername());

						String fileToRead = templatePath +"CURRENT_OPEN" + ".d4st";
						_log.trace("Loading fileToRead from Disk");

						Model toReturn = null;
						Model toConvert = null;

						FileInputStream fis = null;
						ObjectInputStream in = null;
						try {
							fis = new FileInputStream(fileToRead);
							in = new ObjectInputStream(fis);
							toConvert = (Model) in.readObject();
							in.close();
						} catch (IOException ex) {
							ex.printStackTrace();
						} catch (ClassNotFoundException ex) {
							ex.printStackTrace();
						}
						_log.trace("Converting fileToRead to Serializable object");

						if (toConvert == null) {
							return null;
						}

						toReturn = toConvert;

						//changes the template name model
						toReturn.setTemplateName(templateName);
						File toDelete = new File(zipToExtract);
						toDelete.delete();
						_log.trace("Converted, Author:" + toReturn.getAuthor());


						//**** IMPORTANT ****

						//need just when user uses save template
						if (! isImporting) {
							getASLSession().setAttribute("currTemplateID", templateObjectID);
							_log.trace("****\n\n STORING CURRENT OPEN TEMPLATE WITH ID = " + templateObjectID);
							storeTemplateInSession(toReturn);
						}
						AccessLogger log = AccessLogger.getAccessLogger();
						OpenTemplateLogEntry logEntry = new OpenTemplateLogEntry(toReturn.getTemplateName(), templateObjectID);
						log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);
						return toReturn;
					}
				}
				_log.error("FAILED TO READ RETURING EMPTY Serializable Template");
				return new Model();
			}
			_log.error("FAILED TO READ FROM BASKET RETURING EMPTY Serializable Template");
			return new Model();
		}
	}


	/**
	 * 
	 * @return the model previously stored in the session 
	 */
	public Model readTemplateFromSession() {
		_log.debug("READING TEMPLATE FROM SESSION ");
		ASLSession aslSession =  getASLSession();
		String templateid = (String) aslSession.getAttribute("idtemplate");
		String templateName = "";
			
		_log.debug("Session Id = " + aslSession.getExternalSessionID());
		_log.debug("templateid  NULL? " + ( aslSession.getAttribute("idtemplate") == null));
		
		if (templateid != null) {
			_log.debug("templateid NOT NULL");
			if (! templateid.equals("")) {
				//reset the value
				aslSession.setAttribute("idtemplate", "");
				Workspace root = null;
				WorkspaceItem item = null;
				try {
					root = getWorkspaceArea();
					item = root.getItem(templateid);
					templateName = item.getName();
				} catch (Exception e) { 
					e.printStackTrace();
				}
				return (Model) readModel(templateName, templateid, false);
			}
			if (aslSession.getAttribute("myTemplate") != null)
				return (Model) aslSession.getAttribute("myTemplate") ;
			return null;
		}
		else {
			_log.debug("READING TEMPLATE FROM SESSION... template null?" + (aslSession.getAttribute("myTemplate") == null) );
			if (aslSession.getAttribute("myTemplate") != null) {
				_log.trace("getAttribute(\"myTemplate\")...");
				Model model =  (Model) aslSession.getAttribute("myTemplate") ;
				_log.trace("secNo returned: " + model.getSections().size());		
				return model;
			}


			ServiceUtil myUtil = new ServiceUtil(getASLSession());
			String dirToClean = myUtil.getTemplateFolder(getVreName(), getUsername());
			_log.trace("RETURNING> NULL, going to clean user template area: " + dirToClean);
			if (TGenConstants.isDeployed)
				delTemplateDir(new File(dirToClean));
			return null;
		}
	}


	/**
	 * @param model to save
	 */
	public void saveTemplate(String basketidToSaveIn,Model model) {
		//raplacing " " with _
		String templateName = model.getTemplateName(); 

		if (model.getAuthor() == null) {
			model.setAuthor(getUsername());
			_log.trace("Model author was Null: ");

		}
		if (model.getDateCreated() == null) {
			model.setDateCreated(new Date());
			_log.trace("getDateCreated  was Null: ");
		}
		model.setLastEdit(new Date());
		model.setLastEditBy(getUsername());
		_log.trace("Serializing Model, author: " + getUsername() + "\n*************\n**************\n date created:" + model.getDateCreated());

		//adding some metadata for the model
		model.getMetadata().clear();
		model.getMetadata().add(new Metadata("Author", model.getAuthor()));
		model.getMetadata().add(new Metadata("Created", ""+model.getDateCreated()));
		model.getMetadata().add(new Metadata("LastEdit", ""+model.getLastEdit()));
		model.getMetadata().add(new Metadata("LastEditBy", ""+model.getLastEditBy()));
		model.getMetadata().add(new Metadata("SectionsNo", ""+model.getSections().size()));		

		//adding some metadata for the sections
		for (BasicSection ss : model.getSections()) {
			ss.getMetadata().clear();
			//adding some metadata for the model
			ss.getMetadata().add(new Metadata("Author",  model.getAuthor()));
			ss.getMetadata().add(new Metadata("Created", ""+model.getDateCreated()));
			ss.getMetadata().add(new Metadata("LastEdit", ""+model.getLastEdit()));
			ss.getMetadata().add(new Metadata("LastEditBy", ""+model.getLastEditBy()));
		}
		ServiceUtil myUtil = new ServiceUtil(getASLSession());
		boolean result = myUtil.writeModel(model, templateName, getVreName(), getUsername());

		if (TGenConstants.isDeployed ) {
			if (!result) {
				_log.error("Could not save template, serializing failed");	
			}
			else {
				String templatePath = myUtil.getTemplateFolder(getVreName(), getUsername());	
				templatePath += "CURRENT_OPEN";

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				_log.trace("Trying to zip folder: " + templatePath);	

				String folderToZip = templatePath;
				String outZip = templatePath+".zip";

				try {
					ZipUtil.zipDir(outZip, folderToZip);
				} catch (IOException e) {
					_log.trace("Could not zip template, serializing failed");	
					e.printStackTrace();
				}
				_log.trace("Folder zipped, result: "+ outZip);	

				InputStream isZip;
				try {
					isZip = new BufferedInputStream(new FileInputStream(outZip));



					//in this case I need to know in which basket the template is					
					if (basketidToSaveIn == null) {
						_log.trace("\n\nSAVE TEMPLATE CALLED");
						String templateid = "";
						try {
							templateid = getASLSession().getAttribute("currTemplateID").toString();
						}
						catch (NullPointerException e) {

						}
						Workspace root = null;
						WorkspaceItem item = null;
						try {
							root = getWorkspaceArea();
							if (! templateid.equals("")) {
								_log.trace("*********************+------+**********************\n\n TRYING TO GET ITEM WITH ID = " + templateid);
								item = root.getItem(templateid);
								basketidToSaveIn = ((WorkspaceFolder) item.getParent()).getId();
							}
							else {								
								basketidToSaveIn = root.getRoot().getId();
								_log.trace("*********************---------**********************\n\n ITEM ID WAS EMPTY SAVING TO Root");

							}
						} 
						catch (WorkspaceFolderNotFoundException e) { 	_log.error("******\n\n\n\n" +
								"Item id=" + templateid + " has not been found\n\n\n*********");
						} catch (InternalErrorException e) { e.printStackTrace();
						} catch (HomeNotFoundException e) {	e.printStackTrace();
						} catch (ItemNotFoundException e) {	e.printStackTrace();}


						_log.trace("BASKET ID: " + basketidToSaveIn);

					}
					WorkspaceFolder toSaveIn = getWorkspaceInstance(basketidToSaveIn);


					if (toSaveIn.exists(templateName)) {
						_log.trace("Item exists already, deleting and creating new one");
						toSaveIn.removeChild(toSaveIn.find(templateName));
					}

					if (toSaveIn.exists(templateName+".d4sT")) {
						_log.trace("Item exists already, deleting and creating new one");
						toSaveIn.removeChild(toSaveIn.find(templateName+".d4sT"));
					}

					String templateToInsert = templateName;

					if (! templateName.endsWith(".d4sT"))
						templateToInsert+=".d4sT";

					/**
					 * Create a Report Template into this basket.
					 * @param name the template name.
					 * @param description the template description.
					 * @param created the template creation time.
					 * @param lastEdit the last edit time.
					 * @param author the template author.
					 * @param lastEditBy the last template editor.
					 * @param numberOfSections the number of sections.
					 * @param status the template status.
					 * @param templateData the template data.
					 * @return the template.
					 * @throws InsufficientPrivilegesException if the user don't have sufficient privileges to perform this operation.
					 * @throws InternalErrorException if an internal error occurs.
					 * @throws ItemAlreadyExistException if an item with the specified name already exists.
					 */ 

					Calendar dateCreated = Calendar.getInstance();
					dateCreated.setTime(model.getDateCreated());

					Calendar lastEdit = Calendar.getInstance();
					lastEdit.setTime(model.getLastEdit());

					ReportTemplate inserted;

					inserted = toSaveIn.createReportTemplateItem(templateToInsert, templateToInsert, dateCreated,
							lastEdit, model.getAuthor(), model.getLastEditBy(), model.getSections().size(), "no-status", isZip);




					//	ReportTemplate inserted = toSaveIn.createReportTemplateItem(templateToInsert, templateToInsert, isZip);
					getASLSession().setAttribute("currTemplateID", inserted.getId());
					AccessLogger log = AccessLogger.getAccessLogger();
					CreateTemplateLogEntry logEntry = new CreateTemplateLogEntry(model.getTemplateName(), inserted.getId());
					log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);


				} catch (Exception e) {
					e.printStackTrace();
				}

			}
		}
	}


	/**
	 * @param model to store in the session
	 */
	public void storeTemplateInSession(Model model) {
		ASLSession d4Session =  getASLSession();

		d4Session.setAttribute("myTemplate", model);

		try {
			_log.trace("Sec total: " +model.getSections().size());
		} catch (Exception e) {}

	}
	@Override
	public void renewHTTPSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		_log.info("HTTP Session renewed" + new Date(session.getLastAccessedTime()));
	}
}
