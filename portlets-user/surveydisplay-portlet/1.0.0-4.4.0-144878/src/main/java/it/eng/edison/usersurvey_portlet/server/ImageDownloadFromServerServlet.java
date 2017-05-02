package it.eng.edison.usersurvey_portlet.server;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.liferay.portal.kernel.repository.model.FileEntry;
import com.liferay.portal.kernel.util.FileUtil;
import com.liferay.portlet.documentlibrary.model.DLFileEntry;
import com.liferay.portlet.documentlibrary.service.DLAppLocalServiceUtil;
import com.liferay.portlet.documentlibrary.service.DLFileEntryLocalServiceUtil;

/**
 * Servlet implementation class ImageDownloadFromServerServlet
 */
@WebServlet("/ImageDownloadFromServerServlet")
public class ImageDownloadFromServerServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    public ImageDownloadFromServerServlet() {
        super();
    }
    
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
    }

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
	
		String fileName = null;
		long groupID = 0;
		long folderID = 0;
		
		groupID = Long.parseLong(request.getParameter("groupID"));
		folderID = Long.parseLong(request.getParameter("folderID"));
		fileName = request.getParameter("imgName");
		
		if(fileName.isEmpty()) 
	            return; 
 
        response.setContentType("image/jpeg");
        String fileWExt = fileName.replaceFirst("[.][^.]+$", "");
        try { 
        	 DLFileEntry dlFileEntryEx =  DLFileEntryLocalServiceUtil.getFileEntry(groupID, folderID, fileWExt);
             File file1 = (java.io.File) DLFileEntryLocalServiceUtil.getFile(dlFileEntryEx.getUserId(), dlFileEntryEx.getFileEntryId(), dlFileEntryEx.getVersion(), false);
             InputStream is = new FileInputStream(file1);
            OutputStream out = response.getOutputStream(); 
            byte[] buffer = new byte[1024]; 
            int len; 
            while ((len = is.read(buffer)) > 0) { 
                out.write(buffer, 0, len); 
            } 
            is.close(); 
            out.flush(); 
            out.close(); 
        } catch (Exception e) { 
            e.printStackTrace(); 
        } 
        
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}

}
