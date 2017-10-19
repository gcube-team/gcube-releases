/**
 *
 */
package org.gcube.portlets.user.workspace;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;


/**
 *
 * @author Francesco Mangiacrapa francesco.mangiacrapa@isti.cnr.it
 * May 9, 2016
 */
public class DataMinerTest {

	public static void main(String[] args) {

		String myToken = "4620e6d0-2313-4f48-9d54-eb3efd01a810";
		try {

			URL url = new URL("http://dataminer1-d-d4s.d4science.org/wps/WebProcessingService?request=Execute&service=WPS&Version=1.0.0&gcube-token="+myToken+"&lang=en-US&Identifier=org.gcube.dataanalysis.wps.statisticalmanager.synchserver.mappedclasses.transducerers.BIOCLIMATE_HCAF&DataInputs=HCAF_Table_List=http://goo.gl/LTqufC|http://goo.gl/LTqufC;HCAF_Table_Names=h1|h2");
			url.openConnection();
		}
		catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
