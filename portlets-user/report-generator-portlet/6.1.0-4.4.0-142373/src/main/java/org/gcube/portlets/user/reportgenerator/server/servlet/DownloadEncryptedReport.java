package org.gcube.portlets.user.reportgenerator.server.servlet;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.framework.core.session.ASLSession;
import org.gcube.application.framework.core.session.SessionManager;
import org.gcube.common.homelibary.model.items.type.FolderItemType;
import org.gcube.common.homelibrary.home.HomeLibrary;
import org.gcube.common.homelibrary.home.workspace.Workspace;
import org.gcube.common.homelibrary.home.workspace.folder.FolderItem;
import org.gcube.common.homelibrary.home.workspace.folder.items.Report;
import org.gcube.common.homelibrary.home.workspace.folder.items.ReportTemplate;
import org.gcube.common.homelibrary.util.encryption.EncryptionUtil;
import org.gcube.portal.custom.scopemanager.scopehelper.ScopeHelper;

@SuppressWarnings("serial")
public class DownloadEncryptedReport extends HttpServlet {
	

	@Override
	protected void doGet(HttpServletRequest request,
            HttpServletResponse response) throws ServletException, IOException {
        	
		FolderItem item = null;
		File tmpFile = File.createTempFile("report", "texz");
		String fileName = null;
		InputStream data = null;
		try {
			Workspace workspace = HomeLibrary.getUserWorkspace(getASLSession(request).getUsername());
			item = (FolderItem)workspace.getItem(request.getParameter("itemId"));
			
			if (item.getFolderItemType() == FolderItemType.REPORT) {
				 Report report = (Report)item;
				 data = report.getData();
			} else {
				ReportTemplate template = (ReportTemplate)item;
				data = template.getData();
			}
			
			fileName = item.getName() + ".texz";
			
			EncryptionUtil util = new EncryptionUtil();
			FileOutputStream out = new FileOutputStream(tmpFile);
			util.encrypt(data, out);
			
		} catch (Exception e) {
			throw new ServletException(e);
		}
		
        int length   = 0;
        ServletOutputStream outStream = response.getOutputStream();
        String mimetype = "application/octet-stream";
        
        response.setContentType(mimetype);
        response.setContentLength((int)tmpFile.length());
         
        // sets HTTP header
        response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
        
        byte[] byteBuffer = new byte[1024];
        DataInputStream in = new DataInputStream(new FileInputStream(tmpFile));
        
        // reads the file's bytes and writes them to the response stream
        while ((in != null) && ((length = in.read(byteBuffer)) != -1))
        {
            outStream.write(byteBuffer,0,length);
        }
        
        in.close();
        outStream.close();
    }
	
	/**
	 * the current ASLSession
	 * @return .
	 */
	private ASLSession getASLSession(HttpServletRequest request) {
		
		
		String sessionID = request.getSession().getId();
		String user = (String) request.getSession().getAttribute(ScopeHelper.USERNAME_ATTRIBUTE);
		if (user == null) {
			user = "massimiliano.assante";
			request.getSession().setAttribute(ScopeHelper.USERNAME_ATTRIBUTE, user);
			SessionManager.getInstance().getASLSession(sessionID, user).setScope("/gcube/devsec");
		}

		return SessionManager.getInstance().getASLSession(sessionID, user);

	}
}
