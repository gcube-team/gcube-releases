package org.gcube.contentmanagement.lexicalmatcher.analysis.run;

import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.contentmanagement.lexicalmatcher.analysis.guesser.data.TSObjectTransformer;
import org.gcube.contentmanagement.lexicalmatcher.utils.AnalysisLogger;
import org.gcube.contentmanagement.lexicalmatcher.utils.DatabaseFactory;
import org.hibernate.SessionFactory;

public class StarGraphExtraction {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		try {
			RunMain();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	
    private final static String ConfigurationFileNameLocal = "hibernate.cfg.xml";
    
	public static void RunMain() throws Exception{
		AnalysisLogger.setLogger("./ALog.properties");	
		
		//configurazione DB - inizializzo la sessione e mi connetto
	    SessionFactory dbSession = DatabaseFactory.initDBConnection(ConfigurationFileNameLocal);
	    DBObjectTranslator dbo = new DBObjectTranslator();
	    dbo.buildWholeStructure(dbSession,null,null,null,null,null);
	    TSObjectTransformer.transform2Graph(dbo);
	    
	}
}
