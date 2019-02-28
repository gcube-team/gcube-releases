package org.gcube.data.spd.irmng;

import java.io.IOException;
import java.sql.SQLException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class TestUpdate {
	static Logger logger = LoggerFactory.getLogger(TestUpdate.class);
	/**
	 * @param args
	 */
	public static void main(String[] args) {
	
		//		if (!(IrmngPlugin.SQLTableExists("taxon")) & !(IrmngPlugin.SQLTableExists("speciesprofile")))
		//			new CreateDBThread();

		if (!(Utils.SQLTableExists("taxon")) || !(Utils.SQLTableExists("speciesprofile")) || !(Utils.SQLTableExists("updates")))
			try {
				logger.trace("create db");
				Utils.createDB();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		
//		long update = UpdateThread.lastupdate();
		new UpdateThread(0);	
//		
//				UpdateThread b = new UpdateThread();	
//				b.update();
//				b.openArch(new File ("/tmp/irmng-folder6909172940732455302"));
	}

}
