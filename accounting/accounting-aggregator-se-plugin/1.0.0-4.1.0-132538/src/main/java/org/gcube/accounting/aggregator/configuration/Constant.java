package org.gcube.accounting.aggregator.configuration;


/**
 * @author Alessandro Pieve (ISTI - CNR) 
 *
 */

public class Constant {
	
	
	
	//CONSTANT for generate file and backup	
	public static String user=null;
	public static String NAME_DIR_BACKUP=".aggregatorPlugin";
	public static String PATH_DIR_BACKUP="backup";
	public final static String HOME_SYSTEM_PROPERTY = "user.home";
	public static  String PATH_DIR_BACKUP_INSERT="backup/insert";
	public static  String PATH_DIR_BACKUP_DELETE="backup/delete";

	//create a file for delete  record before insert a new aggregate
	public static final String FILE_RECORD_NO_AGGREGATE="no_aggregated";
	//create a temporany file for insert a new record aggregate
	public static final String FILE_RECORD_AGGREGATE="aggregated";
	
	
	
	public static final Integer CONNECTION_TIMEOUT=10;
	public static final Integer NUM_RETRY=5;
	public static final Integer CONNECTION_TIMEOUT_BUCKET=10;
}
