package org.gcube.application.aquamaps.images;

import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gcube.application.aquamaps.images.model.MapItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.CloseableIterator;

public class Report extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 2486605867155769417L;
	private static final Logger logger = LoggerFactory.getLogger(Report.class);
	private static DateFormat df = new  SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		CloseableIterator<MapItem> it=null;		
		try{
			Common com=Common.get();
		it=com.getMapDao().closeableIterator();
		PrintWriter writer=resp.getWriter();
		long totalCount=com.getMapDao().countOf();
		while(it.hasNext()){
			writer.println(it.next().toString());
		}
		writer.println("Total count "+totalCount);
		writer.println("Last completed routine "+df.format(new Date(com.getLastCompletedUpdate())));

		writer.println("Last used configuration");
		Map<String,String> lastConfiguration=com.getLastConfiguration();
		writer.println("Update interval in minutes "+lastConfiguration.get(Common.FETCH_ROUTINE_INTERVAL_MINUTES));
		writer.println("GCUBE Scope "+lastConfiguration.get(Common.SCOPE_PROP));
		writer.println("Suitable hspec id "+lastConfiguration.get(Common.SUITABLE_PROP));
		writer.println("Suitable 2050 hspec id "+lastConfiguration.get(Common.SUITABLE_2050_PROP));
		writer.println("Native hspec id "+lastConfiguration.get(Common.NATIVE_PROP));
		writer.println("Native 2050 hspec id "+lastConfiguration.get(Common.NATIVE_2050_PROP));
		
		writer.println("Current configuration");
		writer.println("Update interval in minutes "+com.getProperty(Common.FETCH_ROUTINE_INTERVAL_MINUTES));
		writer.println("GCUBE Scope "+com.getProperty(Common.SCOPE_PROP));
		writer.println("Suitable hspec id "+com.getProperty(Common.SUITABLE_PROP));
		writer.println("Suitable 2050 hspec id "+com.getProperty(Common.SUITABLE_2050_PROP));
		writer.println("Native hspec id "+com.getProperty(Common.NATIVE_PROP));
		writer.println("Native 2050 hspec id "+com.getProperty(Common.NATIVE_2050_PROP));
		writer.flush();
		}catch(Exception e){
			logger.error("Unexpected Error ",e);
			resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}finally{
			if(it!=null)try{
				it.close();
			}catch(Exception e){
				logger.error("Unable to close iterator ",e);
			}			
		}
	}
}
