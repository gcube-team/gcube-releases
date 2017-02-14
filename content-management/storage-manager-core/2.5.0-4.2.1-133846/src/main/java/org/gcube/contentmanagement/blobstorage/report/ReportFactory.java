package org.gcube.contentmanagement.blobstorage.report;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.gcube.contentmanagement.blobstorage.report.ReportConfig;

/**
 * 
 * @author Roberto Cirillo (ISTI-CNR)
 *
 */
public class ReportFactory {

	final static Logger logger=LoggerFactory.getLogger(ReportFactory.class);
	 /**
	    * <p> Instantiate the class specified by user </p>
	    * @return the Dao class
	    * @throws DAOException
	    */
	    public static Report getReport(int ReportType) throws ReportException {

	        Report report = null;
	        
	        try {
	            switch(ReportType) {
	                case ReportConfig.ACCOUNTING_TYPE :
	                  report = new ReportAccountingImpl();
	                break;
	                default :
	                  throw new ReportException("MyDAOFactory.getDAO: ["+ReportType+"] is an UNKNOWN TYPE !");
	            }
	            logger.trace("ReportFactory.getDao : returning class ["+report.getClass().getName()+"]...");

	         } catch (Exception e) {
	            e.printStackTrace();
	            throw new ReportException("ReportFactory.getReport: Exception while getting Report type : \n" + e.getMessage());
	        }

	        logger.trace("MyReportFactory.getReport : returning class ["+report.getClass().getName()+"]...");
	        return report;
	    }
}