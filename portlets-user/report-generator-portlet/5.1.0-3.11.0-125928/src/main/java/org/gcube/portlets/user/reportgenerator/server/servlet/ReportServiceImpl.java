package org.gcube.portlets.user.reportgenerator.server.servlet;

import static org.gcube.resources.discovery.icclient.ICFactory.clientFor;
import static org.gcube.resources.discovery.icclient.ICFactory.queryFor;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.xml.bind.DatatypeConverter;

import net.sf.csv4j.CSVFileProcessor;
import net.sf.csv4j.CSVLineProcessor;
import net.sf.csv4j.ParseException;
import net.sf.csv4j.ProcessingException;

import org.apache.commons.io.IOUtils;
import org.apache.tika.config.TikaConfig;
import org.apache.tika.detect.Detector;
import org.apache.tika.io.TikaInputStream;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.gcube.application.framework.accesslogger.library.impl.AccessLogger;
import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.application.reporting.reader.ModelReader;
import org.gcube.application.rsg.client.RsgReadClient;
import org.gcube.application.rsg.client.RsgWriteClient;
import org.gcube.application.rsg.client.support.ClientException;
import org.gcube.application.rsg.service.dto.ReportEntry;
import org.gcube.application.rsg.service.dto.ReportType;
import org.gcube.application.rsg.service.dto.response.ServiceResponse;
import org.gcube.application.rsg.service.dto.response.ServiceResponseMessage;
import org.gcube.application.rsg.support.builder.exceptions.ReportBuilderException;
import org.gcube.application.rsg.support.builder.impl.ReportManagerReportBuilder;
import org.gcube.application.rsg.support.model.components.impl.CompiledReport;
import org.gcube.applicationsupportlayer.social.ApplicationNotificationsManager;
import org.gcube.applicationsupportlayer.social.NotificationsManager;
import org.gcube.common.encryption.StringEncrypter;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibary.model.items.type.WorkspaceItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.exceptions.HomeNotFoundException;
import org.gcube.common.homelibrary.home.exceptions.InternalErrorException;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.WorkspaceFolder;
import org.gcube.common.homelibrary.home.workspace.WorkspaceItem;
import org.gcube.common.homelibrary.home.workspace.WorkspaceSharedFolder;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemAlreadyExistException;
import org.gcube.common.homelibrary.home.workspace.exceptions.ItemNotFoundException;
import org.gcube.common.homelibrary.home.workspace.exceptions.WorkspaceFolderNotFoundException;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.ExternalImage;
import org.gcube.common.homelibrary.home.workspace.folder.items.GCubeItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.home.workspace.folder.items.ts.TimeSeries;
import org.gcube.common.resources.gcore.ServiceEndpoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.AccessPoint;
import org.gcube.common.resources.gcore.ServiceEndpoint.Property;
import org.gcube.common.scope.api.ScopeProvider;
import org.gcube.portal.custom.communitymanager.OrganizationsUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.portlets.admin.wfdocslibrary.client.WfDocsLibrary;
import org.gcube.portlets.admin.wfdocslibrary.server.db.MyDerbyStore;
import org.gcube.portlets.admin.wfdocslibrary.server.db.Store;
import org.gcube.portlets.d4sreporting.common.server.ServiceUtil;
import org.gcube.portlets.d4sreporting.common.shared.BasicComponent;
import org.gcube.portlets.d4sreporting.common.shared.BasicSection;
import org.gcube.portlets.d4sreporting.common.shared.ComponentType;
import org.gcube.portlets.d4sreporting.common.shared.Model;
import org.gcube.portlets.d4sreporting.common.shared.RepTimeSeries;
import org.gcube.portlets.d4sreporting.common.shared.RepeatableSequence;
import org.gcube.portlets.d4sreporting.common.shared.Table;
import org.gcube.portlets.d4sreporting.common.shared.TableCell;
import org.gcube.portlets.user.reportgenerator.client.ReportConstants;
import org.gcube.portlets.user.reportgenerator.client.ReportService;
import org.gcube.portlets.user.reportgenerator.server.servlet.loggers.CreateReportLogEntry;
import org.gcube.portlets.user.reportgenerator.server.servlet.loggers.OpenReportLogEntry;
import org.gcube.portlets.user.reportgenerator.server.servlet.loggers.OpenWorkflowLogEntry;
import org.gcube.portlets.user.reportgenerator.server.servlet.loggers.SaveWorkflowLogEntry;
import org.gcube.portlets.user.reportgenerator.shared.RSGAccessPoint;
import org.gcube.portlets.user.reportgenerator.shared.ReportImage;
import org.gcube.portlets.user.reportgenerator.shared.SessionInfo;
import org.gcube.portlets.user.reportgenerator.shared.UserBean;
import org.gcube.portlets.user.reportgenerator.shared.VMEReportBean;
import org.gcube.portlets.user.reportgenerator.shared.VMETypeIdentifier;
import org.gcube.portlets.user.reportgenerator.shared.VmeExportResponse;
import org.gcube.portlets.user.reportgenerator.shared.VmeResponseEntry;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileException;
import org.gcube.portlets.widgets.exporter.shared.SaveReportFileExistException;
import org.gcube.portlets.widgets.exporter.shared.TypeExporter;
import org.gcube.resources.discovery.client.api.DiscoveryClient;
import org.gcube.resources.discovery.client.queries.api.SimpleQuery;
import org.gcube.vomanagement.usermanagement.RoleManager;
import org.gcube.vomanagement.usermanagement.impl.liferay.LiferayRoleManager;
import org.gcube.vomanagement.usermanagement.model.RoleModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.gwt.user.server.rpc.RemoteServiceServlet;
import com.liferay.portal.model.User;
import com.liferay.portal.model.UserModel;
import com.liferay.portal.service.LockLocalServiceUtil;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;




/**
 * 
 * class implementing services
 * @author Massimiliano Assante, ISTI-CNR - massimiliano.assante@isti.cnr.it
 */
@SuppressWarnings("serial")
public class ReportServiceImpl  extends RemoteServiceServlet implements ReportService {

	private static final Logger _log = LoggerFactory.getLogger(ReportServiceImpl.class);

	protected static final String IMAGE_SERVICE_URL = "reports/DownloadService";
	/**
	 * 
	 */
	public static final String CURRENT_REPORT_ID_ATTRIBUTE = "CURRENT_REPORT_ID_ATTRIBUTE";
	/**
	 * 
	 */
	public static final String CURRENT_REPORT_INSTANCE = "myReport";
	/**
	 * 
	 */
	public static final String PREVIOUS_REPORT_INSTANCE = "myPreviousReport";
	/**
	 * 
	 */
	public static final String RSG_WS_ADDRESS = "RSG-WS-ENDPOINT";

	private static final String REPORT_IMAGES_FOLDER = "Report Images";
	public static final String TEST_USER = "test.user";
	public static final String TEST_SCOPE = "/gcube/devsec/devVRE";

	/**
	 * the WF DB Store
	 */
	private Store store;
	//the client for the VME-DB case
	private RsgReadClient rsgReadClient;
	private RsgWriteClient rsgWriteClient;

	//set to true if want to test workflow menu mode
	boolean testWorkflow = false;

	/**
	 * Called then servlet is intialized
	 */
	public void init()	{
		_log.info("Initializing Servlet ReportServiceImpl... connecting to WF DB");
		store = new MyDerbyStore();
	}

	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession() {
		String sessionID = this.getThreadLocalRequest().getSession().getId();

		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user =  getDevelopmentUser();
			if (user.compareTo(TEST_USER) != 0) {
				SessionManager.getInstance().getASLSession(sessionID, user).setScope(TEST_SCOPE);
				this.getThreadLocalRequest().getSession().setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);			
				String email = user+"@isti.cnr.it";
				String fullName = "Andrea Rossi";
				String thumbnailURL = "images/Avatar_default.png";
				SessionManager.getInstance().getASLSession(sessionID, user).setUserEmailAddress(email);
				SessionManager.getInstance().getASLSession(sessionID, user).setUserAvatarId(thumbnailURL);
				SessionManager.getInstance().getASLSession(sessionID, user).setUserFullName(fullName);
			}

		}
		return SessionManager.getInstance().getASLSession(sessionID, user);

	}
	public String getDevelopmentUser() {
		String user = TEST_USER;
		//		user = "andrea.rossi";
		return user;
	}
	/**
	 * 
	 * @return true if you're running into the portal, false if in development
	 */
	private boolean isWithinPortal() {
		try {
			UserLocalServiceUtil.getService();
			return true;
		} 
		catch (com.liferay.portal.kernel.bean.BeanLocatorException ex) {			
			_log.trace("Development Mode ON");
			return false;
		}			
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

		_log.debug("userDir: " + userDir);

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
	 *  @return a SerializableModel instance of the imported fimes xml
	 */
	public Model readImportedModel(String tempPath) {
		Model toConvert = null;

		FileInputStream fis = null;
		ObjectInputStream in = null;

		try {

			fis = new FileInputStream(tempPath);
			in = new ObjectInputStream(fis);
			toConvert = (Model) in.readObject();
			in.close();
		} catch (IOException ex) {
			ex.printStackTrace();
		} catch (ClassNotFoundException ex) {
			ex.printStackTrace();
		}
		_log.debug("Converting Imported Fimes to Serializable object, num sections: " + toConvert.getSections().size());
		return (toConvert);
	}

	/**
	 * @return a SerializableModel instance of the templatename passed as parameter
	 * @param templateName : the template to read from disk
	 * @param templateObjectID the id in the basket
	 * @param isTemplate says if you're opening a template or a report
	 * @param isImporting says if your importing or youre loading a template in the UI
	 * 
	 */
	public Model readModel(String templateName, String templateObjectID, boolean isTemplate, boolean isImporting) {
		ServiceUtil myUtil = new ServiceUtil(getASLSession());
		_log.debug("Reading " + templateName);

		Workspace root = null;
		WorkspaceItem item = null;
		try {
			root = getWorkspaceArea();
			item = root.getItem(templateObjectID);
			if (! isImporting)
				storeReportItemIDInSession(templateObjectID);

			_log.debug("** -> getItem, id: " + templateObjectID +  " Name: " + item.getName());

		} catch (Exception e) {
			e.printStackTrace();
		}

		String zipToExtract = "";
		if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
			_log.debug("Item is a FolderItem - OK... next step check if is a templet or a report");

			FolderItem bi = (FolderItem) item;
			boolean fromBasket = false;

			if (isTemplate) {
				if (bi.getFolderItemType() == FolderItemType.REPORT_TEMPLATE) {
					ReportTemplate zippedTemplate =  (ReportTemplate) bi;
					String zipFilename = "";
					if (! isTemplate) {//then is a report 
						zipFilename = templateName + "-report.zip"; //gCube report
						_log.debug("********************** Reading template -----------------");
					}
					else
						zipFilename = templateName + ".zip"; //gCube template

					String zipPath = myUtil.getTemplatePath(templateName, getVreName(), getUsername());
					fromBasket = getTemplateFromBasket(zippedTemplate, zipPath, zipFilename);
					zipToExtract = zipPath + zipFilename;
				}
			}

			if (bi.getFolderItemType() == FolderItemType.REPORT) {

				_log.debug("Item is a REPORT");
				Report zippedTemplate = (Report) bi;

				String zipFilename = "";
				if (! isTemplate) {//then is a report 
					zipFilename = templateName + "-report.zip"; //gCube report
					_log.debug(" Reading report -----------------");
				}
				else
					zipFilename = templateName + ".zip"; //d4science template

				File toDelete = new File(zipFilename);
				toDelete.delete();

				String zipPath = myUtil.getTemplatePath(templateName, getVreName(), getUsername());
				fromBasket = getReportFromBasket(zippedTemplate, zipPath, zipFilename);
				zipToExtract = zipPath + zipFilename;
			}

			_log.info("\n\n** zipToExtract: " +  zipToExtract);

			if (bi.getFolderItemType() == FolderItemType.REPORT || bi.getFolderItemType() == FolderItemType.REPORT_TEMPLATE) { 

				if (fromBasket) {
					File toExtract = new File(zipToExtract);
					File outputDir = new File( myUtil.getTemplatePath(templateName, getVreName(), getUsername()) );
					ZipUtil.unzipArchive(toExtract, outputDir);

					String templatePath = myUtil.getTemplatePath(templateName, getVreName(), getUsername());

					String modelFilename = "";
					try {
						modelFilename = seekModel(templatePath, templateName);
					} catch (FileNotFoundException e) {	e.printStackTrace(); }  

					String fileToRead = templatePath + modelFilename + ".d4st" ;

					_log.debug("Loading fileToRead from Disk -> " + fileToRead);

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
					_log.debug("Converting fileToRead to Serializable object");
					toReturn = (toConvert);

					//					changes the template name model
					toReturn.setTemplateName(templateName);

					File toDelete = new File(fileToRead); //to delete the file extracted from the workspace					

					File toDelete1 = new File( myUtil.getTemplatePath(templateName, getVreName(), getUsername()));
					boolean deleted1 = toDelete1.delete();


					File toDelete2 = new File(zipToExtract);
					boolean deleted2 = toDelete2.delete();

					boolean deleted = toDelete.delete();
					_log.debug("deleting extracted file: " + fileToRead + " result: " + deleted);	
					_log.debug("dirToDelete: " + toDelete1 + " result: " + deleted1);
					_log.debug("dirToDelete: " + toDelete2 + " result: " + deleted2);

					//**** IMPORTANT ****
					if (! isImporting) {
						storeTemplateInSession(toReturn);
						_log.debug("storeTemplateInSession DONE");
					}

					AccessLogger log = AccessLogger.getAccessLogger();
					OpenReportLogEntry logEntry = new OpenReportLogEntry(toReturn.getTemplateName(), templateObjectID);
					log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

					if (! isTemplate) {
						try {
							ModelReader reader = new ModelReader(toReturn);
							System.out.println(reader);
						} catch (Exception e) {
							_log.warn("ModelReader fails to read this report, continue...");	
						}
					}				

					return toReturn;
				}
			}
			_log.error("FAILED TO READ RETURING EMPTY Serializable Template");
			return new Model();
		}
		_log.error("FAILED TO READ FROM BASKET RETURING EMPTY Serializable Template");
		return new Model();

	}


	/**
	 * used when an image is uploaded
	 */
	@Override
	public ReportImage getUploadedImageUrlById(String fileName, String absolutePathOnServer) {
		if (absolutePathOnServer == null)
			return null;
		try {
			_log.trace("trying read: "+absolutePathOnServer);
			WorkspaceFolder imagesFolder = getImagesFolder();
			// Read from an input stream
			InputStream imageData = new BufferedInputStream(new FileInputStream(absolutePathOnServer));
			String itemName = fileName;
			int i = 1;            
			while (imagesFolder.exists(itemName)) {
				String[] splitted = fileName.split("\\.");               
				itemName = splitted[0]+"_"+i+"."+splitted[splitted.length-1];
				i++;
			}            	
			ExternalImage image = imagesFolder.createExternalImageItem(itemName, "automatically uploaded by Reports Manager", getMimeType(imageData, fileName), imageData);
			_log.trace("Created external image with name " + image.getName());
			//delete the temp file
			File toDelete = new File(absolutePathOnServer);
			toDelete.delete();
			return new ReportImage(image.getId(), buildImageServiceUrl(image.getId()), image.getWidth(), image.getHeight());
		} catch (Exception e) {
			_log.error("Error in server get image by id", e);
			e.printStackTrace();
		}
		return null;
	}
	/**
	 * @return  the images folder of the workspace, create it if not exist.
	 */
	private WorkspaceFolder getImagesFolder() {
		Workspace ws = null;
		WorkspaceFolder reportFolder = null;
		try {
			ws = getWorkspaceArea();			
			if (! ws.getRoot().exists(REPORT_IMAGES_FOLDER)) {
				reportFolder = ws.getRoot().createFolder(REPORT_IMAGES_FOLDER, "This folder hosts the images uploaded by the Reports Manager");				
			} else {
				reportFolder =  (WorkspaceFolder) ws.getRoot().find(REPORT_IMAGES_FOLDER);
			}

		} catch (Exception e) {
			e.printStackTrace();
		} 
		return reportFolder;
	}
	/**
	 * 
	 * @param is
	 * @return
	 * @throws IOException 
	 * @throws MagicParseException
	 * @throws MagicMatchNotFoundException
	 * @throws MagicException
	 */
	protected static String getMimeType(InputStream is, String filenameWithExtension) throws IOException {
		TikaConfig config = TikaConfig.getDefaultConfig();
		Detector detector = config.getDetector();
		TikaInputStream stream = TikaInputStream.get(is);
		Metadata metadata = new Metadata();
		metadata.add(Metadata.RESOURCE_NAME_KEY, filenameWithExtension);
		MediaType mediaType = detector.detect(stream, metadata);
		return mediaType.getBaseType().toString();
	}
	/**
	 * used to actually display images in reports (when reading reports)
	 */
	@Override
	public ReportImage getImageUrlById(String identifier) {
		if (identifier == null)
			return null;
		try {
			Workspace workspace = getWorkspaceArea();
			_log.trace("get image URL by id: "+identifier);

			WorkspaceItem item = workspace.getItem(identifier); //get item from workspace	

			//ACCOUNTING READ
			item.markAsRead(true);

			_log.trace("item name " + item.getName());
			ExternalImage image = (ExternalImage) item; //Cast External Document
			return new ReportImage(image.getId(), buildImageServiceUrl(image.getId()), image.getWidth(), image.getHeight());

		} catch (Exception e) {
			_log.error("Error in server get image by id", e);
			e.printStackTrace();
		}
		return null;
	}

	/**
	 * 
	 * @param id
	 * @return
	 */
	protected String buildImageServiceUrl(String id) {
		StringBuilder sb = new StringBuilder();

		sb.append(this.getServletContext().getContextPath()).append("/");
		sb.append(IMAGE_SERVICE_URL).append("?id=").append(id).append("&type=IMAGE");
		return sb.toString();
	}


	/**
	 * handles the case that the user has changed the template name in the basket
	 * @param templatePath
	 * @param templateName
	 * @return
	 * @throws FileNotFoundException
	 */
	private String seekModel(String templatePath, String templateName) throws FileNotFoundException {
		_log.debug("seekModel: tPath=" + templatePath);
		String fileToSeek = templatePath + templateName + ".d4st";

		File toSeek = new File(fileToSeek);
		if (toSeek.exists()) {
			_log.debug("seekModel: modelName is the SAME returning");
			return templateName;
		}
		else {
			_log.debug("seekModel: modelName DIFFERENT upgrading");
			File dirToLookIn = new File(templatePath);
			File[] innerFiles = dirToLookIn.listFiles();
			for (int i = 0; i < innerFiles.length; i++) {
				_log.debug("scanning files in extracted folder: " + innerFiles[i].getName());
				if (innerFiles[i].getName().endsWith(".d4st")) {
					String toReturn = innerFiles[i].getName();
					toReturn = toReturn.substring(0, toReturn.length()-5);
					_log.debug("seekModel: returning.. =" + toReturn);
					return toReturn;
				}
			}
		}	
		throw new FileNotFoundException();
	}


	/**
	 * get the template instance from the Basket
	 * @param repTmp
	 * @return
	 */
	private boolean getTemplateFromBasket(ReportTemplate repTmp, String pathToFile, String filename)  {
		try	{
			File dir = new File(pathToFile);
			_log.debug("DIR: " + pathToFile);
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
			_log.info("Successfully got ReportTemplate from Basket: " + pathToFile);
			return true;
		}
		catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}


	/**
	 * get the report instance from the Basket
	 * @param repTmp .
	 * @param pathToFile the directory where to save the file
	 * @param filename the filename to give to the newly created file
	 * @return
	 */
	private boolean getReportFromBasket(Report repTmp, String pathToFile, String filename)  {
		try	{
			File dir = new File(pathToFile);
			_log.debug("DIR: " + pathToFile);
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
			_log.info("Successfully got ReportTemplate from HL: " + pathToFile);
			return true;
		}
		catch (IOException e){
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * 
	 * @return the shared session
	 */
	public String getUsername() {
		if (! ReportConstants.isDeployed) {
			return "massimiliano.assante";
		} else {
			HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
			HttpSession session = httpServletRequest.getSession();
			String user = (String) session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
			if(session.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE)== null)
			{
				user = "massimiliano.assante";
				_log.warn("D4ScienceSession user NULL set to: " + user);
			}
			_log.warn("ASLSession user: " + user);

			ASLSession d4session = SessionManager.getInstance().getASLSession(session.getId(), user);
			d4session.setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			return user;
		}
	}

	/**
	 * 
	 * @return the current scope
	 */
	public String getVreName() {
		if (! ReportConstants.isDeployed) {
			return TEST_SCOPE;
		} else {
			HttpServletRequest httpServletRequest = this.getThreadLocalRequest();
			HttpSession session = httpServletRequest.getSession();

			ASLSession d4session = SessionManager.getInstance().getASLSession(session.getId(), getUsername());
			String scope = d4session.getScopeName();

			if(scope == null) {
				scope = TEST_SCOPE;
				_log.warn("ASL Session scope NULL set to: " + scope);
			}

			//need to remove the initial / of the scope
			if (scope.charAt(0) == '/')
				scope = scope.substring(1, scope.length());
			_log.info("SCOPE: " + scope);
			return scope;
		}

	}

	private RSGAccessPoint getRSGWSAddress() {
		RSGAccessPoint rsgAp = (RSGAccessPoint) this.getThreadLocalRequest().getSession().getAttribute(RSG_WS_ADDRESS);
		if (rsgAp != null)
			_log.trace("getRSGWSAddress: " + rsgAp.getRestUrl());
		return rsgAp;
	}

	private void setRSGWSAddress(RSGAccessPoint rsgAp) {
		this.getThreadLocalRequest().getSession().setAttribute(RSG_WS_ADDRESS, rsgAp);
		if (rsgAp != null)
			_log.trace("setting RSG WS address to " + rsgAp.getRestUrl());
	}
	/**
	 * 
	 * @return
	 * @throws WorkspaceFolderNotFoundException
	 * @throws InternalErrorException
	 * @throws HomeNotFoundException
	 */
	protected Workspace getWorkspaceArea() throws WorkspaceFolderNotFoundException, InternalErrorException, HomeNotFoundException 	{
		return HomeLibrary.getUserWorkspace(getASLSession().getUsername());	
	}


	/**
	 * reads from the file system and returns the user workspace as TreeNode object
	 * 
	 * @return the Default folder if if there is no basket in session, else the folder in session id
	 */
	public String getRootFolder() {
		try {
			Workspace workspaceArea = getWorkspaceArea();
			WorkspaceFolder basket = workspaceArea.getRoot();
			return basket.getId();
		} catch (Exception e) {	e.printStackTrace();
		} 
		return "Coud not open default folder";
	}

	/**
	 * 
	 * @return the model previously stored in the session 
	 */
	public Model readTemplateFromSession() {
		ASLSession d4Session =  getASLSession();

		String templateid = (String) d4Session.getAttribute("idreport");

		Object workflowid = getASLSession().getAttribute(WfDocsLibrary.LAST_WORKFLOW_ID);
		_log.debug(" (templateid != null && workflowid != null)  =  " +  (templateid != null) + " - " + (workflowid != null));
		if (workflowid != null) {
			getASLSession().setAttribute(WfDocsLibrary.LAST_WORKFLOW_ID, null);
			return null;
		}		

		String templateName = "";
		_log.debug("TEMPLATE ID==NULL " + (templateid == null));
		if (templateid != null) {
			if (! templateid.equals("")) {
				_log.debug("READING SESSION VARIABLE FOR REPORT ID... " + templateid);
				//reset the value
				d4Session.setAttribute("idreport", "");
				Workspace root = null;
				WorkspaceItem item = null;
				try {
					root = getWorkspaceArea();

					item = root.getItem(templateid);
					_log.info("READ REPORT FROM WP... " +  item.getName());
					templateName = item.getName();
				} catch (WorkspaceFolderNotFoundException e) {e.printStackTrace();
				} catch (InternalErrorException e) { e.printStackTrace();
				} catch (HomeNotFoundException e) {	e.printStackTrace();
				} catch (ItemNotFoundException e) {	e.printStackTrace();}

				Model toReturn = readModel(templateName, templateid, false, false);
				return toReturn; 
			}
			if (d4Session.getAttribute(CURRENT_REPORT_INSTANCE) != null)
				return (Model) d4Session.getAttribute(CURRENT_REPORT_INSTANCE) ;
			return null;
		}
		else {
			if (d4Session.getAttribute(CURRENT_REPORT_INSTANCE) != null) {				
				_log.debug("getAttribute(\"CURRENT_REPORT_INSTANCE\")...");
				Model model =  (Model) d4Session.getAttribute(CURRENT_REPORT_INSTANCE) ;
				_log.debug(model.getTemplateName());

				return model;
			}
		}
		ServiceUtil myUtil = new ServiceUtil(getASLSession());
		String dirToClean = myUtil.getTemplateFolder(getVreName(), getUsername());
		_log.info("No data on session for Reports, cleaning temp dir: " + dirToClean);
		delTemplateDir(new File(dirToClean));
		return null;
	}


	/**
	 * recurdively delete the templates folder of the dir dir
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
		} catch (Exception e) {
			System.out.println("WARNING: Could not cleaning temp dir: reason unknown");
		}
	}


	/**
	 * @param model to store in the session
	 */
	public void storeTemplateInSession(Model model) {
		ASLSession d4Session =  getASLSession();
		d4Session.setAttribute(CURRENT_REPORT_INSTANCE, model);
		_log.trace("Saved in Session");
	}


	private void convertDynamicImagesFromHL(Model model) {
		_log.debug("model == NULL " + (model == null));
		Vector<BasicSection> sections = model.getSections();
		for (BasicSection section : sections) {
			for (BasicComponent component : section.getComponents()) {
				if (component.getType() == ComponentType.DYNA_IMAGE) {
					_log.debug("Found Simple Image: " + component.getPossibleContent());
					if (component.getId() != null) {  // you need to convert only new images that stay in the HL Workspace, this is the check
						String imageID = component.getId();
						component.setPossibleContent(getdDataImagebase64(imageID));
						_log.trace("Image converted base 64 OK: " + component.getPossibleContent());
					}
				}
				if (component.getType() == ComponentType.REPEAT_SEQUENCE || component.getType() == ComponentType.BODY_TABLE_IMAGE) { //there could be images inside
					RepeatableSequence res = (RepeatableSequence) component.getPossibleContent();
					for (BasicComponent co : res.getGroupedComponents()) {
						if (component.getType() == ComponentType.DYNA_IMAGE) {
							_log.debug("Found Image IN SEQUENCE, type is: " + component.getType());
							if (co.getId() != null) { // you need to convert only new images that stay in the HL Workspace, this is the check
								String imageID = co.getId();
								co.setPossibleContent(getdDataImagebase64(imageID));
								_log.trace("Image converted base 64 OK, in SEQUENCE: " + co.getPossibleContent());
							}
						}
					}
				}
			}
		}
	}


	/**
	 * convert the image into a String encoded base 64
	 * @param imageIDinWorkspace the id of the image in workspace
	 * @return the string representing the image converted to be put in the img src attribute e.g. data:image/ong;base64,a...z
	 */
	private String getdDataImagebase64(String imageIDinWorkspace) {
		Workspace root = null;
		try {
			root = getWorkspaceArea();

			WorkspaceItem item = null;
			item = root.getItem(imageIDinWorkspace);
			if (item.getType() == WorkspaceItemType.FOLDER_ITEM) {
				_log.debug("Item is a Folder Item");
				FolderItem imageItem = (FolderItem) item;
				InputStream data = null;

				if (imageItem.getFolderItemType()==FolderItemType.EXTERNAL_IMAGE){
					_log.debug("EXTERNAL_IMAGE -|- " + item.getType() + " itemId=" + item.getId());
					ExternalImage image = (ExternalImage)item;
					_log.debug("EXTERNAL_IMAGE Name= " + item.getName() + " Asking InputStream ..");
					data = image.getData();					
					_log.debug("Got inputStream");
				}
				else if (imageItem.getFolderItemType()==FolderItemType.IMAGE_DOCUMENT){
					GCubeItem image = (GCubeItem)item;
					if (image.getMimeType().equals("image/tiff"))
						//						image.getProperties().getProperties().get(NodeProperty.THUMBNAIL_DATA); //this is a problem
						//					else
						data = image.getData();
				}
				if (data != null) {
					_log.debug("Encoding image in base64");
					byte[] imageBytes = IOUtils.toByteArray(data);
					String extension = ImagesUtil.getImageExtension(imageItem);
					String srcAttrValue = "data:image/"+extension+";base64,"+DatatypeConverter.printBase64Binary(imageBytes);
					_log.debug("Encoded image=" + srcAttrValue);
					return srcAttrValue;
				} 
				return null;
			}
			else 
				return null;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * @param reportItemid the report itemd id in basket to store in the session
	 */
	public void storeReportItemIDInSession(String reportItemid) {
		ASLSession d4Session =  getASLSession();
		d4Session.setAttribute(CURRENT_REPORT_ID_ATTRIBUTE, reportItemid);
		_log.debug("WROTE REPORT ID IN SESSION: " + reportItemid);
	}

	/**
	 * @return the report item id in basket, or "" if doesn't exist
	 */
	public String getReportItemIDFromSession() {
		ASLSession d4Session =  getASLSession();
		if (d4Session.getAttribute(CURRENT_REPORT_ID_ATTRIBUTE) == null)
			return "";
		else 
			return d4Session.getAttribute(CURRENT_REPORT_ID_ATTRIBUTE).toString();		
	}
	/**
	 * used to save  the report in the same folder
	 */
	public void saveReport(Model toSave) {
		Workspace root = null;
		try {
			root = getWorkspaceArea();
		} catch (Exception e) {	e.printStackTrace();}

		WorkspaceItem item = null;
		String folderid = "";
		String itemName = "";
		try {
			if (getReportItemIDFromSession().equals("")) {
				folderid = getRootFolder();
			}
			else {
				item = root.getItem(getReportItemIDFromSession());
				folderid = item.getParent().getId();
				itemName = item.getName();
			}			
		} catch (ItemNotFoundException e) {
			_log.error("ITEM NOT FOUND -> " + getReportItemIDFromSession());

		} catch (InternalErrorException e) {
			e.printStackTrace();
		}
		saveReport(toSave, folderid, itemName);
	}



	/**
	 * @param save a report in another folder .
	 * 
	 */
	public void saveReport(Model toSave, String folderid, String newname) {

		Model model = toSave;

		storeTemplateInSession(toSave);
		_log.info("Serializing Model in folder: " + folderid );
		_log.info("Trying to convert dynamic images ... ");
		convertDynamicImagesFromHL(model);

		ServiceUtil myUtil = new ServiceUtil(getASLSession());
		boolean result = myUtil.writeModel(model, "CURRENT_OPEN", getVreName(), getUsername());

		try {
			ModelReader reader = new ModelReader(model);
			System.out.println(reader);
		} catch (Exception e) {
			_log.warn("ModelReader fails to read this report, continue...");	
		}


		if (!result) {
			_log.debug("Could not save report, serializing failed");	
		}
		else {
			String templatePath = myUtil.getTemplateFolder(getVreName(), getUsername())  + "CURRENT_OPEN";	
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			_log.debug("Trying to zip folder: " + templatePath);	

			String folderToZip = templatePath;
			String outZip = templatePath+"-report.zip";

			try {
				ZipUtil.zipDir(outZip, folderToZip);
			} catch (IOException e) {
				_log.error("Could not zip template, serializing failed");	
				e.printStackTrace();
			}
			_log.info("Folder zipped, result: "+ outZip);	

			InputStream isZip = null;

			WorkspaceFolder toSaveIn = null;
			try {
				isZip = new BufferedInputStream(new FileInputStream(outZip));

				toSaveIn = getRootFolder(folderid);

				String templateName = newname;	

				boolean isUpdate = false;
				if (templateName.endsWith("d4sR") ) {
					if (toSaveIn.exists(templateName)) {
						_log.warn("Item exists already, updating");
						//toSaveIn.removeChild(toSaveIn.find(templateName));
						isUpdate = true;
					}
				}

				if (toSaveIn.exists(templateName + ".d4sR")) {
					_log.warn("Item exists already, updating");
					//toSaveIn.removeChild(toSaveIn.find(templateName + ".d4sR"));
					isUpdate = true;
				}

				//remove the template extension
				String templateToInsert = templateName.replace(".d4sT", "");
				if (! templateToInsert.endsWith(".d4sR"))
					templateToInsert+=".d4sR";

				Report rep = null;
				if (isUpdate) {
					rep = (Report) toSaveIn.find(templateName);
					getWorkspaceArea().updateItem(rep.getId(), isZip);
				} else { //is new
					Calendar dateCreated = Calendar.getInstance();
					dateCreated.setTime(model.getDateCreated());

					Calendar lastEdit = Calendar.getInstance();
					lastEdit.setTime(model.getLastEdit());

					rep = toSaveIn.createReportItem(templateToInsert, templateToInsert, dateCreated, lastEdit, 
							model.getAuthor(), model.getLastEditBy(), templateToInsert, model.getSections().size(), "no-status", isZip);
				}

				storeReportItemIDInSession(rep.getId());

				if (toSaveIn.getType() == WorkspaceItemType.SHARED_FOLDER) {
					sendReportUpdateNotification(toSaveIn, rep);
				}

				AccessLogger log = AccessLogger.getAccessLogger();
				CreateReportLogEntry logEntry = new CreateReportLogEntry(model.getTemplateName(), rep.getId());
				log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

			} catch (Exception e) {
				e.printStackTrace();
			} 
		}
	}

	/**
	 * Return an instance of the root folder
	 * @param folderId
	 * @return
	 * @throws ItemNotFoundException 
	 */
	private WorkspaceFolder getRootFolder(String folderId) throws ItemNotFoundException {
		Workspace root = null;
		try {
			root = getWorkspaceArea();
		} catch (WorkspaceFolderNotFoundException e) {e.printStackTrace();
		} catch (InternalErrorException e) { e.printStackTrace();
		} catch (HomeNotFoundException e) {	e.printStackTrace();
		}

		WorkspaceItem item = null;
		try {
			item = root.getItem(folderId);
		} catch (ItemNotFoundException e) {
			_log.info("Folder : " + folderId + " NOT FOUND RETURNING DEFAULT ONE");
			return(WorkspaceFolder)root.getItem(getRootFolder());


		}
		if (item.getType() == WorkspaceItemType.FOLDER || item.getType() == WorkspaceItemType.SHARED_FOLDER) { 
			return (WorkspaceFolder) item;
		}
		_log.error("The item id does not belong to a valid folder id:" + folderId);
		return null;
	}

	/**
	 * send an update notification to all the user sharing this folder
	 * @param toSaveIn
	 * @throws InternalErrorException 
	 */
	private void sendReportUpdateNotification(WorkspaceFolder sharedFolder, WorkspaceItem wsItem) throws InternalErrorException {
		if (sharedFolder.getType() == WorkspaceItemType.SHARED_FOLDER) {
			WorkspaceSharedFolder wsFolder = (WorkspaceSharedFolder) sharedFolder;
			List<String> usersToNotify = wsFolder.getUsers();	
			Thread thread = new Thread(new NotificationsThread(getASLSession(), usersToNotify, wsItem, sharedFolder));
			thread.start();
		}
	}
	/**
	 * return a sample of the given TS to the client
	 * @param sTS .
	 * @return .
	 */
	public Table getSampleTimeSeries(RepTimeSeries sTS) {

		File csvTS = getTimeSeriesFromWorkspace(sTS);
		Table toReturn = null;
		try {
			toReturn = parseCSV(csvTS, sTS);
		} catch (ParseException e) { e.printStackTrace();
		} catch (IOException e) {	e.printStackTrace();
		} catch (ProcessingException e) { e.printStackTrace();
		}	

		return toReturn;	
	}

	/**
	 * retrieve the given TS csv representation and writes it into /tmp returning the File
	 * @param sTS serializable TS
	 * @return a File csv
	 */
	private File getTimeSeriesFromWorkspace(RepTimeSeries sTS) {
		try {
			String timeSeriesBasketID = sTS.getTsMetadata().getId();

			Workspace root = null;
			try {
				root = getWorkspaceArea();
			} catch (WorkspaceFolderNotFoundException e) {e.printStackTrace();
			} catch (InternalErrorException e) { e.printStackTrace();
			} catch (HomeNotFoundException e) {	e.printStackTrace();
			}
			WorkspaceItem item = null;
			try {
				item = root.getItem(timeSeriesBasketID);
			} catch (ItemNotFoundException e) {
				e.printStackTrace();
			}
			_log.debug("Got Item TS From HL, Item Type: "+item.getType());

			if (item.getType() != WorkspaceItemType.FOLDER_ITEM) { 
				_log.debug("The item id does not belong to a timeseries, id:" + timeSeriesBasketID);
				return null;
			}
			FolderItem bItem = (FolderItem) item;
			if (bItem.getFolderItemType() != FolderItemType.TIME_SERIES) {
				_log.debug("The basket item does not belong to a timeseries, id:" + timeSeriesBasketID);
				return null;
			}

			TimeSeries ts = (TimeSeries) bItem;
			return getTSFromBasket(ts);	
		} 
		catch (NullPointerException e) {
			_log.error("No TS was dragged in the Area returning NULL");
			return null;
		}
	}

	/**
	 * 
	 * @param toParse the csv to parse
	 * @throws ProcessingException .
	 * @throws IOException .
	 * @throws ParseException . 
	 */
	private Table parseCSV(File toParse, final RepTimeSeries sTS) throws ParseException , IOException, ProcessingException {		

		final Table toReturn;
		final boolean isFiltered;
		final int fromLine;
		final int toLine;

		//if there is no filter
		if ( sTS.getFilter() == null) {
			toReturn = new Table(sTS.getTsMetadata().getHeaderLabels().size());
			isFiltered = false;
			fromLine = 1;
			toLine = 10;
		}
		else {
			int headers = sTS.getFilter().getColsNumberToShow().size();
			toReturn = new Table(headers);
			isFiltered = true;
			fromLine = sTS.getFilter().getFrom();
			toLine = fromLine + 10;
		}


		final CSVFileProcessor fp = new CSVFileProcessor();
		fp.processFile(toParse.getAbsolutePath() , new CSVLineProcessor() {
			boolean keepGoing = true;

			public void processHeaderLine( final int linenumber, final List<String> fieldNames )    {
				ArrayList<TableCell> toInsert = new ArrayList<TableCell>();
				for (String field : fieldNames) {
					toInsert.add(new TableCell(field));
				}
				if (! isFiltered) 
					toReturn.addRow((ArrayList<TableCell>) toInsert);
				else {
					ArrayList<String> filteredHeaders = new ArrayList<String>();
					for (Integer colNo : sTS.getFilter().getColsNumberToShow()) {
						String toAdd = sTS.getTsMetadata().getHeaderLabels().get(colNo);
						filteredHeaders.add(toAdd);
						toInsert = new ArrayList<TableCell>();
						for (String field : filteredHeaders) {
							toInsert.add(new TableCell(field));
						}
					}
					toReturn.addRow(toInsert);
				}		       		
			}


			public void processDataLine( final int linenumber, final List<String> fieldValues ) {
				if (linenumber > toLine)
					keepGoing = false;
				if (linenumber >= fromLine && linenumber <= toLine) {
					ArrayList<TableCell> toInsert = new ArrayList<TableCell>();
					for (String field : fieldValues) {
						toInsert.add(new TableCell(field));
					}
					if (! isFiltered) 
						toReturn.addRow(toInsert);
					else {
						ArrayList<String> filteredFields = new ArrayList<String>();
						for (Integer colNo : sTS.getFilter().getColsNumberToShow()) {
							String toAdd = fieldValues.get(colNo);
							filteredFields.add(toAdd);
							toInsert = new ArrayList<TableCell>();
							for (String field : filteredFields) {
								toInsert.add(new TableCell(field));
							}
						}
						toReturn.addRow(toInsert);
					}		    
				}
			}
			public boolean continueProcessing() {				
				return keepGoing;
			}
		} );
		return toReturn;
	}


	/**
	 * get the TS from the Basket
	 * @param ts
	 * @return a csv file
	 */
	private File getTSFromBasket(TimeSeries ts)  {
		try	{
			File temp = File.createTempFile(ts.getName(), ".csv");

			InputStream inputStream = null;
			try {
				inputStream = ts.getData();
			} catch (InternalErrorException e) {
				e.printStackTrace();
				return null;
			}

			OutputStream out = new FileOutputStream(temp);

			byte buf[] = new byte[1024];
			int len;
			while((len = inputStream.read(buf))>0)
				out.write(buf,0,len);
			out.close();
			inputStream.close();
			_log.debug("Successfully got TimeSeries from Basket: \n" + temp.getAbsolutePath());
			return temp;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		} catch (InternalErrorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * each portlet instance runs in a scope
	 * each portlet instance is used by a unique username from within the portal
	 * @param currentHost .
	 * @return a SessionInfo bean containing the username  the scope andis opening a workflow document or not
	 */
	public SessionInfo getSessionInfo(String currentHost) {
		if (testWorkflow) {
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE, "1");
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_GIVEN_NAME, "TEST REPORT");
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_READONLY_ATTRIBUTE, true);
			return new SessionInfo(getUserBean(), getVreName(), true, true, false, "");
		}

		boolean isVME = isReportsStoreGatewayAvailable();

		if (getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE) == null) {
			_log.debug("WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE is NULL: ");
			String rsgEndpoint = "";
			if (getRSGWSAddress() != null)
				rsgEndpoint = ((RSGAccessPoint) getRSGWSAddress()).getRestUrl();
			return new SessionInfo(getUserBean(), getVreName(), false, false, isVME, rsgEndpoint);

		}
		else {
			_log.debug("FOUND WORKFLOW_ID_ATTRIBUTE ***** ");
			//String workflowid = getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE).toString();
			Boolean canEdit = ! (Boolean) getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_READONLY_ATTRIBUTE);
			return new SessionInfo(getUserBean(), getVreName(), true, canEdit, false, "");
		}
	}

	public Model getWorkflowDocumentFromDocumentLibrary() {
		ServiceUtil myUtil = new ServiceUtil(getASLSession());

		if (testWorkflow) {
			FileInputStream fis = null;
			ObjectInputStream in = null;
			Model toConvert = null;
			try {
				fis = new FileInputStream("/Users/massi/portal/CURRENT_OPEN.d4st");
				in = new ObjectInputStream(fis);
				toConvert = (Model) in.readObject();
				in.close();
			} catch (IOException ex) {
				ex.printStackTrace();
			} catch (ClassNotFoundException ex) {
				ex.printStackTrace();
			}
			toConvert.setTemplateName("TEST");
			_log.info(" Converting TEST REPORT to Serializable object, model name: \n" + toConvert.getTemplateName());
			return toConvert;
		}
		else {
			String workflowid = getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE).toString();
			Boolean canEdit = ! (Boolean) getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_READONLY_ATTRIBUTE);
			String documentName = getASLSession().getAttribute(WfDocsLibrary.WORKFLOW_GIVEN_NAME).toString();
			_log.info("getWorkflowDocumentFromDocumentLibrary() CALLED ***** ID = " + workflowid +  "  name:\n " + documentName);

			//TODO: check this
			//reset the values in session 

			_log.info("Reset the values in session  ... ");

			getASLSession().setAttribute(WfDocsLibrary.LAST_WORKFLOW_ID, workflowid);	
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_ID_ATTRIBUTE, null);
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_READONLY_ATTRIBUTE, null);	
			getASLSession().setAttribute(WfDocsLibrary.WORKFLOW_GIVEN_NAME, null);	



			try {			
				InputStream inputStream = DocLibraryUtil.getFileEntryAsStream(getASLSession(), workflowid);
				String templatePath = myUtil.getTemplateFolder(getVreName(), getUsername())  + "CURRENT_OPEN/";	
				String pathToFile = templatePath;
				File toExtract = writeReportToDisk(inputStream, pathToFile, "Workflodoc-report.zip");

				File outputDir = new File( myUtil.getTemplatePath("", getVreName(), getUsername()) );
				ZipUtil.unzipArchive(toExtract, outputDir);
				toExtract.delete();


				FileInputStream fis = null;
				ObjectInputStream in = null;
				Model toConvert = null;
				try {
					String path = myUtil.getTemplateFolder(getVreName(), getUsername());

					String reportFileName = seekModel(templatePath, UUID.randomUUID().toString()); //random name just to make sure it look for the name
					String pathToReport = path + "CURRENT_OPEN/"+reportFileName+".d4st";
					_log.trace("WF PathToReport = " + pathToReport);
					fis = new FileInputStream(pathToReport);
					in = new ObjectInputStream(fis);
					toConvert = (Model) in.readObject();
					in.close();
				} catch (IOException ex) {
					ex.printStackTrace();
				} catch (ClassNotFoundException ex) {
					ex.printStackTrace();
				}
				toConvert.setTemplateName(documentName);
				_log.debug("Converting fileToRead to Serializable object, model name: \n" + toConvert.getTemplateName());
				Model toReturn =  (toConvert);

				//saves this model as previous one in session
				getASLSession().setAttribute(PREVIOUS_REPORT_INSTANCE, toConvert);		

				AccessLogger log = AccessLogger.getAccessLogger();
				OpenWorkflowLogEntry logEntry = new OpenWorkflowLogEntry(toConvert.getTemplateName(), toConvert.getUniqueID(), toConvert.getAuthor());
				log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

				return toReturn;

			} catch (Exception e) {
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * get the report instance from the Basket
	 * @param repTmp .
	 * @param pathToFile the directory where to save the file
	 * @param filename the filename to give to the newly created file
	 * @return
	 */
	private File writeReportToDisk(InputStream isData, String pathToFile, String filename)  {
		try	{
			File dir = new File(pathToFile);
			_log.debug("DIR: " + pathToFile);
			if (! dir.exists() )
				dir.mkdirs();

			File f = new File(pathToFile+filename);
			OutputStream out = new FileOutputStream(f);

			IOUtils.copy(isData, out);
			out.close();
			_log.debug("Successfully WROTE ReportTemplate from DL: " + pathToFile);
			return f;
		}
		catch (IOException e){
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * update the Workflow Document in session
	 */
	public void updateWorkflowDocument(Model toSave, boolean update) {
		ASLSession session = getASLSession();
		ServiceUtil myUtil = new ServiceUtil(session);

		String workflowid = session.getAttribute(WfDocsLibrary.LAST_WORKFLOW_ID).toString();
		Model model = null; 
		String documentWorkflowOwnerId = store.getWorkflowById(workflowid).getAuthor();
		String documentWorkflowName = store.getWorkflowById(workflowid).getName();
		if (update) {
			_log.debug("SAVING in WorkflowDocument Library ");

			model = (Model) toSave;
			_log.debug("Trying to convert dynamic images ... ");
			convertDynamicImagesFromHL(model);

			boolean result = myUtil.writeModel(model, "CURRENT_OPEN", getVreName(), getUsername());

			if (!result) {
				_log.error("Could not save report, serializing failed");	
			}
			else {
				String templatePath = myUtil.getTemplateFolder(getVreName(), getUsername())  + "CURRENT_OPEN";	
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				_log.debug("Trying to zip folder: " + templatePath);	

				String folderToZip = templatePath;
				String outZip = templatePath+"-report.zip";

				try {
					ZipUtil.zipDir(outZip, folderToZip);
					_log.debug("Folder zipped, result: "+ outZip);	
					InputStream isZip = new BufferedInputStream(new FileInputStream(outZip));

					DocLibraryUtil.updateFileIntoDocLibrary(getASLSession(), workflowid, getBytesFromInputStream(isZip));
					_log.info("Updated in DOC LIB OK");	
					store.addWorkflowLogAction(workflowid, getASLSession().getUsername(), "Updated");

					//send the notification
					NotificationsManager nm = new ApplicationNotificationsManager(session, "org.gcube.admin.portlet.wfdocviewer.server.WorkflowDocServiceImpl");
					nm.notifyDocumentWorkflowUpdate(documentWorkflowOwnerId, workflowid, documentWorkflowName);

					AccessLogger log = AccessLogger.getAccessLogger();
					SaveWorkflowLogEntry logEntry = new SaveWorkflowLogEntry(model.getTemplateName(), model.getUniqueID(), model.getAuthor());
					log.logEntry(getASLSession().getUsername(), getASLSession().getScopeName(), logEntry);

				}
				catch (Exception e) {
					_log.error("Could not zip template, serializing failed");	
					e.printStackTrace();
				}
			}
		} else {
			store.addWorkflowLogAction(workflowid, getASLSession().getUsername(), "Viewed"); 
			//send the notification
			NotificationsManager nm = new ApplicationNotificationsManager(session, "org.gcube.admin.portlet.wfdocviewer.server.WorkflowDocServiceImpl");
			nm.notifyDocumentWorkflowView(documentWorkflowOwnerId, workflowid, documentWorkflowName);
		}
		//unlocks
		unlock(workflowid);
		getASLSession().setAttribute("idreport", null);
	}
	/**
	 * 
	 * @return the info about the current user
	 */
	private UserBean getUserBean() {
		try {

			String username = getASLSession().getUsername();
			String email = username+"@isti.cnr.it";
			String fullName = username+" FULL";
			String thumbnailURL = "images/Avatar_default.png";

			if (isWithinPortal()) {

				UserModel user = UserLocalServiceUtil.getUserByScreenName(OrganizationsUtil.getCompany().getCompanyId(), username);
				thumbnailURL = "/image/user_male_portrait?img_id="+user.getPortraitId();
				fullName = user.getFirstName() + " " + user.getLastName();
				email = user.getEmailAddress();
				UserBean toReturn = new UserBean(username, fullName, thumbnailURL, user.getEmailAddress());
				_log.info("Returning USER: " + toReturn);
				return toReturn;
			}
			else {
				UserBean toReturn = new UserBean(getASLSession().getUsername(), fullName, thumbnailURL, email);
				_log.info("Returning test USER: " + toReturn);
				return toReturn;
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return new UserBean();
	}
	/**
	 */
	byte[] getBytesFromInputStream(InputStream is) {
		ByteArrayOutputStream os = new ByteArrayOutputStream();
		try {
			IOUtils.copy(is, os);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return os.toByteArray();
	}
	/**
	 * 
	 * @param workflowid .
	 */
	private void unlock(String workflowid) {
		DLFileEntry fileEntry;
		try {
			fileEntry = DocLibraryUtil.getFileEntry(getASLSession(), workflowid);
			_log.info("Log action saved, trying ot unlock document ...");	
			LockLocalServiceUtil.unlock(DLFileEntry.class.getName(), fileEntry.getFileEntryId());
			_log.info("UNLOCK OK!");	
		} 
		catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void renewLock() {
		HttpSession httpSes = this.getThreadLocalRequest().getSession();
		httpSes.setMaxInactiveInterval(-1); //session won't expire 
		String workflowid = getASLSession().getAttribute(WfDocsLibrary.LAST_WORKFLOW_ID).toString();
		try {
			DLFileEntry fileEntry  = DocLibraryUtil.getFileEntry(getASLSession(), workflowid);
			_log.info("Renewing Lock ...");	
			long fifteenMin = 900000;
			Date currTimePlus15 = new Date(new Date().getTime() + fifteenMin);
			LockLocalServiceUtil.getLock(DLFileEntry.class.getName(), fileEntry.getFileEntryId()).setExpirationDate(currTimePlus15);
			_log.info("Lock Renewed, expiring: " + currTimePlus15);	
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public String save(String filePath, String workspaceFolderId, String itemName, TypeExporter type, boolean overwrite) throws SaveReportFileException, SaveReportFileExistException {
		try {
			File file = new File(filePath);

			Workspace workspace = HomeLibrary.getUserWorkspace(getASLSession().getUsername());
			_log.info("Saving in Workspace of " + workspace.getOwner().getPortalLogin());
			WorkspaceFolder folder = (workspaceFolderId != null)?
					(WorkspaceFolder)workspace.getItem(workspaceFolderId):workspace.getRoot();

					itemName = 	itemName + "." + type.toString().toLowerCase();	
					if (workspace.exists(itemName, folder.getId())) {
						if (overwrite)
							workspace.remove(itemName, folder.getId());
						else
							throw new SaveReportFileException("The item " + itemName + " already exists");
					}

					switch (type) {
					case PDF:
						return folder.createExternalPDFFileItem(itemName,
								"", null, new FileInputStream(file)).getId();

					case HTML:
						return folder.createExternalFileItem(itemName,
								"", "text/html", new FileInputStream(file)).getId();
					case DOCX:
						return folder.createExternalFileItem(itemName,
								"", "application/msword", new FileInputStream(file)).getId();

					case XML:
						return folder.createExternalFileItem(itemName,
								"", "application/xml", new FileInputStream(file)).getId();
					}
					throw new SaveReportFileException("Unknown file type");
		} catch (ItemAlreadyExistException e) {
			throw new SaveReportFileExistException(e.getMessage());
		} catch (Exception e) {
			throw new SaveReportFileException(e.getMessage());
		}
	}

	@Override
	public void renewHTTPSession() {
		HttpSession session = this.getThreadLocalRequest().getSession();
		_log.info("HTTP Session renewed" + new Date(session.getLastAccessedTime()));
	}

	private RsgReadClient getRsgClient() {
		RSGAccessPoint rsgWsAddr = getRSGWSAddress();
		if (rsgReadClient == null) {
			rsgReadClient = new RsgReadClient(rsgWsAddr.getRestUrl());
			System.out.println("rsgReadClient = new RsgReadClient(rsgWsAddr.getRestUrl()"+rsgWsAddr.getRestUrl());
		}
		return rsgReadClient;
	}

	private RsgWriteClient getRsgSecureClient() {
		RSGAccessPoint rsgWsAddr = getRSGWSAddress();
		if (rsgWriteClient == null) {
			rsgWriteClient = new RsgWriteClient(rsgWsAddr.getRestUrl()+"/write");

			try {
				rsgWriteClient.secureWithPlainTokenSecurity(rsgWsAddr.getTokenUrl());
				System.out.println("rsgWriteClient.secureWithPlainTokenSecurity(rsgWsAddr.getTokenUrl())"+rsgWsAddr.getTokenUrl());
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		}
		return rsgWriteClient;
	}
	/**
	 * the list of Regional Fishery Management Organizations (RFMO) associated to the current user is constructed by looking at the roles
	 * of the current user. 
	 * 
	 * @returnt the list of Regional Fishery Management Organizations (RFMO) associated to the current user.
	 */
	private ArrayList<String> getUserRFMOs() {
		if (! isWithinPortal()) 
			return new ArrayList<String>();
		ArrayList<String> toReturn = new ArrayList<String>();
		RoleManager rm = new LiferayRoleManager();
		ASLSession session = getASLSession();
		try {
			User theUser = OrganizationsUtil.validateUser(session.getUsername());
			List<RoleModel> roles = rm.listRolesByUserAndGroup(""+session.getGroupId(), ""+theUser.getUserId());
			for (RoleModel role : roles) {
				_log.info("Role " + role.getRoleName() + " Adding RFMO");
				if (role.getRoleName().endsWith("-Editor")) {
					String[] splits = role.getRoleName().split("-");
					toReturn.add(splits[0]);
					_log.info("Added grant for RFMO="+splits[0]);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return toReturn;
	}
	/**
	 * 
	 * @return whether the user is a VRE Manager for this VRE or not.
	 */
	private boolean isVREManager() {
		if (isWithinPortal()) {
			RoleManager rm = new LiferayRoleManager();
			ASLSession session = getASLSession();

			try {
				User theUser = OrganizationsUtil.validateUser(session.getUsername());
				List<RoleModel> roles = rm.listRolesByUserAndGroup(""+session.getGroupId(), ""+theUser.getUserId());
				for (RoleModel role : roles) 
					if (role.getRoleName().equalsIgnoreCase("VRE-Manager"))	return true;
			} 
			catch (Exception e) {
				e.printStackTrace();
			} 
			return false;
		} else {
			_log.warn("Working in Eclipse, returning all rights");
			return true;
		}
	}

	@Override
	public ArrayList<VMEReportBean> listVMEReports() {
		ArrayList<VMEReportBean> toReturn = new ArrayList<VMEReportBean>();
		ReportType type = new ReportType();
		type.setTypeIdentifier("Vme");

		_log.info("listVMEReports() instantiating secure rsgClient ...");
		rsgReadClient = getRsgClient();		
		_log.info("listVMEReports() securedWithEncryptedToken completed");

		ReportEntry[] reports = null;
		try {
			_log.info("calling rsgClient#listReports... ");
			reports = rsgReadClient.listReports(type);
			_log.info("got the reports list");
		} catch (ClientException e) {
			_log.error("failed to get the list of VME Reports: " + e.getMessage());
		}
		if (isVREManager()) {
			for (ReportEntry re : reports) {
				String rfmo = re.getOwner();
				String name = re.getIdentifier();
				toReturn.add(new VMEReportBean(""+re.getId(), rfmo, name));		
			}	
		} else {
			ArrayList<String> allowedRFMOs = getUserRFMOs();
			for (ReportEntry re : reports) {
				String rfmo = re.getOwner().trim();
				String name = re.getIdentifier();
				for (String allowedRFMO : allowedRFMOs) {
					if (allowedRFMO.compareTo(rfmo) == 0) {
						toReturn.add(new VMEReportBean(""+re.getId(), rfmo, name));
						_log.debug("Added " + rfmo + " - " + name);
					}
				}

			}	
		}	

		return toReturn;
	}

	@Override
	public ArrayList<VMEReportBean> listVMEReportRefByType(VMETypeIdentifier refType) {
		if (refType == VMETypeIdentifier.Vme)
			throw new IllegalArgumentException("VME Type is not a reference");
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		ArrayList<VMEReportBean> toReturn = new ArrayList<VMEReportBean>();
		ReportType type = new ReportType();
		type.setTypeIdentifier(refType.toString());
		rsgReadClient = getRsgClient();	

		if (isVREManager()) {
			for (ReportEntry re :rsgReadClient.listReports(new ReportType(refType.getId()))) {
				String rfmo = re.getOwner();
				String name = re.getIdentifier();
				toReturn.add(new VMEReportBean(""+re.getId(), rfmo, name));			
			}	
		} else {
			ArrayList<String> allowedRFMOs = getUserRFMOs();
			for (ReportEntry re :rsgReadClient.listReports(new ReportType(refType.getId()))) {
				String rfmo = re.getOwner();
				String name = re.getIdentifier();
				if (rfmo == null) { //when getting a RFMO refType the Owner is null
					rfmo = name;
				}				
				for (String allowedRFMO : allowedRFMOs) {
					if (allowedRFMO.compareTo(rfmo) == 0) {						
						toReturn.add(new VMEReportBean(""+re.getId(), rfmo, name));
						_log.debug("Added ref report " + rfmo + " - " + name);
					}
				}
			}	
		}


		return toReturn;		
	}


	@Override
	public Model importVMEReport(String id, String name, VMETypeIdentifier refType) {
		rsgReadClient = getRsgClient();	


		CompiledReport cr = null;
		if (refType == VMETypeIdentifier.Vme) {
			_log.info("Importing VME id=" + id +  " name=" + name);
			cr = rsgReadClient.getReportById(new ReportType(refType.getId()), id);
		} 
		else {
			_log.info("Importing Ref type= " + refType.getId() + " id=" + id +  " name=" + name);
			cr = rsgReadClient.getReferenceReportById(new ReportType(refType.getId()), id);

		}
		Model model = null;
		try {
			model = new ReportManagerReportBuilder().buildReport(cr);
			model.setTemplateName(name);
			//ReportsReader.readReportStructure(model);			
		} catch (ReportBuilderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		ModelReader reader = new ModelReader(model);
		System.out.println(reader);
		return model;
	}

	@Override
	public Model getVMEReportRef2Associate(String id, VMETypeIdentifier refType) {
		rsgReadClient = getRsgClient();	


		_log.info("Importing Ref type= " + refType.getId() + " id=" + id);
		CompiledReport cr = rsgReadClient.getReferenceReportById(new ReportType(refType.getId()), id);
		Model model = null;
		try {
			model = new ReportManagerReportBuilder().buildReferenceReport(cr);
		} catch (ReportBuilderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	@Override
	public Model importVMETemplate(VMETypeIdentifier refType) {
		rsgReadClient = getRsgClient();	

		CompiledReport cr = null;
		if (refType == VMETypeIdentifier.Vme) {
			_log.info("Importing VME Template");
			cr = rsgReadClient.getTemplate(new ReportType(refType.getId()));			
		} 
		else {
			_log.info("Importing Ref type= " + refType.getId());
			cr = rsgReadClient.getTemplate(new ReportType(refType.getId()));	

		}
		Model model = null;
		try {
			model = new ReportManagerReportBuilder().buildReport(cr);			
			model.setTemplateName("New " + refType.getDisplayName());
		} catch (ReportBuilderException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return model;
	}

	@Override
	public VmeExportResponse exportReportToRSG(VMETypeIdentifier refType, Model model) {
		System.out.println(new ModelReader(model).toString());

		rsgReadClient = getRsgClient();	
		rsgWriteClient = getRsgSecureClient();	

		//Use the RSG client to get a template for the report whose type is the last token (i.e. the corresponding class' 'simple name') 
		//appearing in the VME model class name as stored in the 'type' metadata

		CompiledReport template = null;
		if (refType == VMETypeIdentifier.Vme) { 
			_log.info("getTemplate for VME Report");
			template = rsgReadClient.getTemplate(new ReportType(refType.getId()));
		}
		else {
			_log.info("getTemplate for Ref Report");
			template = rsgReadClient.getRefTemplate(new ReportType(refType.getId()));
		}
		CompiledReport toSend = null;
		try {
			toSend = new ReportManagerReportBuilder().extract(template, model);			
		} catch (Exception e) {
			e.printStackTrace();
		}
		try {
			//Actual RSG client interface exposes different methods (publish / publishDelta etc.) that need to be updated
			ServiceResponse res = null;
			if (refType == VMETypeIdentifier.Vme) {
				_log.info("Exporting VME Report");
				res = rsgWriteClient.update(toSend);
			} 
			else {
				_log.info("Exporting Ref type= " + refType.getId());
				res = rsgWriteClient.updateRef(toSend);
			}
			return getClientResponse(res);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			return new VmeExportResponse(new VmeResponseEntry("RUNTIME_EXCEPTION", "Sorry, there was an error on the Server, please try again in few minutes or report an Issue."));
		}
	}

	@Override
	public VmeExportResponse deleteReportFromRSG(VMETypeIdentifier refType,	String idToDelete) {
		RsgWriteClient rsgClient = getRsgSecureClient();	

		try {
			ServiceResponse res = null;
			if (refType == VMETypeIdentifier.Vme) {
				_log.info("Deleting VME Report id = " + idToDelete);
				res = rsgClient.deleteById(new ReportType(refType.getId()), idToDelete);
			} 
			else {
				_log.info("Deleting VME Reference Report of Type " + refType + " having id = " + idToDelete);
				res = rsgClient.deleteReferenceById(new ReportType(refType.getId()), idToDelete);
			}
			return getClientResponse(res);
		} catch (RuntimeException ex) {
			ex.printStackTrace();
			return new VmeExportResponse(new VmeResponseEntry("RUNTIME_EXCEPTION", "Sorry, there was an error on the Server, we could not delete. Please try again in few minutes or report an Issue."));
		}
	}

	/**
	 * this method look for a ReportsStoreGateway WS available in the infrastructure
	 * @return true if an instance of the ReportsStoreGateway is available in the infrastructure
	 * @throws Exception 
	 */
	private boolean isReportsStoreGatewayAvailable(){
		String user = (String) this.getThreadLocalRequest().getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null && isWithinPortal()) {
			_log.warn("Session expired, returning ReportsStoreGateway not available");
			return false;
		}
		String scope = getASLSession().getScope();
		_log.info("Looking for a running ReportsStoreGateway WS in " + scope);
		String previousScope = ScopeProvider.instance.get();
		ScopeProvider.instance.set(scope);
		SimpleQuery query = queryFor(ServiceEndpoint.class);
		query.addCondition("$resource/Profile/Category/string() eq 'Service'");
		query.addCondition("$resource/Profile/Name/string() eq 'ReportsStoreGateway'");

		DiscoveryClient<ServiceEndpoint> client = clientFor(ServiceEndpoint.class);
		List<ServiceEndpoint> list = client.submit(query);
		ScopeProvider.instance.set(previousScope);

		if (list.size() > 1) {
			_log.warn("Multiple ReportsStoreGateway Service Endpoints available in the scope, should be only one.");
			return false;
		}
		else if (list.size() == 1) {
			ServiceEndpoint se = list.get(0);
			String host = se.profile().runtime().hostedOn();
			AccessPoint ap = se.profile().accessPoints().iterator().next();
			String address =  ap.address();
			String password = "";
			String iMarineSkrURL = "";
			String vmeSkrURL = "";
			try {
				password = StringEncrypter.getEncrypter().decrypt(ap.password());
				for (Property property : ap.properties()) {
					if (property.name().compareTo(RSGAccessPoint.IMARINE_SECURE_KEYRING_NAME) == 0) 
						iMarineSkrURL = StringEncrypter.getEncrypter().decrypt(property.value());
					if (property.name().compareTo(RSGAccessPoint.VME_SECURE_KEYRING_NAME) == 0) 
						vmeSkrURL = StringEncrypter.getEncrypter().decrypt(property.value());
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
			RSGAccessPoint rsgAp = new RSGAccessPoint(host+address, iMarineSkrURL, vmeSkrURL, password);
			setRSGWSAddress(rsgAp);
			return true;
		}
		else return false;

	}

	private VmeExportResponse getClientResponse(ServiceResponse rsgResponse) {
		VmeExportResponse toReturn = new VmeExportResponse();
		for (ServiceResponseMessage entry : rsgResponse.getResponseMessageList()) {
			String entryCode = entry.getResponseCode() == null ? "no-code" : entry.getResponseCode().toString();
			String entryMessage = entry.getResponseMessage() == null ? "no response message" : entry.getResponseMessage();
			toReturn.getResponseMessageList().add(new VmeResponseEntry(entryCode, entryMessage));
		}
		toReturn.setGloballySucceded(rsgResponse.isGloballySucceeded());
		return toReturn;
	}




}
