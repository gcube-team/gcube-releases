package org.gcube.social_networking.socialutillibrary;

import java.util.List;


public class TestUnit {

	//@Test
	public void testHashtag() {
		String text = "Dear members, The item 'ADSDTest4CWP' has been just published by Leonardo Candela. " +
				"You can find it here: http://data.d4science.org/ctlg/CWP_Secretariat/adsdtest4cwp " +
				"#Area #Geospatial #Periodicity-When_updates_are_available ";
		List<String> hashtags = Utils.getHashTags(text);
		System.out.println("Hashtags are " + hashtags);
	}

	//@Test
	public void extractUrl(){

		String url = " test http://2001:db8:0:1:1:1:1:1:8080/group/preeco/what-if?p_p_id=simul_WAR_simulfishgrowthportlet&p_p_lifecycle=0 ";
		String result = Utils.extractURL(url);
		System.out.println("urls are " + result);
	}


}
