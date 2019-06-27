package it.eng.edison.usersurvey_portlet.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLDecoder;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;

import com.google.gwt.http.client.Request;

/**
 * Servlet implementation class ExportCSVServlet
 */
@WebServlet("/ExportCSVServlet")
public class ExportCSVServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
    public ExportCSVServlet() {
        super();
    }

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    	String realPath = req.getSession().getServletContext().getRealPath("/");
    	String titleSurvey = (String) req.getParameter("titleSurvey");
    	titleSurvey = titleSurvey.replaceAll("\\s+", "_");
    	File downloadableFile = new File(realPath+ "/SurveyStatistics/"+titleSurvey+"_Statistics.csv");

    	resp.setContentType("text/csv");
    	String headerFile = "attachment; filename="+titleSurvey+"_Statistics.csv";
    	resp.setHeader("Content-Disposition", headerFile);
    	ServletOutputStream os = resp.getOutputStream();
    	try {
    		InputStream is = FileUtils.openInputStream(downloadableFile);
    		try {
    			IOUtils.copy(is, os);
    		 } catch (Exception e){
        		e.printStackTrace();
    		 } finally {
    			is.close();
    		}
    	} catch (Exception e){
    		e.printStackTrace();
    	} finally {
    		os.close();
    	}
    }

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doGet(request, response);
	}

}
