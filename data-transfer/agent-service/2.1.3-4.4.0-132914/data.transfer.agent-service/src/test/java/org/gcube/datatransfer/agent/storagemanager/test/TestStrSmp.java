package org.gcube.datatransfer.agent.storagemanager.test;

public class TestStrSmp {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String rootName="smp://kos/";
		String relPath= "Wikipedia_logo_silver.png?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeSxqJkp9OeWKkznDnXYgDz7F/ELBV1lV8qTh/bosrhjOzQb50+GI/1DUWNMQZdZbHMJfYMmmXptQ==";
		String wholeLink;
		if(relPath.contains("?")){
	        wholeLink=rootName+relPath;
	        }
	        else{
	        	 wholeLink=rootName.substring(0,rootName.length()-1)+"?"+relPath;
	    }
		System.out.println("wholeLink="+wholeLink);
	}

}
