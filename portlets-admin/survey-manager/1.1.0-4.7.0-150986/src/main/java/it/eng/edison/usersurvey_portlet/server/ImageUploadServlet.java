package it.eng.edison.usersurvey_portlet.server;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

import javax.activation.MimetypesFileTypeMap;
import javax.portlet.ActionRequest;
import javax.portlet.PortletRequest;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.FileItemFactory;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;
import org.apache.commons.io.FilenameUtils;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.portal.custom.communitymanager.impl.GCubeSiteManagerImpl;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;
import org.gcube.vomanagement.usermanagement.GroupManager;
import org.gcube.vomanagement.usermanagement.exception.GroupRetrievalFault;
import org.gcube.vomanagement.usermanagement.exception.UserManagementSystemException;
import org.gcube.vomanagement.usermanagement.impl.LiferayGroupManager;

import com.liferay.portal.kernel.exception.PortalException;
import com.liferay.portal.kernel.exception.SystemException;
import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.repository.model.Folder;
import com.liferay.portal.kernel.upload.UploadPortletRequest;
import com.liferay.portal.kernel.util.MimeTypesUtil;
import com.liferay.portal.kernel.util.PortalClassLoaderUtil;
import com.liferay.portal.kernel.util.WebKeys;
import com.liferay.portal.kernel.workflow.WorkflowConstants;
import com.liferay.portal.model.User;
import com.liferay.portal.service.ServiceContext;
import com.liferay.portal.service.ServiceContextFactory;
import com.liferay.portal.service.UserLocalServiceUtil;
import com.liferay.portal.theme.ThemeDisplay;
import com.liferay.portal.util.PortalUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.model.DLFolder;
import com.liferay.portlet.documentlibrary.model.DLFolderConstants;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLAppServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFolderLocalServiceUtil;

/**
 * Servlet implementation class ImageUploadServlet.
 */
@WebServlet("/ImageUploadServlet")
public class ImageUploadServlet extends HttpServlet {
	
	/** The Constant serialVersionUID. */
	private static final long serialVersionUID = 1L;
       
	/** The path image. */
	private String pathImage = "";
	
	/** The company id. */
	private int companyId = 0;
	
	/** The user liferay. */
	private User userLiferay = null;
	
    /** The Constant MIN_NUM_RANDOM. */
    public final static Integer MIN_NUM_RANDOM = 1000;
    
    /** The Constant MAX_NUM_RANDOM. */
    public final static Integer MAX_NUM_RANDOM = 10000;		
	
    /**
     * Instantiates a new image upload servlet.
     */
    public ImageUploadServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		    String idTempFolder = request.getParameter("idTempFolder");
		    String curTimeMill = request.getParameter("curTimeMill");
		    FileItemFactory factory = new DiskFileItemFactory();
	        ServletFileUpload upload = new ServletFileUpload(factory);
	        String fileName = null;
	        String fileNamePathImage = null;
	        HttpSession httpSession = request.getSession();
			ServiceContext serviceContext = null;
			GroupManager gm = new LiferayGroupManager();
			long groupId = 0;
			long repositoryId = 0;
			long userId = 0;
			try{
	        	serviceContext = ServiceContextFactory.getInstance(request);
			    String username = httpSession.getAttribute(ScopeHelper.USERNAME_ATTRIBUTE).toString();
			    String scope = SessionManager.getInstance().getASLSession(request.getSession().getId(), username).getScope();
				companyId = (int) GCubeSiteManagerImpl.getCompany().getCompanyId();
			    groupId = gm.getGroupIdFromInfrastructureScope(scope);
				repositoryId = gm.getGroupIdFromInfrastructureScope(scope);
				userLiferay = UserLocalServiceUtil.getUserByScreenName(companyId, username);
			    userId = userLiferay.getUserId(); 
			    
	            List items = upload.parseRequest(request);
	            Iterator iter = items.iterator();
	            String fieldName = null;
	            FileItem item = null;
	            String contentType  = null;
	            boolean isInMemory  = false;
	            long sizeInBytes = 0;
	            byte[] data = null;
	            FileEntry fileEntry = null;
	            while (iter.hasNext()) {
	                item = (FileItem) iter.next();
	                if (!item.isFormField()) {
	                    fieldName = item.getFieldName();
	                    fileName = item.getName();
	                    if (fileName != null) {
	                        fileName = FilenameUtils.getName(fileName);
	                    }
	                     contentType = item.getContentType();
	                     isInMemory = item.isInMemory();
	                     sizeInBytes = item.getSize();
	                     data = item.get();
	                     fileNamePathImage = pathImage + fileName;
					    
					     if (idTempFolder != null){
					    	 MimetypesFileTypeMap mimeTypesMap = new MimetypesFileTypeMap();
					    	 String mimeType = mimeTypesMap.getContentType(fileName);
					    	 String type = mimeType.split("/")[0];
						     fileEntry = DLAppLocalServiceUtil.addFileEntry(userId,repositoryId, 
							    		 Long.valueOf(idTempFolder),curTimeMill+fileName,mimeType,curTimeMill+FilenameUtils.removeExtension(fileName),
							    		 "","",data,
							    		 serviceContext);
					     }
	                }
	            }
	            
        }catch(Exception e){
            e.printStackTrace();
        }
	}
	
}
