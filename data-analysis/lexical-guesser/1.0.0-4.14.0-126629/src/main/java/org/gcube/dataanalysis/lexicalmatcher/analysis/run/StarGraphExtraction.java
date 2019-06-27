package org.gcube.dataanalysis.lexicalmatcher.analysis.run;

import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.DBObjectTranslator;
import org.gcube.dataanalysis.lexicalmatcher.analysis.guesser.data.TSObjectTransformer;
import org.gcube.dataanalysis.lexicalmatcher.utils.DatabaseFactory;
import org.gcube.dataanalysis.lexicalmatcher.utils.LexicalLogger;
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
//		AnalysisLogger.setLogger("./ALog.properties");	
		
		//configurazione DB - inizializzo la sessione e mi connetto
	    SessionFactory dbSession = DatabaseFactory.initDBConnection(ConfigurationFileNameLocal);
	    DBObjectTranslator dbo = new DBObjectTranslator();
	    dbo.buildWholeStructure(dbSession,null,null,null,null,null);
	    TSObjectTransformer.transform2Graph(dbo);
	    
	}
}
