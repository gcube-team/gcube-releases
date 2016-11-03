package org.gcube.datatransfer.agent.storagemanager.test;
/**
 *	
 * @author Nikolaos Drakopoulos(CERN)
 *
 */

public class TestStr {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		String uri = "smp://nikolaos.drakopoulos/folder3/Fira at Santorini (from north) with spaces.jpg?5ezvFfBOLqaqBlwCEtAvz4ch5BUu1ag3yftpCvV+gayz9bAtSsnO1/sX6pemTKbDe0qbchLexXeWgGcJlskYE8td9QSDXSZj5VSl9kdN9SN0/LRYaWUZuP4Q1J7lEiwkNMDuLTfe4rIQELLbyLZ/GesZiduaN+bqFELF3+F57Kk=";
		String[] parts = uri.split("\\?");
		String[] partsOfMain=parts[0].split("/");
		String outputFile = partsOfMain[partsOfMain.length-1];
		
		System.out.println(outputFile);

	}

}
