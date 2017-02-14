package gr.cite.geoanalytics.dataaccess.test;

import gr.cite.geoanalytics.dataaccess.entities.principal.Principal;
import gr.cite.geoanalytics.layerimport.ShapeImportUtil;

public class TestUtil {

	public static void main(String[] args) {
		Principal principal = new Principal();
		try {
			ShapeImportUtil.fromShapeFile("dd", 4326);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
