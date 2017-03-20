package org.gcube.dataanalysis.wps.statisticalmanager.synchserver.infrastructure;

public class TableCoherenceChecker {

	public static boolean isSystemTable(String tablename){
		if (tablename.equalsIgnoreCase("hcaf_d") || 
				tablename.equalsIgnoreCase("occurrencecells") ||
						tablename.equalsIgnoreCase("hspen") ||
								tablename.equalsIgnoreCase("hspen_mini")||
										tablename.equalsIgnoreCase("hspen_mini_100")||
												tablename.equalsIgnoreCase("hspen_mini_10")||
														tablename.equalsIgnoreCase("hcaf_d_2050"))
			return true;
		else
			return false;
	}

}
