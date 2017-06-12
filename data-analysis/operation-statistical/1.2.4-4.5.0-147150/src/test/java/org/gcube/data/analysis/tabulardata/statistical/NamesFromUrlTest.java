package org.gcube.data.analysis.tabulardata.statistical;

public class NamesFromUrlTest {

	public static void main(String[] args) throws Exception {
		String url="https://goo.gl/EOG41p";
		System.out.println(Common.retrieveFileName(url,"defaultName"));
		
	}

}
